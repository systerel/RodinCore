package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.core.seqprover.tests.Util;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;

/**
 * Abstract unit tests for the manual rewriter
 * 
 * @author htson
 */
public abstract class AbstractManualRewriterTests extends AbstractReasonerTests {

	protected FormulaFactory ff = FormulaFactory.getDefault();

	protected Collection<SuccessfullReasonerApplication> makeSuccessfullReasonerApplication(
			String predicateImage, String positionImage, String result) {
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
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- " + predicate), input,
				"[" + inferredEnvironment + "[][][⊤] |- "+ result + "]"));

		// Successful in hypothesis
		input = new AbstractManualRewrites.Input(predicate, ff
				.makePosition(positionImage));

		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(predicate + " |- ⊤"), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(predicate + " |- ⊤"), input,
				"[" + inferredEnvironment
				+ "[" + predicate + "][][" + result + "] |- ⊤]"));
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
		IReasonerInput input = new AbstractManualRewrites.Input(null, ff
				.makePosition(positionImage));

		IProverSequent sequent = TestLib.genSeq(" ⊤ |- " + predicateImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for goal " + predicate));

		sequent = TestLib.genSeq(predicateImage + " |- ⊤");
		input = new AbstractManualRewrites.Input(predicate, ff
				.makePosition(positionImage));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for hypothesis " + predicate));

		return unsuccessfullReasonerApps;
	}


	protected void testGetPosition(String predicateImage, String expected) {
		Predicate predicate = TestLib.genPred(predicateImage);
		List<IPosition> positions = getPositions(predicate);
		assertPositions("Position found for " + predicateImage, expected,
				positions);
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		String [] successfulTests = getSuccessfulTests();
		assert successfulTests.length % 3 == 0;
		for (int i = 0; i < successfulTests.length;i += 3) {
			Collection<SuccessfullReasonerApplication> apps = makeSuccessfullReasonerApplication(
					successfulTests[i], successfulTests[i+1], successfulTests[i+2]);
			successfullReasonerApps.addAll(apps);

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}
	
	protected abstract String[] getSuccessfulTests();

	protected abstract List<IPosition> getPositions(Predicate predicate);

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

	protected void assertPositions(String message, String expected,
			List<IPosition> positions) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IPosition position : positions) {
			if (sep)
				builder.append('\n');
			builder.append(position);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
