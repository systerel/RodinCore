/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
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

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.junit.Test;

public class HypTests extends AbstractReasonerTests {

	private static final IReasonerInput INPUT = new EmptyInput();

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.hyp";
	}

	@Test
	public void success() throws UntranslatableException {
		// Goal in hypotheses
		assertReasonerSuccess(" x = 1 |- x = 1 ");
		assertReasonerSuccess(" 1∈P |- 1∈P ");
		// A hypothesis equivalent to the goal
		assertReasonerSuccess(" 1 = x |- x = 1 ");
		assertReasonerSuccess(" 1 > x |- x < 1 ");
		assertReasonerSuccess(" 1 < x |- x > 1 ");
		assertReasonerSuccess(" 1 ≥ x |- x ≤ 1 ");
		assertReasonerSuccess(" 1 ≤ x |- x ≥ 1 ");
		assertReasonerSuccess(" x ∈ ℕ |- x ≥ 0");
		assertReasonerSuccess(" 1 ≤ x |- 0 < x");
		// A hypothesis stronger than a positive Goal
		// H; P |- P†
		assertReasonerSuccess(" 1 = x |- 1 ≥ x ");
		assertReasonerSuccess(" 1 = x |- 1 ≤ x ");
		assertReasonerSuccess(" 1 > x |- 1 ≥ x ");
		assertReasonerSuccess(" 1 < x |- 1 ≤ x ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A = B |- A ⊆ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A = B |- B ⊆ A ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A ⊂ B |- A ⊆ B ");
		// A hypothesis stronger than a negative Goal
		// H; nP† |- ¬P
		assertReasonerSuccess(" 1 < x |- ¬x = 1 ");
		assertReasonerSuccess(" 1 > x |- ¬x = 1 ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A ⊂ B |- ¬A = B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B ⊂ A |- ¬A = B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; ¬A ⊆ B |- ¬A = B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; ¬B ⊆ A |- ¬A = B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B ⊂ A |- ¬A ⊆ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B ⊂ A |- ¬A ⊂ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B ⊆ A |- ¬A ⊂ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; ¬A ⊆ B |- ¬A ⊂ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B = A |- ¬A ⊂ B ");
		assertReasonerSuccess("A∈ℙ(ℤ) ;; B = A |- ¬A ⊂ B ");
	}

	private void assertReasonerSuccess(String sequentImage) throws UntranslatableException {
		assertReasonerSuccess(sequentImage, INPUT);
	}

	@Test
	public void testUnsuccessful() throws UntranslatableException {
		// Sequent not normalized
		assertReasonerFailure(" 1 > x ;; ¬x ≥ 1 |- x = 1 ");

		// simple tests
		assertReasonerFailure(" x = 1 |- x = 2 "); //
		assertReasonerFailure(" 1∈P |- 2∈P ");
		assertReasonerFailure(" x > 1 ;; x < 1 ;; x ≥ 1 ;; x ≤ 1 |- x = 1 ");
		assertReasonerFailure(" 1 > x ;; 1 < x ;; 1 ≥ x ;; 1 ≤ x |- x = 1 ");
		assertReasonerFailure(" 1 > x ;; 1 < x ;; 1 ≥ x ;; 1 ≤ x |- x = 1 ");
	}

	private void assertReasonerFailure(String sequentImage) throws UntranslatableException {
		assertReasonerFailure(sequentImage, INPUT, "Goal not in hypothesis");
	}

}
