package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;

public class HypOrTests extends AbstractReasonerTests {

	private static final Predicate pred = Lib.parsePredicate("x = 1");
	
	private static final IReasonerInput input = new SinglePredInput(pred);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		pred.typeCheck(FormulaFactory.getDefault().makeTypeEnvironment());
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hypOr";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 1 ∨ x = 3 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 1 ∨ x = 3 "), input,
						"[]"),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 1 ∨ x = 2 ∨ x = 3 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 1 ∨ x = 2 ∨ x = 3 "), input,
						"[]"),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 3 ∨ x = 1 "), input),
				new SuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 3 ∨ x = 1 "), input,
						"[]")				
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 2 |- x = 2 ∨ x = 1 ∨ x = 3 "), input),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 2 |- x = 2 ∨ x = 1 ∨ x = 3 "), input,
						"Sequent does not contain predicate " + pred),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2"), input),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 "), input,
						"Goal is not a disjunctive predicate"),			
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 4 ∨ x = 3"), input),
				new UnsuccessfullReasonerApplication(TestLib
						.genSeq(" x = 1 |- x = 2 ∨ x = 4 ∨ x = 3 "), input,
						"Hypothesis with ∨ goal is not applicable for hypothesis "
								+ pred) 				
		};
	}

}
