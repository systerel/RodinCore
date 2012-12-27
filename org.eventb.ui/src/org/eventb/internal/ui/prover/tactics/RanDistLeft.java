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
 * Provider for the "rewrites range distribution left" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.ranDistLeft</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class RanDistLeft extends AbstractHypGoalTacticProvider {

	public static class RanDistLeftApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.ranDistLeft";

		public RanDistLeftApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.ranDistLeftRewrites(hyp, position);
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
			final Expression left = ((BinaryExpression) subFormula).getLeft();
			final AssociativeExpression leftAssocExp = (AssociativeExpression) left;
			final Expression[] children = leftAssocExp.getChildren();
			final Expression first = children[0];
			final Expression second = children[1];
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}

	}

	public static class RanDistLeftAppliInspector extends
			DefaultApplicationInspector {

		public RanDistLeftAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			final int tag = expression.getTag();
			if (!(tag == Expression.RANRES || tag == Expression.RANSUB)) {
				return;
			}
			final Expression left = expression.getLeft();
			if (left.getTag() == Expression.BUNION
					|| left.getTag() == Expression.BINTER) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new RanDistLeftApplication(hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new RanDistLeftAppliInspector(hyp));
	}

}
