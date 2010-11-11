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
import static org.eventb.core.ast.Formula.BUNION;
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
	// {x ∣ x∈ℤ}
	final Expression set_x_in_int = mQuantifiedExpression(CSET, Implicit, 
			mList(bd_x), mRelationalPredicate(IN, b0, sint), b0
	);
	// {s ∣ s∈ℤ}
	final Expression set_s_in_int = mQuantifiedExpression(CSET, Implicit, 
			mList(bd_s), mRelationalPredicate(IN, b0, sint), b0
	);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testItems = new ArrayList<TestItem<?>>(Arrays.asList(simpleTests));
		
		// Implicit comprehension set with enclosed comprehension set on the right
		// {y∩{x ∣ x∈ℤ} ∣ y=∅}={∅}
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
		// {{x ∣ x∈ℤ}∩y ∣ y=∅}={∅}
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
		
		// {y∩{x ∣ x∈y} ∣ y=∅}={∅}
		TestItem<Predicate> item3 = new TestItem<Predicate>(
				mRelationalPredicate(EQUAL,
						mQuantifiedExpression(CSET, Implicit,
							mList(bd_y), 
							mRelationalPredicate(EQUAL, b0, emptySet),
							mAssociativeExpression(BINTER, b0, set_x_in_b0)),
						mSetExtension(mList(emptySet))),
				true);
		testItems.add(item3);
		
		// {{x ∣ x∈y}∩y ∣ y=∅}={∅}
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
			// ⊥
			new TestItem<Predicate>( 
					bfalse,
					true
			),
			// finite(x)
			new TestItem<Predicate>(
					mSimplePredicate(id_x),
					true
			),
			// x=x
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, id_x, id_x),
					true
			),
			// ¬⊥
			new TestItem<Predicate>(
					mUnaryPredicate(NOT, bfalse),
					true
			),
			// ⊥∧⊥
			new TestItem<Predicate>(
					mAssociativePredicate(LAND, bfalse, bfalse),
					true
			),
			// ⊥⇒⊥
			new TestItem<Predicate>(
					mBinaryPredicate(LIMP, bfalse, bfalse),
					true
			),
			
			// QuantPred + Pred
			// ∀x,y,z·⊥
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z), bfalse),
					true
			),
			// ∃x,y·∃s,t·⊥
			new TestItem<Predicate>(
					mQuantifiedPredicate(EXISTS, 
							mList(bd_x, bd_y), 
							mQuantifiedPredicate(EXISTS, mList(bd_s, bd_t), bfalse)
					), 
					true
			),
			// ∃x,y·∃s,y·⊥
			new TestItem<Predicate>(
					mQuantifiedPredicate(EXISTS, 
							mList(bd_x,bd_y), 
							mQuantifiedPredicate(EXISTS, mList(bd_s, bd_y), bfalse)
					),
					false // bound in 2 places
			),
			// bool(∀x·⊥)=x
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse)),
							id_x
					),
					false
			),
			// bool(∀x·⊥)=y
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse)),
							id_y
					),
					true
			),
			// x=bool(∀x·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							id_x,
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x), bfalse))
					),
					false
			),
			// ∀x0,y,z·x=x
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mRelationalPredicate(EQUAL, id_x, id_x)
					),
					false
			),
			// ∀x,y,z·x=x
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mRelationalPredicate(EQUAL, b2, b2)
					),
					true
			),
			// ∀x,y,z0·finite({x,y·⊥ ∣ z})
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_x, bd_y), bfalse, id_z))
					),
					false
			),
			// ∀x,y,z0·finite({s,t·⊥ ∣ z})
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit, 
											mList(bd_s, bd_t), bfalse, id_z))
					),
					false
			),
			// ∀x,y,z·finite({s,t·⊥ ∣ z})
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL, 
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, b2))
					),
					true
			),
			// ∀x,y,z·finite({s,t0·⊥ ∣ t})
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, id_t))
					),
					false
			),
			// ∀x,y,z·finite({s,t·⊥ ∣ t})
			new TestItem<Predicate>(
					mQuantifiedPredicate(FORALL,
							mList(bd_x, bd_y, bd_z),
							mSimplePredicate(
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_s, bd_t), bfalse, b0))
					),
					true
			),
			
			// (∀y·y∈ℤ∧(∀z·⊥)∧(∀z·⊥))⇒⊥
			new TestItem<Predicate>(
					mBinaryPredicate(Formula.LIMP,
							mQuantifiedPredicate(
							mList(bd_y),
							mAssociativePredicate(
									mRelationalPredicate(IN, b0, sint),
									mQuantifiedPredicate(FORALL, 
											mList(bd_z),bfalse),
									mQuantifiedPredicate(FORALL, 
											mList(bd_z),bfalse))
					),
					bfalse),
					true
			),
			
			// (∀y·y∈ℤ∧(∀z·∀z·⊥))⇒⊥
			new TestItem<Predicate>(mBinaryPredicate(
					Formula.LIMP,
					mQuantifiedPredicate(
							mList(bd_y),
							mAssociativePredicate(
									mRelationalPredicate(IN, b0, sint),
									mQuantifiedPredicate(
											FORALL,
											mList(bd_z),
											mQuantifiedPredicate(FORALL,
													mList(bd_z), bfalse)))),
					bfalse),
					false
			),
			
			// QuantExpr + Expr
			// {x,y·⊥ ∣ z}=a
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							id_a
					),
					true
			),
			// {x,y·⊥ ∣ z}=x
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							id_x
					),
					false
			),
			// {x,y·⊥ ∣ z}={s,t·⊥ ∣ u}
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mQuantifiedExpression(CSET, Explicit, mList(bd_s, bd_t), bfalse, id_u)),
							true
			),
			// {x,y·⊥ ∣ z}={x,y·⊥ ∣ z}
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z)),
							true
			),
			// {x,y·⊥ ∣ {x,y·⊥ ∣ z}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y), bfalse, id_z)
					),
					false
			),
			// {x,y·⊥ ∣ {s,t·⊥ ∣ u}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, id_u)
					),
					true
			),
			// {x0,y·⊥ ∣ {s,t·⊥ ∣ x}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, id_x)
					),
					false
			),
			// {x,y·⊥ ∣ {s,t·⊥ ∣ x}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mQuantifiedExpression(CSET, Explicit, 
									mList(bd_s, bd_t), bfalse, b3)
					),
					true
			),
			// {x,y·⊥ ∣ {s,t·⊥ ∣ t}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_s, bd_t), bfalse, b0)
					),
					true
			),
			// {x,y·⊥ ∣ z}={s,t}
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mSetExtension(id_s, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ z}={x,t}
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z), 
							mSetExtension(id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ {s,t}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mSetExtension(id_s, id_t)
					),
					true
			),
			// {x0,y·⊥ ∣ {x,t}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mSetExtension(id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ {x,t}}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse, 
							mSetExtension(b1, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ union(z)}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mUnaryExpression(KUNION, id_z)
					),
					true
			),
			// {x0,y·⊥ ∣ union(x)}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse,
							mUnaryExpression(KUNION, id_x)
					),
					false
			),
			// {x,y·⊥ ∣ union(x)}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mUnaryExpression(KUNION, b1)
					),
					true
			),
			// {x,y·⊥ ∣ z}=union(s)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mUnaryExpression(KUNION, id_s)
					),
					true
			),
			// {x,y·⊥ ∣ z}=union(x)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mUnaryExpression(KUNION, id_x)
					),
					false
			),
			// {x,y·⊥ ∣ s ^ t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y), 
							bfalse,
							mBinaryExpression(EXPN, id_s, id_t)
					),
					true
			),
			// {x0,y·⊥ ∣ x ^ t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mBinaryExpression(EXPN, id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ x ^ t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse, 
							mBinaryExpression(EXPN, b1, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ z}=s ^ t
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBinaryExpression(EXPN, id_s, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ z}=x ^ t
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBinaryExpression(EXPN, id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ z}=s∗t
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mAssociativeExpression(MUL, id_s, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ z}=x∗t
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mAssociativeExpression(MUL, id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ s∗t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y), 
							bfalse, 
							mAssociativeExpression(MUL, id_s, id_t)
					),
					true
			),
			// {x0,y·⊥ ∣ x∗t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit, 
							mList(bd_x, bd_y),
							bfalse,
							mAssociativeExpression(MUL, id_x, id_t)
					),
					false
			),
			// {x,y·⊥ ∣ x∗t}
			new TestItem<Expression>(
					mQuantifiedExpression(CSET, Explicit,
							mList(bd_x, bd_y),
							bfalse,
							mAssociativeExpression(MUL, b1, id_t)
					),
					true
			),
			// {x,y·⊥ ∣ z}=bool(∀x,y·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z), 
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), bfalse))
					),
					true
			),
			// {x,y·⊥ ∣ z}=bool(∀s,t·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, 
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_s, bd_t), bfalse))
					),
					true
			),
			// {x,y·⊥ ∣ s}=bool(∀s,t·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_s),
							mBoolExpression(mQuantifiedPredicate(FORALL, mList(bd_s, bd_t), bfalse))
					),
					false
			),	
			
			// QuantExpr + QuantPred
			// {x,y·∀x,y,z·⊥ ∣ b}=a
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
			),
			// {x,y·∀s,t,u·⊥ ∣ b}=a
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_b
							), id_a
					),
					true
			),
			// {x0,y·∀s,t,u·⊥ ∣ x}=a
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_x
							), id_a
					),
					false
			),
			// {x,y·∀s,t,u·⊥ ∣ u}=a
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), id_u
							), id_a
					),
					false
			),
			// {x,y·∀s,t,u·⊥ ∣ x}=a
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit,
									mList(bd_x, bd_y),
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse
									), b1
							), id_a
					),
					true
			),
			// {x,y·⊥ ∣ z}=bool(∀x,y,z·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z), bfalse))
					),
					false
			),
			// {x,y·⊥ ∣ z}=bool(∀s,t,u·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_z),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse))
					),
					true
			),
			// {x,y·⊥ ∣ s}=bool(∀s,t,u·⊥)
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x, bd_y), bfalse, id_s),
							mBoolExpression(
									mQuantifiedPredicate(FORALL, mList(bd_s, bd_t, bd_u), bfalse))
					),
					false
			),
			
			// { x · x ∈ ℤ ∣ x} ∪ { x · x ∈ ℤ ∣ x } = ∅
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL,
					mAssociativeExpression(BUNION,
							mQuantifiedExpression(CSET, Explicit, mList(bd_x),
									mRelationalPredicate(IN, b0, sint), b0),
							mQuantifiedExpression(CSET, Explicit, mList(bd_x),
									mRelationalPredicate(IN, b0, sint), b0)),
					emptySet), 
					true
			),
			
			// { x · x ∈ ℙ(ℤ) ∣ x ∪ { x · x ∈ ℤ ∣ x }} = ∅
			new TestItem<Predicate>(mRelationalPredicate(
					EQUAL,
					mQuantifiedExpression(
							CSET,
							Explicit,
							mList(bd_x),
							mRelationalPredicate(IN, b0,
									mUnaryExpression(Formula.POW, sint)),
							mAssociativeExpression(
									BUNION,
									b0,
									mQuantifiedExpression(CSET, Explicit,
											mList(bd_x),
											mRelationalPredicate(IN, b0, sint),
											b0))), 
					emptySet),
					false
			),
			
			// Only Exprs
			// union(x)=y
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, mUnaryExpression(KUNION, id_x), id_y),
					true
			),
			// bool(⊥)=y
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, mBoolExpression(bfalse), id_y),
					true
			),
			// {x,y}
			new TestItem<Expression>(
					mSetExtension(id_x, id_y),
					true
			),
			// x=2
			new TestItem<Predicate>(
					mRelationalPredicate(EQUAL, id_x, mIntegerLiteral(2)),
					true
			),
			// x ^ y
			new TestItem<Expression>(
					mBinaryExpression(EXPN, id_x, id_y),
					true
			),
			// x∗x
			new TestItem<Expression>(
					mAssociativeExpression(MUL, id_x, id_x),
					true
			),
			
			
			// Assignments
			// x ≔ x
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(id_x)),
					true
			),
			// x ≔ {x ∣ x∈ℤ}
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(set_x_in_int)),
					false
			),
			// x ≔ {s ∣ s∈ℤ}
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x), mList(set_s_in_int)),
					true
			),
			// x,y ≔ y, x
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y), mList(id_y, id_x)),
					true
			),
			// x,y ≔ {x ∣ x∈ℤ}, t
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y), mList(set_x_in_int, id_t)),
					false
			),
			// y,x ≔ {x ∣ x∈ℤ}, t
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_y, id_x), mList(set_x_in_int, id_t)),
					false
			),
			// x,y,z ≔ y, z, x
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(id_y, id_z, id_x)),
					true
			),
			// x,y,z ≔ {x ∣ x∈ℤ}, t, u
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_x, id_y, id_z), mList(set_x_in_int, id_t, id_u)),
					false
			),
			// y,x,z ≔ {x ∣ x∈ℤ}, t, u
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_y, id_x, id_z), mList(set_x_in_int, id_t, id_u)),
					false
			),
			// z,y,x ≔ {x ∣ x∈ℤ}, t, u
			new TestItem<Assignment>(
					mBecomesEqualTo(mList(id_z, id_y, id_x), mList(set_x_in_int, id_t, id_u)),
					false
			),
			// x :∈ y
			new TestItem<Assignment>(
					mBecomesMemberOf(id_x, id_y),
					true
			),
			// x :∈ {x ∣ x∈ℤ}
			new TestItem<Assignment>(
					mBecomesMemberOf(id_x, set_x_in_int),
					false
			),
			// x :∣ x'=x
			new TestItem<Assignment>(
					mBecomesSuchThat(mList(id_x), mList(bd_xp), mRelationalPredicate(EQUAL, b0, id_x)),
					true
			),
			// x :∣ x'={x ∣ x∈ℤ}
			new TestItem<Assignment>(
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
			final List<FreeIdentifier> freeIdentifiers = Arrays.asList(item.formula.getFreeIdentifiers());
			boolean result = item.formula.isLegible(freeIdentifiers).isSuccess();
			String syntaxTree = item.formula.getSyntaxTree();
			assertEquals("\nTesting syntax tree:\n" + syntaxTree
					+ "\nResult obtained: " + (result ? "" : "NOT")
					+ " legible\n" + "Result expected: "
					+ (item.expectedResult ? "" : "NOT") + " legible\n",
					item.expectedResult, result);
		}
	}
}
