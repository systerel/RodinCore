/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.EOF;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.IDENT;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.INT_LIT;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.PRED_VAR;
import static org.eventb.internal.core.parser.BMathV2.B_MATH_V2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.ParseResult;
import org.junit.Test;

/**
 * This tests the lexical analyzer.
 * <p>
 * Takes as input a string and verifies that the token is correctly recognized.
 * 
 * @author Laurent Voisin
 * @author François Terrier
 * 
 */
public class TestLexer extends AbstractTests {

	/*
	 * Operators that are used in classical B but are meaningless in Event-B. 
	 */
	private static final String[] invalidStrings = new String[] { "-", "/", };

	/**
	 * Tests all the tokens that are needed to construct an event-B formula.
	 */
	@Test 
	public void testToken() {
		for (FormulaFactory factory : ALL_VERSION_FACTORIES) {
			testAllTokens(factory);
		}
	}

	// Check each token string through the lexical analyzer.
	private void testAllTokens(FormulaFactory factory) {
		final AbstractGrammar grammar = factory.getGrammar();
		for (Entry<String, Integer> token : grammar.getTokens().entrySet()) {
			final String image = token.getKey();
			final Integer kind = token.getValue();
			testToken(image, kind, factory);
		}
		testToken("", grammar.getKind(EOF), factory);
		testToken("x", grammar.getKind(IDENT), factory);
		testToken("_toto", grammar.getKind(IDENT), factory);
		testToken("x'", grammar.getKind(IDENT), factory);
		testToken("2", grammar.getKind(INT_LIT), factory);
		testToken("3000000000", grammar.getKind(INT_LIT), factory);
		testToken("50000000000000000000", grammar.getKind(INT_LIT), factory);
		testToken("001", grammar.getKind(INT_LIT), factory);
		testToken("$P", grammar.getKind(PRED_VAR), factory);
		testToken("$_toto", grammar.getKind(PRED_VAR), factory);
		testToken("p'", grammar.getKind(IDENT), factory);
		testToken("prj'", grammar.getKind(IDENT), factory);
	}

	private void testToken(String image, int expectedKind, FormulaFactory factory) {
		final ParseResult result = new ParseResult(factory, null);
		final AbstractGrammar grammar = factory.getGrammar();
		final Scanner scanner = new Scanner(image, result, grammar);
		final Token t = scanner.Scan();
		assertEquals(image, t.val);
		final String msg = "for \"" + image + "\" with factory " + factory;
		assertEquals(msg, expectedKind, t.kind);
	}

	/**
	 * Ensure that invalid tokens get rejected.
	 */
	@Test 
	public void testInvalidStrings() {
		for (FormulaFactory factory : ALL_VERSION_FACTORIES) {
			testInvalidStrings(factory);
		}
	}

	private void testInvalidStrings(FormulaFactory factory) {
		for (String string : invalidStrings) {
			final ParseResult result = new ParseResult(factory, null);
			final AbstractGrammar grammar = factory.getGrammar();
			final Scanner scanner = new Scanner(string, result, grammar);
			Token t = scanner.Scan();
			assertTrue("Scanner should have succeeded", result.isSuccess());
			assertTrue(t.kind == 0); // _EOF
			assertTrue("Scanner should have a problem", result.hasProblem());
			List<ASTProblem> problems = result.getProblems();
			assertEquals("Should get one problem", 1, problems.size());
			ASTProblem problem = problems.get(0);
			assertEquals("The problem should be a warning",
					ProblemSeverities.Warning, problem.getSeverity());
			assertEquals("The problem should be a lexer error",
					ProblemKind.LexerError, problem.getMessage());
		}
	}
	
	@Test 
	public void testCodePoint() throws Exception {
		final int codePoint = 0x27C54;
		
		// pre requirements:
		assertTrue(Character.isSupplementaryCodePoint(codePoint));
		assertTrue(Character.isJavaIdentifierStart(codePoint));

		final char[] chars = Character.toChars(codePoint);
		final String str = String.copyValueOf(chars);

		// 2 code points in 3 chars
		// verify that the last character is not ignored
		final String ident = str + "c";
		assertTrue(ident.length() == 3);

		assertTrue(FormulaFactory.checkSymbol(ident));

		final ParseResult result = new ParseResult(ff, null);
		final AbstractGrammar grammar = ff.getGrammar();
		final Scanner scanner = new Scanner(ident, result, grammar);
		final Token t = scanner.Scan();
		
		assertFalse(result.hasProblem());
		
		final Token next = scanner.Scan();
		assertEquals(B_MATH_V2.getKind(EOF), next.kind);
		
		assertEquals(ident, t.val);
	}

	@Test 
	public void testIsValidIdentifierName() throws Exception {
		assertTrue(ff.isValidIdentifierName("foo"));
		assertTrue(ff.isValidIdentifierName("foo'"));
		assertFalse(ff.isValidIdentifierName("foo''"));
		assertFalse(ff.isValidIdentifierName("foo bar"));
		assertFalse(ff.isValidIdentifierName(" foo"));
		assertFalse(ff.isValidIdentifierName("foo "));
		assertFalse(ff.isValidIdentifierName("foo	bar"));
		assertFalse(ff.isValidIdentifierName("foo'bar"));
		assertFalse(ff.isValidIdentifierName("'"));
		assertFalse(ff.isValidIdentifierName("'foo"));
		assertFalse(ff.isValidIdentifierName("prj1"));
		assertFalse(ff.isValidIdentifierName(""));
		assertFalse(ff.isValidIdentifierName("    "));
		assertFalse(ff.isValidIdentifierName("$P"));
		assertFalse(ff.isValidIdentifierName("l$"));
		assertFalse(ff.isValidIdentifierName("$"));
		assertFalse(ff.isValidIdentifierName("$$P"));
		assertFalse(ff.isValidIdentifierName("prj1'"));
		assertFalse(ff.isValidIdentifierName("prj'p"));
		assertFalse(ff.isValidIdentifierName("partition'"));
	}

	@Test 
	public void testCheckSymbol() throws Exception {
		assertTrue(FormulaFactory.checkSymbol("ident_like€SYMBOL"));
		assertTrue(FormulaFactory.checkSymbol("\u2b50"));
		assertTrue(FormulaFactory.checkSymbol("\u2b50\u2b11"));
		assertFalse(FormulaFactory.checkSymbol(""));
		assertFalse(FormulaFactory.checkSymbol("idWith\u2b50Symbol"));
		assertFalse(FormulaFactory.checkSymbol("\u2b50WithId"));
		assertFalse(FormulaFactory.checkSymbol("idWithPrime'"));
		assertFalse(FormulaFactory.checkSymbol("$metaIdent"));
		assertFalse(FormulaFactory.checkSymbol("spaced ident"));
	}

	@Test
	public void testExtensions() throws Exception {
		// final String prefix = "oo"; // no bug
		final String prefix = "o"; // bug
		// final String prefix = "S"; // no bug
		// final String prefix = "Se"; // no bug
		// final String prefix = "Seq"; // bug

		// no bug if we do: new DT(prefix + "0")
		final List<GivenType> emptyArgs = new ArrayList<GivenType>();
		final IDatatypeBuilder dtSeqBuild = ff.makeDatatypeBuilder(prefix,
				emptyArgs);
		final IDatatypeBuilder dtSeq1Build = ff.makeDatatypeBuilder(prefix
				+ "1", emptyArgs);
		final IDatatypeBuilder dtSeq2Build = ff.makeDatatypeBuilder(prefix
				+ "2", emptyArgs);
		dtSeqBuild.addConstructor("new" + prefix);
		dtSeq1Build.addConstructor("new" + prefix + "1");
		dtSeq2Build.addConstructor("new" + prefix + "2");

		final IDatatype dtSeq = dtSeqBuild.finalizeDatatype();
		final IDatatype dtSeq1 = dtSeq1Build.finalizeDatatype();
		final IDatatype dtSeq2 = dtSeq2Build.finalizeDatatype();

		final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		extensions.addAll(dtSeq.getExtensions());
		extensions.addAll(dtSeq1.getExtensions());
		extensions.addAll(dtSeq2.getExtensions());

		final FormulaFactory ffSeqs = ff.withExtensions(extensions);
		final ParseResult result = new ParseResult(ffSeqs, null);

		// an AssertionError is thrown when the bug is present
		new Scanner("", result, ffSeqs.getGrammar());
	}

}
