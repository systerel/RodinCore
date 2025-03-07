/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.UPTO;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;

import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Derives equality of intervals from equality of their bounds.
 *
 * @author Guillaume Verdier
 */
public class DerivEqualInterv extends HypothesisReasoner {

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".derivEqualInterv";
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "equality of intervals";
	}

	public static boolean isApplicable(Predicate predicate) {
		if (predicate.getTag() != EQUAL) {
			return false;
		}
		var equal = (RelationalPredicate) predicate;
		return equal.getLeft().getTag() == UPTO && equal.getRight().getTag() == UPTO;
	}

	@Override
	@ProverRule("DERIV_EQUAL_INTERV_L")
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred) {
		if (pred == null || !isApplicable(pred)) {
			throw new IllegalArgumentException("Invalid input hypothesis: " + pred);
		}
		var equal = (RelationalPredicate) pred;
		var left = (BinaryExpression) equal.getLeft();
		var right = (BinaryExpression) equal.getRight();
		var fb = new FormulaBuilder(seq.getFormulaFactory());
		return new IAntecedent[] { //
				makeAntecedent(fb.le(left.getLeft(), left.getRight())), //
				makeAntecedent(null, //
						Set.of( //
								fb.equal(left.getLeft(), right.getLeft()), //
								fb.equal(left.getRight(), right.getRight()) //
						), makeHideHypAction(Set.of(pred))) //
		};
	}

	@Override
	protected boolean isGoalDependent(IProverSequent sequent, Predicate pred) {
		return false;
	}

}
