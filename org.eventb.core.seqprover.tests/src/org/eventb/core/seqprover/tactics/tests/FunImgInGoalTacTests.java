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
		assertSuccess(";H; ;S; f ∈ ℤ→(ℤ → ℤ)|- f(y)∈ℤ ⇸ ℤ",
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
		assertSuccess(" ;H; ;S; f ∈ ℤ→(ℤ→(ℤ → ℤ)) |- f(y)(x)∈ℤ ⇸ ℤ",
				funImgGoal(hyp1, "0.0", funImgGoal(hyp2, "0", isFunGoal())));
	}

	/**
	 * Ensures that the tactic succeeds using hyp.
	 */
	@Test
	public void successWithHyp() {
		final Predicate hyp = parsePredicate("f ∈ ℤ→(ℤ→(ℤ → ℤ))");
		assertSuccess(" ;H; ;S; f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;; f(y)(x) ∈ ℤ → ℤ "
				+ "|- f(y)∈ℤ→(ℤ → ℤ)", funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using a relational property.
	 */
	@Test
	public void successWithRelation() {
		final Predicate hyp = parsePredicate("f ∈ ℤ↔ℤ");
		assertSuccess(" ;H; ;S; f ∈ ℤ↔ℤ |- f(x) ∈ ℤ",
				funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using the suitable hyp.
	 */
	@Test
	public void successWithSuitableHyp() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔T");
		final Predicate hypA = parsePredicate("f∈S ⇸ A");
		final Predicate hypB = parsePredicate("f∈S ⇸ B");
		assertSuccess(" ;H; ;S; f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈B",
				funImgGoal(hypA, "0", funImgGoal(hypB, "0", hyp())));
	}

	/**
	 * Ensures that the tactic succeeds when the goal contains nested
	 * applications of the same function.
	 */
	@Test
	public void successWithNestedFunAppsInGoal() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔S");
		final Predicate hypA = parsePredicate("f∈S ⇸ A");
		assertSuccess(" ;H; ;S; f∈S ⇸ A |- f(f(x))∈A",
				funImgGoal(hypA, "0.1", funImgGoal(hypA, "0", hyp())));
	}

	/**
	 * Ensures that the tactic succeeds when the goal contains two successive
	 * applications of the same function but to different arguments.
	 */
	@Test
	public void successWithSuccessiveFunAppsInGoal() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔T");
		final Predicate hypA = parsePredicate("f∈S ⇸ {f(a)}");
		assertSuccess(" ;H; ;S;f∈S ⇸ {f(a)}|- f(x)∈{f(a)}",
				funImgGoal(hypA, "1.0", funImgGoal(hypA, "0", hyp())));
	}

	/**
	 * Ensures that the tactic succeeds when the goal contains two successive
	 * applications of the same function but to different arguments, and the
	 * first is deeper than the second.
	 */
	@Test
	public void successWithSuccessiveFunAppsFirstDeeper() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), U=ℙ(U), f=S↔(T↔U)");
		final Predicate hypA = parsePredicate("f∈S ⇸ (T↔ran(f(a)))");
		final Predicate hypB = parsePredicate("f(a)∈T↔ran(f(a))");
		assertSuccess(
				" ;H; ;S; f∈S ⇸ (T↔ran(f(a)))|- f(a)(b)∈ran(f(a))",
				funImgGoal(hypA, "1.0",
						funImgGoal(hypA, "0.0", funImgGoal(hypB, "0", hyp()))));
	}

	/**
	 * Ensures that the tactic succeeds when the goal contains two successive
	 * applications of the same function but to different arguments, and the
	 * second is deeper than the first.
	 */
	@Test
	public void successWithSuccessiveFunAppsSecondDeeper() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), U=ℙ(U), f=S↔(T↔U)");
		final Predicate hypA = parsePredicate("f∈S ⇸ {{a↦f(b)(c)}}");
		assertSuccess(" ;H;" + ";S; f∈S ⇸ {{a↦f(b)(c)}} |- f(b)∈{{a↦f(b)(c)}}",
				funImgGoal(hypA, "1.0.0.1.0", funImgGoal(hypA, "0", hyp())));
	}

	/**
	 * Ensures that the tactic fails.
	 */
	@Test
	public void failure() {
		assertFailure(" ;H; ;S; f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;; f(y)(x) ∈ ℤ → ℤ"
				+ " |- g(y)(x) ∈ ℤ → ℤ");
	}

	/**
	 * Ensures that the tactic fails when functions in hypotheses does not match
	 * the set of the goal.
	 */
	@Test
	public void failureWithFunctionalHyps() {
		addToTypeEnvironment("S=ℙ(S), T=ℙ(T), f=S↔T");
		assertFailure(" ;H; ;S; f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈C");
	}

}