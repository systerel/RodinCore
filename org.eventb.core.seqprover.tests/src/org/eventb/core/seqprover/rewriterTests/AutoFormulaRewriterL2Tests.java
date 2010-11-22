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
		expressionTest("∅⦂U↔V", "S × (∅⦂ℙ(V))", "S", "ℙ(U)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_CPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CPROD_L() {
		expressionTest("∅⦂U↔V", "(∅⦂ℙ(U)) × S", "S", "ℙ(V)");
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
		predicateTest("⊥","S ⊂ S" ,"S", "ℙ(T)");
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
		expressionTest("∅⦂U↔V","S ◁ (∅⦂U↔V)", "S", "ℙ(U)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DOMRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMRES() {
		expressionTest("r", "S ◁ r", "r", "S↔T");
		expressionTest("S ◁ r", "S ◁ r", "r", "U↔V");
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
		expressionTest("∅⦂U↔V","(∅⦂U↔V) ▷ S", "S", "ℙ(V)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RANRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANRES() {
		expressionTest("r", "r ▷ T", "r", "S↔T");
		expressionTest("r ▷ T", "r ▷ T", "r", "U↔V");
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
		expressionTest("∅⦂U↔V", "S ⩤ (∅⦂U↔V)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMSUB() {
		expressionTest("∅⦂S↔T", "S ⩤ r", "r","S↔T");
		expressionTest("S ⩤ r", "S ⩤ r", "r","U↔V");
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
		expressionTest("∅⦂U↔V", "(∅⦂U↔V) ⩥ S");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RANSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANSUB() {
		expressionTest("∅⦂S↔T", "r ⩥ T", "r", "S↔T");
		expressionTest("r ⩥ T", "r ⩥ T", "r", "U↔V");
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
	
	/**
	 * Ensures that rule SIMP_SPECIAL_PPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_PPROD_R() {
		expressionTest("∅⦂S×U↔T×V", "r ∥ (∅⦂U↔V)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_PPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_PPROD_L() {
		expressionTest("∅⦂S×U↔T×V", "(∅⦂S↔T) ∥ r", "r", "U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RELIMAGE is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RELIMAGE() {
		expressionTest("ran(r)", "r[S]", "r", "S↔T");
		expressionTest("r[U]", "r[U]", "r", "S↔T", "U", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_DOM() {
		expressionTest("ran(r)", "r[dom(r)]", "r", "S↔T");
		expressionTest("r[dom(s)]", "r[dom(s)]", "r", "S↔T", "s", "S↔V");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RELIMAGE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RELIMAGE_ID() {
		expressionTest("T","id[T]", "T", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CPROD_SING
	 *  is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CPROD_SING() {
		expressionTest("S", "({E}×S)[{E}]", "S", "ℙ(U)", "E", "V");
		expressionTest("({E}×S)[{F}]", "({E}×S)[{F}]", "S", "ℙ(U)", "E", "V", "F", "V");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_SING_MAPSTO
	 *  is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_SING_MAPSTO() {
		expressionTest("{F}", "{E ↦ F}[{E}]", "E", "S", "F", "T");
		expressionTest("{E ↦ F}[{G}]", "{E ↦ F}[{G}]", "E", "S", "F", "T", "G", "S");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB
	 * is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CONVERSE_RANSUB() {
		expressionTest("∅⦂ℙ(U)", "(r ⩥ S)∼[S]", "r", "U↔V");
		expressionTest("(r ⩥ S)∼[T]", "(r ⩥ S)∼[T]", "r", "U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANRES
	 *  is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CONVERSE_RANRES() {
		expressionTest("r∼[S]", "(r ▷ S)∼[S]", "r", "U↔V");
		expressionTest("(r ▷ S)∼[T]", "(r ▷ S)∼[T]", "r", "U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_RELIMAGE_CONVERSE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_RELIMAGE_CONVERSE_DOMSUB() {
		expressionTest("r∼[T]∖S", "(S ⩤ r)∼[T]","r","U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_DOMSUB() {
		expressionTest("∅⦂ℙ(V)", "(S ⩤ r)[S]","r","U↔V");
		expressionTest("(S ⩤ r)[T]", "(S ⩤ r)[T]","r","U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CONVERSE() {
		expressionTest("∅⦂T↔S", "(∅⦂S↔T)∼");
	}
	
	/**
	 * Ensures that rule SIMP_CONVERSE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_CONVERSE_ID() {
		expressionTest("id⦂S↔S", "(id⦂S↔S)∼");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_ID_L is implemented correctly.
	 */
	@Test
	public void testSIMP_FCOMP_ID_L() {
		expressionTest("S ◁ r", "(S ◁ id) ; r", "r", "U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_FCOMP_ID_R is implemented correctly.
	 */
	@Test
	public void testSIMP_FCOMP_ID_R() {
		expressionTest("r ▷ S", "r ; (S ◁ id)", "r", "U↔V");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_REL_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_REL_R() {
		expressionTest("{∅⦂U↔V}", "S ↔ (∅⦂ℙ(V))", "S", "ℙ(U)");
		// this test is the surjective relation <->>
		expressionTest("{∅⦂U↔V}", "S  (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("{∅⦂U↔V}", "S ⇸ (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("{∅⦂U↔V}", "S ⤔ (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("{∅⦂U↔V}", "S ⤀ (∅⦂ℙ(V))", "S", "ℙ(U)");
		
		expressionTest("S → (∅⦂ℙ(V))", "S → (∅⦂ℙ(V))", "S", "ℙ(U)");
		// this test is the total relation <<->
		expressionTest("S  (∅⦂ℙ(V))", "S  (∅⦂ℙ(V))", "S", "ℙ(U)");
		// this test is the surjective total relation <<->>
		expressionTest("S  (∅⦂ℙ(V))", "S  (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("S ↣ (∅⦂ℙ(V))", "S ↣ (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("S ↠ (∅⦂ℙ(V))", "S ↠ (∅⦂ℙ(V))", "S", "ℙ(U)");
		expressionTest("S ⤖ (∅⦂ℙ(V))", "S ⤖ (∅⦂ℙ(V))", "S", "ℙ(U)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_REL_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_REL_L() {
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U)) ↔ S", "S", "ℙ(V)");
		// this test is the total relation <<->
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U))  S", "S", "ℙ(V)");
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U)) ⇸ S", "S", "ℙ(V)");
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U)) → S", "S", "ℙ(V)");
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U)) ⤔ S", "S", "ℙ(V)");
		expressionTest("{∅⦂U↔V}", "(∅⦂ℙ(U)) ↣ S", "S", "ℙ(V)");
		
		// this test is the surjective relation <->>
		expressionTest("(∅⦂ℙ(U))  S", "(∅⦂ℙ(U))  S", "S", "ℙ(V)");
		// this test is the surjective total relation <<->>
		expressionTest("(∅⦂ℙ(U))  S", "(∅⦂ℙ(U))  S", "S", "ℙ(V)");
		expressionTest("(∅⦂ℙ(U)) ⤀ S", "(∅⦂ℙ(U)) ⤀ S", "S", "ℙ(V)");
		expressionTest("(∅⦂ℙ(U)) ↠ S", "(∅⦂ℙ(U)) ↠ S", "S", "ℙ(V)");
		expressionTest("(∅⦂ℙ(U)) ⤖ S", "(∅⦂ℙ(U)) ⤖ S", "S", "ℙ(V)");
	}
	
	/**
	 * Ensures that rule SIMP_FUNIMAGE_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_PRJ1() {
		expressionTest("E", "prj1(E ↦ F)", "E", "S", "F", "T");
	}
	
	/**
	 * Ensures that rule SIMP_FUNIMAGE_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_PRJ2() {
		expressionTest("F", "prj2(E ↦ F)", "E", "S", "F", "T");
	}
	
	/**
	 * Ensures that rule SIMP_FUNIMAGE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_ID() {
		expressionTest("x", "id(x)", "x", "S");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_RELDOMRAN is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_RELDOMRAN() {
		// this test is the surjective total relation <<->>
		expressionTest("{∅⦂ℙ(S×T)}", "(∅⦂ℙ(S))  (∅⦂ℙ(T))");
		expressionTest("{∅⦂ℙ(S×T)}", "(∅⦂ℙ(S)) ↠ (∅⦂ℙ(T))");
		expressionTest("{∅⦂ℙ(S×T)}", "(∅⦂ℙ(S)) ⤖ (∅⦂ℙ(T))");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_DOM_CPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOM_CPROD() {
		expressionTest("E", "dom(E×E)", "E", "ℙ(S)");
		expressionTest("dom(E×F)", "dom(E×F)", "E", "ℙ(S)", "F", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_MULTI_RAN_CPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RAN_CPROD() {
		expressionTest("E", "ran(E×E)", "E", "ℙ(S)");
		expressionTest("ran(E×F)", "ran(E×F)", "E", "ℙ(S)", "F", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_COMPSET_BFALSE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_COMPSET_BFALSE() {
		expressionTest("∅⦂ℙ(S)", "{x⦂S · ⊥ ∣ E}", "E", "S");
		expressionTest("{x⦂S, y⦂S · ⊥ ∣ E}", "{x⦂S, y⦂S · ⊥ ∣ E}", "E", "S");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_COMPSET_BTRUE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_COMPSET_BTRUE() {
		expressionTest("S", "{x⦂S · ⊤ ∣ x}", "S", "ℙ(S)");
		expressionTest("{x⦂S, y⦂S · ⊤ ∣ x}", "{x⦂S, y⦂S · ⊤ ∣ x}", "S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_KUNION_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_KUNION_POW() {
		expressionTest("S", "union(ℙ(S))", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_KUNION_POW1 is implemented correctly.
	 */
	@Test
	public void testSIMP_KUNION_POW1() {
		expressionTest("S", "union(ℙ1(S))", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_KUNION is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_KUNION() {
		expressionTest("∅⦂ℙ(S)", "union({∅⦂ℙ(S)})");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_QUNION is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_QUNION() {
		expressionTest("∅⦂ℙ(S)", "⋃ x⦂S · ⊥ ∣ E", "E", "ℙ(S)");
		expressionTest("⋃ x⦂S, y⦂S · ⊥ ∣ E", "⋃ x⦂S, y⦂S · ⊥ ∣ E", "E", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_KINTER is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_KINTER() {
		expressionTest("∅⦂ℙ(S)", "inter({∅⦂ℙ(S)})");
	}
	
	/**
	 * Ensures that rule SIMP_KINTER_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_KINTER_POW() {
		expressionTest("∅⦂ℙ(T)", "inter(ℙ(S))", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_FINITE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_ID() {
		predicateTest("finite(S)", "finite(id⦂S↔S)", "S", "ℙ(S)");
	}

}