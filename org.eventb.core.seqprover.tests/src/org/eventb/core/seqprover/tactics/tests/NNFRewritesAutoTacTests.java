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

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertFailure;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.genProofTree;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rn;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * @author Emmanuel Billaud
 *
 * Unit tests for the auto tactic NnfrewritesAutoTac
 */
public class NNFRewritesAutoTacTests {

	private static final ITactic tac = new AutoTactics.NNFRewritesAutoTac();
	private static final String TAC_ID = "org.eventb.core.seqprover.NNFTac";

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		final String[] tacticIds =  {TAC_ID};
		final ITactic[] tactics = { tac };
		TacticTestUtils.assertTacticsRegistered(tacticIds, tactics);
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

		final IProofTree pt2Pred = genProofTree(//
				pred1.toString(),// hyp1
				pred2.toString(),// hyp2
				"⊥"); // goal
		assertSuccess(pt2Pred.getRoot(), rn(pred1, "",rn(pred2, "", empty)), tac);

		final IProofTree pt1_1 = genProofTree(//
				pred1.toString(),// hyp
				pred2.toString());// goal
		assertSuccess(pt1_1.getRoot(), rn(pred1, "",rn("", empty)), tac);
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
		assertFailsHyp(pDSNE);
		assertFailsGoal(pDSNE);

		final String pDSNEswitch = "¬ (∅⦂ℙ(ℤ))=S"; // Expressions' place switch
		assertFailsHyp(pDSNEswitch);
		assertFailsGoal(pDSNEswitch);

		final String pEqv = "¬(1=1 ⇔ 2=2)";
		assertFailsHyp(pEqv);
		assertFailsGoal(pEqv);

		final String pNoChange = "(1=1 ∧ 2=2)";
		assertFailsHyp(pNoChange);
		assertFailsGoal(pNoChange);

		final String pNotExp = "¬1=1";
		assertFailsHyp(pNotExp);
		assertFailsGoal(pNotExp);
	}

	//************************************************************************
	//
	//						  Assert Functions
	//
	//************************************************************************

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * hypothesis (the goal is ⊥) returns <code>null</code> and that the 
	 * resulting tree shape is equal to the given <code>shape</code> tree shape.
	 * 
	 * @param hyp
	 * 			the considered hypothesis
	 * @param shape
	 * 			the expected tree shape
	 */
	private void assertSuccessHyp(final Predicate hyp, final TreeShape shape) {
		final IProofTree pt = genProofTree(//
				hyp.toString(),// hyp
				"⊥" // goal
		);
		assertSuccess(pt.getRoot(), shape, tac);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * goal returns <code>null</code> and that the resulting tree shape is equal
	 * to the given <code>shape</code> tree shape.
	 * 
	 * @param goal
	 * 			the considered goal
	 * @param shape
	 * 			the expected tree shape
	 */
	private void assertSuccessGoal(final Predicate goal, final TreeShape shape) {
		final IProofTree pt = genProofTree(//
				goal.toString()// Goal
		);
		assertSuccess(pt.getRoot(), shape, tac);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * hypothesis (the goal is ⊥) does not return <code>null</code>.
	 * 
	 * @param predStr
	 * 			the considered hypothesis in String
	 */
	private void assertFailsHyp(final String predStr) {
		final IProofTree pt = genProofTree(
				predStr,// hyp
				"⊥" // goal
				);
		assertFailure(pt.getRoot(), tac);
	}

	/**
	 * Assert that the application of the NnfRewriter on a node made up of one
	 * goal does not return <code>null</code>.
	 * 
	 * @param predStr
	 * 			the considered goal in String
	 */
	private void assertFailsGoal(final String predStr) {
		final IProofTree pt = genProofTree(
				predStr // goal
				);
		assertFailure(pt.getRoot(), tac);
	}
}
