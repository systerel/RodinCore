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

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "rewrites range distribution right" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.ranDistRight</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class RanDistRight extends AbstractHypGoalTacticProvider {

	public static class RanDistRightApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.ranDistRight";

		public RanDistRightApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.ranDistRightRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate, parsedString);
		}

		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			final Formula<?> subFormula = predicate.getSubFormula(position);
			final Expression right = ((BinaryExpression) subFormula).getRight();
			final AssociativeExpression rightExp = (AssociativeExpression) right;
			final Expression[] children = rightExp.getChildren();
			final Expression first = children[0];
			final Expression second = children[1];
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}

	}

	public static class RanDistRightAppliInspector extends
			DefaultApplicationInspector {

		public RanDistRightAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			final int exprTag = expression.getTag();
			if (!(exprTag == Expression.RANRES || exprTag == Expression.RANSUB)) {
				return;
			}
			final Expression right = expression.getRight();
			final int rightTag = right.getTag();
			if (rightTag == Expression.BUNION || rightTag == Expression.BINTER) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new RanDistRightApplication(hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new RanDistRightAppliInspector(hyp));
	}

}
