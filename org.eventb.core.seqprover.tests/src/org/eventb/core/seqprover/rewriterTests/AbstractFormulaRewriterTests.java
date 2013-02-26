/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - allowing subclasses to provide a type environment
 *     Systerel - mathematical language V2
 *     Systerel - full refactoring of this class
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.seqprover.tests.TestLib.assertNoProblem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * Abstract base class for testing formula rewriters.
 * <p>
 * Main methods are :
 * <ul>
 * <li>{@link #rewriteExpr(String, String, String...)}</li>
 * <li>{@link #rewritePred(String, String, String...)}</li>
 * <li>{@link #noRewriteExpr(String, String...)}</li>
 * <li>{@link #noRewritePred(String, String...)}</li>
 * </ul>
 * </p>
 * Internally these methods are implemented using a hierarchy of helper classes
 * in order to eradicate code duplication.
 * 
 * @author Thai Son Hoang
 * @author Laurent Voisin
 */
public abstract class AbstractFormulaRewriterTests {

	private static abstract class FormulaTest<T extends Formula<T>> {

		private static void assertTypeChecked(Formula<?> formula) {
			assertTrue("Formula " + formula + " should be type checked.",
					formula.isTypeChecked());
		}

		private final IFormulaRewriter rewriter;
		protected final FormulaFactory factory;
		protected final ITypeEnvironmentBuilder typenv;

		public FormulaTest(FormulaFactory ff, IFormulaRewriter rewriter,
				String... env) {
			this.rewriter = rewriter;
			this.factory = ff;
			this.typenv = makeTypeEnvironment(env);
		}

		protected abstract void checkCompatibility(T input, T expected);

		private ITypeEnvironmentBuilder makeTypeEnvironment(String... env) {
			assertTrue("invalid environment specification", env.length % 2 == 0);
			final ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
			for (int i = 0; i < env.length; i += 2) {
				final String name = env[i];
				final String typeString = env[i + 1];
				final IParseResult res = factory.parseType(typeString);
				assertNoProblem(res, typeString, "is not a type");
				typenv.addName(name, res.getParsedType());
			}
			return typenv;
		}

		private T parse(String image) {
			T formula = parseString(image);
			assertTypeChecked(formula);
			return formula;
		}

		protected abstract T parseString(String image);

		public final void run(String inputImage, String expectedImage) {
			final T input = parse(inputImage);
			final T actual = input.rewrite(rewriter);
			if (expectedImage == null) {
				assertSame("Rewriter should not have changed the formula",
						input, actual);
				return;
			}

			final int typenvSize = typenvSize();
			final T expected = parse(expectedImage);
			assertEquals(
					"Expected formula contributed to the type environment",
					typenvSize, typenvSize());
			checkCompatibility(input, expected);
			if (expected.equals(input)) {
				fail("Expected is the same as input " + input);
			}
			assertEquals(expected, actual);
		}

		private void typeCheck(Formula<?> formula, String image) {
			final ITypeCheckResult tcResult = formula.typeCheck(typenv);
			assertNoProblem(tcResult, image, "does not typecheck");
			typenv.addAll(tcResult.getInferredEnvironment());
		}

		private int typenvSize() {
			return typenv.getNames().size();
		}

	}

	private static class ExpressionTest extends FormulaTest<Expression> {

		public ExpressionTest(FormulaFactory ff, IFormulaRewriter rewriter,
				String... env) {
			super(ff, rewriter, env);
		}

		@Override
		protected void checkCompatibility(Expression input, Expression expected) {
			assertEquals("Expression " + input + " and expression " + expected
					+ " should bear the same type.", input.getType(),
					expected.getType());
		}

		@Override
		protected Expression parseString(String image) {
			return TestLib.genExpr(typenv, image);
		}

	}

	private static class PredicateTest extends FormulaTest<Predicate> {

		public PredicateTest(FormulaFactory ff, IFormulaRewriter rewriter,
				String... env) {
			super(ff, rewriter, env);
		}

		@Override
		protected void checkCompatibility(Predicate input, Predicate expected) {
			// Nothing to do for predicates
		}

		@Override
		protected Predicate parseString(String image) {
			return TestLib.genPred(typenv, image);
		}

	}

	/**
	 * The formula factory used to create formulas.
	 */
	private final FormulaFactory ff;

	/**
	 * The rewriter under test.
	 */
	private final IFormulaRewriter rewriter;

	/**
	 * Client extending this class must provide the rewriter which is tested.
	 * 
	 * @param rewriter
	 *            the rewriter under test
	 */
	protected AbstractFormulaRewriterTests(FormulaFactory ff, IFormulaRewriter rewriter) {
		this.rewriter = rewriter;
		this.ff = ff;
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
		new PredicateTest(ff, rewriter, env).run(inputImage, expectedImage);
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
	 * 
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void noRewritePred(String inputImage, String... env) {
		new PredicateTest(ff, rewriter, env).run(inputImage, null);
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is an
	 * integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void rewriteExpr(String inputImage, String expectedImage,
			String... env) {
		new ExpressionTest(ff, rewriter, env).run(inputImage, expectedImage);
	}

	/**
	 * Ensures that the rewriter does not change the given expression.
	 * <p>
	 * The type environment is described by a list of strings which must contain
	 * an even number of elements. It contains alternatively names and types to
	 * assign to them in the environment. For instance, to describe a type
	 * environment where <code>S</code> is a given set and <code>x</code> is an
	 * integer, one would pass the strings <code>"S", "ℙ(S)", "x", "ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param env
	 *            a list of strings describing the type environment to use for
	 *            type-checking
	 */
	protected void noRewriteExpr(String inputImage, String... env) {
		new ExpressionTest(ff, rewriter, env).run(inputImage, null);
	}

}
