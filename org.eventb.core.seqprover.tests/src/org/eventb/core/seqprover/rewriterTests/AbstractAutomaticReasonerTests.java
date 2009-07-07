package org.eventb.core.seqprover.rewriterTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Automatic rewriter reasoner
 *         {@link AbstractAutoRewrites}
 */
public abstract class AbstractAutomaticReasonerTests extends AbstractReasonerTests {

	public class SuccessfulTest {
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
		SuccessfulTest [] successfulTests = getSuccessfulTests();
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
			String sequenceImage, String[] results) {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();

		IReasonerInput input = new EmptyInput();
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genFullSeq(sequenceImage), input));
		
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
				.genFullSeq(sequenceImage), input, buffer.toString()));
		return successfullReasonerApps;
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		for (int i = 0; i < unsuccessfulTests.length; ++i) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i]));
		}
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String [] getUnsuccessfulTests();
	
	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String sequentImage) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		IReasonerInput input = new EmptyInput();
		
		IProverSequent sequent = TestLib.genFullSeq(sequentImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input,  "No rewrites applicable"));
		return unsuccessfullReasonerApps;
	}

}
