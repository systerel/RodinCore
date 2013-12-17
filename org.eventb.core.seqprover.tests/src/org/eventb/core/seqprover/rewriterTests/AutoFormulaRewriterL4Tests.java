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
	 * Ensures that rule SIMP_BUNION_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_BUNION_EQUAL_EMPTY() {
		rewritePredEmptySet("A ∪ B", "A=∅ ∧ B=∅", "A=ℙ(S)");
		rewritePredEmptySet("A ∪ B ∪ C", "A=∅ ∧ B=∅ ∧ C=∅", "A=ℙ(S)");
	}

	/**
	 * Ensures that rule SIMP_SETENUM_EQUAL_EMPTY is implemented correctly.
	 */
	@Test
	public void testSIMP_SETENUM_EQUAL_EMPTY() {
		rewritePredEmptySet("{A}", "⊥", "A=S");
		rewritePredEmptySet("{A, B}", "⊥", "A=S");
		// Other rewrite rules apply to empty enumeration
		rewritePred("{} = ∅⦂ℙ(S)", "⊤", "");
	}

}