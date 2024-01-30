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

import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L0;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;
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
		assertReasonerSuccess("1 = 0 + 1 ;; 0+1 = 2 |- 2+0+1 = 3 ", makeInput("1 = 0 + 1"),
				"{}[][0+1=2][1=0+1 ;; 1=2] |- 2+1=3");
		assertReasonerSuccess("2 + 1 = 0 + 1 + 2 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ", makeInput("2 + 1 = 0 + 1 + 2"),
				"{}[][0+1=0+1+2][2+1=0+1+2 ;; 0+1=2+1] |- 2+0+1 = 2+1+3");
		// FR #371
		if (level.from(L1)) {
			// Hypothesis is hidden when rewriting an identifier
			assertReasonerSuccess("y + 1 = x |- x + 1 = 1 + y + 1", makeInput("y + 1 = x"),
					"{}[y + 1 = x][][] |- y + 1 + 1 = 1 + y + 1");
		} else {
			assertReasonerSuccess("y + 1 = x |- x + 1 = 1 + y + 1", makeInput("y + 1 = x"),
					"{}[][][y + 1 = x] |- y + 1 + 1 = 1 + y + 1");
		}
		// Bug #818
		if (level.from(L2)) {
			// Hypothesis is deselected (not hidden) when rewriting an identifier that is
			// used in deselected hypotheses
			assertReasonerSuccess(genFullSeq("y + 1 = x ;; x > 0 ;H; ;S; y + 1 = x |- x + 1 = 1 + y + 1", ff),
					makeInput("y + 1 = x"), "{}[][y + 1 = x ;; x > 0][] |- y + 1 + 1 = 1 + y + 1");
		} else if (level.from(L1)) {
			assertReasonerSuccess(genFullSeq("y + 1 = x ;; x > 0 ;H; ;S; y + 1 = x |- x + 1 = 1 + y + 1", ff),
					makeInput("y + 1 = x"), "{}[y + 1 = x][x > 0][] |- y + 1 + 1 = 1 + y + 1");
		} else {
			assertReasonerSuccess(genFullSeq("y + 1 = x ;; x > 0 ;H; ;S; y + 1 = x |- x + 1 = 1 + y + 1", ff),
					makeInput("y + 1 = x"), "{}[][x > 0][y + 1 = x] |- y + 1 + 1 = 1 + y + 1");
		}
		// Behavior is not modified when rewriting a complex expression
		assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
				"{}[][][x = y + 1] |- x + 1 = 1 + x");
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
