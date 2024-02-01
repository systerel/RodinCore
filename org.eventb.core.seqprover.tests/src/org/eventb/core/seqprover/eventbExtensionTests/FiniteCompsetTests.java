/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteCompsetGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteCompset;
import org.junit.Test;

/**
 * Unit tests for the Finite of set comprehension reasoner {@link FiniteCompset}
 *
 * @author Guillaume Verdier
 */
public class FiniteCompsetTests extends AbstractEmptyInputReasonerTests {

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return finiteCompsetGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteCompset";
	}

	@Test
	public void success() throws Exception {
		// Single bound variable
		assertReasonerSuccess("⊤ |- finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 })",
				"{}[][][⊤] |- finite({ x ∣ x ∈ ℕ ∧ x < 10 })");
		assertReasonerSuccess("⊤ |- finite(λ x · x ∈ ℕ ∧ x < 10 ∣ x + 1)",
				"{}[][][⊤] |- finite({ x ∣ x ∈ ℕ ∧ x < 10 })");
		// Multiple bound variables
		assertReasonerSuccess("⊤ |- finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x + y })",
				"{}[][][⊤] |- finite({ x ↦ y ∣ x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 })");
		assertReasonerSuccess("⊤ |- finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ 0 })",
				"{}[][][⊤] |- finite({ x ↦ y ∣ x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 })");
		assertReasonerSuccess("⊤ |- finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ x ↦ y })",
				"{}[][][⊤] |- finite({ x ↦ y ∣ x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 })");
	}

	@Test
	public void failure() throws Exception {
		// Goal is not finite(...)
		assertReasonerFailure("⊤ |- (n = 2) ⇒ finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 })",
				"Inference 'finite of set comprehension' is not applicable");
		// No expression to remove in set
		assertReasonerFailure("⊤ |- finite({ x ∣ x ∈ ℕ ∧ x < 10})",
				"Inference 'finite of set comprehension' is not applicable");
		assertReasonerFailure("⊤ |- finite({ x · x ∈ ℕ ∧ x < 10 ∣ x })",
				"Inference 'finite of set comprehension' is not applicable");
		assertReasonerFailure("⊤ |- finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ y })",
				"Inference 'finite of set comprehension' is not applicable");
		assertReasonerFailure("⊤ |- finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ y ↦ x })",
				"Inference 'finite of set comprehension' is not applicable");
		assertReasonerFailure("⊤ |- finite({ a, b, c, d · a + b + c + d = 0 ∣ (c ↦ b) ↦ (a ↦ d) })",
				"Inference 'finite of set comprehension' is not applicable");
		// Finite in hypothesis, not in goal
		assertReasonerFailure("finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 }) |- ⊤",
				"Inference 'finite of set comprehension' is not applicable");
	}

	@Test
	public void testPositions() {
		assertGetPositions("(n = 2) ⇒ finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 })");
		assertGetPositions("∀n· n = 2 ⇒ finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 })");
		assertGetPositions("finite({ x · x ∈ ℕ ∧ x < 10 ∣ x + 1 })", "ROOT");
		assertGetPositions("finite({ x ∣ x ∈ ℕ ∧ x < 10})");
		assertGetPositions("finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x + y })", "ROOT");
		assertGetPositions("finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ 0 })", "ROOT");
		assertGetPositions("finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ x ↦ y })", "ROOT");
		assertGetPositions("finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ x ↦ y })");
		assertGetPositions("finite({ x, y · x ∈ ℕ ∧ x < 10 ∧ y ∈ 5‥15 ∣ y ↦ x })");
		assertGetPositions("finite({ a, b, c, d · a + b + c + d = 0 ∣ (c ↦ b) ↦ (a ↦ d) })");
	}

}
