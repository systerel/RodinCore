/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.ast.IPosition.ROOT;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteCompset;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteCompsetGetPositions;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "finite of comprehension set" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.finiteCompsetGoal</code></li>
 * <li>Target : goal</li>
 * <ul>
 *
 * @author Guillaume Verdier
 */
public class FiniteCompsetGoal implements ITacticProvider {

	public static class FiniteCompsetGoalApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteCompsetGoal";

		public FiniteCompsetGoalApplication() {
			super(null, ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return finiteCompset();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static final List<ITacticApplication> GOAL_APPLICATION = singletonList(new FiniteCompsetGoalApplication());

	@Override
	public List<ITacticApplication> getPossibleApplications(IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null) {
			return NO_APPLICATIONS;
		}
		if (finiteCompsetGetPositions(node.getSequent().goal()).isEmpty()) {
			return NO_APPLICATIONS;
		} else {
			return GOAL_APPLICATION;
		}
	}

}
