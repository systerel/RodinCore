/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.PredicateVariable.LEADING_SYMBOL;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtendedFormulas.barS;
import static org.eventb.core.ast.tests.ExtendedFormulas.fooS;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.TestGenParser.MONEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for factory methods which are not fully tested elsewhere.
 * 
 * @author Laurent Voisin
 */
public class TestFormulaFactory extends AbstractTests {

	private static final GivenType tS = ff.makeGivenType("S");
	private static final GivenType tT = ff.makeGivenType("T");

	private static final FreeIdentifier iS = mFreeIdentifier("s", POW(tS));

	private static final BoundIdentDecl dS = mBoundIdentDecl("s'", POW(tS));
	private static final BoundIdentDecl dT = mBoundIdentDecl("t'", POW(tT));

	private static final Expression eS = mEmptySet(POW(tS));
	private static final Expression eT = mEmptySet(POW(tT));

	private static final Predicate P = mLiteralPredicate();

	private static final String BAD_NAME = "bad-name";

	private static final String PRED_VAR_NAME = PredicateVariable.LEADING_SYMBOL
			+ "P";

	/**
	 * Ensures that method isValidIdentifierName() takes into account the
	 * version of the mathematical language supported by the formula factory
	 * instance.
	 */
	@Test
	public void validIdentifierName() throws Exception {
		final String validName = "foo";
		assertTrue(ffV1.isValidIdentifierName(validName));
		assertTrue(ff.isValidIdentifierName(validName));
		assertTrue(LIST_FAC.isValidIdentifierName(validName));

		final String nameInV1Only = "partition";
		assertTrue(ffV1.isValidIdentifierName(nameInV1Only));
		assertFalse(ff.isValidIdentifierName(nameInV1Only));
		assertFalse(LIST_FAC.isValidIdentifierName(nameInV1Only));

		final String typeConstructorName = "List";
		assertTrue(ffV1.isValidIdentifierName(typeConstructorName));
		assertTrue(ff.isValidIdentifierName(typeConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(typeConstructorName));

		final String valueConstructorName = "cons";
		assertTrue(ffV1.isValidIdentifierName(valueConstructorName));
		assertTrue(ff.isValidIdentifierName(valueConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(valueConstructorName));

		final String destructorName = "head";
		assertTrue(ffV1.isValidIdentifierName(destructorName));
		assertTrue(ff.isValidIdentifierName(destructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(destructorName));
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF TYPE OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = IllegalArgumentException.class)
	public void givenType_InvalidIdentifierName() {
		ff.makeGivenType(BAD_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_UnknownExtension() {
		ff.makeParametricType(mList(tS), new UnknownExtension());
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_InvalidExtension() {
		ff.makeParametricType(mList(tS), EXT_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_NotATypeConstructor() {
		final FormulaFactory extFac = getInstance();
		extFac.makeParametricType(mList(tS), MONEY);
	}

	@Ignore("Known bug")
	@Test(expected = IllegalArgumentException.class)
	public void parametricType_WrongNumberOfParameter() {
		LIST_FAC.makeParametricType(mList(tS, tT), EXT_LIST);
	}

	@Test(expected = NullPointerException.class)
	public void parametricType_NullParameters() {
		final Type[] typeParams = null;
		LIST_FAC.makeParametricType(typeParams, EXT_LIST);
	}

	@Test(expected = NullPointerException.class)
	public void parametricType_NullInParameter() {
		LIST_FAC.makeParametricType(new Type[] { null }, EXT_LIST);
	}

	@Test
	public void parametricType_ArrayParameter() {
		final Type[] typeParams = { tS };
		assertArrayProtected(LIST_FAC.makeParametricType(typeParams, EXT_LIST),
				typeParams);
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF ASSIGNMENT OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_singleNullLHS() {
		ff.makeBecomesEqualTo(null, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_singleNullRHS() {
		ff.makeBecomesEqualTo(iS, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void becomesEqualTo_emptyArrays() {
		ff.makeBecomesEqualTo(NO_IDS, NO_EXPRS, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void becomesEqualTo_DifferentSizes() {
		ff.makeBecomesEqualTo(mList(iS), mList(eS, eT), null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_NullLHS() {
		ff.makeBecomesEqualTo(null, mList(eS), null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_NullInLHS() {
		final FreeIdentifier[] left = { null };
		ff.makeBecomesEqualTo(left, mList(eS), null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_NullRHS() {
		ff.makeBecomesEqualTo(mList(iS), null, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesEqualTo_NullInRHS() {
		final Expression[] right = { null };
		ff.makeBecomesEqualTo(mList(iS), right, null);
	}

	@Test
	public void becomesEqualTo_ArrayParameterLHS() {
		final FreeIdentifier[] idents = { iS };
		assertArrayProtected(ff.makeBecomesEqualTo(idents, mList(eS), null),
				idents);
	}

	@Test
	public void becomesEqualTo_ArrayParameterRHS() {
		final Expression[] exprs = { eS };
		assertArrayProtected(ff.makeBecomesEqualTo(mList(iS), exprs, null),
				exprs);
	}

	@Test(expected = NullPointerException.class)
	public void becomesMemberOf_NullLHS() {
		ff.makeBecomesMemberOf(null, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesMemberOf_NullRHS() {
		ff.makeBecomesMemberOf(iS, null, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesSuchThat_singleNullLHS() {
		ff.makeBecomesSuchThat(null, dS, P, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesSuchThat_singleNullRHS() {
		ff.makeBecomesSuchThat(iS, dS, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void becomesSuchThat_emptyArrays() {
		ff.makeBecomesSuchThat(NO_IDS, NO_BIDS, P, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void becomesSuchThat_DifferentSizes() {
		ff.makeBecomesSuchThat(mList(iS), mList(dS, dT), P, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesSuchThat_NullLHS() {
		ff.makeBecomesSuchThat(null, mList(dS), P, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesSuchThat_NullInLHS() {
		final FreeIdentifier[] left = { null };
		ff.makeBecomesSuchThat(left, mList(dS), P, null);
	}

	@Test(expected = NullPointerException.class)
	public void becomesSuchThat_NullRHS() {
		ff.makeBecomesSuchThat(mList(iS), mList(dS), null, null);
	}

	@Test
	public void becomesSuchThat_ArrayParameterLHS() {
		final FreeIdentifier[] idents = { iS };
		assertArrayProtected(
				ff.makeBecomesSuchThat(idents, mList(dS), P, null),//
				idents);
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF IDENTIFIER OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = NullPointerException.class)
	public void boundIdentDecl_NullName() {
		ff.makeBoundIdentDecl(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void boundIdentDecl_InvalidName() {
		ff.makeBoundIdentDecl(BAD_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void boundIdentDecl_PredicateVariable() {
		ff.makeBoundIdentDecl(PRED_VAR_NAME, null);
	}

	@Test(expected = NullPointerException.class)
	public void freeIdentifier_NullName() {
		ff.makeFreeIdentifier(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void freeIdentifier_InvalidName() {
		ff.makeFreeIdentifier(BAD_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void freeIdentifier_PredicateVariable() {
		ff.makeFreeIdentifier(PRED_VAR_NAME, null);
	}

	// Type of free identifier is tested in TestTypedConstructor

	@Test(expected = IllegalArgumentException.class)
	public void boundIdentifier_InvalidIndex() {
		ff.makeBoundIdentifier(-1, null);
	}

	@Test(expected = NullPointerException.class)
	public void predicateVariable_NullName() {
		ff.makePredicateVariable(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateVariable_NoPrefix() {
		ff.makePredicateVariable("P", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateVariable_NoSuffix() {
		ff.makePredicateVariable(LEADING_SYMBOL, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateVariable_InvalidSuffix() {
		ff.makePredicateVariable(LEADING_SYMBOL + BAD_NAME, null);
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF REGULAR PREDICATE OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = IllegalArgumentException.class)
	public void associativePredicate_InvalidTag() {
		ff.makeAssociativePredicate(FREE_IDENT, mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void associativePredicate_NullChildren() {
		final Predicate[] children = null;
		ff.makeAssociativePredicate(LOR, children, null);
	}

	@Test(expected = NullPointerException.class)
	public void associativePredicate_NullChild() {
		ff.makeAssociativePredicate(LOR, mList(P, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void associativePredicate_OneChild() {
		ff.makeAssociativePredicate(LOR, mList(P), null);
	}

	@Test
	public void associativePredicate_ArrayParameter() {
		final Predicate[] children = { P, P };
		assertArrayProtected(ff.makeAssociativePredicate(LOR, children, null),
				children);
	}

	@Test(expected = IllegalArgumentException.class)
	public void binaryPredicate_InvalidTag() {
		ff.makeBinaryPredicate(FREE_IDENT, P, P, null);
	}

	@Test(expected = NullPointerException.class)
	public void binaryPredicate_NullLeft() {
		ff.makeBinaryPredicate(LIMP, null, P, null);
	}

	@Test(expected = NullPointerException.class)
	public void binaryPredicate_NullRight() {
		ff.makeBinaryPredicate(LIMP, P, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void literalPredicate_InvalidTag() {
		ff.makeLiteralPredicate(FREE_IDENT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void multiplePredicate_InvalidTag() {
		ff.makeMultiplePredicate(FREE_IDENT, mList(eS), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void multiplePredicate_NotInV1() {
		ffV1.makeMultiplePredicate(KPARTITION, mList(eS), null);
	}

	@Test(expected = NullPointerException.class)
	public void multiplePredicate_NullChildren() {
		final Expression[] children = null;
		ff.makeMultiplePredicate(KPARTITION, children, null);
	}

	@Test(expected = NullPointerException.class)
	public void multiplePredicate_NullChild() {
		ff.makeMultiplePredicate(KPARTITION, mList(eS, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void multiplePredicate_NoChild() {
		ff.makeMultiplePredicate(KPARTITION, NO_EXPRS, null);
	}

	@Test
	public void multiplePredicate_ArrayParameter() {
		final Expression[] children = { eS, eS };
		assertArrayProtected(
				ff.makeMultiplePredicate(KPARTITION, children, null),//
				children);
	}

	@Test(expected = IllegalArgumentException.class)
	public void quantifiedPredicate_InvalidTag() {
		ff.makeQuantifiedPredicate(FREE_IDENT, mList(dS), P, null);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedPredicate_NullDecls() {
		final BoundIdentDecl[] decls = null;
		ff.makeQuantifiedPredicate(FORALL, decls, P, null);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedPredicate_NullInDecls() {
		ff.makeQuantifiedPredicate(FORALL, mList(dS, null), P, null);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedPredicate_NullPredicate() {
		ff.makeQuantifiedPredicate(FORALL, mList(dS), null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void quantifiedPredicate_NoChild() {
		ff.makeQuantifiedPredicate(FORALL, NO_BIDS, P, null);
	}

	@Test
	public void quantifiedPredicate_ArrayParameter() {
		final BoundIdentDecl[] decls = { dS };
		assertArrayProtected(
				ff.makeQuantifiedPredicate(FORALL, mList(dS), P, null),//
				decls);
	}

	@Test(expected = IllegalArgumentException.class)
	public void relationalPredicate_InvalidTag() {
		ff.makeRelationalPredicate(FREE_IDENT, eS, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void relationalPredicate_NullLeft() {
		ff.makeRelationalPredicate(EQUAL, null, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void relationalPredicate_NullRight() {
		ff.makeRelationalPredicate(EQUAL, eS, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void simplePredicate_InvalidTag() {
		ff.makeSimplePredicate(FREE_IDENT, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void simplePredicate_NullChild() {
		ff.makeSimplePredicate(KFINITE, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unaryPredicate_InvalidTag() {
		ff.makeUnaryPredicate(FREE_IDENT, P, null);
	}

	@Test(expected = NullPointerException.class)
	public void unaryPredicate_NullChild() {
		ff.makeUnaryPredicate(NOT, null, null);
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF REGULAR EXPRESSION OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = IllegalArgumentException.class)
	public void associativeExpression_InvalidTag() {
		ff.makeAssociativeExpression(FREE_IDENT, mList(eS, eS), null);
	}

	@Test(expected = NullPointerException.class)
	public void associativeExpression_NullChildren() {
		final Expression[] children = null;
		ff.makeAssociativeExpression(BUNION, children, null);
	}

	@Test(expected = NullPointerException.class)
	public void associativeExpression_NullChild() {
		ff.makeAssociativeExpression(BUNION, mList(eS, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void associativeExpression_OneChild() {
		ff.makeAssociativeExpression(BUNION, mList(eS), null);
	}

	@Test
	public void associativeExpression_ArrayParameter() {
		final Expression[] children = { eS, eS };
		assertArrayProtected(
				ff.makeAssociativeExpression(BUNION, children, null),//
				children);
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_InvalidTag() {
		ff.makeAtomicExpression(FREE_IDENT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_Prj1NotInV1() {
		ffV1.makeAtomicExpression(KPRJ1_GEN, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_Prj2NotInV1() {
		ffV1.makeAtomicExpression(KPRJ2_GEN, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_IdNotInV1() {
		ffV1.makeAtomicExpression(KID_GEN, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void emptySet_InvalidType() {
		ff.makeEmptySet(tS, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_InvalidTypeForPrj1() {
		ff.makeAtomicExpression(KPRJ1_GEN, null, REL(CPROD(tS, tT), tT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_InvalidTypeForPrj2() {
		ff.makeAtomicExpression(KPRJ2_GEN, null, REL(CPROD(tS, tT), tS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void atomicExpression_InvalidTypeForId() {
		ff.makeAtomicExpression(KID_GEN, null, REL(tS, tT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void binaryExpression_InvalidTag() {
		ff.makeBinaryExpression(FREE_IDENT, eS, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void binaryExpression_NullLeft() {
		ff.makeBinaryExpression(MAPSTO, null, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void binaryExpression_NullRight() {
		ff.makeBinaryExpression(MAPSTO, eS, null, null);
	}

	@Test(expected = NullPointerException.class)
	public void boolExpression_NullChild() {
		ff.makeBoolExpression(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void integerLiteral_NullChild() {
		ff.makeIntegerLiteral(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void quantifiedExpression_InvalidTag() {
		ff.makeQuantifiedExpression(FREE_IDENT, mList(dS), P, eS, null,
				Explicit);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedExpression_NullDecls() {
		final BoundIdentDecl[] decls = null;
		ff.makeQuantifiedExpression(CSET, decls, P, eS, null, Explicit);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedExpression_NullInDecls() {
		ff.makeQuantifiedExpression(CSET, mList(dS, null), P, eS, null,
				Explicit);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedExpression_NullPredicate() {
		ff.makeQuantifiedExpression(CSET, mList(dS), null, eS, null, Explicit);
	}

	@Test(expected = NullPointerException.class)
	public void quantifiedExpression_NullExpression() {
		ff.makeQuantifiedExpression(CSET, mList(dS), P, null, null, Explicit);
	}

	@Test(expected = IllegalArgumentException.class)
	public void quantifiedExpression_NoChild() {
		ff.makeQuantifiedExpression(CSET, NO_BIDS, P, eS, null, Explicit);
	}

	@Test
	public void quantifiedExpression_ArrayParameter() {
		final BoundIdentDecl[] decls = { dS };
		assertArrayProtected(
				ff.makeQuantifiedExpression(CSET, decls, P, eS, null, Explicit),//
				decls);
	}

	@Test
	public void quantifiedExpression_ImplicitToExplicit() {
		final QuantifiedExpression expr = ff.makeQuantifiedExpression(CSET,
				mList(dS), P, iS, null, Implicit);
		assertEquals(Explicit, expr.getForm());
	}

	@Test
	public void quantifiedExpression_LambdaToImplicit() {
		final QuantifiedExpression expr = ff.makeQuantifiedExpression(CSET,
				mList(dS), P, eS, null, Lambda);
		assertEquals(Implicit, expr.getForm());
	}

	@Test
	public void quantifiedExpression_LambdaToExplicit() {
		final QuantifiedExpression expr = ff.makeQuantifiedExpression(CSET,
				mList(dS), P, iS, null, Lambda);
		assertEquals(Explicit, expr.getForm());
	}

	@Test(expected = NullPointerException.class)
	public void singleton_NullChild() {
		final Expression child = null;
		ff.makeSetExtension(child, null);
	}

	@Test(expected = NullPointerException.class)
	public void setExtension_NullChildren() {
		final Expression[] children = null;
		ff.makeSetExtension(children, null);
	}

	@Test(expected = NullPointerException.class)
	public void setExtension_NullChild() {
		ff.makeSetExtension(mList(eS, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setExtension_InvalidType() {
		ff.makeEmptySetExtension(tS, null);
	}

	@Test
	public void setExtension_ArrayParameter() {
		final Expression[] members = { eS };
		assertArrayProtected(ff.makeSetExtension(members, null), members);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unaryExpression_InvalidTag() {
		ff.makeUnaryExpression(FREE_IDENT, eS, null);
	}

	@Test(expected = NullPointerException.class)
	public void unaryExpression_NullChild() {
		ff.makeUnaryExpression(KCARD, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("deprecation")
	public void unaryExpression_Prj1NotInV2() {
		ff.makeUnaryExpression(Formula.KPRJ1, eS, null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("deprecation")
	public void unaryExpression_Prj2NotInV2() {
		ff.makeUnaryExpression(Formula.KPRJ2, eS, null);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("deprecation")
	public void unaryExpression_IdNotInV2() {
		ff.makeUnaryExpression(Formula.KID, eS, null);
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF EXTENSION OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = IllegalArgumentException.class)
	public void extendedPredicate_Unknown() {
		ff.makeExtendedPredicate(new UnknownExtension(), NO_EXPRS, NO_PREDS,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedPredicate_Unsupported() {
		ff.makeExtendedPredicate(fooS, mList(eS, eT), mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedPredicate_NullExpressions() {
		EFF.makeExtendedPredicate(fooS, null, mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedPredicate_NullInExpressions() {
		EFF.makeExtendedPredicate(fooS, mList(eS, null), mList(P, P), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedPredicate_WrongNumberOfExpressions() {
		EFF.makeExtendedPredicate(fooS, mList(eS), mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedPredicate_NullPredicates() {
		EFF.makeExtendedPredicate(fooS, mList(eS, eT), null, null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedPredicate_NullInPredicates() {
		EFF.makeExtendedPredicate(fooS, mList(eS, eT), mList(P, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedPredicate_WrongNumberOfPredicates() {
		EFF.makeExtendedPredicate(fooS, mList(eS, eT), mList(P), null);
	}

	@Test
	public void extendedPredicate_ArrayParameter() {
		final Expression[] exprs = { eS, eT };
		final Predicate[] preds = { P, P };
		assertArrayProtected(
				EFF.makeExtendedPredicate(fooS, exprs, preds, null), exprs);
		assertArrayProtected(
				EFF.makeExtendedPredicate(fooS, exprs, preds, null), preds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedExpression_Unknown() {
		ff.makeExtendedExpression(new UnknownExtension(), NO_EXPRS, NO_PREDS,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedExpression_Unsupported() {
		ff.makeExtendedExpression(barS, mList(eS, eT), mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedExpression_NullExpressions() {
		EFF.makeExtendedExpression(barS, null, mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedExpression_NullInExpressions() {
		EFF.makeExtendedExpression(barS, mList(eS, null), mList(P, P), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedExpression_WrongNumberOfExpressions() {
		EFF.makeExtendedExpression(barS, mList(eS), mList(P, P), null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedExpression_NullPredicates() {
		EFF.makeExtendedExpression(barS, mList(eS, eT), null, null);
	}

	@Test(expected = NullPointerException.class)
	public void extendedExpression_NullInPredicates() {
		EFF.makeExtendedExpression(barS, mList(eS, eT), mList(P, null), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedExpression_WrongNumberOfPredicates() {
		EFF.makeExtendedExpression(barS, mList(eS, eT), mList(P), null);
	}

	@Test
	public void extendedExpression_ArrayParameter() {
		final Expression[] exprs = { eS, eT };
		final Predicate[] preds = { P, P };
		assertArrayProtected(
				EFF.makeExtendedExpression(barS, exprs, preds, null), exprs);
		assertArrayProtected(
				EFF.makeExtendedExpression(barS, exprs, preds, null), preds);
	}

	@Test(expected = IllegalArgumentException.class)
	public void extendedExpression_InvalidType() {
		EFF.makeExtendedExpression(barS, mList(eS, eT), mList(P, P), null, tS);
	}

	/**
	 * Verifies that object construction is protected against array mutation.
	 * The test consists in verifying that the behavior of
	 * <code>toString()</code> is not impacted by changing the array.
	 * 
	 * @param obj
	 *            an object which was constructed with the given array
	 * @param array
	 *            a non-empty array
	 */
	private static final void assertArrayProtected(Object obj, Object[] array) {
		final String expected = obj.toString();
		final Object save = array[0];
		array[0] = null;
		final String actual = obj.toString();
		array[0] = save;
		assertEquals(expected, actual);
	}

	/**
	 * Instances of this class must never be used to construct a formula
	 * factory.
	 */
	private static final class UnknownExtension implements
			IExpressionExtension, IPredicateExtension {

		public UnknownExtension() {
			// Do nothing
		}

		@Override
		public String getSyntaxSymbol() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean conjoinChildrenWD() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public String getId() {
			return "Unknown id";
		}

		@Override
		public String getGroupId() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public IExtensionKind getKind() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Object getOrigin() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean isATypeConstructor() {
			throw new AssertionError("Must never be called");
		}

	}

}
