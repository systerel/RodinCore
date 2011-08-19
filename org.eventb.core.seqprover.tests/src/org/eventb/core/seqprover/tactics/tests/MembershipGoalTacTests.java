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

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 */
public class MembershipGoalTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.mbGoalTac";
	private static final ITactic TAC = new AutoTactics.MembershipGoalAutoTac();

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		assertTacticRegistered(TAC_ID, TAC);
	}

}
