package org.evenb.ui.prover.tests;

import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Lib;
import org.eventb.internal.ui.prover.PredicateUtil;

import junit.framework.TestCase;

public class TestAddSpacingPredicate extends TestCase {
	private void addSpacingTest(String msg, String predString,
			String expectedPrettyPrint) {
		System.out.println("Predicate: \"" + predString + "\"");
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();

		String prettyPrint = PredicateUtil.addSpacing(predString, parsedPred,
				false);
		System.out.println("Add Spacing: \"" + prettyPrint + "\"");

		assertEquals(msg + ": ", expectedPrettyPrint, prettyPrint);
	}

	public void testAssociativePredicate() {
		addSpacingTest("And 1", "⊤\u2227⊤", "⊤ \u2227 ⊤");
		addSpacingTest(
				"And 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤",
				"⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤");
		addSpacingTest("Or 1", "⊤" + "\u2228" + "⊤", "⊤ \u2228 ⊤");
		addSpacingTest(
				"Or 2",
				"⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤");
	}

	public void testBinaryPredicate() {
		addSpacingTest("Imply 1", "⊤" + "\u21d2" + "⊤", "⊤ \u21d2 ⊤");
		addSpacingTest(
				"Imply 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u21d2⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤  \u21d2  ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤");
		addSpacingTest("Equivalent 1", "⊤" + "\u21d4" + "⊤", "⊤ \u21d4 ⊤");
		addSpacingTest(
				"Equivalent 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u21d4⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤  \u21d4  ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤");
	}

	public void testLiteralPredicate() {

	}

	public void testQuantifiedPredicate() {

	}

	public void testRelationalPred() {
		addSpacingTest("Equal", "1" + "=" + "2", "1=2");
		addSpacingTest("Not Equal", "1" + "\u2260" + "2", "1" + "\u2260" + "2");
		addSpacingTest("Less Than", "1" + "<" + "2", "1<2");
		addSpacingTest("Less Than Equal", "1" + "\u2264" + "2", "1" + "\u2264"
				+ "2");
		addSpacingTest("Greater Than", "1" + ">" + "2", "1>2");
		addSpacingTest("Greater Than Equal", "1" + "\u2265" + "2", "1\u2265"
				+ "2");
		addSpacingTest("In", "1" + "\u2208" + "ℕ", "1\u2208ℕ");
		addSpacingTest("Not In", "1" + "\u2209" + "ℕ", "1\u2209ℕ");
		addSpacingTest("Subset", "ℕ" + "\u2282" + "ℕ", "ℕ\u2282ℕ");
		addSpacingTest("Not Subset", "ℕ" + "\u2284" + "ℕ", "ℕ\u2284ℕ");
		addSpacingTest("Subset Equal", "ℕ" + "\u2286" + "ℕ", "ℕ\u2286ℕ");
		addSpacingTest("Not Subset Equal", "ℕ" + "\u2288" + "ℕ", "ℕ\u2288ℕ");
	}

	public void testSimplePredicate() {

	}

	public void testUnaryPredicate() {
		addSpacingTest("Not", "\u00ac" + "⊤", " \u00ac " + "⊤");
	}

	public void testBrackets() {
		addSpacingTest("Brackets", "(1=2" + "\u2228" + "2=3" + "\u2228"
				+ "3=4)" + "\u2227" + "4=5", "(1=2" + " \u2228 " + "2=3"
				+ " \u2228 " + "3=4)" + "  \u2227  " + "4 = 5");
	}

}
