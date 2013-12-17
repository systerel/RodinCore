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

import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
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
		protected final ITypeEnvironmentBuilder typenv;

		public FormulaTest(FormulaFactory ff, IFormulaRewriter rewriter,
				String typenvImage) {
			this.rewriter = rewriter;
			this.typenv = mTypeEnvironment(typenvImage, ff);
		}

		protected abstract void checkCompatibility(T input, T expected);

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

		private int typenvSize() {
			return typenv.getNames().size();
		}

	}

	private static class ExpressionTest extends FormulaTest<Expression> {

		public ExpressionTest(FormulaFactory ff, IFormulaRewriter rewriter,
				String typenvImage) {
			super(ff, rewriter, typenvImage);
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
				String typenvImage) {
			super(ff, rewriter, typenvImage);
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
	protected final FormulaFactory ff;

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
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param typenvImage
	 *            the string image of the type environment
	 */
	protected void rewritePred(String inputImage, String expectedImage,
			String typenvImage) {
		new PredicateTest(ff, rewriter, typenvImage).run(inputImage,
				expectedImage);
	}

	/**
	 * Tests the rewriter for rewriting conditionally from an input predicate
	 * (represented by its string image) to an expected predicate (represented
	 * by its string image).
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 *
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 * @param typenvImage
	 *            the string image of the type environment
	 * @param rewrite
	 *            tells whether the predicate shall be rewritten
	 */
	protected void rewritePred(String inputImage, String expectedImage,
			String typenvImage, boolean rewrite) {
		if (rewrite) {
			rewritePred(inputImage, expectedImage, typenvImage);
		} else {
			noRewritePred(inputImage, typenvImage);
		}
	}

	/**
	 * Test the rewriter for rewriting from an input predicate (represented by
	 * its string image) to an expected predicate (represented by its string
	 * image).
	 * <p>
	 * Both predicates must type-check by themselves without a given type
	 * environment.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the expected predicate
	 */
	protected void rewritePred(String inputImage, String expectedImage) {
		new PredicateTest(ff, rewriter, "").run(inputImage, expectedImage);
	}
	
	/**
	 * Tests the rewriter for rewriting conditionally from an empty set input
	 * predicate to an expected predicate (represented by its string image). The
	 * input empty set predicate is composed from a given input expression
	 * (represented by its string image).
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 *
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param expectedImage
	 *            the string image of the output predicate
	 * @param typenvImage
	 *            the string image of the type environment
	 * @param rewrite
	 *            tells whether the predicate shall be rewritten
	 */
	protected void rewritePredEmptySet(String inputImage, String expectedImage,
			String typenvImage, boolean rewrite) {
		rewritePred(inputImage + " = ∅", expectedImage, typenvImage, rewrite);
		rewritePred(inputImage + " ⊆ ∅", expectedImage, typenvImage, rewrite);
		rewritePred("∅ = " + inputImage, expectedImage, typenvImage, rewrite);
		// Not applicable (wrong right-hand side)
		noRewritePred(inputImage + " = ZZZ", typenvImage);
	}

	/**
	 * Ensures that the rewriter does not change the given predicate.
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param typenvImage
	 *            the string image of the type environment
	 */
	protected void noRewritePred(String inputImage, String typenvImage) {
		new PredicateTest(ff, rewriter, typenvImage).run(inputImage, null);
	}

	/**
	 * Ensures that the rewriter does not change the given predicate.
	 * <p>
	 * The predicate must type-check by itself without a given type environment.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input predicate
	 */
	protected void noRewritePred(String inputImage) {
		new PredicateTest(ff, rewriter, "").run(inputImage, null);
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param typenvImage
	 *            the string image of the type environment
	 */
	protected void rewriteExpr(String inputImage, String expectedImage,
			String typenvImage) {
		new ExpressionTest(ff, rewriter, typenvImage).run(inputImage,
				expectedImage);
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 *
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param expectedImage
	 *            the string image of the expected expression.
	 * @param typenvImage
	 *            the string image of the type environment
	 * @param rewrite
	 *            tells whether the predicate shall be rewritten
	 */
	protected void rewriteExpr(String inputImage, String expectedImage,
			String typenvImage, boolean rewrite) {
		if (rewrite) {
			rewriteExpr(inputImage, expectedImage, typenvImage);
		} else {
			noRewriteExpr(inputImage, typenvImage);
		}
	}

	/**
	 * Test the rewriter for rewriting from an input expression (represented by
	 * its string image) to an expected expression (represented by its string
	 * image).
	 * <p>
	 * Both expressions must type-check by themselves without a given type
	 * environment.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param expectedImage
	 *            the string image of the expected expression.
	 */
	protected void rewriteExpr(String inputImage, String expectedImage) {
		rewriteExpr(inputImage, expectedImage, "");
	}

	/**
	 * Ensures that the rewriter does not change the given expression.
	 * <p>
	 * The type environment is described as usual. For instance, to describe a
	 * type environment where <code>S</code> is a given set and <code>x</code>
	 * is an integer, one would pass the strings <code>"S=ℙ(S); x=ℤ"</code>.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 * @param typenvImage
	 *            the string image of the type environment
	 */
	protected void noRewriteExpr(String inputImage, String typenvImage) {
		new ExpressionTest(ff, rewriter, typenvImage).run(inputImage, null);
	}

	/**
	 * Ensures that the rewriter does not change the given expression.
	 * <p>
	 * The expression must type-check by itself without a given type
	 * environment.
	 * </p>
	 * 
	 * @param inputImage
	 *            the string image of the input expression.
	 */
	protected void noRewriteExpr(String inputImage) {
		noRewriteExpr(inputImage, "");
	}

}
