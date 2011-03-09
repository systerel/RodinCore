/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
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
	private static final IFormulaRewriter rewriter = new ArithRewriterImpl(ff);
	
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
		rewriteExpr("x ∗ 2 + y ∗ 3 − x ∗ 2", "y ∗ 3");
		rewriteExpr("x ∗ 2 + y ∗ 3 − y ∗ 3", "x ∗ 2");
		rewriteExpr("x + y + z − z", "x + y");
		rewriteExpr("x + y + z − y", "x + z");
		rewriteExpr("x + y + z − x", "y + z");
		
		// (A + ... + D + ... + (C - D) + ... + B)  ==  A + ... + C + ... B
		rewriteExpr("x + (y ∗ 2 − x)", "y ∗ 2");
		rewriteExpr("(y ∗ 2 − x) + x", "y ∗ 2");
		rewriteExpr("x + (y − z) + z", "x + y");
		rewriteExpr("x + z + (y − z)", "x + y");
		rewriteExpr("z + (y − z) + x", "y + x");
		rewriteExpr("(y − z) + z + x", "y + x");
		rewriteExpr("z + x + (y − z)", "x + y");
		rewriteExpr(" (y − z) + x + z", "y + x");
		rewriteExpr("(x − z) + z + (y − z)", "x + (y − z)");
		rewriteExpr("(x − z) + (y − z) + z", "x + (y − z)");
		rewriteExpr("z + (x − z) + (y − z)", "x + (y − z)");

		// (A + ... + E + ... + B) - (C + ... + E + ... + D)  == (A + ... + B) - (C + ... + D)
		rewriteExpr("(x + y) − (x + z)", "y − z");
		rewriteExpr("(x + y) − (z + x)", "y − z");
		rewriteExpr("(x + y) − (y + z)", "x − z");
		rewriteExpr("(x + y) − (z + y)", "x − z");
		rewriteExpr("(x + y) − (x + z + x)", "y − (z + x)");
		rewriteExpr("(y + x) − (x + z + x)", "y − (z + x)");
		rewriteExpr("(x + y + x) − (x + z)", "(y + x) − z");
		rewriteExpr("(x + y + x) − (z + x)", "(y + x) − z");
		rewriteExpr("(a + e + b) − (c + e + d)", "(a + b) − (c + d)");
		rewriteExpr("(a + b + e) − (c + e + d)", "(a + b) − (c + d)");
		rewriteExpr("(e + a + b) − (c + e + d)", "(a + b) − (c + d)");
		rewriteExpr("(a + e + b) − (e + c + d)", "(a + b) − (c + d)");
		rewriteExpr("(a + b + e) − (e + c + d)", "(a + b) − (c + d)");
		rewriteExpr("(e + a + b) − (e + c + d)", "(a + b) − (c + d)");
		rewriteExpr("(a + e + b) − (c + d + e)", "(a + b) − (c + d)");
		rewriteExpr("(a + b + e) − (c + d + e)", "(a + b) − (c + d)");
		rewriteExpr("(e + a + b) − (c + d + e)", "(a + b) − (c + d)");
		
		// C - (A + ... + C + ... + B)  ==  -(A + ... + B)
		rewriteExpr("x ∗ 2 − (x ∗ 2 + y ∗ 3)", "−(y ∗ 3)");
		rewriteExpr("y ∗ 3 − (x ∗ 2 + y ∗ 3)", "−(x ∗ 2)");
		rewriteExpr("z − (x + y + z)", "−(x + y)");
		rewriteExpr("y − (x + y + z)", "−(x + z)");
		rewriteExpr("x − (x + y + z)", "−(y + z)");
		
		// A - (- B)  == A + B
		rewriteExpr("x− (−y)", "x+y");
		rewriteExpr("x∗2 − (−(3∗y + x))", "x∗2 + 3∗y + x");
		rewriteExpr("x−(−3)", "x+3");
		
		// C = A + ... + C + ... + B  ==  0 = A + ... + B
		rewritePred("y = x + y", "0 = x");
		rewritePred("x = x + y", "0 = y");
		rewritePred("z = x + y + z", "0 = x + y");
		rewritePred("z = z + x + y", "0 = x + y");
		rewritePred("z = x + z + y", "0 = x + y");

		// A + ... + C + ... + B = C  ==  A + ... + B = 0
		rewritePred("x + y = y", "x = 0");
		rewritePred("x + y = x", "y = 0");
		rewritePred("x + y + z = z", "x + y = 0");
		rewritePred("z + x + y = z", "x + y = 0");
		rewritePred("x + z + y = z", "x + y = 0");
		
		// A + ... + E + ... + B  = C + ... + E + ... + D   == A + ... + B = C + ... + D
		rewritePred("a + b = c ∗ 2 + b", "a = c ∗ 2");
		rewritePred("b + a = c ∗ 2 + b", "a = c ∗ 2");
		rewritePred("a + b = b + c ∗ 2", "a = c ∗ 2");
		rewritePred("b + a = b + c ∗ 2", "a = c ∗ 2");
		rewritePred("a + b = c + b + d", "a = c + d");
		rewritePred("a + b = c + d + b", "a = c + d");
		rewritePred("a + b = b + c + d", "a = c + d");
		rewritePred("a + b + d = c + d", "a + b = c");
		rewritePred("a + d + b = c + d", "a + b = c");
		rewritePred("d + a + b = c + d", "a + b = c");
		rewritePred("a + b + e = c + d + e", "a + b = c + d");
		rewritePred("a + e + b = c + d + e", "a + b = c + d");
		rewritePred("e + a + b = c + d + e", "a + b = c + d");
		rewritePred("a + b + e = c + e + d", "a + b = c + d");
		rewritePred("a + e + b = c + e + d", "a + b = c + d");
		rewritePred("e + a + b = c + e + d", "a + b = c + d");
		rewritePred("a + b + e = e + c + d", "a + b = c + d");
		rewritePred("a + e + b = e + c + d", "a + b = c + d");
		rewritePred("e + a + b = e + c + d", "a + b = c + d");
		
		// C < A + ... + C + ... + B  ==  0 < A + ... + B
		rewritePred("y < x + y", "0 < x");
		rewritePred("x < x + y", "0 < y");
		rewritePred("z < x + y + z", "0 < x + y");
		rewritePred("z < z + x + y", "0 < x + y");
		rewritePred("z < x + z + y", "0 < x + y");

		// A + ... + C + ... + B < C  ==  A + ... + B < 0
		rewritePred("x + y < y", "x < 0");
		rewritePred("x + y < x", "y < 0");
		rewritePred("x + y + z < z", "x + y < 0");
		rewritePred("z + x + y < z", "x + y < 0");
		rewritePred("x + z + y < z", "x + y < 0");

		// A + ... + E + ... + B  < C + ... + E + ... + D   == A + ... + B < C + ... + D
		rewritePred("a + b < c ∗ 2 + b", "a < c ∗ 2");
		rewritePred("b + a < c ∗ 2 + b", "a < c ∗ 2");
		rewritePred("a + b < b + c ∗ 2", "a < c ∗ 2");
		rewritePred("b + a < b + c ∗ 2", "a < c ∗ 2");
		rewritePred("a + b < c + b + d", "a < c + d");
		rewritePred("a + b < c + d + b", "a < c + d");
		rewritePred("a + b < b + c + d", "a < c + d");
		rewritePred("a + b + d < c + d", "a + b < c");
		rewritePred("a + d + b < c + d", "a + b < c");
		rewritePred("d + a + b < c + d", "a + b < c");
		rewritePred("a + b + e < c + d + e", "a + b < c + d");
		rewritePred("a + e + b < c + d + e", "a + b < c + d");
		rewritePred("e + a + b < c + d + e", "a + b < c + d");
		rewritePred("a + b + e < c + e + d", "a + b < c + d");
		rewritePred("a + e + b < c + e + d", "a + b < c + d");
		rewritePred("e + a + b < c + e + d", "a + b < c + d");
		rewritePred("a + b + e < e + c + d", "a + b < c + d");
		rewritePred("a + e + b < e + c + d", "a + b < c + d");
		rewritePred("e + a + b < e + c + d", "a + b < c + d");

		// C <= A + ... + C + ... + B  ==  0 <= A + ... + B
		rewritePred("y ≤ x + y", "0 ≤ x");
		rewritePred("x ≤ x + y", "0 ≤ y");
		rewritePred("z ≤ x + y + z", "0 ≤ x + y");
		rewritePred("z ≤ z + x + y", "0 ≤ x + y");
		rewritePred("z ≤ x + z + y", "0 ≤ x + y");

		// A + ... + C + ... + B <= C  ==  A + ... + B <= 0
		rewritePred("x + y ≤ y", "x ≤ 0");
		rewritePred("x + y ≤ x", "y ≤ 0");
		rewritePred("x + y + z ≤ z", "x + y ≤ 0");
		rewritePred("z + x + y ≤ z", "x + y ≤ 0");
		rewritePred("x + z + y ≤ z", "x + y ≤ 0");

		// A + ... + E + ... + B  <= C + ... + E + ... + D   == A + ... + B <= C + ... + D
		rewritePred("a + b ≤ c ∗ 2 + b", "a ≤ c ∗ 2");
		rewritePred("b + a ≤ c ∗ 2 + b", "a ≤ c ∗ 2");
		rewritePred("a + b ≤ b + c ∗ 2", "a ≤ c ∗ 2");
		rewritePred("b + a ≤ b + c ∗ 2", "a ≤ c ∗ 2");
		rewritePred("a + b ≤ c + b + d", "a ≤ c + d");
		rewritePred("a + b ≤ c + d + b", "a ≤ c + d");
		rewritePred("a + b ≤ b + c + d", "a ≤ c + d");
		rewritePred("a + b + d ≤ c + d", "a + b ≤ c");
		rewritePred("a + d + b ≤ c + d", "a + b ≤ c");
		rewritePred("d + a + b ≤ c + d", "a + b ≤ c");
		rewritePred("a + b + e ≤ c + d + e", "a + b ≤ c + d");
		rewritePred("a + e + b ≤ c + d + e", "a + b ≤ c + d");
		rewritePred("e + a + b ≤ c + d + e", "a + b ≤ c + d");
		rewritePred("a + b + e ≤ c + e + d", "a + b ≤ c + d");
		rewritePred("a + e + b ≤ c + e + d", "a + b ≤ c + d");
		rewritePred("e + a + b ≤ c + e + d", "a + b ≤ c + d");
		rewritePred("a + b + e ≤ e + c + d", "a + b ≤ c + d");
		rewritePred("a + e + b ≤ e + c + d", "a + b ≤ c + d");
		rewritePred("e + a + b ≤ e + c + d", "a + b ≤ c + d");
		
		// A - C = B - C  ==  A = B
		rewritePred("a − b = c ∗ 2 − b", "a = c ∗ 2");
		
		// A - C < B - C  ==  A < B
		rewritePred("a − b < c ∗ 2 − b", "a < c ∗ 2");
		
		// A - C <= B - C  ==  A <= B
		rewritePred("a − b ≤ c ∗ 2 − b", "a ≤ c ∗ 2");
		
		// C - A = C - B  ==  B = A
		rewritePred("b − a = b − c ∗ 2", "c ∗ 2 = a");
		
		// C - A < C - B  ==  B < A
		rewritePred("b − a < b − c ∗ 2", "c ∗ 2 < a");
		
		// C - A <= C - B  ==  B <= A
		rewritePred("b − a ≤ b − c ∗ 2", "c ∗ 2 ≤ a");
	}

	@Test
	public void testBugZeroPlusZero() throws Exception {
		// the following succeeds
		noRewriteExpr("0+0");	// FIXME what's this
		// the following fails
		rewritePred("1+1=1", "1=0");
		rewritePred("1=1+1", "0=1");
		rewritePred("1+1=1+1", "1=1");
	}
}
