/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - refactored to use StringBuilder instead of String concat
 *******************************************************************************/
package org.evenb.ui.prover.tests;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.internal.ui.prover.PredicateUtil.appendPredicate;
import junit.framework.TestCase;

import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class TestAddSpacingPredicate extends TestCase {
	private void addSpacingTest(String msg, String predString,
			String expectedPrettyPrint) {
		final IParseResult parseResult = Lib.ff.parsePredicate(predString, V2, null);
		if (parseResult.hasProblem()) {
			System.out.println(parseResult.getProblems());
			fail("Parse failed");
		}
		final Predicate parsedPred = parseResult.getParsedPredicate();
		final StringBuilder sb = new StringBuilder();
		appendPredicate(sb, predString, parsedPred);
		assertEquals(msg + ": ", expectedPrettyPrint, sb.toString());
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
		// TODO implement this test
	}

	public void testQuantifiedPredicate() {
		// TODO implement this test
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
		// TODO implement this test
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
