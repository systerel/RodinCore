/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointInstantiator2.instantiateExpression;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Handles the processing for rewriting rule SIMP_COMPSET_EQUAL. The following
 * operations are performed:
 * <ul>
 * <li>checking the validity of the quantified expression: its predicate must be
 * a conjunction of valid replacements, with no quantified bound identifier
 * appearing in the right hand-side of any of the replacements,</li>
 * <li>instantiating the bound identifiers with their corresponding expressions.
 * </li>
 * </ul>
 */
public class OnePointProcessorExpression extends OnePointProcessor<Expression> {

	public OnePointProcessorExpression(QuantifiedExpression qExpr,
			FormulaFactory ff) {
		super(ff);
		this.original = qExpr;
		this.bids = qExpr.getBoundIdentDecls();
		this.processing = qExpr;
		this.replacements = new Expression[bids.length];
	}

	@Override
	public void matchAndInstantiate() {
		successfullyApplied = false;

		if (!validExpression()) {
			return;
		}

		processing = instantiate(processing, replacements);
		successfullyApplied = true;
	}

	private boolean validExpression() {
		final Predicate pred = ((QuantifiedExpression) original).getPredicate();

		if (pred.getTag() == EQUAL) {
			if (!checkReplacement((RelationalPredicate) pred)) {
				return false;
			}
		} else if (pred.getTag() == LAND) {
			final AssociativePredicate land = (AssociativePredicate) pred;
			for (Predicate child : land.getChildren()) {
				if (child.getTag() != EQUAL) {
					return false;
				}
				if (!checkReplacement((RelationalPredicate) child)) {
					return false;
				}
			}
		} else {
			return false;
		}

		if (hasInnerBIReplacement()) {
			return false;
		}

		return true;
	}

	private boolean hasInnerBIReplacement() {
		for (Expression rep : replacements) {
			if (rep.getTag() == BOUND_IDENT) {
				if (((BoundIdentifier) rep).getBoundIndex() < bids.length) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Expression instantiate(Expression expression,
			Expression[] replacements) {
		assert expression instanceof QuantifiedExpression;
		return instantiateExpression((QuantifiedExpression) expression,
				replacements, ff);
	}
}
