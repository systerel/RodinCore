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

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertFailure;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertTacticRegistered;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.ri;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rm;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactics <code>rmiGoalTac</code> and
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
		assertSuccess(genSeq("x ∈ ℤ ;; y ∈ ℤ |- x↦y ∈ id"), rm("", empty),
				GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds recursively on a goal
	 */
	@Test
	public void applyRecursivelyGoal() {
		assertSuccess(genSeq("s ⊆ ℤ |- r∈s ↔ s"),
				rm("", ri("", rm("2.1", empty))), GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic fails when the goal cannot be rewritten (even
	 * if some hypothesis can).
	 */
	@Test
	public void noApplyGoal() {
		assertFailure(genSeq("x∈{1,2} ;; {1}⊆S |- 3=3"), GOAL_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds once on an hypothesis
	 */
	@Test
	public void applyOnceHyp() {
		assertSuccess(genSeq("x⊆ℤ |- ⊥"), ri("", empty), HYP_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds on several hypotheses.
	 */
	@Test
	public void applyManyHyp() {
		assertSuccess(genSeq("x∈{1,2} ;; {1}⊆S |- ⊥"),
				rm("", ri("", rm("1.0", empty))), HYP_TAC);
	}

	/**
	 * Ensures that the Rmi tactic succeeds once on an hypothesis
	 */
	@Test
	public void applyRecusivelyHyp() {
		assertSuccess(genSeq("s⊆ℤ ;; r∈s ↔ s |- ⊥"),
				rm("", ri("", ri("", rm("2.1", empty)))), HYP_TAC);
	}

	/**
	 * Ensures that the Rmi tactic fails when no hypothesis can be rewritten
	 * (even if the goal can).
	 */
	@Test
	public void noApplyHyp() {
		assertFailure(genSeq("1=1 |- {1}⊆S"), HYP_TAC);
	}

}
