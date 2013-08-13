/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add variations
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;

public class HypTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hyp";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final String typEnv = "A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;;";
		return new SuccessfullReasonerApplication[] {
				// Goal in hypotheses
				makeSuccess(" x = 1 |- x = 1 "),
				makeSuccess(" 1∈P |- 1∈P "),
				// A hypothesis equivalent to the goal
				makeSuccess(" 1 = x |- x = 1 "),
				makeSuccess(" 1 > x |- x < 1 "),
				makeSuccess(" 1 < x |- x > 1 "),
				makeSuccess(" 1 ≥ x |- x ≤ 1 "),
				makeSuccess(" 1 ≤ x |- x ≥ 1 "),
				// A hypothesis stronger than a positive Goal
				// H, P |- P†
				makeSuccess(" 1 = x |- 1 ≥ x "),
				makeSuccess(" 1 = x |- 1 ≤ x "),
				makeSuccess(" 1 > x |- 1 ≥ x "),
				makeSuccess(" 1 < x |- 1 ≤ x "),
				makeSuccess(typEnv + " A = B |- A ⊆ B "),
				makeSuccess(typEnv + " A = B |- B ⊆ A "),
				makeSuccess(typEnv + " A ⊂ B |- A ⊆ B "),
				// A hypothesis stronger than a negative Goal
				// H, nP† |- ¬P
				makeSuccess(" 1 < x |- ¬x = 1 "),
				makeSuccess(" 1 > x |- ¬x = 1 "),
				makeSuccess(typEnv + " A ⊂ B |- ¬A = B "),
				makeSuccess(typEnv + " B ⊂ A |- ¬A = B "),
				makeSuccess(typEnv + " ¬A ⊆ B |- ¬A = B "),
				makeSuccess(typEnv + " ¬B ⊆ A |- ¬A = B "),
				makeSuccess(typEnv + " B ⊂ A |- ¬A ⊆ B "),
				makeSuccess(typEnv + " B ⊂ A |- ¬A ⊂ B "),
				makeSuccess(typEnv + " B ⊆ A |- ¬A ⊂ B "),
				makeSuccess(typEnv + " ¬A ⊆ B |- ¬A ⊂ B "),
				makeSuccess(typEnv + " B = A |- ¬A ⊂ B "),
				makeSuccess(typEnv + " B = A |- ¬A ⊂ B "),

		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, input);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[] {
				// Sequent not normalized
				makeFailure(" 1 > x ;; ¬x ≥ 1 |- x = 1 "),

				// simple tests
				makeFailure(" x = 1 |- x = 2 "), //
				makeFailure(" 1∈P |- 2∈P "),
				makeFailure(" x > 1 ;; x < 1 ;; x ≥ 1 ;; x ≤ 1 |- x = 1 "),
				makeFailure(" 1 > x ;; 1 < x ;; 1 ≥ x ;; 1 ≤ x |- x = 1 "),
				makeFailure(" 1 > x ;; 1 < x ;; 1 ≥ x ;; 1 ≤ x |- x = 1 "),

		};
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, input,
				"Goal not in hypothesis");
	}

}
