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
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.junit.Test;

/**
 * This is the class for testing automatic rewriter L3 {@link AutoRewriterImpl}
 * using the abstract auto formula rewriter tests
 * {@link AbstractAutoFormulaRewriterTests}.
 */
public class AutoFormulaRewriterL3Tests extends AutoFormulaRewriterL2Tests {

	// The automatic rewriter for testing.
	private static final AutoRewriterImpl REWRITER_L3 = new AutoRewriterImpl(
			DT_FAC, Level.L3);

	public AutoFormulaRewriterL3Tests() {
		this(REWRITER_L3);
	}

	protected AutoFormulaRewriterL3Tests(AutoRewriterImpl rewriter) {
		super(rewriter);
	}

	/**
	 * Ensures that rule MY_NAME is implemented correctly.
	 */
	@Test
	public void testMY_NAME() {
		rewritePred("a↦b∈A×B", "a∈A ∧ b∈B", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("a↦b↦c∈A×B×C", "a↦b∈A×B ∧ c∈C", "A", "ℙ(S)", "B", "ℙ(T)", "C", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOM_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOM_DOMSUB() {
		rewriteExpr("dom(A⩤f)", "dom(f)∖A", "A", "ℙ(S)", "f", "ℙ(S×S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOM_DOMRES is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOM_DOMRES() {
		rewriteExpr("dom(A◁f)", "dom(f)∩A", "A", "ℙ(S)", "f", "ℙ(S×S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RAN_RANSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RAN_RANSUB() {
		rewriteExpr("ran(f⩥A)", "ran(f)∖A", "A", "ℙ(S)", "f", "ℙ(S×S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RAN_RANRES is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RAN_RANRES() {
		rewriteExpr("ran(f▷A)", "ran(f)∩A", "A", "ℙ(S)", "f", "ℙ(S×S)");
	}

	/**
	 * Ensures that rule  is implemented correctly.
	 */
	@Test
	public void test5() {
		rewritePred("x∈A∖{x}", "⊥", "A", "ℙ(S)", "x", "S");
		rewritePred("x∈A∖{w, x, y}", "⊥", "A", "ℙ(S)", "w", "S", "x", "S", "y", "S");
	}

	/**
	 * Ensures that rule  is implemented correctly.
	 */
	@Test
	public void test6() {
		rewritePred("x∈A∪B∪{x}∪C∪D", "⊤", "A", "ℙ(S)");
		rewritePred("x∈A∪B∪{w, x, y}∪C∪D", "⊤", "A", "ℙ(S)");
	}

	/**
	 * Ensures that rule  is implemented correctly.
	 */
	@Test
	public void test7() {
		rewritePred("pred(int) = int−1", "succ∼(int) = int−1");
	}

	/**
	 * Ensures that rule DEF_DOM_SUCC is implemented correctly.
	 */
	@Test
	public void testDEF_DOM_SUCC() {
		rewriteExpr("dom(succ)", "ℤ");
	}

	/**
	 * Ensures that rule DEF_RAN_SUCC is implemented correctly.
	 */
	@Test
	public void testDEF_RAN_SUCC() {
		rewriteExpr("ran(succ)", "ℤ");
	}

}