package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Single Expression Input reasoner
 *         {@link SingleExprInputReasoner}
 */
public abstract class AbstractSingleExpressionInputReasonerTests extends AbstractManualReasonerTests {

	class SuccessfulTest {
		String sequenceImage;
		String hypothesisImage;
		String expressionImage;
		String [] results;
		public SuccessfulTest(String sequenceImage, String hypothesisImage,
				String expressionImage, String... results) {
			this.sequenceImage = sequenceImage;
			this.hypothesisImage = hypothesisImage;
			this.expressionImage = expressionImage;
			this.results = results;
		}
	}
	
	protected abstract SuccessfulTest[] getSuccessfulTests();

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest [] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			Collection<SuccessfullReasonerApplication> apps = makeSuccessfullReasonerApplication(
					test.sequenceImage, test.hypothesisImage, test.expressionImage, test.results);
			successfullReasonerApps.addAll(apps);

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}

	protected Collection<SuccessfullReasonerApplication> makeSuccessfullReasonerApplication(
			String sequenceImage, String hypothesisImage,
			String expressionImage, String[] results) {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();

		Predicate predicate = null;
		if (hypothesisImage != null) {
			predicate = TestLib.genPred(hypothesisImage);
			predicate.typeCheck(ff.makeTypeEnvironment());
		}

		IReasonerInput input = new SingleExprInput(Lib.parseExpression(expressionImage));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(sequenceImage), input));
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		boolean first = true;
		for (String result : results) {
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(result);
		}
		buffer.append("]");
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(sequenceImage), input, buffer.toString()));
		return successfullReasonerApps;
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		assert unsuccessfulTests.length % 4 == 0;
		for (int i = 0; i < unsuccessfulTests.length; i += 4) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i], unsuccessfulTests[i + 1],
					unsuccessfulTests[i + 2], unsuccessfulTests[i + 3]));
		}
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String [] getUnsuccessfulTests();
	
	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String sequentImage, String predicateImage, String expressionImage, String reason) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		Predicate predicate = null;
		if (predicateImage != null) {
			predicate = TestLib.genPred(predicateImage);
			predicate.typeCheck(ff.makeTypeEnvironment());
		}
		IReasonerInput input = new SingleExprInput(Lib
				.parseExpression(expressionImage));
		
		IProverSequent sequent = TestLib.genSeq(sequentImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, reason));

		return unsuccessfullReasonerApps;
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
