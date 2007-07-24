package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;

//import com.b4free.rodin.core.B4freeCore;

public class CutTests extends AbstractReasonerTests {

//	private static final IReasonerInput input = new SinglePredInput(TestLib.genPred("3∈P"));
//	private static final IReasonerInput input_x_WD = new SinglePredInput(TestLib.genPred("x∈P"));
//	private static final IReasonerInput input_x_NotWD = new SinglePredInput(TestLib.genPred("x÷x∈P"));

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
						"[{P=ℙ(ℤ)}[][][1∈P] |- ⊤," +
						" {P=ℙ(ℤ)}[][][1∈P] |- 3∈P," +
						" {P=ℙ(ℤ)}[][][1∈P, 3∈P] |- 2∈P]"),
				// With WD
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" 1∈P |- 2∈P "),
						new SinglePredInput(TestLib.genPred("0÷0∈P")),
						"[{P=ℙ(ℤ)}[][][1∈P] |- 0≠0," +
						" {P=ℙ(ℤ)}[][][1∈P, 0≠0] |- 0 ÷ 0∈P," +
						" {P=ℙ(ℤ)}[][][1∈P, 0≠0, 0 ÷ 0∈P] |- 2∈P]")
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

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
