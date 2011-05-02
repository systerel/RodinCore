/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - allowing subclasses to provide a type environment
 *     Systerel - mathematical language V2
 ******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * @author htson
 *         <p>
 *         This is the abstract class for testing formula rewriters. This
 *         provides several utility methods including
 *         {@link #predicateTest(String, String)} and
 *         {@link #expressionTest(String, String)}.
 */
public abstract class AbstractFormulaRewriterTests {

	/**
	 * The formula factory used to create different formulas for testing.
	 */
	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	private static void assertTypeChecked(Formula<?> formula) {
		assertTrue("Formula " + formula + " should be type checked.",
				formula.isTypeChecked());
	}

	private static void assertSameType(Expression left, Expression right) {
		assertEquals("Expression " + left + " and expression " + right
				+ " should bear the same type.", left.getType(),
				right.getType());
	}

	private static void typeCheck(String image, Formula<?> formula,
			ITypeEnvironment typenv) {
		assertNotNull("Formula " + image + " does not parse.", formula);
		final ITypeCheckResult tcResult = formula.typeCheck(typenv);
		if (tcResult.hasProblem()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Formula ");
			sb.append(image);
			sb.append(" produces problems during type-check:\n");
			for (final ASTProblem pb : tcResult.getProblems()) {
				sb.append('\t');
				sb.append(pb);
				sb.append('\n');
			}
			fail(sb.toString());
		}
		typenv.addAll(tcResult.getInferredEnvironment());
	}

	/**
	 * The rewriter under test.
	 */
	private final IFormulaRewriter rewriter;

	/**
	 * The factory to use for parsing
	 */
	private final FormulaFactory factory;
	
	private final DLib lib;
	
	/**
	 * Client extending this class must provide the rewriter which is tested.
	 * 
	 * @param rewriter
	 *            the rewriter under test
	 */
	protected AbstractFormulaRewriterTests(IFormulaRewriter rewriter) {
		this.rewriter = rewriter;
		this.factory = rewriter.getFactory();
		this.lib = mDLib(factory);
	}

	/**
	 * Utility method for making a predicate from its string image. This method
	 * will fail if the string image does not correspond to a well-formed and
	 * well-typed predicate. The type environment is enriched as a side-effect
	 * of type-checking the resulting predicate.
	 * 
	 * @param image
	 *            the string image of a predicate
	 * @param typenv
	 *            typing environment to use for type-check (will be enriched)
	 * @return a type-checked predicate corresponding to the string image
	 */
	protected Predicate makePredicate(String image, ITypeEnvironment typenv) {
		final Predicate pred = lib.parsePredicate(image);
		typeCheck(image, pred, typenv);
		return pred;
	}

	/**
	 * Test the rewriter for rewriting from an input predicate (represented by
	 * its string image) to an expected predicate (represented by its string
	 * image).
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is an
	 * integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void rewritePred(String inputImage, String expectedImage,
			String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Predicate input = makePredicate(inputImage, typenv);
		final Predicate expected = makePredicate(expectedImage, typenv);
		rewritePred(input, expected);
	}

	/**
	 * Ensures that the rewriter does not change the given predicate.
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is an
	 * integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void noRewritePred(String inputImage, String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Predicate input = makePredicate(inputImage, typenv);
		noRewritePred(input);
	}

	private void rewritePred(Predicate input, Predicate expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);
		if (expected.equals(input)) {
			fail("Expected is the same as input " + input);
		}
		final Predicate actual = input.rewrite(rewriter);
		assertEquals(input.toString(), expected, actual);
	}

	private void noRewritePred(Predicate input) {
		assertTypeChecked(input);
		final Predicate actual = input.rewrite(rewriter);
		assertSame(input.toString(), input, actual);
	}

	/**
	 * Utility method for making a predicate from its string image. This method
	 * will fail if the string image does not correspond to a well-formed and
	 * well-typed expression. The type environment is enriched as a side-effect
	 * of type-checking the resulting expression.
	 * 
	 * @param image
	 *            the string image of an expression.
	 * @param typenv
	 *            the type environment to use for type-checking the expression
	 * @return a type-checked expression corresponding to the string image.
	 */
	private Expression makeExpression(String image, ITypeEnvironment typenv) {
		final Expression input = lib.parseExpression(image);
		typeCheck(image, input, typenv);
		return input;
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is
	 * an integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void rewriteExpr(String inputImage,
			String expectedImage, String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Expression input = makeExpression(inputImage, typenv);
		final Expression expected = makeExpression(expectedImage, typenv);
		rewriteExpr(input, expected);
	}

	/**
	 * Ensures that the rewriter does not change the given expression.
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is
	 * an integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void noRewriteExpr(String inputImage, String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Expression input = makeExpression(inputImage, typenv);
		noRewriteExpr(input);
	}

	private void rewriteExpr(Expression input, Expression expected) {
		assertTypeChecked(input);
		assertTypeChecked(expected);
		assertSameType(input, expected);
		if (expected.equals(input)) {
			fail("Expected is the same as input " + input);
		}
		final Expression actual = input.rewrite(rewriter);
		assertEquals(input.toString(), expected, actual);
	}

	private void noRewriteExpr(Expression input) {
		assertTypeChecked(input);
		final Expression actual = input.rewrite(rewriter);
		assertSame(input.toString(), input, actual);
	}

	private ITypeEnvironment makeTypeEnvironment(String... env) {
		assertTrue(env.length % 2 == 0);
		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		for (int i = 0; i < env.length; i+=2) {
			final String name = env[i];
			final String typeString = env[i+1];
			final IParseResult res = factory.parseType(typeString, V2);
			assertFalse(res.hasProblem());
			typenv.addName(name, res.getParsedType());
		}
		return typenv;
	}

}
