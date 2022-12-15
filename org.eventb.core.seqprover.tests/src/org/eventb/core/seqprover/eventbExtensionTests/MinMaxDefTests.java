/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.minMaxDefGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * Unit tests for the "min/max definition" rewriter.
 *
 * @author Guillaume Verdier
 */
public class MinMaxDefTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.minMaxDefRewrites";
	}

	/**
	 * Applications of the rewriter on the goal that should succeed.
	 */
	@Test
	public void successInGoal() throws Exception {
		// Rewrite, min/max on left-hand side
		assertReasonerSuccess("|- min({1, 2})=1", input("0"), //
				"{}[][][] |- 1 ∈ {1, 2}", //
				"{}[][][] |- ∀ x · x ∈ {1, 2} ⇒ 1 ≤ x");
		assertReasonerSuccess("|- max({1, 2})=2", input("0"), //
				"{}[][][] |- 2 ∈ {1, 2}", //
				"{}[][][] |- ∀ x · x ∈ {1, 2} ⇒ 2 ≥ x");
		// Rewrite, min/max on right-hand side
		assertReasonerSuccess("|- 1=min({1, 2})", input("1"), //
				"{}[][][] |- 1 ∈ {1, 2}", //
				"{}[][][] |- ∀ x · x ∈ {1, 2} ⇒ 1 ≤ x");
		assertReasonerSuccess("|- 2=max({1, 2})", input("1"), //
				"{}[][][] |- 2 ∈ {1, 2}", //
				"{}[][][] |- ∀ x · x ∈ {1, 2} ⇒ 2 ≥ x");
		// Rewrite in sub-expression
		assertReasonerSuccess("|- S ⊆ ℕ ∨ min(S) = n", input("1.0"), //
				"{}[][][] |- S ⊆ ℕ ∨ (n ∈ S ∧ (∀ x · x ∈ S ⇒ n ≤ x))");
		assertReasonerSuccess("|- S ⊆ ℕ ∨ max(S) = n", input("1.0"), //
				"{}[][][] |- S ⊆ ℕ ∨ (n ∈ S ∧ (∀ x · x ∈ S ⇒ n ≥ x))");
		// Rewrite in quantified expression
		assertReasonerSuccess("|- ∀x ⦂ ℤ · min({x})=x", input("1.0"), //
				"{}[][][] |- ∀x ⦂ ℤ · x ∈ {x} ∧ (∀ x0 · x0 ∈ {x} ⇒ x ≤ x0)");
		assertReasonerSuccess("|- ∀x ⦂ ℤ · max({x})=x", input("1.0"), //
				"{}[][][] |- ∀x ⦂ ℤ · x ∈ {x} ∧ (∀ x0 · x0 ∈ {x} ⇒ x ≥ x0)");
		// Rewrites when both sides of the equality are rewritable
		assertReasonerSuccess("|- min({1, 2})=max({0, 1})", input("0"), //
				"{}[][][] |- max({0, 1}) ∈ {1, 2}", //
				"{}[][][] |- ∀ x · x ∈ {1, 2} ⇒ max({0, 1}) ≤ x");
		assertReasonerSuccess("|- min({1, 2})=max({0, 1})", input("1"), //
				"{}[][][] |- min({1, 2}) ∈ {0, 1}", //
				"{}[][][] |- ∀ x · x ∈ {0, 1} ⇒ min({1, 2}) ≥ x");
	}

	/**
	 * Applications of the rewriter on a hypothesis that should succeed.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		// Rewrite, min/max on left-hand side
		assertReasonerSuccess("min({1, 2})=1 |- ⊥", input("min({1, 2})=1", "0"), //
				"{}[min({1, 2})=1][][1 ∈ {1, 2} ;; ∀ x · x ∈ {1, 2} ⇒ 1 ≤ x] |- ⊥");
		assertReasonerSuccess("max({1, 2})=2 |- ⊥", input("max({1, 2})=2", "0"), //
				"{}[max({1, 2})=2][][2 ∈ {1, 2} ;; ∀ x · x ∈ {1, 2} ⇒ 2 ≥ x] |- ⊥");
		// Rewrite, min/max on right-hand side
		assertReasonerSuccess("1=min({1, 2}) |- ⊥", input("1=min({1, 2})", "1"), //
				"{}[1=min({1, 2})][][1 ∈ {1, 2} ;; ∀ x · x ∈ {1, 2} ⇒ 1 ≤ x] |- ⊥");
		assertReasonerSuccess("2=max({1, 2}) |- ⊥", input("2=max({1, 2})", "1"), //
				"{}[2=max({1, 2})][][2 ∈ {1, 2} ;; ∀ x · x ∈ {1, 2} ⇒ 2 ≥ x] |- ⊥");
		// Rewrite in sub-expression
		assertReasonerSuccess("S ⊆ ℕ ∨ min(S) = n |- ⊥", input("S ⊆ ℕ ∨ min(S) = n", "1.0"), //
				"{}[S ⊆ ℕ ∨ min(S) = n][][S ⊆ ℕ ∨ (n ∈ S ∧ (∀ x · x ∈ S ⇒ n ≤ x))] |- ⊥");
		assertReasonerSuccess("S ⊆ ℕ ∨ max(S) = n |- ⊥", input("S ⊆ ℕ ∨ max(S) = n", "1.0"), //
				"{}[S ⊆ ℕ ∨ max(S) = n][][S ⊆ ℕ ∨ (n ∈ S ∧ (∀ x · x ∈ S ⇒ n ≥ x))] |- ⊥");
		// Rewrite in quantified expression
		assertReasonerSuccess("∀x ⦂ ℤ · min({x})=x |- ⊥", input("∀x ⦂ ℤ · min({x})=x", "1.0"), //
				"{}[∀x ⦂ ℤ · min({x})=x][][∀x ⦂ ℤ · x ∈ {x} ∧ (∀ x0 · x0 ∈ {x} ⇒ x ≤ x0)] |- ⊥");
		assertReasonerSuccess("∀x ⦂ ℤ · max({x})=x |- ⊥", input("∀x ⦂ ℤ · max({x})=x", "1.0"), //
				"{}[∀x ⦂ ℤ · max({x})=x][][∀x ⦂ ℤ · x ∈ {x} ∧ (∀ x0 · x0 ∈ {x} ⇒ x ≥ x0)] |- ⊥");
		// Rewrites when both sides of the equality are rewritable
		assertReasonerSuccess("min({1, 2})=max({0, 1}) |- ⊥", input("min({1, 2})=max({0, 1})", "0"), //
				"{}[min({1, 2})=max({0, 1})][][max({0, 1}) ∈ {1, 2} ;; ∀ x · x ∈ {1, 2} ⇒ max({0, 1}) ≤ x] |- ⊥");
		assertReasonerSuccess("min({1, 2})=max({0, 1}) |- ⊥", input("min({1, 2})=max({0, 1})", "1"), //
				"{}[min({1, 2})=max({0, 1})][][min({1, 2}) ∈ {0, 1} ;; ∀ x · x ∈ {0, 1} ⇒ min({1, 2}) ≥ x] |- ⊥");
	}

	/**
	 * Applications of the rewriter on a goal that should fail.
	 */
	@Test
	public void failureInGoal() throws Exception {
		// Wrong position
		assertReasonerFailure("|- min({1, 2})=1", input("1"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal min({1,2})=1 at position 1");
		// Not an equality
		assertReasonerFailure("|- max({1, 2})≠1", input("0"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal max({1,2})≠1 at position 0");
		// Not a min or max
		assertReasonerFailure("|- card({1, 2})=2", input("0"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1,2})=2 at position 0");
	}

	/**
	 * Applications of the rewriter on a hypothesis that should fail.
	 */
	@Test
	public void failureInHypothesis() throws Exception {
		// Wrong position
		assertReasonerFailure("min({1, 2})=1 |- ⊥", input("min({1, 2})=1", "1"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis min({1,2})=1 at position 1");
		// Not an equality
		assertReasonerFailure("max({1, 2})≠1 |- ⊥", input("max({1, 2})≠1", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis max({1,2})≠1 at position 0");
		// Not a min or max
		assertReasonerFailure("card({1, 2})=2 |- ⊥", input("card({1, 2})=2", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1,2})=2 at position 0");
	}

	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("min({1, 2})=1", "0");
		assertGetPositions("1=min({1, 2})", "1");
		assertGetPositions("max({1, 2})=2", "0");
		assertGetPositions("2=max({1, 2})", "1");
		assertGetPositions("S ⊆ ℕ ∧ min(S) = n", "1.0");
		assertGetPositions("S ⊆ ℕ ∧ max(S) = n", "1.0");
		assertGetPositions("∀x ⦂ ℤ · min({x})=x", "1.0");
		assertGetPositions("∀x ⦂ ℤ · max({x})=x", "1.0");
		// Cases on which the reasoner can't be applied
		assertGetPositions("min({2})≠1"); // Not an equality
		assertGetPositions("max({2})≠1"); // Not an equality
		assertGetPositions("card({1, 2})=2"); // Not a min or max
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return minMaxDefGetPositions(predicate);
	}

	protected IReasonerInput input(String position) {
		return new AbstractManualRewrites.Input(null, makePosition(position));
	}

	protected IReasonerInput input(String hyp, String position) {
		return new AbstractManualRewrites.Input(genPred(hyp), makePosition(position));
	}

}