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

import static org.eventb.core.seqprover.tactics.tests.TreeShape.dti;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impI;

import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 * 
 *         Units tests for the auto-tactic DisjGoalTac
 */
public class DisjGoalTacTests extends AbstractTacticTests {

	public DisjGoalTacTests() {
		super(new AutoTactics.DisjGoalTac(),
				"org.eventb.core.seqprover.DisjGoalTac");
	}

	/**
	 * Ensures that DisjGoalTac succeeds once.
	 */
	@Test
	public void applyOnce() {
		assertSuccess(" ;H; ;S; |- 1=1 ∨ 2=2", disjGoal(empty));
	}

	/**
	 * Ensures that DisjGoalTac succeeds several times.
	 */
	@Test
	public void applyMany() {
		assertSuccess(" ;H; ;S; |- 1=1 ∨ 2=2 ∨ 3=3 ∨ 4=4",
				disjGoal(disjGoal(disjGoal(empty))));
	}

	/**
	 * Ensures that DisjGoalTac fails when the goal is not a disjunction.
	 */
	@Test
	public void failure() {
		assertFailure(" ;H; ;S; |-1=1⇒2=2");
		assertFailure(" ;H; ;S; |-(1=1∨2=2)∧(3=3∨4=4)");
		assertFailure(" ;H; ;S; |-¬(1=1∨2=2)");
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
