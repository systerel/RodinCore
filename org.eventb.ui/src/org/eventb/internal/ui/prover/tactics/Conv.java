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

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "rewrites converse" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.conv</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class Conv extends AbstractHypGoalTacticProvider {

	public static class ConvApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.conv";

		public ConvApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.convRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class ConvAppliInspector extends DefaultApplicationInspector {

		public ConvAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (expression.getTag() != Expression.CONVERSE) {
				return;
			}
			final Expression child = expression.getChild();
			if (child instanceof AssociativeExpression
					&& (child.getTag() == Expression.BUNION //
							|| child.getTag() == Expression.BINTER //
					|| child.getTag() == Expression.FCOMP)) {
				accumulator.add(new ConvApplication(hyp, accumulator
						.getCurrentPosition()));
			} else if (child instanceof BinaryExpression
					&& (child.getTag() == Expression.DOMRES //
							|| child.getTag() == Expression.DOMSUB //
							|| child.getTag() == Expression.RANRES //
					|| child.getTag() == Expression.RANSUB)) {
				accumulator.add(new ConvApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new ConvAppliInspector(hyp));
	}

}
