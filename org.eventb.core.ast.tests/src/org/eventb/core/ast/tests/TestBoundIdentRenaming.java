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
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
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
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.List;
import java.util.Vector;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for the following methods:
 * <ul>
 * <li><code>org.eventb.core.ast.Formula.toString()</code></li>
 * <li><code>org.eventb.core.ast.Formula.toStringFullyParenthesized()</code></li>
 * </ul> 
 * 
 * @author Laurent Voisin
 */
public class TestBoundIdentRenaming extends AbstractTests {
	
	private List<TestItem<?>> testItems = new Vector<TestItem<?>>();

	private class TestItem<T extends Formula<T>> {
		T formula;
		
		TestItem(T formula) {
			this.formula = formula;
		}
	}

	// Builds all kinds of expressions containing only <code>expr</code> as child (if any).
	private static Expression[] mExpressions(Expression expr) {
		return new Expression[] {
				expr,
				mAssociativeExpression(expr, expr),
				mAtomicExpression(),
				mBinaryExpression(expr, expr),
				mBoolExpression(mSimplePredicate(expr)),
				mIntegerLiteral(),
				// mQuantifiedExpression(mList(ids[z][1]), mLiteralPredicate(), ident),
				mSetExtension(expr),
				mUnaryExpression(expr)
		};
	}
	
	// Builds all kinds of predicates containing only <code>expr</code> as child (if any).
	private static Predicate[] mPredicates(Expression expr) {
		Predicate pred = mSimplePredicate(expr);
		return new Predicate[] {
				mAssociativePredicate(pred, pred),
				mBinaryPredicate(pred, pred),
				mLiteralPredicate(),
				// mQuantifiedPredicate(mList(ids[z][1]), identPred),
				mRelationalPredicate(expr, expr),
				pred,
				mUnaryPredicate(pred)
		};
	}
	
	private static QuantifiedExpression mExplicitQuantifiedExpression(
			BoundIdentDecl[] boundIdents, Predicate pred, Expression expr) {
		return mQuantifiedExpression(boundIdents, pred, expr);
	}
	
	private static QuantifiedExpression mImplicitQuantifiedExpression(
			BoundIdentDecl[] boundIdents, Predicate pred, Expression expr) {
		return ff.makeQuantifiedExpression(Formula.QUNION, boundIdents, pred,
				expr, null, QuantifiedExpression.Form.Implicit);
	}

	private static QuantifiedExpression mLambda(
			Expression pattern, Predicate pred, Expression expr) {
		List<BoundIdentDecl> boundIdents = new Vector<BoundIdentDecl>();
		Expression boundPattern = pattern.bindAllFreeIdents(boundIdents, ff);
		Expression pair = ff.makeBinaryExpression(Formula.MAPSTO, boundPattern, expr, null);
		return ff.makeQuantifiedExpression(Formula.CSET, boundIdents
				.toArray(new BoundIdentDecl[boundIdents.size()]), pred, pair,
				null, QuantifiedExpression.Form.Lambda);
	}

	private void addForall(Expression conflicting, Expression unchanged, BoundIdentDecl[] boundIdents) {
		Expression[] freeExprs = mExpressions(conflicting);
		Expression[] boundExprs = mExpressions(unchanged);
		for (Expression fe: freeExprs) {
			for (Expression be: boundExprs) {
				testItems.add(new TestItem<Predicate>(
						mQuantifiedPredicate(boundIdents, mSimplePredicate(fe))
				));
				testItems.add(new TestItem<Predicate>(
						mQuantifiedPredicate(boundIdents, mRelationalPredicate(fe, be))
				));
			}
		}
		Predicate[] freePreds = mPredicates(conflicting);
		Predicate[] boundPreds = mPredicates(unchanged);
		for (Predicate fp: freePreds) {
			for (Predicate bp: boundPreds) {
				testItems.add(new TestItem<Predicate>(
						mQuantifiedPredicate(boundIdents, fp)
				));
				testItems.add(new TestItem<Predicate>(
						mQuantifiedPredicate(boundIdents, mAssociativePredicate(fp, bp))
				));
			}
		}
	}

	private static BinaryExpression mMaplet(Expression left, Expression right) {
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, null);
	}
	
	@Before
	public void setUp() throws Exception {

		final FreeIdentifier id_x = mFreeIdentifier("x");
		final FreeIdentifier id_y = mFreeIdentifier("y");
		final FreeIdentifier id_z = mFreeIdentifier("z");

		final BoundIdentDecl b_x = mBoundIdentDecl("x");
		final BoundIdentDecl b_y = mBoundIdentDecl("y");
		final BoundIdentDecl b_z = mBoundIdentDecl("z");
		
		final BoundIdentifier b0 = mBoundIdentifier(0);
		final BoundIdentifier b1 = mBoundIdentifier(1);

		//-------------
		// Basic tests
		//-------------
		// Conflict between bound and free identifier.
		testItems.add(new TestItem<Predicate>(
				mQuantifiedPredicate(mList(b_x),
						mRelationalPredicate(id_x, b0)
				)
		));
		
		// Conflict between two bound identifiers.
		testItems.add(new TestItem<Predicate>(
				mQuantifiedPredicate(mList(b_x),
					mQuantifiedPredicate(mList(b_x),
							mRelationalPredicate(b0, b1)
					)
				)
		));
		
		// Conflict between two bound identifiers.
		testItems.add(new TestItem<Predicate>(
				mQuantifiedPredicate(mList(b_x, b_x),
						mRelationalPredicate(b0, b1)
				)
		));
		
		// Same tests with Explicit QuantifiedExpression
		testItems.add(new TestItem<Expression>(
				mExplicitQuantifiedExpression(mList(b_x),
						mRelationalPredicate(id_x, b0),
						b0
				)
		));
		testItems.add(new TestItem<Expression>(
				mExplicitQuantifiedExpression(mList(b_x),
						mSimplePredicate(b0),
						mExplicitQuantifiedExpression(mList(b_x),
								mRelationalPredicate(b0, b1),
								id_y
						)
				)
		));
		
		// Same tests with Implicit QuantifiedExpression
		testItems.add(new TestItem<Expression>(
				mImplicitQuantifiedExpression(mList(b_x),
						mRelationalPredicate(id_x, b0),
						b0
				)
		));
		testItems.add(new TestItem<Expression>(
				mImplicitQuantifiedExpression(mList(b_x),
						mSimplePredicate(b0),
						mImplicitQuantifiedExpression(mList(b_x),
								mRelationalPredicate(b0, b1),
								id_y
						)
				)
		));
		
		// Same tests with Lambda QuantifiedExpression
		testItems.add(new TestItem<Expression>(
				mLambda(id_x,
						mRelationalPredicate(id_x, b0),
						b0
				)
		));
		testItems.add(new TestItem<Expression>(
				mLambda(id_x,
						mSimplePredicate(b0),
						mLambda(id_x,
								mRelationalPredicate(b0, b1),
								id_y
						)
				)
		));
		
		// More test with Lambda (checks pattern rewriting)
		testItems.add(new TestItem<Expression>(
				mLambda(mMaplet(id_x, mMaplet(id_y, id_z)),
						mRelationalPredicate(id_x, b0),
						mBinaryExpression(b1, id_y)
				)
		));
		
		// More test with implicitly quantifier expression
		// Simple approach would capture free variable y
		testItems.add(new TestItem<Expression>(
				mImplicitQuantifiedExpression(mList(b_x),
						mSimplePredicate(id_x),
						mBinaryExpression(b0, id_y)
				)
		));

		// Similar test with capture of variable bound above.
		testItems.add(new TestItem<Expression>(
				mLambda(id_x,
						mSimplePredicate(id_x),
						mImplicitQuantifiedExpression(mList(b_x),
								mSimplePredicate(id_x),
								mBinaryExpression(b0, b1)
						)
				)
		));
		
		//---------------------------------------------------
		// Extensive tests on all expressions and predicates
		//---------------------------------------------------
		
		// Test with free and unique conflicting bound for expressions.
		addForall(id_x, mBoundIdentifier(0), mList(b_x));
		
		// Test with free and first conflicting bound for expressions.
		addForall(id_x, mBoundIdentifier(0), mList(b_x, b_y));
		addForall(id_x, mBoundIdentifier(1), mList(b_x, b_y));
		
		// Test with free and last conflicting bound for expressions.
		addForall(id_y, mBoundIdentifier(0), mList(b_x, b_y));
		addForall(id_y, mBoundIdentifier(1), mList(b_x, b_y));
		
		// Test with free and middle conflicting bound for expressions.
		addForall(id_y, mBoundIdentifier(0), mList(b_x, b_y, b_z));
		addForall(id_y, mBoundIdentifier(1), mList(b_x, b_y, b_z));
		addForall(id_y, mBoundIdentifier(2), mList(b_x, b_y, b_z));
		
	}

	private void checkresult(TestItem<?> testItem, String result) {
		Predicate expected;
		String parserInput;
		
		if (testItem.formula instanceof Expression) {
			expected = mSimplePredicate((Expression) testItem.formula);
			parserInput = "finite(" + result + ")";
		}
		else {
			expected = (Predicate) testItem.formula;
			parserInput = result;
		}
		
		Predicate actual = parsePredicate(parserInput);
		String msg = "Input formula:\n" + expected.getSyntaxTree()
				+ "\nString result:" + parserInput //
				+ "\nResult formula: " + actual.getSyntaxTree() + "\n";
		assertEquals(msg, expected, actual);
	}
	
	/**
	 * Test method for 'org.eventb.core.ast.Formula.toString()'
	 */
	@Test 
	public final void testToString() {
		for (TestItem<?> testItem : testItems) {
			// Unparse the formula
			String result = testItem.formula.toString();
			checkresult(testItem, result);
		}
	}

	/**
	 * Test method for 'org.eventb.core.ast.Formula.toStringFullyParenthesized()'
	 */
	@Test 
	public final void testToStringFullyParenthesized() {
		for (TestItem<?> testItem : testItems) {
			// Unparse the formula
			String result = testItem.formula.toStringFullyParenthesized();
			checkresult(testItem, result);
		}
	}
	
}
