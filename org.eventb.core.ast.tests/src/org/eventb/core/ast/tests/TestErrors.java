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
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.ProblemKind.InvalidTypeExpression;
import static org.eventb.core.ast.ProblemKind.LexerError;
import static org.eventb.core.ast.ProblemKind.PredicateVariableNotAllowed;
import static org.eventb.core.ast.ProblemKind.SyntaxError;
import static org.eventb.core.ast.ProblemKind.UnexpectedLPARInDeclList;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.core.ast.ProblemSeverities.Warning;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.IParseResult;
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
	
	private static abstract class TestItem {
		protected final String input;
		protected final ASTProblem problem;
		
		public TestItem(String input, ASTProblem problem) {
			this.input = input;
			this.problem = problem;
		}

		public abstract IParseResult parse();
	}
	
	private static class PredTestItem extends TestItem {

		public PredTestItem(String input, ASTProblem problem) {
			super(input, problem);
		}

		@Override
		public IParseResult parse() {
			return ff.parsePredicate(input, LATEST, null);
		}
	}
	
	private static class PredPatternTestItem extends TestItem {

		public PredPatternTestItem(String input, ASTProblem problem) {
			super(input, problem);
		}

		@Override
		public IParseResult parse() {
			return ff.parsePredicatePattern(input, LATEST, null);
		}
	}
	
	private final TestItem[] lexTestItems = new TestItem[] {
			new PredTestItem(
					"\ueeee\u22a5",
					new ASTProblem(new SourceLocation(0, 0), LexerError,
							Warning, "\ueeee")),
			new PredTestItem(
					"\u22a5\ueeee",
					new ASTProblem(new SourceLocation(1, 1), LexerError,
							Warning, "\ueeee")),
			new PredTestItem(
					"finite(\u03bb x\u21a6(\ueeeey\u21a6s)\u00b7\u22a5\u2223z)",
					new ASTProblem(new SourceLocation(12, 12), LexerError,
							Warning, "\ueeee")),
			new PredTestItem(
			// From bug #2689872 Illegal character not reported
					"0/=1",
					new ASTProblem(new SourceLocation(1, 1), LexerError,
							Warning, "/")),
			new PredPatternTestItem(
					"$P'",
					new ASTProblem(new SourceLocation(2, 2), LexerError,
							Warning, "'")),
	};

	private final TestItem[] parseTestItems = new TestItem[] {
			new PredTestItem(
					"finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
					new ASTProblem(new SourceLocation(20, 20), SyntaxError,
							Error, "RPAR expected")),
			new PredTestItem(
					"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
					new ASTProblem(new SourceLocation(0, 1), SyntaxError,
							Error, "invalid SimpleExpr")),
			new PredTestItem(
					"finite(\u03bb x\u21a6y\u21a6s)\u00b7\u22a5\u2223z)",
					new ASTProblem(new SourceLocation(14, 15), SyntaxError,
							Error, "QDOT expected")),
			new PredTestItem(
					"∀(x)·x∈ℤ",
					new ASTProblem(new SourceLocation(1, 1), UnexpectedLPARInDeclList,
							Error)),
			new PredTestItem(
					"∀(x,y)·x∈ℤ ∧ y∈ℤ",
					new ASTProblem(new SourceLocation(1, 1), UnexpectedLPARInDeclList,
							Error)),
			new PredTestItem(
					"s ∈ (∅ \u2982 S)",
					new ASTProblem(new SourceLocation(9, 9), InvalidTypeExpression,
							Error)),
			new PredTestItem(
					"x∈$P",
					new ASTProblem(new SourceLocation(2, 4), SyntaxError,
							Error, "invalid SimpleExpr")),
			new PredTestItem(
					"$P",
					new ASTProblem(new SourceLocation(0, 1), PredicateVariableNotAllowed,
							Error, "$P")),
// TODO check how it could be extended to quantified expressions
//							"finite(⋃(x)·(x⊆ℤ ∣ x))",
//							new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
//							"finite(⋃(x,y)·(x⊆ℤ ∧ y⊆ℤ ∣ x∩y))",
//							new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
	};
	
	
	/**
	 * Test of lexical errors
	 */
	public void testLexErrors() {
		for (TestItem testItem : lexTestItems) {
			final IParseResult result = testItem.parse();
			assertLexProblem(result, testItem.problem);
		}
	}

	@SuppressWarnings("deprecation")
	private void assertLexProblem(IParseResult result,
			ASTProblem expectedProblem) {
		assertTrue("parse should have succeeded", result.isSuccess());
		assertTrue("parse should have a problem", result.hasProblem());
		assertEquals(result.getProblems().size(), 1);
		assertEquals(result.getProblems().get(0), expectedProblem);
		assertNotNull(result.getParsedPredicate());
	}
	
	/**
	 * Test of syntactic errors
	 */
	public void testParseErrors() {
		for (TestItem testItem : parseTestItems) {
			final IParseResult result = testItem.parse();
			assertParseProblem(testItem, result);
		}
	}

	private void assertParseProblem(TestItem testItem, IParseResult result) {
		assertFailure("parse should have failed for " + testItem.input,
				result);
		assertEquals(1, result.getProblems().size());
		assertEquals(testItem.problem, result.getProblems().get(0));
		assertNull(result.getParsedPredicate());
	}
	
	/* TODO: Add well-formedness and type-check errors. */
}
