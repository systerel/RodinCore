/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.funDprodImgGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

/**
 * Unit tests for the "direct product fun. image" reasoner.
 *
 * @author Guillaume Verdier
 */
public class FunDprodImgTests extends AbstractManualInferenceTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funDprodImg";
	}

	/**
	 * Successful tests in goal.
	 */
	@Test
	public void successInGoal() throws Exception {
		assertReasonerSuccess("|- (f ⊗ g)(0) = 0 ↦ 0", input("0"), //
				"{}[][][] |- 0 ∈ dom(f) ∧ f ∈ ℤ ⇸ ℤ ∧ 0 ∈ dom(g) ∧ g ∈ ℤ ⇸ ℤ", //
				"{}[][][] |- f(0) ↦ g(0) = 0 ↦ 0");
		assertReasonerSuccess("|- ∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b", input("5.0"), //
				"{}[][][] |- ∀ f, g, h, a, b · a + b ∈ dom(f ∪ g) ∧ f ∪ g ∈ ℤ ⇸ ℤ ∧ a + b ∈ dom(h) ∧ h ∈ ℤ ⇸ ℤ", //
				"{}[][][] |- ∀ f, g, h, a, b · (f ∪ g)(a + b) ↦ h(a + b) = a ↦ b");
	}

	/**
	 * Successful tests in hypothesis.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		assertReasonerSuccess("(f ⊗ g)(0) = 0 ↦ 0 |- ⊥", input("(f ⊗ g)(0) = 0 ↦ 0", "0"), //
				"{}[][][(f ⊗ g)(0) = 0 ↦ 0] |- 0 ∈ dom(f) ∧ f ∈ ℤ ⇸ ℤ ∧ 0 ∈ dom(g) ∧ g ∈ ℤ ⇸ ℤ", //
				"{}[(f ⊗ g)(0) = 0 ↦ 0][][f(0) ↦ g(0) = 0 ↦ 0] |- ⊥");
		assertReasonerSuccess("∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b |- ⊥",
				input("∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b", "5.0"), //
				"{}[][][∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b] "
						+ "|- ∀ f, g, h, a, b · a + b ∈ dom(f ∪ g) ∧ f ∪ g ∈ ℤ ⇸ ℤ ∧ a + b ∈ dom(h) ∧ h ∈ ℤ ⇸ ℤ", //
				"{}[∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b][]"
						+ "[∀ f, g, h, a, b · (f ∪ g)(a + b) ↦ h(a + b) = a ↦ b] |- ⊥");
	}

	/**
	 * Tests on computation of positions.
	 */
	@Test
	public void testPositions() {
		// Valid positions
		assertGetPositions("(f ⊗ g)(0) = 0 ↦ 0", "0");
		assertGetPositions("∀ f, g, h, a, b · ((f ∪ g) ⊗ h)(a + b) = a ↦ b", "5.0");
		// No positions
		assertGetPositions("f ⊗ g = ℤ×(ℤ×ℤ)");
		assertGetPositions("f(0) = 0 ↦ 0");
		assertGetPositions("(f ∥ g)(0 ↦ 0) = 0 ↦ 0");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return funDprodImgGetPositions(predicate);
	}

}
