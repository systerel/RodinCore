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
 * Provider for the " rewrites" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.relOvr</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class RelOvr extends AbstractHypGoalTacticProvider {

	public static class RelOvrApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.relOvr";

		public RelOvrApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.relOvr(hyp, position);
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
			final Formula<?> right = predicate.getSubFormula(position);
			final IPosition prevPosition = position.getPreviousSibling();
			final Formula<?> left = predicate.getSubFormula(prevPosition);
			return getOperatorPosition(predStr, left.getSourceLocation()
					.getEnd() + 1, right.getSourceLocation().getStart());
		}

	}

	public static class RelOvrAppliInspector extends
			DefaultApplicationInspector {

		public RelOvrAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@SuppressWarnings("unused")
		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (expression.getTag() != Expression.OVR) {
				return;
			}
			IPosition childPos = accumulator.getCurrentPosition()
					.getFirstChild();
			for (final Expression child : expression.getChildren()) {
				if (!childPos.isFirstChild()) {
					accumulator.add(new RelOvrApplication(hyp, childPos));
				}
				childPos = childPos.getNextSibling();
			}
		}
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new RelOvrAppliInspector(hyp));
	}

}
