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
 * Provider for the "fun. comp. img." tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funCompImg</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunCompImg extends AbstractHypGoalTacticProvider {

	public static class FunCompImgApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funCompImg";

		public FunCompImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funCompImg(hyp, position);
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

	public static class FunCompImgAppliInspector extends
			DefaultApplicationInspector {

		public FunCompImgAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (Tactics.isFunCompImgApplicable(expression)) {
				// expression is like (f;g)(E)
				final IPosition exprPos = accumulator.getCurrentPosition();
				// we take (f;g)
				final IPosition fCGPos = exprPos.getFirstChild();
				// we take g
				final IPosition gPos = fCGPos.getFirstChild().getNextSibling();
				accumulator.add(new FunCompImgApplication(hyp, gPos));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunCompImgAppliInspector(hyp));
	}

}
