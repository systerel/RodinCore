/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;
import org.junit.Test;

/**
 * Unit tests for the Eq reasoner
 * 
 * @author htson
 */
public class EhTests extends AbstractTests {

	private static final IReasoner ehReasoner = new Eq();

	Predicate P1 = TestLib.genPred("0 = 1");

	Predicate P2 = TestLib.genPred("0 + 1 = 1");

	Predicate P3 = TestLib.genPred("0 + 1 + 2 = 2 + 1");

	@Test
	public void testHypIsNotWellForm() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not equality
		seq = TestLib.genSeq(" 1 = 2 ⇒ 2 = 3 |- ⊤ ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(TestLib
				.genPred("1 = 2 ⇒ 2 = 3")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testNothingToDo() {
		IProverSequent seq;
		IReasonerOutput output;

		seq = TestLib.genSeq(P1 + " ;; ⊤ |- ⊤ ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(P1), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testHypNotPresent() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(P1), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for reasoner success
	 */
	@Test
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(P1 + " ;; 0+1 = 2 |- 1+0+1 = 3 ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(P1), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P1 ",
				"{}[][0+1=2][0=1, 1+1=2] |- 1+1+1=3", newSeqs);

		seq = TestLib.genSeq(P2 + " ;; 0+1 = 2 |- 2+0+1 = 3 ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(P2), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P2 ",
				"{}[][0+1=2][0+1=1, 1=2] |- 2+1=3", newSeqs);

		seq = TestLib.genSeq(P3 + " ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ");
		output = ehReasoner.apply(seq, new HypothesisReasoner.Input(P3), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully equality P2 ",
				"{}[][0+1=0+1+2][0+1+2=2+1, 0+1=2+1] |- 2+0+1=2+1+3", newSeqs);
	}

}
