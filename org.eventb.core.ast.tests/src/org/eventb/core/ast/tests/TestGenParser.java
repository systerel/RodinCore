/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

/**
 * This test class aims at supporting generic parser development. It is not part
 * of the AST project test and is intended to be removed when the development is
 * complete.
 * 
 * @author Nicolas Beauger
 * FIXME remove this class (DO NOT COMMIT TO TRUNK !)
 */
public class TestGenParser extends AbstractTests {

	private static final SourceLocationChecker slChecker = new SourceLocationChecker();

	private void doExpressionTest(String formula, Formula<?> expected) {
		final IParseResult result = ff.parseExpression(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Expression actual = result.getParsedExpression();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		actual.accept(slChecker);
	}
	
	private void doPredicateTest(String formula, Predicate expected) {
		final IParseResult result = ff.parsePredicate(formula,
				LanguageVersion.V2, null);
		if (result.hasProblem()) {
			System.out.println(result.getProblems());
		}
		assertFalse(result.hasProblem());
		final Predicate actual = result.getParsedPredicate();
		System.out.println(actual);
		assertEquals(expected, actual);
	
		actual.accept(slChecker);
	}

	public void testPlus() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(PLUS,
				Arrays.<Expression> asList(ff.makeIntegerLiteral(BigInteger
						.valueOf(2), null), ff.makeIntegerLiteral(BigInteger
						.valueOf(3), null)), null);
		doExpressionTest("2+3", expected);
	}

	public void testPlusAsso() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(PLUS, Arrays
						.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(2), null),
								ff.makeIntegerLiteral(BigInteger.valueOf(3), null)), null);
		doExpressionTest("1+2+3", expected);
	}

	public void testPlusMult() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(PLUS, Arrays
						.<Expression> asList(
								ff.makeIntegerLiteral(BigInteger.valueOf(1), null),
								ff.makeAssociativeExpression(MUL, Arrays.<Expression> asList(ff
										.makeIntegerLiteral(BigInteger
												.valueOf(2), null), ff
										.makeIntegerLiteral(BigInteger
												.valueOf(3), null)), null)
						), null);
		doExpressionTest("1+2∗3", expected);
	}
	
	public void testUnion() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BUNION,
				Arrays.<Expression> asList(ff.makeFreeIdentifier("A", null)
						, ff.makeFreeIdentifier("B", null)), null);
		doExpressionTest("A∪B", expected);
	}

	public void testInter() throws Exception {
		final Expression expected = ff.makeAssociativeExpression(BINTER,
				Arrays.<Expression> asList(ff.makeFreeIdentifier("A", null)
						, ff.makeFreeIdentifier("B", null)), null);
		doExpressionTest("A∩B", expected);
	}

	public void testUnionInter() throws Exception {
		final Expression expected = ff
				.makeAssociativeExpression(BINTER, Arrays.<Expression> asList(
						ff.makeFreeIdentifier("A", null),
						ff.makeAssociativeExpression(BUNION, Arrays.<Expression> asList(
								ff.makeFreeIdentifier("B", null),
								ff.makeFreeIdentifier("C", null)),
										null)), null);
		doExpressionTest("A∩(B∪C)", expected);
	}
	
	public void testUnionInterNoParen() throws Exception {
		final IParseResult result = ff.parseExpression("A∩B∪C",
				LanguageVersion.V2, null);
		assertTrue(result.hasProblem());
		final List<ASTProblem> problems = result.getProblems();
		System.out.println(problems);
	}

	public void testAnd() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(Formula.LAND,
				Arrays.<Predicate> asList(ff.makeLiteralPredicate(
						Formula.BTRUE, null), ff.makeLiteralPredicate(
						Formula.BFALSE, null)), null);
		doPredicateTest("⊤∧⊥", expected);
	}
	
	public void testOrAnd() throws Exception {
		final Predicate expected = ff.makeAssociativePredicate(LOR,
				Arrays.<Predicate> asList(ff.makeAssociativePredicate(
						LAND, Arrays.<Predicate> asList(ff
								.makeLiteralPredicate(BTRUE, null), ff
								.makeLiteralPredicate(BFALSE, null)),
						null), ff.makeLiteralPredicate(BFALSE, null)),
				null);
		doPredicateTest("(⊤∧⊥)∨⊥", expected);
	}	
	
	public void testSourceLocation() throws Exception {
		final IParseResult result = ff.parsePredicate("(⊤∧⊥)∨⊥",
				LanguageVersion.V2, null);
		final Predicate pred = result.getParsedPredicate();
		assertNotNull(pred.getSourceLocation());
		final Predicate childFalse = ((AssociativePredicate) pred)
				.getChildren()[1];
		assertEquals(new SourceLocation(6, 6), childFalse.getSourceLocation());
	}
}
