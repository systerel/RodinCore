/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ArithRewriterImpl;
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
		
		// A - (- B)  == A + B
		expressionTest("x+y", "x− (−y)");
		expressionTest("x∗2 + 3∗y + x", "x∗2 − (−(3∗y + x))");
		expressionTest("x+3", "x−(−3)");
		
		// C = A + ... + C + ... + B  ==  0 = A + ... + B
		predicateTest("0 = x", "y = x + y");
		predicateTest("0 = y", "x = x + y");
		predicateTest("0 = x + y", "z = x + y + z");
		predicateTest("0 = x + y", "z = z + x + y");
		predicateTest("0 = x + y", "z = x + z + y");

		// A + ... + C + ... + B = C  ==  A + ... + B = 0
		predicateTest("x = 0", "x + y = y");
		predicateTest("y = 0", "x + y = x");
		predicateTest("x + y = 0", "x + y + z = z");
		predicateTest("x + y = 0", "z + x + y = z");
		predicateTest("x + y = 0", "x + z + y = z");
		
		// A + ... + E + ... + B  = C + ... + E + ... + D   == A + ... + B = C + ... + D
		predicateTest("a = c ∗ 2", "a + b = c ∗ 2 + b");
		predicateTest("a = c ∗ 2", "b + a = c ∗ 2 + b");
		predicateTest("a = c ∗ 2", "a + b = b + c ∗ 2");
		predicateTest("a = c ∗ 2", "b + a = b + c ∗ 2");
		predicateTest("a = c + d", "a + b = c + b + d");
		predicateTest("a = c + d", "a + b = c + d + b");
		predicateTest("a = c + d", "a + b = b + c + d");
		predicateTest("a + b = c", "a + b + d = c + d");
		predicateTest("a + b = c", "a + d + b = c + d");
		predicateTest("a + b = c", "d + a + b = c + d");
		predicateTest("a + b = c + d", "a + b + e = c + d + e");
		predicateTest("a + b = c + d", "a + e + b = c + d + e");
		predicateTest("a + b = c + d", "e + a + b = c + d + e");
		predicateTest("a + b = c + d", "a + b + e = c + e + d");
		predicateTest("a + b = c + d", "a + e + b = c + e + d");
		predicateTest("a + b = c + d", "e + a + b = c + e + d");
		predicateTest("a + b = c + d", "a + b + e = e + c + d");
		predicateTest("a + b = c + d", "a + e + b = e + c + d");
		predicateTest("a + b = c + d", "e + a + b = e + c + d");
		
		// C < A + ... + C + ... + B  ==  0 < A + ... + B
		predicateTest("0 < x", "y < x + y");
		predicateTest("0 < y", "x < x + y");
		predicateTest("0 < x + y", "z < x + y + z");
		predicateTest("0 < x + y", "z < z + x + y");
		predicateTest("0 < x + y", "z < x + z + y");

		// A + ... + C + ... + B < C  ==  A + ... + B < 0
		predicateTest("x < 0", "x + y < y");
		predicateTest("y < 0", "x + y < x");
		predicateTest("x + y < 0", "x + y + z < z");
		predicateTest("x + y < 0", "z + x + y < z");
		predicateTest("x + y < 0", "x + z + y < z");

		// A + ... + E + ... + B  < C + ... + E + ... + D   == A + ... + B < C + ... + D
		predicateTest("a < c ∗ 2", "a + b < c ∗ 2 + b");
		predicateTest("a < c ∗ 2", "b + a < c ∗ 2 + b");
		predicateTest("a < c ∗ 2", "a + b < b + c ∗ 2");
		predicateTest("a < c ∗ 2", "b + a < b + c ∗ 2");
		predicateTest("a < c + d", "a + b < c + b + d");
		predicateTest("a < c + d", "a + b < c + d + b");
		predicateTest("a < c + d", "a + b < b + c + d");
		predicateTest("a + b < c", "a + b + d < c + d");
		predicateTest("a + b < c", "a + d + b < c + d");
		predicateTest("a + b < c", "d + a + b < c + d");
		predicateTest("a + b < c + d", "a + b + e < c + d + e");
		predicateTest("a + b < c + d", "a + e + b < c + d + e");
		predicateTest("a + b < c + d", "e + a + b < c + d + e");
		predicateTest("a + b < c + d", "a + b + e < c + e + d");
		predicateTest("a + b < c + d", "a + e + b < c + e + d");
		predicateTest("a + b < c + d", "e + a + b < c + e + d");
		predicateTest("a + b < c + d", "a + b + e < e + c + d");
		predicateTest("a + b < c + d", "a + e + b < e + c + d");
		predicateTest("a + b < c + d", "e + a + b < e + c + d");

		// C <= A + ... + C + ... + B  ==  0 <= A + ... + B
		predicateTest("0 ≤ x", "y ≤ x + y");
		predicateTest("0 ≤ y", "x ≤ x + y");
		predicateTest("0 ≤ x + y", "z ≤ x + y + z");
		predicateTest("0 ≤ x + y", "z ≤ z + x + y");
		predicateTest("0 ≤ x + y", "z ≤ x + z + y");

		// A + ... + C + ... + B <= C  ==  A + ... + B <= 0
		predicateTest("x ≤ 0", "x + y ≤ y");
		predicateTest("y ≤ 0", "x + y ≤ x");
		predicateTest("x + y ≤ 0", "x + y + z ≤ z");
		predicateTest("x + y ≤ 0", "z + x + y ≤ z");
		predicateTest("x + y ≤ 0", "x + z + y ≤ z");

		// A + ... + E + ... + B  <= C + ... + E + ... + D   == A + ... + B <= C + ... + D
		predicateTest("a ≤ c ∗ 2", "a + b ≤ c ∗ 2 + b");
		predicateTest("a ≤ c ∗ 2", "b + a ≤ c ∗ 2 + b");
		predicateTest("a ≤ c ∗ 2", "a + b ≤ b + c ∗ 2");
		predicateTest("a ≤ c ∗ 2", "b + a ≤ b + c ∗ 2");
		predicateTest("a ≤ c + d", "a + b ≤ c + b + d");
		predicateTest("a ≤ c + d", "a + b ≤ c + d + b");
		predicateTest("a ≤ c + d", "a + b ≤ b + c + d");
		predicateTest("a + b ≤ c", "a + b + d ≤ c + d");
		predicateTest("a + b ≤ c", "a + d + b ≤ c + d");
		predicateTest("a + b ≤ c", "d + a + b ≤ c + d");
		predicateTest("a + b ≤ c + d", "a + b + e ≤ c + d + e");
		predicateTest("a + b ≤ c + d", "a + e + b ≤ c + d + e");
		predicateTest("a + b ≤ c + d", "e + a + b ≤ c + d + e");
		predicateTest("a + b ≤ c + d", "a + b + e ≤ c + e + d");
		predicateTest("a + b ≤ c + d", "a + e + b ≤ c + e + d");
		predicateTest("a + b ≤ c + d", "e + a + b ≤ c + e + d");
		predicateTest("a + b ≤ c + d", "a + b + e ≤ e + c + d");
		predicateTest("a + b ≤ c + d", "a + e + b ≤ e + c + d");
		predicateTest("a + b ≤ c + d", "e + a + b ≤ e + c + d");
		
		// A - C = B - C  ==  A = B
		predicateTest("a = c ∗ 2", "a − b = c ∗ 2 − b");
		
		// A - C < B - C  ==  A < B
		predicateTest("a < c ∗ 2", "a − b < c ∗ 2 − b");
		
		// A - C <= B - C  ==  A <= B
		predicateTest("a ≤ c ∗ 2", "a − b ≤ c ∗ 2 − b");
		
		// C - A = C - B  ==  A = B
		predicateTest("a = c ∗ 2", "b − a = b − c ∗ 2");
		
		// C - A < C - B  ==  B < A
		predicateTest("c ∗ 2 < a", "b − a < b − c ∗ 2");
		
		// C - A <= C - B  ==  B <= A
		predicateTest("c ∗ 2 ≤ a", "b − a ≤ b − c ∗ 2");
	}

	@Test
	public void testBugZeroPlusZero() throws Exception {
		// the following succeeds
		expressionTest("0+0", "0+0");
		// the following fails
		predicateTest("1=0", "1+1=1");
		predicateTest("0=1", "1=1+1");
		predicateTest("1=1", "1+1=1+1");
	}
}
