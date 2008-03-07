/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.seqprover.arith.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.arith.ArithRewriterImpl;
import org.eventb.core.seqprover.rewriterTests.AbstractFormulaRewriterTests;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the class for testing automatic rewriter {@link ArithRewriterImpl}
 *         using the abstract formula rewriter tests
 *         {@link AbstractFormulaRewriterTests}.
 */
public class ArithFormulaRewriterTests extends AbstractFormulaRewriterTests {
	
	// The automatic rewriter for testing.
	private static final IFormulaRewriter rewriter = new ArithRewriterImpl();
	
	/**
	 * Constructor.
	 * <p>
	 * Create an abstract formula rewriter test with the input is the automatic
	 * rewriter.
	 */
	public ArithFormulaRewriterTests() {
		super(rewriter);
	}

	/**
	 * Tests for rewriting arithmetic expressions.
	 */
	@Test
	public void testArithmetics() {
		// (A + ... + C + ... + B) - C  ==  A + ... + B
		expressionTest("y ∗ 3", "x ∗ 2 + y ∗ 3 − x ∗ 2");
		expressionTest("x ∗ 2", "x ∗ 2 + y ∗ 3 − y ∗ 3");
		expressionTest("x + y", "x + y + z − z");
		expressionTest("x + z", "x + y + z − y");
		expressionTest("y + z", "x + y + z − x");
		
		// (A + ... + D + ... + (C - D) + ... + B)  ==  A + ... + C + ... B
		expressionTest("y ∗ 2", "x + (y ∗ 2 − x)");
		expressionTest("y ∗ 2", "(y ∗ 2 − x) + x");
		expressionTest("x + y", "x + (y − z) + z");
		expressionTest("x + y", "x + z + (y − z)");
		expressionTest("y + x", "z + (y − z) + x");
		expressionTest("y + x", "(y − z) + z + x");
		expressionTest("x + y", "z + x + (y − z)");
		expressionTest("y + x", " (y − z) + x + z");
		expressionTest("x + (y − z)", "(x − z) + z + (y − z)");
		expressionTest("x + (y − z)", "(x − z) + (y − z) + z");
		expressionTest("x + (y − z)", "z + (x − z) + (y − z)");

		// (A + ... + E + ... + B) - (C + ... + E + ... + D)  == (A + ... + B) - (C + ... + D)
		expressionTest("y − z", "(x + y) − (x + z)");
		expressionTest("y − z", "(x + y) − (z + x)");
		expressionTest("x − z", "(x + y) − (y + z)");
		expressionTest("x − z", "(x + y) − (z + y)");
		expressionTest("y − (z + x)", "(x + y) − (x + z + x)");
		expressionTest("y − (z + x)", "(y + x) − (x + z + x)");
		expressionTest("(y + x) − z", "(x + y + x) − (x + z)");
		expressionTest("(y + x) − z", "(x + y + x) − (z + x)");
		expressionTest("(a + b) − (c + d)", "(a + e + b) − (c + e + d)");
		expressionTest("(a + b) − (c + d)", "(a + b + e) − (c + e + d)");
		expressionTest("(a + b) − (c + d)", "(e + a + b) − (c + e + d)");
		expressionTest("(a + b) − (c + d)", "(a + e + b) − (e + c + d)");
		expressionTest("(a + b) − (c + d)", "(a + b + e) − (e + c + d)");
		expressionTest("(a + b) − (c + d)", "(e + a + b) − (e + c + d)");
		expressionTest("(a + b) − (c + d)", "(a + e + b) − (c + d + e)");
		expressionTest("(a + b) − (c + d)", "(a + b + e) − (c + d + e)");
		expressionTest("(a + b) − (c + d)", "(e + a + b) − (c + d + e)");
		
		// C - (A + ... + C + ... + B)  ==  -(A + ... + B)
		expressionTest("−(y ∗ 3)", "x ∗ 2 − (x ∗ 2 + y ∗ 3)");
		expressionTest("−(x ∗ 2)", "y ∗ 3 − (x ∗ 2 + y ∗ 3)");
		expressionTest("−(x + y)", "z − (x + y + z)");
		expressionTest("−(x + z)", "y − (x + y + z)");
		expressionTest("−(y + z)", "x − (x + y + z)");
	}

}
