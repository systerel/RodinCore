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
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "finite set" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.finiteSetGoal</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class FiniteSetGoal implements ITacticProvider {

	
	public static class FiniteSetGoalApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteSetGoal";
		private final IProofTreeNode node;

		public FiniteSetGoalApplication(IProofTreeNode node) {
			super(null, IPosition.ROOT);
			this.node = node;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.finiteSet(node.getSequent(), globalInput);
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
			final ITacticApplication appli = new FiniteSetGoalApplication(node);
			return Collections.singletonList(appli);
		}
		return emptyList();
	}

}
