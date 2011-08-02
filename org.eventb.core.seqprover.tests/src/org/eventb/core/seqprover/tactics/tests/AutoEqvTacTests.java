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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.eqv;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>eqvGoalTac</code> and
 * <code>eqvHypTac</code>.
 */
public class AutoEqvTacTests {

	private static final String HYP_TAC_ID = "org.eventb.core.seqprover.eqvHypTac";
	private static final ITactic HYP_TAC = new AutoTactics.EqvRewritesHypAutoTac();

	private static final String GOAL_TAC_ID = "org.eventb.core.seqprover.eqvGoalTac";
	private static final ITactic GOAL_TAC = new AutoTactics.EqvRewritesGoalAutoTac();

	/**
	 * Assert that both hypothesis and goal auto tactics are registered
	 */
	@Test
	public void assertRegistered() {
		assertTacticRegistered(HYP_TAC_ID, HYP_TAC);
		assertTacticRegistered(GOAL_TAC_ID, GOAL_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds once on an hypothesis.
	 */
	@Test
	public void applyOnceHyp() {
		assertSuccess(genSeq("s ⊆ ℤ ;; r ∈ s ↔ s ;; (s ≠ ∅) ⇔ (r≠∅) |- ⊥"),
				eqv("", empty), HYP_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds once on several hypotheses.
	 */
	@Test
	public void applyManyHyp() {
		assertSuccess(genSeq("1=1 ⇔ 2=2 ;; 3=3 ⇔ 4=4 |- ⊥"),
				eqv("", eqv("", empty)), HYP_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds recursively on hypotheses.
	 */
	@Test
	public void applyRecursivelyHyp() {
		assertSuccess(genSeq("a∈ℤ ;; b∈ℤ ;; a≠b ⇔ (b≠a ⇔ a≠b) |- ⊥"),
				eqv("", eqv("1", eqv("0", empty))), HYP_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic fails when no hypothesis can be
	 * rewritten (even if the goal can).
	 */
	@Test
	public void noApplyHyp() {
		assertFailure(genSeq("1=1 ;; 2=2 |- 3=3 ⇔ 4=4"), HYP_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds once on a goal.
	 */
	@Test
	public void applyOnceGoal() {
		assertSuccess(genSeq("s ⊆ ℤ ;; r ∈ s ↔ s |- (s ≠ ∅) ⇔ (r≠∅)"),
				eqv("", empty, empty), GOAL_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds recursively on the goal.
	 */
	@Test
	public void applyRecusivelyGoal() {
		assertSuccess(genSeq("a∈ℤ ;; b∈ℤ |- a≠b⇔(b≠a⇔a≠b)"),
				eqv("", eqv("1", empty), eqv("0", empty)), GOAL_TAC);
	}

	/**
	 * Ensures that the EqvRewrites tactic fails when the goal cannot be
	 * rewritten (even if some hypothesis can).
	 */
	@Test
	public void noApplyGoal() {
		assertFailure(genSeq("1=1 ;; 3=3 ⇔ 4=4 |- 2=2"), GOAL_TAC);
	}

}
