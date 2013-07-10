/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;

//import com.b4free.rodin.core.B4freeCore;

public class HypTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hyp";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				makeSuccess(" x = 1 |- x = 1 "),
				makeSuccess(" 1∈P |- 1∈P "),
		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, input);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				makeFailure(" x = 1 |- x = 2 "),
				makeFailure(" 1∈P |- 2∈P "),
		};
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, input,
				"Goal not in hypotheses");
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
