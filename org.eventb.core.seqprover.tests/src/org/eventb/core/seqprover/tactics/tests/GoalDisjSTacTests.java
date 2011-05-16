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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impI;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 */
public class GoalDisjSTacTests {

	private static final ITactic tac = new AutoTactics.GoalDisjSTac();
	private static final String TAC_ID = "org.eventb.core.seqprover.GoalDisjSTac";

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		final String[] tacticIds = { TAC_ID };
		final ITactic[] tactics = { tac };
		TacticTestUtils.assertTacticsRegistered(tacticIds, tactics);
	}

	@Test
	public void succeed() {
		assertSucceed("1=1 ∨ 2=2", dti("", impI(empty)));
		assertSucceed("1=1 ∨ 2=2", dti("", impI(empty)));
		assertSucceed("1=1 ∨ 2=2 ∨ 3=3 ∨ 4=4",
				dti("", impI(dti("", impI(dti("", impI(empty)))))));
		assertSucceed("1=1 ∨ (2=2 ∧ 3=3)", dti("", impI(empty)));
	}

	@Test
	public void fails() {
		assertFails("1=1⇒2=2");
		assertFails("(1=1∨2=2)∧(3=3∨4=4)");
		assertFails("¬(1=1∨2=2)");
	}

	/**
	 * Assert that the application of the GoalDisjSTac on a node made up of one
	 * goal returns <code>null</code> and that the resulting tree shape is equal
	 * to the given <code>shape</code> tree shape.
	 * 
	 * @param goalStr
	 *            the considered goal
	 * @param shape
	 *            the expected tree shape
	 */
	private void assertSucceed(final String goalStr, final TreeShape shape) {
		final IProofTree pt = genProofTree(//
		goalStr// Goal
		);
		assertSuccess(pt.getRoot(), shape, tac);
	}

	/**
	 * Assert that the application of the GoalDisjSTac on a node made up of one
	 * goal does not return <code>null</code>.
	 * 
	 * @param predStr
	 *            the considered goal in String
	 */
	private void assertFails(final String predStr) {
		final IProofTree pt = genProofTree(predStr // goal
		);
		assertFailure(pt.getRoot(), tac);
	}

}
