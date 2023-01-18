/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Université de Lorraine - additional hypotheses for set membership
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BOUND_IDENT_DECL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeConj;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.NO_PRED;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.isDatatypeType;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeArgName;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ITypeInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Makes an induction on an inductive datatypes.
 * 
 * Antecedents are created for each constructor.
 * 
 * @author Nicolas Beauger
 */
public class DTInduction extends PredicatePositionReasoner implements IVersionedReasoner {

	private static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".dtInduction";
	private static final String DISPLAY_NAME = "dt induc";

	private static final int VERSION = 2;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public int getVersion() {
		return VERSION;
	}

	@ProverRule("DATATYPE_INDUCTION")
	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm) {
		var input = (Input) reasonerInput;
		IAntecedent[] antecedents = getAntecedents(seq, input.getPosition());
		if (antecedents == null) {
			return reasonerFailure(this, input,
					"Inference " + getReasonerID() + " is not applicable for " + seq.goal());
		}
		// Generate the successful reasoner output
		return makeProofRule(this, input, seq.goal(), DISPLAY_NAME, antecedents);
	}

	protected IAntecedent[] getAntecedents(IProverSequent seq, IPosition position) {
		Predicate goal = seq.goal();
		if (goal.getTag() != FORALL || !position.getParent().isRoot()) {
			return null;
		}
		Formula<?> formula = goal.getSubFormula(position);
		if (formula.getTag() != BOUND_IDENT_DECL) {
			return null;
		}
		var decl = (BoundIdentDecl) formula;
		Type type = decl.getType();
		if (!isDatatypeType(type)) {
			return null;
		}

		final ParametricType prmType = (ParametricType) type;
		final IExpressionExtension ext = prmType.getExprExtension();
		final IDatatype dt = (IDatatype) ext.getOrigin();
		return makeAntecedents(seq, prmType, dt, position.getChildIndex());
	}

	private IAntecedent[] makeAntecedents(IProverSequent seq, ParametricType type, IDatatype dt, int inducIndex) {
		final List<IAntecedent> antecedents = new ArrayList<IAntecedent>();
		final FormulaFactory ff = seq.getFormulaFactory();
		final FormulaBuilder fb = new FormulaBuilder(ff);
		final ITypeInstantiation inst = dt.getTypeInstantiation(type);
		final QuantifiedPredicate goal = (QuantifiedPredicate) seq.goal();
		for (IConstructorExtension constr : dt.getConstructors()) {
			final IConstructorArgument[] arguments = constr.getArguments();
			final BoundIdentDecl[] decls = new BoundIdentDecl[arguments.length];
			final BoundIdentifier[] params = new BoundIdentifier[arguments.length];
			final List<Predicate> hyps = new ArrayList<>();
			for (int i = 0; i < arguments.length; i++) {
				Type argType = arguments[i].getType(inst);
				decls[i] = fb.boundIdentDecl(makeArgName(arguments[i], i), argType);
				params[i] = fb.boundIdent(arguments.length - i - 1, argType);
				if (argType.equals(type)) {
					hyps.add(instantiateBound(goal, inducIndex, params[i], ff));
				}
			}
			final Expression constrExpr = ff.makeExtendedExpression(constr, params, NO_PRED, null, type);
			Predicate newGoal = instantiateBound(goal, inducIndex, constrExpr, ff);
			if (arguments.length > 0) {
				if (!hyps.isEmpty()) {
					newGoal = fb.imp(makeConj(ff, hyps), newGoal);
				}
				newGoal = fb.forall(decls, newGoal);
			}
			antecedents.add(makeAntecedent(newGoal, null, null, null));
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

	private Predicate instantiateBound(QuantifiedPredicate pred, int index, Expression expr, FormulaFactory ff) {
		var replacements = new Expression[pred.getBoundIdentDecls().length];
		replacements[index] = expr;
		return pred.instantiate(replacements, ff);
	}

}
