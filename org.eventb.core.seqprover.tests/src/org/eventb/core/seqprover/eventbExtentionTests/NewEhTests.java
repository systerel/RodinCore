/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added mathematical extension related tests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerExtentionTests.ExtendedOperators.AssocExt;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;

/**
 * Unit tests for the Eq reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class NewEhTests extends AbstractReasonerTests {

	private static final FormulaFactory FF_WITH_ASSOC = FormulaFactory.getInstance(AssocExt.getInstance());
	
	public NewEhTests() {
		super(FF_WITH_ASSOC);
	}
	
	@Override
	public String getReasonerID() {
		return (new Eq()).getReasonerID();
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1=2 ;; 1+1 = 2 |- 1+1+1 = 3 "),
						new HypothesisReasoner.Input(TestLib.genPred("1=2")),
						"{}[][1+1=2][1=2;; 2+2=2] |- 2+2+2=3"),
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1+2 = 12 |- 0+1+2+3 = 123 "),
						new HypothesisReasoner.Input(TestLib.genPred("1+2=12")),
						"{}[][][1+2=12] |- 0+12+3=123"),
				new SuccessfullReasonerApplication(
						// bug 3389537
						TestLib.genSeq(
								" 1●2 = 12 ;; 1●2 + 1●2 = 2 |- 1 + 1●2 + 1 = 1●2 ",
								FF_WITH_ASSOC), new HypothesisReasoner.Input(
								TestLib.genPred("1●2 = 12", FF_WITH_ASSOC)),
						"{}[][1●2 + 1●2 = 2][1●2 = 12;; 12+12=2] |- 1+12+1=12"),
				new SuccessfullReasonerApplication(
						// bug 3389537
						TestLib.genSeq(
								" 1●2 = 12 ;; 0●1●2●3 = 123 |- 0 = 1 + 1●2●3 + 1",
								FF_WITH_ASSOC), new HypothesisReasoner.Input(
								TestLib.genPred("1●2 = 12", FF_WITH_ASSOC)),
						"{}[][0●1●2●3 = 123][1●2=12;; 0●12●3 = 123] |- 0 = 1 + 12●3 + 1"),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// eqHyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), new HypothesisReasoner.Input(TestLib.genPred("1=2"))),
				// eqHyp not an equality
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new HypothesisReasoner.Input(TestLib.genPred("⊤"))),
				// nothing to do
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" 1=2 ;; ⊤ |- ⊤ "), new HypothesisReasoner.Input(TestLib.genPred("1=2"))),
				// nothing to do
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" 1=2 ;; 1=1 ;; 2=2 |- ⊤ "), new HypothesisReasoner.Input(TestLib.genPred("1=2")))
		};
	}

	// Commented out, but makes the tests successful
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
