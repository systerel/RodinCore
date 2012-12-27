/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - adapted tests after ER10 rule fixing (see bug #3495675)
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.pptrans.Translator;
import org.junit.Test;


/**
 * Unit testing of class org.eventb.internal.pp.translator.Reorganizer.
 * 
 */
public class ReorganisationTests extends AbstractTranslationTests {
	
	private static final ITypeEnvironmentBuilder defaultTe = mTypeEnvironment(
			"S=ℙ(S); T=ℙ(T); U=ℙ(U); V=ℙ(V); a=ℤ; b=S; A=ℙ(ℤ); B=ℙ(S)", ff);

	private static void doTest(String input, String expected) {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addAll(defaultTe);
		doTest(input, expected, false, te);
	}
	
	private static void doTest(String input, String expected,
			boolean transformExpected) {
		ITypeEnvironmentBuilder te = ff.makeTypeEnvironment();
		te.addAll(defaultTe);
		doTest(input, expected, transformExpected, te);
	}
	
	@SuppressWarnings("deprecation")
	private static void doTest(String input, String expected,
			boolean transformExpected, ITypeEnvironmentBuilder te) {

		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if (transformExpected) {
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	@SuppressWarnings("deprecation")
	private static void doTest(Predicate input, Predicate expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);

		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);

		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		
		// System.out.println(expected.getSyntaxTree());
		// System.out.println(actual.getSyntaxTree());
		
		assertEquals("Unexpected result for " + input, expected, actual);
	}

	// Runs twice the same test, one with "min" the second with "max".
	private static void doTestMinMax(String input, String expected) {
		doTest(input, expected, true);
		doTest( input.replaceAll("min", "max"),
				expected.replaceAll("min", "max"),
				true);
	}
	
	/**
	 * Returns an array of arithmetic expressions different from a single identifier
	 * and containing the given term.
	 */
	private String[] makeArithExprs(String term) {
		return new String[] {
			"−(" + term + ")",

			"(" + term + ") + 1",
			"1 + (" + term + ") + 2",
			"1 + (" + term + ")",

			"(" + term + ") ∗ 2",
			"2 ∗ (" + term + ") ∗ 3",
			"2 ∗ (" + term + ")",
			
			"(" + term + ") − 1",
			"1 − (" + term + ")",
			
			"(" + term + ") ÷ 2",
			"1 ÷ (" + term + ")",

			"(" + term + ") mod 2",
			"2 mod (" + term + ")",

			"(" + term + ") ^ 2",
			"2 ^ (" + term + ")",
		};
	}
	
	/**
	 * Ensures that a function application is not extracted when equalled to an
	 * identifier.
	 */
	@Test
	public void testFuncNoExtractEqualIdent() {
		doTest("a = f(b)", "b↦a∈f");
		doTest("f(b) = a", "b↦a∈f");
		doTest("a ≠ f(b)", "¬(b↦a∈f)");
		doTest("f(b) ≠ a", "¬(b↦a∈f)");
	}

	/**
	 * Ensures that a function application is not extracted when equalled to an
	 * arithmetic expression which is not an identifier.
	 */
	@Test
	public void testFuncNoExtractEqualExpr() {
		
		List<String> arithExprs = new ArrayList<String>();
		arithExprs.add("1");
		arithExprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr : arithExprs) {
			doTest(expr + " = f(b)", "∃x·x=" + expr + " ∧ b↦x∈f");
			doTest("f(b) = " + expr, "∃x·x=" + expr + " ∧ b↦x∈f");
			doTest(expr + " ≠ f(b)", "¬(∃x·x=" + expr + " ∧ b↦x∈f)");
			doTest("f(b) ≠ " + expr, "¬(∃x·x=" + expr + " ∧ b↦x∈f)");
		}
	}
	
	/**
	 * Ensures that a function application is extracted when occurring on the
	 * side of an inequality.
	 */
	@Test
	public void testFuncExtractInequality() {

		List<String> exprs = new ArrayList<String>();
		exprs.add("a");
		exprs.add("1");
		exprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr: exprs) {
			// less than
			doTest(expr + " < f(b)", "∀x·b↦x∈f ⇒ " + expr + " < x");
			doTest("f(b) < " + expr, "∀x·b↦x∈f ⇒ x < " + expr);
			
			// less than or equal to
			doTest(expr + " ≤ f(b)", "∀x·b↦x∈f ⇒ " + expr + " ≤ x");
			doTest("f(b) ≤ " + expr, "∀x·b↦x∈f ⇒ x ≤ " + expr);

			// greater than
			doTest(expr + " > f(b)", "∀x·b↦x∈f ⇒ x < " + expr);
			doTest("f(b) > " + expr, "∀x·b↦x∈f ⇒ " + expr + " < x");

			// greater than or equal to
			doTest(expr + " ≥ f(b)", "∀x·b↦x∈f ⇒ x ≤ " + expr);
			doTest("f(b) ≥ " + expr, "∀x·b↦x∈f ⇒ " + expr + " ≤ x");
		}
	}
		
	/**
	 * Ensures that a function application is extracted when occurring inside
	 * an arithmetic expression.
	 */
	@Test
	public void testFuncExtractArithmeticExpression() {

		String[] inExprs = makeArithExprs("f(b)");
		String[] outExprs = makeArithExprs("x");
		for (int i = 0; i < inExprs.length; i++) {
			String inExpr = inExprs[i];
			String outExpr = outExprs[i];
			
			// equal to
			doTest(inExpr + " = a", "∀x·b↦x∈f ⇒ " + outExpr + " = a");
			doTest("a = " + inExpr, "∀x·b↦x∈f ⇒ a = " + outExpr);
			
			// not equal to
			doTest(inExpr + " ≠ a", "¬(∀x·b↦x∈f ⇒ " + outExpr + " = a)");
			doTest("a ≠ " + inExpr, "¬(∀x·b↦x∈f ⇒ a = " + outExpr + ")");
			
			// less than
			doTest(inExpr + " < a", "∀x·b↦x∈f ⇒ " + outExpr + " < a");
			doTest("a < " + inExpr, "∀x·b↦x∈f ⇒ a < " + outExpr);
			
			// less than or equal to
			doTest(inExpr + " ≤ a", "∀x·b↦x∈f ⇒ " + outExpr + " ≤ a");
			doTest("a ≤ " + inExpr, "∀x·b↦x∈f ⇒ a ≤ " + outExpr);
			
			// greater than
			doTest(inExpr + " > a", "∀x·b↦x∈f ⇒ a < " + outExpr);
			doTest("a > " + inExpr, "∀x·b↦x∈f ⇒ " + outExpr + " < a");

			// greater than or equal to
			doTest(inExpr + " ≥ a", "∀x·b↦x∈f ⇒ a ≤ " + outExpr);
			doTest("a ≥ " + inExpr, "∀x·b↦x∈f ⇒ " + outExpr + " ≤ a");
		}
	}
		
	/**
	 * Ensures that function applications are extracted recursively. We do it in
	 * only one context (from the right-hand side of an inequality), as other
	 * context should behave the same (this is checked with the simple tests).
	 */
	@Test
	public void testFuncExtractRecursive() {
		doTest("f(1+g(b)) ≤ 3", "∀x·(∃y·(∀z·b↦z∈g ⇒ y=1+z) ∧ y↦x∈f) ⇒ x≤3");
	}
	
	/**
	 * Ensures that extraction of a function application doesn't break the de
	 * Bruijn coding of bound identifiers.
	 */
	@Test
	public void testFuncExtractBound() {
		doTest("∃a,b·a = f(b∪{c∣c∈b}) + 1",
				"∃a,b·∀x·(b∪{c∣c∈b})↦x∈f ⇒ a = x + 1", true,
				mTypeEnvironment("f=ℙ(S)↔ℤ", ff));
	}
	

	/**
	 * Ensures that a cardinal is not extracted when equalled to an identifier.
	 */
	@Test
	public void testCardNoExtractEqualIdent() {
		doTest("a = card(B)", "0≤a∧(∃f·f∈B⤖1‥a)", true);
		doTest("card(B) = a", "0≤a∧(∃f·f∈B⤖1‥a)", true);
		doTest("a ≠ card(B)", "¬(0≤a∧∃f·f∈B⤖1‥a)", true);
		doTest("card(B) ≠ a", "¬(0≤a∧∃f·f∈B⤖1‥a)", true);
	}

	/**
	 * Ensures that a cardinal is extracted when equalled to an arithmetic
	 * expression which is not an identifier.
	 */
	@Test
	public void testCardExtractEqualExpr() {
		
		List<String> arithExprs = new ArrayList<String>();
		arithExprs.add("1");
		arithExprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr : arithExprs) {
			doTest( expr + " = card(B)",
					"∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + "=x",
					true);
			doTest( "card(B) = " + expr,
					"∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x=" + expr,
					true);
			doTest( expr + " ≠ card(B)", 
					"¬(∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + "=x)",
					true);
			doTest( "card(B) ≠ " + expr, 
					"¬(∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x=" + expr + ")",
					true);
		}
	}
	
	/**
	 * Ensures that a cardinal is extracted when occurring on the
	 * side of an inequality.
	 */
	@Test
	public void testCardExtractInequality() {

		List<String> exprs = new ArrayList<String>();
		exprs.add("a");
		exprs.add("1");
		exprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr: exprs) {
			// less than
			doTest(expr + " < card(B)", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + " < x", true);
			doTest("card(B) < " + expr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x < " + expr, true);
			
			// less than or equal to
			doTest(expr + " ≤ card(B)", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + " ≤ x", true);
			doTest("card(B) ≤ " + expr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x ≤ " + expr, true);

			// greater than
			doTest(expr + " > card(B)", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x < " + expr, true);
			doTest("card(B) > " + expr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + " < x", true);

			// greater than or equal to
			doTest(expr + " ≥ card(B)", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ x ≤ " + expr, true);
			doTest("card(B) ≥ " + expr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + expr + " ≤ x", true);
		}
	}

	/**
	 * Ensures that a cardinal is extracted when occurring inside
	 * an arithmetic expression.
	 */
	@Test
	public void testCardExtractArithmeticExpression() {

		String[] inExprs = makeArithExprs("card(B)");
		String[] outExprs = makeArithExprs("x");
		for (int i = 0; i < inExprs.length; i++) {
			String inExpr = inExprs[i];
			String outExpr = outExprs[i];
			
			// equal to
			doTest(inExpr + " = a", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " = a", true);
			doTest("a = " + inExpr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a = " + outExpr, true);
			
			// not equal to
			doTest(inExpr + " ≠ a", "¬(∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " = a)", true);
			doTest("a ≠ " + inExpr, "¬(∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a = " + outExpr + ")", true);
			
			// less than
			doTest(inExpr + " < a", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " < a", true);
			doTest("a < " + inExpr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a < " + outExpr, true);
			
			// less than or equal to
			doTest(inExpr + " ≤ a", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " ≤ a", true);
			doTest("a ≤ " + inExpr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a ≤ " + outExpr, true);
			
			// greater than
			doTest(inExpr + " > a", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a < " + outExpr, true);
			doTest("a > " + inExpr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " < a", true);

			// greater than or equal to
			doTest(inExpr + " ≥ a", "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ a ≤ " + outExpr, true);
			doTest("a ≥ " + inExpr, "∀x·(0≤x∧(∃f·f∈B⤖1‥x)) ⇒ " + outExpr + " ≤ a", true);
		}
	}
		
	/**
	 * Ensures that cardinals are extracted recursively. We do it in only one
	 * context (from the right-hand side of an inequality), as other context
	 * should behave the same (this is checked with the simple tests).
	 * 
	 * Note that we do not give the full expansion, as it gets very complex. The
	 * fact that all cardinals have been translated is verified by checking that
	 * the result is in the goal.
	 */
	@Test
	public void testCardExtractRecursive() {
		doTest( "card({card(B)}) ≤ 3",
				"∀x·(0≤x∧(∃f·f∈{card(B)}⤖1‥x)) ⇒  x≤3",
				true);
	}
	
	/**
	 * Ensures that extraction of a cardinal doesn't break the de Bruijn coding
	 * of bound identifiers.
	 */
	@Test
	public void testCardExtractBound() {
		doTest( "∃a,b·b⊆S ∧ a = card(b∪{c∣c∈b}) + 1",
				"∃a,b·b⊆S ∧ (∀x·(0≤x∧(∃f·f∈(b∪{c∣c∈b})⤖1‥x)) ⇒ a = x + 1)",
				true);
	}
	
	
	/**
	 * Ensures that a minimum or maximum is not extracted when equalled to an
	 * identifier.
	 */
	@Test
	public void testMinMaxNoExtractEqualIdent() {
		doTest("a = min(A)", "a∈A ∧ (∀x·x∈A ⇒ a ≤ x)");
		doTest("min(A) = a", "a∈A ∧ (∀x·x∈A ⇒ a ≤ x)");
		doTest("a ≠ min(A)", "¬(a∈A ∧ (∀x·x∈A ⇒ a ≤ x))");
		doTest("min(A) ≠ a", "¬(a∈A ∧ (∀x·x∈A ⇒ a ≤ x))");

		doTest("a = max(A)", "a∈A ∧ (∀x·x∈A ⇒ x ≤ a)");
		doTest("max(A) = a", "a∈A ∧ (∀x·x∈A ⇒ x ≤ a)");
		doTest("a ≠ max(A)", "¬(a∈A ∧ (∀x·x∈A ⇒ x ≤ a))");
		doTest("max(A) ≠ a", "¬(a∈A ∧ (∀x·x∈A ⇒ x ≤ a))");
	}

	/**
	 * Ensures that a minimum or maximum is extracted when equalled to an
	 * arithmetic expression which is not an identifier.
	 */
	@Test
	public void testMinMaxExtractEqualExpr() {
		
		List<String> arithExprs = new ArrayList<String>();
		arithExprs.add("1");
		arithExprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr : arithExprs) {
			doTestMinMax(expr + " = min(A)", "∀x·x=min(A) ⇒ " + expr + " = x");
			doTestMinMax("min(A) = " + expr, "∀x·x=min(A) ⇒ x = " + expr);
			doTestMinMax(expr + " ≠ min(A)", "¬(∀x·x=min(A) ⇒ " + expr + " = x)");
			doTestMinMax("min(A) ≠ " + expr, "¬(∀x·x=min(A) ⇒ x = " + expr + ")");
		}
	}
	
	/**
	 * Ensures that a minimum or maximum is not extracted when occurring on the
	 * side of an inequality.
	 */
	@Test
	public void testMinMaxNoExtractInequality() {

		List<String> exprs = new ArrayList<String>();
		exprs.add("a");
		exprs.add("1");
		exprs.addAll(Arrays.asList(makeArithExprs("a")));
		
		for (String expr: exprs) {
			// less than
			doTest(expr + " < min(A)", "∀x·x∈A ⇒ " + expr + " < x");
			doTest("min(A) < " + expr, "∃x·x∈A ∧ x < " + expr);
			doTest(expr + " < max(A)", "∃x·x∈A ∧ " + expr + " < x");
			doTest("max(A) < " + expr, "∀x·x∈A ⇒ x < " + expr);
			
			// less than or equal to
			doTest(expr + " ≤ min(A)", "∀x·x∈A ⇒ " + expr + " ≤ x");
			doTest("min(A) ≤ " + expr, "∃x·x∈A ∧ x ≤ " + expr);
			doTest(expr + " ≤ max(A)", "∃x·x∈A ∧ " + expr + " ≤ x");
			doTest("max(A) ≤ " + expr, "∀x·x∈A ⇒ x ≤ " + expr);

			// greater than
			doTest(expr + " > min(A)", "∃x·x∈A ∧ x < " + expr);
			doTest("min(A) > " + expr, "∀x·x∈A ⇒ " + expr + " < x");
			doTest(expr + " > max(A)", "∀x·x∈A ⇒ x < " + expr);
			doTest("max(A) > " + expr, "∃x·x∈A ∧ " + expr + " < x");

			// greater than or equal to
			doTest(expr + " ≥ min(A)", "∃x·x∈A ∧ x ≤ " + expr);
			doTest("min(A) ≥ " + expr, "∀x·x∈A ⇒ " + expr + " ≤ x");
			doTest(expr + " ≥ max(A)", "∀x·x∈A ⇒ x ≤ " + expr);
			doTest("max(A) ≥ " + expr, "∃x·x∈A ∧ " + expr + " ≤ x");
		}
	}
		
	/**
	 * Ensures that a x=min(A)imum or maximum is extracted when occurring inside
	 * an arithmetic expression.
	 */
	@Test
	public void testMinMaxExtractArithmeticExpression() {

		String[] inExprs = makeArithExprs("min(A)");
		String[] outExprs = makeArithExprs("x");
		for (int i = 0; i < inExprs.length; i++) {
			String inExpr = inExprs[i];
			String outExpr = outExprs[i];
			
			// equal to
			doTestMinMax(inExpr + " = a", "∀x·x=min(A) ⇒ " + outExpr + " = a");
			doTestMinMax("a = " + inExpr, "∀x·x=min(A) ⇒ a = " + outExpr);
			
			// not equal to
			doTestMinMax(inExpr + " ≠ a", "¬(∀x·x=min(A) ⇒ " + outExpr + " = a)");
			doTestMinMax("a ≠ " + inExpr, "¬(∀x·x=min(A) ⇒ a = " + outExpr + ")");
			
			// less than
			doTestMinMax(inExpr + " < a", "∀x·x=min(A) ⇒ " + outExpr + " < a");
			doTestMinMax("a < " + inExpr, "∀x·x=min(A) ⇒ a < " + outExpr);
			
			// less than or equal to
			doTestMinMax(inExpr + " ≤ a", "∀x·x=min(A) ⇒ " + outExpr + " ≤ a");
			doTestMinMax("a ≤ " + inExpr, "∀x·x=min(A) ⇒ a ≤ " + outExpr);
			
			// greater than
			doTestMinMax(inExpr + " > a", "∀x·x=min(A) ⇒ a < " + outExpr);
			doTestMinMax("a > " + inExpr, "∀x·x=min(A) ⇒ " + outExpr + " < a");

			// greater than or equal to
			doTestMinMax(inExpr + " ≥ a", "∀x·x=min(A) ⇒ a ≤ " + outExpr);
			doTestMinMax("a ≥ " + inExpr, "∀x·x=min(A) ⇒ " + outExpr + " ≤ a");
		}
	}
		
	/**
	 * Ensures that minimums and maximums are extracted recursively. We do it in
	 * only one context (from the right-hand side of an inequality), as other
	 * context should behave the same (this is checked with the simple tests).
	 * 
	 * Note that we do not give the full expansion, as it gets very complex. The
	 * fact that all minimums and maximums have been translated is verified by
	 * checking that the result is in the goal.
	 */
	@Test
	public void testMinMaxExtractRecursive() {
		doTest("1 + min({max(A), 2}) ≤ 3",
				"∀x·x=min({max(A), 2}) ⇒ 1+x≤3",
				true);
	}
	
	/**
	 * Ensures that extraction of a minimum or maximum doesn't break the de
	 * Bruijn coding of bound identifiers.
	 */
	@Test
	public void testMinMaxExtractBound() {
		doTest( "∃a,b·a = min(b∪{c∣c∈b}) + 1",
				"∃a,b·∀x·x=min(b∪{c∣c∈b}) ⇒ a = x + 1",
				true
		);
	}

	
	@Test
	public void testComplex() {
		doTest("card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)}) < 20",
				"∀x·x=card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)})⇒x<20",
				true, mTypeEnvironment("r=ℙ(ℤ)↔ℤ; f=ℤ↔ℤ", ff));
	}
	
	@Test
	public void testComplex2() {
		doTest("card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)}) < 20",
				"∀x·x=card({a·a>min({f·f(10)>card({1,2})∣max(r[t])})∣f(29)})⇒x<20",
				true, mTypeEnvironment("r=ℙ(ℤ)↔ℤ; f=ℤ↔ℤ", ff));
	}

	@Test
	public void testComplex3() {
		doTest("card({1,2}) + f(20) + min({1,2,3}) + max({2,1}) > 2",
				"∀x4,x3,x2,x1·x1=card({1,2}) ∧ x2=f(20) ∧ x3=min({1,2,3}) ∧ x4=max({2,1}) ⇒ x1+x2+x3+x4 > 2",
				true, mTypeEnvironment("r=ℙ(ℤ)↔ℤ; f=ℤ↔ℤ", ff));
	}

}
