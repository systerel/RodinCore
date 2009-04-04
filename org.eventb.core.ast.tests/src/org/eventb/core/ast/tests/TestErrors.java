/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.LanguageVersion.LATEST;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;

/**
 * Unit test of error messages.
 * 
 * @author franz
 */
public class TestErrors extends AbstractTests {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	

	private Object lexTestPairs[] = new Object[]{
			"\ueeee\u22a5",
			new ASTProblem(new SourceLocation(0,0), ProblemKind.LexerError, ProblemSeverities.Warning, "\ueeee"),
			"\u22a5\ueeee",
			new ASTProblem(new SourceLocation(1,1), ProblemKind.LexerError, ProblemSeverities.Warning, "\ueeee"),
			"finite(\u03bb x\u21a6(\ueeeey\u21a6s)\u00b7\u22a5\u2223z)",
			new ASTProblem(new SourceLocation(12,12), ProblemKind.LexerError, ProblemSeverities.Warning, "\ueeee"),
			// From bug #2689872 Illegal character not reported
			"0/=1",
			new ASTProblem(new SourceLocation(1,1), ProblemKind.LexerError, ProblemSeverities.Warning, "/"),
			
	};
	
	private Object parseTestPairs[] = new Object[]{
			"finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
			new ASTProblem(new SourceLocation(20,20), ProblemKind.SyntaxError, ProblemSeverities.Error, "RPAR expected"),
			"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
			new ASTProblem(new SourceLocation(0,1), ProblemKind.SyntaxError, ProblemSeverities.Error, "invalid SimpleExpr"),
			"finite(\u03bb x\u21a6y\u21a6s)\u00b7\u22a5\u2223z)",
			new ASTProblem(new SourceLocation(14,15), ProblemKind.SyntaxError, ProblemSeverities.Error, "QDOT expected"),
			"∀(x)·x∈ℤ",
			new ASTProblem(new SourceLocation(1,1), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
			"∀(x,y)·x∈ℤ ∧ y∈ℤ",
			new ASTProblem(new SourceLocation(1,1), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
			"s ∈ (∅ \u2982 S)",
			new ASTProblem(new SourceLocation(9,9), ProblemKind.InvalidTypeExpression, ProblemSeverities.Error),
// TODO check how it could be extended to quantified expressions
//			"finite(⋃(x)·(x⊆ℤ ∣ x))",
//			new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
//			"finite(⋃(x,y)·(x⊆ℤ ∧ y⊆ℤ ∣ x∩y))",
//			new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
	};
	
	
	/**
	 * Test of lexical errors
	 */
	public void testLexErrors() {
		for (int i = 0; i < lexTestPairs.length; i = i + 2) {
			final String input = (String) lexTestPairs[i];
			final IParseResult result = ff.parsePredicate(input, LATEST, null);
			// Lexer errors are only warnings, so parsing is a success.
			assertTrue("parse should have succeeded", result.isSuccess());
			assertTrue("parse should have a problem", result.hasProblem());
			assertEquals(result.getProblems().size(), 1);
			assertEquals(result.getProblems().get(0), lexTestPairs[i + 1]);
			assertNotNull(result.getParsedPredicate());
		}
	}
	
	/**
	 * Test of syntactic errors
	 */
	public void testParseErrors() {
		for (int i = 0; i < parseTestPairs.length; i = i + 2) {
			final String input = (String) parseTestPairs[i];
			final ASTProblem problem = (ASTProblem) parseTestPairs[i + 1];
			final IParseResult result = ff.parsePredicate(input, LATEST, null);
			assertFailure("parse should have failed for " + input, result);
			assertEquals(1, result.getProblems().size());
			assertNull(result.getParsedPredicate());
			assertEquals(problem, result.getProblems().get(0));
		}
	}
	
	/* TODO: Add well-formedness and type-check errors. */
}
