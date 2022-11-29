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
 * Rewriter for the equality of cardinals.
 *
 * Rewrites card(S) = card(S) to ∃f·f ∈ S ⤖ T.
 *
 * @author Guillaume Verdier
 */
public class EqualCardRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".equalCardRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "simplify cardinal equality";
	}

	@Override
	@ProverRule("SIMP_EQUAL_CARD")
	public Predicate rewrite(Predicate pred, IPosition position) {
		Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != EQUAL) {
			return null;
		}
		var equal = (RelationalPredicate) subFormula;
		Expression left = equal.getLeft();
		Expression right = equal.getRight();
		if (left.getTag() != KCARD || right.getTag() != KCARD) {
			return null;
		}
		// Given card(S) = card(T), generate ∃f · f∈S⤖T
		FormulaBuilder fb = new FormulaBuilder(equal.getFactory());
		Expression set1 = ((UnaryExpression) left).getChild();
		Expression set2 = ((UnaryExpression) right).getChild();
		Type fType = fb.relType(set1.getType().getBaseType(), set2.getType().getBaseType());
		Expression bij = fb.bij(set1.shiftBoundIdentifiers(1), set2.shiftBoundIdentifiers(1));
		Predicate exists = fb.exists(fb.boundIdentDecl("f", fType), fb.in(fb.boundIdent(0, fType), bij));
		return pred.rewriteSubFormula(position, exists);
	}

}
