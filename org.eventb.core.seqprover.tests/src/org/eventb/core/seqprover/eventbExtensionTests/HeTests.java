/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - port to AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.He;
import org.junit.Test;

/**
 * Unit tests for the He reasoner
 * 
 * @author htson
 */
public class HeTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return He.REASONER_ID;
	}

	@Test
	public void testSuccess() throws UntranslatableException {
		assertReasonerSuccess("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3 ", makeInput("0 = 1"),
				"{}[][0+1=2][0=1 ;; 0+0=2] |- 0+0+0=3");
		assertReasonerSuccess("1 = 0 + 1 ;; 0+1 = 2 |- 2+0+1 = 3 ", makeInput("1 = 0 + 1"),
				"{}[][0+1=2][1=0+1 ;; 1=2] |- 2+1=3");
		assertReasonerSuccess("2 + 1 = 0 + 1 + 2 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ", makeInput("2 + 1 = 0 + 1 + 2"),
				"{}[][0+1=0+1+2][2+1=0+1+2 ;; 0+1=2+1] |- 2+0+1 = 2+1+3");
	}

	@Test
	public void testFailure() throws UntranslatableException {
		// Hyp is not equality
		assertReasonerFailure("1 = 2 ⇒ 2 = 3 |- ⊤", makeInput("1 = 2 ⇒ 2 = 3"), "Unsupported hypothesis: 1=2⇒2=3");
		// Nothing to do
		assertReasonerFailure("0 = 1 ;; ⊤ |- ⊤", makeInput("0 = 1"), "Nothing to rewrite");
		// Hyp is not present
		assertReasonerFailure(" ⊤ |- ⊤ ", makeInput("0 = 1"), "Nonexistent hypothesis: 0=1");
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = TestLib.genPred(predImage);
		return new HypothesisReasoner.Input(pred);
	}

}
