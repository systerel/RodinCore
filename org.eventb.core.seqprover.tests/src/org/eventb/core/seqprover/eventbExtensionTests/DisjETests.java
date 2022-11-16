/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Université de Lorraine - updated to use AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.junit.Test;

/**
 * Unit tests for the DisjE reasoner
 * 
 * @author Farhad Mehta
 * @author Guillaume Verdier
 */
public class DisjETests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.disjE";
	}

	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void failure() throws Exception {
		assertReasonerFailure("⊤ |- ⊤", makeInput("1=1∨2=2"), "Nonexistent hypothesis: 1=1∨2=2");
		assertReasonerFailure("⊤ |- ⊤", makeInput("⊤"), "Hypothesis is not a disjunction: ⊤");
	}

	/**
	 * Tests for reasoner success
	 */
	@Test
	public void success() throws Exception {
		assertReasonerSuccess("1=1∨2=2  |- ⊤", makeInput("1=1∨2=2"), "{}[][1=1∨2=2][1=1] |- ⊤",
				"{}[][1=1∨2=2][2=2] |- ⊤");
	}

	private IReasonerInput makeInput(String input) {
		return new HypothesisReasoner.Input(genPred(input));
	}

}
