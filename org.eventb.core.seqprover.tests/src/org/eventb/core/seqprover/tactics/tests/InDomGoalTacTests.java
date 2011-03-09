/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.trueGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.typeRewrites;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>TotalDomRewritesTac</code>.
 */
public class InDomGoalTacTests {

	private static final ITactic tac = new AutoTactics.InDomGoalTac();

	private static final String TAC_ID = "org.eventb.core.seqprover.InDomGoalTac";

	private static void assertSuccess(IProofTreeNode node, TreeShape expected) {
		TreeShape.assertSuccess(node, expected, tac);
	}

	private static void assertFailure(IProofTreeNode node) {
		TreeShape.assertFailure(node, tac);
	}

	private static IProofTree genProofTree(String... preds) {
		final IProverSequent seq = genSeq(preds);
		return ProverFactory.makeProofTree(seq, null);
	}

	private static IProverSequent genSeq(String... preds) {
		final int nbHyps = preds.length - 1;
		final StringBuilder b = new StringBuilder();
		String sep = "";
		for (int i = 0; i < nbHyps; i++) {
			b.append(sep);
			sep = ";;";
			b.append(preds[i]);
		}
		b.append("|-");
		b.append(preds[nbHyps]);
		return TestLib.genSeq(b.toString());
	}

	private static Expression parseExpression(String exprStr, IProofTree pt) {
		final DLib lib = mDLib(pt.getRoot().getFormulaFactory());
		final Expression expr = lib.parseExpression(exprStr);
		final IProverSequent sequent = pt.getSequent();
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final ITypeCheckResult tcResult = expr.typeCheck(typeEnvironment);
		assertTrue(tcResult.isSuccess());
		return expr;
	}
	
	private static Predicate parsePredicate(String predStr, IProofTree pt) {
		final DLib lib = mDLib(pt.getRoot().getFormulaFactory());
		final Predicate pred = lib.parsePredicate(predStr);
		final IProverSequent sequent = pt.getSequent();
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final ITypeCheckResult tcResult = pred.typeCheck(typeEnvironment);
		assertTrue(tcResult.isSuccess());
		return pred;
	}

	/**
	 * Ensures that the tactic is correctly registered with the sequent prover.
	 */
	@Test
	public void tacticRegistered() {
		final IAutoTacticRegistry registry = SequentProver
				.getAutoTacticRegistry();
		final ITacticDescriptor desc = registry.getTacticDescriptor(TAC_ID);
		assertNotNull(desc);
		assertEquals(tac.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Ensures that the tactic succeeds using true goal tactic
	 */
	@Test
	public void successWithTrueGoal() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"x∈dom(f)" //
		);
		String substituteString = "ℤ";
		Expression substitute = parseExpression(substituteString, pt);

		assertSuccess(pt.getRoot(),
				totalDom(null, "1", substitute, typeRewrites(trueGoal())));
	}

	/**
	 * Ensures that the tactic succeeds using hyp tactic.
	 */
	@Test
	public void successWithHyp() {
		final IProofTree pt = genProofTree(//
				"g ∈ ℤ → ℤ", //
				"0 ∈ dom(g)", //
				"f ∈ dom(g)→ ℤ", //
				"0 ∈ dom(f)" //
		);
		String substituteString = "dom(g)";
		Expression substitute = parseExpression(substituteString, pt);
		assertSuccess(pt.getRoot(), totalDom(null,"1",substitute,hyp()));
	}
	
	/**
	 * Ensures that the tactic succeeds 
	 */
	@Test
	public void successWithFunAppInDom() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ→(ℤ → ℤ)", //
				"x∈dom(f(y))"
		);
		String substituteString = "ℤ";
		String hypString = "f ∈ ℤ→(ℤ → ℤ)";
		Predicate hyp = parsePredicate(hypString, pt);
		Expression substitute = parseExpression(substituteString, pt);
		assertSuccess(pt.getRoot(), funImgGoal(hyp,"1.0",totalDom(null,"1",substitute,typeRewrites(trueGoal()))));
	}

	/**
	 * Ensures that the tactic fails when the domain expression is not top level
	 */
	@Test
	public void notTopLevel() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"x=0 ⇒ x∈dom(f)" //
		);
		assertFailure(pt.getRoot());
	}

	/**
	 * Reproduces bug 3116665.
	 */
	@Test
	public void bug3116665() {
		final IProofTree pt = genProofTree(//
				"v ∈ dom(dom({1↦1↦1}))"
		);
		// when the bug is present, the following line throws an AssertionError
		tac.apply(pt.getRoot(), null);
	}

}
