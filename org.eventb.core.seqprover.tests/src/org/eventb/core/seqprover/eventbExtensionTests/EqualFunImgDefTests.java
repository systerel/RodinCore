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

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.equalFunImgDefGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqualFunImgDefRewrites;
import org.junit.Test;

/**
 * Unit tests for the "functional image equality definition" rewriter.
 *
 * @author Guillaume Verdier
 */
public class EqualFunImgDefTests extends AbstractManualRewriterTests {

	public EqualFunImgDefTests() {
		super(new EqualFunImgDefRewrites());
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.equalFunImgDefRewrites";
	}

	/**
	 * Integration tests.
	 */
	@Test
	public void testIntegration() throws Exception {
		// Simple cases in goal
		assertReasonerSuccess("|- f(0) = 1", input("0"), "{}[][][] |- 0 ↦ 1 ∈ f");
		assertReasonerSuccess("|- 1 = f(0)", input("1"), "{}[][][] |- 0 ↦ 1 ∈ f");
		// Potentially ambiguous cases in goal
		assertReasonerSuccess("|- (λ x · ⊤ ∣ x + 1)(0) = id(1)", input("0"),
				"{}[][][] |- 0 ↦ id(1) ∈ (λ x · ⊤ ∣ x + 1)");
		assertReasonerSuccess("|- (λ x · ⊤ ∣ x + 1)(0) = id(1)", input("1"),
				"{}[][][] |- 1 ↦ (λ x · ⊤ ∣ x + 1)(0) ∈ id");
		// Inside other predicates
		assertReasonerSuccess("|- ∀ x, y · f(x) = y ∧ x > y", input("2.0.0"), "{}[][][] |- ∀ x, y · x ↦ y ∈ f ∧ x > y");
		// In hypothesis
		assertReasonerSuccess("f(0) = 1 |- ⊥", input("f(0) = 1", "0"), "{}[f(0) = 1][][0 ↦ 1 ∈ f] |- ⊥");
		// Invalid inputs
		assertReasonerFailure("|- f(0) = 1", input("1"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal f(0)=1 at position 1");
		assertReasonerFailure("f(0) = 1 |- ⊥", input("f(0) = 1", "1"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis f(0)=1 at position 1");
	}

	/**
	 * Applications of the rewriter that should succeed.
	 */
	@Test
	public void success() throws Exception {
		rewritePred("f(0) = 1", "0", "0 ↦ 1 ∈ f");
		rewritePred("1 = f(0)", "1", "0 ↦ 1 ∈ f");
		rewritePred("{ x ↦ 2 ∗ x ∣ x ∈ ℕ }(0) = { 1 ↦ 0 }(1)", "0", "0 ↦ { 1 ↦ 0 }(1) ∈ { x ↦ 2 ∗ x ∣ x ∈ ℕ }");
		rewritePred("{ x ↦ 2 ∗ x ∣ x ∈ ℕ }(0) = { 1 ↦ 0 }(1)", "1", "1 ↦ { x ↦ 2 ∗ x ∣ x ∈ ℕ }(0) ∈ { 1 ↦ 0 }");
		rewritePred("f(x) = 0 ∧ x = f(1)", "0.0", "x ↦ 0 ∈ f ∧ x = f(1)");
		rewritePred("f(x) = 0 ∧ x = f(1)", "1.1", "f(x) = 0 ∧ 1 ↦ x ∈ f");
	}

	/**
	 * Applications of the rewriter that should fail.
	 */
	@Test
	public void failure() throws Exception {
		noRewritePred("f(0) = 1", "1");
		noRewritePred("f(0) = 1", "");
		noRewritePred("f(0) > 0", "");
		noRewritePred("f(0) > 0", "0");
	}

	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("f(0) = 1", "0");
		assertGetPositions("0 = f(1)", "1");
		assertGetPositions("f(0) = id(1)", "0", "1");
		assertGetPositions("f(x) = 0 ∧ x = f(1)", "0.0", "1.1");
		assertGetPositions("∀ x, y · f(x) = y ∧ x > y", "2.0.0");
		// Cases on which the reasoner can't be applied
		assertGetPositions("f(0) > 0");
		assertGetPositions("1 + f(0) = 1");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return equalFunImgDefGetPositions(predicate);
	}

	protected IReasonerInput input(String position) {
		return new AbstractManualRewrites.Input(null, makePosition(position));
	}

	protected IReasonerInput input(String hyp, String position) {
		return new AbstractManualRewrites.Input(genPred(hyp), makePosition(position));
	}

}