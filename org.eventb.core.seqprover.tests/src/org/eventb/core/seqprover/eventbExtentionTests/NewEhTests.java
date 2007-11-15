package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;

/**
 * Unit tests for the Eq reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class NewEhTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new Eq()).getReasonerID();
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1=2 ;; 1+1 = 2 |- 1+1+1 = 3 "),
						new SinglePredInput(TestLib.genPred("1=2")),
						"[{}[][1+1=2][1=2, 2+2=2] |- 2+2+2=3]"
						)
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// eqHyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), new SinglePredInput(TestLib.genPred("1=2"))),
				// eqHyp not an equality
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new SinglePredInput(TestLib.genPred("⊤"))),
				// nothing to do
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" 1=2 ;; ⊤ |- ⊤ "), new SinglePredInput(TestLib.genPred("1=2"))),
				// nothing to do
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" 1=2 ;; 1=1 ;; 2=2 |- ⊤ "), new SinglePredInput(TestLib.genPred("1=2")))
		};
	}

	// Commented out, but makes the tests successful
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
