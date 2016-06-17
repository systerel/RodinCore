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
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;

public class HypOrTests extends AbstractReasonerTests {

	private static final IReasonerInput input = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hypOr";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final String typEnv = "A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;;";
		return new SuccessfullReasonerApplication[] {
				// Goal disjunct in hypotheses
				makeSuccess(" x = 1 |- x = 2 ∨ x = 1 ∨ x = 3 "),
				makeSuccess(" x = 1 |- x = 1 ∨ x = 2 ∨ x = 3 "),
				makeSuccess(" x = 1 |- x = 2 ∨ x = 3 ∨ x = 1 "),
				// Equivalent goal disjunct in hypotheses
				makeSuccess(" x = 1 |- x = 2 ∨ 1 = x ∨ x = 3 "),
				makeSuccess(" 1 > x |- x = 2 ∨ x < 1 ∨ x = 3 "),
				makeSuccess(" 1 < x |- x = 2 ∨ x > 1 ∨ x = 3 "),
				makeSuccess(" 1 ≥ x |- x = 2 ∨ x ≤ 1 ∨ x = 3 "),
				makeSuccess(" 1 ≤ x |- x = 2 ∨ x ≥ 1 ∨ x = 3 "),
				// A hypothesis stronger than a positive goal disjunct
				makeSuccess(" 1 > x |- x = 2 ∨ 1 ≥ x ∨ x = 3 "),
				makeSuccess(" 1 < x |- x = 2 ∨ 1 ≤ x ∨ x = 3 "),
				makeSuccess(" 1 = x |- x = 2 ∨ 1 ≥ x ∨ x = 3 "),
				makeSuccess(" 1 = x |- x = 2 ∨ 1 ≤ x ∨ x = 3 "),
				makeSuccess(typEnv + " A = B |- x = 2 ∨ A ⊆ B ∨ x = 3 "),
				makeSuccess(typEnv + " A = B |- x = 2 ∨ B ⊆ A ∨ x = 3 "),
				makeSuccess(typEnv + " A ⊂ B |- x = 2 ∨ A ⊆ B ∨ x = 3 "),
				// A hypothesis stronger than a negative goal disjunct
				makeSuccess(" 1 < x |- x = 2 ∨ ¬x = 1 ∨ x = 3 "),
				makeSuccess(" 1 > x |- x = 2 ∨ ¬x = 1 ∨ x = 3 ∨ x = 3 "),
				makeSuccess(typEnv + " A ⊂ B |- x = 2 ∨ ¬A = B ∨ x = 3 "),
				makeSuccess(typEnv + " B ⊂ A |- x = 2 ∨ ¬A = B ∨ x = 3 "),
				makeSuccess(typEnv + " A ⊂ B |- x = 2 ∨ ¬B ⊆ A ∨ x = 3 "),
				makeSuccess(typEnv + " A ⊂ B |- x = 2 ∨ ¬B ⊂ A ∨ x = 3 "),
				makeSuccess(typEnv + " A ⊆ B |- x = 2 ∨ ¬B ⊂ A ∨ x = 3 "),
				makeSuccess(typEnv + " ¬A ⊆ B |- x = 2 ∨ ¬A ⊂ B ∨ x = 3 "),
				makeSuccess(typEnv + " A = B |- x = 2 ∨ ¬B ⊂ A ∨ x = 3 "),
				makeSuccess(typEnv + " A = B |- x = 2 ∨ ¬B ⊂ A ∨ x = 3 "),

		};
	}

	private SuccessfullReasonerApplication makeSuccess(String sequentImage) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new SuccessfullReasonerApplication(sequent, input);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final String error = "Hypotheses contain no disjunct of goal";

		return new UnsuccessfullReasonerApplication[] {
				makeFailure(" x = 1 |- x = 2",
						"Goal is not a disjunctive predicate"),
				makeFailure(" x = 1 |- x = 2 ∨ x = 4 ∨ x = 3 ", error),
				makeFailure(" x = 1 |- x = 2 ∨ x > 1 ∨ x < 1 ∨ x = 3", error),
				makeFailure(" x = 1 |- x = 2 ∨ 1 > x ∨ 1 < x ∨ x = 3", error),

		};
	}

	private UnsuccessfullReasonerApplication makeFailure(String sequentImage,
			String expected) {
		final IProverSequent sequent = genSeq(sequentImage);
		return new UnsuccessfullReasonerApplication(sequent, input, expected);
	}

}
