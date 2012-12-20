/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mId;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mPrj1;
import static org.eventb.core.ast.tests.FastFactory.mPrj2;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.junit.Test;

/**
 * Tests for method {@link Formula#getGivenTypes()}.
 * 
 * @author Laurent Voisin
 */
public class TestGivenTypes extends AbstractTests {

	private static final BooleanType BOOL = ff.makeBooleanType();
	private static final IntegerType INT = ff.makeIntegerType();
	private static final GivenType tS = ff.makeGivenType("S");
	private static final GivenType tT = ff.makeGivenType("T");
	private static final GivenType tU = ff.makeGivenType("U");

	/**
	 * Returns a relation from integer to integer that hides the given type.
	 * 
	 * @param type
	 *            an arbitrary type
	 * @return an integer relation hiding the given type
	 */
	private static Expression mHidingRelation(Type type) {
		Expression left = mEmptySet(REL(INT, type));
		Expression right = mEmptySet(REL(type, INT));
		return mAssociativeExpression(FCOMP, left, right);
	}
	
	/**
	 * Returns a relation from integer to integer that hides the given type and
	 * contains free identifiers with the given names.
	 * 
	 * @param type
	 *            an arbitrary type
	 * @return an integer relation hiding the given type
	 */
	private static Expression mHidingRelation(String lName, String rName,
			Type type) {

		Expression left = mFreeIdentifier(lName, REL(INT, type));
		Expression right = mFreeIdentifier(rName, REL(type, INT));
		return mAssociativeExpression(FCOMP, left, right);
	}
	
	
	private static final Expression eS = mEmptySet(POW(tS));
	private static final Expression eT = mEmptySet(POW(tT));
	private static final Expression eU = mEmptySet(POW(tU));
	
	private static final Expression idSS = mId(REL(tS, tS));
	private static final Expression prj1ST = mPrj1(REL(CPROD(tS, tT), tS));
	private static final Expression prj2ST = mPrj2(REL(CPROD(tS, tT), tT));

	private static final ParametricType LIST_S_TYPE = LIST_FAC
	.makeParametricType(Collections.<Type> singletonList(tS),
			EXT_LIST);
	private static Expression[] NO_EXPR = new Expression[0];
	private static Predicate[] NO_PRED = new Predicate[0];
	private static final Expression nilListS = LIST_FAC.makeExtendedExpression(
			EXT_NIL, NO_EXPR, NO_PRED, null, LIST_S_TYPE);

	private static final Predicate peS = mRelationalPredicate(EQUAL, eS, eS);
	private static final Predicate peT = mRelationalPredicate(EQUAL, eT, eT);
	private static final Predicate peU = mRelationalPredicate(EQUAL, eU, eU);
	
	private static final Expression heS = mHidingRelation(tS);
	private static final Expression heT = mHidingRelation(tT);
	private static final Expression heU = mHidingRelation(tU);

	private static final Expression hiS = mHidingRelation("a", "b", tS);
	private static final Expression hiT = mHidingRelation("c", "d", tT);
	private static final Expression hiU = mHidingRelation("e", "f", tU);

	private static final FreeIdentifier iS = mFreeIdentifier("s", POW(tS));
	private static final FreeIdentifier iT = mFreeIdentifier("t", POW(tT));
	private static final FreeIdentifier iU = mFreeIdentifier("u", POW(tU));
	
	private static final Predicate piS = mRelationalPredicate(EQUAL, iS, iS);
	private static final Predicate piT = mRelationalPredicate(EQUAL, iT, iT);
	private static final Predicate piU = mRelationalPredicate(EQUAL, iU, iU);
	
	private <T extends Formula<T>> void doTest(Formula<T> formula,
			GivenType... types) {

		assertTrue("Input formula is not type-checked", formula.isTypeChecked());
		Set<GivenType> expected = new HashSet<GivenType>(Arrays.asList(types));
		Set<GivenType> actual = formula.getGivenTypes();
		assertEquals("Wrong set of given types", expected, actual);
	}

	/**
	 * Ensures that given types are propagated through an associative
	 * expression.
	 */
	@Test 
	public void testAssociativeExpression() {
		doTest(mAssociativeExpression(BUNION, heS, heT), tS, tT);
		doTest(mAssociativeExpression(BUNION, hiS, hiT), tS, tT);
		doTest(mAssociativeExpression(BUNION, heS, heT, heU), tS, tT, tU);
		doTest(mAssociativeExpression(BUNION, hiS, hiT, hiU), tS, tT, tU);
	}
	
	/**
	 * Ensures that given types are propagated through an associative predicate.
	 */
	@Test 
	public void testAssociativePredicate() {
		doTest(mAssociativePredicate(LAND, peS, peT), tS, tT);
		doTest(mAssociativePredicate(LAND, piS, piT), tS, tT);
		doTest(mAssociativePredicate(LAND, peS, peT, peU), tS, tT, tU);
		doTest(mAssociativePredicate(LAND, piS, piT, piU), tS, tT, tU);
	}

	/**
	 * Ensures that given types are propagated from an atomic expression.
	 */
	@Test 
	public void testAtomicExpression() {
		doTest(eS, tS);
		doTest(idSS, tS);
		doTest(prj1ST, tS, tT);
		doTest(prj2ST, tS, tT);
		doTest(nilListS, tS);
	}

	/**
	 * Ensures that given types in the right-hand side of a becomes equal
	 * to are propagated.
	 */
	@Test 
	public void testBecomesEqualTo() {
		final FreeIdentifier v = mFreeIdentifier("v", REL(INT, INT));
		final FreeIdentifier w = mFreeIdentifier("w", REL(INT, INT));
		final FreeIdentifier t = mFreeIdentifier("t", REL(INT, INT));
		
		doTest(mBecomesEqualTo(v, heS), tS);
		doTest(mBecomesEqualTo(v, hiS), tS);
		doTest(mBecomesEqualTo(mList(v, w), mList(heS, heT)), tS, tT);
		doTest(mBecomesEqualTo(mList(v, w), mList(hiS, hiT)), tS, tT);
		doTest(mBecomesEqualTo(mList(v, w, t), mList(heS, heT, heU)), tS, tT, tU);
		doTest(mBecomesEqualTo(mList(v, w, t), mList(hiS, hiT, hiU)), tS, tT, tU);
	}
	
	/**
	 * Ensures that given types in the right-hand side of a becomes
	 * member of are propagated.
	 */
	@Test 
	public void testBecomesMemberOf() {
		final FreeIdentifier v = mFreeIdentifier("v", CPROD(INT, INT));

		doTest(mBecomesMemberOf(v, heS), tS);
		doTest(mBecomesMemberOf(v, hiS), tS);
	}
	
	/**
	 * Ensures that given types in the right-hand side of a becomes
	 * such that are propagated.
	 */
	@Test 
	public void testBecomesSuchThat() {
		final FreeIdentifier v = mFreeIdentifier("v", INT);

		doTest(mBecomesSuchThat(mList(v), peS), tS);
		doTest(mBecomesSuchThat(mList(v), piS), tS);
		doTest(mBecomesSuchThat(mList(iS), piT), tS, tT);
	}
	
	/**
	 * Ensures that given types in a binary expression are propagated.
	 */
	@Test 
	public void testBinaryExpression() {
		doTest(mBinaryExpression(CPROD, eS, eT), tS, tT);
		doTest(mBinaryExpression(CPROD, iS, iT), tS, tT);
	}
	
	/**
	 * Ensures that given types in a binary predicate are propagated.
	 */
	@Test 
	public void testBinaryPredicate() {
		doTest(mBinaryPredicate(LIMP, peS, peT), tS, tT);
		doTest(mBinaryPredicate(LIMP, piS, piT), tS, tT);
	}
	
	/**
	 * Ensures that given types in a bool expression are propagated.
	 */
	@Test 
	public void testBoolExpression() {
		doTest(mBoolExpression(peS), tS);
		doTest(mBoolExpression(piS), tS);
	}
	
	/**
	 * Ensures that given types in a bound identifier declaration are
	 * propagated.
	 */
	@Test 
	public void testBoundIdentDecl() {
		doTest(mBoundIdentDecl("x", tS), tS);
	}
	
	/**
	 * Ensures that given types in a bound identifier are propagated.
	 */
	@Test 
	public void testBoundIdentifier() {
		doTest(mBoundIdentifier(0, tS), tS);
	}
	
	/**
	 * Ensures that given types in a free identifier are propagated.
	 */
	@Test 
	public void testFreeIdentifier() {
		doTest(iS, tS);
	}
	
	/**
	 * Ensures that given types in a quantified expression are propagated.
	 */
	@Test 
	public void testQuantifiedExpression() {
		final BoundIdentDecl[] decls = mList(mBoundIdentDecl("x", tS));
		final BoundIdentifier use = mBoundIdentifier(0, tS);
		doTest(mQuantifiedExpression(CSET, Explicit, decls, peT, use), tS, tT);
		doTest(mQuantifiedExpression(CSET, Explicit, decls, piT, use), tS, tT);
	}
	
	/**
	 * Ensures that given types in a quantified predicate are propagated.
	 */
	@Test 
	public void testQuantifiedPredicate() {
		final BoundIdentDecl[] decls = mList(mBoundIdentDecl("x", tS));
		final BoundIdentifier use = mBoundIdentifier(0, tS);
		final Predicate pred = mAssociativePredicate(LAND,
				mRelationalPredicate(IN, use, eS),
				peT,
				piU
		);
		doTest(mQuantifiedPredicate(EXISTS, decls, pred), tS, tT, tU);
	}
	
	/**
	 * Ensures that given types in a relational predicate are propagated.
	 */
	@Test 
	public void testRelationalPredicate() {
		doTest(peS, tS);
		doTest(piS, tS);
	}
	
	/**
	 * Ensures that given types in a set extension are propagated.
	 */
	@Test 
	public void testSetExtension() {
		// First test is with an empty set extension: needs type-checking
		final Expression empty = mSetExtension();
		final RelationalPredicate pred = mRelationalPredicate(EQUAL, empty, eS);
		typeCheck(pred);
		doTest(empty, tS);
		
		doTest(mSetExtension(heS), tS);
		doTest(mSetExtension(hiS), tS);
		doTest(mSetExtension(heS, heT), tS, tT);
		doTest(mSetExtension(hiS, hiT), tS, tT);
		doTest(mSetExtension(heS, heT, heU), tS, tT, tU);
		doTest(mSetExtension(hiS, hiT, hiU), tS, tT, tU);
	}
	
	/**
	 * Ensures that given types in a simple predicate are propagated.
	 */
	@Test 
	public void testSimplePredicate() {
		doTest(mSimplePredicate(eS), tS);
		doTest(mSimplePredicate(iS), tS);
	}
	
	/**
	 * Ensures that given types in a unary expression are propagated.
	 */
	@Test 
	public void testUnaryExpression() {
		doTest(mUnaryExpression(POW, eS), tS);
		doTest(mUnaryExpression(POW, iS), tS);
	}
	
	/**
	 * Ensures that given types in a unary predicate are propagated.
	 */
	@Test 
	public void testUnaryPredicate() {
		doTest(mUnaryPredicate(peS), tS);
		doTest(mUnaryPredicate(piS), tS);
	}
	
	/**
	 * Ensures that given types in a multiple predicate are propagated.
	 */
	@Test 
	public void testMultiplePredicate() {
		doTest(FastFactory.mMultiplePredicate(eS), tS);
		doTest(FastFactory.mMultiplePredicate(eS, eS), tS);
		doTest(FastFactory.mMultiplePredicate(eS, eS, eS), tS);
	}
	
	/**
	 * Ensures that given types are extracted from types.
	 */
	@Test 
	public void testTypes() {
		doTest(mFreeIdentifier("x", BOOL));
		doTest(mFreeIdentifier("x", tS), tS);
		doTest(mFreeIdentifier("x", INT));
		doTest(mFreeIdentifier("x", POW(tS)), tS);
		doTest(mFreeIdentifier("x", CPROD(tS, tT)), tS, tT);
		doTest(mFreeIdentifier("x", LIST_S_TYPE), tS);
	}
	
}
