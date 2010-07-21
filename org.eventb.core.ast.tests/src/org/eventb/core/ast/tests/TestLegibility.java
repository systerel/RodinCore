/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.INTEGER;
import static org.eventb.core.ast.Formula.KUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;

/**
 * Unit test of the legibility checker.
 * 
 * @author franz
 */
public class TestLegibility extends TestCase {
	
	private static final class TestItem<T extends Formula<T>> {
		T formula;
		boolean expectedResult;
		
		TestItem(T formula, boolean expectedResult) {
			this.expectedResult = expectedResult;
			this.formula = formula;
		}
	}

	private List<TestItem<?>> testItems;

	final FreeIdentifier id_x = mFreeIdentifier("x");
	final FreeIdentifier id_y = mFreeIdentifier("y");
	final FreeIdentifier id_z = mFreeIdentifier("z");
	final FreeIdentifier id_s = mFreeIdentifier("s");
	final FreeIdentifier id_t = mFreeIdentifier("t");
	final FreeIdentifier id_u = mFreeIdentifier("u");
	final FreeIdentifier id_a = mFreeIdentifier("a");
	final FreeIdentifier id_b = mFreeIdentifier("b");
	
	final BoundIdentDecl bd_x = mBoundIdentDecl("x");
	final BoundIdentDecl bd_y = mBoundIdentDecl("y");
	final BoundIdentDecl bd_z = mBoundIdentDecl("z");
	final BoundIdentDecl bd_s = mBoundIdentDecl("s");
	final BoundIdentDecl bd_t = mBoundIdentDecl("t");
	final BoundIdentDecl bd_u = mBoundIdentDecl("u");

	final BoundIdentDecl bd_xp = mBoundIdentDecl("x'");
	final BoundIdentDecl bd_yp = mBoundIdentDecl("y'");
	final BoundIdentDecl bd_zp = mBoundIdentDecl("z'");

	final Expression b0 = mBoundIdentifier(0);
	final Expression b1 = mBoundIdentifier(1);
	final Expression b2 = mBoundIdentifier(2);
	final Expression b3 = mBoundIdentifier(3);

	final LiteralPredicate bfalse = mLiteralPredicate(BFALSE);
	
	final Expression emptySet = mAtomicExpression(EMPTYSET);
	final Expression sint = mAtomicExpression(INTEGER);
	final Expression set_x_in_int = mQuantifiedExpression(CSET, Implicit, 
			mList(bd_x), mRelationalPredicate(IN, b0, sint), b0
	);
	final Expression set_s_in_int = mQuantifiedExpression(CSET, Implicit, 
			mList(bd_s), mRelationalPredicate(IN, b0, sint), b0
	);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testItems = new ArrayList<TestItem<?>>(Arrays.asList(simpleTests));
		
		// Implicit comprehension set with enclosed comprehension set on the right
		TestItem<Predicate> item1 = new TestItem<Predicate>(
				mRelationalPredicate(EQUAL,
						mQuantifiedExpression(CSET, Implicit,
								mList(bd_y),
								mRelationalPredicate(EQUAL, b0, emptySet),
								mAssociativeExpression(BINTER, b0, set_x_in_int)
						), mSetExtension(emptySet)
				), true);
		testItems.add(item1);
		
		// Implicit comprehension set with enclosed comprehension set on the left
		TestItem<Predicate> item2 = new TestItem<Predicate>(
				mRelationalPredicate(EQUAL,
						mQuantifiedExpression(CSET, Implicit,
								mList(bd_y), 
								mRelationalPredicate(EQUAL, b0, emptySet),
								mAssociativeExpression(BINTER, set_x_in_int, b0)
						), mSetExtension(emptySet)
				), true);
		testItems.add(item2);

		final Expression set_x_in_b0 =
			mQuantifiedExpression(CSET, Implicit, 
					mList(bd_x), mRelationalPredicate(IN, b0, b1), b0);

		TestItem<Predicate> item3 = new TestItem<Predicate>(
				mRelationalPredicate(EQUAL,
						mQuantifiedExpression(CSET, Implicit,
							mList(bd_y), 
							mRelationalPredicate(EQUAL, b0, emptySet),
							mAssociativeExpression(BINTER, b0, set_x_in_b0)),
						mSetExtension(mList(emptySet))),
				true);
		testItems.add(item3);

		TestItem<Predicate> item4 = new TestItem<Predicate>(
				mRelationalPredicate(EQUAL,
						mQuantifiedExpression(CSET, Implicit,
							mList(bd_y), 
							mRelationalPredicate(EQUAL, b0, emptySet),
							mAssociativeExpression(BINTER, set_x_in_b0, b0)),
						mSetExtension(emptySet)),
				true);
		testItems.add(item4);

	}


	
	
	private TestItem<?>[] simpleTests = new TestItem[] {
			// Pred
			new TestItem<Predicate>( 
					bfalse,
					true
			), new TestItem<Predicate>(
					mSimplePredicate(id_x),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, id_x, id_x),
					true
			), new TestItem<Predicate>(
					mUnaryPredicate(NOT, bfalse),
					true
			), new TestItem<Predicate>(
					mAssociativePredicate(LAND, bfalse, bfalse),
					true
			), new TestItem<Predicate>(
					mBinaryPredicate(LIMP, bfalse, bfalse),
					true
			),
			
			// QuantPred + Pred
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z), bfalse),
					true
			), new TestItem<Predicate>(
					mQuantifiedPredicate(EXISTS, 
							mList(bd_x, bd_y), 
							mQuantifiedPredicate(EXISTS, mList(bd_s, bd_t), bfalse)
					), 
					true
			), new TestItem<Predicate>(
					mQuantifiedPredicate(EXISTS, 
							mList(bd_x,bd_y), 
							mQuantifiedPredicate(EXISTS, mList(bd_s, bd_y), bfalse)
					),
					false // bound in 2 places
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse)),
							id_x
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse)),
							id_y
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							id_x,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse))
					),
					false
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mRelationalPredicate(EQUAL, id_x, id_x)
					),
					false
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mRelationalPredicate(EQUAL, b2, b2)
					),
					true
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_x, bd_y), bfalse, id_z))
					),
					false
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit, 
											mList(bd_s, bd_t), bfalse, id_z))
					),
					false
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL, 
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, b2))
					),
					true
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, id_t))
					),
					false
			), new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, b0))
					),
					true
			),			
			
			// QuantExpr + Expr
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							id_a
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							id_x
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mQuantifiedExpression(CSET, Explicit, mList(bd_s, bd_t), bfalse, id_u)),
							true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z)),
							false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y), bfalse, id_z)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, id_u)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, id_x)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mQuantifiedExpression(CSET, Explicit, 
									mList(bd_s, bd_t), bfalse, b3)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, b0)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mSetExtension(id_s, id_t)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z), 
							mSetExtension(id_x, id_t)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mSetExtension(id_s, id_t)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mSetExtension(id_x, id_t)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mSetExtension(b1, id_t)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mUnaryExpression(KUNION, id_z)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse,
							mUnaryExpression(KUNION, id_x)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mUnaryExpression(KUNION, b1)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mUnaryExpression(KUNION, id_s)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mUnaryExpression(KUNION, id_x)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mBinaryExpression(EXPN, id_s, id_t)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mBinaryExpression(EXPN, id_x, id_t)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse, 
							mBinaryExpression(EXPN, b1, id_t)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBinaryExpression(EXPN, id_s, id_t)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBinaryExpression(EXPN, id_x, id_t)
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mAssociativeExpression(MUL, id_s, id_t)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mAssociativeExpression(MUL, id_x, id_t)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse, 
							mAssociativeExpression(MUL, id_s, id_t)
					),
					true
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mAssociativeExpression(MUL, id_x, id_t)
					),
					false
			), new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mAssociativeExpression(MUL, b1, id_t)
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z), 
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), bfalse))
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_s, bd_t), bfalse))
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_s),
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_s, bd_t), bfalse))
					),
					false
			),	
			
			// QuantExpr + QuantPred
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z),
											bfalse
									), id_b
							), id_a
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_b
							), id_a
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_x
							), id_a
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_u
							), id_a
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), b1
							), id_a
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z), bfalse))
					),
					false
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse))
					),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_s),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse))
					),
					false
			),

			// Only Exprs
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, mUnaryExpression(KUNION, id_x), id_y),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, mBoolExpression(bfalse), id_y),
					true
			), new TestItem<Expression>(
					mSetExtension(id_x, id_y),
					true
			), new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, id_x, mIntegerLiteral(2)),
					true
			), new TestItem<Expression>(
					mBinaryExpression(EXPN, id_x, id_y),
					true
			), new TestItem<Expression>(
					mAssociativeExpression(MUL, id_x, id_x),
					true
			),
			
			
			// Assignments
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(id_x)),
					true
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(set_x_in_int)),
					false
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(set_s_in_int)),
					true
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y), mList(id_y, id_x)),
					true
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y), mList(set_x_in_int, id_t)),
					false
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_y, id_x), mList(set_x_in_int, id_t)),
					false
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(id_y, id_z, id_x)),
					true
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(set_x_in_int, id_t, id_u)),
					false
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_y, id_x, id_z), mList(set_x_in_int, id_t, id_u)),
					false
			), new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_z, id_y, id_x), mList(set_x_in_int, id_t, id_u)),
					false
			), new TestItem<Assignment>(
					mBecomesMemberOf(id_x, id_y),
					true
			), new TestItem<Assignment>(
					mBecomesMemberOf(id_x, set_x_in_int),
					false
			), new TestItem<Assignment>(
					mBecomesSuchThat(mList(id_x), mList(bd_xp), mRelationalPredicate(EQUAL, b0, id_x)),
					true
			), new TestItem<Assignment>(
					mBecomesSuchThat(mList(id_x), mList(bd_xp), mRelationalPredicate(EQUAL, b0, set_x_in_int)),
					false
			),
	};

	
	/**
	 * Main test routine.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testLegibility() {
		for (TestItem item : testItems) {
			boolean result = item.formula.isLegible(null).isSuccess();
			String syntaxTree = item.formula.getSyntaxTree();
			assertEquals("\nTesting syntax tree:\n" + syntaxTree
					+ "\nResult obtained: " + (result ? "" : "NOT")
					+ " legible\n" + "Result expected: "
					+ (item.expectedResult ? "" : "NOT") + " legible\n",
					item.expectedResult, result);
		}
	}
}
