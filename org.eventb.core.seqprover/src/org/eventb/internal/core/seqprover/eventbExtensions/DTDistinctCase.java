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

import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.isDatatypeType;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeFreshIdents;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeIdentEqualsConstr;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.makeParamSets;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.predIsExtSetMembership;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.datatype.ITypeInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

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

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return DISPLAY_NAME;
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
		final ITypeEnvironment env = seq.typeEnvironment();
		final ITypeInstantiation inst = dt.getTypeInstantiation(type);
		final ISetInstantiation instSet = set == null ? null : dt.getSetInstantiation(set);
		for (IConstructorExtension constr : dt.getConstructors()) {
			final IConstructorArgument[] arguments = constr.getArguments();
			final FreeIdentifier[] params = makeFreshIdents(arguments, inst, ff, env);
			final Expression[] paramSets = makeParamSets(arguments, instSet);
			final Set<Predicate> newHyps = makeNewHyps(ident, constr, type, params, paramSets, seq.goal(), ff);
			antecedents.add(ProverFactory.makeAntecedent(seq.goal(), newHyps, params, null));
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

}
