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
 * Provider for the "fun. inter. img." tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funInterImg</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunInterImg extends AbstractHypGoalTacticProvider {

	public static class FunInterImgApplication extends
			DefaultPositionApplication {

		public FunInterImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funInterImg(hyp, position);
		}

		@Override
		public String getTacticID() {
			return "org.eventb.ui.funInterImg";
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate, parsedString);
		}

		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			Formula<?> subFormula = predicate.getSubFormula(position);
			Expression right = ((BinaryExpression) subFormula).getRight();
			Expression[] children = ((AssociativeExpression) right)
					.getChildren();
			Expression first = children[0];
			Expression second = children[1];
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}

	}

	public static class FunInterImgAppliInspector extends
			DefaultApplicationInspector {

		private final Predicate pred;

		public FunInterImgAppliInspector(Predicate pred, Predicate hyp) {
			super(hyp);
			this.pred = pred;
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (Tactics.isFunInterImgApp(expression)) {
				final IPosition position = accumulator.getCurrentPosition();
				if (pred.isWDStrict(position)) {
					accumulator.add(new FunInterImgApplication(hyp, position));
				}
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunInterImgAppliInspector(predicate, hyp));
	}

}
