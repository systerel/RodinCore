/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

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

	private static final String[] NO_ENV = new String[0];
	
	/**
	 * The rewriter under test.
	 */
	protected IFormulaRewriter r;
	
	/**
	 * Constructor.
	 * <p>
	 * Client extends this class should provide the rewriter for testing.
	 * 
	 * @param r
	 *            the rewriter under test
	 */
	protected AbstractFormulaRewriterTests(IFormulaRewriter r) {
		this.r = r;
	}

	/**
	 * Utility method for making a predicate from its string image. This method
	 * will fail if the string image does not correspond to a well-defined and
	 * well-typed predicate. The type environment is enriched as a side-effect
	 * of type-checking the resulting predicate.
	 * 
	 * @param image
	 *            the string image of a predicate
	 * @param typenv
	 *            typing environment to use for type-check (will be enriched)
	 * @return a predicate corresponding to the string image
	 */
	protected Predicate makePredicate(String image, ITypeEnvironment typenv) {
		final Predicate pred = Lib.parsePredicate(image);
		if (pred == null)
			fail("Predicate: \n\t" + image + "\n\tcannot be parsed");
		final ITypeCheckResult typeCheck = pred.typeCheck(typenv);
		if (typeCheck.hasProblem())
			fail("Input predicate: \n\t" + image + "\n\tcannot be type checked");
		typenv.addAll(typeCheck.getInferredEnvironment());
		return pred;
	}

	/**
	 * Test the rewriter for rewriting from an input predicate (represented by
	 * its string image) to an expected predicate (represented by its string
	 * image).
	 * 
	 * @param expectedImage
	 *            the string image of the expected predicate.
	 * @param inputImage
	 *            the string image of the input predicate.
	 */
	protected void predicateTest(String expectedImage, String inputImage) {
		predicateTest(expectedImage, inputImage, NO_ENV);
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
	 * 
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void predicateTest(String expectedImage, String inputImage,
			String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Predicate input = makePredicate(inputImage, typenv);
		final Predicate expected = makePredicate(expectedImage, typenv);
		predicateTest(expected, input);
	}

	/**
	 * Test the rewriter for rewriting from an input predicate to an expected
	 * predicate.
	 * 
	 * @param expected
	 *            the expected predicate.
	 * @param input
	 *            the input predicate.
	 */
	protected void predicateTest(Predicate expected, Predicate input) {
		assertTrue("Input expression should be type checked ",
				input.isTypeChecked());
		assertTrue("Expected expression should be type checked ",
				expected.isTypeChecked());
		assertEquals(input + " --> " + expected, expected, input.rewrite(r));
	}

	/**
	 * Utility method for making an expression from its string image. This
	 * method will make the test failed if the string image does not correspond
	 * to a well-defined and well-typed expression.
	 * 
	 * @param image
	 *            the string image of an expression.
	 * @param typenv
	 *            the type environment to use for type-checking the expression
	 * @return an expression corresponding to the string image.
	 */
	protected Expression makeExpression(String image, ITypeEnvironment typenv) {
		Expression input = Lib.parseExpression(image);
		if (input == null)
			Assert.isTrue(false, "Expression: \n\t" + image
					+ "\n\tcannot be parsed");
		ITypeCheckResult typeCheck = input.typeCheck(typenv);
		if (!typeCheck.isSuccess())
			Assert.isTrue(false, "Expression: \n\t" + image
					+ "\n\tcannot be type checked");
		return input;
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * 
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param inputImage
	 *            the string image of the input expression.
	 */
	protected void expressionTest(String expectedImage, String inputImage) {
		expressionTest(expectedImage, inputImage, NO_ENV);
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
	 * 
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void expressionTest(String expectedImage,
			String inputImage, String... env) {
		final ITypeEnvironment typenv = makeTypeEnvironment(env);
		final Expression input = makeExpression(inputImage, typenv);
		final Expression expected = makeExpression(expectedImage, typenv);
		expressionTest(expected, input);
	}

	/**
	 * Test the rewriter for rewriting from an input expression to an expected
	 * expression.
	 * 
	 * @param expected
	 *            the expected expression.
	 * @param input
	 *            the input expression.
	 */
	protected void expressionTest(Expression expected,
			Expression input) {
		Assert.isTrue(input.isTypeChecked(),
				"Input expression " + input + " should be type checked");
		Assert.isTrue(expected.isTypeChecked(),
				"Expected expression " + expected + " should be type checked");
		assertEquals("Expected expression: " + expected
				+ " and input expression: " + input
				+ " should be of the same type ", expected.getType(), input
				.getType());
		assertEquals(input + " --> " + expected, expected, input.rewrite(r));
	}

	private ITypeEnvironment makeTypeEnvironment(String... env) {
		assertTrue(env.length % 2 == 0);
		final ITypeEnvironment typenv = ff.makeTypeEnvironment();
		for (int i = 0; i < env.length; i+=2) {
			final String name = env[i];
			final String typeString = env[i+1];
			final IParseResult res = ff.parseType(typeString, V2);
			assertFalse(res.hasProblem());
			typenv.addName(name, res.getParsedType());
		}
		return typenv;
	}

}
