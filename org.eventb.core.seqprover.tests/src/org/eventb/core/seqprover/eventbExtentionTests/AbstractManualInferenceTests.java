/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstractManualInference;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Manual Inference reasoner
 *         {@link AbstractManualInference}
 */
public abstract class AbstractManualInferenceTests extends AbstractManualReasonerTests {

	final boolean withErrorMessage;

	class SuccessfulTest {
		String sequenceImage;
		String hypothesisImage;
		String positionsImage;
		String [] results;
		public SuccessfulTest(String sequenceImage, String hypothesisImage,
				String positionImage, String... results) {
			this.sequenceImage = sequenceImage;
			this.hypothesisImage = hypothesisImage;
			this.positionsImage = positionImage;
			this.results = results;
		}
	}
	
	public AbstractManualInferenceTests() {
		super();
		withErrorMessage = true;
	}

	public AbstractManualInferenceTests(boolean withErrorMessage) {
		super();
		this.withErrorMessage = withErrorMessage;
	}
	
	public AbstractManualInferenceTests(FormulaFactory ff) {
		super(ff);
		withErrorMessage = true;
	}

	protected abstract SuccessfulTest[] getSuccessfulTests();

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest [] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			successfullReasonerApps.add(makeSuccessfullReasonerApplication(
					test.sequenceImage, test.hypothesisImage, test.positionsImage, test.results));

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}

	private SuccessfullReasonerApplication makeSuccessfullReasonerApplication(
			String sequenceImage, String hypothesisImage, String positionImage,
			String [] results) {
		Predicate predicate = null;
		if (hypothesisImage != null) {
			predicate = TestLib.genPred(hypothesisImage);
			predicate.typeCheck(ff.makeTypeEnvironment());
		}

		IReasonerInput input = new AbstractManualInference.Input(predicate,
				makePosition(positionImage));
		return new SuccessfullReasonerApplication(TestLib
				.genSeq(sequenceImage, ff), input, results);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		assert unsuccessfulTests.length % 3 == 0;
		for (int i = 0; i < unsuccessfulTests.length; i += 3) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i], unsuccessfulTests[i + 1],
					unsuccessfulTests[i + 2]));
		}
		
		unsuccessfullReasonerApps.addAll(makeHypNotPresent());
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String [] getUnsuccessfulTests();
	
	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String sequentImage, String predicateImage, String positionImage) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		Predicate predicate = null;
		if (predicateImage != null) {
			predicate = TestLib.genPred(predicateImage);
			predicate.typeCheck(ff.makeTypeEnvironment());
		}
		IPosition position = makePosition(positionImage);
		IReasonerInput input = new AbstractManualInference.Input(predicate, position);
		
		IProverSequent sequent = TestLib.genSeq(sequentImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		if (withErrorMessage) {
			unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
					sequent, input, "Inference " + getReasonerID()
							+ " is not applicable for "
							+ (predicate == null ? sequent.goal() : predicate)
							+ " at position " + position));
		}
		return unsuccessfullReasonerApps;
	}

	protected Collection<UnsuccessfullReasonerApplication> makeHypNotPresent() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();
		IProverSequent sequent = TestLib.genSeq(" ⊤ |- ⊤ ");
		Predicate pred = TestLib.genPred("⊥");
		IPosition position = IPosition.ROOT;
		IReasonerInput input = new AbstractManualInference.Input(pred, position);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		if (withErrorMessage) {
			unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
					sequent, input, "Inference " + getReasonerID()
							+ " is not applicable for "
							+ (pred == null ? sequent.goal() : pred)
							+ " at position " + position));
		}
		return unsuccessfullReasonerApps;
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
