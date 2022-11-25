/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
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

	private static final SuccessfulTest[] NO_SUCCESSFUL_TESTS = new SuccessfulTest[0];
	
	protected SuccessfulTest[] getSuccessfulTests() {
		return NO_SUCCESSFUL_TESTS;
	}

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
				.genFullSeq(sequenceImage), input, results));
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

	private static final String[] NO_UNSUCCESSFUL_TESTS = new String[0];

	protected String [] getUnsuccessfulTests() {
		return NO_UNSUCCESSFUL_TESTS;
	}
	
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

	private static final EmptyInput NO_INPUT = new EmptyInput();

	protected void assertReasonerSuccess(String sequentImage, String... newSequents) throws UntranslatableException {
		assertReasonerSuccess(sequentImage, NO_INPUT, newSequents);
	}

	protected void assertReasonerFailure(String sequentImage) throws UntranslatableException {
		assertReasonerFailure(sequentImage, NO_INPUT, "Finite hyp is not applicable");
	}

}
