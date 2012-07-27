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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.ri;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rm;

import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactics <code>rmiGoalTac</code> and
 * <code>rmiHypTac</code>.
 */
public class AutoRmiTacTests {

	public static class AutoRmiHypTacTests extends AbstractTacticTests {

		public AutoRmiHypTacTests() {
			super(new AutoTactics.RmiHypAutoTac(),
					"org.eventb.core.seqprover.rmiHypTac");
		}

		/**
		 * Ensures that the Rmi tactic succeeds once on an hypothesis
		 */
		@Test
		public void applyOnceHyp() {
			assertSuccess(" ;H; ;S; x⊆ℤ |- ⊥", ri("", empty));
		}

		/**
		 * Ensures that the Rmi tactic succeeds on several hypotheses.
		 */
		@Test
		public void applyManyHyp() {
			assertSuccess(" ;H; ;S; x∈{1,2} ;; {1}⊆S |- ⊥",
					rm("", ri("", rm("1.0", empty))));
		}

		/**
		 * Ensures that the Rmi tactic succeeds once on an hypothesis
		 */
		@Test
		public void applyRecusivelyHyp() {
			assertSuccess(" ;H; ;S; s⊆ℤ ;; r∈s ↔ s |- ⊥",
					rm("", ri("", ri("", rm("2.1", empty)))));
		}

		/**
		 * Ensures that the Rmi tactic fails when no hypothesis can be rewritten
		 * (even if the goal can).
		 */
		@Test
		public void noApplyHyp() {
			assertFailure(" ;H; ;S; 1=1 |- {1}⊆S");
		}

	}

	public static class AutoRmiGoalTacTests extends AbstractTacticTests {
		public AutoRmiGoalTacTests() {
			super(new AutoTactics.RmiGoalAutoTac(),
					"org.eventb.core.seqprover.rmiGoalTac");
		}

		/**
		 * Ensures that the Rmi tactic succeeds once on a goal
		 */
		@Test
		public void applyOnceGoal() {
			assertSuccess(" ;H; ;S; x ∈ ℤ ;; y ∈ ℤ |- x↦y ∈ id", rm("", empty));
		}

		/**
		 * Ensures that the Rmi tactic succeeds recursively on a goal
		 */
		@Test
		public void applyRecursivelyGoal() {
			assertSuccess(" ;H; ;S; s ⊆ ℤ |- r∈s ↔ s",
					rm("", ri("", rm("2.1", empty))));
		}

		/**
		 * Ensures that the Rmi tactic fails when the goal cannot be rewritten
		 * (even if some hypothesis can).
		 */
		@Test
		public void noApplyGoal() {
			assertFailure(" ;H; ;S; x∈{1,2} ;; {1}⊆S |- 3=3");
		}

	}

}
