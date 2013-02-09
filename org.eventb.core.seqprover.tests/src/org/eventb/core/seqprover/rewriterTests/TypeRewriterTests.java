/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added tests for SIMP_TYPE_SUBSETEQ and SIMP_TYPE_SUBSET_L
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewriterImpl;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the class for testing type rewriter {@link TypeRewriterImpl}
 *         using the abstract formula rewriter tests
 *         {@link AbstractFormulaRewriterTests}.
 */
public class TypeRewriterTests extends AbstractFormulaRewriterTests {

	// The type rewriter for testing.
	private static final IFormulaRewriter rewriter = new TypeRewriterImpl();
	
	/**
	 * Constructor.
	 * <p>
	 * Create an abstract formula rewriter test with the input is the type rewriter.
	 */
	public TypeRewriterTests() {
		super(FormulaFactory.getDefault(), rewriter);
	}

	@Test
	public void test_SIMP_TYPE_EQUAL_EMPTY_Left() throws Exception {
		// Typ = {} == false (where Typ is a type expression)
		rewritePred("ℤ = ∅", "⊥");
		rewritePred("ℙ(ℤ) = ∅", "⊥");
		noRewritePred("ℕ = ∅");
	}

	@Test
	public void test_SIMP_TYPE_EQUAL_EMPTY_Right() throws Exception {
		// {} = Typ == false (where Typ is a type expression)
		rewritePred("∅ = ℤ", "⊥");
		rewritePred("∅ = ℙ(ℤ)", "⊥");
		noRewritePred("∅ = ℕ");
	}

	@Test
	public void test_SIMP_TYPE_IN() throws Exception {
		// E : Typ == true (where Typ is a type expression)
		rewritePred("E ∈ ℤ", "⊤");
		rewritePred("E ∈ ℙ(ℤ)", "⊤");
		noRewritePred("E ∈ ℕ");
	}
	
	@Test
	public void test_SIMP_TYPE_SUBSETEQ() throws Exception {
		// S <: Typ == true (where Typ is a type expression)
		rewritePred("S ⊆ ℤ", "⊤");
		rewritePred("S ⊆ ℙ(ℤ)", "⊤");
		noRewritePred("S ⊆ ℕ");
	}
	
	@Test
	public void test_SIMP_TYPE_SUBSET_L() throws Exception {
		// S <<: Typ == S /= Typ (where Typ is a type expression)
		rewritePred("S ⊂ ℤ", "S ≠ ℤ");
		rewritePred("S ⊂ ℙ(ℤ)", "S ≠ ℙ(ℤ)");
		noRewritePred("S ⊂ ℕ");
	}

}
