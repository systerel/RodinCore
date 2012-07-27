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

import static org.eventb.core.seqprover.tactics.tests.TreeShape.conjI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgSimp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funOvr;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>FunOvrGoalTac</code>.
 * 
 * @author Laurent Voisin
 */
public class FunOvrGoalTacTests extends AbstractTacticTests {

	public FunOvrGoalTacTests() {
		super(new AutoTactics.FunOvrGoalTac(),
				"org.eventb.core.seqprover.funOvrGoalTac");
	}

	/**
	 * Ensures that the tactic fails when it is not applicable.
	 */
	@Test
	public void notApplicable() {
		assertFailure(" ;H; ;S; f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; x ∈ ℤ |- f(x) ∈ ℕ");
	}

	/**
	 * Ensures that the tactic succeeds when applicable once.
	 */
	@Test
	public void simpleApplication() {
		assertSuccess(" ;H; ;S; f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; x ∈ ℤ |- (fg)(x) ∈ ℕ",
				funOvr("0", empty, funImgSimp("0", empty)));
	}

	/**
	 * Ensures that the tactic is applied once.
	 */
	@Test
	public void onceApplication() {
		assertSuccess(" ;H; ;S; f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;;  h ∈ ℤ → ℤ ;; x ∈ ℤ "
				+ "|- (fgh)(x) ∈ ℕ", funOvr("0", empty, empty));
	}

	/**
	 * Ensures that the tactic is applied recursively when possible.
	 */
	@Test
	public void recursiveApplication() {
		assertSuccess(
				" ;H; ;S; f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ ;; h ∈ ℤ → ℙ(ℤ) ;;"
						+ " i ∈ ℤ → ℙ(ℤ) ;; x ∈ ℤ |- (fg)(x) ∈ (hi)(x)",
				funOvr("0",
						funOvr("1", empty, funImgSimp("1", empty)),
						funOvr("1", //
								funImgSimp("0", empty),
								funImgSimp("0", funImgSimp("1", empty)))));
	}

	/**
	 * Ensures that the tactic doesn't modify anything out of the subtree where
	 * it is applied.
	 */
	@Test
	public void subtree() {
		final String sequent = " ;H; ;S; f ∈ ℤ → ℤ ;; g ∈ ℤ → ℤ  ;;"
				+ " h ∈ ℤ → ℤ  ;; i ∈ ℤ → ℤ  ;; x ∈ ℤ"
				+ " |- (fg)(x) ∈ ℕ ∧ (hi)(x) ∈ ℕ";
		final IProofTreeNode root = genProofTreeNode(sequent);
		final ITactic conjTactic = new AutoTactics.ConjGoalTac();
		conjTactic.apply(root, null);
		final IProofTreeNode leftNode = root.getChildNodes()[0];
		tactic.apply(leftNode, null);
		TreeShape.assertRulesApplied(root,
				conjI(funOvr("0", empty, funImgSimp("0", empty)), //
						empty));
	}

}
