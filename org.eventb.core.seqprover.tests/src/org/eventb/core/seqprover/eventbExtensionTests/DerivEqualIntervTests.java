/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.DerivEqualInterv;
import org.junit.Test;

/**
 * Tests for the "equality of intervals" reasoner.
 *
 * @author Guillaume Verdier
 */
public class DerivEqualIntervTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.derivEqualInterv";
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("a‥b = c‥d |- ⊤", input("a‥b = c‥d"), //
				"{}[][][a‥b = c‥d] |- a ≤ b", //
				"{}[a‥b = c‥d][][a = c ;; b = d] |- ⊤");
	}

	@Test
	public void failure() throws Exception {
		// Only applicable to whole hypothesis
		assertReasonerFailure("a‥b = c‥d ∧ a ≠ 0 |- ⊤", input("a‥b = c‥d ∧ a ≠ 0"),
				"Invalid input hypothesis: a ‥ b=c ‥ d∧a≠0");
		// Not a hypothesis
		assertReasonerFailure("|- ⊤", input("a‥b = c‥d"), "Nonexistent hypothesis: a ‥ b=c ‥ d");
	}

	private IReasonerInput input(String pred) {
		return new DerivEqualInterv.Input(genPred(pred));
	}

}
