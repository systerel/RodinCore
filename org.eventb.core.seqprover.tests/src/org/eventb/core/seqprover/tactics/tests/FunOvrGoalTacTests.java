/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.assertRulesApplied;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.conjI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funOvr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>FunOvrGoalTac</code>.
 * 
 * @author Laurent Voisin
 */
public class FunOvrGoalTacTests {

	private static final ITactic tac = new AutoTactics.FunOvrGoalTac();

	private static final String TAC_ID = "org.eventb.core.seqprover.funOvrGoalTac";

	private static void assertFailure(IProofTreeNode node) {
		assertNotNull(tac.apply(node, null));
		assertRulesApplied(node, empty);
	}

	private static void assertSuccess(IProofTreeNode node, TreeShape expected) {
		assertNull(tac.apply(node, null));
		assertRulesApplied(node, expected);
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
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"x ∈ ℤ", //
				"f(x) ∈ ℕ" //
		);
		assertFailure(pt.getRoot());
	}

	/**
	 * Ensures that the tactic succeeds when applicable once.
	 */
	@Test
	public void simpleApplication() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"x ∈ ℤ", //
				"(fg)(x) ∈ ℕ" //
		);
		assertSuccess(pt.getRoot(), funOvr("0", empty, empty));
	}

	/**
	 * Ensures that the tactic is applied repeatedly when possible.
	 */
	@Test
	public void doubleApplication() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"h ∈ ℤ → ℤ", //
				"x ∈ ℤ", //
				"(fgh)(x) ∈ ℕ" //
		);
		assertSuccess(pt.getRoot(), funOvr("0", empty,
				funOvr("0", empty, empty)));
	}

	/**
	 * Ensures that the tactic is applied recursively when possible.
	 */
	@Test
	public void recursiveApplication() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"h ∈ ℤ → ℙ(ℤ)", //
				"i ∈ ℤ → ℙ(ℤ)", //
				"x ∈ ℤ", //
				"(fg)(x) ∈ (hi)(x)" //
		);
		final TreeShape sub = funOvr("1", empty, empty);
		assertSuccess(pt.getRoot(), funOvr("0", sub, sub));
	}

	/**
	 * Ensures that the tactic doesn't modify anything out of the subtree where
	 * it is applied.
	 */
	@Test
	public void subtree() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"h ∈ ℤ → ℤ", //
				"i ∈ ℤ → ℤ", //
				"x ∈ ℤ", //
				"(fg)(x) ∈ ℕ ∧ (hi)(x) ∈ ℕ" //
		);
		final IProofTreeNode root = pt.getRoot();
		new AutoTactics.ConjGoalTac().apply(root, null);
		final IProofTreeNode left = root.getChildNodes()[0];
		final TreeShape sub = funOvr("0", empty, empty);
		assertSuccess(left, sub);
		assertRulesApplied(root, conjI(sub, empty));
	}

}
