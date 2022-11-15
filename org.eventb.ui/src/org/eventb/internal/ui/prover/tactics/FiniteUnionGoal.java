/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.ast.IPosition.ROOT;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteUnion;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteUnionGetPositions;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "finite of union" tactic.
 * <ul>
 * <li>Provider ID: <code>org.eventb.ui.finiteUnionGoal</code></li>
 * <li>Target: goal</li>
 * <ul>
 *
 * @author Guillaume Verdier
 */
public class FiniteUnionGoal implements ITacticProvider {

	public static class FiniteUnionGoalApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteUnionGoal";

		public FiniteUnionGoalApplication() {
			super(null, ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return finiteUnion();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static final List<ITacticApplication> GOAL_APPLICATION = singletonList(new FiniteUnionGoalApplication());

	@Override
	public List<ITacticApplication> getPossibleApplications(IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null) {
			return NO_APPLICATIONS;
		}
		final Predicate goal = node.getSequent().goal();
		return finiteUnionGetPositions(goal).isEmpty() ? NO_APPLICATIONS : GOAL_APPLICATION;
	}

}
