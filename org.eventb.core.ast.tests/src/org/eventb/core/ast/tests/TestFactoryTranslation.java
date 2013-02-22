/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.math.BigInteger.ONE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.PredicateVariable.LEADING_SYMBOL;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtendedFormulas.barS;
import static org.eventb.core.ast.tests.ExtendedFormulas.fooS;
import static org.eventb.core.ast.tests.FastFactory.ff_extns;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mListCons;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.TestTypes.LIST_LIST_S;
import static org.eventb.core.ast.tests.TestTypes.powsetTypePrime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.Test;

/**
 * Acceptance tests for factory translation. We first test factory translation
 * on arbitrary formulas. Then, we test exhaustively all operators.
 * 
 * @author Vincent Monfort
 */
public class TestFactoryTranslation extends AbstractTests {

	/**
	 * Verifies formula translation between two factories on an arbitrary
	 * formula.
	 */
	private static void assertTranslation(FormulaFactory from, FormulaFactory to) {
		final Predicate pred = parsePredicate("1 = 2", from);
		assertTranslation(to, pred);
	}

	/**
	 * Verifies that formula translation to <code>ff_extns</code> fulfills its
	 * specification.
	 */
	private static void assertTranslation(Formula<?> f) {
		assertTranslation(ff_extns, f);
	}

	/**
	 * Verifies that the formula is translatable before translation.
	 */
	private static void assertIsTranslatable(FormulaFactory to, Formula<?> f) {
		assertTrue("The formula: " + f
				+ " was expected to be translatable with factory: " + to,
				f.isTranslatable(to));
	}

	/**
	 * Verifies that the formula is not translatable before translation try.
	 */
	private static void assertIsNotTranslatable(FormulaFactory to, Formula<?> f) {
		assertFalse("The formula: " + f
				+ " was NOT expected to be translatable with factory: " + to,
				f.isTranslatable(to));
	}

	/**
	 * Verifies that formula translation to a given factory fulfills its
	 * specification.
	 */
	private static void assertTranslation(FormulaFactory to, Formula<?> f) {
		assertFalse(f.getFactory() ==  to);
		assertIsTranslatable(to, f);
		final Formula<?> actual = f.translate(to);
		assertEquals(to, actual.getFactory());
		assertEquals(f, actual);
	}

	/**
	 * Compatible translation on a node from a factory with extensions to a
	 * factory with a subset of these extensions
	 */
	@Test
	public void testCompatibleTranslationToSubSet() {
		assertTranslation(ff_extns, ff);
	}

	/**
	 * Compatible translation on a node from a factory with extensions to a
	 * factory with a superset of these extensions
	 */
	@Test
	public void testCompatibleTranslationToSuperSet() {
		assertTranslation(ff, ff_extns);
	}

	/**
	 * Incompatible translation to a factory with an incompatible extension
	 * subset
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnExtension() {
		final IntegerType INTe = ff_extns.makeIntegerType();
		final Expression nil = mListCons(INTe);
		final Predicate unaryPred = ff_extns.makeSimplePredicate(KFINITE, nil,
				null);
		unaryPred.translate(ff);
	}

	/**
	 * Incompatible translation to a factory with an incompatible free
	 * identifier name
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnIdName() {
		final FreeIdentifier freeId = ff.makeFreeIdentifier("prime", null);
		assertIsNotTranslatable(ff_extns, freeId);
		freeId.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnFreeIdTypeName() {
		final FreeIdentifier freeId = LIST_FAC.makeFreeIdentifier("l", null,
				powsetTypePrime);
		assertIsNotTranslatable(ff_extns, freeId);
		freeId.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnFreeIdTypeExt() {
		final FreeIdentifier freeId = LIST_FAC.makeFreeIdentifier("l", null,
				LIST_LIST_S);
		assertIsNotTranslatable(ff, freeId);
		freeId.translate(ff);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnBoundIdDeclTypeName() {
		final BoundIdentDecl declId = LIST_FAC.makeBoundIdentDecl("l", null,
				powsetTypePrime);
		assertIsNotTranslatable(ff_extns, declId);
		declId.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnBoundIdDeclTypeExt() {
		final BoundIdentDecl declId = LIST_FAC.makeBoundIdentDecl("l", null,
				LIST_LIST_S);
		assertIsNotTranslatable(ff, declId);
		declId.translate(ff);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnBoundIdTypeName() {
		final BoundIdentifier declId = LIST_FAC.makeBoundIdentifier(0, null,
				powsetTypePrime);
		assertIsNotTranslatable(ff_extns, declId);
		declId.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnBoundIdTypeExt() {
		final BoundIdentifier declId = LIST_FAC.makeBoundIdentifier(0, null,
				LIST_LIST_S);
		assertIsNotTranslatable(ff, declId);
		declId.translate(ff);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnAtomicExprTypeName() {
		final AtomicExpression atomExpr = LIST_FAC.makeAtomicExpression(
				EMPTYSET, null, powsetTypePrime);
		assertIsNotTranslatable(ff_extns, atomExpr);
		atomExpr.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnAtomicExprTypeExt() {
		final AtomicExpression atomExpr = LIST_FAC.makeAtomicExpression(
				EMPTYSET, null, LIST_FAC.makePowerSetType(LIST_LIST_S));
		assertIsNotTranslatable(ff, atomExpr);
		atomExpr.translate(ff);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnSetExtTypeName() {
		final SetExtension setExt = LIST_FAC.makeEmptySetExtension(
				powsetTypePrime, null);
		assertIsNotTranslatable(ff_extns, setExt);
		setExt.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnSetExtTypeExt() {
		final SetExtension setExt = LIST_FAC.makeEmptySetExtension(
				LIST_FAC.makePowerSetType(LIST_LIST_S), null);
		assertIsNotTranslatable(ff, setExt);
		setExt.translate(ff);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnExtExprTypeName() {
		Type listType = LIST_FAC.makeParametricType(mList(powsetTypePrime),
				LIST_DT.getTypeConstructor());
		final ExtendedExpression extExpr = LIST_FAC.makeExtendedExpression(
				LIST_DT.getConstructor("NIL"), new ArrayList<Expression>(0),
				new ArrayList<Predicate>(0), null, listType);
		assertIsNotTranslatable(ff_extns, extExpr);
		extExpr.translate(ff_extns);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncompatibleTranslationOnExtExprTypeExt() {
		Type listType = LIST_FAC.makeParametricType(
				mList(LIST_FAC.makeGivenType("S")),
				LIST_DT.getTypeConstructor());
		final ExtendedExpression extExpr = LIST_FAC.makeExtendedExpression(
				LIST_DT.getConstructor("NIL"), new ArrayList<Expression>(0),
				new ArrayList<Predicate>(0), null, listType);
		assertIsNotTranslatable(ff, extExpr);
		extExpr.translate(ff);
	}

	/*----------------------------------------------------------------
	 *  TEST NORMAL BEHAVIOR ON ALL POSSIBLE FORMULAS
	 *----------------------------------------------------------------*/

	private static final GivenType tS = ff.makeGivenType("S");
	private static final GivenType tT = ff.makeGivenType("T");

	private static final FreeIdentifier iS = mFreeIdentifier("s", POW(tS));
	private static final FreeIdentifier iT = mFreeIdentifier("t", POW(tT));

	private static final BoundIdentDecl dS = mBoundIdentDecl("s'", POW(tS));
	private static final BoundIdentDecl dT = mBoundIdentDecl("t'", POW(tT));

	private static final Expression eS = mEmptySet(POW(tS));
	private static final Expression eT = mEmptySet(POW(tT));

	private static final Predicate P = mLiteralPredicate();

	private static final Predicate Q = mLiteralPredicate();

	private static final GivenType EFFtS = EFF.makeGivenType("S");
	private static final GivenType EFFtT = EFF.makeGivenType("T");

	private static final Expression EFFeS = EFF.makeEmptySet(POW(EFFtS), null);
	private static final Expression EFFeT = EFF.makeEmptySet(POW(EFFtT), null);

	private static final Predicate EFFP = EFF.makeLiteralPredicate(
			Formula.BTRUE, null);
	private static final Predicate EFFQ = EFF.makeLiteralPredicate(
			Formula.BTRUE, null);

	/*----------------------------------------------------------------
	 *  NO POSSIBLE TRANSLATION OF ASSIGNEMENTS
	 *----------------------------------------------------------------*/

	@Test(expected = UnsupportedOperationException.class)
	public void becomesEqualTo() {
		ff.makeBecomesEqualTo(mList(iS), mList(eS), null).translate(ff_extns);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void becomesMember() {
		ff.makeBecomesMemberOf(iS, eS, null).translate(ff_extns);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void becomesSuchThat() {
		ff.makeBecomesSuchThat(mList(iS, iT), mList(dS, dT), P, null)
				.translate(ff_extns);
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF IDENTIFIER OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void boundIdentDecl() {
		assertTranslation(ff.makeBoundIdentDecl("x", null));
	}

	@Test
	public void freeIdentifier() {
		assertTranslation(ff.makeFreeIdentifier("s", null, tS));
	}

	@Test
	public void boundIdentifier() {
		assertTranslation(ff.makeBoundIdentifier(0, null));
	}

	@Test
	public void predicateVariable() {
		assertTranslation(ff.makePredicateVariable(LEADING_SYMBOL + "p", null));
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF REGULAR PREDICATE OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void associativePredicate() {
		assertTranslation(ff.makeAssociativePredicate(LOR, mList(P, Q), null));
	}

	@Test
	public void binaryPredicate() {
		assertTranslation(ff.makeBinaryPredicate(LIMP, P, Q, null));
	}

	@Test
	public void literalPredicate() {
		assertTranslation(ff.makeLiteralPredicate(Formula.BTRUE, null));
	}

	@Test
	public void multiplePredicate() {
		assertTranslation(ff.makeMultiplePredicate(KPARTITION, mList(eS), null));
	}

	@Test
	public void quantifiedPredicate() {
		assertTranslation(ff
				.makeQuantifiedPredicate(FORALL, mList(dS), P, null));
	}

	@Test
	public void relationalPredicate() {
		assertTranslation(ff.makeRelationalPredicate(EQUAL, eT, eS, null));
	}

	@Test
	public void simplePredicate() {
		assertTranslation(ff.makeSimplePredicate(KFINITE, eS, null));
	}

	@Test
	public void unaryPredicate_NullChild() {
		assertTranslation(ff.makeUnaryPredicate(NOT, P, null));
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF REGULAR EXPRESSION OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void associativeExpression_OneChild() {
		assertTranslation(ff.makeAssociativeExpression(BUNION, mList(eS, eT),
				null));
	}

	@Test
	public void atomicExpression() {
		assertTranslation(ff.makeAtomicExpression(KID_GEN, null, null));
	}

	@Test
	public void emptySet() {
		assertTranslation(ff.makeEmptySet(POW(tS), null));
	}

	@Test
	public void binaryExpression() {
		assertTranslation(ff.makeBinaryExpression(MAPSTO, eS, eS, null));
	}

	@Test
	public void boolExpression() {
		assertTranslation(ff.makeBoolExpression(P, null));
	}

	@Test
	public void integerLiteral() {
		assertTranslation(ff.makeIntegerLiteral(ONE, null));
	}

	@Test
	public void quantifiedExpression() {
		assertTranslation(ff.makeQuantifiedExpression(CSET, mList(dS), P, eS,
				null, Explicit));
	}

	@Test
	public void setExtension() {
		assertTranslation(ff.makeSetExtension(mList(eS), null));
	}

	@Test
	public void unaryExpression() {
		assertTranslation(ff.makeUnaryExpression(KCARD, eS, null));
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF EXTENSION OBJECTS
	 *----------------------------------------------------------------*/
	private static final FormulaFactory EFFPlus;

	static {
		final Set<IFormulaExtension> extns = EFF.getExtensions();
		extns.addAll(ff_extns.getExtensions());
		EFFPlus = FormulaFactory.getInstance(extns);
	}

	@Test
	public void extendedPredicate() {
		assertTranslation(
				EFFPlus,
				EFF.makeExtendedPredicate(fooS, mList(EFFeS, EFFeT),
						mList(EFFP, EFFQ), null));
	}

	@Test
	public void extendedExpression() {
		assertTranslation(
				EFFPlus,
				EFF.makeExtendedExpression(barS, mList(EFFeS, EFFeS),
						mList(EFFP, EFFP), null));
	}

}
