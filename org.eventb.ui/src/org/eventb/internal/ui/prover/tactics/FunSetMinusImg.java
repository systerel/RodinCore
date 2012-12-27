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
 * Provider for the "fun. set minus img." tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funSetMinusImg</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunSetMinusImg extends AbstractHypGoalTacticProvider {

	public static class FunSetMinusImgApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funSetMinusImg";

		public FunSetMinusImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funSetMinusImg(hyp, position);
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
			final Expression setMinus = ((BinaryExpression) subFormula)
					.getRight();
			final Expression first = ((BinaryExpression) setMinus).getLeft();
			final Expression second = ((BinaryExpression) setMinus).getRight();
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}

	}

	public static class FunSetMinusImgAppliInspector extends
			DefaultApplicationInspector {

		private final Predicate pred;

		public FunSetMinusImgAppliInspector(Predicate pred, Predicate hyp) {
			super(hyp);
			this.pred = pred;
		}

		/**
		 * Selection of expression like f[S ∖ T]
		 */
		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (!(expression.getTag() == Formula.RELIMAGE)) {
				return;
			}
			if (expression.getRight().getTag() == Formula.SETMINUS) {
				final IPosition position = accumulator.getCurrentPosition();
				if (pred.isWDStrict(position)) {
					accumulator
							.add(new FunSetMinusImgApplication(hyp, position));
				}
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunSetMinusImgAppliInspector(predicate,
				hyp));
	}

}
