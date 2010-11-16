/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
 * This is the class for testing automatic rewriter L2 {@link AutoRewriterImpl}
 * using the abstract auto formula rewriter tests
 * {@link AbstractAutoFormulaRewriterTests}.
 */
public class AutoFormulaRewriterL2Tests extends AutoFormulaRewriterTests {

	// The automatic rewriter for testing.
	private static final AutoRewriterImpl rewriter = new AutoRewriterImpl(
			DT_FAC, Level.L2);

	/**
	 * Constructor.
	 * <p>
	 * Create an formula rewriter test with the input is the automatic rewriter.
	 */
	public AutoFormulaRewriterL2Tests() {
		super(rewriter);
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_OR is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_OR() {
		predicateTest("⊤", "a=0 ∧ b=1 ⇒ a=0");
		predicateTest("⊤", "a=0 ∧ b=1 ⇒ b=1");

		predicateTest("⊤", "a=0 ∧ b=1 ∧ c=2 ⇒ a=0");
		predicateTest("⊤", "a=0 ∧ b=1 ∧ c=2 ⇒ b=1");
		predicateTest("⊤", "a=0 ∧ b=1 ∧ c=2 ⇒ c=2");

		predicateTest("a=0 ∧ b=1 ⇒ a=1", "a=0 ∧ b=1 ⇒ a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_AND_NOT_R is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_AND_NOT_R() {
		predicateTest("¬(a=0 ∧ b=1)", "a=0 ∧ b=1 ⇒ ¬a=0");
		predicateTest("¬(a=0 ∧ b=1)", "a=0 ∧ b=1 ⇒ ¬b=1");

		predicateTest("¬(a=0 ∧ b=1 ∧ c=2)", "a=0 ∧ b=1 ∧ c=2⇒ ¬a=0");
		predicateTest("¬(a=0 ∧ b=1 ∧ c=2)", "a=0 ∧ b=1 ∧ c=2⇒ ¬b=1");
		predicateTest("¬(a=0 ∧ b=1 ∧ c=2)", "a=0 ∧ b=1 ∧ c=2⇒ ¬c=2");

		predicateTest("a=0 ∧ b=1 ⇒ ¬a=1", "a=0 ∧ b=1 ⇒ ¬a=1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_IMP_AND_NOT_L is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_IMP_AND_NOT_L() {
		predicateTest("¬(¬a=0 ∧  b=1)", "¬a=0 ∧  b=1 ⇒ a=0");
		predicateTest("¬( a=0 ∧ ¬b=1)", " a=0 ∧ ¬b=1 ⇒ b=1");

		predicateTest("¬(¬a=0 ∧  b=1 ∧  c=2)", "¬a=0 ∧  b=1 ∧  c=2 ⇒ a=0");
		predicateTest("¬( a=0 ∧ ¬b=1 ∧  c=2)", " a=0 ∧ ¬b=1 ∧  c=2 ⇒ b=1");
		predicateTest("¬( a=0 ∧  b=1 ∧ ¬c=2)", " a=0 ∧  b=1 ∧ ¬c=2 ⇒ c=2");

		predicateTest("¬a=0 ∧ b=1 ⇒ a=1", "¬a=0 ∧ b=1 ⇒ a=1");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_EQV_NOT is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_EQV_NOT() {
		predicateTest("⊥", " a=0 ⇔ ¬a=0");
		predicateTest("⊥", "¬b=1 ⇔  b=1");
		
		predicateTest(" a=0 ⇔ ¬a=1", " a=0 ⇔ ¬a=1");
		predicateTest("¬a=0 ⇔  b=1", "¬a=0 ⇔ b=1");
	}
	
	/**
	 * Ensures that rule SIMP_SUBSETEQ_SING is implemented correctly.
	 */
	@Test
	public void testSIMP_SUBSETEQ_SING() {
		predicateTest("0 ∈ S","{0} ⊆ S");
		predicateTest("0 ∈ ℤ","{0} ⊆ ℤ");
		predicateTest("0 ∈ ℕ1","{0} ⊆ ℕ1");
		
		predicateTest("{0,1} ⊆ ℤ","{0,1} ⊆ ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_POW() {
		expressionTest("{∅⦂ℙ(S)}", "ℙ(∅⦂ℙ(S))");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_POW1 is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_POW1() {
		expressionTest("∅⦂ℙ(ℙ(S))", "ℙ1(∅⦂ℙ(S))");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_CPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CPROD_R() {
		expressionTest("∅⦂S↔T", "S × (∅⦂ℙ(T))", "S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_CPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CPROD_L() {
		expressionTest("∅⦂S↔T", "(∅⦂ℙ(S)) × S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_NATURAL is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_NATURAL() {
		predicateTest("⊥","finite(ℕ)");
	}
	
	/**
	 * Ensures that rule SIMP_FINITE_NATURAL1 is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_NATURAL1() {
		predicateTest("⊥","finite(ℕ1)");
	}
	
	/**
	 * Ensures that rule SIMP_FINITE_INTEGER is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_INTEGER() {
		predicateTest("⊥","finite(ℤ)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_SUBSET_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_SUBSET_R() {
		predicateTest("⊥","S ⊂ (∅⦂ℙ(T))");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_SUBSET is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_SUBSET() {
		predicateTest("⊥","S ⊂ S" ,"S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_DOM_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_CONVERSE() {
		expressionTest("ran(r)", "dom(r∼)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_RAN_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_CONVERSE() {
		expressionTest("dom(r)", "ran(r∼)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DOMRES_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMRES_L() {
		expressionTest("∅⦂S↔T","(∅⦂ℙ(S)) ◁ r", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DOMRES_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMRES_R() {
		expressionTest("∅⦂S↔T","S ◁ (∅⦂S↔T)", "S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DOMRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMRES() {
		expressionTest("r", "S ◁ r", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_DOMRES_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMRES_DOM() {
		expressionTest("r", "dom(r) ◁ r", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_DOMRES_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMRES_RAN() {
		expressionTest("r∼", "ran(r) ◁ r∼", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_RANRES_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANRES_R() {
		expressionTest("∅⦂S↔T","r ▷ (∅⦂ℙ(T))", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_RANRES_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANRES_L() {
		expressionTest("∅⦂S↔T","(∅⦂S↔T) ▷ S", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RANRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANRES() {
		expressionTest("r", "r ▷ T", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RANRES_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANRES_RAN() {
		expressionTest("r", "r ▷ ran(r)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RANRES_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANRES_DOM() {
		expressionTest("r∼", "r∼ ▷ dom(r)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DOMSUB_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMSUB_L() {
		expressionTest("r", "∅ ⩤ r", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DOMSUB_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMSUB_R() {
		expressionTest("∅⦂S↔T", "S ⩤ (∅⦂S↔T)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMSUB() {
		expressionTest("∅⦂S↔T", "S ⩤ r", "r","S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_DOMSUB_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMSUB_DOM() {
		expressionTest("∅⦂S↔T", "dom(r) ⩤ r", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_RANSUB_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANSUB_R() {
		expressionTest("r", "r ⩥ ∅", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_RANSUB_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANSUB_L() {
		expressionTest("∅⦂S↔T", "(∅⦂S↔T) ⩥ S", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RANSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANSUB() {
		expressionTest("∅⦂S↔T", "r ⩥ T", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RANSUB_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANSUB_RAN() {
		expressionTest("∅⦂S↔T", "r ⩥ ran(r)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DPROD_R() {
		expressionTest("∅⦂S↔T×U", "r ⊗ (∅⦂S↔U)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_DPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DPROD_L() {
		expressionTest("∅⦂S↔T×U", "(∅⦂S↔T) ⊗ r", "r", "S↔U");
	}
	
}
