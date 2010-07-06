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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.eventbExtensions.Lib;
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
		final List<SuccessfullReasonerApplication> apps = new ArrayList<SuccessfullReasonerApplication>();		
		final ITypeEnvironment typenv = Lib.makeTypeEnvironment();
		final Predicate predicate = TestLib.genPred(typenv, predicateImage);

		// Successful in goal
		IReasonerInput input = new AbstractManualRewrites.Input(null, ff
				.makePosition(positionImage));
		final IProverSequent[] expecteds = new IProverSequent[results.length];
		for (int i = 0; i < expecteds.length; i++) {
			expecteds[i] = TestLib.genFullSeq(typenv, "", "", "⊤", results[i]);
		}
		apps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- " + predicate), input, expecteds));

		// Successful in hypothesis
		input = new AbstractManualRewrites.Input(predicate, ff
				.makePosition(positionImage));
		
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String result : results) {
			sb.append(sep);
			sb.append(result);
			sep = " ;; ";
		}
		final IProverSequent expected = TestLib.genFullSeq(typenv, predicate
				.toString(), "", sb.toString(), "⊤");
		apps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(predicate + " |- ⊤"), input, expected));
		return apps;
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
