package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;

//import com.b4free.rodin.core.B4freeCore;

/**
 * @author htson
 *         <p>
 *         Abstract unit tests for the Manual Rewrites reasoner
 *         {@link AbstractManualRewrites}
 * 
 */
public abstract class AbstractManualRewriterTests extends AbstractManualReasonerTests {

	protected static class SuccessfulTest {
		
		String predicateImage;
		String positionImage;
		String[] results;
		public SuccessfulTest(String predicateImage, String positionImage,
				String... results) {
			this.predicateImage = predicateImage;
			this.positionImage = positionImage;
			this.results = results;
		}
		
	}

	protected Collection<SuccessfullReasonerApplication> makeSuccessfullReasonerApplication(
			String predicateImage, String positionImage, String [] results) {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		
		Predicate predicate = TestLib.genPred(predicateImage);
		ITypeCheckResult typeCheck = predicate.typeCheck(ff.makeTypeEnvironment());
		ITypeEnvironment inferredEnvironment = typeCheck
				.getInferredEnvironment();

		// Successful in goal
		IReasonerInput input = new AbstractManualRewrites.Input(null, ff
				.makePosition(positionImage));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- " + predicate), input));
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		boolean first = true;
		for (String result : results) {
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(inferredEnvironment + "[][][⊤] |- "+ result);
		}
		buffer.append("]");
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- " + predicate), input, buffer.toString()));

		// Successful in hypothesis
		input = new AbstractManualRewrites.Input(predicate, ff
				.makePosition(positionImage));

		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(predicate + " |- ⊤"), input));
		
		buffer = new StringBuffer();
		buffer.append("[" + inferredEnvironment
				+ "[" + predicate + "][][");
		first = true;
		for (String result : results) {
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(result);
		}
		buffer.append("] |- ⊤]");
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(predicate + " |- ⊤"), input, buffer.toString()));
		return successfullReasonerApps;
	}
	

	protected Collection<UnsuccessfullReasonerApplication> makeHypNotPresent() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();
		IProverSequent sequent = TestLib.genSeq(" ⊤ |- ⊤ ");
		Predicate pred = TestLib.genPred("⊥");
		IReasonerInput input = new AbstractManualRewrites.Input(pred, ff
				.makePosition(""));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Nonexistent hypothesis: ⊥"));
		return unsuccessfullReasonerApps;
	}

	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String predicateImage, String positionImage) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		Predicate predicate = TestLib.genPred(predicateImage);
		predicate.typeCheck(ff.makeTypeEnvironment());
		IPosition position = ff
						.makePosition(positionImage);
		IReasonerInput input = new AbstractManualRewrites.Input(null, position);

		IProverSequent sequent = TestLib.genSeq(" ⊤ |- " + predicateImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for goal " + predicate
						+ " at position " + position));

		sequent = TestLib.genSeq(predicateImage + " |- ⊤");
		input = new AbstractManualRewrites.Input(predicate, position);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for hypothesis " + predicate
						+ " at position " + position));

		return unsuccessfullReasonerApps;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest [] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			Collection<SuccessfullReasonerApplication> apps = makeSuccessfullReasonerApplication(
					test.predicateImage, test.positionImage, test.results);
			successfullReasonerApps.addAll(apps);

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}
	
	protected abstract SuccessfulTest[] getSuccessfulTests();

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		assert unsuccessfulTests.length % 2 == 0;
		for (int i = 0; i < unsuccessfulTests.length; i += 2) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i], unsuccessfulTests[i+1]));
		}
		
		unsuccessfullReasonerApps.addAll(makeHypNotPresent());
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String[] getUnsuccessfulTests();

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}
	
}
