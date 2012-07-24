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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

public class FunImgInGoalTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.FunImgInGoalTac";
	private static final ITactic tac = new AutoTactics.FunImgInGoalTac();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Ensures that the tactic is correctly registered with the sequent prover.
	 */
	@Test
	public void tacticRegistered() {
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ITacticDescriptor desc = reg.getTacticDescriptor(TAC_ID);
		assertNotNull(desc);
		assertEquals(tac.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Ensures that the tactic succeeds using isFunGoal.
	 */
	@Test
	public void successWithIsFunGoal() {
		final TacticTestHelper tester = new TacticTestHelper(ff, "", tac);
		final Predicate hyp = tester.parsePredicate("f ∈ ℤ→(ℤ → ℤ)");
		tester.assertSuccess("f ∈ ℤ→(ℤ → ℤ) |- f(y)∈ℤ ⇸ ℤ",
				funImgGoal(hyp, "0", isFunGoal()));
	}

	/**
	 * Ensures that the tactic succeeds by adding two hypotheses and using
	 * isFunGoal.
	 */
	@Test
	public void successWithDoubleFunApp() {
		final TacticTestHelper tester = new TacticTestHelper(ff, "y=ℤ", tac);
		final Predicate hyp1 = tester.parsePredicate("f ∈ ℤ→(ℤ→(ℤ → ℤ))");
		final Predicate hyp2 = tester.parsePredicate("f(y) ∈ ℤ→(ℤ → ℤ)");
		tester.assertSuccess("f ∈ ℤ→(ℤ→(ℤ → ℤ)) |- f(y)(x)∈ℤ ⇸ ℤ",
				funImgGoal(hyp1, "0.0", funImgGoal(hyp2, "0", (isFunGoal()))));
	}

	/**
	 * Ensures that the tactic succeeds using hyp.
	 */
	@Test
	public void successWithHyp() {
		final TacticTestHelper tester = new TacticTestHelper(ff, "", tac);
		final Predicate hyp = tester.parsePredicate("f ∈ ℤ→(ℤ→(ℤ → ℤ))");
		tester.assertSuccess(
				"f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;; f(y)(x) ∈ ℤ → ℤ |- f(y)∈ℤ→(ℤ → ℤ)", //
				funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using a relational property.
	 */
	@Test
	public void successWithRelation() {
		final TacticTestHelper tester = new TacticTestHelper(ff, "", tac);
		final Predicate hyp = tester.parsePredicate("f ∈ ℤ↔ℤ");
		tester.assertSuccess("f ∈ ℤ↔ℤ |- f(x) ∈ ℤ", funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic succeeds using the suitable hyp.
	 */
	@Test
	public void successWithSuitableHyp() {
		final TacticTestHelper tester = new TacticTestHelper(ff,
				"f=ℙ(S×T), T=ℙ(T), A=ℙ(T), B=ℙ(T), S=ℙ(S), x=S", tac);
		final Predicate hypA = tester.parsePredicate("f∈S ⇸ A");
		final Predicate hypB = tester.parsePredicate("f∈S ⇸ B");
		tester.assertSuccess("f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈B",
				funImgGoal(hypA, "0", funImgGoal(hypB, "0", hyp())));
	}

	/**
	 * Ensures that the tactic fails when functions in hypotheses does not match
	 * the set of the goal.
	 */
	@Test
	public void failureWithFunctionalHyps() {
		final TacticTestHelper tester = new TacticTestHelper(ff,
				"f=ℙ(S×T), T=ℙ(T), A=ℙ(T), B=ℙ(T), C=ℙ(T), S=ℙ(S), x=S", tac);
		tester.assertFailure("f∈S ⇸ A ;; f∈S ⇸ B |- f(x)∈C");
	}

	/**
	 * Ensures that the tactic fails.
	 */
	@Test
	public void failure() {
		final TacticTestHelper tester = new TacticTestHelper(ff, "", tac);
		tester.assertFailure("f ∈ ℤ→(ℤ→(ℤ → ℤ)) ;;"
				+ "f(y)(x) ∈ ℤ → ℤ |- g(y)(x) ∈ ℤ → ℤ");
	}

}
