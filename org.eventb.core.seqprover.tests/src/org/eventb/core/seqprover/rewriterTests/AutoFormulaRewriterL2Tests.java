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

}
