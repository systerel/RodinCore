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
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
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

	private static final SuccessfulTest[] NO_SUCCESSFUL_TESTS = new SuccessfulTest[0];
	
	protected SuccessfulTest[] getSuccessfulTests() {
		return NO_SUCCESSFUL_TESTS;
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest[] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			successfullReasonerApps.add(makeSuccessfullReasonerApplication(
					test.sequenceImage, test.results));

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}

	private SuccessfullReasonerApplication makeSuccessfullReasonerApplication(
			String sequenceImage, String[] results) {
		IReasonerInput input = new EmptyInput();
		return new SuccessfullReasonerApplication(
				TestLib.genSeq(sequenceImage), input, results);
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

	private static final String[] NO_UNSUCCESSFUL_TESTS = new String[0];

	protected String[] getUnsuccessfulTests() {
		return NO_UNSUCCESSFUL_TESTS;
	}

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

	public void assertReasonerSuccess(String sequentImage, String... newSequents) throws UntranslatableException {
		assertReasonerSuccess(sequentImage, new EmptyInput(), newSequents);
	}

	public void assertReasonerFailure(String sequentImage, String reason) throws UntranslatableException {
		assertReasonerFailure(sequentImage, new EmptyInput(), reason);
	}

}
