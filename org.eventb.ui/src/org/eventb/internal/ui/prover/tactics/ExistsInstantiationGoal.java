/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
 * Provider for the "∃ goal (inst)" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.exI</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class ExistsInstantiationGoal implements ITacticProvider {

	public static class ExistsInstantiationGoalApplication extends
			DefaultPositionApplication {

		public ExistsInstantiationGoalApplication() {
			super(null, IPosition.ROOT);
		}

		private static final String TACTIC_ID = "org.eventb.ui.exI";

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.exI(inputs);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && Tactics.exI_applicable(node.getSequent().goal())) {
			singletonList(new ExistsInstantiationGoalApplication());
		}
		return emptyList();
	}

}
