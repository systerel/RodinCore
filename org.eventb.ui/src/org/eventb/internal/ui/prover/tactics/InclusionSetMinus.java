/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;

/**
 * Provider for the "remove ⊆ with ∖in" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.inclusionSetMinusRight</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public abstract class InclusionSetMinus extends AbstractHypGoalTacticProvider {

	protected static abstract class InclusionSetMinusApplication extends
			DefaultPositionApplication {

		public InclusionSetMinusApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate, parsedString);
		}

		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			final Formula<?> subFormula = predicate.getSubFormula(position);
			final RelationalPredicate rel = (RelationalPredicate) subFormula;
			final Expression child = getChild(rel);
			final BinaryExpression bExp = (BinaryExpression) child;
			return getOperatorPosition(predStr, bExp.getLeft()
					.getSourceLocation().getEnd() + 1, bExp.getRight()
					.getSourceLocation().getStart());
		}

		protected abstract Expression getChild(RelationalPredicate rel);

	}

}
