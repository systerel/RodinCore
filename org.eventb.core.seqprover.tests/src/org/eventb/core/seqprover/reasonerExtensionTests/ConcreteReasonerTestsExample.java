/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtensionTests;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.junit.Test;

/**
 * This class demonstrates how to extend {@link AbstractReasonerTests} to test a particular 
 * registered reasoner implementation (in this case, {@link TrueGoal}).
 * 
 * @author Farhad Mehta
 *
 */
public class ConcreteReasonerTestsExample extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.trueGoal";
	}
	
	@Override
	public ITactic getJustDischTactic() {
		return new AutoTactics.TrueGoalTac();
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess(" ⊤ |- ⊤ ", input);
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure(" ⊤ |- ⊥ ", input, "Goal is not a tautology");
	}

}
