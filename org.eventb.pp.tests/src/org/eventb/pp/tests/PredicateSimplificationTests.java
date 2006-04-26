package org.eventb.pp.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.pp.Translator;

public class PredicateSimplificationTests extends TestCase {
	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static IntegerType INT = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	private static PowerSetType INT_SET = ff.makePowerSetType(INT);

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	public static Predicate parse(String string, ITypeEnvironment te) {
		IParseResult parseResult = ff.parsePredicate(string);
		assertTrue("Parse error for: " + string + " Problems: " + parseResult.getProblems(), parseResult.isSuccess());
		Predicate pred = parseResult.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(te);
		assertTrue(string + " is not typed. Problems: " + tcResult.getProblems(), tcResult.isSuccess());
		return pred;
	}
	
	public static Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}
	
	public static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, FastFactory.mTypeEnvironment());
	}

	public static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if(transformExpected) {
			pexpected = Translator.simplifyPredicate(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		ITypeCheckResult tcr = null;
		tcr = input.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Input is not typed: " + tcr.getProblems(), tcr.isSuccess());
		tcr=expected.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Expected result is not typed: " + tcr.getProblems(), tcr.isSuccess());

		Predicate actual = Translator.simplifyPredicate(input, ff);
		
		tcr=actual.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Actual result is not typed: " + tcr.getProblems(), tcr.isSuccess());
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual, ff));
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
