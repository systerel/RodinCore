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
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "remove ⊆ with ∖in" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.inclusionSetMinusRight</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class InclusionSetMinusRight extends AbstractHypGoalTacticProvider {

	public static class InclusionSetMinusRightApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.inclusionSetMinusRight";

		public InclusionSetMinusRightApplication(Predicate hyp,
				IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.inclusionSetMinusRightRewrites(hyp, position);
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
			final Expression right = ((RelationalPredicate) subFormula)
					.getRight();
			final BinaryExpression bExp = (BinaryExpression) right;
			return getOperatorPosition(predStr, bExp.getLeft()
					.getSourceLocation().getEnd() + 1, bExp.getRight()
					.getSourceLocation().getStart());
		}

	}

	public static class InclusionSetMinusRightAppliInspector extends
			DefaultApplicationInspector {

		public InclusionSetMinusRightAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.SUBSETEQ) {
				if (Lib.isSetMinus(predicate.getRight())) {
					final IPosition position = accumulator.getCurrentPosition();
					accumulator.add(new InclusionSetMinusRightApplication(
							hyp, position));
				}
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new InclusionSetMinusRightAppliInspector(hyp));
	}

}
