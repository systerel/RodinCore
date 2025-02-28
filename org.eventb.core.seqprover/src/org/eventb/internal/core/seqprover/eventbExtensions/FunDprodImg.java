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

import static org.eventb.core.ast.Formula.DPROD;
import static org.eventb.core.ast.Formula.FUNIMAGE;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Replaces function application of a direct product by its definition.
 *
 * @author Guillaume Verdier
 */
public class FunDprodImg extends AbstractManualInference {

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funDprodImg";
	}

	@Override
	protected boolean isExpressionApplicable(Expression expression) {
		if (expression.getTag() != FUNIMAGE) {
			return false;
		}
		var left = ((BinaryExpression) expression).getLeft();
		return left.getTag() == DPROD;
	}

	@Override
	protected String getDisplayName() {
		return "direct product fun. image";
	}

	@Override
	@ProverRule({ "SIM_DPROD_L", "SIM_DPROD_R" })
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred, IPosition position) {
		Predicate predicate = pred;
		if (pred == null) {
			predicate = seq.goal();
		} else if (!seq.containsHypothesis(pred)) {
			return null;
		}
		Formula<?> formula = predicate.getSubFormula(position);
		if (formula == null || formula.getTag() != FUNIMAGE) {
			return null;
		}
		var funImg = (BinaryExpression) formula;
		var fun = funImg.getLeft();
		if (fun == null || fun.getTag() != DPROD) {
			return null;
		}
		var dprod = (BinaryExpression) fun;
		var arg = funImg.getRight();
		var fb = new FormulaBuilder(seq.getFormulaFactory());
		var funImg2 = fb.mapsTo(fb.funimg(dprod.getLeft(), arg), fb.funimg(dprod.getRight(), arg));
		var inferredPred = predicate.rewriteSubFormula(position, funImg2);
		return new IAntecedent[] { //
				makeWD(inferredPred), //
				makeAntecedent(pred, inferredPred) //
		};
	}

}
