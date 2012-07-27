/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.eqv;

import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>eqvGoalTac</code> and
 * <code>eqvHypTac</code>.
 */
public class AutoEqvTacTests {

	
	public static class AutoEqvHypTac extends AbstractTacticTests {

		public AutoEqvHypTac() {
			super(new AutoTactics.EqvRewritesHypAutoTac(),
					"org.eventb.core.seqprover.eqvHypTac");
		}

		/**
		 * Ensures that the EqvRewrites tactic succeeds once on an hypothesis.
		 */
		@Test
		public void applyOnceHyp() {
			assertSuccess(
					" ;H; ;S; s ⊆ ℤ ;; r ∈ s ↔ s ;; (s ≠ ∅) ⇔ (r≠∅) |- ⊥",
					eqv("", empty));
		}
		
		/**
		 * Ensures that the EqvRewrites tactic succeeds once on several hypotheses.
		 */
		@Test
		public void applyManyHyp() {
			assertSuccess(" ;H; ;S; 1=1 ⇔ 2=2 ;; 3=3 ⇔ 4=4 |- ⊥",
					eqv("", eqv("", empty)));
		}
		
		/**
		 * Ensures that the EqvRewrites tactic succeeds recursively on hypotheses.
		 */
		@Test
		public void applyRecursivelyHyp() {
			assertSuccess(" ;H; ;S; a∈ℤ ;; b∈ℤ ;; a≠b ⇔ (b≠a ⇔ a≠b) |- ⊥",
					eqv("", eqv("1", eqv("0", empty))));
		}
		
		/**
		 * Ensures that the EqvRewrites tactic fails when no hypothesis can be
		 * rewritten (even if the goal can).
		 */
		@Test
		public void noApplyHyp() {
			assertFailure(" ;H; ;S; 1=1 ;; 2=2 |- 3=3 ⇔ 4=4");
		}
		
	}
	
	public static class AutoEqvGoalTac extends AbstractTacticTests {

		public AutoEqvGoalTac() {
			super(new AutoTactics.EqvRewritesGoalAutoTac(),
					"org.eventb.core.seqprover.eqvGoalTac");
		}

		/**
		 * Ensures that the EqvRewrites tactic succeeds once on a goal.
		 */
		@Test
		public void applyOnceGoal() {
			assertSuccess(" ;H; ;S; s ⊆ ℤ ;; r ∈ s ↔ s |- (s ≠ ∅) ⇔ (r≠∅)",
					eqv("", empty, empty));
		}

		/**
		 * Ensures that the EqvRewrites tactic succeeds recursively on the goal.
		 */
		@Test
		public void applyRecusivelyGoal() {
			assertSuccess(" ;H; ;S; a∈ℤ ;; b∈ℤ |- a≠b⇔(b≠a⇔a≠b)",
					eqv("", eqv("1", empty), eqv("0", empty)));
		}

		/**
		 * Ensures that the EqvRewrites tactic fails when the goal cannot be
		 * rewritten (even if some hypothesis can).
		 */
		@Test
		public void noApplyGoal() {
			assertFailure(" ;H; ;S; 1=1 ;; 3=3 ⇔ 4=4 |- 2=2");
		}

	}

}
