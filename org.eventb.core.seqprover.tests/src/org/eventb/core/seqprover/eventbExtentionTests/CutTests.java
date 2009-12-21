/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;

//import org.eventb.core.seqprover.ITactic;
//import com.b4free.rodin.core.B4freeCore;

public class CutTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cut";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Without WD
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- 2∈P "),
						new SinglePredInput(TestLib.genPred("3∈P")),
						"{P=ℙ(ℤ)}[][][1∈P] |- ⊤",
						"{P=ℙ(ℤ)}[][][1∈P] |- 3∈P",
						"{P=ℙ(ℤ)}[][][1∈P;; 3∈P] |- 2∈P"),
				// With WD
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- 2∈P "),
						new SinglePredInput(TestLib.genPred("0÷0∈P")),
						"{P=ℙ(ℤ)}[][][1∈P] |- 0≠0",
						"{P=ℙ(ℤ)}[][][1∈P;; 0≠0] |- 0 ÷ 0∈P",
						"{P=ℙ(ℤ)}[][][1∈P;; 0≠0;; 0 ÷ 0∈P] |- 2∈P"),
				// With WD and variable
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- x∈P "),
						new SinglePredInput(TestLib.genPred("x÷x∈P")),
						"{P=ℙ(ℤ), x=ℤ}[][][1∈P] |- x≠0",
						"{P=ℙ(ℤ), x=ℤ}[][][1∈P;; x≠0] |- x ÷ x∈P",
						"{P=ℙ(ℤ), x=ℤ}[][][1∈P;; x≠0;; x ÷ x∈P] |- x∈P")
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Input cannot be parsed
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" 1∈P |- 2∈P "), 
						new SinglePredInput("#unparsable#",TestLib.genTypeEnv("")),
						"Parse error for predicate: #unparsable#"),
				// Input cannot be type-checked
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" 1∈P |- 2∈P "), 
						new SinglePredInput("y∈P",TestLib.genTypeEnv("")),
						"Type check failed for Predicate: y∈P")
//				The test framework cannot test the following type of failure
//				// Rule can be generated, but cannot be applied (extra free variable in input predicate)
//				new UnsuccessfullReasonerApplication(
//						TestLib.genSeq(" 1∈P |- 2∈P "),
//						new SinglePredInput(TestLib.genPred("(x+1)∈P")),
//						"[]"),	
				};
	}

// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
