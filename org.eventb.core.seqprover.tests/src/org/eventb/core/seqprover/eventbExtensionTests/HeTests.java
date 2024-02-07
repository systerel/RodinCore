/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
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

import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L0;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;
import org.junit.Test;

/**
 * Unit tests for the He reasoner
 *
 * The He and Eh reasoners share the same implementation. This class only has a
 * few integration tests for He. Most tests should be added to {@link EhTests}.
 * 
 * @author htson
 */
public class HeTests extends AbstractReasonerTests {

	private final Level level;

	protected HeTests(Level level) {
		this.level = level;
	}

	public HeTests() {
		this.level = L0;
	}

	@Override
	public String getReasonerID() {
		switch (level) {
		case L2:
			return "org.eventb.core.seqprover.heL2";
		case L1:
			return "org.eventb.core.seqprover.heL1";
		case L0:
			return "org.eventb.core.seqprover.he";
		default:
			fail("unknown reasoner level");
			return null;
		}
	}

	@Test
	public void testSuccess() throws UntranslatableException {
		assertReasonerSuccess("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3 ", makeInput("0 = 1"),
				"{}[][0+1=2][0=1 ;; 0+0=2] |- 0+0+0=3");
		assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
				"{}[][][x = y + 1] |- x + 1 = 1 + x");
	}

	@Test
	public void testFailure() throws UntranslatableException {
		// Hyp is not equality
		assertReasonerFailure("1 = 2 ⇒ 2 = 3 |- ⊤", makeInput("1 = 2 ⇒ 2 = 3"), "Unsupported hypothesis: 1=2⇒2=3");
		// Nothing to do
		assertReasonerFailure("0 = 1 ;; ⊤ |- ⊤", makeInput("0 = 1"), "Nothing to rewrite");
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = TestLib.genPred(predImage);
		return new HypothesisReasoner.Input(pred);
	}

}
