/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ONE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.KCARD;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Rewriter for the definition of card.
 *
 * Rewrites card(S) = k or k = card(S) to ∃f·f ∈ 1‥k ⤖ S.
 *
 * @author Guillaume Verdier
 */
public class CardDefRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".cardDefRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "cardinal definition";
	}

	@Override
	@ProverRule("DEF_EQUAL_CARD")
	public Predicate rewrite(Predicate pred, IPosition position) {
		// The position points to the card(), but we also need the parent equality
		IPosition equalPos = position.getParent();
		Formula<?> subFormula = pred.getSubFormula(equalPos);
		if (subFormula == null || subFormula.getTag() != EQUAL) {
			return null;
		}
		var equal = (RelationalPredicate) subFormula;
		Expression card, value;
		switch (position.getChildIndex()) {
		case 0:
			card = equal.getLeft();
			value = equal.getRight();
			break;
		case 1:
			card = equal.getRight();
			value = equal.getLeft();
			break;
		default:
			return null;
		}
		if (card.getTag() != KCARD) {
			return null;
		}
		Predicate rewritten = rewrite((UnaryExpression) card, value);
		return pred.rewriteSubFormula(equalPos, rewritten);
	}

	private Predicate rewrite(UnaryExpression cardExpr, Expression value) {
		// Given card(S) and its value, generate ∃f · f∈1‥value⤖S
		FormulaBuilder fb = new FormulaBuilder(cardExpr.getFactory());
		Expression set = cardExpr.getChild();
		Type fType = fb.relType(fb.intType(), set.getType().getBaseType());
		Expression upTo = fb.upto(fb.intLit(ONE), value.shiftBoundIdentifiers(1));
		Expression bij = fb.bij(upTo, set.shiftBoundIdentifiers(1));
		return fb.exists(fb.boundIdentDecl("f", fType), fb.in(fb.boundIdent(0, fType), bij));
	}

}
