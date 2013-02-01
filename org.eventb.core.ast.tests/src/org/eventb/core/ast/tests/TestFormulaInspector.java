/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtendedFormulas.barS;
import static org.eventb.core.ast.tests.ExtendedFormulas.fooS;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Ensures that finding accumulators work as expected for addition of new
 * findings. Also ensures that skipping nodes during inspections is properly
 * implemented. The other aspects of formula inspection are actually tested in
 * class {@link TestSubFormulas}.
 */
public class TestFormulaInspector {

	private static final IFormulaInspector<String> inspector = new DefaultInspector<String>() {

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<String> accumulator) {
			accumulator.add(asList("a", "b", "c"));
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<String> accumulator) {
			accumulator.add(new String[] { "1", "2" });
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<String> accumulator) {
			accumulator.add("simple");
		}

	};

	private static void assertFindings(Formula<?> formula, String... expected) {
		final List<String> actual = formula.inspect(inspector);
		assertEquals(asList(expected), actual);
	}

	/**
	 * Ensures that the add method for one finding works.
	 */
	@Test 
	public void testSimpleAdd() throws Exception {
		assertFindings(mLiteralPredicate(), "simple");
	}

	/**
	 * Ensures that the add method for an array of findings works.
	 */
	@Test 
	public void testArrayAdd() throws Exception {
		assertFindings(mIntegerLiteral(), "1", "2");
	}

	/**
	 * Ensures that the add method for a list of findings works.
	 */
	@Test 
	public void testListAdd() throws Exception {
		assertFindings(mBoundIdentifier(0), "a", "b", "c");
	}

	private static class Skipper implements IFormulaInspector<IPosition> {

		private final IPosition skipChildrenPos;
		private final IPosition skipAllPos;

		public Skipper(IPosition skipChildrenPos, IPosition skipAllPos) {
			this.skipChildrenPos = skipChildrenPos;
			this.skipAllPos = skipAllPos;
		}

		// Common inspection method
		private void doInspect(IAccumulator<IPosition> accumulator) {
			final IPosition pos = accumulator.getCurrentPosition();
			accumulator.add(pos);
			if (pos.equals(skipChildrenPos)) {
				accumulator.skipChildren();
			}
			if (pos.equals(skipAllPos)) {
				accumulator.skipAll();
			}
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AtomicExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoolExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentDecl decl,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(MultiplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(PredicateVariable predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SetExtension expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SimplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

	}

	private static class Trace {
		private final List<IPosition> positions;

		public Trace(List<IPosition> positions) {
			this.positions = new LinkedList<IPosition>(positions);
		}

		public void removeChildren(IPosition pos) {
			if (pos.isRoot()) {
				positions.clear();
				positions.add(pos);
				return;
			}
			final IPosition nextSibling = pos.getNextSibling();
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0 && cur.compareTo(nextSibling) < 0) {
					iter.remove();
				}
			}
		}

		public void removeAfter(IPosition pos) {
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0) {
					iter.remove();
				}
			}
		}

		public void assertEquals(List<IPosition> actual) {
			Assert.assertEquals(positions, actual);
		}
	}

	private static void assertTrace(Formula<?> formula, String posImage) {
		final Skipper noSkip = new Skipper(null, null);
		final List<IPosition> fullTrace = formula.inspect(noSkip);
		final IPosition pos = makePosition(posImage);

		assertTrace(formula, fullTrace, pos, null);
		assertTrace(formula, fullTrace, null, pos);
		assertTrace(formula, fullTrace, pos, pos);
	}

	private static void assertTrace(Formula<?> formula,
			List<IPosition> fullTrace, IPosition skipChildrenPos,
			IPosition skipAllPos) {
		final Trace expected = new Trace(fullTrace);
		if (skipChildrenPos != null) {
			expected.removeChildren(skipChildrenPos);
		}
		if (skipAllPos != null) {
			expected.removeAfter(skipAllPos);
		}
		final Skipper skipper = new Skipper(skipChildrenPos, skipAllPos);
		final List<IPosition> actual = formula.inspect(skipper);
		expected.assertEquals(actual);
	}

	private static Expression eA = mAtomicExpression();
	private static Expression eB = mAtomicExpression();
	private static Expression eC = mAtomicExpression();
	private static Expression eD = mAtomicExpression();

	private static Predicate pA = mLiteralPredicate();
	private static Predicate pB = mLiteralPredicate();
	private static Predicate pC = mLiteralPredicate();
	private static Predicate pD = mLiteralPredicate();

	private static BoundIdentDecl b_x = mBoundIdentDecl("x");
	private static BoundIdentDecl b_y = mBoundIdentDecl("y");
	private static BoundIdentDecl b_z = mBoundIdentDecl("z");

	private static final Expression eAEFF =  EFF.makeAtomicExpression(TRUE, null);
	private static final Expression eBEFF =  EFF.makeAtomicExpression(TRUE, null);
	private static final LiteralPredicate pAEFF = EFF.makeLiteralPredicate(BTRUE, null);
	private static final LiteralPredicate pBEFF = EFF.makeLiteralPredicate(BTRUE, null);

	public static final FormulaFactory ff = FormulaFactory.getDefault();
	protected static final IntegerLiteral ONE = ff.makeIntegerLiteral(BigInteger.ONE, null);

	/**
	 * Ensures that skip methods work as expected on associative expressions
	 */
	@Test 
	public void testAssociativeExpression() throws Exception {
		final Expression child = mAssociativeExpression(eB, eC);
		final Expression expression = mAssociativeExpression(eA, child, eD);
		assertTrace(expression, "1");
	}

	/**
	 * Ensures that skip methods work as expected on associative predicates.
	 */
	@Test 
	public void testAssociativePredicate() throws Exception {
		final Predicate child = mAssociativePredicate(pB, pC);
		final Predicate predicate = mAssociativePredicate(pA, child, pD);
		assertTrace(predicate, "1");
	}

	/**
	 * Ensures that skip methods work as expected on binary expressions
	 */
	@Test 
	public void testBinaryExpression() throws Exception {
		final Expression grandChild = mBinaryExpression(eB, eC);
		final Expression child = mBinaryExpression(eA, grandChild);
		final Expression expression = mBinaryExpression(child, eD);
		assertTrace(expression, "0.1");
	}

	/**
	 * Ensures that skip methods work as expected on binary predicates
	 */
	@Test 
	public void testBinaryPredicate() throws Exception {
		final Predicate grandChild = mBinaryPredicate(pB, pC);
		final Predicate child = mBinaryPredicate(pA, grandChild);
		final Predicate predicate = mBinaryPredicate(child, pD);
		assertTrace(predicate, "0.1");
	}

	/**
	 * Ensures that skip methods work as expected on relational predicates
	 */
	@Test 
	public void testRelationalPredicate() throws Exception {
		final Predicate predicate = mRelationalPredicate(eA, eB);
		assertTrace(predicate, "");
		assertTrace(predicate, "0");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on extended predicates
	 */
	@Test 
	public void testExtendedPredicate() throws Exception {
		final Predicate predicate = EFF.makeExtendedPredicate(fooS,
				Arrays.<Expression> asList(eAEFF, eBEFF),
				Arrays.<Predicate> asList(pAEFF, pBEFF), null);
		assertTrace(predicate, "");
		assertTrace(predicate, "0");
		assertTrace(predicate, "1");
		assertTrace(predicate, "2");
		assertTrace(predicate, "3");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on extended expression
	 */
	@Test 
	public void testExtendedExpression() throws Exception {
		final Expression expression = EFF.makeExtendedExpression(barS,
				Arrays.<Expression> asList(eAEFF, eBEFF),
				Arrays.<Predicate> asList(pAEFF, pBEFF), null);
		assertTrace(expression, "");
		assertTrace(expression, "0");
		assertTrace(expression, "1");
		assertTrace(expression, "2");
		assertTrace(expression, "3");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods work as expected on unary predicates
	 */
	@Test 
	public void testUnaryPredicate() throws Exception {
		final UnaryPredicate predicate = mUnaryPredicate(pA);
		assertTrace(predicate, "");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on unary expressions
	 */
	@Test 
	public void testUnaryExpression() throws Exception {
		final Expression expression = mUnaryExpression(eA);
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods work as expected on quantified predicates.
	 * BoundIdentDecl are tested it testBoundIdentDecl.
	 */
	@Test 
	public void testQuantifiedPredicate() throws Exception {
		final QuantifiedPredicate predicate = mQuantifiedPredicate(
				mList(b_x, b_y), pA);
		assertTrace(predicate, "");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on quantified expressions.
	 * BoundIdentDecl are tested it testBoundIdentDecl.
	 */
	@Test 
	public void testQuantifiedExpression() throws Exception {
		final Expression expression = mQuantifiedExpression(mList(b_x, b_y),
				pA, eA);
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods work as expected on simple predicates
	 */
	@Test 
	public void testSimplePredicate() throws Exception {
		final SimplePredicate predicate = mSimplePredicate(eA);
		assertTrace(predicate, "");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on multiple predicates
	 */
	@Test 
	public void testMultiplePredicate() throws Exception {
		final MultiplePredicate predicate = mMultiplePredicate(eA, eB, eC);
		assertTrace(predicate, "");
		assertTrace(predicate, "1");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on literal predicates
	 */
	@Test 
	public void testLiteralPredicate() throws Exception {
		final Predicate predicate = pA;
		assertTrace(predicate, "");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods work as expected on bool expressions
	 */
	@Test 
	public void testBoolExpression() throws Exception {
		final Expression expression = mBoolExpression(pA);
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on predicate variable
	 */
	@Test 
	public void testPredicateVariable() throws Exception {
		final Predicate predicate = ff.makePredicateVariable("$P", null);
		assertTrace(predicate, "");
		assertSkipChildrenOnce(predicate);
	}

	/**
	 * Ensures that skip methods works as expected on atomic expression
	 */
	@Test 
	public void testAtomicExpression() throws Exception {
		final Expression expression = mAtomicExpression();
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on integerLiteral
	 */
	@Test 
	public void testIntegerLiteral() throws Exception {
		final Expression expression = mIntegerLiteral();
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on SetExtension
	 */
	@Test 
	public void testSetExtension() throws Exception {
		final Set<Expression> collec = new HashSet<Expression>();
		collec.add(eA);
		collec.add(eB);
		collec.add(eC);
		final Expression expression = ff.makeSetExtension(collec, null);
		assertTrace(expression, "");
		assertTrace(expression, "1");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on BoundIndentifier
	 */
	@Test 
	public void testBoundIdentifier() throws Exception {
		final Expression expression = mBoundIdentifier(0);
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on FreeIdentifier
	 */
	@Test 
	public void testFreeIdentifier() throws Exception {
		final Expression expression = ff.makeFreeIdentifier("x", null);
		assertTrace(expression, "");
		assertSkipChildrenOnce(expression);
	}

	/**
	 * Ensures that skip methods works as expected on BoundIdentDecl.
	 */
	@Test 
	public void testBoundIdentDecl() throws Exception {
		final QuantifiedPredicate predicate = mQuantifiedPredicate(
				mList(b_x, b_y, b_z), pA);
		assertTrace(predicate, "0");
		assertTrace(predicate, "1");
		assertTrace(predicate, "2");
		final Expression expression = mQuantifiedExpression(
				mList(b_x, b_y, b_z), pA, eA);
		assertTrace(expression, "0");
		assertTrace(expression, "1");
		assertTrace(expression, "2");
	}

	/**
	 * Ensures that when children of a node are skipped, children of the other
	 * node won't be skipped (skipChildren is set to False)
	 * 
	 * @param predicate
	 *            the predicate on which test is realized
	 */
	private void assertSkipChildrenOnce(final Predicate predicate) {
		final FormulaFactory fact = predicate.getFactory();
		final AtomicExpression leaf = fact.makeAtomicExpression(TRUE, null);
		assertTrace(fact.makeBinaryPredicate(LIMP, predicate,
				fact.makeRelationalPredicate(EQUAL, leaf, leaf, null), null),
				"0");
	}

	/**
	 * Ensures that when children of a node are skipped, children of the other
	 * node won't be skipped (skipChildren is set to False)
	 * 
	 * @param expression
	 *            the expression on which test is realized
	 */
	private void assertSkipChildrenOnce(final Expression expression) {
		final FormulaFactory fact = expression.getFactory();
		final AtomicExpression leaf = fact.makeAtomicExpression(TRUE, null);
		assertTrace(fact.makeBinaryExpression(MINUS, expression,
				fact.makeAssociativeExpression(PLUS, mList(leaf, leaf), null),
				null), "0");
	}

}
