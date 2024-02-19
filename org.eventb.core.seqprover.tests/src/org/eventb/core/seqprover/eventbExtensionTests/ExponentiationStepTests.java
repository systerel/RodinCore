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

import static org.eventb.core.seqprover.eventbExtensions.Tactics.exponentiationStepGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

/**
 * Unit tests for the "definition of exponentiation step" rewriter.
 *
 * @author Guillaume Verdier
 */
public class ExponentiationStepTests extends AbstractManualInferenceTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.exponentiationStep";
	}

	/**
	 * Successful tests in goal.
	 */
	@Test
	public void successInGoal() throws Exception {
		// Simple case
		assertReasonerSuccess("|- x^n = 0", input("0"), //
				"{x=ℤ}[][][] |- ¬ n = 0", //
				"{}[][][] |- x∗x^(n−1) = 0");
		// With sub-expressions
		assertReasonerSuccess("|- (a+1)^(k+n) > 0", input("0"), //
				"{a=ℤ}[][][] |- ¬ k+n = 0", //
				"{}[][][] |- (a+1)∗(a+1)^(k+n−1) > 0");
		// Inside an expression
		assertReasonerSuccess("|- f(x^n)+1 = 0", input("0.0.1"), //
				"{f=ℙ(ℤ×ℤ);x=ℤ}[][][] |- ¬ n = 0", //
				"{}[][][] |- f(x∗x^(n−1))+1 = 0");
		// Apply to the right exponent when there are several
		assertReasonerSuccess("|- a^(b^n) = 0", input("0"), //
				"{a=ℤ;b=ℤ}[][][] |- ¬ b^n = 0", //
				"{}[][][] |- a∗a^(b^n−1) = 0");
		assertReasonerSuccess("|- a^(b^n) = 0", input("0.1"), //
				"{a=ℤ;b=ℤ}[][][] |- ¬ n = 0", //
				"{}[][][] |- a^(b∗b^(n−1)) = 0");
	}

	/**
	 * Successful tests in hypothesis.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		// Simple case
		assertReasonerSuccess("x^n = 0 |- ⊥", input("x^n = 0", "0"), //
				"{}[][][x^n = 0] |- ¬ n = 0", //
				"{}[x^n = 0][][x∗x^(n−1) = 0] |- ⊥");
		// With sub-expressions
		assertReasonerSuccess("(a+1)^(k+n) > 0 |- ⊥", input("(a+1)^(k+n) > 0", "0"), //
				"{a=ℤ}[][][(a+1)^(k+n) > 0] |- ¬ k+n = 0", //
				"{}[(a+1)^(k+n) > 0][][(a+1)∗(a+1)^(k+n−1) > 0] |- ⊥");
		// Inside an expression
		assertReasonerSuccess("f(x^n)+1 = 0 |- ⊥", input("f(x^n)+1 = 0", "0.0.1"), //
				"{f=ℙ(ℤ×ℤ);x=ℤ}[][][f(x^n)+1 = 0] |- ¬ n = 0", //
				"{}[f(x^n)+1 = 0][][f(x∗x^(n−1))+1 = 0] |- ⊥");
		// Apply to the right exponent when there are several
		assertReasonerSuccess("a^(b^n) = 0 |- ⊥", input("a^(b^n) = 0", "0"), //
				"{a=ℤ;b=ℤ}[][][a^(b^n) = 0] |- ¬ b^n = 0", //
				"{}[a^(b^n) = 0][][a∗a^(b^n−1) = 0] |- ⊥");
		assertReasonerSuccess("a^(b^n) = 0 |- ⊥", input("a^(b^n) = 0", "0.1"), //
				"{a=ℤ;b=ℤ}[][][a^(b^n) = 0] |- ¬ n = 0", //
				"{}[a^(b^n) = 0][][a^(b∗b^(n−1)) = 0] |- ⊥");
	}

	/**
	 * Invalid cases in goal.
	 */
	@Test
	public void errorInGoal() throws Exception {
		// Invalid position
		assertReasonerFailure("|- x^n = 0", input("1"),
				"Inference org.eventb.core.seqprover.exponentiationStep is not applicable for x ^ n=0 at position 1");
		assertReasonerFailure("|- x∗n = 0", input("0"),
				"Inference org.eventb.core.seqprover.exponentiationStep is not applicable for x∗n=0 at position 0");
		assertReasonerFailure("|- y ≠ 0 ⇒ a^(x÷y) = k", input("1.0"),
				"Inference org.eventb.core.seqprover.exponentiationStep is not applicable for y≠0⇒a ^ (x ÷ y)=k at position 1.0");
	}

	/**
	 * Tests on positions computation.
	 */
	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("x^n = 0", "0");
		assertGetPositions("(a+1)^(k+n) > 0", "0");
		assertGetPositions("f(x^n)+1 = 0", "0.0.1");
		assertGetPositions("a^(b^n) = 0", "0", "0.1");
		// Cases on which the reasoner can't be applied
		assertGetPositions("x∗n = 0");
		assertGetPositions("y ≠ 0 ⇒ a^(x÷y) = k");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return exponentiationStepGetPositions(predicate);
	}

}
