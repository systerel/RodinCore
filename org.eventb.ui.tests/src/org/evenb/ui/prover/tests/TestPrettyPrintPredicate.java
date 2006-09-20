package org.evenb.ui.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.seqprover.Lib;
import org.eventb.internal.ui.prover.PredicateUtil;

public class TestPrettyPrintPredicate extends TestCase {

	private void predTest(String msg, String predString,
			String expectedPrettyPrint) {
		System.out.println("Predicate: \"" + predString + "\"");
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();

		String prettyPrint = PredicateUtil.prettyPrint(30, predString,
				parsedPred);
		System.out.println("Pretty Print: \"" + prettyPrint + "\"");

		assertEquals(msg + ": ", expectedPrettyPrint, prettyPrint);
	}

	public void testAssociativePredicate() {
		predTest("And 1", "⊤\u2227⊤", "⊤ \u2227 ⊤");
		predTest(
				"And 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤",
				"⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227"
						+ "\n" + "⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤");
		predTest("Or 1", "⊤" + "\u2228" + "⊤", "⊤ \u2228 ⊤");
		predTest(
				"Or 2",
				"⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228"
						+ "\n" + "⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤");
	}

	public void testBinaryPredicate() {
		predTest("Imply 1", "⊤" + "\u21d2" + "⊤", "⊤ \u21d2 ⊤");
		predTest(
				"Imply 2",
				"⊤\u2227⊤\u2227⊤\u2227⊤\u2227⊤\u21d2⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤\u2228⊤",
				"  ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤ \u2227 ⊤"
						+ "\n\u21d2\n"
						+ "  ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228 ⊤ \u2228"
						+ "\n" + "  ⊤");
		predTest("Equivalent", "⊤" + "\u21d4" + "⊤", "⊤ \u21d4 ⊤");
	}

	public void testLiteralPredicate() {

	}

	public void testQuantifiedPredicate() {

	}

	public void testRelationalPred() {
		predTest("Equal", "1" + "=" + "2", "1 = 2");
		predTest("Not Equal", "1" + "\u2260" + "2", "1 \u2260 2");
		predTest("Less Than", "1" + "<" + "2", "1 < 2");
		predTest("Less Than Equal", "1" + "\u2264" + "2", "1 \u2264 2");
		predTest("Greater Than", "1" + ">" + "2", "1 > 2");
		predTest("Greater Than Equal", "1" + "\u2265" + "2", "1 \u2265 2");
		predTest("In", "1" + "\u2208" + "ℕ", "1 \u2208 ℕ");
		predTest("Not In", "1" + "\u2209" + "ℕ", "1 \u2209 ℕ");
		predTest("Subset", "ℕ" + "\u2282" + "ℕ", "ℕ \u2282 ℕ");
		predTest("Not Subset", "ℕ" + "\u2284" + "ℕ", "ℕ \u2284 ℕ");
		predTest("Subset Equal", "ℕ" + "\u2286" + "ℕ", "ℕ \u2286 ℕ");
		predTest("Not Subset Equal", "ℕ" + "\u2288" + "ℕ", "ℕ \u2288 ℕ");
	}

	public void testSimplePredicate() {

	}

	public void testUnaryPredicate() {
		predTest("Not", "\u00ac" + "⊤", " \u00ac " + "⊤");
	}

	public void testBrackets() {
		predTest("Brackets", "(1=2" + "\u2228" + "2=3)" + "\u2227" + "3=4",
				"(1 = 2" + "  \u2228  " + "2 = 3)" + "   \u2227   " + "3  =  4");
	}

	public void testPredicate1() {
		String predString = "(X=Y\u2228Z=T)\u2227U=V";
		System.out.println("Predicate: \"" + predString + "\"");
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();

		AssociativePredicate aPred1 = (AssociativePredicate) parsedPred;

		Predicate[] children1 = aPred1.getChildren();

		SourceLocation loc = children1[0].getSourceLocation();
		assertEquals("Source Loc Start 1 ", 1, loc.getStart());
		assertEquals("Source Loc End 1 ", 7, loc.getEnd());

		loc = children1[1].getSourceLocation();
		assertEquals("Source Loc Start 2 ", 10, loc.getStart());
		assertEquals("Source Loc End 2 ", 12, loc.getEnd());

		AssociativePredicate aPred2 = (AssociativePredicate) children1[0];
		Predicate[] children2 = aPred2.getChildren();

		loc = children2[0].getSourceLocation();
		assertEquals("Source Loc Start 3 ", 1, loc.getStart());
		assertEquals("Source Loc End 3 ", 3, loc.getEnd());

		loc = children2[1].getSourceLocation();
		assertEquals("Source Loc Start 4 ", 5, loc.getStart());
		assertEquals("Source Loc End 4 ", 7, loc.getEnd());

		RelationalPredicate rPred1 = (RelationalPredicate) children2[0];

		loc = rPred1.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 5 ", 1, loc.getStart());
		assertEquals("Source Loc End 5 ", 1, loc.getEnd());

		loc = rPred1.getRight().getSourceLocation();
		assertEquals("Source Loc Start 6 ", 3, loc.getStart());
		assertEquals("Source Loc End 6 ", 3, loc.getEnd());

		RelationalPredicate rPred2 = (RelationalPredicate) children2[1];

		loc = rPred2.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 7 ", 5, loc.getStart());
		assertEquals("Source Loc End 7 ", 5, loc.getEnd());

		loc = rPred2.getRight().getSourceLocation();
		assertEquals("Source Loc Start 8 ", 7, loc.getStart());
		assertEquals("Source Loc End 8 ", 7, loc.getEnd());

	}

	public void testPredicate2() {
		String predString = "1" + "\u21a6" + "2" + "\u2208" + "\u2115"
				+ "\u21f8" + "\u2115";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;

		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 2, loc.getEnd());

		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 4, loc.getStart());
		assertEquals("Source Loc End 2 ", 6, loc.getEnd());
	}

	public void testPredicate3() {
		String predString = "{1}\u2282\u2115";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;

		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 2, loc.getEnd());

		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 4, loc.getStart());
		assertEquals("Source Loc End 2 ", 4, loc.getEnd());
	}

	public void testPredicate4() {
		String predString = "∅∈∅↔S";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;

		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 0, loc.getEnd());

		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 2, loc.getStart());
		assertEquals("Source Loc End 2 ", 4, loc.getEnd());				
	}
	
	public void testPredicate5() {
		String predString = "a∈dom(f)∧" +
				"f∼;({a}◁f)⊆id(ℤ)";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		AssociativePredicate aPred = (AssociativePredicate) parsedPred;

		Predicate [] children = aPred.getChildren();
		
		SourceLocation loc = children[0].getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 7, loc.getEnd());

		loc = children[1].getSourceLocation();
		assertEquals("Source Loc Start 2 ", 9, loc.getStart());
		assertEquals("Source Loc End 2 ", 24, loc.getEnd());				
	}
	
	public void testPredicate6() {
		String predString = "f∼;({a}◁f)⊆id(ℤ)";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;

		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 9, loc.getEnd());

		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 11, loc.getStart());
		assertEquals("Source Loc End 2 ", 25, loc.getEnd());				
	}

	public void testPredicate7() {
		String predString = "f(a)⊆S";
		System.out.println("Predicate: " + predString);
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assertTrue("Parse Successful", parseResult.isSuccess());
		Predicate parsedPred = parseResult.getParsedPredicate();
		RelationalPredicate rPred = (RelationalPredicate) parsedPred;

		SourceLocation loc = rPred.getLeft().getSourceLocation();
		assertEquals("Source Loc Start 1 ", 0, loc.getStart());
		assertEquals("Source Loc End 1 ", 3, loc.getEnd());

		loc = rPred.getRight().getSourceLocation();
		assertEquals("Source Loc Start 2 ", 5, loc.getStart());
		assertEquals("Source Loc End 2 ", 5, loc.getEnd());				
	}

}
