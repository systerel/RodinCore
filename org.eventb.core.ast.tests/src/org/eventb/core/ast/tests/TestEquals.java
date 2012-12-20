/*******************************************************************************
 * Copyright (c) 2005, 20012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - added support for predicate variables
 *     Systerel - bug #3574162: AST does not compare bound ident decl types
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.Type;
import org.junit.Test;


/**
 * Unit test for formula equality and hash codes.
 * 
 * @author Laurent Voisin
 */
public class TestEquals extends AbstractTests {

	private static final Type B = ff.makeBooleanType();
	private static final Type Z = ff.makeIntegerType();

	private class TestItem<T extends Formula<T>> {
		T[] formulas;
		
		TestItem(T... formulas) {
			this.formulas = formulas;
		}
	}

	private FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);

	private BoundIdentDecl b_x = ff.makeBoundIdentDecl("x", null);
	private BoundIdentDecl b_y = ff.makeBoundIdentDecl("y", null);
	private BoundIdentDecl b_z = ff.makeBoundIdentDecl("z", null);
	private BoundIdentDecl b_t = ff.makeBoundIdentDecl("t", null);

	private BoundIdentifier b0 = ff.makeBoundIdentifier(0, null);
	private BoundIdentifier b1 = ff.makeBoundIdentifier(1, null);
	
	private final PredicateVariable pv_P = ff.makePredicateVariable("$P", null);
	private final PredicateVariable pv_P_bis = ff.makePredicateVariable("$P", null);
	private final PredicateVariable pv_Q = ff.makePredicateVariable("$Q", null);
	
	private TestItem<?>[] equals = {
		new TestItem<Expression>(id_x, id_x),
		// Formulas are equal modulo alpha-conversion
		new TestItem<Predicate>(
				mQuantifiedPredicate(mList(b_x), mSimplePredicate(b0)),
				mQuantifiedPredicate(mList(b_y), mSimplePredicate(b0)),
				mQuantifiedPredicate(mList(b_z), mSimplePredicate(b0))
		),
		new TestItem<Expression>(
				mQuantifiedExpression(mList(b_x), mSimplePredicate(b0), b0),
				mQuantifiedExpression(mList(b_y), mSimplePredicate(b0), b0),
				mQuantifiedExpression(mList(b_z), mSimplePredicate(b0), b0)
		),
		new TestItem<Predicate>(
				mQuantifiedPredicate(mList(b_x, b_y),
						mRelationalPredicate(b1, b0)
				),
				mQuantifiedPredicate(mList(b_y, b_x),
						mRelationalPredicate(b1, b0)
				),
				mQuantifiedPredicate(mList(b_z, b_t),
						mRelationalPredicate(b1, b0)
				)
		),
		new TestItem<Expression>(
				mQuantifiedExpression(mList(b_x, b_y),
						mRelationalPredicate(b1, b0),
						mBinaryExpression(b1, b0)
				),
				mQuantifiedExpression(mList(b_y, b_x),
						mRelationalPredicate(b1, b0),
						mBinaryExpression(b1, b0)
				),
				mQuantifiedExpression(mList(b_z, b_t),
						mRelationalPredicate(b1, b0),
						mBinaryExpression(b1, b0)
				)
		),
		// Formulas are equal modulo their form
		new TestItem<Expression>(
				ff.makeQuantifiedExpression(Formula.QUNION, mList(b_x),
						mSimplePredicate(b0),
						b0, null, QuantifiedExpression.Form.Explicit
				),
				ff.makeQuantifiedExpression(Formula.QUNION, mList(b_y),
						mSimplePredicate(b0),
						b0, null, QuantifiedExpression.Form.Explicit
				),
				ff.makeQuantifiedExpression(Formula.QUNION, mList(b_x),
						mSimplePredicate(b0),
						b0, null, QuantifiedExpression.Form.Implicit
				),
				ff.makeQuantifiedExpression(Formula.QUNION, mList(b_y),
						mSimplePredicate(b0),
						b0, null, QuantifiedExpression.Form.Implicit
				)
		),
		new TestItem<Expression>(
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_x),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Explicit
				),
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_y),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Explicit
				),
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_x),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Implicit
				),
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_y),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Implicit
				),
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_x),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Lambda
				),
				ff.makeQuantifiedExpression(Formula.CSET, mList(b_y),
						mSimplePredicate(b0),
						mMaplet(b0, b0), null, QuantifiedExpression.Form.Lambda
				)
		),
		// predicate variables
		new TestItem<Predicate>(
				pv_P, pv_P_bis),
	};
	
	private TestItem<?>[] notEquals = {
			new TestItem<Expression>(id_x, id_y),
			// Bound occurences are interchanged
			new TestItem<Predicate>(
					mQuantifiedPredicate(mList(b_x, b_y),
							mRelationalPredicate(b1, b0)
					),
					mQuantifiedPredicate(mList(b_y, b_x),
							mRelationalPredicate(b0, b1)
					)
			),
			new TestItem<Predicate>(
					mQuantifiedPredicate(mList(b_x, b_y),
							mSimplePredicate(b0)
					),
					mQuantifiedPredicate(mList(b_y, b_x),
							mSimplePredicate(b1)
					)
			),
			new TestItem<Expression>(
					mQuantifiedExpression(mList(b_x, b_y),
							mRelationalPredicate(b1, b0),
							mBinaryExpression(b1, b0)
					),
					mQuantifiedExpression(mList(b_y, b_x),
							mRelationalPredicate(b0, b1),
							mBinaryExpression(b1, b0)
					)
			),
			new TestItem<Expression>(
					mQuantifiedExpression(mList(b_x, b_y),
							mRelationalPredicate(b1, b0),
							mBinaryExpression(b1, b0)
					),
					mQuantifiedExpression(mList(b_y, b_x),
							mRelationalPredicate(b1, b0),
							mBinaryExpression(b0, b1)
					)
			),
			// predicate variables
			new TestItem<Predicate>(
					pv_P, pv_Q),
	};
		
	
	/**
	 * Test method for 'org.eventb.core.ast.Formula.equals()'
	 * and 'org.eventb.core.ast.Formula.hashCode()'
	 */
	@Test 
	public final void testEquals() {
		for (TestItem<?> testItem : equals) {
			Formula<?>[] formulas = testItem.formulas;
			int length = formulas.length;
			for (int i = 0; i < length; ++i) {
				assertTrue("Reflexive equality for " + formulas[i],
						formulas[i].equals(formulas[i]));
				for (int j = i + 1; j < length; ++j) {
					final Formula<?> f1 = formulas[i];
					final Formula<?> f2 = formulas[j];
					assertTrue("Equality of " + f1 + " and " + f2,
							f1.equals(f2));
					assertTrue("Equality of " + f2 + " and " + f1,
							f2.equals(f1));
					assertTrue("Hash codes equality for " + f2 + " and " + f1,
							f1.hashCode() == f2.hashCode());
				}
			}
		}
	}		
	
	@Test 
	public final void testNotEquals() {
		for (TestItem<?> testItem : notEquals) {
			Formula<?>[] formulas = testItem.formulas;
			int length = formulas.length;
			for (int i = 0; i < length; ++i) {
				for (int j = i + 1; j < length; ++j) {
					final Formula<?> f1 = formulas[i];
					final Formula<?> f2 = formulas[j];
					assertFalse("Disequality of " + f1 + " and " + f2,
							f1.equals(f2));
					assertFalse("Disequality of " + f2 + " and " + f1,
							f2.equals(f1));
					assertFalse("Hash codes disequality for " + f2 + " and " + f1,
							f1.hashCode() == f2.hashCode());
				}
			}
		}
	}
	
	/**
	 * Ensures that two different formulas that happen to have the same hash
	 * code can nevertheless be compared without raising an exception.
	 * 
	 * Cf. bug #1711912: ClassCastException in AST Formula.equals()
	 */
	@Test 
	public final void testNotEqualsSameHashCode() {
		final Predicate f1 = parsePredicate("CLTR ∩ cel_inv[ran(env)]=∅");
		final Predicate f2 = parsePredicate("ran(env) ∩ cel_inv[CLTR]=∅");
		assertEquals("Both predicates should have the same hash code",
				f1.hashCode(), f2.hashCode());
		assertFalse("Disequality of " + f1 + " and " + f2,
				f1.equals(f2));
		assertFalse("Disequality of " + f2 + " and " + f1,
				f2.equals(f1));
	}

	/**
	 * Ensures that bound identifier declarations that bear different names or
	 * different types are considered different.
	 * 
	 * Cf. bug #3574162: AST does not compare bound ident decl types
	 */
	@Test 
	public void testBoundIdentDeclEquality() {
		final BoundIdentDecl b_xZ = mBoundIdentDecl("x", Z);
		final BoundIdentDecl b_xB = mBoundIdentDecl("x", B);
		final BoundIdentDecl b_yZ = mBoundIdentDecl("y", Z);
		final BoundIdentDecl b_yB = mBoundIdentDecl("y", B);

		// Test differing names
		assertFormulaNotEqual(b_x, b_y);
		assertFormulaNotEqual(b_xZ, b_yZ);
		assertFormulaNotEqual(b_xZ, b_yB);

		// Test differing types
		assertFormulaEquality(b_x, mBoundIdentDecl("x"), b_xZ,
				mBoundIdentDecl("x", Z), b_xB);
	}

	/**
	 * Ensures that bound identifier declarations that bear different types make
	 * "becomes such that" assignments different.
	 * 
	 * Cf. bug #3574162: AST does not compare bound ident decl types
	 */
	@Test 
	public void testBecomesSuchThat() {
		final FreeIdentifier id_xZ = FastFactory.mFreeIdentifier("x", Z);
		final BoundIdentDecl b_xp = mBoundIdentDecl("x'");
		final BoundIdentDecl b_xpZ = mBoundIdentDecl("x'", Z);
		final BoundIdentDecl b_xpB = mBoundIdentDecl("x'", B);
		final Predicate cond = FastFactory.mLiteralPredicate();
		assertFormulaEquality(
				(Assignment) //
				mBecomesSuchThat(mList(id_xZ), mList(b_xp), cond),
				mBecomesSuchThat(mList(id_xZ), mList(b_xp), cond),
				mBecomesSuchThat(mList(id_xZ), mList(b_xpZ), cond),
				mBecomesSuchThat(mList(id_xZ), mList(b_xpZ), cond),
				mBecomesSuchThat(mList(id_xZ), mList(b_xpB), cond));
	}

	/**
	 * Ensures that bound identifier declarations that bear different types make
	 * quantified predicates different.
	 * 
	 * Cf. bug #3574162: AST does not compare bound ident decl types
	 */
	@Test 
	public void testQuantifiedExpression() {
		assertPredicateEquality("finite({x·⊤∣1})", "finite({x⦂ℤ·⊤∣1})",
				"finite({x⦂BOOL·⊤∣1})");
	}

	/**
	 * Ensures that bound identifier declarations that bear different types make
	 * quantified expressions different.
	 * 
	 * Cf. bug #3574162: AST does not compare bound ident decl types
	 */
	@Test 
	public void testQuantifiedPredicate() {
		assertPredicateEquality("∀x·⊤", "∀x⦂ℤ·⊤", "∀x⦂BOOL·⊤");
	}

	private void assertPredicateEquality(String untypedImage,
			String typeZImage, String typeBImage) {
		assertFormulaEquality(parsePredicate(untypedImage),
				parsePredicate(untypedImage), parsePredicate(typeZImage),
				parsePredicate(typeZImage), parsePredicate(typeBImage));
	}

	private <T extends Formula<T>> void assertFormulaEquality(T untyped,
			T untyped2, T typeZ, T typeZ2, T typeB) {
		assertFormulaEqual(untyped, untyped2);
		assertFormulaNotEqual(untyped, typeZ);
		assertFormulaEqual(typeZ, typeZ2);
		assertFormulaNotEqual(typeZ, typeB);
	}

	private <T extends Formula<T>> void assertFormulaNotEqual(T f1, T f2) {
		assertFormulaEqual(f1, f2, false);
	}

	private <T extends Formula<T>> void assertFormulaEqual(T f1, T f2) {
		assertFormulaEqual(f1, f2, true);
	}

	private <T extends Formula<T>> void assertFormulaEqual(T f1, T f2,
			boolean equal) {
		final String msg = f1.toStringWithTypes() + " and "
				+ f2.toStringWithTypes();
		assertEquals(msg, equal, f1.equals(f2));
		assertEquals(msg, equal, f2.equals(f1));
	}

}
