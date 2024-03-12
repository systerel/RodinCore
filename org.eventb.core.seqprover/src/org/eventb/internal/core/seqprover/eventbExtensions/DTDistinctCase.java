/*******************************************************************************
 * Copyright (c) 2010, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Université de Lorraine - additional hypotheses for set membership
 *     UPEC - added optional input names for fresh idents
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.NO_NAMES;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.checkNamesValidity;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.splitInput;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.isDatatypeType;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeFreshIdents;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeIdentEqualsConstr;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeParamSets;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.predIsExtSetMembership;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.datatype.ITypeInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;

/**
 * Makes a distinct case on simple (non inductive) datatypes.
 * 
 * Antecedents are created for each constructor.
 * 
 * @author Nicolas Beauger
 */
public class DTDistinctCase extends AbstractManualInference {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".dtDistinctCase";
	private static final String DISPLAY_NAME = "dt dc";

	/**
	 * An input adding an optional list of names to the super-class input.
	 *
	 * @author Guillaume Verdier
	 */
	public static class Input extends PredicatePositionReasoner.Input {

		private String[] names = NO_NAMES;
		private String error;

		/**
		 * Constructs an input with no user-provided names.
		 *
		 * The predicate is the hypothesis to rewrite. If {@code null}, the rewriting
		 * will be applied to the goal.
		 *
		 * @param pred     hypothesis to rewrite or {@code null}
		 * @param position position of the datatype
		 */
		public Input(Predicate pred, IPosition position) {
			super(pred, position);
		}

		/**
		 * Constructs an input with user-provided names.
		 *
		 * The predicate is the hypothesis to rewrite. If {@code null}, the rewriting
		 * will be applied to the goal.
		 *
		 * The input should be a comma-separated list of names. It may be empty.
		 *
		 * The names will be checked to be valid identifiers in the provided formula
		 * factory.
		 *
		 * @param pred     hypothesis to rewrite or {@code null}
		 * @param position position of the datatype
		 * @param input    comma-separated list of names
		 * @param ff       formula factory to use to check names validity
		 */
		public Input(Predicate pred, IPosition position, String input, FormulaFactory ff) {
			super(pred, position);
			String[] inputs = splitInput(input);
			error = checkNamesValidity(inputs, ff);
			if (error == null) {
				names = inputs;
			}
		}

		// Private constructor: the caller has to provide valid names
		private Input(Predicate pred, IPosition position, String[] names) {
			super(pred, position);
			this.names = names;
		}

		public String[] getNames() {
			return names;
		}

		@Override
		public boolean hasError() {
			return super.hasError() || error != null;
		}

		@Override
		public String getError() {
			String superError = super.getError();
			if (superError != null) {
				return superError;
			}
			return error;
		}

		@Override
		public Input translate(FormulaFactory factory) {
			var result = super.translate(factory);
			return new Input(result.getPred(), result.getPosition(), names);
		}

	}

	private Input input;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm) {
		if (reasonerInput.hasError()) {
			return reasonerFailure(this, reasonerInput, reasonerInput.getError());
		}
		try {
			input = (Input) reasonerInput;
			return super.apply(seq, reasonerInput, pm);
		} finally {
			// Make sure that we do not keep a reference to an old input
			input = null;
		}
	}

	@ProverRule("DATATYPE_DISTINCT_CASE")
	protected Set<Predicate> makeNewHyps(FreeIdentifier ident,
			IExpressionExtension constr, ParametricType type,
			FreeIdentifier[] params, Expression[] paramSets, Predicate goal, FormulaFactory ff) {
		final Set<Predicate> newHyps = new LinkedHashSet<Predicate>();
		if (paramSets != null) {
			assert params.length == paramSets.length;
			for (int i = 0; i < params.length; ++i) {
				newHyps.add(ff.makeRelationalPredicate(Formula.IN, params[i], paramSets[i], null));
			}
		}
		newHyps.add(makeIdentEqualsConstr(ident, constr, type, params, ff));
		return newHyps;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred, IPosition position) {
		Predicate target = pred;
		if (target == null) {
			target = seq.goal();
		}
		final Formula<?> subFormula = target.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Formula.FREE_IDENT) {
			return null;
		}
		final FreeIdentifier ident = (FreeIdentifier) subFormula;
		if (!isDatatypeType(ident.getType())) {
			return null;
		}

		final ParametricType prmType = (ParametricType) ident.getType();
		final IExpressionExtension ext = prmType.getExprExtension();
		final IDatatype dt = (IDatatype) ext.getOrigin();
		Expression prmSet = null;
		if (pred != null) {
			prmSet = predIsExtSetMembership(pred, position, ext);
		}
		return makeAntecedents(seq, ident, prmType, prmSet, dt);
	}

	private IAntecedent[] makeAntecedents(IProverSequent seq, FreeIdentifier ident, ParametricType type, Expression set,
			IDatatype dt) {
		final List<IAntecedent> antecedents = new ArrayList<IAntecedent>();
		final FormulaFactory ff = seq.getFormulaFactory();
		final ITypeEnvironmentBuilder env = seq.typeEnvironment().makeBuilder();
		final ITypeInstantiation inst = dt.getTypeInstantiation(type);
		final ISetInstantiation instSet = set == null ? null : dt.getSetInstantiation(set);
		Deque<String> baseNames = new LinkedList<>(asList(input.getNames()));
		for (IConstructorExtension constr : dt.getConstructors()) {
			final IConstructorArgument[] arguments = constr.getArguments();
			final FreeIdentifier[] params = makeFreshIdents(arguments, inst, env, baseNames);
			final Expression[] paramSets = makeParamSets(arguments, instSet);
			final Set<Predicate> newHyps = makeNewHyps(ident, constr, type, params, paramSets, seq.goal(), ff);
			antecedents.add(ProverFactory.makeAntecedent(seq.goal(), newHyps, params, null));
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		var result = (PredicatePositionReasoner.Input) super.deserializeInput(reader);
		String[] names = stream(reader.getAntecedents()).flatMap(ante -> stream(ante.getAddedFreeIdents()))
				.map(FreeIdentifier::getName).toArray(String[]::new);
		// We know the names are valid: they were parsed by the formula factory
		return new Input(result.getPred(), result.getPosition(), names);
	}

}
