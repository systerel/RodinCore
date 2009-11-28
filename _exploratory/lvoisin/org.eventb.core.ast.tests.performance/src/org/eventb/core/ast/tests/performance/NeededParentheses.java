/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.performance;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_ATOMIC_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_MULTIPLE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_RELATIONAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_UNARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_UNARY_PREDICATE;
import static org.eventb.core.ast.Formula.KID;
import static org.eventb.core.ast.Formula.KPRJ1;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.FastFactory.ff;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Ensures that the AST pretty-printer uses as few parentheses as needed.
 * <p>
 * The purpose of these tests is to build as many expressions and predicates as
 * reasonably possible, and to check for each that the pretty-printer does not
 * produce unnecessary parentheses. This is done by taking the string produced
 * by the pretty-printer and trying to re-parse this string after removing a
 * pair of matching parentheses.
 * </p>
 * <p>
 * The tests are organized around so-called processors. A processor is either an
 * accumulator (which builds a list of formulas) or a checker (which runs
 * tests).
 * </p>
 * 
 * @author Laurent Voisin
 */
public class NeededParentheses extends TestCase {

	private static List<Expression> expr0 = makeExpr0();
	private static List<Predicate> pred0 = makePred0();

	// Strings to ignore because they have a special behavior.
	static Set<String> ignored = new HashSet<String>(Arrays.asList( //
			"− 0 " //
			));

	abstract static class Processor<T extends Formula<T>> {
		public abstract void process(T formula);
	}

	static class Accumulator<T extends Formula<T>> extends Processor<T> {

		private final List<T> result = new ArrayList<T>();

		@Override
		public void process(T formula) {
			result.add(formula);
		}

		public List<T> getResult() {
			return result;
		}
	}

	static class CheckProcessor<T extends Formula<T>> extends Processor<T> {

		@Override
		public void process(T formula) {
			if (formula instanceof Expression) {
				new ExprChecker((Expression) formula).run();
			} else if (formula instanceof Predicate) {
				new PredChecker((Predicate) formula).run();
			} else {
				fail("Unknown formula");
			}
		}

	}

	private abstract static class Checker<T extends Formula<T>> {

		private final T expected;
		private final String actualImage;

		public Checker(T expected) {
			assertTrue(expected.isWellFormed());
			this.expected = expected;
			this.actualImage = expected.toString();
		}

		public void run() {
			assertTrue(expected.isWellFormed());
			final T actual = parse(actualImage);
			assertEquals(expected, actual);

			int openIdx = actualImage.indexOf('(', 0);
			while (0 <= openIdx) {
				assertDifferentWithoutMatchingParen(openIdx);
				openIdx = actualImage.indexOf('(', openIdx + 1);
			}
		}

		protected abstract T parse(String image);

		private void assertDifferentWithoutMatchingParen(int openIdx) {
			final int closeIdx = findMatchingParenIdx(openIdx);
			final String input = actualImage.substring(0, openIdx) + " "
					+ actualImage.substring(openIdx + 1, closeIdx) + " "
					+ actualImage.substring(closeIdx + 1, actualImage.length());
			if (ignored.contains(input))
				return;
			// assertFalse(input, expected.equals(parse(input)));
			if (expected.equals(parse(input)))
				System.out.println(input + " is equivalent to " + actualImage);
		}

		private int findMatchingParenIdx(int openIdx) {
			int count = 1;
			for (int i = openIdx + 1; i < actualImage.length(); i++) {
				final char ch = actualImage.charAt(i);
				if (ch == ')') {
					if (--count == 0)
						return i;
				} else if (ch == '(') {
					++count;
				}
			}
			fail("Unbalanced parentheses in " + actualImage);
			return -1;
		}

	}

	private static class ExprChecker extends Checker<Expression> {

		public ExprChecker(Expression original) {
			super(close(original));
		}

		private static Expression close(Expression original) {
			if (original.isWellFormed()) {
				return original;
			}
			return mQuantifiedExpression(mList(mBoundIdentDecl("x")),
					mLiteralPredicate(BTRUE), original);
		}

		@Override
		protected Expression parse(String image) {
			// System.out.println("Parsing " + image);
			final IParseResult pr = ff.parseExpression(image, V2, null);
			if (pr.hasProblem()) {
				return null;
			}
			return pr.getParsedExpression();
		}

	}

	private static class PredChecker extends Checker<Predicate> {

		public PredChecker(Predicate original) {
			super(close(original));
		}

		private static Predicate close(Predicate original) {
			if (original.isWellFormed()) {
				return original;
			}
			return mQuantifiedPredicate(mList(mBoundIdentDecl("x")), original);
		}

		@Override
		protected Predicate parse(String image) {
			// System.out.println("Parsing " + image);
			final IParseResult pr = ff.parsePredicate(image, V2, null);
			if (pr.hasProblem()) {
				return null;
			}
			return pr.getParsedPredicate();
		}

	}

	/**
	 * @return a list of all expressions of level 0 (i.e., not containing any
	 *         sub-expression or sub-predicate)
	 */
	private static List<Expression> makeExpr0() {
		final Accumulator<Expression> processor = new Accumulator<Expression>();
		processor.process(mFreeIdentifier("x"));
		processor.process(mBoundIdentifier(0));
		processor.process(mIntegerLiteral());
		processor.process(mIntegerLiteral(-1));
		processAtomicExpressions(processor);
		return processor.getResult();
	}

	/**
	 * @return a list of all predicates of level 0 (i.e., not containing any
	 *         sub-expression or sub-predicate)
	 */
	private static List<Predicate> makePred0() {
		final Accumulator<Predicate> processor = new Accumulator<Predicate>();
		processor.process(mLiteralPredicate(BTRUE));
		processor.process(mLiteralPredicate(BFALSE));
		return processor.getResult();
	}

	private List<Expression> makeExpr1() {
		final List<Predicate> p0 = pred0.subList(0, 1);
		final List<Expression> e0 = expr0.subList(0, 1);

		final Accumulator<Expression> e1Acc = new Accumulator<Expression>();
		processExpressions(e1Acc, p0, e0);
		return e1Acc.getResult();
	}

	private List<Predicate> makePred1() {
		final List<Predicate> p0 = pred0.subList(0, 1);
		final List<Expression> e0 = expr0.subList(0, 1);

		final Accumulator<Predicate> p1Acc = new Accumulator<Predicate>();
		processPredicates(p1Acc, p0, e0);
		return p1Acc.getResult();
	}

	/**
	 * Processes all compound expressions built from the given sub-predicates
	 * and sub-expressions.
	 */
	private static void processExpressions(Processor<Expression> processor,
			List<Predicate> preds, List<Expression> exprs) {
		processAssociativeExpressions(processor, exprs);
		processBinaryExpressions(processor, exprs);
		processBoolExpressions(processor, preds);
		processQuantifiedExpressions(processor, preds, exprs);
		processSetExtensions(processor, exprs);
		processUnaryExpressions(processor, exprs);
	}

	/**
	 * Processes all compound predicates built from the given sub-predicates and
	 * sub-expressions.
	 */
	private static void processPredicates(Processor<Predicate> processor,
			List<Predicate> preds, List<Expression> exprs) {
		processAssociativePredicates(processor, preds);
		processBinaryPredicates(processor, preds);
		processMultiplePredicates(processor, exprs);
		processQuantifiedPredicates(processor, preds);
		processRelationalPredicates(processor, exprs);
		processSimplePredicates(processor, exprs);
		processUnaryPredicates(processor, preds);
	}

	private static void processAssociativeExpressions(
			Processor<Expression> processor, List<Expression> subExprs) {
		for (int i = 0; i < AssociativeExpression.TAGS_LENGTH; i++) {
			final int tag = FIRST_ASSOCIATIVE_EXPRESSION + i;
			for (final Expression left : subExprs)
				for (final Expression right : subExprs)
					processor.process(mAssociativeExpression(tag, left, right));
			for (final Expression left : subExprs)
				for (final Expression mid : subExprs)
					for (final Expression right : subExprs)
						processor.process(mAssociativeExpression(tag, //
								left, mid, right));
		}
	}

	private static void processAssociativePredicates(
			Processor<Predicate> processor, List<Predicate> subPreds) {
		for (int i = 0; i < AssociativePredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_ASSOCIATIVE_PREDICATE + i;
			for (final Predicate left : subPreds)
				for (final Predicate right : subPreds)
					processor.process(mAssociativePredicate(tag, left, right));
			for (final Predicate left : subPreds)
				for (final Predicate mid : subPreds)
					for (final Predicate right : subPreds)
						processor.process(mAssociativePredicate(tag, //
								left, mid, right));
		}
	}

	private static void processAtomicExpressions(Processor<Expression> processor) {
		for (int i = 0; i < AtomicExpression.TAGS_LENGTH; i++) {
			final int tag = FIRST_ATOMIC_EXPRESSION + i;
			processor.process(mAtomicExpression(tag));
		}
	}

	private static void processBinaryExpressions(
			Processor<Expression> processor, List<Expression> subExprs) {
		for (int i = 0; i < BinaryExpression.TAGS_LENGTH; i++) {
			final int tag = FIRST_BINARY_EXPRESSION + i;
			for (final Expression left : subExprs)
				for (final Expression right : subExprs)
					processor.process(mBinaryExpression(tag, left, right));
		}
	}

	private static void processBinaryPredicates(Processor<Predicate> processor,
			List<Predicate> subPreds) {
		for (int i = 0; i < BinaryPredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_BINARY_PREDICATE + i;
			for (final Predicate left : subPreds)
				for (final Predicate right : subPreds)
					processor.process(mBinaryPredicate(tag, left, right));
		}
	}

	private static void processBoolExpressions(Processor<Expression> processor,
			List<Predicate> subPreds) {
		for (final Predicate pred : subPreds)
			processor.process(mBoolExpression(pred));
	}

	private static void processMultiplePredicates(
			Processor<Predicate> processor, List<Expression> subExprs) {
		for (int i = 0; i < MultiplePredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_MULTIPLE_PREDICATE + i;
			for (final Expression expr : subExprs)
				processor.process(mMultiplePredicate(tag, expr));
			for (final Expression left : subExprs)
				for (final Expression right : subExprs)
					processor.process(mMultiplePredicate(tag, left, right));
			for (final Expression left : subExprs)
				for (final Expression mid : subExprs)
					for (final Expression right : subExprs)
						processor.process(mMultiplePredicate(tag, left, mid,
								right));
		}
	}

	private static void processRelationalPredicates(
			Processor<Predicate> processor, List<Expression> subExprs) {
		for (int i = 0; i < RelationalPredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_RELATIONAL_PREDICATE + i;
			for (final Expression left : subExprs)
				for (final Expression right : subExprs)
					processor.process(mRelationalPredicate(tag, left, right));
		}

	}

	private static void processSimplePredicates(Processor<Predicate> processor,
			List<Expression> subExprs) {
		for (final Expression expr : subExprs)
			processor.process(mSimplePredicate(expr));
	}

	private static void processQuantifiedExpressions(
			Processor<Expression> processor, List<Predicate> subPreds,
			List<Expression> subExprs) {
		final BoundIdentDecl[] decls = new BoundIdentDecl[] { //
		mBoundIdentDecl("x"), //
		};
		for (int i = 0; i < QuantifiedExpression.TAGS_LENGTH; i++) {
			final int tag = FIRST_QUANTIFIED_EXPRESSION + i;
			for (final Predicate pred : subPreds)
				for (final Expression expr : subExprs)
					processor.process(mQuantifiedExpression(tag, Explicit,
							decls, pred, expr));
		}
	}

	private static void processQuantifiedPredicates(
			Processor<Predicate> processor, List<Predicate> subPreds) {
		final BoundIdentDecl[] decls = new BoundIdentDecl[] { //
		mBoundIdentDecl("x"), //
		};
		for (int i = 0; i < QuantifiedPredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_QUANTIFIED_PREDICATE + i;
			for (final Predicate pred : subPreds)
				processor.process(mQuantifiedPredicate(tag, decls, pred));
		}
	}

	private static void processSetExtensions(Processor<Expression> processor,
			List<Expression> subExprs) {
		processor.process(mSetExtension());
		for (final Expression expr : subExprs)
			processor.process(mSetExtension(expr));
		for (final Expression left : subExprs)
			for (final Expression right : subExprs)
				processor.process(mSetExtension(left, right));
		for (final Expression left : subExprs)
			for (final Expression mid : subExprs)
				for (final Expression right : subExprs)
					processor.process(mSetExtension(left, mid, right));
	}

	private static void processUnaryExpressions(
			Processor<Expression> processor, List<Expression> subExprs) {
		for (int i = 0; i < UnaryExpression.TAGS_LENGTH; i++) {
			final int tag = FIRST_UNARY_EXPRESSION + i;
			if (isDeprecatedUnary(tag))
				continue;
			for (final Expression subExpr : subExprs) {
				processor.process(mUnaryExpression(tag, subExpr));
			}
		}
	}

	private static void processUnaryPredicates(Processor<Predicate> processor,
			List<Predicate> subPreds) {
		for (int i = 0; i < UnaryPredicate.TAGS_LENGTH; i++) {
			final int tag = FIRST_UNARY_PREDICATE + i;
			if (isDeprecatedUnary(tag))
				continue;
			for (final Predicate subExpr : subPreds) {
				processor.process(mUnaryPredicate(tag, subExpr));
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static boolean isDeprecatedUnary(final int tag) {
		return KPRJ1 <= tag && tag <= KID;
	}

	public void testParenExpr0() throws Exception {
		for (final Expression expr : expr0) {
			new ExprChecker(expr).run();
		}
	}

	public void testParenExpr1() throws Exception {
		final Processor<Expression> processor = new CheckProcessor<Expression>();
		processExpressions(processor, pred0, expr0);
	}

	public void testParenExpr2() throws Exception {
		final List<Predicate> p1 = makePred1();
		final List<Expression> e1 = makeExpr1();
		final Processor<Expression> processor = new CheckProcessor<Expression>();
		processExpressions(processor, p1, e1);
	}

	public void testParenPred0() throws Exception {
		for (final Predicate pred : pred0) {
			new PredChecker(pred).run();
		}
	}

	public void testParenPred1() throws Exception {
		final Processor<Predicate> processor = new CheckProcessor<Predicate>();
		processPredicates(processor, pred0, expr0);
	}

	public void testParenPred2() throws Exception {
		final List<Predicate> p1 = makePred1();
		final List<Expression> e1 = makeExpr1();
		final Processor<Predicate> processor = new CheckProcessor<Predicate>();
		processPredicates(processor, p1, e1);
	}

}
