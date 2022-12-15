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
import static org.eventb.core.seqprover.eventbExtensions.Tactics.equalCardGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * Unit tests for the "simplify cardinal equality" rewriter.
 *
 * @author Guillaume Verdier
 */
public class EqualCardTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.equalCardRewrites";
	}

	/**
	 * Applications of the rewriter on the goal that should succeed.
	 */
	@Test
	public void successInGoal() throws Exception {
		// Rewrite, equality at root position
		assertReasonerSuccess("|- card({1})=card({2})", input(""), //
				"{}[][][] |- ∃f · f∈{1}⤖{2}");
		// Rewrite in sub-expression
		assertReasonerSuccess("|- S ∪ T ⊆ ℕ ∨ card(S) = card(T)", input("1"), //
				"{}[][][] |- S ∪ T ⊆ ℕ ∨ (∃f · f∈S⤖T)");
		// Rewrite in quantified expression
		assertReasonerSuccess("|- ∀x ⦂ ℤ · card({x})=card({0})", input("1"), //
				"{}[][][] |- ∀x ⦂ ℤ · ∃f · f∈{x}⤖{0}");
	}

	/**
	 * Applications of the rewriter on an hypothesis that should succeed.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		// Rewrite, equality at root position
		assertReasonerSuccess("card({1})=card({2}) |- ⊥", input("card({1})=card({2})", ""), //
				"{}[card({1})=card({2})][][∃f · f∈{1}⤖{2}] |- ⊥");
		// Rewrite in sub-expression
		assertReasonerSuccess("S ∪ T ⊆ ℕ ∧ card(S) = card(T) |- ⊥", input("S ∪ T ⊆ ℕ ∧ card(S) = card(T)", "1"), //
				"{}[S ∪ T ⊆ ℕ ∧ card(S) = card(T)][][S ∪ T ⊆ ℕ;; ∃f · f∈S⤖T] |- ⊥");
		// Rewrite in quantified expression
		assertReasonerSuccess("∀x ⦂ ℤ · card({x})=card({0}) |- ⊥", input("∀x ⦂ ℤ · card({x})=card({0})", "1"), //
				"{}[∀x ⦂ ℤ · card({x})=card({0})][][∀x ⦂ ℤ · ∃f · f∈{x}⤖{0}] |- ⊥");
	}

	/**
	 * Applications of the rewriter on a goal that should fail.
	 */
	@Test
	public void failureInGoal() throws Exception {
		// Wrong position
		assertReasonerFailure("|- card({1})=card({2})", input("1"),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1})=card({2}) at position 1");
		// Not an equality
		assertReasonerFailure("|- card({1})≠card({2})", input(""),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1})≠card({2}) at position ");
		// No cardinal on left-hand side
		assertReasonerFailure("|- 1=card({1})", input(""),
				"Rewriter " + getReasonerID() + " is inapplicable for goal 1=card({1}) at position ");
		// No cardinal on right-hand side
		assertReasonerFailure("|- card({1})=1", input(""),
				"Rewriter " + getReasonerID() + " is inapplicable for goal card({1})=1 at position ");
		// No cardinals
		assertReasonerFailure("|- inter({{1}})=inter({{2}})", input(""),
				"Rewriter " + getReasonerID() + " is inapplicable for goal inter({{1}})=inter({{2}}) at position ");
	}

	/**
	 * Applications of the rewriter on an hypothesis that should fail.
	 */
	@Test
	public void failureInHypothesis() throws Exception {
		// Wrong position
		assertReasonerFailure("card({1})=card({2}) |- ⊥", input("card({1})=card({2})", "1"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1})=card({2}) at position 1");
		// Not an equality
		assertReasonerFailure("card({1})≠card({2}) |- ⊥", input("card({1})≠card({2})", ""),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1})≠card({2}) at position ");
		// No cardinal on left-hand side
		assertReasonerFailure("1=card({1}) |- ⊥", input("1=card({1})", ""),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis 1=card({1}) at position ");
		// No cardinal on right-hand side
		assertReasonerFailure("card({1})=1 |- ⊥", input("card({1})=1", ""),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis card({1})=1 at position ");
		// No cardinals
		assertReasonerFailure("inter({{1}})=inter({{2}}) |- ⊥", input("inter({{1}})=inter({{2}})", ""), "Rewriter "
				+ getReasonerID() + " is inapplicable for hypothesis inter({{1}})=inter({{2}}) at position ");
	}

	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("card({1})=card({2})", "ROOT");
		assertGetPositions("S ∪ T ⊆ ℕ ∧ card(S) = card(T)", "1");
		assertGetPositions("∀x ⦂ ℤ · card({x})=card({0})", "1");
		// Cases on which the reasoner can't be applied
		assertGetPositions("card({1})≠card({2})"); // Not an equality
		assertGetPositions("1=card({1})"); // Not a cardinal in left-hand side
		assertGetPositions("card({1})=1"); // Not a cardinal in right-hand side
		assertGetPositions("inter({{1}})=inter({{2}})"); // No cardinals
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return equalCardGetPositions(predicate);
	}

	protected IReasonerInput input(String position) {
		return new AbstractManualRewrites.Input(null, makePosition(position));
	}

	protected IReasonerInput input(String hyp, String position) {
		return new AbstractManualRewrites.Input(genPred(hyp), makePosition(position));
	}

}