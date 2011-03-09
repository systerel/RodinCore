/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
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
import static org.eventb.core.seqprover.tactics.tests.TreeShape.isFunGoal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

public class FunImgInGoalTacTests {

	private static final ITactic tac = new AutoTactics.FunImgInGoalTac();

	private static final String TAC_ID = "org.eventb.core.seqprover.FunImgInGoalTac";

	private static void assertSuccess(IProofTreeNode node, TreeShape expected) {
		TreeShape.assertSuccess(node, expected, tac);
	}

	private static void assertFailure(IProofTreeNode node) {
		TreeShape.assertFailure(node, tac);
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
	 * Ensures that the tactic succeeds using isFunGoal.
	 */
	@Test
	public void successWithIsFunGoal() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ→(ℤ → ℤ)", //
				"f(y)∈ℤ ⇸ ℤ" //
		);
		String hypString = "f ∈ ℤ→(ℤ → ℤ)";
		final Predicate hyp = parsePredicate(hypString, pt);
		assertSuccess(pt.getRoot(), funImgGoal(hyp, "0", isFunGoal()));
	}

	/**
	 * Ensures that the tactic succeeds by adding two hypotheses and using
	 * isFunGoal.
	 */
	@Test
	public void successWithDoubleFunApp() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ→(ℤ→(ℤ → ℤ))", //
				"f(y)(x)∈ℤ ⇸ ℤ" //
		);
		String hyp1String = "f ∈ ℤ→(ℤ→(ℤ → ℤ))";
		String hyp2String = "f(y)∈ℤ→(ℤ → ℤ)";

		final Predicate hyp1 = parsePredicate(hyp1String, pt);
		final Predicate hyp2 = parsePredicate(hyp2String, pt);
		assertSuccess(pt.getRoot(),
				funImgGoal(hyp1, "0.0", funImgGoal(hyp2, "0", (isFunGoal()))));
	}

	/**
	 * Ensures that the tactic succeeds using hyp.
	 */
	@Test
	public void successWithHyp() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ→(ℤ→(ℤ → ℤ))", "f(y)(x) ∈ ℤ → ℤ", //
				"f(y)∈ℤ→(ℤ → ℤ)" //
		);
		String hypString = "f ∈ ℤ→(ℤ→(ℤ → ℤ))";
		final Predicate hyp = parsePredicate(hypString, pt);
		assertSuccess(pt.getRoot(), funImgGoal(hyp, "0", hyp()));
	}

	/**
	 * Ensures that the tactic fails.
	 */
	@Test
	public void failure() {
		final IProofTree pt = genProofTree(//
				"f ∈ ℤ→(ℤ→(ℤ → ℤ))", "f(y)(x) ∈ ℤ → ℤ", //
				"g(y)(x) ∈ ℤ → ℤ" //
		);
		assertFailure(pt.getRoot());
	}

}
