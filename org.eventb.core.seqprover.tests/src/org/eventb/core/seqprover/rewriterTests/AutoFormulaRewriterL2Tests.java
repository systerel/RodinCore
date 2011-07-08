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
	 * Ensures that rule SIMP_SUBSETEQ_SING is implemented correctly.
	 */
	@Test
	public void testSIMP_SUBSETEQ_SING() {
		rewritePred("{0} ⊆ S", "0 ∈ S");
		rewritePred("{0} ⊆ ℤ", "0 ∈ ℤ");
		rewritePred("{0} ⊆ ℕ1", "0 ∈ ℕ1");

		noRewritePred("{0,1} ⊆ ℤ");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_POW() {
		rewriteExpr("ℙ(∅⦂ℙ(S))", "{∅⦂ℙ(S)}");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_POW1 is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_POW1() {
		rewriteExpr("ℙ1(∅⦂ℙ(S))", "∅⦂ℙ(ℙ(S))");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_CPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CPROD_R() {
		rewriteExpr("S × (∅⦂ℙ(V))", "∅⦂U↔V", "S", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_CPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CPROD_L() {
		rewriteExpr("(∅⦂ℙ(U)) × S", "∅⦂U↔V", "S", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_NATURAL is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_NATURAL() {
		rewritePred("finite(ℕ)", "⊥");
	}

	/**
	 * Ensures that rule SIMP_FINITE_NATURAL1 is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_NATURAL1() {
		rewritePred("finite(ℕ1)", "⊥");
	}

	/**
	 * Ensures that rule SIMP_FINITE_INTEGER is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_INTEGER() {
		rewritePred("finite(ℤ)", "⊥");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_SUBSET_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_SUBSET_R() {
		rewritePred("S ⊂ (∅⦂ℙ(T))", "⊥");
	}

	/**
	 * Ensures that rule SIMP_MULTI_SUBSET is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_SUBSET() {
		rewritePred("S ⊂ S", "⊥", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_DOM_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_CONVERSE() {
		rewriteExpr("dom(r∼)", "ran(r)", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_RAN_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_CONVERSE() {
		rewriteExpr("ran(r∼)", "dom(r)", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DOMRES_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMRES_L() {
		rewriteExpr("(∅⦂ℙ(S)) ◁ r", "∅⦂S↔T", "r", "S↔T");
		// Former rules SIMP_SPECIAL_(ID|PRJ1|PRJ2)
		rewriteExpr("∅ ◁ (id⦂S↔S)", "∅⦂S↔S");
		rewriteExpr("∅ ◁ (prj1⦂S×T↔S)", "∅⦂S×T↔S");
		rewriteExpr("∅ ◁ (prj2⦂S×T↔T)", "∅⦂S×T↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DOMRES_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMRES_R() {
		rewriteExpr("S ◁ (∅⦂U↔V)", "∅⦂U↔V", "S", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_DOMRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMRES() {
		rewriteExpr("S ◁ r", "r", "r", "S↔T");
		noRewriteExpr("S ◁ r", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOMRES_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMRES_DOM() {
		rewriteExpr("dom(r) ◁ r", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOMRES_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMRES_RAN() {
		rewriteExpr("ran(r) ◁ r∼", "r∼", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_RANRES_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANRES_R() {
		rewriteExpr("r ▷ (∅⦂ℙ(T))", "∅⦂S↔T", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_RANRES_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANRES_L() {
		rewriteExpr("(∅⦂U↔V) ▷ S", "∅⦂U↔V", "S", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_RANRES is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANRES() {
		rewriteExpr("r ▷ T", "r", "r", "S↔T");
		noRewriteExpr("r ▷ T", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RANRES_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANRES_RAN() {
		rewriteExpr("r ▷ ran(r)", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RANRES_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANRES_DOM() {
		rewriteExpr("r∼ ▷ dom(r)", "r∼", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DOMSUB_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMSUB_L() {
		rewriteExpr("∅ ⩤ r", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DOMSUB_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DOMSUB_R() {
		rewriteExpr("S ⩤ (∅⦂U↔V)", "∅⦂U↔V");
	}

	/**
	 * Ensures that rule SIMP_TYPE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOMSUB() {
		rewriteExpr("S ⩤ r", "∅⦂S↔T", "r", "S↔T");
		noRewriteExpr("S ⩤ r", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOMSUB_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOMSUB_DOM() {
		rewriteExpr("dom(r) ⩤ r", "∅⦂S↔T", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_RANSUB_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANSUB_R() {
		rewriteExpr("r ⩥ ∅", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_RANSUB_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_RANSUB_L() {
		rewriteExpr("(∅⦂U↔V) ⩥ S", "∅⦂U↔V");
	}

	/**
	 * Ensures that rule SIMP_TYPE_RANSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RANSUB() {
		rewriteExpr("r ⩥ T", "∅⦂S↔T", "r", "S↔T");
		noRewriteExpr("r ⩥ T", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RANSUB_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RANSUB_RAN() {
		rewriteExpr("r ⩥ ran(r)", "∅⦂S↔T", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DPROD_R() {
		rewriteExpr("r ⊗ (∅⦂S↔U)", "∅⦂S↔T×U", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_DPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_DPROD_L() {
		rewriteExpr("(∅⦂S↔T) ⊗ r", "∅⦂S↔T×U", "r", "S↔U");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_PPROD_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_PPROD_R() {
		rewriteExpr("r ∥ (∅⦂U↔V)", "∅⦂S×U↔T×V", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_PPROD_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_PPROD_L() {
		rewriteExpr("(∅⦂S↔T) ∥ r", "∅⦂S×U↔T×V", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_TYPE_RELIMAGE is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RELIMAGE() {
		rewriteExpr("r[S]", "ran(r)", "r", "S↔T");
		noRewriteExpr("r[U]", "r", "S↔T", "U", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_DOM() {
		rewriteExpr("r[dom(r)]", "ran(r)", "r", "S↔T");
		noRewriteExpr("r[dom(s)]", "r", "S↔T", "s", "S↔V");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_RELIMAGE_ID() {
		rewriteExpr("id[T]", "T", "T", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CPROD_SING is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CPROD_SING() {
		rewriteExpr("({E}×S)[{E}]", "S", "S", "ℙ(U)", "E", "V");
		noRewriteExpr("({E}×S)[{F}]", "S", "ℙ(U)", "E", "V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_SING_MAPSTO is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_SING_MAPSTO() {
		rewriteExpr("{E ↦ F}[{E}]", "{F}", "E", "S", "F", "T");
		noRewriteExpr("{E ↦ F}[{G}]", "E", "S", "F", "T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CONVERSE_RANSUB() {
		rewriteExpr("(r ⩥ S)∼[S]", "∅⦂ℙ(U)", "r", "U↔V");
		noRewriteExpr("(r ⩥ S)∼[T]", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_CONVERSE_RANRES is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_CONVERSE_RANRES() {
		rewriteExpr("(r ▷ S)∼[S]", "r∼[S]", "r", "U↔V");
		noRewriteExpr("(r ▷ S)∼[T]", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_CONVERSE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_RELIMAGE_CONVERSE_DOMSUB() {
		rewriteExpr("(S ⩤ r)∼[T]", "r∼[T]∖S", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RELIMAGE_DOMSUB is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RELIMAGE_DOMSUB() {
		rewriteExpr("(S ⩤ r)[S]", "∅⦂ℙ(V)", "r", "U↔V");
		noRewriteExpr("(S ⩤ r)[T]", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_CONVERSE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_CONVERSE() {
		rewriteExpr("(∅⦂S↔T)∼", "∅⦂T↔S");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_CONVERSE_ID() {
		rewriteExpr("(id⦂S↔S)∼", "id⦂S↔S");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_ID_L is implemented correctly.
	 */
	@Test
	public void testSIMP_FCOMP_ID_L() {
		rewriteExpr("(S ◁ id) ; r", "S ◁ r", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_ID_R is implemented correctly.
	 */
	@Test
	public void testSIMP_FCOMP_ID_R() {
		rewriteExpr("r ; (S ◁ id)", "r ▷ S", "r", "U↔V");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_REL_R is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_REL_R() {
		rewriteExpr("S ↔ (∅⦂ℙ(V))", "{∅⦂U↔V}", "S", "ℙ(U)");
		// this test is the surjective relation <->>
		rewriteExpr("S  (∅⦂ℙ(V))", "{∅⦂U↔V}", "S", "ℙ(U)");
		rewriteExpr("S ⇸ (∅⦂ℙ(V))", "{∅⦂U↔V}", "S", "ℙ(U)");
		rewriteExpr("S ⤔ (∅⦂ℙ(V))", "{∅⦂U↔V}", "S", "ℙ(U)");
		rewriteExpr("S ⤀ (∅⦂ℙ(V))", "{∅⦂U↔V}", "S", "ℙ(U)");

		noRewriteExpr("S → (∅⦂ℙ(V))", "S", "ℙ(U)");
		// this test is the total relation <<->
		noRewriteExpr("S  (∅⦂ℙ(V))", "S", "ℙ(U)");
		// this test is the surjective total relation <<->>
		noRewriteExpr("S  (∅⦂ℙ(V))", "S", "ℙ(U)");
		noRewriteExpr("S ↣ (∅⦂ℙ(V))", "S", "ℙ(U)");
		noRewriteExpr("S ↠ (∅⦂ℙ(V))", "S", "ℙ(U)");
		noRewriteExpr("S ⤖ (∅⦂ℙ(V))", "S", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_REL_L is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_REL_L() {
		rewriteExpr("(∅⦂ℙ(U)) ↔ S", "{∅⦂U↔V}", "S", "ℙ(V)");
		// this test is the total relation <<->
		rewriteExpr("(∅⦂ℙ(U))  S", "{∅⦂U↔V}", "S", "ℙ(V)");
		rewriteExpr("(∅⦂ℙ(U)) ⇸ S", "{∅⦂U↔V}", "S", "ℙ(V)");
		rewriteExpr("(∅⦂ℙ(U)) → S", "{∅⦂U↔V}", "S", "ℙ(V)");
		rewriteExpr("(∅⦂ℙ(U)) ⤔ S", "{∅⦂U↔V}", "S", "ℙ(V)");
		rewriteExpr("(∅⦂ℙ(U)) ↣ S", "{∅⦂U↔V}", "S", "ℙ(V)");

		// this test is the surjective relation <->>
		noRewriteExpr("(∅⦂ℙ(U))  S", "S", "ℙ(V)");
		// this test is the surjective total relation <<->>
		noRewriteExpr("(∅⦂ℙ(U))  S", "S", "ℙ(V)");
		noRewriteExpr("(∅⦂ℙ(U)) ⤀ S", "S", "ℙ(V)");
		noRewriteExpr("(∅⦂ℙ(U)) ↠ S", "S", "ℙ(V)");
		noRewriteExpr("(∅⦂ℙ(U)) ⤖ S", "S", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_FUNIMAGE_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_PRJ1() {
		rewriteExpr("prj1(E ↦ F)", "E", "E", "S", "F", "T");
	}

	/**
	 * Ensures that rule SIMP_FUNIMAGE_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_PRJ2() {
		rewriteExpr("prj2(E ↦ F)", "F", "E", "S", "F", "T");
	}

	/**
	 * Ensures that rule SIMP_FUNIMAGE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_FUNIMAGE_ID() {
		rewriteExpr("id(x)", "x", "x", "S");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_RELDOMRAN is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_RELDOMRAN() {
		// this test is the surjective total relation <<->>
		rewriteExpr("(∅⦂ℙ(S))  (∅⦂ℙ(T))", "{∅⦂ℙ(S×T)}");
		rewriteExpr("(∅⦂ℙ(S)) ↠ (∅⦂ℙ(T))", "{∅⦂ℙ(S×T)}");
		rewriteExpr("(∅⦂ℙ(S)) ⤖ (∅⦂ℙ(T))", "{∅⦂ℙ(S×T)}");

		// negative tests for the other types of relations are not written
		// because they are previously matched by SIMP_SPECIAL_REL_L and
		// SIMP_SPECIAL_REL_R
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOM_CPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_DOM_CPROD() {
		rewriteExpr("dom(E×E)", "E", "E", "ℙ(S)");
		noRewriteExpr("dom(E×F)", "E", "ℙ(S)", "F", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RAN_CPROD is implemented correctly.
	 */
	@Test
	public void testSIMP_MULTI_RAN_CPROD() {
		rewriteExpr("ran(E×E)", "E", "E", "ℙ(S)");
		noRewriteExpr("ran(E×F)", "E", "ℙ(S)", "F", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_COMPSET_BFALSE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_COMPSET_BFALSE() {
		rewriteExpr("{x⦂S · ⊥ ∣ E}", "∅⦂ℙ(S)", "E", "S");
		rewriteExpr("{x⦂S · ⊥ ∣ x}", "∅⦂ℙ(S)");
		rewriteExpr("{x · ⊥ ∣ x+1}", "∅⦂ℙ(ℤ)");
		rewriteExpr("{x⦂S, y⦂S · ⊥ ∣ E}", "∅⦂ℙ(S)", "E", "S");
		rewriteExpr("{x⦂S, y⦂S · ⊥ ∣ y}", "∅⦂ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_COMPSET_BTRUE is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_COMPSET_BTRUE() {
		rewriteExpr("{x⦂S · ⊤ ∣ x}", "S", "S", "ℙ(S)");
		rewriteExpr("{x⦂S, y⦂T · ⊤ ∣ x↦y}", "S×T", "S", "ℙ(S)", "T", "ℙ(T)");
		rewriteExpr("{x⦂S, y⦂T · ⊤ ∣ y↦x}", "T×S", "S", "ℙ(S)", "T", "ℙ(T)");
		rewriteExpr("{x⦂S, y⦂T, z⦂U · ⊤ ∣ y↦x↦z}", "T×S×U", //
				"S", "ℙ(S)", "T", "ℙ(T)", "U", "ℙ(U)");
		rewriteExpr("{x⦂S, y⦂T, z⦂U, t⦂V · ⊤ ∣ x↦(y↦z)↦t}", "S×(T×U)×V",
				"S", "ℙ(S)", "T", "ℙ(T)", "U", "ℙ(U)", "V", "ℙ(V)");

		noRewriteExpr("{x⦂S, y · ⊤ ∣ x↦y+1}", "S", "ℙ(S)");
		noRewriteExpr("{x⦂S, y⦂T · ⊤ ∣ x↦a↦y}", "a", "A");
		noRewriteExpr("{x⦂S, y⦂T · ⊤ ∣ x↦y↦x}");
	}

	/**
	 * Ensures that rule SIMP_KUNION_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_KUNION_POW() {
		rewriteExpr("union(ℙ(S))", "S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_KUNION_POW1 is implemented correctly.
	 */
	@Test
	public void testSIMP_KUNION_POW1() {
		rewriteExpr("union(ℙ1(S))", "S", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_KUNION is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_KUNION() {
		rewriteExpr("union({∅⦂ℙ(S)})", "∅⦂ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_QUNION is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_QUNION() {
		rewriteExpr("⋃ x⦂S · ⊥ ∣ E", "∅⦂ℙ(S)", "E", "ℙ(S)");
		rewriteExpr("⋃ x⦂S · ⊥ ∣ {x}", "∅⦂ℙ(S)");
		rewriteExpr("⋃ x⦂S, y⦂S · ⊥ ∣ E", "∅⦂ℙ(S)", "E", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_KINTER is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_KINTER() {
		rewriteExpr("inter({∅⦂ℙ(S)})", "∅⦂ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_KINTER_POW is implemented correctly.
	 */
	@Test
	public void testSIMP_KINTER_POW() {
		rewriteExpr("inter(ℙ(S))", "∅⦂ℙ(T)", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_REL is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_REL() {
		rewritePred("A ↔ B = ∅", "⊥", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A ⇸ B = ∅", "⊥", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A ⤔ B = ∅", "⊥", "A", "ℙ(S)", "B", "ℙ(T)");

		// this test is the surjective relation
		noRewritePred("A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		// this test is the surjective total relation
		noRewritePred("A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		noRewritePred("A ⤀ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// negative tests for the other types of relations are not written
		// because they are matched by SIMP_SPECIAL_EQUAL_RELDOM
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_RELDOM is implemented correctly.
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_RELDOM() {
		// this test is the total relation
		rewritePred("A  B = ∅", "¬ A=∅ ∧ B=∅", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A → B = ∅", "¬ A=∅ ∧ B=∅", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A ↣ B = ∅", "¬ A=∅ ∧ B=∅", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A ↠ B = ∅", "¬ A=∅ ∧ B=∅", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("A ⤖ B = ∅", "¬ A=∅ ∧ B=∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// this test is the surjective relation
		noRewritePred("A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		// this test is the surjective total relation
		noRewritePred("A  B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");
		noRewritePred("A ⤀ B = ∅", "A", "ℙ(S)", "B", "ℙ(T)");

		// negative tests for the other types of relations are not written
		// because they are matched by SIMP_SPECIAL_EQUAL_REL
	}

	/**
	 * Ensures that rule SIMP_FINITE_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_LAMBDA() {
		rewritePred("finite({x,y,z⦂U · x↦y∈P ∣ x↦E↦z ↦ z})", //
				"finite({x,y,z⦂U · x↦y∈P ∣ x↦E↦z})", //
				"P", "S↔T", "E", "ℙ(V)");
		rewritePred("finite({x,y,z⦂U,t⦂ℤ · x↦y∈P ∣ x↦z↦3∗t ↦ z})", //
				"finite({x,y,z⦂U,t⦂ℤ · x↦y∈P ∣ x↦z↦3∗t})", //
				"P", "S↔T");
		rewritePred("finite({x,y,z,t · x↦y∈P ∣ x↦z+t ↦ x})", //
				"finite({x,y,z,t · x↦y∈P ∣ x↦z+t})", //
				"P", "S↔T");
		rewritePred("finite({x,y,z,t · x↦y∈P ∣ z+t↦x ↦ x})", //
				"finite({x,y,z,t · x↦y∈P ∣ z+t↦x})", //
				"P", "S↔T");
		rewritePred("finite({x,y,z,t · x↦y∈P ∣ t↦z+3∗t ↦ t})", //
				"finite({x,y,z,t · x↦y∈P ∣ t↦z+3∗t})", //
				"P", "S↔T");
		rewritePred("finite({x,y,z,t · x↦y∈P ∣ z↦3∗t↦t ↦ z+t})", //
				"finite({x,y,z⦂ℤ,t · x↦y∈P ∣ z↦3∗t↦t})", //
				"P", "S↔T");

		noRewritePred("finite({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"P", "S↔T");
		noRewritePred("finite({x,y,z⦂U,t · x↦y∈P ∣ z↦3∗t ↦ t})", //
				"P", "S↔T");
		noRewritePred("finite({x,y,z⦂U,t⦂V · x↦y∈P ∣ (x↦z) ↦ (x↦t)})", //
				"P", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_CARD_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_CARD_LAMBDA() {
		// The SIMP_CARD_LAMBDA rule implementation uses the same algorithm as
		// SIMP_FINITE_LAMBDA, therefore minimal testing is performed

		rewriteExpr("card({x,y,z,t · x↦y∈P ∣ z↦3∗t↦t ↦ z+t})", //
				"card({x,y,z⦂ℤ,t · x↦y∈P ∣ z↦3∗t↦t})", //
				"P", "S↔T");

		noRewriteExpr("card({x,y,z,t · x↦y∈P ∣ z↦3∗t+z ↦ z+t})", //
				"P", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_R is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_R() {
		rewriteExpr("r ; (T×U)", "dom(r) × U", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(U)");
		noRewriteExpr("r ; (T×U)", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(W)");
		noRewriteExpr("r ; (T×U)", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(U)");
		noRewriteExpr("r ; (T×U)", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_L is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_L() {
		rewriteExpr("(T×U) ; r", "T × ran(r)", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		noRewriteExpr("(T×U) ; r", //
				"r", "U↔V", "T", "ℙ(X)", "U", "ℙ(U)");
		noRewriteExpr("(T×U) ; r", //
				"r", "W↔V", "T", "ℙ(T)", "U", "ℙ(W)");
		noRewriteExpr("(T×U) ; r", //
				"r", "W↔V", "T", "ℙ(X)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_L is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_L() {
		rewriteExpr("(T×U) ∘ r", "dom(r) × U", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(U)");
		noRewriteExpr("(T×U) ∘ r", //
				"r", "S↔T", "T", "ℙ(T)", "U", "ℙ(W)");
		noRewriteExpr("(T×U) ∘ r", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(U)");
		noRewriteExpr("(T×U) ∘ r", //
				"r", "S↔V", "T", "ℙ(V)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_R is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_R() {
		rewriteExpr("r ∘ (T×U)", "T × ran(r)", //
				"r", "U↔V", "T", "ℙ(T)", "U", "ℙ(U)");
		noRewriteExpr("r ∘ (T×U)", //
				"r", "U↔V", "T", "ℙ(X)", "U", "ℙ(U)");
		noRewriteExpr("r ∘ (T×U)", //
				"r", "W↔V", "T", "ℙ(T)", "U", "ℙ(W)");
		noRewriteExpr("r ∘ (T×U)", //
				"r", "W↔V", "T", "ℙ(X)", "U", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_DOM_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_ID() {
		rewriteExpr("dom(id⦂S↔S)", "S", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_RAN_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_ID() {
		rewriteExpr("ran(id⦂S↔S)", "S", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_DOM_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_PRJ1() {
		rewriteExpr("dom(prj1⦂ℙ(S×T×S))", "S × T", "S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_DOM_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_PRJ2() {
		rewriteExpr("dom(prj2⦂ℙ(S×T×T))", "S × T", "S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_RAN_PRJ1 is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_PRJ1() {
		rewriteExpr("ran(prj1⦂ℙ(S×T×S))", "S", "S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_RAN_PRJ2 is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_PRJ2() {
		rewriteExpr("ran(prj2⦂ℙ(S×T×T))", "T", "S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_DOM is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_DOM() {
		rewriteExpr("dom(A×B)", "A", "A", "ℙ(A)", "B", "ℙ(B)");
		noRewriteExpr("dom(A×B)", "A", "ℙ(S)", "B", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_RAN is implemented correctly.
	 */
	@Test
	public void testSIMP_TYPE_RAN() {
		rewriteExpr("ran(A×B)", "B", "A", "ℙ(A)", "B", "ℙ(B)");
		noRewriteExpr("ran(A×B)", "A", "ℙ(S)", "B", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_MOD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_MOD_0() {
		rewriteExpr("0 mod E", "0");
		noRewriteExpr("2 mod E");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_MOD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_MOD_1() {
		rewriteExpr("E mod 1", "0");
		noRewriteExpr("E mod 3");
	}

	/**
	 * Ensures that rule SIMP_MIN_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_SING() {
		rewriteExpr("min({E})", "E", "E", "ℤ");
		noRewriteExpr("min({E,F})");
		rewriteExpr("min({})", "min(∅)");
	}

	/**
	 * Ensures that rule SIMP_MAX_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_SING() {
		rewriteExpr("max({E})", "E", "E", "ℤ");
		noRewriteExpr("max({E,F})");
		rewriteExpr("max({})", "max(∅)");
	}

	/**
	 * Ensures that rule SIMP_MIN_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_NATURAL() {
		rewriteExpr("min(ℕ)", "0");
		noRewriteExpr("min(ℤ)");
	}

	/**
	 * Ensures that rule SIMP_MIN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_NATURAL1() {
		rewriteExpr("min(ℕ1)", "1");
	}

	/**
	 * Ensures that rule SIMP_MIN_UPTO is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_UPTO() {
		rewriteExpr("min(E‥F)", "E", "E", "ℤ", "F", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_MAX_UPTO is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_UPTO() {
		rewriteExpr("max(E‥F)", "F", "E", "ℤ", "F", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_CARD_CONVERSE is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_CONVERSE() {
		rewriteExpr("card(r∼)", "card(r)", "r", "S↔T");
		noRewriteExpr("card(r)", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_LIT_GE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_GE_CARD_1() {
		rewritePred("card(S) ≥ 1", "¬(S = ∅)", "S", "ℙ(T)");
		noRewritePred("card(S) ≥ 2", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_LE_CARD_1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_LE_CARD_1() {
		rewritePred("1 ≤ card(S)", "¬(S = ∅)", "S", "ℙ(T)");
		noRewritePred("2 ≤ card(S)", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_LE_CARD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_LE_CARD_0() {
		rewritePred("0 ≤ card(S)", "⊤", "S", "ℙ(T)");
		noRewritePred("2 ≤ card(S)", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_GE_CARD_0 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_GE_CARD_0() {
		rewritePred("card(S) ≥ 0", "⊤", "S", "ℙ(T)");
		noRewritePred("card(S) ≥ 2", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_NATURAL() {
		rewritePred("card(S) ∈ ℕ", "⊤", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_NATURAL1() {
		rewritePred("card(S) ∈ ℕ1", "¬ S = ∅", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_NATURAL() {
		rewritePred(" 0 ∈ ℕ", "⊤");
		rewritePred(" 1 ∈ ℕ", "⊤");
		rewritePred(" 3000000000 ∈ ℕ", "⊤");
		rewritePred(" 5000000000000000000000 ∈ ℕ", "⊤");
		noRewritePred(" i ∈ ℕ");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_NATURAL1() {
		rewritePred(" 1 ∈ ℕ1", "⊤");
		rewritePred(" 3000000000 ∈ ℕ1", "⊤");
		rewritePred(" 5000000000000000000000 ∈ ℕ1", "⊤");
		noRewritePred(" i ∈ ℕ1");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_IN_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_IN_NATURAL1() {
		rewritePred("0 ∈ ℕ1", "⊥");
	}

	/**
	 * Ensures that rule SIMP_MULTI_MOD is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_MOD() {
		rewriteExpr("E mod E", "0");
		noRewriteExpr("E mod F");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_MINUS_NATURAL is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_MINUS_NATURAL() {
		rewritePred("−(1) ∈ ℕ", "⊥");
		rewritePred("−1 ∈ ℕ", "⊥");
		rewritePred("−3000000000 ∈ ℕ", "⊥");
		rewritePred("−5000000000000000000000 ∈ ℕ", "⊥");
		noRewritePred("−(i) ∈ ℕ");
	}

	/**
	 * Ensures that rule SIMP_LIT_IN_MINUS_NATURAL1 is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_IN_MINUS_NATURAL1() {
		rewritePred("−(1) ∈ ℕ1", "⊥");
		rewritePred("−1 ∈ ℕ1", "⊥");
		rewritePred("−3000000000 ∈ ℕ1", "⊥");
		rewritePred("−5000000000000000000000 ∈ ℕ1", "⊥");
		noRewritePred("−(i) ∈ ℕ1");
	}

	/**
	 * Ensures that superseded rule SIMP_CARD_COMPSET is implemented correctly,
	 * although indirectly by rule SIMP_COMPSET_IN.
	 */
	@Test
	public void testSIMP_CARD_COMPSET() {
		rewriteExpr("card({x · x∈S ∣ x})", "card(S)", "S", "ℙ(T)");
		rewriteExpr("card({x, y · x↦y∈S ∣ x↦y})", "card(S)", "S", "ℙ(T×U)");

		noRewriteExpr("card({x, y · x↦a↦y∈S ∣ x↦a↦y})", "S", "ℙ(T×A×U)");
		noRewriteExpr("card({x, y · x↦y∈S ∣ y↦x})", "S", "ℙ(T×U)");
		noRewriteExpr("card({x, y · x↦y+1∈S ∣ x↦y+1})", "S", "ℙ(T×ℤ)");

		noRewriteExpr("card({x · x∈S∪{x} ∣ x})", "S", "ℙ(T)");
		noRewriteExpr("card({x, y · x↦y∈S∪{x↦y} ∣ x↦y})", "S", "ℙ(T×U)");
		noRewriteExpr("card({x, y · x↦y∈S×(U∪{y}) ∣ x↦y})", //
				"S", "ℙ(T)", "U", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_DPROD_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_DPROD_CPROD() {
		rewriteExpr("(A × B) ⊗ (C × D)", "(A ∩ C) × (B × D)", //
				"A", "ℙ(S)", "B", "ℙ(T)", "C", "ℙ(S)", "D", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_PPROD_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_PPROD_CPROD() {
		rewriteExpr("(A × B) ∥ (C × D)", "(A × C) × (B × D)", //
				"A", "ℙ(S)", "B", "ℙ(T)", "C", "ℙ(U)", "D", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_CONVERSE_CPROD() {
		rewriteExpr("(A × B)∼", "(B × A)", "A", "ℙ(S)", "B", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_OVERL_CPROD is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_OVERL_CPROD() {
		rewriteExpr("r  (S × T)  s", "(S × T)  s", //
				"S", "ℙ(S)", "T", "ℙ(T)");
		rewriteExpr("r  (S × T)", "S × T", "S", "ℙ(S)", "T", "ℙ(T)");
		rewriteExpr("r  (S × T)  s  (S × T)", "S × T", //
				"S", "ℙ(S)", "T", "ℙ(T)");
		rewriteExpr("(S × T)  r  (S × T)  s", "(S × T)  s", //
				"S", "ℙ(S)", "T", "ℙ(T)");

		noRewriteExpr("(S × T)  r", "S", "ℙ(S)", "T", "ℙ(T)");

		noRewriteExpr("r  (S × T)", "S", "ℙ(U)", "T", "ℙ(T)");
		noRewriteExpr("r  (S × T)", "S", "ℙ(S)", "T", "ℙ(V)");
		noRewriteExpr("r  (S × T)", "S", "ℙ(U)", "T", "ℙ(V)");
	}

	/**
	 * Ensures that rule SIMP_TYPE_BCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_BCOMP_ID() {
		rewriteExpr("r ∘ id ∘ s", "r ∘ s", "r", "T↔U", "s", "S↔T");
		rewriteExpr(" r ∘ id", "r", "r", "S↔T");
		rewriteExpr("id ∘ r ", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_TYPE_FCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_TYPE_FCOMP_ID() {
		rewriteExpr("r ; id ; s", "r ; s", "r", "S↔T", "s", "T↔U");
		rewriteExpr(" r ; id", "r", "r", "S↔T");
		rewriteExpr("id ; r ", "r", "r", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_COMPSET is implemented correctly.
	 */
	@Test
	public void testSIMP_CONVERSE_COMPSET() {
		rewriteExpr("{x, y, z · x ↦ y ↦ z ∈ P∣ x ↦ y}∼",
				"{x, y, z · x ↦ y ↦ z ∈ P ∣ y ↦ x}", "P", "S×T↔U");

		noRewriteExpr("{x · x∈P ∧ x∈Q ∣ x}∼", "P", "S↔T");
		noRewriteExpr("{x, y, z · x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℤ ∣ x ↦ y}");
	}

	/**
	 * Ensures that rule SIMP_LIT_UPTO is implemented correctly.
	 */
	@Test
	public void testSIMP_LIT_UPTO() {
		noRewriteExpr("−1‥5");
		rewriteExpr("5‥1", "∅⦂ℙ(ℤ)");
		rewriteExpr("5‥−1", "∅⦂ℙ(ℤ)");
		rewriteExpr("−1‥−5", "∅⦂ℙ(ℤ)");
		rewriteExpr("10000000000‥−50000000000", "∅⦂ℙ(ℤ)");
		noRewriteExpr("i‥j");
	}

	/**
	 * Ensures that rule SIMP_DOM_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_LAMBDA() {
		rewriteExpr("dom({x,y,z,t · x↦y∈P ∣ x+z+3∗t ↦ t})", //
				"{x,y,z,t · x↦y∈P ∣ x+z+3∗t}", //
				"P", "ℤ↔T");
		rewriteExpr("dom({x,y,z,t⦂U · x↦y∈P ∣ x+z ↦ t})", //
				"{x,y,z⦂ℤ,t⦂U · x↦y∈P ∣ x+z}", //
				"P", "ℤ↔T");
		noRewriteExpr("dom({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})");
	}

	/**
	 * Ensures that rule SIMP_RAN_LAMBDA is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_LAMBDA() {
		rewriteExpr("ran({x,y,z,t · x↦y∈P ∣ (x+z+3∗t) ↦ t})", //
				"{x,y,z⦂ℤ,t⦂ℤ · x↦y∈P ∣ t}", //
				"P", "ℤ↔T");
		rewriteExpr("ran({x,y,z⦂V · x↦y∈P ∣ x↦z ↦ x})", //
				"{x,y,z⦂V · x↦y∈P ∣ x}", //
				"P", "S↔T");
		noRewriteExpr("ran({x,y · x ∈ 1‥2 × 3‥4 ∧ y = 5 ∣ x})");
	}

	/**
	 * Ensures that rule SIMP_MIN_BUNION_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MIN_BUNION_SING() {
		rewriteExpr("min(S ∪ {min(T)} ∪ U)", "min(S ∪ T ∪ U)");
		rewriteExpr("min(S ∪ {min(T)})", "min(S ∪ T)");
		rewriteExpr("min({min(S)} ∪ T)", "min(S ∪ T)");
		noRewriteExpr("min(S ∪ {min(T), min(U)})");
		rewriteExpr("min({min(S)} ∪ T ∪ {min(U)})", "min(S ∪ T ∪ U)");
		noRewriteExpr("min(S ∪ {max(T)} ∪ U)");
		noRewriteExpr("max(S ∪ {min(T)} ∪ U)");
	}

	/**
	 * Ensures that rule SIMP_MAX_BUNION_SING is implemented correctly
	 */
	@Test
	public void testSIMP_MAX_BUNION_SING() {
		rewriteExpr("max(S ∪ {max(T)} ∪ U)", "max(S ∪ T ∪ U)");
		rewriteExpr("max(S ∪ {max(T)})", "max(S ∪ T)");
		rewriteExpr("max({max(S)} ∪ T)", "max(S ∪ T)");
		noRewriteExpr("max(S ∪ {max(T), max(U)})");
		rewriteExpr("max({max(S)} ∪ T ∪ {max(U)})", "max(S ∪ T ∪ U)");
		noRewriteExpr("min(S ∪ {max(T)} ∪ U)");
		noRewriteExpr("max(S ∪ {min(T)} ∪ U)");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE() {
		rewritePred("E ↦ F(E) ∈ F", "⊤", "F", "S↔T");
		noRewritePred("E ↦ F(G) ∈ F", "F", "S↔T");
		noRewritePred("E ↦ F(E) ∈ G", "F", "S↔T");
		noRewritePred("E ↦ F(E) ∈ F∼", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE_CONVERSE_L is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE_CONVERSE_L() {
		rewritePred("F∼(E) ↦ E ∈ F", "⊤", "F", "T↔S");
		noRewritePred("F∼(E) ↦ G ∈ F", "F", "T↔S");
		noRewritePred("F∼(E) ↦ E ∈ G", "F", "T↔S");
		noRewritePred("F∼(E) ↦ E ∈ F∼", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_IN_FUNIMAGE_CONVERSE_R is implemented correctly
	 */
	@Test
	public void testSIMP_IN_FUNIMAGE_CONVERSE_R() {
		rewritePred("F(E) ↦ E ∈ F∼", "⊤", "F", "S↔T");
		noRewritePred("F(E) ↦ G ∈ F∼", "F", "S↔T");
		noRewritePred("F(E) ↦ E ∈ G∼", "F", "S↔T");
		noRewritePred("F(E) ↦ E ∈ F", "F", "S↔S");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_SETENUM_LL is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_SETENUM_LL() {
		rewriteExpr("{1 ↦ 3, 2 ↦ 3}(1)", "3");
		rewriteExpr("{0 ↦ FALSE, 1 ↦ FALSE}(0)", "FALSE");
		rewriteExpr("{1 ↦ 3, 2 ↦ 3}(x)", "3");

		noRewriteExpr("{0 ↦ FALSE, 1 ↦ TRUE}(x)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_SETENUM_LR is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_SETENUM_LR() {
		rewriteExpr("{A ↦ E, X ↦ Y, B ↦ F}(X)", "Y", "X", "S", "Y", "T");
		rewriteExpr("{X ↦ Y}(X)", "Y", "X", "S", "Y", "T");

		noRewriteExpr("{A ↦ E, B ↦ F}(X)", "A", "S", "E", "T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_FUNIMAGE_BUNION_SETENUM is implemented
	 * correctly
	 */
	@Test
	public void testSIMP_MULTI_FUNIMAGE_BUNION_SETENUM() {
		rewriteExpr("(r ∪ {A ↦ E, X ↦ Y, B ↦ F } ∪ s)(X)", "Y", //
				"r", "S↔T", "X", "S", "Y", "T");
		rewriteExpr("(r ∪ {A ↦ E, X ↦ Y, B ↦ F })(X)", "Y", //
				"r", "S↔T", "X", "S", "Y", "T");
		rewriteExpr("({A ↦ E, X ↦ Y, B ↦ F } ∪ r)(X)", "Y", //
				"r", "S↔T", "X", "S", "Y", "T");
		rewriteExpr("(r ∪ {X ↦ Y} ∪ s)(X)", "Y", //
				"r", "S↔T", "X", "S", "Y", "T");

		noRewriteExpr("(r ∪ {A ↦ E, B ↦ F } ∪ s)(X)", //
				"r", "S↔T", "X", "S", "Y", "T");
	}

	/**
	 * Ensures that rule SIMP_LIT_MIN is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_MIN() {
		rewriteExpr("min({−2, 1, −3})", "min({−3})");
		noRewriteExpr("min({A, B})");
		noRewriteExpr("min({A, B, −100000000000, C})");
	}

	/**
	 * Ensures that rule SIMP_LIT_MAX is implemented correctly
	 */
	@Test
	public void testSIMP_LIT_MAX() {
		rewriteExpr("max({−2, 1, −3})", "max({1})");
		noRewriteExpr("max({A, B})");
		noRewriteExpr("max({A, B, 100000000000, C})");
	}

	/**
	 * Ensures that rule SIMP_FINITE_ID is implemented correctly.
	 */
	@Test
	public void testSIMP_FINITE_ID() {
		rewritePred("finite(id⦂S↔S)", "finite(S)", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_ID_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_ID_DOMRES() {
		rewritePred("finite(E ◁ id)", "finite(E)", "E", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ1 is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ1() {
		rewritePred("finite(prj1⦂ℙ(S×T×S))", "finite(S × T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ1_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ1_DOMRES() {
		rewritePred("finite(E ◁ prj1)", "finite(E)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ2 is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ2() {
		rewritePred("finite(prj2⦂S×T↔T)", "finite(S × T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_FINITE_PRJ2_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_PRJ2_DOMRES() {
		rewritePred("finite(E ◁ prj2)", "finite(E)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_ID is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_ID() {
		rewriteExpr("card(id⦂S↔S)", "card(S)", "S", "ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_CARD_ID_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_ID_DOMRES() {
		rewriteExpr("card(E ◁ id)", "card(E)", "E", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ1 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ1() {
		rewriteExpr("card(prj1⦂S×T↔S)", "card(S × T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ1_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ1_DOMRES() {
		rewriteExpr("card(E ◁ prj1)", "card(E)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ2 is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ2() {
		rewriteExpr("card(prj2⦂S×T↔T)", "card(S × T)", //
				"S", "ℙ(S)", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_CARD_PRJ2_DOMRES is implemented correctly
	 */
	@Test
	public void testSIMP_CARD_PRJ2_DOMRES() {
		rewriteExpr("card(E ◁ prj2)", "card(E)", "E", "ℙ(S×T)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_EQUAL_BINTER is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_EQUAL_BINTER() {
		rewritePred("S ∩ T ∩ U = T", "T ⊆ S ∩ U", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");
		rewritePred("S ∩ T = T", "T ⊆ S", "S", "ℙ(V)", "T", "ℙ(V)");
		rewritePred("T ∩ U = T", "T ⊆ U", "T", "ℙ(V)", "U", "ℙ(V)");
		rewritePred("S ∩ T ∩ T ∩ U = T", "T ⊆ S ∩ U", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");

		rewritePred("T ∩ T = T", "⊤", "T", "ℙ(U)");

		noRewritePred("S ∩ T ∩ U = V", //
				"S", "ℙ(W)", "T", "ℙ(W)", "U", "ℙ(W)", "V", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_EQUAL_BUNION is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_EQUAL_BUNION() {
		rewritePred("S ∪ T ∪ U = T", "S ∪ U ⊆ T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");
		rewritePred("S ∪ T = T", "S ⊆ T", "S", "ℙ(V)", "T", "ℙ(V)");
		rewritePred("T ∪ U = T", "U ⊆ T", "T", "ℙ(V)", "U", "ℙ(V)");
		rewritePred("S ∪ T ∪ T ∪ U = T", "S ∪ U ⊆ T", //
				"S", "ℙ(V)", "T", "ℙ(V)", "U", "ℙ(V)");

		rewritePred("T ∪ T = T", "⊤", "T", "ℙ(U)");

		noRewritePred("S ∪ T ∪ U = V", //
				"S", "ℙ(W)", "T", "ℙ(W)", "U", "ℙ(W)", "V", "ℙ(W)");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_SUBSET_L is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_SUBSET_L() {
		rewritePred("∅ ⊂ S", "¬ S = ∅", "S", "ℙ(T)");
		rewritePred("∅ ⊆ S", "⊤", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_DOMRES_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMRES_DOMRES_ID() {
		rewriteExpr("S ◁ (T ◁ id)", "(S ∩ T) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_DOMRES_ID() {
		rewriteExpr("(S ◁ id) ▷ T", "(S ∩ T) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMSUB_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMSUB_DOMRES_ID() {
		rewriteExpr("S ⩤ (T ◁ id)", "(T ∖ S) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_DOMRES_ID() {
		rewriteExpr("(S ◁ id) ⩥ T", "(S ∖ T) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMRES_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMRES_DOMSUB_ID() {
		rewriteExpr("S ◁ (T ⩤ id)", "(S ∖ T) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_DOMSUB_ID() {
		rewriteExpr("(S ⩤ id) ▷ T", "(T ∖ S) ◁ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_DOMSUB_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_DOMSUB_DOMSUB_ID() {
		rewriteExpr("S ⩤ (T ⩤ id)", "(S ∪ T) ⩤ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_DOMSUB_ID() {
		rewriteExpr("(S ⩤ id) ⩥ T", "(S ∪ T) ⩤ id", //
				"S", "ℙ(U)", "T", "ℙ(U)");
	}

	/**
	 * Ensures that rule SIMP_RANRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANRES_ID() {
		rewriteExpr("id ▷ S", "S ◁ id", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RANSUB_ID() {
		rewriteExpr("id ⩥ S", "S ⩤ id", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_MULTI_DOMSUB_RAN is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_DOMSUB_RAN() {
		rewriteExpr("ran(r) ⩤ r∼", "∅⦂T↔S", "r", "S↔T");

		noRewriteExpr("ran(r) ⩤ s∼", "r", "S↔T", "s", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_MULTI_RANSUB_DOM is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_RANSUB_DOM() {
		rewriteExpr("r∼ ⩥ dom(r)", "∅⦂T↔S", "r", "S↔T");

		noRewriteExpr("s∼ ⩥ dom(r)", "r", "S↔T", "s", "S↔T");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_FCOMP_ID() {
		rewriteExpr("r ; (S ◁ id) ; (T ◁ id) ; s", "r ; ((S ∩ T) ◁ id) ; s", //
				"r", "A↔B", "s", "B↔C");
		rewriteExpr(
				"r ; (S ◁ id) ; (T ⩤ id) ; s ; (U ⩤ id) ; t ; (V ⩤ id) ; (W ⩤ id) ; u", //
				"r ; ((S ∖ T) ◁ id) ; s ; (U ⩤ id) ; t ; ((V ∪ W) ⩤ id) ; u",//
				"r", "A↔B", "s", "B↔C", "t", "C↔D", "u", "D↔E");

		noRewriteExpr("r ; (S ◁ id) ; s", //
				"r", "A↔B", "s", "B↔C");
	}

	/**
	 * Ensures that rule SIMP_MULTI_OVERL is implemented correctly
	 */
	@Test
	public void testSIMP_MULTI_OVERL() {
		rewriteExpr("s  s", "s", "s", "A↔B");
		rewriteExpr("r  s  t  s  u", "r  t  s  u", "r", "A↔B");

		noRewriteExpr("r  s  t", "r", "A↔B");
	}

	/**
	 * Ensures that rule SIMP_BCOMP_ID is implemented correctly
	 */
	@Test
	public void testSIMP_BCOMP_ID() {
		rewriteExpr("r ∘ (S ◁ id) ∘ (T ◁ id) ∘ s", "r ∘ ((S ∩ T) ◁ id) ∘ s", //
				"r", "B↔C", "s", "A↔B");
		rewriteExpr(
				"r ∘ (S ◁ id) ∘ (T ⩤ id) ∘ s ∘ (U ⩤ id) ∘ t ∘ (V ⩤ id) ∘ (W ⩤ id) ∘ u", //
				"r ∘ ((S ∖ T) ◁ id) ∘ s ∘ (U ⩤ id) ∘ t ∘ ((V ∪ W) ⩤ id) ∘ u",//
				"r", "D↔E", "s", "C↔D", "t", "B↔C", "u", "A↔B");

		noRewriteExpr("r ∘ (S ◁ id) ∘ s", "r", "B↔C", "s", "A↔B");
	}

	/**
	 * Ensures that rule SIMP_SUBSETEQ_COMPSET_L is implemented correctly
	 */
	@Test
	public void testSIMP_SUBSETEQ_COMPSET_L() {
		// predicateTest("∀y · y∈ℕ ⇒ y∈S", "{x · x∈ℕ ∣ x} ⊆ S", "S", "ℙ(ℤ)");
		rewritePred("{x, y · x∈ℤ∧y∈ℤ ∣ x+y } ⊆ S ",
				"∀x, y · x∈ℤ∧y∈ℤ ⇒ x+y∈S", "S", "ℙ(ℤ)");
		rewritePred("{x, y · x+y=a ∣ x+y} ⊆ S", "∀x, y · x+y=a ⇒ x+y∈S", //
				"a", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_SPECIAL_EQUAL_COMPSET is implemented correctly
	 */
	@Test
	public void testSIMP_SPECIAL_EQUAL_COMPSET() {
		// predicateTest("∀x · ¬x∈ℕ", "{x · x∈ℕ ∣ x} = ∅", "S", "ℙ(ℤ)");
		rewritePred("{x, y · x∈ℤ∧y∈ℤ ∣ x+y } = ∅ ", "∀x, y · ¬(x∈ℤ∧y∈ℤ)", //
				"S", "ℙ(ℤ)");
		rewritePred("{x, y · x+y=a ∣ x+y} = ∅", "∀x, y · ¬(x+y=a)", //
				"a", "ℤ");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_DOMRES_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RELIMAGE_DOMRES_ID() {
		rewriteExpr("(S ◁ id)[T]", "S ∩ T", "S", "ℙ(A)", "T", "ℙ(A)");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_DOMSUB_ID is implemented correctly
	 */
	@Test
	public void testSIMP_RELIMAGE_DOMSUB_ID() {
		rewriteExpr("(S ⩤ id)[T]", "T ∖ S", "S", "ℙ(A)", "T", "ℙ(A)");
	}

	/**
	 * Ensures that rule SIMP_COMPSET_IN is implemented correctly
	 */
	@Test
	public void testSIMP_COMPSET_IN() {
		rewriteExpr("{x · x∈S ∣ x}", "S", "S", "ℙ(T)");
		rewriteExpr("{x, y · x↦y∈S ∣ x↦y}", "S", "S", "ℙ(T×U)");
		rewriteExpr("{y · y ⊆ A ∧ finite(y) ∣ {x · x∈y ∣ x}}", //
				"{y · y ⊆ A ∧ finite(y) ∣ y}",//
				"A", "ℙ(S)");

		noRewriteExpr("{x, y · x↦a↦y∈S ∣ x↦a↦y}", "S", "ℙ(T×A×U)");
		noRewriteExpr("{x, y · x↦y∈S ∣ y↦x}", "S", "ℙ(T×U)");
		noRewriteExpr("{x, y · x↦y+1∈S ∣ x↦y+1}", "S", "ℙ(T×ℤ)");

		noRewriteExpr("{x · x∈S∪{x} ∣ x}", "S", "ℙ(T)");
		noRewriteExpr("{x, y · x↦y∈S×(U∪{y}) ∣ x↦y}", "S", "ℙ(T)", "U", "ℙ(V)");
		noRewriteExpr("{x, y⦂ℙ(T) · x∈y ∣ x}", "T", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_COMPSET_SUBSETEQ is implemented correctly
	 */
	@Test
	public void testSIMP_COMPSET_SUBSETEQ() {
		rewriteExpr("{x, y⦂U · x⊆S ∣ x}", "ℙ(S)", "S", "ℙ(T)");

		noRewriteExpr("{x, y⦂U · x⊆S ∣ y}", "S", "ℙ(T)");
		noRewriteExpr("{x, y· x×y⊆S ∣ x × y}", "S", "ℙ(T×U)");
		noRewriteExpr("{x, y⦂T · x⊆S∪{y} ∣ x}", "S", "ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_IN_COMPSET_ONEPOINT is implemented correctly
	 */
	@Test
	public void testSIMP_IN_COMPSET_ONEPOINT() {
		rewritePred("1 ∈ {x · x≥0 ∣ x}", "1≥0");
		rewritePred("1↦2 ∈ {x,y · x≥0 ∧ y>x ∣ x↦y}", "1≥0 ∧ 2>1");
		rewritePred("1 ∈ {x,y · x≥0 ∧ y>x ∣ x}", "∃y · 1≥0 ∧ y>1");
		rewritePred("a↦b ∈ {x,y,z · x≥0 ∧ y>x ∧ z>2∗y ∣ (x↦y)↦z}",
				"∃x,y · (x≥0 ∧ y>x ∧ b>2∗y) ∧ x↦y=a");

		rewritePred("a↦b ∈ {x,y · x=0 ∧ y=0 ∣ x↦y}", "a=0 ∧ b=0");
		rewritePred("a↦b ∈ {x · x≥0 ∣ x↦x}", "a≥0 ∧ a=b");
		rewritePred("a↦b↦c ∈ {x,y,z · x>y+z ∣ x↦y+1↦z}", "∃y · a>y+c ∧ y+1=b");

		rewritePred("a↦b ∈ {x,y · x=0 ∧ y=0 ∣ x↦y+1}",
				"∃y · (a=0 ∧ y=0) ∧ y+1=b");

		rewritePred("1 ∈ {x · x≥0 ∣ x+1}", "∃x ·  x≥0 ∧ x+1=1");
		rewritePred("a ∈ {x,y · x≥0 ∧ y>x ∣ x↦y}",
				"∃x,y · (x≥0 ∧ y>x) ∧ x↦y=a");
	}

	/**
	 * Ensures that rule SIMP_FINITE_BOOL is implemented correctly
	 */
	@Test
	public void testSIMP_FINITE_BOOL() {
		rewritePred("finite(BOOL)", "⊤");
	}

}