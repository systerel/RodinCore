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

import org.eventb.core.ast.Formula;
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
 * Provider for the "card. up to" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.cardUpTo</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class CardUpTo extends AbstractHypGoalTacticProvider {

	public static class CardUpToApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.cardUpTo";

		public CardUpToApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.cardUpToRewrites(hyp, position);
		}

	}
	
	public static class CardUpToAppliInspector extends DefaultApplicationInspector {

		public CardUpToAppliInspector(Predicate hyp) {
			super(hyp);
		}
		
		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (expression.getTag() != Formula.KCARD) {
				return;
			}
			if (expression.getChild().getTag() == Formula.UPTO) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new CardUpToApplication(hyp, position));
			}
		}
		
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new CardUpToAppliInspector(hyp));
	}

}
