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
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Rewriter for the definition of min and max.
 *
 * Rewrites:
 * <ul>
 * <li>min(S) = E or E = min(S) to E ∈ S ∧ (∀ x · x ∈ S ⇒ E ≤ x)</li>
 * <li>max(S) = E or E = max(S) to E ∈ S ∧ (∀ x · x ∈ S ⇒ E ≥ x)</li>
 * </ul>
 *
 * @author Guillaume Verdier
 */
public class MinMaxDefRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".minMaxDefRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "min/max definition";
	}

	@Override
	@ProverRule({ "DEF_EQUAL_MIN", "DEF_EQUAL_MAX" })
	public Predicate rewrite(Predicate pred, IPosition position) {
		// The position points to the min/max, but we also need the parent equality
		IPosition equalPos = position.getParent();
		Formula<?> subFormula = pred.getSubFormula(equalPos);
		if (subFormula == null || subFormula.getTag() != EQUAL) {
			return null;
		}
		var equal = (RelationalPredicate) subFormula;
		Expression minMax, value;
		switch (position.getChildIndex()) {
		case 0:
			minMax = equal.getLeft();
			value = equal.getRight();
			break;
		case 1:
			minMax = equal.getRight();
			value = equal.getLeft();
			break;
		default:
			return null;
		}
		if (minMax.getTag() != KMIN && minMax.getTag() != KMAX) {
			return null;
		}
		Predicate rewritten = rewrite((UnaryExpression) minMax, value);
		return pred.rewriteSubFormula(equalPos, rewritten);
	}

	private Predicate rewrite(UnaryExpression minMaxExpr, Expression value) {
		// Given min/max(S) and its value E, generate E ∈ S ∧ (∀ x · x ∈ S ⇒ E op x)
		// where op = ≤ for min and op = ≥ for max
		FormulaBuilder fb = new FormulaBuilder(minMaxExpr.getFactory());
		Expression set = minMaxExpr.getChild();
		BoundIdentDecl xDecl = fb.boundIdentDecl("x", fb.intType());
		BoundIdentifier x = fb.boundIdent(0, fb.intType());
		Predicate comparison;
		if (minMaxExpr.getTag() == KMIN) {
			comparison = fb.le(value.shiftBoundIdentifiers(1), x);
		} else { // KMAX
			comparison = fb.ge(value.shiftBoundIdentifiers(1), x);
		}
		return fb.and(fb.in(value, set), fb.forall(xDecl, fb.imp(fb.in(x, set.shiftBoundIdentifiers(1)), comparison)));
	}

}
