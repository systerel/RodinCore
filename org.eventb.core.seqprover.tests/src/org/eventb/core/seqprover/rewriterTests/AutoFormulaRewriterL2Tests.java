/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
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
		predicateTest("0 ∈ S", "{0} ⊆ S");
		predicateTest("0 ∈ ℤ", "{0} ⊆ ℤ");
		predicateTest("0 ∈ ℕ1", "{0} ⊆ ℕ1");

		predicateTest("{0,1} ⊆ ℤ", "{0,1} ⊆ ℤ");
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
		predicateTest("⊥", "finite(ℕ)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_NATURAL1 is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_NATURAL1() {
		predicateTest("⊥", "finite(ℕ1)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_INTEGER is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_INTEGER() {
		predicateTest("⊥", "finite(ℤ)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_SUBSET_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_SUBSET_R() {
		predicateTest("⊥", "S ⊂ (∅⦂ℙ(T))");
	}

	/**
	 * Ensures that rule SIMP_MULTI_SUBSET is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_SUBSET() {
		predicateTest("⊥", "S ⊂ S", "S", "ℙ(T)");
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
		expressionTest("∅⦂S↔T", "(∅⦂ℙ(S)) ◁ r", "r", "S↔T");
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
		expressionTest("∅⦂U↔V", "S ◁ (∅⦂U↔V)", "S", "ℙ(U)");
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
		expressionTest("∅⦂S↔T", "r ▷ (∅⦂ℙ(T))", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_RANRES_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANRES_L() {
		expressionTest("∅⦂U↔V", "(∅⦂U↔V) ▷ S", "S", "ℙ(V)");
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
		expressionTest("∅⦂S↔T", "S ⩤ r", "r", "S↔T");
		expressionTest("S ⩤ r", "S ⩤ r", "r", "U↔V");
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
		expressionTest("T", "id[T]", "T", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CPROD_SING is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CPROD_SING() {
		expressionTest("S", "({E}×S)[{E}]", "S", "ℙ(U)", "E", "V");
		expressionTest("({E}×S)[{F}]", "({E}×S)[{F}]", //
				"S", "ℙ(U)", "E", "V", "F", "V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_SING_MAPSTO is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_SING_MAPSTO() {
		expressionTest("{F}", "{E ↦ F}[{E}]", "E", "S", "F", "T");
		expressionTest("{E ↦ F}[{G}]", "{E ↦ F}[{G}]", //
				"E", "S", "F", "T", "G", "S");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CONVERSE_RANSUB() {
		expressionTest("∅⦂ℙ(U)", "(r ⩥ S)∼[S]", "r", "U↔V");
		expressionTest("(r ⩥ S)∼[T]", "(r ⩥ S)∼[T]", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANRES is implemented
	 * correctly.
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
		expressionTest("r∼[T]∖S", "(S ⩤ r)∼[T]", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_DOMSUB() {
		expressionTest("∅⦂ℙ(V)", "(S ⩤ r)[S]", "r", "U↔V");
		expressionTest("(S ⩤ r)[T]", "(S ⩤ r)[T]", "r", "U↔V");
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

		// negative tests for the other types of relations are not written
		// because they are previously matched by SIMP_SPECIAL_REL_L and
		// SIMP_SPECIAL_REL_R
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
		expressionTest("S×T", "{x⦂S, y⦂T · ⊤ ∣ x↦y}", "S", "ℙ(S)", "T", "ℙ(T)");
		expressionTest("T×S", "{x⦂S, y⦂T · ⊤ ∣ y↦x}", "S", "ℙ(S)", "T", "ℙ(T)");
		expressionTest("T×S×U", "{x⦂S, y⦂T, z⦂U · ⊤ ∣ y↦x↦z}", //
				"S", "ℙ(S)", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("S×(T×U)×V", "{x⦂S, y⦂T, z⦂U, t⦂V · ⊤ ∣ x↦(y↦z)↦t}",
				"S", "ℙ(S)", "T", "ℙ(T)", "U", "ℙ(U)", "V", "ℙ(V)");

		expressionTest("{x⦂S, y · ⊤ ∣ x↦y+1}", "{x⦂S, y · ⊤ ∣ x↦y+1}", //
				"S", "ℙ(S)");
		expressionTest("{x⦂S, y⦂T · ⊤ ∣ x↦a↦y}", "{x⦂S, y⦂T · ⊤ ∣ x↦a↦y}", "a",
				"A");
		expressionTest("{x⦂S, y⦂T · ⊤ ∣ x↦y↦x}", "{x⦂S, y⦂T · ⊤ ∣ x↦y↦x}");
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
	 * Ensures that rule SIMP_SPECIAL_EQUAL_REL is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_REL() {
		predicateTest("⊥", "A ↔ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("⊥", "A ⇸ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("⊥", "A ⤔ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// this test is the surjective relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		// this test is the surjective total relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("A ⤀ B = ∅", "A ⤀ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// negative tests for the other types of relations are not written
		// because they are matched by SIMP_SPECIAL_EQUAL_RELDOM
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_RELDOM is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_RELDOM() {
		// this test is the total relation
		predicateTest("¬ A=∅ ∧ B=∅", "A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("¬ A=∅ ∧ B=∅", "A → B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ↣ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ↠ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("¬ A=∅ ∧ B=∅", "A ⤖ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// this test is the surjective relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		// this test is the surjective total relation
		predicateTest("A  B = ∅", "A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		predicateTest("A ⤀ B = ∅", "A ⤀ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// negative tests for the other types of relations are not written
		// because they are matched by SIMP_SPECIAL_EQUAL_REL
	}

	/**
	 * Ensures that rule SIMP_FINITE_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_LAMBDA() {
		predicateTest("finite({x,y,z⦂U · x↦y∈P ∣ x↦E↦z})", //
				"finite({x,y,z⦂U · x↦y∈P ∣ x↦E↦z ↦ z})", //
				"P", "S↔T", "E", "ℙ(V)");
		predicateTest("finite({x,y,z⦂U,t⦂ℤ · x↦y∈P ∣ x↦z↦3∗t})", //
				"finite({x,y,z⦂U,t⦂ℤ · x↦y∈P ∣ x↦z↦3∗t ↦ z})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z,t · x↦y∈P ∣ x↦z+t})", //
				"finite({x,y,z,t · x↦y∈P ∣ x↦z+t ↦ x})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z,t · x↦y∈P ∣ z+t↦x})", //
				"finite({x,y,z,t · x↦y∈P ∣ z+t↦x ↦ x})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z,t · x↦y∈P ∣ t↦z+3∗t})", //
				"finite({x,y,z,t · x↦y∈P ∣ t↦z+3∗t ↦ t})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z⦂ℤ,t · x↦y∈P ∣ z↦3∗t↦t})", //
				"finite({x,y,z,t · x↦y∈P ∣ z↦3∗t↦t ↦ z+t})", //
				"P", "S↔T");

		predicateTest("finite({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"finite({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z⦂U,t · x↦y∈P ∣ z↦3∗t ↦ t})", //
				"finite({x,y,z⦂U,t · x↦y∈P ∣ z↦3∗t ↦ t})", //
				"P", "S↔T");
		predicateTest("finite({x,y,z⦂U,t⦂V · x↦y∈P ∣ (x↦z) ↦ (x↦t)})", //
				"finite({x,y,z⦂U,t⦂V · x↦y∈P ∣ (x↦z) ↦ (x↦t)})", //
				"P", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_CARD_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_CARD_LAMBDA() {
		// The SIMP_CARD_LAMBDA rule implementation uses the same algorithm as
		// SIMP_FINITE_LAMBDA, therefore minimal testing is performed

		expressionTest("card({x,y,z⦂ℤ,t · x↦y∈P ∣ z↦3∗t↦t})", //
				"card({x,y,z,t · x↦y∈P ∣ z↦3∗t↦t ↦ z+t})", //
				"P", "S↔T");

		expressionTest("card({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"card({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"P", "S↔T");
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
		expressionTest("r ; (T×U)", "r ; (T×U)", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(U)");
		expressionTest("r ; (T×U)", "r ; (T×U)", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_L is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_L() {
		expressionTest("T × ran(r)", "(T×U) ; r", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("(T×U) ; r", "(T×U) ; r", //
				"r", "U↔V", "T", "ℙ(X)", "U", "ℙ(U)");
		expressionTest("(T×U) ; r", "(T×U) ; r", //
				"r", "W↔V", "T", "ℙ(T)", "U", "ℙ(W)");
		expressionTest("(T×U) ; r", "(T×U) ; r", //
				"r", "W↔V", "T", "ℙ(X)", "U", "ℙ(W)");
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
		expressionTest("(T×U) ∘ r", "(T×U) ∘ r", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(U)");
		expressionTest("(T×U) ∘ r", "(T×U) ∘ r", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_R is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_R() {
		expressionTest("T × ran(r)", "r ∘ (T×U)", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		expressionTest("r ∘ (T×U)", "r ∘ (T×U)", //
				"r", "U↔V", "T", "ℙ(X)", "U", "ℙ(U)");
		expressionTest("r ∘ (T×U)", "r ∘ (T×U)", //
				"r", "W↔V", "T", "ℙ(T)", "U", "ℙ(W)");
		expressionTest("r ∘ (T×U)", "r ∘ (T×U)", //
				"r", "W↔V", "T", "ℙ(X)", "U", "ℙ(W)");
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
		expressionTest("0", "0 mod E");
		expressionTest("2 mod E", "2 mod E");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_MOD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_MOD_1() {
		expressionTest("0", "E mod 1");
		expressionTest("E mod 3", "E mod 3");
	}

	/**
	 * Ensures that rule SIMP_MIN_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_SING() {
		expressionTest("E", "min({E})", "E", "ℤ");
		expressionTest("min({E,F})", "min({E,F})");
		expressionTest("min(∅)", "min({})");
	}

	/**
	 * Ensures that rule SIMP_MAX_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_SING() {
		expressionTest("E", "max({E})", "E", "ℤ");
		expressionTest("max({E,F})", "max({E,F})");
		expressionTest("max(∅)", "max({})");
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
	 * Ensures that rule SIMP_LIT_GE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_GE_CARD_1() {
		predicateTest("¬(S = ∅)", "card(S) ≥ 1", "S", "ℙ(T)");
		predicateTest("card(S) ≥ 2", "card(S) ≥ 2", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_LE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_LE_CARD_1() {
		predicateTest("¬(S = ∅)", "1 ≤ card(S)", "S", "ℙ(T)");
		predicateTest("2 ≤ card(S)", "2 ≤ card(S)", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_LE_CARD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_LE_CARD_0() {
		predicateTest("⊤", "0 ≤ card(S)", "S", "ℙ(T)");
		predicateTest("2 ≤ card(S)", "2 ≤ card(S)", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_GE_CARD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_GE_CARD_0() {
		predicateTest("⊤", "card(S) ≥ 0", "S", "ℙ(T)");
		predicateTest("card(S) ≥ 2", "card(S) ≥ 2", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_NATURAL() {
		predicateTest("⊤", "card(S) ∈ ℕ", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_NATURAL1() {
		predicateTest("¬ S = ∅", "card(S) ∈ ℕ1", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_NATURAL() {
		predicateTest("⊤", " 0 ∈ ℕ");
		predicateTest("⊤", " 1 ∈ ℕ");
		predicateTest("⊤", " 3000000000 ∈ ℕ");
		predicateTest("⊤", " 5000000000000000000000 ∈ ℕ");
		predicateTest(" i ∈ ℕ", " i ∈ ℕ");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_NATURAL1() {
		predicateTest("⊤", " 1 ∈ ℕ1");
		predicateTest("⊤", " 3000000000 ∈ ℕ1");
		predicateTest("⊤", " 5000000000000000000000 ∈ ℕ1");
		predicateTest(" i ∈ ℕ1", " i ∈ ℕ1");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_IN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_IN_NATURAL1() {
		predicateTest("⊥", "0 ∈ ℕ1");
	}

	/**
	 * Ensures that rule SIMP_MULTI_MOD is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_MOD() {
		expressionTest("0", "E mod E", "E", "ℤ");
		expressionTest("E mod F", "E mod F", "E", "ℤ", "F", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_MINUS_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_MINUS_NATURAL() {
		predicateTest("⊥", "−(1) ∈ ℕ");
		predicateTest("⊥", "−1 ∈ ℕ");
		predicateTest("⊥", "−3000000000 ∈ ℕ");
		predicateTest("⊥", "−5000000000000000000000 ∈ ℕ");
		predicateTest("−(i) ∈ ℕ", "−(i) ∈ ℕ");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_MINUS_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_MINUS_NATURAL1() {
		predicateTest("⊥", "−(1) ∈ ℕ1");
		predicateTest("⊥", "−1 ∈ ℕ1");
		predicateTest("⊥", "−3000000000 ∈ ℕ1");
		predicateTest("⊥", "−5000000000000000000000 ∈ ℕ1");
		predicateTest("−(i) ∈ ℕ1", "−(i) ∈ ℕ1");
	}

	/**
	 * Ensures that superseded rule SIMP_CARD_COMPSET is implemented correctly,
	 * although indirectly by rule SIMP_COMPSET_IN.
	 */
	@Test
	public void testSIMP_CARD_COMPSET() {
		expressionTest("card(S)", "card({x · x∈S ∣ x})", "S", "ℙ(T)");
		expressionTest("card(S)", "card({x, y · x↦y∈S ∣ x↦y})", "S", "ℙ(T×U)");

		expressionTest("card({x, y · x↦a↦y∈S ∣ x↦a↦y})",
				"card({x, y · x↦a↦y∈S ∣ x↦a↦y})", "S", "ℙ(T×A×U)");
		expressionTest("card({x, y · x↦y∈S ∣ y↦x})",
				"card({x, y · x↦y∈S ∣ y↦x})", "S", "ℙ(T×U)");
		expressionTest("card({x, y · x↦y+1∈S ∣ x↦y+1})",
				"card({x, y · x↦y+1∈S ∣ x↦y+1})", "S", "ℙ(T×ℤ)");

		expressionTest("card({x · x∈S∪{x} ∣ x})", "card({x · x∈S∪{x} ∣ x})", //
				"S", "ℙ(T)");
		expressionTest("card({x, y · x↦y∈S∪{x↦y} ∣ x↦y})",
				"card({x, y · x↦y∈S∪{x↦y} ∣ x↦y})", //
				"S", "ℙ(T×U)");
		expressionTest("card({x, y · x↦y∈S×(U∪{y}) ∣ x↦y})",
				"card({x, y · x↦y∈S×(U∪{y}) ∣ x↦y})", //
				"S", "ℙ(T)", "U", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_DPROD_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_DPROD_CPROD() {
		expressionTest("(A ∩ C) × (B × D)", "(A × B) ⊗ (C × D)", //
				"A", "ℙ(S)", "B", "ℙ(T)", "C", "ℙ(S)", "D", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_PPROD_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_PPROD_CPROD() {
		expressionTest("(A × C) × (B × D)", "(A × B) ∥ (C × D)", //
				"A", "ℙ(S)", "B", "ℙ(T)", "C", "ℙ(U)", "D", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_CONVERSE_CPROD() {
		expressionTest("(B × A)", "(A × B)∼", "A", "ℙ(S)", "B", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_OVERL_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_OVERL_CPROD() {
		expressionTest("(S × T)  s", "r  (S × T)  s", //
				"S", "ℙ(S)", "T", "ℙ(T)");
		expressionTest("S × T", "r  (S × T)", "S", "ℙ(S)", "T", "ℙ(T)");
		expressionTest("S × T", "r  (S × T)  s  (S × T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
		expressionTest("(S × T)  s", "(S × T)  r  (S × T)  s", //
				"S", "ℙ(S)", "T", "ℙ(T)");

		expressionTest("(S × T)  r", "(S × T)  r", "S", "ℙ(S)", "T", "ℙ(T)");

		expressionTest("r  (S × T)", "r  (S × T)", "S", "ℙ(U)", "T", "ℙ(T)");
		expressionTest("r  (S × T)", "r  (S × T)", "S", "ℙ(S)", "T", "ℙ(V)");
		expressionTest("r  (S × T)", "r  (S × T)", "S", "ℙ(U)", "T", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_ID() {
		expressionTest("r ∘ s", "r ∘ id ∘ s", "r", "T↔U", "s", "S↔T");
		expressionTest("r", " r ∘ id", "r", "S↔T");
		expressionTest("r", "id ∘ r ", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_ID() {
		expressionTest("r ; s", "r ; id ; s", "r", "S↔T", "s", "T↔U");
		expressionTest("r", " r ; id", "r", "S↔T");
		expressionTest("r", "id ; r ", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_COMPSET is implemented correctly.
	 */
	@Test
	public void testSIMP_CONVERSE_COMPSET() {
		expressionTest("{x, y, z · x ↦ y ↦ z ∈ P ∣ y ↦ x}",
				"{x, y, z · x ↦ y ↦ z ∈ P∣ x ↦ y}∼", "P", "S×T↔U");

		// expressionTest("{x · x ∈ P ∣ x}∼", "{x · x ∈ P ∣ x}∼", "P", "S↔T");
		expressionTest("{x, y, z · x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℤ ∣ x ↦ y}",
				"{x, y, z · x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℤ ∣ x ↦ y}");
	}

	/**
	 * Ensures that rule SIMP_LIT_UPTO is implemented correctly.
	 */
	@Test
	public void testSIMP_LIT_UPTO() {
		expressionTest("−1‥5", "−1‥5");
		expressionTest("∅⦂ℙ(ℤ)", "5‥1");
		expressionTest("∅⦂ℙ(ℤ)", "5‥−1");
		expressionTest("∅⦂ℙ(ℤ)", "−1‥−5");
		expressionTest("∅⦂ℙ(ℤ)", "10000000000‥−50000000000");
		expressionTest("i‥j", "i‥j");
	}

	/**
	 * Ensures that rule SIMP_DOM_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_LAMBDA() {
		expressionTest("{x,y,z,t · x↦y∈P ∣ x+z+3∗t}", //
				"dom({x,y,z,t · x↦y∈P ∣ x+z+3∗t ↦ t})", //
				"P", "ℤ↔T");
		expressionTest("{x,y,z⦂ℤ,t⦂U · x↦y∈P ∣ x+z}", //
				"dom({x,y,z,t⦂U · x↦y∈P ∣ x+z ↦ t})", //
				"P", "ℤ↔T");
		expressionTest("dom({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})", //
				"dom({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})");
	}

	/**
	 * Ensures that rule SIMP_RAN_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_LAMBDA() {
		expressionTest("{x,y,z⦂ℤ,t⦂ℤ · x↦y∈P ∣ t}", //
				"ran({x,y,z,t · x↦y∈P ∣ (x+z+3∗t) ↦ t})", //
				"P", "ℤ↔T");
		expressionTest("{x,y,z⦂V · x↦y∈P ∣ x}", //
				"ran({x,y,z⦂V · x↦y∈P ∣ x↦z ↦ x})", //
				"P", "S↔T");
		expressionTest("ran({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})", //
				"ran({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})");
	}

	/**
	 * Ensures that rule SIMP_MIN_BUNION_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_BUNION_SING() {
		expressionTest("min(S ∪ T ∪ U)", "min(S ∪ {min(T)} ∪ U)");
		expressionTest("min(S ∪ T)", "min(S ∪ {min(T)})");
		expressionTest("min(S ∪ T)", "min({min(S)} ∪ T)");
		expressionTest("min(S ∪ {min(T), min(U)})", "min(S ∪ {min(T), min(U)})");
		expressionTest("min(S ∪ T ∪ U)", "min({min(S)} ∪ T ∪ {min(U)})");
		expressionTest("min(S ∪ {max(T)} ∪ U)", "min(S ∪ {max(T)} ∪ U)");
		expressionTest("max(S ∪ {min(T)} ∪ U)", "max(S ∪ {min(T)} ∪ U)");
	}

	/**
	 * Ensures that rule SIMP_MAX_BUNION_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_BUNION_SING() {
		expressionTest("max(S ∪ T ∪ U)", "max(S ∪ {max(T)} ∪ U)");
		expressionTest("max(S ∪ T)", "max(S ∪ {max(T)})");
		expressionTest("max(S ∪ T)", "max({max(S)} ∪ T)");
		expressionTest("max(S ∪ {max(T), max(U)})", "max(S ∪ {max(T), max(U)})");
		expressionTest("max(S ∪ T ∪ U)", "max({max(S)} ∪ T ∪ {max(U)})");
		expressionTest("min(S ∪ {max(T)} ∪ U)", "min(S ∪ {max(T)} ∪ U)");
		expressionTest("max(S ∪ {min(T)} ∪ U)", "max(S ∪ {min(T)} ∪ U)");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE() {
		predicateTest("⊤", "E ↦ F(E) ∈ F", "F", "S↔T");
		predicateTest("E ↦ F(G) ∈ F", "E ↦ F(G) ∈ F", "F", "S↔T");
		predicateTest("E ↦ F(E) ∈ G", "E ↦ F(E) ∈ G", "F", "S↔T");
		predicateTest("E ↦ F(E) ∈ F∼", "E ↦ F(E) ∈ F∼", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE_CONVERSE_L is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE_CONVERSE_L() {
		predicateTest("⊤", "F∼(E) ↦ E ∈ F", "F", "T↔S");
		predicateTest("F∼(E) ↦ G ∈ F", "F∼(E) ↦ G ∈ F", "F", "T↔S");
		predicateTest("F∼(E) ↦ E ∈ G", "F∼(E) ↦ E ∈ G", "F", "T↔S");
		predicateTest("F∼(E) ↦ E ∈ F∼", "F∼(E) ↦ E ∈ F∼", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE_CONVERSE_R is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE_CONVERSE_R() {
		predicateTest("⊤", "F(E) ↦ E ∈ F∼", "F", "S↔T");
		predicateTest("F(E) ↦ G ∈ F∼", "F(E) ↦ G ∈ F∼", "F", "S↔T");
		predicateTest("F(E) ↦ E ∈ G∼", "F(E) ↦ E ∈ G∼", "F", "S↔T");
		predicateTest("F(E) ↦ E ∈ F", "F(E) ↦ E ∈ F", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_SETENUM_LL is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_SETENUM_LL() {
		expressionTest("3", "{1 ↦ 3, 2 ↦ 3}(1)");
		expressionTest("FALSE", "{0 ↦ FALSE, 1 ↦ FALSE}(0)");
		expressionTest("3", "{1 ↦ 3, 2 ↦ 3}(x)");

		expressionTest("{0 ↦ FALSE, 1 ↦ TRUE}(x)", "{0 ↦ FALSE, 1 ↦ TRUE}(x)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_SETENUM_LR is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_SETENUM_LR() {
		expressionTest("Y", "{A ↦ E, X ↦ Y, B ↦ F}(X)", "X", "S", "Y", "T");
		expressionTest("Y", "{X ↦ Y}(X)", "X", "S", "Y", "T");

		expressionTest("{A ↦ E, B ↦ F}(X)", "{A ↦ E, B ↦ F}(X)", //
				"A", "S", "E", "T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_BUNION_SETENUM is implemented
	 * correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_BUNION_SETENUM() {
		expressionTest("Y", "(r ∪ {A ↦ E, X ↦ Y, B ↦ F } ∪ s)(X)", //
				"r", "S↔T", "X", "S", "Y", "T");
		expressionTest("Y", "(r ∪ {A ↦ E, X ↦ Y, B ↦ F })(X)", //
				"r", "S↔T", "X", "S", "Y", "T");
		expressionTest("Y", "({A ↦ E, X ↦ Y, B ↦ F } ∪ r)(X)", //
				"r", "S↔T", "X", "S", "Y", "T");
		expressionTest("Y", "(r ∪ {X ↦ Y} ∪ s)(X)", //
				"r", "S↔T", "X", "S", "Y", "T");

		expressionTest("(r ∪ {A ↦ E, B ↦ F } ∪ s)(X)",
				"(r ∪ {A ↦ E, B ↦ F } ∪ s)(X)", //
				"r", "S↔T", "X", "S", "Y", "T");
	}

	/**
	 * Ensures that rule SIMP_LIT_MIN is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_MIN() {
		expressionTest("min({−3})", "min({−2, 1, −3})");
		expressionTest("min({A, B})", "min({A, B})");
		expressionTest("min({A, B, −100000000000, C})",
				"min({A, −3, −1, B, −100000000000, C})");
	}

	/**
	 * Ensures that rule SIMP_LIT_MAX is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_MAX() {
		expressionTest("max({1})", "max({−2, 1, −3})");
		expressionTest("max({A, B})", "max({A, B})");
		expressionTest("max({A, B, 100000000000, C})",
				"max({A, 3, 1, B, 100000000000, C})");
	}

	/**
	 * Ensures that rule SIMP_FINITE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_ID() {
		predicateTest("finite(S)", "finite(id⦂S↔S)", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_ID_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_ID_DOMRES() {
		predicateTest("finite(E)", "finite(E ◁ id)", "E", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ1 is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ1() {
		predicateTest("finite(S × T)", "finite(prj1⦂ℙ(S×T×S))", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ1_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ1_DOMRES() {
		predicateTest("finite(E)", "finite(E ◁ prj1)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ2 is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ2() {
		predicateTest("finite(S × T)", "finite(prj2⦂S×T↔T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ2_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ2_DOMRES() {
		predicateTest("finite(E)", "finite(E ◁ prj2)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_ID is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_ID() {
		expressionTest("card(S)", "card(id⦂S↔S)", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_CARD_ID_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_ID_DOMRES() {
		expressionTest("card(E)", "card(E ◁ id)", "E", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ1 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ1() {
		expressionTest("card(S × T)", "card(prj1⦂S×T↔S)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ1_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ1_DOMRES() {
		expressionTest("card(E)", "card(E ◁ prj1)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ2 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ2() {
		expressionTest("card(S × T)", "card(prj2⦂S×T↔T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ2_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ2_DOMRES() {
		expressionTest("card(E)", "card(E ◁ prj2)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_EQUAL_BINTER is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_EQUAL_BINTER() {
		predicateTest("T ⊆ S ∩ U", "S ∩ T ∩ U = T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");
		predicateTest("T ⊆ S", "S ∩ T = T", "S", "ℙ(V)", "T", "ℙ(V)");
		predicateTest("T ⊆ U", "T ∩ U = T", "T", "ℙ(V)", "U", "ℙ(V)");
		predicateTest("T ⊆ S ∩ U", "S ∩ T ∩ T ∩ U = T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");

		predicateTest("⊤", "T ∩ T = T", "T", "ℙ(U)");

		predicateTest("S ∩ T ∩ U = V", "S ∩ T ∩ U = V", //
				"S", "ℙ(W)", "T", "ℙ(W)", "U", "ℙ(W)", "V", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_EQUAL_BUNION is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_EQUAL_BUNION() {
		predicateTest("S ∪ U ⊆ T", "S ∪ T ∪ U = T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");
		predicateTest("S ⊆ T", "S ∪ T = T", "S", "ℙ(V)", "T", "ℙ(V)");
		predicateTest("U ⊆ T", "T ∪ U = T", "T", "ℙ(V)", "U", "ℙ(V)");
		predicateTest("S ∪ U ⊆ T", "S ∪ T ∪ T ∪ U = T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");

		predicateTest("⊤", "T ∪ T = T", "T", "ℙ(U)");

		predicateTest("S ∪ T ∪ U = V", "S ∪ T ∪ U = V", //
				"S", "ℙ(W)", "T", "ℙ(W)", "U", "ℙ(W)", "V", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_SUBSET_L is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_SUBSET_L() {
		predicateTest("¬ S = ∅", "∅ ⊂ S", "S", "ℙ(T)");
		predicateTest("⊤", "∅ ⊆ S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_DOMRES_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMRES_DOMRES_ID() {
		expressionTest("(S ∩ T) ◁ id", "S ◁ (T ◁ id)", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_DOMRES_ID() {
		expressionTest("(S ∩ T) ◁ id", "(S ◁ id) ▷ T", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMSUB_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMSUB_DOMRES_ID() {
		expressionTest("(T ∖ S) ◁ id", "S ⩤ (T ◁ id)", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_DOMRES_ID() {
		expressionTest("(S ∖ T) ◁ id", "(S ◁ id) ⩥ T", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMRES_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMRES_DOMSUB_ID() {
		expressionTest("(S ∖ T) ◁ id", "S ◁ (T ⩤ id)", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_DOMSUB_ID() {
		expressionTest("(T ∖ S) ◁ id", "(S ⩤ id) ▷ T", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMSUB_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMSUB_DOMSUB_ID() {
		expressionTest("(S ∪ T) ⩤ id", "S ⩤ (T ⩤ id)", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_DOMSUB_ID() {
		expressionTest("(S ∪ T) ⩤ id", "(S ⩤ id) ⩥ T", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_ID() {
		expressionTest("S ◁ id", "id ▷ S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_ID() {
		expressionTest("S ⩤ id", "id ⩥ S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOMSUB_RAN is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_DOMSUB_RAN() {
		expressionTest("∅⦂T↔S", "ran(r) ⩤ r∼", "r", "S↔T");

		expressionTest("ran(r) ⩤ s∼", "ran(r) ⩤ s∼", "r", "S↔T", "s", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RANSUB_DOM is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_RANSUB_DOM() {
		expressionTest("∅⦂T↔S", "r∼ ⩥ dom(r)", "r", "S↔T");

		expressionTest("s∼ ⩥ dom(r)", "s∼ ⩥ dom(r)", "r", "S↔T", "s", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_FCOMP_ID() {
		expressionTest("r ; ((S ∩ T) ◁ id) ; s", "r ; (S ◁ id) ; (T ◁ id) ; s", //
				"r", "A↔B", "s", "B↔C");
		expressionTest(
				"r ; ((S ∖ T) ◁ id) ; s ; (U ⩤ id) ; t ; ((V ∪ W) ⩤ id) ; u", //
				"r ; (S ◁ id) ; (T ⩤ id) ; s ; (U ⩤ id) ; t ; (V ⩤ id) ; (W ⩤ id) ; u",//
				"r", "A↔B", "s", "B↔C", "t", "C↔D", "u", "D↔E");

		expressionTest("r ; (S ◁ id) ; s", "r ; (S ◁ id) ; s", //
				"r", "A↔B", "s", "B↔C");
	}

	/**
	 * Ensures that rule SIMP_MULTI_OVERL is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_OVERL() {
		expressionTest("s", "s  s", "s", "A↔B");
		expressionTest("r  t  s  u", "r  s  t  s  u", "r", "A↔B");

		expressionTest("r  s  t", "r  s  t", "r", "A↔B");
	}

	/**
	 * Ensures that rule SIMP_BCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_BCOMP_ID() {
		expressionTest("r ∘ ((S ∩ T) ◁ id) ∘ s", "r ∘ (S ◁ id) ∘ (T ◁ id) ∘ s", //
				"r", "B↔C", "s", "A↔B");
		expressionTest(
				"r ∘ ((S ∖ T) ◁ id) ∘ s ∘ (U ⩤ id) ∘ t ∘ ((V ∪ W) ⩤ id) ∘ u", //
				"r ∘ (S ◁ id) ∘ (T ⩤ id) ∘ s ∘ (U ⩤ id) ∘ t ∘ (V ⩤ id) ∘ (W ⩤ id) ∘ u",//
				"r", "D↔E", "s", "C↔D", "t", "B↔C", "u", "A↔B");

		expressionTest("r ∘ (S ◁ id) ∘ s", "r ∘ (S ◁ id) ∘ s", //
				"r", "B↔C", "s", "A↔B");
	}

	/**
	 * Ensures that rule SIMP_SUBSETEQ_COMPSET_L is implemented correctly
	 */
	@Test
	public void testSIMP_SUBSETEQ_COMPSET_L() {
		// predicateTest("∀y · y∈ℕ ⇒ y∈S", "{x · x∈ℕ ∣ x} ⊆ S", "S", "ℙ(ℤ)");
		predicateTest("∀x, y · x∈ℤ∧y∈ℤ ⇒ x+y∈S",
				"{x, y · x∈ℤ∧y∈ℤ ∣ x+y } ⊆ S ", "S", "ℙ(ℤ)");
		predicateTest("∀x, y · x+y=a ⇒ x+y∈S", "{x, y · x+y=a ∣ x+y} ⊆ S", //
				"a", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_COMPSET is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_COMPSET() {
		// predicateTest("∀x · ¬x∈ℕ", "{x · x∈ℕ ∣ x} = ∅", "S", "ℙ(ℤ)");
		predicateTest("∀x, y · ¬(x∈ℤ∧y∈ℤ)", "{x, y · x∈ℤ∧y∈ℤ ∣ x+y } = ∅ ", //
				"S", "ℙ(ℤ)");
		predicateTest("∀x, y · ¬(x+y=a)", "{x, y · x+y=a ∣ x+y} = ∅", //
				"a", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RELIMAGE_ID() {
		expressionTest("S ∩ T", "(S ◁ id)[T]", "S", "ℙ(A)", "T", "ℙ(A)");
	}

	/**
	 * Ensures that rule SIMP_COMPSET_IN is implemented correctly
	 */
	@Test
	public void testSIMP_COMPSET_IN() {
		expressionTest("S", "{x · x∈S ∣ x}", "S", "ℙ(T)");
		expressionTest("S", "{x, y · x↦y∈S ∣ x↦y}", "S", "ℙ(T×U)");
		expressionTest("{y · y ⊆ A ∧ finite(y) ∣ y}", //
				"{y · y ⊆ A ∧ finite(y) ∣ {x · x∈y ∣ x}}",//
				"A", "ℙ(S)");

		expressionTest("{x, y · x↦a↦y∈S ∣ x↦a↦y}", "{x, y · x↦a↦y∈S ∣ x↦a↦y}",
				"S", "ℙ(T×A×U)");
		expressionTest("{x, y · x↦y∈S ∣ y↦x}", "{x, y · x↦y∈S ∣ y↦x}", "S",
				"ℙ(T×U)");
		expressionTest("{x, y · x↦y+1∈S ∣ x↦y+1}", "{x, y · x↦y+1∈S ∣ x↦y+1}",
				"S", "ℙ(T×ℤ)");

		expressionTest("{x · x∈S∪{x} ∣ x}", "{x · x∈S∪{x} ∣ x}", "S", "ℙ(T)");
		expressionTest("{x, y · x↦y∈S×(U∪{y}) ∣ x↦y}",
				"{x, y · x↦y∈S×(U∪{y}) ∣ x↦y}", "S", "ℙ(T)", "U", "ℙ(V)");
		expressionTest("{x, y⦂ℙ(T) · x∈y ∣ x}", "{x, y⦂ℙ(T) · x∈y ∣ x}",//
				"T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_IN_COMPSET_ONEPOINT is implemented correctly
	 */
	@Test
	public void testSIMP_IN_COMPSET_ONEPOINT() {
		predicateTest("1≥0", "1 ∈ {x · x≥0 ∣ x}");
		predicateTest("1≥0 ∧ 2>1", "1↦2 ∈ {x,y · x≥0 ∧ y>x ∣ x↦y}");
		predicateTest("∃y · 1≥0 ∧ y>1", "1 ∈ {x,y · x≥0 ∧ y>x ∣ x}");
		predicateTest("∃x,y ·  x↦y=a ∧ (x≥0 ∧ y>x ∧ b>2∗y)",
				"a↦b ∈ {x,y,z · x≥0 ∧ y>x ∧ z>2∗y ∣ (x↦y)↦z}");

		predicateTest("a=0 ∧ b=0", "a↦b ∈ {x,y · x=0 ∧ y=0 ∣ x↦y}");
		predicateTest("a=b ∧ a≥0", "a↦b ∈ {x · x≥0 ∣ x↦x}");
		predicateTest("∃y · y+1=b ∧ a>y+c", "a↦b↦c ∈ {x,y,z · x>y+z ∣ x↦y+1↦z}");

		predicateTest("∃y · y+1=b ∧ (a=0 ∧ y=0)",
				"a↦b ∈ {x,y · x=0 ∧ y=0 ∣ x↦y+1}");

		predicateTest("∃x ·  x≥0 ∧ x+1=1", "1 ∈ {x · x≥0 ∣ x+1}");
		predicateTest("∃x,y · (x≥0 ∧ y>x) ∧ x↦y=a",
				"a ∈ {x,y · x≥0 ∧ y>x ∣ x↦y}");
	}

}