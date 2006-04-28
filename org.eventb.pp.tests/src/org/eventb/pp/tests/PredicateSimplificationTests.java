package org.eventb.pp.tests;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pp.Translator;

public class PredicateSimplificationTests extends AbstractTranslationTests {
	
	public static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, FastFactory.mTypeEnvironment());
	}

	public static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		assertTrue("Input is not typed: " + input, input.isTypeChecked());
		assertTrue("Expected result is not typed: " + expected, 
				expected.isTypeChecked());

		Predicate actual = Translator.simplifyPredicate(input, ff);
		
		assertTrue("Actual result is not typed: " + actual, 
				actual.isTypeChecked());
		assertTrue("Result not in goal: " + actual,
				Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	
	public void testAndRule1 () {
		doTest( "⊤ ∧ ⊤",
				"⊤ ", false);
	}
	
	public void testAndRule12 () {
		doTest( "⊤ ∧ ⊥",
				"⊥ ", false);
	}
	
	public void testAndRule3 () {
		doTest( "⊥ ∧ ⊤",
				"⊥ ", false);
	}

	public void testAndRule4 () {
		doTest( "(⊤ ∧ ⊥) ∨ (⊤ ∧ ⊤ ∧ ⊤)",
				"⊤", false);
	}

	public void testOrRule1 () {
		doTest( "⊤ ∨ ⊤",
				"⊤ ", false);
	}

	public void testOrRule2 () {
		doTest( "⊥ ∨ ⊤",
				"⊤ ", false);
	}

	public void testOrRule3 () {
		doTest( "⊤ ∨ ⊥",
				"⊤ ", false);
	}
	
	public void testOrRule4 () {
		doTest( "(⊤ ∨ ⊥) ∧ (⊥ ∨ ⊥ ∨ ⊥)",
				"⊥ ", false);
	}
	
	public void testImplRule1() {
		doTest( "a>b ⇒ ⊤",
				"⊤", false);
	}
	
	public void testImplRule2() {
		doTest( "⊥ ⇒ a>b",
				"⊤", false);
	}
	
	public void testImplRule3() {
		doTest( "⊤ ⇒ a>b",
				"a>b", false);
	}

	public void testImplRule4() {
		doTest( "⊤ ⇒ (⊤ ∨ (a>b))",
				"⊤", false);
	}

	public void testImplRule5() {
		doTest( "a>b ⇒ ⊥",
				"¬(a>b)", false);
	}
	
	public void testImplRule6() {
		doTest( "⊤∧(a>b) ⇒ ⊥",
				"¬(a>b)", false);
	}

	public void testNotRule1() {
		doTest( "¬⊤",
				"⊥", false);
	}
	
	public void testNotRule2() {
		doTest( "¬⊥",
				"⊤", false);
	}
}
