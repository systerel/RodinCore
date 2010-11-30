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
		// Former rules SIMP_SPECIAL_(ID|PRJ1|PRJ2) 
		expressionTest("∅⦂S↔S", "∅ ◁ (id⦂S↔S)");
		expressionTest("∅⦂S×T↔S", "∅ ◁ (prj1⦂S×T↔S)");
		expressionTest("∅⦂S×T↔T", "∅ ◁ (prj2⦂S×T↔T)");
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
		
		// some relations were not tested because they are matched previously
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
		expressionTest("∅⦂ℙ(S)", "{x⦂S · ⊥ ∣ x}");
		expressionTest("∅⦂ℙ(ℤ)", "{x · ⊥ ∣ x+1}");
		expressionTest("∅⦂ℙ(S)", "{x⦂S, y⦂S · ⊥ ∣ E}", "E", "S");
		expressionTest("∅⦂ℙ(S)", "{x⦂S, y⦂S · ⊥ ∣ y}");
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
		expressionTest("∅⦂ℙ(S)", "⋃ x⦂S · ⊥ ∣ {x}");
		expressionTest("∅⦂ℙ(S)", "⋃ x⦂S, y⦂S · ⊥ ∣ E", "E", "ℙ(S)");
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

	/**
	 * Ensures that rule SIMP_TYPE_OVERL_CPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_OVERL_CPROD() {
		expressionTest("Ty × S", "r  (Ty × S)", "Ty", "ℙ(Ty)", "S", "ℙ(T)");
		expressionTest("r  (T × S)", "r  (T × S)", "T", "ℙ(U)", "S", "ℙ(V)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_REL is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_REL() {
		predicateTest("⊥", "A ↔ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("⊥", "A ⇸ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("⊥", "A ⤔ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		
		// some relations were not tested because they are matched previously
		
		// this test is the surjective relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		// this test is the surjective total relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("A ⤀ B = ∅", "A ⤀ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_RELDOM is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_RELDOM() {
		// this test is the total relation
		predicateTest("¬ A=∅ ∧ B=∅", "A  B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("¬ A=∅ ∧ B=∅", "A → B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ↣ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ↠ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ⤖ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		
		// some relations were not tested because they are matched previously
		
		// this test is the surjective relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		// this test is the surjective total relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
		predicateTest("A ⤀ B = ∅", "A ⤀ B = ∅", "A", "ℙ(A)", "B", "ℙ(B)");
	}
	
	/**
	 * Ensures that rule SIMP_FINITE_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_LAMBDA() {
		predicateTest("finite({x ∣ x = 5})", "finite(λ x · x = 5 ∣ E)", //
				"E", "S");
		predicateTest("finite({x ∣ x = 5})", "finite(λ x · x = 5 ∣ x)");
		predicateTest("finite({x ∣ x ∈ ℕ})", "finite(λ x · x ∈ ℕ ∣ E)", //
				"E", "S");
		predicateTest("finite({x↦y ∣ x ∈ ℕ ∧ y ∈ BOOL})",
				"finite(λ x↦y · x ∈ ℕ ∧ y ∈ BOOL ∣ E)", "E", "S");
		predicateTest("finite({x↦(y↦z) ∣ x = y+z})",
				"finite(λ x↦(y↦z) · x = y+z ∣ x−y+z)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_R is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_R() {
		expressionTest("dom(r) × U", "r ; (T×U)", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("r ; (T×U)", "r ; (T×U)", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(W)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_L is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_L() {
		expressionTest("T × ran(r)", "(T×U) ; r", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("(T×U) ; r", "(T×U) ; r", //
				"r", "U↔V", "T", "ℙ(W)", "U", "ℙ(U)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_L is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_L() {
		expressionTest("dom(r) × U", "(T×U) ∘ r", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("(T×U) ∘ r", "(T×U) ∘ r", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(W)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_R is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_R() {
		expressionTest("T × ran(r)", "r ∘ (T×U)", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("r ∘ (T×U)", "r ∘ (T×U)", //
				"r", "U↔V", "T", "ℙ(W)", "U", "ℙ(U)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DPROD() {
		expressionTest("A×(B×C)", "(A×B) ⊗ (A×C)", //
				"A", "ℙ(A)", "B", "ℙ(B)", "C", "ℙ(C)");
		expressionTest("(A×B) ⊗ (A×C)", "(A×B) ⊗ (A×C)", //
				"A", "ℙ(S)", "B", "ℙ(B)", "C", "ℙ(C)");
		expressionTest("(A×B) ⊗ (A×C)", "(A×B) ⊗ (A×C)", //
				"A", "ℙ(A)", "B", "ℙ(S)", "C", "ℙ(C)");
		expressionTest("(A×B) ⊗ (A×C)", "(A×B) ⊗ (A×C)", //
				"A", "ℙ(A)", "B", "ℙ(B)", "C", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_PPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_PPROD() {
		expressionTest("(A × C) × (B × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(A)", "B", "ℙ(B)", "C", "ℙ(C)", "D", "ℙ(D)");
		expressionTest("(A × B) ∥ (C × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(S)", "B", "ℙ(B)", "C", "ℙ(C)", "D", "ℙ(D)");
		expressionTest("(A × B) ∥ (C × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(A)", "B", "ℙ(S)", "C", "ℙ(C)", "D", "ℙ(D)");
		expressionTest("(A × B) ∥ (C × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(A)", "B", "ℙ(B)", "C", "ℙ(S)", "D", "ℙ(D)");
		expressionTest("(A × B) ∥ (C × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(A)", "B", "ℙ(B)", "C", "ℙ(C)", "D", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_CONVERSE() {
		expressionTest("(B × A)", "(A × B)∼", "A", "ℙ(A)", "B", "ℙ(B)");
		expressionTest("(A × B)∼", "(A × B)∼", "A", "ℙ(S)", "B", "ℙ(B)");
		expressionTest("(A × B)∼", "(A × B)∼", "A", "ℙ(A)", "B", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_DOM_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_ID() {
		expressionTest("S", "dom(id⦂S↔S)", "S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_RAN_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_ID() {
		expressionTest("S", "ran(id⦂S↔S)", "S", "ℙ(S)");
	}
	
	/**
	 * Ensures that rule SIMP_DOM_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_PRJ1() {
		expressionTest("S × T", "dom(prj1⦂ℙ(S×T×S))", "S", "ℙ(S)", "T", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_DOM_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_PRJ2() {
		expressionTest("S × T", "dom(prj2⦂ℙ(S×T×T))", "S", "ℙ(S)", "T", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_RAN_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_PRJ1() {
		expressionTest("S", "ran(prj1⦂ℙ(S×T×S))", "S", "ℙ(S)", "T", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_RAN_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_PRJ2() {
		expressionTest("T", "ran(prj2⦂ℙ(S×T×T))", "S", "ℙ(S)", "T", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOM() {
		expressionTest("A", "dom(A×B)", "A", "ℙ(A)", "B", "ℙ(B)");
		expressionTest("dom(A×B)", "dom(A×B)", "A", "ℙ(S)", "B", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_TYPE_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RAN() {
		expressionTest("B", "ran(A×B)", "A", "ℙ(A)", "B", "ℙ(B)");
		expressionTest("ran(A×B)", "ran(A×B)", "A", "ℙ(S)", "B", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_MOD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_MOD_0() {
		expressionTest("0", "0 mod E", "E", "ℤ");
		expressionTest("2 mod E", "2 mod E", "E", "ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_SPECIAL_MOD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_MOD_1() {
		expressionTest("0", "E mod 1", "E", "ℤ");
		expressionTest("E mod 3", "E mod 3", "E", "ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_MIN_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_SING() {
		expressionTest("E", "min({E})", "E", "ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_MAX_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_SING() {
		expressionTest("E", "max({E})", "E", "ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_MIN_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_NATURAL() {
		expressionTest("0", "min(ℕ)");
		expressionTest("min(ℤ)", "min(ℤ)");
	}

	/**
	 * Ensures that rule SIMP_MIN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_NATURAL1() {
		expressionTest("1", "min(ℕ1)");
	}

	/**
	 * Ensures that rule SIMP_MIN_UPTO is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_UPTO() {
		expressionTest("E", "min(E‥F)", "E", "ℤ", "F", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_MAX_UPTO is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_UPTO() {
		expressionTest("F", "max(E‥F)", "E", "ℤ", "F", "ℤ");
	}
	
	/**
	 * Ensures that rule SIMP_CARD_CONVERSE is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_CONVERSE() {
		expressionTest("card(r)", "card(r∼)", "r", "S↔T");
		expressionTest("card(r)", "card(r)", "r", "S↔T");
	}
	
	/**
	 * Ensures that rule SIMP_CARD_ID is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_ID() {
		expressionTest("card(S)", "card(S ◁ (id⦂T↔T))", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_LIT_GE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_GE_CARD_1() {
		predicateTest("¬(S = ∅⦂ℙ(T))", "card(S) ≥ 1", "S", "ℙ(T)");
		predicateTest("card(S) ≥ 2", "card(S) ≥ 2", "S", "ℙ(T)");
	}
	
	/**
	 * Ensures that rule SIMP_LIT_LE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_LE_CARD_1() {
		predicateTest("¬(S = ∅⦂ℙ(T))", "1 ≤ card(S)", "S", "ℙ(T)");
		predicateTest("0 ≤ card(S)", "0 ≤ card(S)", "S", "ℙ(T)");
	}
	
}