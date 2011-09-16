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
 * This is the class for testing automatic rewriter L1 {@link AutoRewriterImpl}
 * using the abstract auto formula rewriter tests
 * {@link AbstractAutoFormulaRewriterTests}.
 */
public class AutoFormulaRewriterL1Tests extends AutoFormulaRewriterL0Tests {

	// The automatic rewriter for testing.
	private static final AutoRewriterImpl REWRITER_L1 = new AutoRewriterImpl(
			DT_FAC, Level.L1);

	/**
	 * Constructor.
	 * <p>
	 * Create an formula rewriter test with the input is the automatic rewriter.
	 */
	public AutoFormulaRewriterL1Tests() {
		this(REWRITER_L1);
	}

	protected AutoFormulaRewriterL1Tests(AutoRewriterImpl rewriter) {
		super(rewriter);
	}

	/**
	 * Ensures that membership in comprehension set is correctly rewritten.
	 */
	@Test
	public void testSimpCompSet() {

		rewritePred("1 ↦ 0∈{x,y·x∈ℕ∧y∉ℕ1 ∣ x ↦ y}", "1 ∈ ℕ  ∧   ¬ 0∈ℕ1");
		rewritePred("1 ↦ 0 ↦ 6∈{x,y,z·x∈ℕ∧y∉ℕ1∧f(z)<7 ∣ x ↦ y ↦ z}",
				"1 ∈ ℕ  ∧   ¬ 0∈ℕ1  ∧  f(6) < 7");

	}

}
