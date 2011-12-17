/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.tests.FastFactory;
import org.eventb.pptrans.Translator;

public class PredicateSimplificationTests extends AbstractTranslationTests {
	
	public static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, FastFactory.mTypeEnvironment());
	}

	public static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		doTest(pinput, pexpected);
	}
	
	@SuppressWarnings("deprecation")
	private static void doTest(Predicate input, Predicate expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);

		Predicate actual = Translator.simplifyPredicate(input, ff);
		
		assertTypeChecked(actual);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	
	/**
	 * Tests for PR1
	 */
	public void testPR1_simple () {
		doTest( "a>b ∧ ⊥ ∧ c>d",
				"⊥", false);
	}
	
	/**
	 * Tests for PR2
	 */
	public void testPR2_simple () {
		doTest( "a>b ∨ ⊤ ∨ c>d",
				"⊤", false);
	}

	/**
	 * Tests for PR3
	 */
	public void testPR3_simple () {
		doTest( "a>b ∧ ⊤ ∧ c>d",
				"a>b ∧ c>d", false);
	}

	public void testPR3_recursive () {
		doTest( "(⊤ ∧ ⊤) ∧ ⊤ ∧ c>d",
				"c>d", false);
	}

	/**
	 * Tests for PR4
	 */
	public void testPR4_simple () {
		doTest( "a>b ∨ ⊥ ∨ c>d",
				"a>b ∨ c>d", false);
	}

	public void testPR4_recursive () {
		doTest( "(⊥ ∨ ⊥) ∨ ⊥ ∨ c>d",
				"c>d", false);
	}
	
	/**
	 * Tests for PR5
	 */
	public void testPR5_simple () {
		doTest( "a>b ⇒ ⊤",
				"⊤", false);
	}

	/**
	 * Tests for PR6
	 */
	public void testPR6_simple () {
		doTest( "⊥ ⇒ a>b",
				"⊤", false);
	}
	
	/**
	 * Tests for PR7
	 */
	public void testPR7_simple () {
		doTest( "⊤ ⇒ a>b",
				"a>b", false);
	}

	public void testPR7_recursive () {
		doTest( "⊤ ⇒ (⊤ ⇒ a>b)",
				"a>b", false);
	}

	/**
	 * Tests for PR8
	 */
	public void testPR8_simple () {
		doTest( "a>b ⇒ ⊥",
				"¬(a>b)", false);
	}

	public void testPR8_recursive () {
		doTest( "(a>b ⇒ ⊥) ⇒ ⊥",
				"a>b", false);
	}
	
	/**
	 * Tests for PR9
	 */
	public void testPR9_simple() {
		doTest( "¬⊤",
				"⊥", false);
	}
	
	/**
	 * Tests for PR10
	 */
	public void testPR10_simple() {
		doTest( "¬⊥",
				"⊤", false);
	}

	/**
	 * Tests for PR11
	 */
	public void testPR11_simple() {
		doTest( "¬¬(a>b)",
				"a>b", false);
	}

	public void testPR11_recursive() {
		doTest( "¬¬(¬¬(a>b))",
				"a>b", false);
	}

	public void testPR11_recursive2() {
		doTest( "¬¬¬(a>b)",
				"¬(a>b)", false);
	}

	/**
	 * Tests for PR12
	 */
	public void testPR12_simple() {
		doTest( "a>b ⇔ a>b",
				"⊤", false);
	}
	
	/**
	 * Tests for PR13
	 */
	public void testPR13_simple() {
		doTest( "a>b ⇔ ⊤",
				"a>b", false);
	}

	public void testPR13_recursive() {
		doTest( "(a>b⇔ ⊤) ⇔ ⊤",
				"a>b", false);
	}

	public void testPR13_reversed() {
		doTest( "⊤ ⇔ a>b",
				"a>b", false);
	}

	/**
	 * Tests for PR14
	 */
	public void testPR14_simple() {
		doTest( "a>b ⇔ ⊥",
				"¬(a>b)", false);
	}

	public void testPR14_recursive() {
		doTest( "(a>b⇔ ⊥) ⇔ ⊥",
				"a>b", false);
	}

	public void testPR14_reversed() {
		doTest( "⊥ ⇔ a>b",
				"¬(a>b)", false);
	}
	
	/**
	 * Tests for PR15
	 */
	public void testPR15_simple() {
		doTest( FastFactory.mQuantifiedPredicate(
					Formula.FORALL, 
					mList(
							FastFactory.mBoundIdentDecl("x0", INT),
							FastFactory.mBoundIdentDecl("x1", INT)),
					FastFactory.mLiteralPredicate(Formula.BTRUE)),
				FastFactory.mLiteralPredicate(Formula.BTRUE));
	}
	
	public void testPR15_simple2() {
		doTest( FastFactory.mQuantifiedPredicate(
				Formula.EXISTS, 
				mList(
						FastFactory.mBoundIdentDecl("x0", INT),
						FastFactory.mBoundIdentDecl("x1", INT)),
				FastFactory.mLiteralPredicate(Formula.BTRUE)),
			FastFactory.mLiteralPredicate(Formula.BTRUE));
	}
	
	/**
	 * Tests for PR16
	 */
	public void testPR16_simple() {
		doTest( FastFactory.mQuantifiedPredicate(
				Formula.FORALL, 
				mList(
						FastFactory.mBoundIdentDecl("x0", INT),
						FastFactory.mBoundIdentDecl("x1", INT)),
				FastFactory.mLiteralPredicate(Formula.BFALSE)),
			FastFactory.mLiteralPredicate(Formula.BFALSE));
	}
	
	public void testPR16_simple2() {
		doTest( FastFactory.mQuantifiedPredicate(
				Formula.EXISTS, 
				mList(
						FastFactory.mBoundIdentDecl("x0", INT),
						FastFactory.mBoundIdentDecl("x1", INT)),
				FastFactory.mLiteralPredicate(Formula.BFALSE)),
			FastFactory.mLiteralPredicate(Formula.BFALSE));
	}
}
