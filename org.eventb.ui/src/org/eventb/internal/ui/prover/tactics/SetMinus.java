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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "rewrites set minus" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.setMinus</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class SetMinus extends AbstractHypGoalTacticProvider {

	public static class SetMinusApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.setMinus";

		public SetMinusApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.setMinusRewrites(hyp, position);
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
			if (right instanceof AssociativeExpression) {
				final AssociativeExpression rightExpr = (AssociativeExpression) right;
				final Expression[] children = rightExpr.getChildren();
				final Expression first = children[0];
				final Expression second = children[1];
				return getOperatorPosition(predStr, first.getSourceLocation()
						.getEnd() + 1, second.getSourceLocation().getStart());
			} else {
				final BinaryExpression bExp = (BinaryExpression) right;
				return getOperatorPosition(predStr, bExp.getLeft()
						.getSourceLocation().getEnd() + 1, bExp.getRight()
						.getSourceLocation().getStart());
			}
		}

	}

	public class SetMinusAppliInspector extends DefaultApplicationInspector {

		private final FormulaFactory ff;

		public SetMinusAppliInspector(FormulaFactory ff, Predicate hyp) {
			super(hyp);
			this.ff = ff;
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (!(expression.getTag() == Expression.SETMINUS)) {
				return;
			}
			final Expression left = expression.getLeft();
			final Type baseType = left.getType().getBaseType();
			if (left.equals(baseType.toExpression(ff))) {
				final Expression right = expression.getRight();
				if (Lib.isUnion(right) || Lib.isInter(right)
						|| Lib.isSetMinus(right)) {
					final IPosition position = accumulator.getCurrentPosition();
					accumulator.add(new SetMinusApplication(hyp, position));
				}
			}
		}
		
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new SetMinusAppliInspector(node
				.getFormulaFactory(), hyp));
	}

}
