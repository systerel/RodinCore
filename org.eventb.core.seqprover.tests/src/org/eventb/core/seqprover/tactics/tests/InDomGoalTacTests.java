/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.trueGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.typeRewrites;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>TotalDomRewritesTac</code>.
 */
public class InDomGoalTacTests extends AbstractTacticTests {

	public InDomGoalTacTests() {
		super(new AutoTactics.InDomGoalTac(),
				"org.eventb.core.seqprover.InDomGoalTac");
	}

	/**
	 * Ensures that the tactic succeeds using true goal tactic
	 */
	@Test
	public void successWithTrueGoal() {
		final Expression substitute = parseExpression("ℤ");
		assertSuccess(" ;H; ;S; f ∈ ℤ → ℤ|- x∈dom(f)",
				totalDom(null, "1", substitute, typeRewrites(trueGoal())));
	}

	/**
	 * Ensures that the tactic succeeds using hyp tactic.
	 */
	@Test
	public void successWithHyp() {
		addToTypeEnvironment("g=ℤ↔ℤ");
		final Expression substitute = parseExpression("dom(g)");
		assertSuccess(
				" ;H; ;S; g ∈ ℤ → ℤ ;; 0 ∈ dom(g) ;; f ∈ dom(g)→ ℤ |- 0 ∈ dom(f)",
				totalDom(null, "1", substitute, hyp()));
	}

	/**
	 * Ensures that the tactic succeeds
	 */
	@Test
	public void successWithFunAppInDom() {
		addToTypeEnvironment("f=ℤ↔(ℤ↔ℤ)");
		final Predicate hyp = parsePredicate("f ∈ ℤ→(ℤ → ℤ)");
		final Expression substitute = parseExpression("ℤ");
		assertSuccess(
				" ;H; ;S; f ∈ ℤ→(ℤ → ℤ) |- x∈dom(f(y))",
				funImgGoal(
						hyp,
						"1.0",
						totalDom(null, "1", substitute,
								typeRewrites(trueGoal()))));
	}

	/**
	 * Ensures that the tactic succeeds when dom appears on both sides of the
	 * membership predicate in the goal.
	 */
	@Test
	public void successWithDomOnBothSides() {
		final Expression substitute = parseExpression("ℙ(ℤ)");
		assertSuccess(" ;H; ;S; f ∈ ℙ(ℤ) → ℤ|- dom({1↦1})∈dom(f)",
				totalDom(null, "1", substitute, typeRewrites(trueGoal())));
	}

	/**
	 * Ensures that the tactic fails when the domain expression is not top level
	 */
	@Test
	public void notTopLevel() {
		assertFailure(" ;H; ;S; f ∈ ℤ → ℤ|- x=0 ⇒ x∈dom(f)");
	}

	/**
	 * Reproduces bug 3116665.
	 */
	@Test
	public void bug3116665() {
		// when the bug is present, the following line throws an AssertionError
		assertFailure(";H; ;S; |- v ∈ dom(dom({1↦1↦1}))");
	}

}
