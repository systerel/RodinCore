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
import static org.eventb.core.seqprover.eventbExtensions.Tactics.cardDefGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * Unit tests for the "cardinal definition" rewriter.
 *
 * @author Guillaume Verdier
 */
public class CardDefTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardDefRewrites";
	}

	/**
	 * Applications of the rewriter on the goal that should succeed.
	 */
	@Test
	public void successInGoal() throws Exception {
		// Rewrite, card on left-hand side
		assertReasonerSuccess("|- card({1})=1", input("0"), //
				"{}[][][] |- ∃f · f∈1‥1⤖{1}");
		// Rewrite, card on right-hand side
		assertReasonerSuccess("|- 1=card({1})", input("1"), //
				"{}[][][] |- ∃f · f∈1‥1⤖{1}");
		// Rewrite in sub-expression
		assertReasonerSuccess("|- S ⊆ ℕ ∨ card(S) = n", input("1.0"), //
				"{}[][][] |- S ⊆ ℕ ∨ (∃f · f∈1‥n⤖S)");
		// Rewrite in quantified expression
		assertReasonerSuccess("|- ∀x ⦂ ℤ · card({x})=1", input("1.0"), //
				"{}[][][] |- ∀x ⦂ ℤ · ∃f · f∈1‥1⤖{x}");
		// Rewrites when both sides of the equality are rewritable
		assertReasonerSuccess("|- card({1})=card({2})", input("0"), //
				"{}[][][] |- ∃f · f∈1‥card({2})⤖{1}");
		assertReasonerSuccess("|- card({1})=card({2})", input("1"), //
				"{}[][][] |- ∃f · f∈1‥card({1})⤖{2}");
	}

	/**
	 * Applications of the rewriter on an hypothesis that should succeed.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		// Rewrite, card on left-hand side
		assertReasonerSuccess("card({1})=1 |- ⊥", input("card({1})=1", "0"), //
				"{}[card({1})=1][][∃f · f∈1‥1⤖{1}] |- ⊥");
		// Rewrite, card on right-hand side
		assertReasonerSuccess("1=card({1}) |- ⊥", input("1=card({1})", "1"), //
				"{}[1=card({1})][][∃f · f∈1‥1⤖{1}] |- ⊥");
		// Rewrite in sub-expression
		assertReasonerSuccess("S ⊆ ℕ ∧ card(S) = n |- ⊥", input("S ⊆ ℕ ∧ card(S) = n", "1.0"), //
				"{}[S ⊆ ℕ ∧ card(S) = n][][S ⊆ ℕ;; ∃f · f∈1‥n⤖S] |- ⊥");
		// Rewrite in quantified expression
		assertReasonerSuccess("∀x ⦂ ℤ · card({x})=1 |- ⊥", input("∀x ⦂ ℤ · card({x})=1", "1.0"), //
				"{}[∀x ⦂ ℤ · card({x})=1][][∀x ⦂ ℤ · ∃f · f∈1‥1⤖{x}] |- ⊥");
		// Rewrites when both sides of the equality are rewritable
		assertReasonerSuccess("card({1})=card({2}) |- ⊥", input("card({1})=card({2})", "0"), //
				"{}[card({1})=card({2})][][∃f · f∈1‥card({2})⤖{1}] |- ⊥");
		assertReasonerSuccess("card({1})=card({2}) |- ⊥", input("card({1})=card({2})", "1"), //
				"{}[card({1})=card({2})][][∃f · f∈1‥card({1})⤖{2}] |- ⊥");
	}

	/**
	 * Applications of the rewriter on a goal that should fail.
	 */
	@Test
	public void failureInGoal() throws Exception {
		// Wrong position
		assertReasonerFailure("|- card({1})=1", input("1"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1})=1 at position 1");
		// Not an equality
		assertReasonerFailure("|- card({1})≠1", input("0"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1})≠1 at position 0");
		// Not a cardinal
		assertReasonerFailure("|- inter({{1}})={1}", input("0"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal inter({{1}})={1} at position 0");
	}

	/**
	 * Applications of the rewriter on an hypothesis that should fail.
	 */
	@Test
	public void failureInHypothesis() throws Exception {
		// Wrong position
		assertReasonerFailure("card({1})=1 |- ⊥", input("card({1})=1", "1"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1})=1 at position 1");
		// Not an equality
		assertReasonerFailure("card({1})≠1 |- ⊥", input("card({1})≠1", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1})≠1 at position 0");
		// Not a cardinal
		assertReasonerFailure("inter({{1}})={1} |- ⊥", input("inter({{1}})={1}", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis inter({{1}})={1} at position 0");
	}

	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("card({1})=1", "0");
		assertGetPositions("1=card({1})", "1");
		assertGetPositions("S ⊆ ℕ ∧ card(S) = n", "1.0");
		assertGetPositions("∀x ⦂ ℤ · card({x})=1", "1.0");
		// Cases on which the reasoner can't be applied
		assertGetPositions("card({1})≠1"); // Not an equality
		assertGetPositions("inter({{1}})={1}"); // Not a cardinal
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return cardDefGetPositions(predicate);
	}

	protected IReasonerInput input(String position) {
		return new AbstractManualRewrites.Input(null, makePosition(position));
	}

	protected IReasonerInput input(String hyp, String position) {
		return new AbstractManualRewrites.Input(genPred(hyp), makePosition(position));
	}

}