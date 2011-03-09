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
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.genProofTree;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.eqv;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>eqvGoalTac</code> and
 * <code>eqvHypTac</code>.
 */
public class AutoEqvTacTests {

	private static final ITactic goalTac = new AutoTactics.EqvRewritesGoalAutoTac();
	private static final ITactic hypTac = new AutoTactics.EqvRewritesHypAutoTac();

	private static final String GOAL_TAC_ID = "org.eventb.core.seqprover.eqvGoalTac";
	private static final String HYP_TAC_ID = "org.eventb.core.seqprover.eqvHypTac";

	/**
	 * Assert that both hypothesis and goal auto tactics are registered
	 */
	@Test
	public void assertRegistered() {
		final String[] tacticIds =  {HYP_TAC_ID, GOAL_TAC_ID};
		final ITactic[] tactics = { hypTac, goalTac };
		TacticTestUtils.assertTacticsRegistered(tacticIds, tactics);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds once on a goal
	 */
	@Test
	public void applyOnceGoal() {
		final IProofTree pt = genProofTree(//
				"s ⊆ ℤ ",//
				"r ∈ s ↔ s", //
				"(s ≠ ∅) ⇔ (r≠∅)" //
				// 2 sub-goals: s≠∅ ⇒ r≠∅
				//              r≠∅ ⇒ s≠∅
		);
		assertSuccess(pt.getRoot(), eqv("", empty, empty), goalTac);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds recursively on hypotheses
	 */
	@Test
	public void applyRecursivelyHyp() {
		final IProofTree pt = genProofTree(//
				"a∈ℤ",// hyp1
				"b∈ℤ",// hyp2
				"a≠b⇔(b≠a⇔a≠b)", // hyp3
				"⊥" //goal
				);
		//1:
		//	a≠b⇔(b≠a⇔a≠b)
		//  ⊢	a≠b⇒(b≠a⇔a≠b)
		//		(b≠a⇔a≠b)⇒a≠b
		//2:
		// a≠b⇒(b≠a⇔a≠b)
		// ⊢	a≠b⇒(b≠a⇒a≠b)∧(a≠b⇒b≠a)
		//3:
		// (b≠a⇔a≠b)⇒a≠b
		// ⊢	(b≠a⇒a≠b)∧(a≠b⇒b≠a)⇒a≠b
		assertSuccess(pt.getRoot(), eqv("", eqv("1", eqv("0", empty))), hypTac);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds once on an hypothesis
	 */
	@Test
	public void applyOnceHyp() {
		final IProofTree pt = genProofTree(//
				"s ⊆ ℤ ",//
				"r ∈ s ↔ s", //
				"(s ≠ ∅) ⇔ (r≠∅)", //
				"⊥" //
		);
		// 1 subgoal: ⊥
		assertSuccess(pt.getRoot(), eqv("", empty), hypTac);
	}

	/**
	 * Ensures that the EqvRewrites tactic succeeds recursively on the goal
	 */
	@Test
	public void applyRecusivelyGoal() {
		final IProofTree pt = genProofTree(//
				"a∈ℤ",// hyp1
				"b∈ℤ",// hyp2
				"a≠b⇔(b≠a⇔a≠b)" // goal
		);
		//Step 1 (eqv): 
		// ⊢	a≠b⇔(b≠a⇔a≠b)
		//2 sub-goals :
		//	⊢	a≠b⇒(b≠a⇔a≠b)
			//Step2 (eqv):
			//	⊢ a≠b⇒(b≠a⇒a≠b)∧(a≠b⇒b≠a)
		//  ⊢	(b≠a⇔a≠b)⇒a≠b
			//Step2 (eqv):
			//  ⊢ (b≠a⇒a≠b)∧(a≠b⇒b≠a)⇒a≠b
		assertSuccess(pt.getRoot(), eqv("", eqv("1", empty), eqv("0", empty)),
				goalTac);
	}

}
