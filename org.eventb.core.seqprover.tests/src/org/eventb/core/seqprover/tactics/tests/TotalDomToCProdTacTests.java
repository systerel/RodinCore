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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;
import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the tactic TotalDomToCProdTac, which substitutes a domain by a
 * Cartesian product in the goal. The goal must be as follow :  <code>x↦y∈dom(g)</code>
 * 
 * @author Emmanuel Billaud
 */
public class TotalDomToCProdTacTests {

	private static final String TAC_ID = "org.eventb.core.seqprover.totalDomToCProdTac";
	private static final ITactic TAC = new AutoTactics.TotalDomToCProdAutoTac();

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		TacticTestUtils.assertTacticRegistered(TAC_ID, TAC);
	}

	/**
	 * Assert that auto-tactic succeeds once when the relation is :
	 * <ul>
	 * <li>a total relation : </li>
	 * <li>a total surjective relation : </li>
	 * <li>a total function : →</li>
	 * <li>a total injection : ↣</li>
	 * <li>a total surjection : ↠</li>
	 * <li>a bijection : ⤖</li>
	 * </ul>
	 */
	@Test
	public void applyOnce() {
		final ITypeEnvironment typeEnv = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), y=ℤ, x=ℤ");
		final Predicate goal = genPred(typeEnv, "x ↦ y∈dom(g)");
		final Expression expr = genExpr(typeEnv, "ℤ×ℤ");
		final TreeShape totalDom = totalDom(null, "1", expr, empty);

		final Predicate hypTREL = genPred("g∈ℤ×ℤℤ");
		assertSucceeded(goal, hypTREL, totalDom);

		final Predicate hypSTREL = genPred("g∈ℤ×ℤℤ");
		assertSucceeded(goal, hypSTREL, totalDom);

		final Predicate hypTFUN = genPred("g∈ℤ×ℤ→ℤ");
		assertSucceeded(goal, hypTFUN, totalDom);

		final Predicate hypTINJ = genPred("g∈ℤ×ℤ↣ℤ");
		assertSucceeded(goal, hypTINJ, totalDom);

		final Predicate hypTSUR = genPred("g∈ℤ×ℤ↠ℤ");
		assertSucceeded(goal, hypTSUR, totalDom);

		final Predicate hypTBIJ = genPred("g∈ℤ×ℤ⤖ℤ");
		assertSucceeded(goal, hypTBIJ, totalDom);
	}

	/**
	 * Assert that auto-tactic fails when the operator op in the sequent
	 * <code>g∈ℤ×ℤ op ℤ ⊦ x ↦ y∈dom(g)</code> is :
	 * <ul>
	 * <li>a relation : ↔</li>
	 * <li>a surjective relation : </li>
	 * <li>a partial function : ⇸</li>
	 * <li>a partial injection : ⤔</li>
	 * <li>a partial surjection : ⤀</li>
	 * </ul>
	 */
	@Test
	public void fails() {
		final ITypeEnvironment typeEnv = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), y=ℤ, x=ℤ");
		final Predicate goal = genPred(typeEnv, "x ↦ y∈dom(g)");

		final Predicate hypREL = genPred("g∈ℤ×ℤ↔ℤ");
		assertFailed(goal, hypREL);

		final Predicate hypSREL = genPred("g∈ℤ×ℤℤ");
		assertFailed(goal, hypSREL);

		final Predicate hypPFUN = genPred("g∈ℤ×ℤ⇸ℤ");
		assertFailed(goal, hypPFUN);

		final Predicate hypPINJ = genPred("g∈ℤ×ℤ⤔ℤ");
		assertFailed(goal, hypPINJ);

		final Predicate hypPSUR = genPred("g∈ℤ×ℤ⤀ℤ");
		assertFailed(goal, hypPSUR);

	}

	/**
	 * Assert that the auto-tactic fails when the goal is incorrect :
	 * <ul>
	 * <li>it is not an inclusion</li>
	 * <li>left member of the inclusion is not a mapplet</li>
	 * <li>right member of the inclusion is not a domain</li>
	 * </ul>
	 */
	@Test
	public void failsOnGoal() {
		final Predicate hypTBIJ = genPred("g∈ℤ×ℤ×ℤ⤖ℤ");

		final ITypeEnvironment typeEnv1 = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), y=ℤ, x=ℤ");
		final Predicate goal1 = genPred(typeEnv1, "x ↦ y∉dom(g)");
		assertFailed(goal1, hypTBIJ);

		final ITypeEnvironment typeEnv2 = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), f=ℤ×ℤ");
		final Predicate goal2 = genPred(typeEnv2, "f∈dom(g)");
		assertFailed(goal2, hypTBIJ);

		final ITypeEnvironment typeEnv3 = genTypeEnv("y=ℤ, x=ℤ");
		final Predicate goal3 = genPred(typeEnv3, "x ↦ y∈ℤ×ℤ");
		assertFailed(goal3, hypTBIJ);
	}

	/**
	 * Assert that auto-tactic fails when :
	 * <ul>
	 * <li>g is not explicitly set as a relation</li>
	 * <li>dom(g) cannot be substituted by a Cartesian product</li>
	 * </ul>
	 * 
	 */
	@Test
	public void failsOnHypothesis() {
		final ITypeEnvironment typeEnv1 = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), A=ℙ(ℙ(ℤ×ℤ×ℤ)), y=ℤ, x=ℤ");
		final Predicate goal1 = genPred(typeEnv1, "x ↦ y∈dom(g)");
		final Predicate hyp11 = genPred(typeEnv1, "A=ℤ×ℤ⤖ℤ");
		final Predicate hyp12 = genPred(typeEnv1, "g∈A");
		final Predicate hyp13 = genPred(typeEnv1, "dom(g)=ℤ×ℤ");

		final IProofTree pt1 = genProofTree(hyp11.toString(), hyp12.toString(),
				hyp13.toString(), goal1.toString());
		assertFailure(pt1.getRoot(), TAC);

		final ITypeEnvironment typeEnv2 = genTypeEnv("g=ℙ(ℤ×ℤ×ℤ), A=ℙ(ℤ×ℤ), y=ℤ, x=ℤ");
		final Predicate goal2 = genPred(typeEnv2, "x ↦ y∈dom(g)");
		final Predicate hyp21 = genPred(typeEnv2, "A=ℤ×ℤ");
		final Predicate hyp22 = genPred(typeEnv2, "g∈A⤖ℤ");

		final IProofTree pt2 = genProofTree(hyp21.toString(), hyp22.toString(),
				goal2.toString());
		assertFailure(pt2.getRoot(), TAC);
	}

	/**
	 * Assert that the application of the TotalDomToCProdTac on a node made up
	 * of one goal and one hypothesis return <code>null</code> and that the
	 * resulting tree shape is equal to the given <code>shape</code> tree shape.
	 * 
	 * @param goal
	 *            the considered goal
	 * @param hyp
	 *            the considered hypothesis
	 * @param shape
	 *            the expected tree shape
	 */
	private void assertSucceeded(final Predicate goal, final Predicate hyp,
			final TreeShape shape) {
		final IProofTree pt = genProofTree(hyp.toString(), goal.toString());
		assertSuccess(pt.getRoot(), shape, TAC);
	}

	/**
	 * Assert that the application of the TotalDomToCProdTac on a node made up
	 * of one goal and one hypothesis does not return <code>null</code>.
	 * 
	 * @param goal
	 *            the considered goal
	 * @param hyp
	 *            the considered hypothesis
	 */
	private void assertFailed(final Predicate goal, final Predicate hyp) {
		final IProofTree pt = genProofTree(hyp.toString(), goal.toString());
		assertFailure(pt.getRoot(), TAC);
	}

}