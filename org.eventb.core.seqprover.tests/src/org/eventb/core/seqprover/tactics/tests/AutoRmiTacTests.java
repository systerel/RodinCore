/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertTacticRegistered;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.genProofTree;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.ri;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rm;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>rmiGoalTac</code> and
 * <code>rmiHypTac</code>.
 */
public class AutoRmiTacTests {

	private static final String HYP_TAC_ID = "org.eventb.core.seqprover.rmiHypTac";
	private static final ITactic HYP_TAC = new AutoTactics.RmiHypAutoTac();

	private static final String GOAL_TAC_ID = "org.eventb.core.seqprover.rmiGoalTac";
	private static final ITactic GOAL_TAC = new AutoTactics.RmiGoalAutoTac();

	/**
	 * Assert that both hypothesis and goal auto tactics are registered
	 */
	@Test
	public void assertRegistered() {
		assertTacticRegistered(HYP_TAC_ID, HYP_TAC);
		assertTacticRegistered(GOAL_TAC_ID, GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds once on a goal
	 */
	@Test
	public void applyOnceGoal() {
		final IProofTree pt = genProofTree(//
				"x ∈ ℤ ",//
				"y ∈ ℤ", //
				"x↦y ∈ id" //
		);
		assertSuccess(pt.getRoot(), rm("", empty), GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds recursively on a goal
	 */
	@Test
	public void applyRecursivelyGoal() {
		final IProofTree pt = genProofTree(//
				"s ⊆ ℤ ",//
				"r∈s ↔ s" //
		);
		// |- r⊆s × s
		// |- ∀x,x0 · x ↦ x0∈r ⇒ x ↦ x0∈s × s
		// |- ∀x,x0 · x ↦ x0∈r ⇒ x∈s ∧ x0∈s
		assertSuccess(pt.getRoot(), rm("", ri("", rm("2.1", empty))), GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds once on an hypothesis
	 */
	@Test
	public void applyOnceHyp() {
		final IProofTree pt = genProofTree(//
				"x⊆ℤ ",//
				"⊥" //
		);
		assertSuccess(pt.getRoot(), ri("", empty), HYP_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds once on an hypothesis
	 */
	@Test
	public void applyRecusivelyHyp() {
		final IProofTree pt = genProofTree(//
				"s⊆ℤ ",// hyp1
				"r∈s ↔ s",// hyp2
				"⊥" // goal
		);
		assertSuccess(pt.getRoot(), rm("", ri("", ri("", rm("2.1", empty)))),
				HYP_TAC);
	}

}
