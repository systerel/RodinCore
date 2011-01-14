/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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

import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.LanguageVersion.V1;
import static org.eventb.internal.core.parser.AbstractGrammar._EOF;
import static org.eventb.internal.core.parser.BMathV2.B_MATH_V2;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.ParseResult;

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

	private static final String[] invalidStrings = new String[] { "-", "/", };

	private static int getExpectedKind(int kind, LanguageVersion version) {
		if (kind == B_MATH_V2.getPARTITION() && version == V1)
			return B_MATH_V2.getIDENT();
		return kind;
	}

	/**
	 * Tests all the tokens that are needed to construct an event-B formula.
	 */
	public void testToken() {

		for (LanguageVersion version : LanguageVersion.values()) {
			testAllTokens(version);
		}
	}

	// Check each token string through the lexical analyzer.
	private void testAllTokens(LanguageVersion version) {
		for (Entry<String, Integer> token : B_MATH_V2.getTokens().entrySet()) {
			final String image = token.getKey();
			final Integer kind = token.getValue();
			testToken(image, kind, version);
		}
		testToken("", B_MATH_V2.getEOF(), version);
		testToken("x", B_MATH_V2.getIDENT(), version);
		testToken("_toto", B_MATH_V2.getIDENT(), version);
		testToken("x'", B_MATH_V2.getIDENT(), version);
		testToken("2", B_MATH_V2.getINTLIT(), version);
		testToken("3000000000", B_MATH_V2.getINTLIT(), version);
		testToken("50000000000000000000", B_MATH_V2.getINTLIT(), version);
		testToken("001", B_MATH_V2.getINTLIT(), version);
		testToken("$P", B_MATH_V2.getPREDVAR(), version);
		testToken("$_toto", B_MATH_V2.getPREDVAR(), version);
		testToken("p'", B_MATH_V2.getIDENT(), version);
		testToken("prj'", B_MATH_V2.getIDENT(), version);
	}

	private void testToken(String image, Integer kind, LanguageVersion version) {
		ParseResult result = new ParseResult(ff, version, null);
		Scanner scanner = new Scanner(image, result, B_MATH_V2);
		Token t = scanner.Scan();
		assertEquals(image, t.val);
		final String msg = "for \"" + image + "\" with language " + version;
		assertEquals(msg, getExpectedKind(kind, version), t.kind);
	}

	/**
	 * Ensure that invalid tokens get rejected.
	 */
	public void testInvalidStrings() {
		for (LanguageVersion version : LanguageVersion.values()) {
			testInvalidStrings(version);
		}
	}

	@SuppressWarnings("deprecation")
	private void testInvalidStrings(LanguageVersion version) {
		for (String string : invalidStrings) {
			final IParseResult result = new ParseResult(ff, version, null);
			Scanner scanner = new Scanner(string, (ParseResult) result,
					B_MATH_V2);
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

		final ParseResult result = new ParseResult(ff, LATEST, null);
		final Scanner scanner = new Scanner(ident, result, B_MATH_V2);
		final Token t = scanner.Scan();
		
		assertFalse(result.hasProblem());
		
		final Token next = scanner.Scan();
		assertEquals(_EOF, next.kind);
		
		assertEquals(ident, t.val);
	}

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

	private static class DT implements IDatatypeExtension {

		private final String name;

		public DT(String name) {
			this.name = name;
		}

		@Override
		public String getTypeName() {
			return name;
		}

		@Override
		public String getId() {
			return "id" + name;
		}

		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			// none
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			// none
		}

	}

	public void testExtensions() throws Exception {
//		final String prefix = "oo"; // no bug
		final String prefix = "o"; // bug
//		final String prefix = "S"; // no bug
//		final String prefix = "Se"; // no bug
//		final String prefix = "Seq"; // bug

		// no bug if we do: new DT(prefix + "0")
		final IDatatype dtSeq = ff.makeDatatype(new DT(prefix));
		final IDatatype dtSeq1 = ff.makeDatatype(new DT(prefix + "1"));
		final IDatatype dtSeq2 = ff.makeDatatype(new DT(prefix + "2"));

		final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();
		extensions.addAll(dtSeq.getExtensions());
		extensions.addAll(dtSeq1.getExtensions());
		extensions.addAll(dtSeq2.getExtensions());

		final FormulaFactory ffSeqs = ff.withExtensions(extensions);
		final ParseResult result = new ParseResult(ffSeqs, LATEST, null);

		// an AssertionError is thrown when the bug is present
		new Scanner("", result, ffSeqs.getGrammar());
	}

}