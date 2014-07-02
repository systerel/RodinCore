/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.EqvLR;

/**
 * Unit tests for the EqvLR reasoner
 * 
 * @author Josselin Dolhen
 */
public class EqvLRTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return EqvLR.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				//
				new SuccessfullReasonerApplication(
						genSeq(" ⊤ ⇔ ⊤∨⊥ ;; ⊤∨⊥ |- ⊤∧⊥"), //
						makeInput("⊤ ⇔ ⊤∨⊥"), //
						"{}[][⊤∨⊥][⊤ ⇔ ⊤∨⊥ ;; ⊤∨⊥∨⊥] |- (⊤∨⊥)∧⊥"),
				//
				new SuccessfullReasonerApplication(
						genSeq(" ⊤ ⇔ ⊤∨⊥ ;; ∃z·⊤∧z>0 |- ⊥"), //
						makeInput("⊤ ⇔ ⊤∨⊥"), //
						"{}[][∃z·⊤∧z>0][⊤ ⇔ ⊤∨⊥ ;; ∃z·(⊤∨⊥)∧z>0] |- ⊥"), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Null predicate
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ ⇔ ⊤∨⊥ ;; ⊤∧⊥ |- ⊤"),
						new HypothesisReasoner.Input(null),
						"Nonexistent hypothesis"),
				// Not a hypothesis
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤ ⇔ ⊤∨⊥ ;; ⊥ |- ⊤"), makeInput("⊥∨⊤⇔⊤∨⊥"),
						"Nonexistent hypothesis: ⊥∨⊤⇔⊤∨⊥"),
				// Not an equivalence
				new UnsuccessfullReasonerApplication(
						genSeq(" 1 = x ;; x > 0 |- ⊤"), makeInput("1=x"),
						"Unsupported hypothesis: 1=x"),
				// Nothing to do
				new UnsuccessfullReasonerApplication(
						genSeq(" ⊤∨⊥ ⇔ ⊤ ;; ⊤∧⊥ |- ⊤"), //
						makeInput("⊤∨⊥ ⇔ ⊤"), //
						"Nothing to rewrite"), };
	}

	private IProverSequent genSeq(String seqImage) {
		return TestLib.genSeq(seqImage, ff);
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = genPred(predImage, ff);
		return new HypothesisReasoner.Input(pred);
	}

}
