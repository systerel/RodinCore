/*******************************************************************************
 * Copyright (c) 2007, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.isCardComparisonApplicable;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "card. comparison" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.cardComparisonGoal</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class CardComparisonGoal extends AbstractHypGoalTacticProvider {

	public static class CardComparisonApplication extends DefaultPositionApplication {
		
		private static final String TACTIC_ID = "org.eventb.ui.cardComparisonGoal";

		public CardComparisonApplication() {
			super(null, IPosition.ROOT);
		}
		
		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.cardComparison();
		}
		
	}

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static final List<ITacticApplication> GOAL_APPLICATION = singletonList(new CardComparisonApplication());

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		// Tactic can only be applied to the root element of the goal
		if (hyp == null && isCardComparisonApplicable(predicate)) {
			return GOAL_APPLICATION;
		} else {
			return NO_APPLICATIONS;
		}
	}

}
