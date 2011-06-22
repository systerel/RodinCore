/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.FunImgInclusionGoal;

/**
 * Unit tests for the reasoner FunImgInclusionGoal
 * 
 * @author Emmanuel Billaud
 */
public class FunImgInclusionGoalTests extends AbstractReasonerTests {

	private static final IReasonerInput EmptyInput = new EmptyInput();

	@Override
	public String getReasonerID() {
		return new FunImgInclusionGoal().getReasonerID();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[] {
				// Works with SUBSETEQ
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆C ;; f∈A↔B ;; f(x)∈B |- f(x)∈C "),
						EmptyInput),
				// Works with SUBSET
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊂C ;; f∈A↔B ;; f(x)∈B |- f(x)∈C "),
						EmptyInput) };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Goal is not an inclusion
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- x = 2"), EmptyInput,
						"Goal is not an inclusion"),
				// Left member is not a function application
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- 2∈P "), EmptyInput,
						"Left member is not a function application"),
				// It misses f(x)∈B
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆C ;; f∈A↔B |- f(x)∈C "),
						EmptyInput,
						"Cannot find hypothesis discharging the goal"),
				// It misses g(x)∈B (not explicitely written
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆C ;; f∈A↔B ;; g∈A↔B ;; g=f ;; f(x)∈B |-  g(x)∈C "),
						EmptyInput,
						"Cannot find hypothesis discharging the goal"),
				// It misses B⊆C or B⊂C
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; f∈A↔B ;; f(x)∈B |- f(x)∈C "),
						EmptyInput,
						"Cannot find hypothesis discharging the goal"),
				// It does not discharge in the case B=C (an other reasoner do
				// this)
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;;  B=C ;; f∈A↔B ;; f(x)∈B |- f(x)∈C "),
						EmptyInput,
						"Cannot find hypothesis discharging the goal"),
				// It does not discharge in the case hypothesis is in goal
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; f∈A↔A ;; f(x)∈A  |-  f(x)∈A "),
						EmptyInput,
						"Cannot find hypothesis discharging the goal"), };
	}

}
