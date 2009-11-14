/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - corrected: hid original hyp and added notImpRight hypothesis
 *     Systerel - visibility: deselected notImpRight hypothesis
 *     Systerel - adapted tests to V2
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.ModusTollens;

/**
 * Unit tests for the Modus Tollens reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class ModusTollensTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new ModusTollens()).getReasonerID();
	}
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Basic test
				new SuccessfullReasonerApplication(
						TestLib.genSeq("  1∈P ⇒ 2∈P  |- 3∈P "),
						new HypothesisReasoner.Input(TestLib.genPred("1∈P ⇒ 2∈P")),
						"[{P=ℙ(ℤ)}[1∈P⇒2∈P][][] |- ¬2∈P," +
						" {P=ℙ(ℤ)}[1∈P⇒2∈P][][¬1∈P] |- 3∈P]"
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// hyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤ "), new HypothesisReasoner.Input(TestLib.genPred("1=1 ⇒ 2=2"))),
				// hyp not an implication
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new HypothesisReasoner.Input(TestLib.genPred("⊤"))),
		};
	}
	
}
