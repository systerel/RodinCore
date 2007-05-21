package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;

/**
 * Unit tests for the Eq reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class ImpETests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new ImpE()).getReasonerID();
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				new SuccessfullReasonerApplication(
						TestLib.genSeq("  1=1 ⇒ 2=2  |- ⊤ "),
						new HypothesisReasoner.Input(TestLib.genPred("1=1 ⇒ 2=2")),
						"[{}[][1=1⇒2=2][] |- 1=1, {}[][1=1⇒2=2][1=1, 2=2] |- ⊤]"
						)
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// hyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), new HypothesisReasoner.Input(TestLib.genPred("1=1 ⇒ 2=2"))),
				// hyp not an implication
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new HypothesisReasoner.Input(TestLib.genPred("⊤"))),
		};
	}

}
