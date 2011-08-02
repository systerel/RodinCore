/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.genProofTree;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rn;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 *
 * Unit tests for the auto tactic NnfrewritesAutoTac
 */
public class NNFRewritesAutoTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.NNFTac";
	private static final ITactic TAC = new AutoTactics.NNFRewritesAutoTac();

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		TacticTestUtils.assertTacticRegistered(TAC_ID, TAC);
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds once on both Hypothesis
	 * and Goal which contains :
	 * <ul>
	 *  <li>a disjunction</li>
	 *  <li>a conjunction</li>
	 *  <li>an implication</li>
	 *  <li>an existential quantifier</li>
	 *  <li>a universal quantifier</li>
	 *  <li>a double negation</li>
	 *  </ul>
	 */
	@Test
	public void applyOnce() {
		final Predicate pOr = genPred("¬(1=1 ∨ 2=2)");
		assertSuccessHyp(pOr, rn(pOr, "", empty));
		assertSuccessGoal(pOr, rn("", empty, empty));

		final Predicate pAnd = genPred("¬(1=1 ∧ 2=2)");
		assertSuccessHyp(pAnd, rn(pAnd, "", empty));
		assertSuccessGoal(pAnd, rn("", empty));

		final Predicate pImp = genPred("¬(1=1 ⇒ 2=2)");
		assertSuccessHyp(pImp, rn(pImp, "", empty));
		assertSuccessGoal(pImp, rn("", empty, empty));

		final Predicate pExists = genPred("¬(∃x·x∈ℤ)");
		assertSuccessHyp(pExists, rn(pExists, "", empty));
		assertSuccessGoal(pExists, rn("", empty));

		final Predicate pForall = genPred("¬(∀x·x∈ℤ)");
		assertSuccessHyp(pForall, rn(pForall, "", empty));
		assertSuccessGoal(pForall, rn("", empty));

		final Predicate pNot = genPred("¬¬1=1");
		assertSuccessHyp(pNot, rn(pNot, "", empty));
		assertSuccessGoal(pNot, rn("", empty));

	}

	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds many times on both
	 * hypothesis and goal in those configuration :
	 * <ul>
	 * <li>recursively on one predicate</li>
	 * <li>on one predicate containing two sub-predicates on which the tactic
	 * can be applied</li>
	 * </ul>
	 */
	@Test
	public void applyMany() {
		final Predicate pRec = genPred("¬(1=1 ∧ ¬2=2)");
		final Predicate pRecChild = genPred("¬1=1 ∨ ¬¬2=2");
		assertSuccessHyp(pRec, rn(pRec, "", rn(pRecChild ,"1", empty)));
		assertSuccessGoal(pRec, rn("", rn("1", empty)));

		final Predicate pMany = genPred("¬(1=1 ∧ 2=2) ∨ ¬¬3=3");
		final Predicate pManyChild = genPred("(¬1=1 ∨ ¬2=2) ∨ ¬¬3=3");
		assertSuccessHyp(pMany, rn(pMany, "0", rn(pManyChild ,"1", empty)));
		assertSuccessGoal(pMany, rn("0", rn("1", empty)));
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic succeeds on hybrid
	 * configurations :
	 * <ul>
	 * <li>When there are two predicate in the hypothesis (impossible for the
	 * goal)</li>
	 * <li>When there are one predicate in the hypothesis and one in the goal</li>
	 * </ul>
	 */
	@Test
	public void applyHybrid() {
		final Predicate pred1 = genPred("¬(1=1 ∧ 2=2)");
		final Predicate pred2 = genPred("¬¬3=3");
		assertSuccess(rn(pred1, "", rn(pred2, "", empty)), pred1, pred2, FALSE);
		assertSuccess(rn(pred1, "", rn("", empty)), pred1, pred2);
	}

	/**
	 * Ensures that the NnfRewrites auto-tactic fails on both Hypothesis and
	 * Goal which contains :
	 * <ul>
	 * <li>predicate which contains a rule that is implemented in the underlying
	 * reasoner but shall not be used (¬S=∅, 1=1⇔2=2)</li>
	 * <li>no ¬ (e.g. 1=1 ∧ 2=2)</li>
	 * <li>a ¬ that cannot be removed or pushed in its children (e.g. ¬1=1)</li>
	 * </ul>
	 */
	@Test
	public void failOnce() {
		final String pDSNE = "¬ S=(∅⦂ℙ(ℤ))"; // DEF_SPECIAL_NOT_EQUAL
		assertFailureHyp(pDSNE);
		assertFailureGoal(pDSNE);

		final String pDSNEswitch = "¬ (∅⦂ℙ(ℤ))=S"; // Expressions' place switch
		assertFailureHyp(pDSNEswitch);
		assertFailureGoal(pDSNEswitch);

		final String pEqv = "¬(1=1 ⇔ 2=2)";
		assertFailureHyp(pEqv);
		assertFailureGoal(pEqv);

		final String pNoChange = "(1=1 ∧ 2=2)";
		assertFailureHyp(pNoChange);
		assertFailureGoal(pNoChange);

		final String pNotExp = "¬1=1";
		assertFailureHyp(pNotExp);
		assertFailureGoal(pNotExp);
	}

	//************************************************************************
	//
	//						  Assert Functions
	//
	//************************************************************************

	private static final Predicate FALSE = TestLib.ff.makeLiteralPredicate(
			BFALSE, null);

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * hypothesis (the goal being ⊥) succeeds and that the resulting tree shape
	 * is equal to the given tree shape.
	 * 
	 * @param hyp
	 *            the considered hypothesis
	 * @param shape
	 *            the expected tree shape
	 */
	private static void assertSuccessHyp(final Predicate hyp,
			final TreeShape shape) {
		assertSuccess(shape, hyp, FALSE);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * goal succeeds and that the resulting tree shape is equal to the given
	 * tree shape.
	 * 
	 * @param goal
	 *            the considered goal
	 * @param shape
	 *            the expected tree shape
	 */
	private static void assertSuccessGoal(final Predicate goal,
			final TreeShape shape) {
		assertSuccess(shape, goal);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of the
	 * given sequent succeeds and that the resulting tree shape is equal to the
	 * given tree shape.
	 * 
	 * @param predicates
	 *            selected hypotheses and goal of the sequent
	 * @param shape
	 *            the expected tree shape
	 */
	private static void assertSuccess(final TreeShape shape,
			final Predicate... predicates) {
		TacticTestUtils.assertSuccess(genSeq(predicates), shape, TAC);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * hypothesis (the goal being ⊥) fails and does not modify the proof tree.
	 * 
	 * @param predStr
	 *            the considered hypothesis as a String
	 */
	private static void assertFailureHyp(final String predStr) {
		final IProofTree pt = genProofTree(predStr, "⊥");
		TacticTestUtils.assertFailure(pt.getRoot(), TAC);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * goal fails and does not modify the proof tree.
	 * 
	 * @param predStr
	 *            the considered goal as a String
	 */
	private static void assertFailureGoal(final String predStr) {
		final IProofTree pt = genProofTree(predStr);
		TacticTestUtils.assertFailure(pt.getRoot(), TAC);
	}

}
