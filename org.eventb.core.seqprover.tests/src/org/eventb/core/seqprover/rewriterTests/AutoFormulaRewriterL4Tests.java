/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
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
 * This is the class for testing automatic rewriter L4 {@link AutoRewriterImpl}
 * using the abstract auto formula rewriter tests
 * {@link AbstractAutoFormulaRewriterTests}.
 * 
 * @author Josselin Dolhen
 */
public class AutoFormulaRewriterL4Tests extends AutoFormulaRewriterL3Tests {

	// The automatic rewriter for testing.
	private static final AutoRewriterImpl REWRITER_L4 = new AutoRewriterImpl(
			Level.L4);

	public AutoFormulaRewriterL4Tests() {
		this(REWRITER_L4);
	}

	protected AutoFormulaRewriterL4Tests(AutoRewriterImpl rewriter) {
		super(rewriter);
	}

	/**
	 * Ensures that rule SIMP_SETENUM_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_SETENUM_EQUAL_EMPTY() {
		rewritePredEmptySet("{A}", "⊥", "A=S");
		rewritePredEmptySet("{A, B}", "⊥", "A=S");

		// Other rewrite rules apply to empty enumeration
		rewritePred("{} = ∅⦂ℙ(S)", "⊤");
	}

	/**
	 * Ensures that rule SIMP_BINTER_SING_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_BINTER_SING_EQUAL_EMPTY() {
		rewritePredEmptySet("A ∩ {a}", "¬ a ∈ A", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_BINTER_SETMINUS_EQUAL_EMPTY is implemented
	 * correctly.
	 */
	@Test
	public void testSIMP_BINTER_SETMINUS_EQUAL_EMPTY() {
		rewritePredEmptySet("(A ∖ B) ∩ C", "(A ∩ C) ∖ B = ∅", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_BUNION_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_BUNION_EQUAL_EMPTY() {
		rewritePredEmptySet("A ∪ B", "A=∅ ∧ B=∅", "A=ℙ(S)");
		rewritePredEmptySet("A ∪ B ∪ C", "A=∅ ∧ B=∅ ∧ C=∅", "A=ℙ(S)");

		noRewritePred("(A ∪ B) ∩ C = ∅", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SETMINUS_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_SETMINUS_EQUAL_EMPTY() {
		rewritePredEmptySet("A ∖ B", "A⊆B", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_POW_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_POW_EQUAL_EMPTY() {
		rewritePredEmptySet("ℙ(A)", "⊥", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_POW1_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_POW1_EQUAL_EMPTY() {
		rewritePredEmptySet("ℙ1(A)", "A=∅", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_KUNION_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_KUNION_EQUAL_EMPTY() {
		rewritePredEmptySet("union(A)", "A⊆{∅}", "A=ℙ(ℙ(S))");

		noRewritePred("inter(A) = ∅", "A=ℙ(ℙ(S))");
	}

	/**
	 * Ensures that rule SIMP_QUNION_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_QUNION_EQUAL_EMPTY() {
		rewritePredEmptySet("(⋃x· x∈E ∣ h(x))", "∀x· x∈E ⇒ h(x)=∅",
				"h=S↔ℙ(ℙ(T))");

		noRewritePred("(⋂x· x∈E ∣ h(x)) ∩ B = ∅", "h=S↔ℙ(ℙ(T))");
	}

	/**
	 * Ensures that rule SIMP_NATURAL_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_NATURAL_EQUAL_EMPTY() {
		rewritePredEmptySet("ℕ", "⊥", "");
	}

	/**
	 * Ensures that rule SIMP_NATURAL1_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_NATURAL1_EQUAL_EMPTY() {
		rewritePredEmptySet("ℕ1", "⊥", "");
	}

	/**
	 * Ensures that rule SIMP_CPROD_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_CPROD_EQUAL_EMPTY() {
		rewritePredEmptySet("A × B", "A=∅ ∨ B=∅", "A=ℙ(S); B=ℙ(T);");
	}

	/**
	 * Ensures that rule SIMP_UPTO_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_UPTO_EQUAL_EMPTY() {
		rewritePredEmptySet("i ‥ j", "i > j", "");
	}

	/**
	 * Ensures that rule SIMP_SREL_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_SREL_EQUAL_EMPTY() {
		rewritePredEmptySet("A  B", "A=∅  ∧ ¬ B=∅", "A=ℙ(S); B=ℙ(T);");
	}

	/**
	 * Ensures that rule SIMP_STREL_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_STREL_EQUAL_EMPTY() {
		rewritePredEmptySet("A  B", "A=∅  ⇔ ¬ B=∅", "A=ℙ(S); B=ℙ(T);");
	}

	/**
	 * Ensures that rule SIMP_DOM_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_DOM_EQUAL_EMPTY() {
		rewritePredEmptySet("dom(r)", "r=∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_RAN_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_RAN_EQUAL_EMPTY() {
		rewritePredEmptySet("ran(r)", "r=∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_FCOMP_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_FCOMP_EQUAL_EMPTY() {
		rewritePredEmptySet("p ; q", "ran(p) ∩ dom(q) = ∅", "p=S↔T; q=T↔U");
	}

	/**
	 * Ensures that rule SIMP_BCOMP_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_BCOMP_EQUAL_EMPTY() {
		rewritePredEmptySet("p ∘ q", "ran(q) ∩ dom(p) = ∅", "p=T↔U; q=S↔T");
	}

	/**
	 * Ensures that rule SIMP_DOMRES_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_DOMRES_EQUAL_EMPTY() {
		rewritePredEmptySet("A ◁ r", "dom(r) ∩ A = ∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_DOMSUB_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_DOMSUB_EQUAL_EMPTY() {
		rewritePredEmptySet("A ⩤ r", "dom(r) ⊆ A", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_RANRES_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_RANRES_EQUAL_EMPTY() {
		rewritePredEmptySet("r ▷ A", "ran(r) ∩ A = ∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_RANSUB_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_RANSUB_EQUAL_EMPTY() {
		rewritePredEmptySet("r ⩥ A", "ran(r) ⊆ A", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_CONVERSE_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_CONVERSE_EQUAL_EMPTY() {
		rewritePredEmptySet("r∼", "r = ∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_RELIMAGE_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_RELIMAGE_EQUAL_EMPTY() {
		rewritePredEmptySet("r[A]", "A ◁ r = ∅", "r=S↔T");

		noRewritePred("r(A) = ∅", "r=S↔ℙ(T)");
	}

	/**
	 * Ensures that rule SIMP_OVERL_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_OVERL_EQUAL_EMPTY() {
		rewritePredEmptySet("r  s", "r=∅ ∧ s=∅", "r=S↔T");
		rewritePredEmptySet("r  s  t", "r=∅ ∧ s=∅ ∧ t=∅", "r=S↔T");
	}

	/**
	 * Ensures that rule SIMP_DPROD_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_DPROD_EQUAL_EMPTY() {
		rewritePredEmptySet("p ⊗ q", "dom(p) ∩ dom(q)=∅", "p=S↔T; q=S↔U");
	}

	/**
	 * Ensures that rule SIMP_PPROD_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_PPROD_EQUAL_EMPTY() {
		rewritePredEmptySet("p ∥ q", "p=∅ ∨ q=∅", "p=S↔T; q=U↔V");
	}

	/**
	 * Ensures that rule SIMP_ID_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_ID_EQUAL_EMPTY() {
		rewritePredEmptySet("id⦂ℙ(S×S)", "⊥", "");
	}

	/**
	 * Ensures that rule SIMP_PRJ1_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_PRJ1_EQUAL_EMPTY() {
		rewritePredEmptySet("prj1⦂ℙ(S×T×S)", "⊥", "");
	}

	/**
	 * Ensures that rule SIMP_PRJ2_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_PRJ2_EQUAL_EMPTY() {
		rewritePredEmptySet("prj2⦂ℙ(S×T×T)", "⊥", "");
	}

	protected void rewritePredEmptySet(String inputImage, String expectedImage,
			String typenvImage) {
		rewritePred(inputImage + " = ∅", expectedImage, typenvImage);
		rewritePred(inputImage + " ⊆ ∅", expectedImage, typenvImage);
		rewritePred(inputImage + " ⊂ ∅", "⊥", typenvImage);

		rewritePred("∅ = " + inputImage, expectedImage, typenvImage);
		rewritePred("∅ ⊆ " + inputImage, "⊤", typenvImage);
		rewritePred("∅ ⊂ " + inputImage, "¬ " + inputImage + " = ∅",
				typenvImage);

		// Not applicable (wrong right-hand side)
		noRewritePred(inputImage + " = ZZZ", typenvImage);
	}

}