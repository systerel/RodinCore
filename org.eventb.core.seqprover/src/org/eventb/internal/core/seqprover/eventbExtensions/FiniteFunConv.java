/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed rules FIN_FUN_*
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isRelation;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInput;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInputReasoner;

@ProverRule("FIN_FUN2_R")
public class FiniteFunConv extends PFunSetInputReasoner {

	private static final int VERSION = 0;

	private static final String REASONER_DESC = "finite of function converse";

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".finiteFunConv";

	public static int getVersion() {
		return VERSION;
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getReasonerDesc() {
		return REASONER_DESC;
	}

	@Override
	protected IReasonerFailure verifyGoal(Predicate goal, IReasonerInput input) {
		final Expression f = getFiniteExpression(goal);
		if (!isRelation(f)) {
			return reasonerFailure(this, input,
					"Goal is not a finiteness of a relation");
		}
		return null;
	}

	@Override
	protected IReasonerFailure verifyInput(Predicate goal, PFunSetInput input,
			FormulaFactory ff) {
		final Expression fConverse = getFunctionConverse(goal, ff);
		final Expression expr = input.getExpression();
		if (!fConverse.getType().equals(expr.getType().getBaseType())) {
			return reasonerFailure(this, input, "Type check failed for "
					+ fConverse + "∈" + expr);
		}
		return null;
	}

	@Override
	protected Predicate[] getSubgoals(Predicate goal, PFunSetInput input,
			FormulaFactory ff) {
		final Expression fConverse = getFunctionConverse(goal, ff);
		final Expression inputExpr = input.getExpression();
		final Expression S = input.getLeft();
		return new Predicate[] {
		// WD(S +-> T)
				inputExpr.getWDPredicate(ff), //

				// f~ : S +-> T
				ff.makeRelationalPredicate(IN, fConverse, inputExpr, null), //

				// finite(S)
				ff.makeSimplePredicate(KFINITE, S, null), //
		};
	}

	private Expression getFunctionConverse(Predicate goal, FormulaFactory ff) {
		final Expression f = getFiniteExpression(goal);
		return ff.makeUnaryExpression(CONVERSE, f, null);
	}

}
