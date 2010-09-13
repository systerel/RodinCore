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

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider2;

/**
 * Provider for the "finite of ∖" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.finiteSetMinusGoal</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class FiniteSetMinusGoal implements ITacticProvider2 {

	public static class FiniteSetMinusGoalApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteSetMinusGoal";

		public FiniteSetMinusGoalApplication() {
			super(null, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.finiteSetMinus();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null)
			return emptyList();

		final Predicate goal = node.getSequent().goal();
		if (Lib.isFinite(goal)) {
			if (Lib.isSetMinus(((SimplePredicate) goal).getExpression())) {
				final ITacticApplication appli = new FiniteSetMinusGoalApplication();
				return Collections.singletonList(appli);
			}
		}
		return emptyList();
	}

}
