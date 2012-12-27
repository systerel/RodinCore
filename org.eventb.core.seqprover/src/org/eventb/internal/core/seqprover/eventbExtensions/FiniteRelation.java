/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isFinite;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isRelation;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isSetOfRelation;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public class FiniteRelation extends SingleExprInputReasoner implements
		IVersionedReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".finiteRelation";

	private static final int VERSION = 0;

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("FIN_REL_R")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final Predicate goal = seq.goal();
		if (!isFinite(goal))
			return reasonerFailure(this, input, "Goal is not a finiteness");
		final Expression r = ((SimplePredicate) goal).getExpression();
		if (!isRelation(r))
			return reasonerFailure(this, input,
					"Goal is not a finiteness of a relation");

		if (input.hasError()) {
			return reasonerFailure(this, input, input.getError());
		}
		if (!(input instanceof SingleExprInput))
			return reasonerFailure(this, input,
					"Expected a single expression input");
		final Expression relSet = ((SingleExprInput) input).getExpression();
		if (!isSetOfRelation(relSet)) {
			return reasonerFailure(this, input,
					"Expected a set of all relations S ↔ T");
		}
		final Expression S = ((BinaryExpression) relSet).getLeft();
		final Expression T = ((BinaryExpression) relSet).getRight();

		// Check compatibility of types
		if (!r.getType().equals(relSet.getType().getBaseType())) {
			return reasonerFailure(this, input, "Type check failed for " + r
					+ "∈" + relSet);
		}
		
		final FormulaFactory ff = seq.getFormulaFactory();
		final IAntecedent[] antecedents = new IAntecedent[] { //
				makeAntecedent(relSet.getWDPredicate(ff)), //
				makeAntecedent(ff.makeRelationalPredicate(IN, r, relSet, null)), //
				makeAntecedent(ff.makeSimplePredicate(KFINITE, S, null)), //
				makeAntecedent(ff.makeSimplePredicate(KFINITE, T, null)), //
		};

		return makeProofRule(this, input, goal, "finite of relation",
				antecedents);
	}

	public int getVersion() {
		return VERSION;
	}

}
