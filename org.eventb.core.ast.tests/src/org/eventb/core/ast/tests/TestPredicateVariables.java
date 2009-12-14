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
package org.eventb.core.ast.tests;

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
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mPredicateVariable;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.PredicateVariable;

/**
 * Ensures that the set of predicate variables occurring in a formula is
 * computed correctly.
 * 
 * @author Nicolas Beauger
 */
public class TestPredicateVariables extends AbstractTests {

	private static <T> Set<T> mSet(T... objects) {
		return new HashSet<T>(Arrays.asList(objects));
	}

	private static <T> T[] mArray(T... objects) {
		return objects;
	}

	private static void assertPredicateVariables(Formula<?> formula,
			PredicateVariable... expecteds) {
		final Set<PredicateVariable> expected = mSet(expecteds);
		assertEquals("Duplicates in expecteds", expected.size(),
				expecteds.length);
		final PredicateVariable[] actuals = formula.getPredicateVariables();
		final Set<PredicateVariable> actual = mSet(actuals);
		assertEquals("Duplicates in actuals", actual.size(), actuals.length);
		assertEquals(formula.toString(), expected, actual);
	}

	private static final PredicateVariable PV_P = mPredicateVariable("$P");
	private static final PredicateVariable PV_P2 = mPredicateVariable("$P");
	private static final PredicateVariable PV_Q = mPredicateVariable("$Q");
	private static final PredicateVariable PV_R = mPredicateVariable("$R");

	private static final BoolExpression BOOL_P = mBoolExpression(PV_P);
	private static final BoolExpression BOOL_Q = mBoolExpression(PV_Q);

	private static final FreeIdentifier ID_X = mFreeIdentifier("x");
	private static final FreeIdentifier ID_Y = mFreeIdentifier("y");

	private static final BoundIdentDecl BID_X = mBoundIdentDecl("x");
	private static final BoundIdentDecl[] ARR_X = mArray(BID_X);

	// Assignment

	public void testBecomesEqualTo() throws Exception {
		assertPredicateVariables(mBecomesEqualTo(ID_X, BOOL_P), PV_P);
		assertPredicateVariables(mBecomesEqualTo(mArray(ID_X, ID_Y), //
				mArray(BOOL_P, BOOL_Q)), //
				PV_P, PV_Q);
	}

	public void testBecomesMemberOf() throws Exception {
		assertPredicateVariables(mBecomesMemberOf(ID_X, mSetExtension(BOOL_P)), //
				PV_P);
	}

	public void testBecomesSuchThat() throws Exception {
		assertPredicateVariables(mBecomesSuchThat(mArray(ID_X), PV_P), //
				PV_P);
	}

	// BoundIdentDecl

	public void testBoundIdentDecl() throws Exception {
		assertPredicateVariables(BID_X);
	}

	// Expression

	public void testAssociativeExpression() throws Exception {
		assertPredicateVariables(mAssociativeExpression(BOOL_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testAtomicExpression() throws Exception {
		assertPredicateVariables(mAtomicExpression());
	}

	public void testBinaryExpression() throws Exception {
		assertPredicateVariables(mBinaryExpression(BOOL_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testBoolExpression() throws Exception {
		assertPredicateVariables(BOOL_P, PV_P);
	}

	public void testBoundIdentifier() throws Exception {
		assertPredicateVariables(mBoundIdentifier(0));
	}

	public void testFreeIdentifier() throws Exception {
		assertPredicateVariables(mFreeIdentifier("x"));
	}

	public void testIntegerLiteral() throws Exception {
		assertPredicateVariables(mIntegerLiteral());
	}

	public void testQuantifiedExpression() throws Exception {
		assertPredicateVariables(mQuantifiedExpression(ARR_X, PV_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testSetExtension() throws Exception {
		assertPredicateVariables(mSetExtension(BOOL_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testUnaryExpression() throws Exception {
		assertPredicateVariables(mUnaryExpression(BOOL_P), //
				PV_P);
	}

	// Predicate

	public void testAssociativePredicate() throws Exception {
		assertPredicateVariables(mAssociativePredicate(PV_P, PV_Q), //
				PV_P, PV_Q);
	}

	public void testBinaryPredicate() throws Exception {
		assertPredicateVariables(mBinaryPredicate(PV_P, PV_Q), //
				PV_P, PV_Q);
	}

	public void testLiteralPredicate() throws Exception {
		assertPredicateVariables(mLiteralPredicate());
	}

	public void testMultiplePredicate() throws Exception {
		assertPredicateVariables(mMultiplePredicate(BOOL_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testPredicateVariable() throws Exception {
		assertPredicateVariables(PV_P, PV_P);
	}

	public void testQuantifiedPredicate() throws Exception {
		assertPredicateVariables(mQuantifiedPredicate(ARR_X, PV_P), PV_P);
	}

	public void testRelationalPredicate() throws Exception {
		assertPredicateVariables(mRelationalPredicate(BOOL_P, BOOL_Q), //
				PV_P, PV_Q);
	}

	public void testSimplePredicate() throws Exception {
		assertPredicateVariables(mSimplePredicate(BOOL_P), PV_P);
	}

	public void testUnaryPredicate() throws Exception {
		assertPredicateVariables(mUnaryPredicate(PV_P), PV_P);
	}

	public void testComplexCases() throws Exception {
		assertPredicateVariables(mAssociativePredicate(PV_P, PV_Q, PV_R), //
				PV_P, PV_Q, PV_R);
		assertPredicateVariables(mAssociativePredicate(PV_P, PV_Q, PV_P), //
				PV_P, PV_Q);
		assertPredicateVariables(mAssociativePredicate(PV_P, PV_P2), //
				PV_P);
	}

}
