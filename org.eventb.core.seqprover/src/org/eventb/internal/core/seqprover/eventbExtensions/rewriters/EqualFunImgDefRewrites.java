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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FUNIMAGE;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Rewriter for the definition of equality to the functional image.
 *
 * Rewrites f(x) = y or y = f(x) to x ↦ y ∈ f.
 *
 * @author Guillaume Verdier
 */
public class EqualFunImgDefRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".equalFunImgDefRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "functional image equality definition";
	}

	@ProverRule("DEF_EQUAL_FUN_IMAGE")
	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		// Position should point to a functional image that is the child of an equality.
		if (position.isRoot() || position.getChildIndex() > 1) {
			return null;
		}
		Formula<?> eqFormula = pred.getSubFormula(position.getParent());
		if (eqFormula == null || eqFormula.getTag() != EQUAL) {
			return null;
		}
		var equal = (RelationalPredicate) eqFormula;

		var funImgExpr = equal.getChild(position.getChildIndex());
		if (funImgExpr.getTag() != FUNIMAGE) {
			return null;
		}
		var funImg = (BinaryExpression) funImgExpr;

		var other = equal.getChild(1 - position.getChildIndex());

		var fb = new FormulaBuilder(funImg.getFactory());
		var rewritten = fb.in(fb.mapsTo(funImg.getRight(), other), funImg.getLeft());
		return pred.rewriteSubFormula(position.getParent(), rewritten);
	}

}
