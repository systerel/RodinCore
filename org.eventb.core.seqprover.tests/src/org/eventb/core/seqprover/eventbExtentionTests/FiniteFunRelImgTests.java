/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed rules FIN_FUN_*
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genProofTreeNode;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunRelImg;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInput;
import org.junit.Test;

/**
 * Unit tests for the Finite of relational image of a function reasoner
 * {@link FiniteFunRelImg}
 * 
 * @author htson
 */
public class FiniteFunRelImgTests extends AbstractPFunSetInputReasonerTests {

	private static final String P1 = "(x = 2) ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[S])";

	private static final String P2 = "∀x· x = 2 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[S])";

	private static final String P3 = "finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[A])";
	private static final String P3Input = "ℤ ⇸ ℤ × ℤ";
	
	private static final String[] P3Result = {
		"{A=ℙ(ℤ), x=ℤ}[][][⊤] |- ⊤", //
		"{A=ℙ(ℤ), x=ℤ}[][][⊤] |- {0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)} ∈ ℤ ⇸ ℤ × ℤ", //
		"{A=ℙ(ℤ), x=ℤ}[][][⊤] |- finite(A)", //
	};
	
	private static final String P4 = "finite({0 ↦ 1}[A])";
	private static final String P4Input = "{1÷2} ⇸ {3÷4}";
	
	private static final String[] P4Result = {
		"{A=ℙ(ℤ)}[][][⊤] |- 2≠0∧4≠0", //
		"{A=ℙ(ℤ)}[][][⊤] |- {0 ↦ 1} ∈ {1÷2} ⇸ {3÷4}", //
		"{A=ℙ(ℤ)}[][][⊤] |- finite(A)", //
	};

	private static final String P5 = "finite({0})";

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteFunRelImgGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteFunRelImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, P3Input, P3Result),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, P4Input, P4Result),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// incorrect goal
				" ⊤ |- " + P1,
				null,
				P3Input,
				"Goal is not a finiteness",
				// incorrect goal
				" ⊤ |- " + P2,
				null,
				P3Input,
				"Goal is not a finiteness",
				// incorrect goal, local check
				" ⊤ |- " + P5,
				null,
				"ℤ ⇸ ℤ",
				"Goal is not a finiteness of a relation image",
				// correct goal, wrong input
				" ⊤ |- " + P3,
				null,
				"ℕ ↔ BOOL",
				"Expected a set of all partial functions S ⇸ T",
				// Correct goal, type-check error with input
				" ⊤ |- " + P3,
				null,
				"ℕ ⇸ BOOL",
				"Type check failed for " + "{0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℕ ⇸ BOOL",
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
		};
	}

	/**
	 * Ensures that the deprecated tactic implemented for backward compatibility
	 * can apply the reasoner.
	 */
	@Test
	public void oldTacticSucceeds() {
		final ITactic tactic = getOldTactic();
		final IProofTreeNode root = genProofTreeNode("⊤ |- finite({0 ↦ TRUE}[{1}])");
		tactic.apply(root, null);
		assertFalse(root.isOpen());
		final IProofRule rule = root.getRule();
		assertEquals(FiniteFunRelImg.REASONER_ID, rule.generatedBy().getReasonerID());
		assertPFunSetInput("ℤ ⇸ BOOL", rule.generatedUsing());
	}

	/**
	 * Ensures that the deprecated tactic implemented for backward compatibility
	 * can apply the reasoner.
	 */
	@Test
	public void oldTacticFails() {
		assertFailure("⊤ |- ⊤");
		assertFailure("⊤ |- finite({0 ↦ TRUE})");
		assertFailure("⊤ |- finite({0 ↦ {TRUE}}(0))");
	}
	
	@SuppressWarnings("deprecation")
	private static ITactic getOldTactic() {
		return Tactics.finiteFunRelImg();
	}

	private static void assertPFunSetInput(final String exprImage,
			final IReasonerInput input) {
		final Expression expr = genExpr(genTypeEnv(""), exprImage);
		assertTrue(input instanceof PFunSetInput);
		assertFalse(input.hasError());
		assertEquals(expr, ((PFunSetInput) input).getExpression());
	}

	private static void assertFailure(String sequentImage) {
		final ITactic tactic = getOldTactic();
		final IProofTreeNode root = genProofTreeNode(sequentImage);
		final Object actual = tactic.apply(root, null);
		assertEquals("Tactic inapplicable", actual);
		assertTrue(root.isOpen());
	}
	
// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
