/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.isFunGoal;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

public class FunImgInGoalTacTests extends AbstractTacticTests {

	public FunImgInGoalTacTests() {
		super(new AutoTactics.FunImgInGoalTac(),
				"org.eventb.core.seqprover.FunImgInGoalTac");
	}

	/**
	 * Ensures that the tactic succeeds using isFunGoal.
	 */
	@Test
	public void successWithIsFunGoal() {
		final Predicate hyp = parsePredicate("f ∈ ℤ→(ℤ → ℤ)");
		assertSuccess("f ∈ ℤ→(ℤ → ℤ) |- f(y)∈ℤ ⇸ ℤ",
				funImgGoal(hyp, "0", isFunGoal()));
	}

	/**
	 * Ensures that the tactic succeeds by adding two hypotheses and using
	 * isFunGoal.
	 */
	@Test
	public void successWithDoubleFunApp() {
		addToTypeEnvironment("y=ℤ");
		final Predicate hyp1 = parsePredicate("f ∈ ℤ→(ℤ→(ℤ → ℤ))");
		final Predicate hyp2 = parsePredicate("f(y) ∈ ℤ→(ℤ → ℤ)");
		assertSuccess("f ∈ ℤ→(ℤ→(ℤ → ℤ)) |- f(y)(x)∈ℤ ⇸ ℤ",
				funImgGoal(hyp1, "0.0", funImgGoal(hyp2, "0", isFunGoal())));
	}

	/**
	 * Ensures that the tactic succeeds using hyp.
	 */
	@Test
	public void successWithHyp() {
		final Predicate hyp = parsePredicate("f ∈ ℤ→(ℤ→(ℤ → ℤ))");
		assertSuccess("f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;; f(y)(x) ∈ ℤ → ℤ |- f(y)∈ℤ→(ℤ → ℤ)",
				funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using a relational property.
	 */
	@Test
	public void successWithRelation() {
		final Predicate hyp = parsePredicate("f ∈ ℤ↔ℤ");
		assertSuccess("f ∈ ℤ↔ℤ |- f(x) ∈ ℤ", funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using the suitable hyp.
	 */
	@Test
	public void successWithSuitableHyp() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔T");
		final Predicate hypA = parsePredicate("f∈S ⇸ A");
		final Predicate hypB = parsePredicate("f∈S ⇸ B");
		assertSuccess("f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈B",
				funImgGoal(hypA, "0", funImgGoal(hypB, "0", hyp())));
	}

	/**
	 * Ensures that the tactic fails when functions in hypotheses does not match
	 * the set of the goal.
	 */
	@Test
	public void failureWithFunctionalHyps() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔T");
		assertFailure("f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈C");
	}

	/**
	 * Ensures that the tactic fails.
	 */
	@Test
	public void failure() {
		assertFailure("f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;;"
				+ "f(y)(x) ∈ ℤ → ℤ |- g(y)(x) ∈ ℤ → ℤ");
	}

}
