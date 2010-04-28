/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - fixed implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.assertRulesApplied;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.disjE;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funOvr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>FunOvrHypTac</code>.
 * 
 * @author Laurent Voisin
 */
public class FunOvrHypTacTests {

	private static final ITactic tac = new AutoTactics.FunOvrHypTac();

	private static final String TAC_ID = "org.eventb.core.seqprover.funOvrHypTac";

	private static void assertFailure(IProofTreeNode node) {
		assertNotNull(tac.apply(node, null));
		assertRulesApplied(node, empty);
	}

	private static void assertSuccess(IProofTreeNode node, TreeShape expected) {
		assertNull(tac.apply(node, null));
		assertRulesApplied(node, expected);
	}

	private static IProofTree genProofTree(String[] bgHyps, String[] selHyps,
			String goal) {
		final IProverSequent seq = genSeq(bgHyps, selHyps, goal);
		return ProverFactory.makeProofTree(seq, null);
	}

	private static String[] l(String... preds) {
		return preds;
	}

	private static IProverSequent genSeq(String[] bgHyps, String[] selHyps,
			String goal) {
		final StringBuilder b = new StringBuilder();
		genHypList(b, bgHyps);
		if (bgHyps.length != 0 && selHyps.length != 0) {
			b.append(";;");
		}
		genHypList(b, selHyps);
		b.append(" ;H;   ;S; ");
		genHypList(b, selHyps);
		b.append("|-");
		b.append(goal);
		return TestLib.genFullSeq(b.toString());
	}

	private static void genHypList(final StringBuilder b, String[] hyps) {
		String sep = "";
		for (String s : hyps) {
			b.append(sep);
			sep = ";;";
			b.append(s);
		}
	}

	private static Predicate parsePredicate(String predStr, IProofTree pt) {
		final Predicate pred = Lib.parsePredicate(predStr);
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
	 * Ensures that the tactic fails when it is not applicable.
	 */
	@Test
	public void notApplicable() {
		final IProofTree pt = genProofTree( //
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"x ∈ ℤ", //
						"f(x) ∈ ℕ"), //
				l(), //
				"⊥");
		assertFailure(pt.getRoot());
	}

	/**
	 * Ensures that the tactic fails when it is not applicable to any selected
	 * hypothesis, even when it is applicable to an hypothesis that is not
	 * selected.
	 */
	@Test
	public void notApplicableSelected() {
		final IProofTree pt = genProofTree( //
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"x ∈ ℤ", //
						"(fg)(x) ∈ ℕ"), //
				l(), //
				"⊥");
		assertFailure(pt.getRoot());
	}

	/**
	 * Ensures that the tactic succeeds when applicable once.
	 */
	@Test
	public void simpleApplication() {
		final String hypStr = "(fg)(x) ∈ ℕ";
		final IProofTree pt = genProofTree(//
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"x ∈ ℤ"), //
				l(hypStr), //
				"⊥");
		final Predicate hyp = parsePredicate(hypStr, pt);
		assertSuccess(pt.getRoot(), funOvr(hyp, "0", empty, empty));
	}

	/**
	 * Ensures that the tactic is applied once.
	 */
	@Test
	public void onceApplication() {
		final String hyp1Str = "(fgh)(x) ∈ ℕ";
		final IProofTree pt = genProofTree(//
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"h ∈ ℤ → ℤ", //
						"x ∈ ℤ"), //
				l(hyp1Str), //
				"⊥");
		final Predicate hyp1 = parsePredicate(hyp1Str, pt);
		assertSuccess(pt.getRoot(), //
				funOvr(hyp1, "0", //
						empty, empty));
	}

	/**
	 * Ensures that the tactic is applied recursively when possible.
	 */
	@Test
	public void recursiveApplication() {
		final String hyp1Str = "(fg)(x) ∈ (hi)(x)";
		final String hyp2Str = "g(x) ∈ (hi)(x)";
		final String hyp3Str = "(dom(g) ⩤ f)(x)∈(hi)(x)";
		final IProofTree pt = genProofTree(//
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"h ∈ ℤ → ℙ(ℤ)", //
						"i ∈ ℤ → ℙ(ℤ)", //
						"x ∈ ℤ"), //
				l(hyp1Str), //
				"⊥");
		final Predicate hyp1 = parsePredicate(hyp1Str, pt);
		final Predicate hyp2 = parsePredicate(hyp2Str, pt);
		final Predicate hyp3 = parsePredicate(hyp3Str, pt);
		assertSuccess(pt.getRoot(), funOvr(hyp1, "0", //
				funOvr(hyp2, "1", empty, empty), //
				funOvr(hyp3, "1", empty, empty)));
	}

	/**
	 * Ensures that the tactic doesn't modify anything out of the subtree where
	 * it is applied.
	 */
	@Test
	public void subtree() {
		final String hypStr = "(fg)(x) ∈ ℕ ∨ (hi)(x) ∈ ℕ";
		final IProofTree pt = genProofTree(//
				l("f ∈ ℤ → ℤ", //
						"g ∈ ℤ → ℤ", //
						"h ∈ ℤ → ℤ", //
						"i ∈ ℤ → ℤ", //
						"x ∈ ℤ"), //
				l(hypStr), //
				"⊥");
		final IProofTreeNode root = pt.getRoot();
		final Predicate hyp = parsePredicate(hypStr, pt);
		Tactics.disjE(hyp).apply(root, null);
		final IProofTreeNode left = root.getChildNodes()[0];
		final Predicate leftHyp = ((AssociativePredicate) hyp).getChildren()[0];
		final TreeShape sub = funOvr(leftHyp, "0", empty, empty);
		assertSuccess(left, sub);
		assertRulesApplied(root, disjE(hyp, sub, empty));
	}

}
