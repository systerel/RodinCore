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
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.genProofTree;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.dti;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 * 
 *         Units tests for the auto-tactic DisjGoalTac
 */
public class DisjGoalTacTests {

	private static final ITactic tac = new AutoTactics.DisjGoalTac();
	private static final String TAC_ID = "org.eventb.core.seqprover.DisjGoalTac";

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		final String[] tacticIds = { TAC_ID };
		final ITactic[] tactics = { tac };
		TacticTestUtils.assertTacticsRegistered(tacticIds, tactics);
	}

	/**
	 * Ensures that DisjGoalTac succeeds once.
	 */
	@Test
	public void applyOnce() {
		assertSucceed("1=1 ∨ 2=2", disjGoal(empty));
	}

	/**
	 * Ensures that DisjGoalTac succeeds several times.
	 */
	@Test
	public void applyMany() {
		assertSucceed("1=1 ∨ 2=2 ∨ 3=3 ∨ 4=4",
				disjGoal(disjGoal(disjGoal(empty))));
	}

	/**
	 * Ensures that DisjGoalTac fails when the goal is not a disjunction.
	 */
	@Test
	public void notDisj() {
		assertFails("1=1⇒2=2");
		assertFails("(1=1∨2=2)∧(3=3∨4=4)");
		assertFails("¬(1=1∨2=2)");
	}

	/**
	 * Assert that the application of the DisjGoalTac on a node made up of one
	 * goal returns <code>null</code> and that the resulting tree shape is equal
	 * to the given <code>shape</code> tree shape.
	 * 
	 * @param goalStr
	 *            the considered goal
	 * @param shape
	 *            the expected tree shape
	 */
	private void assertSucceed(final String goalStr, final TreeShape shape) {
		final IProofTree pt = genProofTree(goalStr);
		assertSuccess(pt.getRoot(), shape, tac);
	}

	/**
	 * Assert that the application of the DisjGoalTac on a node made up of one
	 * goal does not return <code>null</code>.
	 * 
	 * @param goalStr
	 *            the considered goal in String
	 */
	private void assertFails(final String goalStr) {
		final IProofTree pt = genProofTree(goalStr);
		assertFailure(pt.getRoot(), tac);
	}

	/**
	 * Returns a Treeshape corresponding to one application of DisjGoalTac.
	 * 
	 * @param child
	 *            the TreeShape child (should be either empty or an other
	 *            disjGoal TreeShape).
	 * @return a Treeshape corresponding to one application of DisjGoalTac.
	 */
	private static TreeShape disjGoal(TreeShape child) {
		return dti("", impI(child));
	}

}
