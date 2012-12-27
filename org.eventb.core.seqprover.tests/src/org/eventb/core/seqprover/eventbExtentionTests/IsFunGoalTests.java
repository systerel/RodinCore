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

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;

//import org.eventb.core.seqprover.ITactic;
//import com.b4free.rodin.core.B4freeCore;

/**
 * Unit tests for the {@link IsFunGoal} reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class IsFunGoalTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new IsFunGoal()).getReasonerID();
	}

	final static IReasonerInput input = new EmptyInput();
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// Something simple
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f ∈ ℕ → ℕ |- f ∈ ℤ ⇸ ℤ"),
						input),
				// Something more complicated
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f ∈ ( ℕ ↠ ℕ ) → ( ℕ ↔ ℕ ) |- f ∈ ℙ ( ℤ × ℤ ) ⇸ ℙ ( ℤ × ℤ )"),
						input),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// Goal not an inclusion
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), input),
				// Goal not an inclusion partial function
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- f ∈ ℕ → ℕ "), input),
				// Domain of inclusion function not a type expression
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- f ∈ ℕ ⇸ ℤ "), input),
				// Range of inclusion function not a type expression
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- f ∈ ℤ ⇸ ℕ "), input),
				// No appropriate hypothesis
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- f ∈ ℤ ⇸ ℤ "), input),
				// No appropriate hypothesis
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" f ∈ ℤ ↔ ℤ |- f ∈ ℤ ⇸ ℤ "), input)
		};
	}
	
//  Comitted out, but make tests succeed	
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
