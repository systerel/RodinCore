/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
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
 * Provider for the "ovr" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funOvr</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunOvr extends AbstractHypGoalTacticProvider {

	public static class FunOvrApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funOvr";

		public FunOvrApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funOvr(hyp, position);
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
			final AssociativeExpression leftExpr = (AssociativeExpression) left;
			final Expression[] children = leftExpr.getChildren();
			final Expression last = children[children.length - 1];
			final Expression secondLast = children[children.length - 2];
			return getOperatorPosition(predStr, secondLast.getSourceLocation()
					.getEnd() + 1, last.getSourceLocation().getStart());
		}

	}

	public static class FunOvrAppliInspector extends
			DefaultApplicationInspector {

		public FunOvrAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (Tactics.isFunOvrApp(expression)) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new FunOvrApplication(hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunOvrAppliInspector(hyp));
	}

}
