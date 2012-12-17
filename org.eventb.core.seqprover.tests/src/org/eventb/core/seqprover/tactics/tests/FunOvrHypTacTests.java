/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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

import static org.eventb.core.seqprover.tactics.tests.TreeShape.disjE;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgSimp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funOvr;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>FunOvrHypTac</code>.
 * 
 * @author Laurent Voisin
 */
public class FunOvrHypTacTests extends AbstractTacticTests {

	public FunOvrHypTacTests() {
		super(new AutoTactics.FunOvrHypTac(),
				"org.eventb.core.seqprover.funOvrHypTac");
	}
	
	/**
	 * Ensures that the tactic fails when it is not applicable.
	 */
	@Test
	public void notApplicable() {
		assertFailure("f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; x ∈ ℤ ;H; ;S; f(x) ∈ ℕ |- ⊥");
	}

	/**
	 * Ensures that the tactic fails when it is not applicable to any selected
	 * hypothesis, even when it is applicable to an hypothesis that is not
	 * selected.
	 */
	@Test
	public void notApplicableSelected() {
		assertFailure("f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; x ∈ ℤ ;; (fg)(x) ∈ ℕ "
				+ ";H; ;S; |- ⊥");
	}

	/**
	 * Ensures that the tactic succeeds when applicable once.
	 */
	@Test
	public void simpleApplication() {
		addToTypeEnvironment("x=ℤ");
		final Predicate hyp = parsePredicate("(fg)(x) ∈ ℕ");
		assertSuccess(
				"f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; x ∈ ℤ ;H; ;S; (fg)(x) ∈ ℕ |- ⊥", //
				funOvr(hyp, "0", empty, funImgSimp("0", empty)));

	}

	/**
	 * Ensures that the tactic is applied once.
	 */
	@Test
	public void onceApplication() {
		addToTypeEnvironment("x=ℤ");
		final Predicate hyp1 = parsePredicate("(fgh)(x) ∈ ℕ");
		assertSuccess("f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; h ∈ ℤ → ℤ ;; x ∈ ℤ ;H; "
				+ ";S; (fgh)(x) ∈ ℕ |- ⊥", //
				funOvr(hyp1, "0", empty, empty));
	}

	/**
	 * Ensures that the tactic is applied recursively when possible.
	 */
	@Test
	public void recursiveApplication() {
		addToTypeEnvironment("x=ℤ; f=ℤ↔ℤ; g=ℤ↔ℤ; h=ℤ↔ℙ(ℤ); i=ℤ↔ℙ(ℤ)");
		final Predicate hyp1 = parsePredicate("(fg)(x) ∈ (hi)(x)");
		final Predicate hyp2 = parsePredicate("g(x) ∈ (hi)(x)");
		final Predicate hyp3 = parsePredicate("(dom(g) ⩤ f)(x)∈(hi)(x)");
		assertSuccess(
				"f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; h ∈ ℤ → ℙ(ℤ) ;; i ∈ ℤ → ℙ(ℤ) ;; x ∈ ℤ ;H; ;S;"
						+ "(fg)(x) ∈ (hi)(x) |- ⊥",
				funOvr(hyp1,
						"0", //
						funOvr(hyp2, "1", empty, funImgSimp("1", empty)), //
						funOvr(hyp3, "1", funImgSimp("0", empty),
								funImgSimp("0", funImgSimp("1", empty)))));
	}

	/**
	 * Ensures that the tactic doesn't modify anything out of the subtree where
	 * it is applied.
	 */
	@Test
	public void subtree() {
		addToTypeEnvironment("x=ℤ");
		final String sequentStr = "f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; h ∈ ℤ → ℤ ;;"
				+ " i ∈ ℤ → ℤ ;; x ∈ ℤ ;H; ;S; (fg)(x) ∈ ℕ ∨ (hi)(x) ∈ ℕ |- ⊥";
		final Predicate hyp = parsePredicate("(fg)(x) ∈ ℕ ∨ (hi)(x) ∈ ℕ");
		final IProofTreeNode root = genProofTreeNode(sequentStr);
		Tactics.disjE(hyp).apply(root, null);
		final IProofTreeNode leftNode = root.getChildNodes()[0];
		final Predicate leftHyp = ((AssociativePredicate) hyp).getChildren()[0];
		tactic.apply(leftNode, null);
		TreeShape.assertRulesApplied(root, //
				disjE(hyp, //
						funOvr(leftHyp, "0", empty, funImgSimp("0", empty)), //
						empty));
	}

}
