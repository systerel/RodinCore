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

import static org.eventb.core.ast.ProblemKind.InvalidGenericType;
import static org.eventb.core.ast.ProblemKind.LexerError;
import static org.eventb.core.ast.ProblemKind.PredicateVariableNotAllowed;
import static org.eventb.core.ast.ProblemKind.UnexpectedSubFormulaKind;
import static org.eventb.core.ast.ProblemKind.UnexpectedSymbol;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.core.ast.ProblemSeverities.Warning;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.SourceLocation;
import org.junit.Test;

/**
 * Unit test of error messages.
 * 
 * @author franz
 */
public class TestErrors extends AbstractTests {
	
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
			return ff.parsePredicate(input, null);
		}
	}
	
	private static class PredPatternTestItem extends TestItem {

		public PredPatternTestItem(String input, ASTProblem problem) {
			super(input, problem);
		}

		@Override
		public IParseResult parse() {
			return ff.parsePredicatePattern(input, null);
		}
	}
	
	/**
	 * Test of lexical errors
	 */
	@Test 
	public void testLexErrors() {
				doLexTest(new PredTestItem(
						"\ueeee\u22a5",
						new ASTProblem(new SourceLocation(0, 0), LexerError,
								Warning, "\ueeee")));
				doLexTest(new PredTestItem(
						"\u22a5\ueeee",
						new ASTProblem(new SourceLocation(1, 1), LexerError,
								Warning, "\ueeee")));
				doLexTest(new PredTestItem(
						"finite(\u03bb x\u21a6(\ueeeey\u21a6s)\u00b7\u22a5\u2223z)",
						new ASTProblem(new SourceLocation(12, 12), LexerError,
								Warning, "\ueeee")));
				doLexTest(new PredTestItem(
				// From bug #2689872 Illegal character not reported
						"0/=1",
						new ASTProblem(new SourceLocation(1, 1), LexerError,
								Warning, "/")));
				doLexTest(new PredPatternTestItem(
						"$P'",
						new ASTProblem(new SourceLocation(2, 2), LexerError,
								Warning, "'")));
	}

	private void doLexTest(TestItem testItem) {
		final IParseResult result = testItem.parse();
		assertLexProblem(result, testItem.problem);
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
	@Test 
	public void testParseErrors() {
				doParseTest(new PredTestItem(
						"finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
						new ASTProblem(new SourceLocation(19, 19), UnexpectedSymbol,
								Error, ")", "End of Formula")));
				doParseTest(new PredTestItem(
						"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z",
						new ASTProblem(new SourceLocation(0, 12), UnexpectedSubFormulaKind,
								Error, "a predicate", "an expression")));
				doParseTest(new PredTestItem(
						"finite(\u03bb x\u21a6y\u21a6s)\u00b7\u22a5\u2223z)",
						new ASTProblem(new SourceLocation(14, 14), UnexpectedSymbol,
								Error, "·", ")")));
				doParseTest(new PredTestItem(
						"∀(x)·x∈ℤ",
						new ASTProblem(new SourceLocation(1, 1), UnexpectedSymbol,
								Error, "an identifier", "(")));
				doParseTest(new PredTestItem(
						"∀(x,y)·x∈ℤ ∧ y∈ℤ",
						new ASTProblem(new SourceLocation(1, 1), UnexpectedSymbol,
								Error, "an identifier", "(")));
				doParseTest(new PredTestItem(
						"s ∈ (∅ \u2982 S)",
						new ASTProblem(new SourceLocation(5, 9), InvalidGenericType,
								Error, "ℙ(alpha)")));
				doParseTest(new PredPatternTestItem(
						"x∈$P",
						new ASTProblem(new SourceLocation(2, 3), UnexpectedSubFormulaKind,
								Error, "an expression", "a predicate")));
				doParseTest(new PredTestItem(
						"$P",
						new ASTProblem(new SourceLocation(0, 1), PredicateVariableNotAllowed,
								Error, "$P")));
				// FIXME test other errors
	// TODO check how it could be extended to quantified expressions
//								"finite(⋃(x)·(x⊆ℤ ∣ x))",
//								new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
//								"finite(⋃(x,y)·(x⊆ℤ ∧ y⊆ℤ ∣ x∩y))",
//								new ASTProblem(new SourceLocation(5,5), ProblemKind.UnexpectedLPARInDeclList, ProblemSeverities.Error),
		
	}

	private void doParseTest(TestItem testItem) {
		final IParseResult result = testItem.parse();
		assertParseProblem(testItem, result);
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
