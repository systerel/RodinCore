package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Empty Input reasoner
 *         {@link EmptyInputReasoner}
 */
public abstract class AbstractEmptyInputReasonerTests extends
		AbstractManualReasonerTests {

	class SuccessfulTest {
		String sequenceImage;
		String [] results;
		public SuccessfulTest(String sequenceImage, String... results) {
			this.sequenceImage = sequenceImage;
			this.results = results;
		}
	}
	
	protected abstract SuccessfulTest[] getSuccessfulTests();

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest[] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			Collection<SuccessfullReasonerApplication> apps = makeSuccessfullReasonerApplication(
					test.sequenceImage, test.results);
			successfullReasonerApps.addAll(apps);

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}

	protected Collection<SuccessfullReasonerApplication> makeSuccessfullReasonerApplication(
			String sequenceImage, String [] results) {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();

		IReasonerInput input = new EmptyInput();
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
		
		for (int i = 0; i < unsuccessfulTests.length; ++i) {
			unsuccessfullReasonerApps.addAll(makeUnsuccessfulApplication(
					unsuccessfulTests[i]));
		}
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String[] getUnsuccessfulTests();

	protected Collection<UnsuccessfullReasonerApplication> makeUnsuccessfulApplication(
			String sequentImage) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		IReasonerInput input = new EmptyInput();

		IProverSequent sequent = TestLib.genSeq(sequentImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Inference " + getReasonerID()
				+ " is not applicable"));

		return unsuccessfullReasonerApps;
	}

}
