/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.He;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class HeTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return He.REASONER_ID;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(
						genSeq("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3 "),
						makeInput("0 = 1"),
						"{}[][0+1=2][0=1 ;; 0+0=2] |- 0+0+0=3"),
				new SuccessfullReasonerApplication(
						genSeq("1 = 0 + 1 ;; 0+1 = 2 |- 2+0+1 = 3 "),
						makeInput("1 = 0 + 1"),
						"{}[][0+1=2][1=0+1 ;; 1=2] |- 2+1=3"),
				new SuccessfullReasonerApplication(
						genSeq("2 + 1 = 0 + 1 + 2 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 "),
						makeInput("2 + 1 = 0 + 1 + 2"),
						"{}[][0+1=0+1+2][2+1=0+1+2 ;; 0+1=2+1] |- 2+0+1 = 2+1+3"), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Hyp is not equality
				new UnsuccessfullReasonerApplication(
						genSeq("1 = 2 ⇒ 2 = 3 |- ⊤"),
						makeInput("1 = 2 ⇒ 2 = 3")),
				// Nothing to do
				new UnsuccessfullReasonerApplication(//
						genSeq("0 = 1 ;; ⊤ |- ⊤"),//
						makeInput("0 = 1")),
				// Hyp is not present
				new UnsuccessfullReasonerApplication(//
						genSeq(" ⊤ |- ⊤ "),//
						makeInput("0 = 1")),

		};
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = TestLib.genPred(predImage);
		return new HypothesisReasoner.Input(pred);
	}

}
