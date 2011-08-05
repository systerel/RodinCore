/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
 * Provider for the "Comp. img. rewrites in goal" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.compImg</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class CompImg extends AbstractHypGoalTacticProvider {

	public static class CompImgApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.compImg";

		public CompImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.compImg(hyp, position);
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			final Formula<?> right = parsedPredicate.getSubFormula(position);
			final IPosition prevPosition = position.getPreviousSibling();
			final Formula<?> left = parsedPredicate.getSubFormula(prevPosition);
			return getOperatorPosition(parsedString, left.getSourceLocation()
					.getEnd() + 1, right.getSourceLocation().getStart());
		}

	}

	public static class CompImgAppliInspector extends
			DefaultApplicationInspector {

		public CompImgAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@SuppressWarnings("unused")
		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (!(expression.getTag() == Expression.RELIMAGE)) {
				return;
			}
			final Expression left = expression.getLeft();
			if (!(left.getTag() == Expression.FCOMP)) {
				return;
			}
			final IPosition leftPos = accumulator.getCurrentPosition()
					.getFirstChild(); // Child on the left
			final AssociativeExpression assoc = (AssociativeExpression) left;
			IPosition childPos = leftPos.getFirstChild();
			for (final Expression child : assoc.getChildren()) {
				if (!childPos.isFirstChild()) {
					accumulator.add(new CompImgApplication(hyp, childPos));
				}
				childPos = childPos.getNextSibling();
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new CompImgAppliInspector(hyp));
	}

}
