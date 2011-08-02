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

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertTacticRegistered;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.dti;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impI;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 * 
 *         Units tests for the auto-tactic DisjGoalTac
 */
public class DisjGoalTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.DisjGoalTac";
	private static final ITactic TAC = new AutoTactics.DisjGoalTac();

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		assertTacticRegistered(TAC_ID, TAC);
	}

	/**
	 * Ensures that DisjGoalTac succeeds once.
	 */
	@Test
	public void applyOnce() {
		assertSuccess("1=1 ∨ 2=2", disjGoal(empty));
	}

	/**
	 * Ensures that DisjGoalTac succeeds several times.
	 */
	@Test
	public void applyMany() {
		assertSuccess("1=1 ∨ 2=2 ∨ 3=3 ∨ 4=4",
				disjGoal(disjGoal(disjGoal(empty))));
	}

	/**
	 * Ensures that DisjGoalTac fails when the goal is not a disjunction.
	 */
	@Test
	public void failure() {
		assertFailure("1=1⇒2=2");
		assertFailure("(1=1∨2=2)∧(3=3∨4=4)");
		assertFailure("¬(1=1∨2=2)");
	}

	/**
	 * Assert that the application of the DisjGoalTac on a node made up of the
	 * given goal succeeds and produces the given tree shape.
	 * 
	 * @param goalStr
	 *            the considered goal
	 * @param shape
	 *            the expected tree shape
	 */
	private static void assertSuccess(final String goalStr, final TreeShape shape) {
		TacticTestUtils.assertSuccess(genSeq("|-" + goalStr), shape, TAC);
	}

	/**
	 * Assert that the application of the DisjGoalTac on a node made up of the
	 * given goal fails and does not modify the proof tree.
	 * 
	 * @param goalStr
	 *            the considered goal in String
	 */
	private static void assertFailure(final String goalStr) {
		TacticTestUtils.assertFailure(genSeq("|- " + goalStr), TAC);
	}

	/**
	 * Returns a Treeshape corresponding to one application of DisjGoalTac.
	 * 
	 * @param child
	 *            the TreeShape child
	 * @return a Treeshape corresponding to one application of DisjGoalTac
	 */
	private static TreeShape disjGoal(TreeShape child) {
		return dti("", impI(child));
	}

}
