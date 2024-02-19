/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.EXPN;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Rewriter for the definition of an exponentiation step.
 *
 * Rewrites x^y to x∗x^(y−1) and adds a goal for ¬ y = 0.
 *
 * @author Guillaume Verdier
 */
public class ExponentiationStep extends AbstractManualInference {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exponentiationStep";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "definition of exponentiation step";
	}

	@Override
	protected boolean isExpressionApplicable(Expression expression) {
		return expression.getTag() == EXPN;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred, IPosition position) {
		Predicate predicate = pred;
		if (pred == null) {
			predicate = seq.goal();
		} else if (!seq.containsHypothesis(pred)) {
			return null;
		}
		if (!predicate.isWDStrict(position)) {
			return null;
		}

		Formula<?> formula = predicate.getSubFormula(position);
		if (formula.getTag() != EXPN) {
			return null;
		}
		Expression base = ((BinaryExpression) formula).getLeft();
		Expression power = ((BinaryExpression) formula).getRight();

		var fb = new FormulaBuilder(formula.getFactory());
		IAntecedent[] antecedents = new IAntecedent[2];
		antecedents[0] = ProverFactory.makeAntecedent(fb.notequal(power, fb.intLit(ZERO)));
		var rewritten = fb.mul(base, fb.expn(base, fb.minus(power, fb.intLit(ONE))));
		antecedents[1] = makeAntecedent(pred, predicate.rewriteSubFormula(position, rewritten));

		return antecedents;
	}

}
