/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
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
import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteMinIsApplicable;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "Existence of minimum using finite" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.finiteMinGoal</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class FiniteMinGoal implements ITacticProvider {

	public static class FiniteMinGoalApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteMinGoal";

		public FiniteMinGoalApplication() {
			super(null, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.finiteMin();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static final List<ITacticApplication> GOAL_APPLICATION = singletonList(new FiniteMinGoalApplication());

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null)
			return NO_APPLICATIONS;

		if (finiteMinIsApplicable(node.getSequent().goal())) {
			return GOAL_APPLICATION;
		}
		return NO_APPLICATIONS;
	}

}
