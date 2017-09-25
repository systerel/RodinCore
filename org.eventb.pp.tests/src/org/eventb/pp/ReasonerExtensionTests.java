/*******************************************************************************
 * Copyright (c) 2007, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.pp.PPInput;
import org.junit.Test;

//import com.b4free.rodin.core.B4freeCore;

public class ReasonerExtensionTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new PPInput(false,3000,1000);

	@Override
	public String getReasonerID() {
		return "org.eventb.pp.pp";
	}

	@Test
	public void testPPSuccessHyp() throws Exception {
		testSuccessfulReasonerApplications("Success expected",
				new SuccessfullReasonerApplication(TestLib.genSeq(" x = 1 |- x = 1 "), input),
				new SuccessfullReasonerApplication(TestLib.genSeq(" 1∈P |- 1∈P "), input));
	}

	@Test
	public void testPPFailure() throws Exception {
		testUnsuccessfulReasonerApplications("Failure expected",
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" x = 1 |- x = 2"), input, "Failed"),
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" 1∈P |- 2∈P "), input, "Failed"));
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
