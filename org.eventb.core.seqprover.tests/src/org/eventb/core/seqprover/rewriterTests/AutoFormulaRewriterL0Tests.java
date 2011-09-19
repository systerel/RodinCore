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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;
import org.junit.Test;

public class AutoFormulaRewriterL0Tests extends AutoFormulaRewriterTests {

	// The automatic rewriter for testing.
	private static final AutoRewriterImpl REWRITER_L0 = new AutoRewriterImpl(
			DT_FAC, Level.L0);

	public AutoFormulaRewriterL0Tests() {
		this(REWRITER_L0);
	}

	protected AutoFormulaRewriterL0Tests(AutoRewriterImpl rewriter) {
		super(rewriter);
	}

	/**
	 * Ensures that the predicate simplifier is correctly parameterized.
	 */
	@Test
	public void checkOptions() {
		assertTrue(REWRITER_L0.withMultiImp);
		assertFalse(REWRITER_L0.withMultiImpNot);
		assertFalse(REWRITER_L0.withMultiEqvNot);
		assertFalse(REWRITER_L0.withMultiImpAnd);
		assertTrue(REWRITER_L0.withQuantDistr);
		assertFalse(REWRITER_L0.withExistsImp);
		assertTrue(REWRITER_L0.withMultiAndOr);
	}

}
