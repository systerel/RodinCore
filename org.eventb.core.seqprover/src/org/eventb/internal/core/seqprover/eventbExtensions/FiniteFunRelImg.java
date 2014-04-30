/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
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

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isRelImg;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInput;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInputReasoner;

@ProverRule("FIN_FUN_IMG_R")
public class FiniteFunRelImg extends PFunSetInputReasoner {

	private static final int VERSION = 0;

	private static final String REASONER_DESC = "finite of relational image of a function";

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".finiteFunRelImg";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	public static int getVersion() {
		return VERSION;
	}

	@Override
	protected String getReasonerDesc() {
		return REASONER_DESC;
	}

	@Override
	protected IReasonerFailure verifyGoal(Predicate goal, IReasonerInput input) {
		// f[s]
		final Expression expr = getFiniteExpression(goal);
		if (!isRelImg(expr)) {
			return reasonerFailure(this, input,
					"Goal is not a finiteness of a relation image");
		}
		return null;
	}

	@Override
	protected IReasonerFailure verifyInput(Predicate goal, PFunSetInput input) {
		// f[s]
		final BinaryExpression img = (BinaryExpression) getFiniteExpression(goal);
		final Expression f = img.getLeft();
		final Expression set = input.getExpression();

		if (!f.getType().equals(set.getType().getBaseType())) {
			return reasonerFailure(this, input, "Type check failed for " + f
					+ "∈" + set);
		}
		return null;
	}

	@Override
	protected Predicate[] getSubgoals(Predicate goal, PFunSetInput input) {
		final BinaryExpression img = (BinaryExpression) getFiniteExpression(goal);
		final Expression f = img.getLeft();
		final Expression s = img.getRight();
		final Expression inputExpr = input.getExpression();
		final FormulaFactory ff = goal.getFactory();
		return new Predicate[] {
		// WD(S +-> T)
				inputExpr.getWDPredicate(), //

				// f : S +-> T
				ff.makeRelationalPredicate(IN, f, inputExpr, null),

				// finite(s)
				ff.makeSimplePredicate(KFINITE, s, null), //
		};
	}

}
