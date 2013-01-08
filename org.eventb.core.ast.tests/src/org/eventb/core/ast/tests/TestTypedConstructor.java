/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fully refactored all tests and added error tests
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BOOL;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.DPROD;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FALSE;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.INTEGER;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KID;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRED;
import static org.eventb.core.ast.Formula.KPRJ1;
import static org.eventb.core.ast.Formula.KPRJ2;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.KSUCC;
import static org.eventb.core.ast.Formula.KUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.MOD;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.ast.Formula.NATURAL1;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.NOTEQUAL;
import static org.eventb.core.ast.Formula.NOTIN;
import static org.eventb.core.ast.Formula.NOTSUBSET;
import static org.eventb.core.ast.Formula.NOTSUBSETEQ;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.PFUN;
import static org.eventb.core.ast.Formula.PINJ;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.Formula.POW1;
import static org.eventb.core.ast.Formula.PPROD;
import static org.eventb.core.ast.Formula.PSUR;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.RANRES;
import static org.eventb.core.ast.Formula.RANSUB;
import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.SREL;
import static org.eventb.core.ast.Formula.STREL;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TINJ;
import static org.eventb.core.ast.Formula.TREL;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.Formula.TSUR;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.Formula.UPTO;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.QuantifiedExpression.Form.Lambda;
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
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mExtendedExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mId;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mPrj1;
import static org.eventb.core.ast.tests.FastFactory.mPrj2;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.tests.TestFormulaFactory.FailedAssertionChecker;
import org.junit.Test;

/**
 * Unit tests for type synthesis happening at node construction.
 * <p>
 * The tests are organized by subclasses of Formula. They are all along the same
 * pattern: for each tag corresponding to the subclass, we first check that type
 * synthesis works appropriately in nominal cases (children are type-checked and
 * bear compatible types), then in error cases (wrong children types or children
 * that are not type-checked).
 * </p>
 * 
 * @author Laurent Voisin
 */
public class TestTypedConstructor extends AbstractTests {

	// Types used in these tests
	private static final Type B = ff.makeBooleanType();
	private static final Type Z = ff.makeIntegerType();

	private static final Type S = ff.makeGivenType("S");
	private static final Type T = ff.makeGivenType("T");
	private static final Type U = ff.makeGivenType("U");
	private static final Type V = ff.makeGivenType("V");

	private static final Type pB = POW(B);
	private static final Type pS = POW(S);
	private static final Type pT = POW(T);
	private static final Type pU = POW(U);
	private static final Type pZ = POW(Z);

	private static final Type ppS = POW(pS);

	private static final Type ST = CPROD(S, T);
	private static final Type SU = CPROD(S, U);
	private static final Type SV = CPROD(S, V);
	private static final Type TU = CPROD(T, U);
	private static final Type TV = CPROD(T, V);

	private static final Type rSS = REL(S, S);
	private static final Type rSU = REL(S, U);
	private static final Type rTU = REL(T, U);
	private static final Type rST = REL(S, T);
	private static final Type rSV = REL(S, V);
	private static final Type rTS = REL(T, S);
	private static final Type rTV = REL(T, V);
	private static final Type rUT = REL(U, T);
	private static final Type rUV = REL(U, V);
	private static final Type rVU = REL(V, U);
	private static final Type rVV = REL(V, V);
	private static final Type rZZ = REL(Z, Z);

	private static final Type prST = POW(rST);

	private static final Type[] l_ = new Type[] { null };

	private static int index = 1;

	private static int getFreshIndex() {
		return index++;
	}

	private static void assertAssociativeExpressionType(int tag, Type expected,
			Type... types) {
		final Expression[] exprs = mExpressions(types);
		assertExpressionType(mAssociativeExpression(tag, exprs), expected);
	}

	/*
	 * Special test for given type coherence. A child expression defines the
	 * given set S, all children have type ℙ(T). idName defines the name of the
	 * free identifier child added.
	 */
	private static void assertAssocExprGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, pT);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℙ(T))(∅⦂ℙ(S))");
		final Expression expr = mAssociativeExpression(BUNION,
				new Expression[] { expr1, expr2 });
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(expr, expected);
	}


	private static void assertAssociativePredicate(int tag, boolean expected,
			boolean... bs) {
		final Predicate[] children = mPredicates(bs);
		final Predicate pred = mAssociativePredicate(tag, children);
		assertFormulaTypeChecked(pred, expected);
	}
	
	/*
	 * Special test for given type coherence. A child predicate of the associative
	 * predicate contains an identifier "S" which is not a given
	 * type. otherPredTypeIn defines the type of another child.
	 */
	private static void assertAssocPredGivenSets(Type otherPredTypeIn) {
		final Predicate pred1 = mPredicateWithSOfTypeZ();
		final Predicate pred2 = mRelationalPredicate(EQUAL,
				mExpression(otherPredTypeIn), mExpression(otherPredTypeIn));
		final Predicate pred = mAssociativePredicate(LAND, new Predicate[]{pred1, pred2});
		boolean expected = typesDoNotContainS(new Type[] { otherPredTypeIn });
		assertFormulaTypeChecked(pred, expected);
	}


	private static void assertAtomicExpressionType(int tag, Type expected) {
		assertExpressionType(mAtomicExpression(tag), expected);
	}

	private static void assertBecomesEqualsTo(boolean expected, Type[] left,
			Type[] right) {
		assertTrue(left.length == right.length);
		final FreeIdentifier[] lhs = mIdentifiers(left);
		final Expression[] rhs = mExpressions(right);
		assertFormulaTypeChecked(mBecomesEqualTo(lhs, rhs), expected);
	}
	
	/*
	 * Special test for given type coherence. The right hand side of assignment
	 * contains the given type S. Both sides have the coherent type "ℤ". idName
	 * defines the name of the free identifier child added.
	 */
	private static void assertBecEqualToGivenSets(String idName) {
		final FreeIdentifier freeId = mFreeIdentifier(idName, Z);
		final Expression value = parseExpression("(∅⦂ℙ(S)↔ℤ)(∅⦂ℙ(S))");
		final Assignment assign = mBecomesEqualTo(freeId, value);
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(assign, expected);
	}

	private static void assertBecomesMemberOf(boolean expected, Type left,
			Type right) {
		final FreeIdentifier lhs = mIdentifier(left);
		final Expression rhs = mExpression(right);
		assertFormulaTypeChecked(mBecomesMemberOf(lhs, rhs), expected);
	}

	/*
	 * Special test for given type coherence. The right hand side expression
	 * defines a given set S. Left hand side has type U and right hand side has
	 * type ℙ(U). idName is the name of the left hand side identifier.
	 */
	private static void assertBecMemberOfGivenSets(String idName) {
		final Expression set = parseExpression("(∅⦂ℙ(S)↔ℙ(U))(∅⦂ℙ(S))");
		final Assignment assign = mBecomesMemberOf(
				mFreeIdentifier(idName, U), set);
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(assign, expected);
	}
	
	private static void assertBecomesSuchThat(boolean expected, Type[] left,
			Type[] bound, boolean typed) {
		final FreeIdentifier[] lhs = mIdentifiers(left);
		final BoundIdentDecl[] decls = mDeclarations(bound);
		final Predicate rhs = mPredicate(typed);
		assertFormulaTypeChecked(mBecomesSuchThat(lhs, decls, rhs), expected);
	}
	
	/*
	 * Special test for given type coherence. The child predicate contains an
	 * identifier "S" which is not a given type.
	 */
	private static void assertBecSuchThatGivenSets(Type...declTypes) {
		final BoundIdentDecl[] bids = mDeclarations(declTypes);
		final FreeIdentifier[] fids = mFreeDeclarations(declTypes);
		final Assignment assign = mBecomesSuchThat(fids, bids,
				mPredicateWithSOfTypeZ());
		boolean expected = typesDoNotContainS(declTypes);
		assertFormulaTypeChecked(assign, expected);
	}

	private static void assertBinaryExpressionType(int tag, Type expected,
			Type leftType, Type rightType) {
		final Expression left = mExpression(leftType);
		final Expression right = mExpression(rightType);
		assertExpressionType(mBinaryExpression(tag, left, right), expected);
	}
	
	/*
	 * Special test for given type coherence. The left expression of the
	 * relation contains an identifier "S" which is not a given type.
	 */
	private static void assertBinExprGivenSets(Type rightPowerSet) {
		final Expression expr = mBinaryExpression(REL, mFreeIdentifier("S", pT),
				mExpression(rightPowerSet));
		boolean expected = typesDoNotContainS(new Type[]{rightPowerSet});
		assertFormulaTypeChecked(expr, expected);
	}

	private static void assertBinaryPredicate(int tag, boolean expected,
			boolean left, boolean right) {
		final Predicate lChild = mPredicate(left);
		final Predicate rChild = mPredicate(right);
		final Predicate pred = mBinaryPredicate(tag, lChild, rChild);
		assertFormulaTypeChecked(pred, expected);
	}
	
	/*
	 * Special test for given type coherence. The left predicate of the
	 * predicate equivalence contains an identifier "S" which is not a given
	 * type.
	 */
	private static void assertBinPredGivenSets(Type rightPredTypeIn) {
		final Predicate pred = mBinaryPredicate(
				LEQV,
				mPredicateWithSOfTypeZ(),
				mRelationalPredicate(EQUAL, mExpression(rightPredTypeIn),
						mExpression(rightPredTypeIn)));
		boolean expected = typesDoNotContainS(new Type[] { rightPredTypeIn });
		assertFormulaTypeChecked(pred, expected);
	}

	private static void assertBoolExpressionType(boolean typed, Type expected) {
		assertExpressionType(mBoolExpression(mPredicate(typed)), expected);
	}

	private static void assertBoundIdentDeclType(Type expected) {
		BoundIdentDecl decl = mBoundIdentDecl("x", expected);
		assertFormulaTypeChecked(decl, expected != null);
		assertEquals("Bad type", expected, decl.getType());
	}
	
	private static void assertBoundIdentifierType(Type type) {
		assertExpressionType(mBoundIdentifier(0, type), type);
	}

	private static void assertExpressionType(Expression expr, Type expected) {
		assertFormulaTypeChecked(expr, expected != null);
		assertEquals("Bad type", expected, expr.getType());
	}

	private static void assertFormulaTypeChecked(Formula<?> form,
			boolean expected) {
		IdentsChecker.check(form, ff);
		assertEquals(expected, form.isTypeChecked());
		if (expected) {
			runTypeCheck(form);
		}
	}

	private static void assertFreeIdentifierType(Type type) {
		assertExpressionType(mFreeIdentifier("x", type), type);
	}
	
	// Tells whether (name, type) corresponds to a given set declaration
	private static boolean isGivenSet(String name, Type type) {
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			final GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}

	
	/*
	 * Special test for given type coherence. The free identifier name is "S"
	 * which is not a given type.
	 */
	private static void assertFreeIdGivenSet(final Type type) {
		final String nameS = "S";
		boolean successExpected = typesDoNotContainS(new Type[] { type })
				|| isGivenSet(nameS, type);
		if (successExpected) {
			mFreeIdentifier(nameS, type);
		} else {
			new FailedAssertionChecker() {
				@Override
				protected void test() throws AssertionError {
					mFreeIdentifier(nameS, type);
				}
			}.run();
		}
	}

	private static void assertLiteralPredicate(int tag, boolean expected) {
		final Predicate pred = mLiteralPredicate(tag);
		assertFormulaTypeChecked(pred, expected);
	}

	private static void assertMultiplePredicate(int tag, boolean expected,
			Type... types) {
		final Expression[] exprs = mExpressions(types);
		final Predicate pred = mMultiplePredicate(tag, exprs);
		assertFormulaTypeChecked(pred, expected);
	}
	
	/*
	 * Special test for given type coherence. A child expression defines a given
	 * set S, all expressions have type ℙ(T). idName is the name of a free
	 * identifier child expression.
	 */
	private static void assertMultPredGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, pT);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℙ(T))(∅⦂ℙ(S))");
		final Predicate pred = mMultiplePredicate(new Expression[] { expr1,
				expr2 });
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(pred, expected);
	}
	

	private static void assertQuantifiedExpressionType(int tag, Form form,
			Type expected, Type[] types, boolean typed, Type type) {
		final BoundIdentDecl[] bids = mDeclarations(types);
		Expression expr = mMapletExpression(type);
		final Expression qexpr = mQuantifiedExpression(tag, form, bids,
				mPredicate(typed), expr);
		assertExpressionType(qexpr, expected);
	}
	
	/*
	 * Special test for given type coherence. The child predicate or expression
	 * contains an identifier "S" which is not a given type.
	 */
	private static void assertQExprGivenSets(Type...declTypes) {
		final BoundIdentDecl[] bids = mDeclarations(declTypes);
		final Expression qexpr1 = mQuantifiedExpression(CSET, Explicit, bids,
				mPredicateWithSOfTypeZ(), mExpression(pB));
		final Expression qexpr2 = mQuantifiedExpression(CSET, Explicit, bids,
				mPredicate(true), mExpressionWithSOfTypeZ());
		boolean expected = typesDoNotContainS(declTypes);
		assertFormulaTypeChecked(qexpr1, expected);
		assertFormulaTypeChecked(qexpr2, expected);
	}

	private static void assertQuantifiedPredicate(int tag, boolean expected,
			Type[] types, boolean typed) {
		final BoundIdentDecl[] bids = mDeclarations(types);
		final Predicate child = mPredicate(typed);
		final Predicate qpred = mQuantifiedPredicate(tag, bids, child);
		assertFormulaTypeChecked(qpred, expected);
	}

	/*
	 * Special test for given type coherence. The child predicate contains an
	 * identifier "S" which is not a given type.
	 */
	private static void assertQPredGivenSets(Type...bidTypes) {
		final BoundIdentDecl[] bids = mDeclarations(bidTypes);
		final Predicate pred = mQuantifiedPredicate(FORALL, bids,
				mPredicateWithSOfTypeZ());
		boolean expected = typesDoNotContainS(bidTypes);
		assertFormulaTypeChecked(pred, expected);
	}

	private static boolean typesDoNotContainS(Type[] types) {
		for (final Type type : types) {
			if (type.getGivenTypes().contains(S)) {
				return false;
			}
		}
		return true;
	}

	private static void assertRelationalPredicate(int tag, boolean expected,
			Type left, Type right) {
		final Expression lhs = mExpression(left);
		final Expression rhs = mExpression(right);
		final Predicate pred = mRelationalPredicate(tag, lhs, rhs);
		assertFormulaTypeChecked(pred, expected);
	}
	
	/*
	 * Special test for given type coherence. The left child expression defines
	 * a given set S, all expressions have type ℙ(T). idName is the name of a
	 * free identifier left child expression.
	 */
	private static void assertRelPredGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, pT);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℙ(T))(∅⦂ℙ(S))");
		final Predicate pred = mRelationalPredicate(expr1,
				expr2);
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(pred, expected);
	}

	private static void assertRelationCompositionType(Type expected,
			Type... types) {
		assertAssociativeExpressionType(FCOMP, expected, types);
		final List<Type> list = Arrays.asList(types);
		Collections.reverse(list);
		final Type[] revTypes = list.toArray(new Type[list.size()]);
		assertAssociativeExpressionType(BCOMP, expected, revTypes);
	}

	private static void assertSetExtensionType(Type expected, Type... types) {
		final Expression[] exprs = mExpressions(types);
		assertExpressionType(mSetExtension(exprs), expected);
	}
	
	/*
	 * Special test for given type coherence. A child expression defines a given
	 * set S, all expressions have type ℙ(T). idName is the name of a free
	 * identifier child expression.
	 */
	private static void assertSetExtGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, pT);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℙ(T))(∅⦂ℙ(S))");
		final Expression expr = mSetExtension(new Expression[]{expr1, expr2});
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(expr, expected);
	}


	private static void assertSimplePredicate(boolean expected, Type type) {
		final Expression expr = mExpression(type);
		final Predicate pred = mSimplePredicate(expr);
		assertFormulaTypeChecked(pred, expected);
	}

	private static void assertUnaryExpressionType(int tag, Type expected,
			Type type) {
		final Expression child = mExpression(type);
		assertExpressionType(mUnaryExpression(tag, child), expected);
	}

	private static void assertUnaryPredicate(int tag, boolean expected,
			boolean typed) {
		final Predicate child = mPredicate(typed);
		final Predicate pred = mUnaryPredicate(tag, child);
		assertFormulaTypeChecked(pred, expected);
	}
	
	/*
	 * Special test for given type coherence. A child expression defines the
	 * given set S, all children expressions have type ℤ. idName defines the
	 * name of the free identifier child added.
	 */
	private static void assertExtExprGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, Z);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℤ)(∅⦂ℙ(S))");
		final Expression expr = mExtendedExpression(new Expression[] { expr1,
				expr2 });
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(expr, expected);
	}

	/*
	 * Special test for given type coherence. A child expression defines the
	 * given set S, all children expressions have type ℤ. idName defines the
	 * name of the free identifier child added.
	 */
	private static void assertExtPredGivenSets(String idName) {
		final Expression expr1 = mFreeIdentifier(idName, Z);
		final Expression expr2 = parseExpression("(∅⦂ℙ(S)↔ℤ)(∅⦂ℙ(S))");
		final Predicate PT = mLiteralPredicate(BTRUE);
		final Predicate[] twoPreds = new Predicate[] { PT, PT };
		final Predicate pred = ExtendedFormulas.EFF.makeExtendedPredicate(
				ExtendedFormulas.fooL, new Expression[] { expr1, expr2 },
				twoPreds, null);
		boolean expected = !"S".equals(idName);
		assertFormulaTypeChecked(pred, expected);
	}


	private static Type[] l(Type... types) {
		return types;
	}

	private static BoundIdentDecl[] mDeclarations(Type... types) {
		final int len = types.length;
		final BoundIdentDecl[] result = new BoundIdentDecl[len];
		for (int i = 0; i < len; i++) {
			result[i] = mBoundIdentDecl("b" + getFreshIndex(), types[i]);
		}
		return result;
	}
	
	private static FreeIdentifier[] mFreeDeclarations(Type... types) {
		final int len = types.length;
		final FreeIdentifier[] result = new FreeIdentifier[len];
		for (int i = 0; i < len; i++) {
			result[i] = mFreeIdentifier("f" + getFreshIndex(), types[i]);
		}
		return result;
	}

	private static Expression mExpression(Type type) {
		if (type == null) {
			return mEmptySet(null);
		}
		return mFreeIdentifier("v" + getFreshIndex(), type);
	}

	private static Expression mMapletExpression(Type type) {
		if (type == null) {
			return mMaplet(mEmptySet(null), mEmptySet(null));
		}
		if (type instanceof ProductType) {
			final ProductType pType = (ProductType) type;
			final Expression left = mMapletExpression(pType.getLeft());
			final Expression right = mExpression(pType.getRight());
			return mMaplet(left, right);
		}
		return mExpression(type);
	}

	private static Expression[] mExpressions(Type[] types) {
		final int len = types.length;
		final Expression[] result = new Expression[len];
		for (int i = 0; i < len; i++) {
			result[i] = mExpression(types[i]);
		}
		return result;
	}

	private static FreeIdentifier mIdentifier(Type type) {
		return mFreeIdentifier("x" + getFreshIndex(), type);
	}

	private static FreeIdentifier[] mIdentifiers(Type[] types) {
		final int len = types.length;
		final FreeIdentifier[] result = new FreeIdentifier[len];
		for (int i = 0; i < len; i++) {
			result[i] = mIdentifier(types[i]);
		}
		return result;
	}

	private static Predicate mPredicate(boolean typed) {
		final Predicate pred;
		if (typed) {
			pred = mLiteralPredicate();
		} else {
			pred = mRelationalPredicate(mEmptySet(null), mEmptySet(null));
		}
		return pred;
	}

	private static Predicate[] mPredicates(boolean... bs) {
		final int len = bs.length;
		final Predicate[] result = new Predicate[len];
		for (int i = 0; i < len; i++) {
			result[i] = mPredicate(bs[i]);
		}
		return result;

	}

	// Returns a type-checked predicate where the identifier "S" is of type "ℤ"
	private static Predicate mPredicateWithSOfTypeZ() {
		final Predicate result = mRelationalPredicate(EQUAL,
				mExpressionWithSOfTypeZ(), mIntegerLiteral());
		assertTrue(result.isTypeChecked());
		return result;
	}

	// Returns a type-checked expression where the identifier "S" is of type "ℤ"
	private static Expression mExpressionWithSOfTypeZ() {
		final Expression result = mFreeIdentifier("S", Z);
		assertTrue(result.isTypeChecked());
		return result;
	}
	
	private static void runTypeCheck(Formula<?> form) {
		if (form.isWellFormed()) {
			typeCheck(form);
			IdentsChecker.check(form, ff);
		}
	}

	@Test 
	public void testAssociativeExpression() {
		for (int tag : Arrays.asList(BUNION, BINTER)) {
			assertAssociativeExpressionType(tag, pS, pS, pS);
			assertAssociativeExpressionType(tag, null, pT, pS);
			assertAssociativeExpressionType(tag, null, pS, pT);
			assertAssociativeExpressionType(tag, null, S, S);

			assertAssociativeExpressionType(tag, pS, pS, pS, pS);
			assertAssociativeExpressionType(tag, null, pT, pS, pS);
			assertAssociativeExpressionType(tag, null, pS, pT, pS);
			assertAssociativeExpressionType(tag, null, pS, pS, pT);
			assertAssociativeExpressionType(tag, null, S, S, S);

			assertAssociativeExpressionType(tag, null, null, pS, pS);
			assertAssociativeExpressionType(tag, null, pS, null, pS);
			assertAssociativeExpressionType(tag, null, pS, pS, null);
		}

		assertRelationCompositionType(rSU, rST, rTU);
		assertRelationCompositionType(null, rSV, rTU);
		assertRelationCompositionType(null, rST, rVU);
		assertRelationCompositionType(null, pT, rTU);
		assertRelationCompositionType(null, rST, pT);
		assertRelationCompositionType(rSV, rST, rTU, rUV);
		assertRelationCompositionType(null, rSU, rTU, rUV);
		assertRelationCompositionType(null, rST, rSU, rUV);
		assertRelationCompositionType(null, rST, rTV, rUV);
		assertRelationCompositionType(null, rST, rTU, rVV);
		assertRelationCompositionType(null, pT, rTU, rUV);
		assertRelationCompositionType(null, rST, pT, rUV);
		assertRelationCompositionType(null, rST, pU, rUV);
		assertRelationCompositionType(null, rST, rTU, pU);
		assertRelationCompositionType(null, null, rTU, rUV);
		assertRelationCompositionType(null, rST, null, rUV);
		assertRelationCompositionType(null, rST, rTU, null);

		assertAssociativeExpressionType(OVR, rST, rST, rST);
		assertAssociativeExpressionType(OVR, null, rST, rSU);
		assertAssociativeExpressionType(OVR, null, rST, rUT);
		assertAssociativeExpressionType(OVR, null, pS, pS);
		assertAssociativeExpressionType(OVR, rST, rST, rST, rST);
		assertAssociativeExpressionType(OVR, null, rSU, rST, rST);
		assertAssociativeExpressionType(OVR, null, rST, rSU, rST);
		assertAssociativeExpressionType(OVR, null, rST, rST, rSU);
		assertAssociativeExpressionType(OVR, null, pS, pS, pS);
		assertAssociativeExpressionType(OVR, null, null, rST, rST);
		assertAssociativeExpressionType(OVR, null, rST, null, rST);
		assertAssociativeExpressionType(OVR, null, rST, rST, null);

		for (int tag : Arrays.asList(PLUS, MUL)) {
			assertAssociativeExpressionType(tag, Z, Z, Z);
			assertAssociativeExpressionType(tag, null, S, Z);
			assertAssociativeExpressionType(tag, null, Z, S);
			assertAssociativeExpressionType(tag, null, S, S);
			assertAssociativeExpressionType(tag, Z, Z, Z, Z);
			assertAssociativeExpressionType(tag, null, S, Z, Z);
			assertAssociativeExpressionType(tag, null, Z, S, Z);
			assertAssociativeExpressionType(tag, null, Z, Z, S);
			assertAssociativeExpressionType(tag, null, S, S, S);
			assertAssociativeExpressionType(tag, null, null, Z, Z);
			assertAssociativeExpressionType(tag, null, Z, null, Z);
			assertAssociativeExpressionType(tag, null, Z, Z, null);
		}
	}

	@Test 
	public void testAssociativePredicate() throws Exception {
		for (int tag : Arrays.asList(LAND, LOR)) {
			assertAssociativePredicate(tag, true, true, true);
			assertAssociativePredicate(tag, false, false, true);
			assertAssociativePredicate(tag, false, true, false);

			assertAssociativePredicate(tag, true, true, true, true);
			assertAssociativePredicate(tag, false, false, true, true);
			assertAssociativePredicate(tag, false, true, false, true);
			assertAssociativePredicate(tag, false, true, true, false);
		}
	}

	@Test 
	public void testAtomicExpression() throws Exception {
		assertAtomicExpressionType(INTEGER, pZ);
		assertAtomicExpressionType(NATURAL, pZ);
		assertAtomicExpressionType(NATURAL1, pZ);

		assertAtomicExpressionType(BOOL, pB);
		assertAtomicExpressionType(TRUE, B);
		assertAtomicExpressionType(FALSE, B);

		assertAtomicExpressionType(KPRED, rZZ);
		assertAtomicExpressionType(KSUCC, rZZ);
	}

	@Test 
	public void testBecomesEqualTo() throws Exception {
		assertBecomesEqualsTo(true, l(S), l(S));
		assertBecomesEqualsTo(false, l(S), l(T));
		assertBecomesEqualsTo(false, l_, l(T));
		assertBecomesEqualsTo(false, l(S), l_);

		assertBecomesEqualsTo(true, l(S, T), l(S, T));
		assertBecomesEqualsTo(false, l(S, T), l(T, S));
		assertBecomesEqualsTo(false, l(S, T), l(U, T));
		assertBecomesEqualsTo(false, l(S, T), l(S, U));
		assertBecomesEqualsTo(false, l(null, T), l(S, T));
		assertBecomesEqualsTo(false, l(S, null), l(S, T));
		assertBecomesEqualsTo(false, l(S, T), l(null, T));
		assertBecomesEqualsTo(false, l(S, T), l(S, null));

		assertBecomesEqualsTo(true, l(S, T, U), l(S, T, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(S, U, T));
		assertBecomesEqualsTo(false, l(S, T, U), l(T, S, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(U, S, T));
		assertBecomesEqualsTo(false, l(S, T, U), l(V, T, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(S, V, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(S, T, V));
		assertBecomesEqualsTo(false, l(null, T, U), l(S, T, U));
		assertBecomesEqualsTo(false, l(S, null, U), l(S, T, U));
		assertBecomesEqualsTo(false, l(S, T, null), l(S, T, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(null, T, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(S, null, U));
		assertBecomesEqualsTo(false, l(S, T, U), l(S, T, null));
	}

	@Test 
	public void testBecomesMemberOf() throws Exception {
		assertBecomesMemberOf(true, S, pS);
		assertBecomesMemberOf(false, S, S);
		assertBecomesMemberOf(false, pS, S);
		assertBecomesMemberOf(false, null, pS);
		assertBecomesMemberOf(false, S, null);
	}

	@Test 
	public void testBecomesSuchThat() throws Exception {
		assertBecomesSuchThat(true, l(S), l(S), true);
		assertBecomesSuchThat(false, l(S), l(T), true);
		assertBecomesSuchThat(false, l_, l(S), true);
		assertBecomesSuchThat(false, l(S), l_, true);
		assertBecomesSuchThat(false, l(S), l(S), false);

		assertBecomesSuchThat(true, l(S, T), l(S, T), true);
		assertBecomesSuchThat(false, l(S, T), l(T, S), true);
		assertBecomesSuchThat(false, l(S, T), l(U, T), true);
		assertBecomesSuchThat(false, l(S, T), l(S, U), true);
		assertBecomesSuchThat(false, l(null, T), l(S, T), true);
		assertBecomesSuchThat(false, l(S, null), l(S, T), true);
		assertBecomesSuchThat(false, l(S, T), l(null, T), true);
		assertBecomesSuchThat(false, l(S, T), l(S, null), true);
		assertBecomesSuchThat(false, l(S, T), l(S, T), false);

		assertBecomesSuchThat(true, l(S, T, U), l(S, T, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, U, T), true);
		assertBecomesSuchThat(false, l(S, T, U), l(T, S, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(U, S, T), true);
		assertBecomesSuchThat(false, l(S, T, U), l(V, T, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, V, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, T, V), true);
		assertBecomesSuchThat(false, l(null, T, U), l(S, T, U), true);
		assertBecomesSuchThat(false, l(S, null, U), l(S, T, U), true);
		assertBecomesSuchThat(false, l(S, T, null), l(S, T, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(null, T, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, null, U), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, T, null), true);
		assertBecomesSuchThat(false, l(S, T, U), l(S, T, U), false);
	}

	@Test 
	public void testBinaryExpression() throws Exception {
		assertBinaryExpressionType(FUNIMAGE, T, rST, S);
		assertBinaryExpressionType(FUNIMAGE, null, rTU, S);
		assertBinaryExpressionType(FUNIMAGE, null, pS, S);
		assertBinaryExpressionType(FUNIMAGE, null, null, S);
		assertBinaryExpressionType(FUNIMAGE, null, rST, null);

		assertBinaryExpressionType(RELIMAGE, pT, rST, pS);
		assertBinaryExpressionType(RELIMAGE, null, rST, S);
		assertBinaryExpressionType(RELIMAGE, null, rTU, pS);
		assertBinaryExpressionType(RELIMAGE, null, pS, pS);
		assertBinaryExpressionType(RELIMAGE, null, null, pS);
		assertBinaryExpressionType(RELIMAGE, null, rST, null);

		assertBinaryExpressionType(MAPSTO, ST, S, T);
		assertBinaryExpressionType(MAPSTO, null, null, T);
		assertBinaryExpressionType(MAPSTO, null, S, null);

		for (int tag : Arrays.asList(REL, TREL, SREL, STREL, PFUN, TFUN, PINJ,
				TINJ, PSUR, TSUR, TBIJ)) {
			assertBinaryExpressionType(tag, prST, pS, pT);
			assertBinaryExpressionType(tag, null, S, pT);
			assertBinaryExpressionType(tag, null, pS, T);
			assertBinaryExpressionType(tag, null, null, pT);
			assertBinaryExpressionType(tag, null, pS, null);
		}

		assertBinaryExpressionType(SETMINUS, pS, pS, pS);
		assertBinaryExpressionType(SETMINUS, null, pS, pT);
		assertBinaryExpressionType(SETMINUS, null, S, pS);
		assertBinaryExpressionType(SETMINUS, null, pS, S);
		assertBinaryExpressionType(SETMINUS, null, null, pS);
		assertBinaryExpressionType(SETMINUS, null, pS, null);

		assertBinaryExpressionType(CPROD, rST, pS, pT);
		assertBinaryExpressionType(CPROD, null, S, pT);
		assertBinaryExpressionType(CPROD, null, pS, T);
		assertBinaryExpressionType(CPROD, null, null, pT);
		assertBinaryExpressionType(CPROD, null, pS, null);

		assertBinaryExpressionType(DPROD, REL(S, TU), rST, rSU);
		assertBinaryExpressionType(DPROD, null, rST, rTU);
		assertBinaryExpressionType(DPROD, null, pS, rSU);
		assertBinaryExpressionType(DPROD, null, rST, pS);
		assertBinaryExpressionType(DPROD, null, null, rSU);
		assertBinaryExpressionType(DPROD, null, rST, null);

		assertBinaryExpressionType(PPROD, REL(SU, TV), rST, rUV);
		assertBinaryExpressionType(PPROD, null, pS, rUV);
		assertBinaryExpressionType(PPROD, null, rST, pU);
		assertBinaryExpressionType(PPROD, null, null, rUV);
		assertBinaryExpressionType(PPROD, null, rST, null);

		for (int tag : Arrays.asList(DOMRES, DOMSUB)) {
			assertBinaryExpressionType(tag, rST, pS, rST);
			assertBinaryExpressionType(tag, null, pT, rST);
			assertBinaryExpressionType(tag, null, S, rST);
			assertBinaryExpressionType(tag, null, pS, pS);
			assertBinaryExpressionType(tag, null, null, rST);
			assertBinaryExpressionType(tag, null, pS, null);
		}

		for (int tag : Arrays.asList(RANRES, RANSUB)) {
			assertBinaryExpressionType(tag, rST, rST, pT);
			assertBinaryExpressionType(tag, null, rST, pS);
			assertBinaryExpressionType(tag, null, pS, pT);
			assertBinaryExpressionType(tag, null, rST, T);
			assertBinaryExpressionType(tag, null, null, pT);
			assertBinaryExpressionType(tag, null, rST, null);
		}

		assertBinaryExpressionType(UPTO, pZ, Z, Z);
		assertBinaryExpressionType(UPTO, null, S, Z);
		assertBinaryExpressionType(UPTO, null, Z, S);
		assertBinaryExpressionType(UPTO, null, S, S);
		assertBinaryExpressionType(UPTO, null, null, Z);
		assertBinaryExpressionType(UPTO, null, Z, null);

		for (int tag : Arrays.asList(MINUS, DIV, MOD, EXPN)) {
			assertBinaryExpressionType(tag, Z, Z, Z);
			assertBinaryExpressionType(tag, null, S, Z);
			assertBinaryExpressionType(tag, null, Z, S);
			assertBinaryExpressionType(tag, null, S, S);
			assertBinaryExpressionType(tag, null, null, Z);
			assertBinaryExpressionType(tag, null, Z, null);
		}
	}

	@Test 
	public void testBinaryPredicate() throws Exception {
		for (int tag : Arrays.asList(LIMP, LEQV)) {
			assertBinaryPredicate(tag, true, true, true);
			assertBinaryPredicate(tag, false, false, true);
			assertBinaryPredicate(tag, false, true, false);
		}
	}

	@Test 
	public void testBoolExpression() throws Exception {
		assertBoolExpressionType(true, B);
		assertBoolExpressionType(false, null);
	}

	@Test 
	public void testBoundIdentDecl() throws Exception {
		assertBoundIdentDeclType(S);
		assertBoundIdentDeclType(null);
	}

	@Test 
	public void testBoundIdentifier() throws Exception {
		assertBoundIdentifierType(S);
		assertBoundIdentifierType(null);
	}

	@Test 
	public void testGenericTypes() throws Exception {
		assertExpressionType(mEmptySet(pS), pS);
		assertExpressionType(mEmptySet(null), null);

		assertExpressionType(mId(rSS), rSS);
		assertExpressionType(mId(null), null);
		
		assertExpressionType(mPrj1(REL(ST, S)), REL(ST, S));
		assertExpressionType(mPrj1(null), null);
		
		assertExpressionType(mPrj2(REL(ST, T)), REL(ST, T));
		assertExpressionType(mPrj2(null), null);
	}

	@Test
	@SuppressWarnings("synthetic-access")
	public void testFreeIdentifier() throws Exception {
		assertFreeIdentifierType(S);
		assertFreeIdentifierType(null);

		// Regular given set
		assertExpressionType(ff.makeFreeIdentifier("S", null, pS), pS);

		// Name occurs with a different type in type
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeFreeIdentifier("S", null, ppS);
			}
		}.run();
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeFreeIdentifier("S", null, rSS);
			}
		}.run();
	}

	@Test 
	public void testIntegerLiteral() throws Exception {
		assertExpressionType(mIntegerLiteral(), Z);
	}

	@Test 
	public void testLiteralPredicate() throws Exception {
		assertLiteralPredicate(BTRUE, true);
		assertLiteralPredicate(BFALSE, true);
	}

	@Test 
	public void testMultiplePredicate() throws Exception {
		assertMultiplePredicate(KPARTITION, true, pS);
		assertMultiplePredicate(KPARTITION, false, S);
		assertMultiplePredicate(KPARTITION, false, (Type) null);
		assertMultiplePredicate(KPARTITION, true, pS, pS);
		assertMultiplePredicate(KPARTITION, false, S, S);
		assertMultiplePredicate(KPARTITION, false, pS, pT);
		assertMultiplePredicate(KPARTITION, false, null, pS);
		assertMultiplePredicate(KPARTITION, false, pS, null);
		assertMultiplePredicate(KPARTITION, false, null, null);
	}

	@Test 
	public void testQuantifiedExpression() throws Exception {
		// Union
		assertQuantifiedExpressionType(QUNION, Explicit, pS, l(S), true, pS);
		assertQuantifiedExpressionType(QUNION, Explicit, null, l_, true, pS);
		assertQuantifiedExpressionType(QUNION, Explicit, null, l(S), false, pS);
		assertQuantifiedExpressionType(QUNION, Explicit, null, l(S), true, null);

		// Intersection
		assertQuantifiedExpressionType(QINTER, Explicit, pS, l(S), true, pS);
		assertQuantifiedExpressionType(QINTER, Explicit, null, l_, true, pS);
		assertQuantifiedExpressionType(QINTER, Explicit, null, l(S), false, pS);
		assertQuantifiedExpressionType(QINTER, Explicit, null, l(S), true, null);

		// Lambda with all components typed
		assertQuantifiedExpressionType(CSET, Lambda, rSV, l(S), true, SV);
		assertQuantifiedExpressionType(CSET, Lambda, REL(ST, V), l(S, T), true,
				CPROD(ST, V));
		assertQuantifiedExpressionType(CSET, Lambda, REL(CPROD(ST, U), V), l(S,
				T, U), true, CPROD(CPROD(ST, U), V));
		assertQuantifiedExpressionType(CSET, Lambda, REL(CPROD(S, TU), V), l(S,
				T, U), true, CPROD(CPROD(S, TU), V));

		// Lambda with one untyped component
		assertQuantifiedExpressionType(CSET, Lambda, null, l_, true, SV);
		assertQuantifiedExpressionType(CSET, Lambda, null, l(S), false, SV);
		assertQuantifiedExpressionType(CSET, Lambda, null, l(S), true, null);

		// Comprehension set
		assertQuantifiedExpressionType(CSET, Explicit, pS, l(S), true, S);
		assertQuantifiedExpressionType(CSET, Explicit, null, l_, true, S);
		assertQuantifiedExpressionType(CSET, Explicit, null, l(S), false, S);
		assertQuantifiedExpressionType(CSET, Explicit, null, l(S), true, null);
	}

	@Test 
	public void testQuantifiedPredicate() throws Exception {
		for (int tag : Arrays.asList(FORALL, EXISTS)) {
			assertQuantifiedPredicate(tag, true, l(S), true);
			assertQuantifiedPredicate(tag, false, l_, true);
			assertQuantifiedPredicate(tag, false, l(S), false);

			assertQuantifiedPredicate(tag, true, l(S, T), true);
			assertQuantifiedPredicate(tag, false, l(null, T), true);
			assertQuantifiedPredicate(tag, false, l(S, null), true);
			assertQuantifiedPredicate(tag, false, l(S, T), false);

			assertQuantifiedPredicate(tag, true, l(S, T, U), true);
			assertQuantifiedPredicate(tag, false, l(null, T, U), true);
			assertQuantifiedPredicate(tag, false, l(S, null, U), true);
			assertQuantifiedPredicate(tag, false, l(S, T, null), true);
			assertQuantifiedPredicate(tag, false, l(S, T, U), false);
		}
	}

	@Test 
	public void testRelationalPredicated() throws Exception {
		for (int tag : Arrays.asList(EQUAL, NOTEQUAL)) {
			assertRelationalPredicate(tag, true, B, B);
			assertRelationalPredicate(tag, true, S, S);
			assertRelationalPredicate(tag, true, Z, Z);
			assertRelationalPredicate(tag, true, pS, pS);
			assertRelationalPredicate(tag, true, ST, ST);
			assertRelationalPredicate(tag, false, S, T);
			assertRelationalPredicate(tag, false, S, pS);
			assertRelationalPredicate(tag, false, S, ST);
			assertRelationalPredicate(tag, false, T, ST);
			assertRelationalPredicate(tag, false, null, S);
			assertRelationalPredicate(tag, false, S, null);
		}

		for (int tag : Arrays.asList(LT, LE, GT, GE)) {
			assertRelationalPredicate(tag, true, Z, Z);
			assertRelationalPredicate(tag, false, Z, S);
			assertRelationalPredicate(tag, false, S, Z);
			assertRelationalPredicate(tag, false, S, S);
			assertRelationalPredicate(tag, false, null, Z);
			assertRelationalPredicate(tag, false, Z, null);
		}

		for (int tag : Arrays.asList(IN, NOTIN)) {
			assertRelationalPredicate(tag, true, S, pS);
			assertRelationalPredicate(tag, true, pS, ppS);
			assertRelationalPredicate(tag, false, S, S);
			assertRelationalPredicate(tag, false, S, pT);
			assertRelationalPredicate(tag, false, null, pS);
			assertRelationalPredicate(tag, false, S, null);
		}

		for (int tag : Arrays.asList(SUBSET, NOTSUBSET, SUBSETEQ, NOTSUBSETEQ)) {
			assertRelationalPredicate(tag, true, pS, pS);
			assertRelationalPredicate(tag, false, pS, pT);
			assertRelationalPredicate(tag, false, S, pS);
			assertRelationalPredicate(tag, false, pS, S);
			assertRelationalPredicate(tag, false, null, pS);
			assertRelationalPredicate(tag, false, pS, null);
		}
	}

	@Test 
	public void testSetExtension() throws Exception {
		assertSetExtensionType(null);

		assertSetExtensionType(pS, S);
		assertSetExtensionType(null, new Type[] { null });

		assertSetExtensionType(pS, S, S);
		assertSetExtensionType(null, S, null);
		assertSetExtensionType(null, null, S);

		assertSetExtensionType(pS, S, S, S);
		assertSetExtensionType(null, S, S, null);
		assertSetExtensionType(null, S, null, S);
		assertSetExtensionType(null, null, S, S);
	}

	@Test 
	public void testSimplePredicate() throws Exception {
		assertSimplePredicate(true, pS);
		assertSimplePredicate(false, B);
		assertSimplePredicate(false, S);
		assertSimplePredicate(false, Z);
		assertSimplePredicate(false, null);
	}

	@SuppressWarnings("deprecation")
	@Test 
	public void testUnaryExpression() throws Exception {
		assertUnaryExpressionType(UNMINUS, null, null);
		assertUnaryExpressionType(UNMINUS, null, B);
		assertUnaryExpressionType(UNMINUS, null, S);
		assertUnaryExpressionType(UNMINUS, Z, Z);
		assertUnaryExpressionType(UNMINUS, null, pS);
		assertUnaryExpressionType(UNMINUS, null, rST);

		assertUnaryExpressionType(CONVERSE, null, null);
		assertUnaryExpressionType(CONVERSE, null, B);
		assertUnaryExpressionType(CONVERSE, null, S);
		assertUnaryExpressionType(CONVERSE, null, Z);
		assertUnaryExpressionType(CONVERSE, null, pS);
		assertUnaryExpressionType(CONVERSE, rTS, rST);

		assertUnaryExpressionType(KCARD, null, null);
		assertUnaryExpressionType(KCARD, null, B);
		assertUnaryExpressionType(KCARD, null, S);
		assertUnaryExpressionType(KCARD, null, Z);
		assertUnaryExpressionType(KCARD, Z, pS);
		assertUnaryExpressionType(KCARD, Z, rST);

		assertUnaryExpressionType(POW, null, null);
		assertUnaryExpressionType(POW, null, B);
		assertUnaryExpressionType(POW, null, S);
		assertUnaryExpressionType(POW, null, Z);
		assertUnaryExpressionType(POW, ppS, pS);
		assertUnaryExpressionType(POW, prST, rST);

		assertUnaryExpressionType(POW1, null, null);
		assertUnaryExpressionType(POW1, null, B);
		assertUnaryExpressionType(POW1, null, S);
		assertUnaryExpressionType(POW1, null, Z);
		assertUnaryExpressionType(POW1, ppS, pS);
		assertUnaryExpressionType(POW1, prST, rST);

		assertUnaryExpressionType(KUNION, null, null);
		assertUnaryExpressionType(KUNION, null, B);
		assertUnaryExpressionType(KUNION, null, S);
		assertUnaryExpressionType(KUNION, null, Z);
		assertUnaryExpressionType(KUNION, null, pS);
		assertUnaryExpressionType(KUNION, null, rST);
		assertUnaryExpressionType(KUNION, pS, ppS);

		assertUnaryExpressionType(KINTER, null, null);
		assertUnaryExpressionType(KINTER, null, B);
		assertUnaryExpressionType(KINTER, null, S);
		assertUnaryExpressionType(KINTER, null, Z);
		assertUnaryExpressionType(KINTER, null, pS);
		assertUnaryExpressionType(KINTER, null, rST);
		assertUnaryExpressionType(KINTER, pS, ppS);

		assertUnaryExpressionType(KDOM, null, null);
		assertUnaryExpressionType(KDOM, null, B);
		assertUnaryExpressionType(KDOM, null, S);
		assertUnaryExpressionType(KDOM, null, Z);
		assertUnaryExpressionType(KDOM, null, pS);
		assertUnaryExpressionType(KDOM, pS, rST);

		assertUnaryExpressionType(KRAN, null, null);
		assertUnaryExpressionType(KRAN, null, B);
		assertUnaryExpressionType(KRAN, null, S);
		assertUnaryExpressionType(KRAN, null, Z);
		assertUnaryExpressionType(KRAN, null, pS);
		assertUnaryExpressionType(KRAN, pT, rST);

		assertUnaryExpressionType(KPRJ1, null, null);
		assertUnaryExpressionType(KPRJ1, null, B);
		assertUnaryExpressionType(KPRJ1, null, S);
		assertUnaryExpressionType(KPRJ1, null, Z);
		assertUnaryExpressionType(KPRJ1, null, pS);
		assertUnaryExpressionType(KPRJ1, REL(ST, S), rST);

		assertUnaryExpressionType(KPRJ2, null, null);
		assertUnaryExpressionType(KPRJ2, null, B);
		assertUnaryExpressionType(KPRJ2, null, S);
		assertUnaryExpressionType(KPRJ2, null, Z);
		assertUnaryExpressionType(KPRJ2, null, pS);
		assertUnaryExpressionType(KPRJ2, REL(ST, T), rST);

		assertUnaryExpressionType(KID, null, null);
		assertUnaryExpressionType(KID, null, B);
		assertUnaryExpressionType(KID, null, S);
		assertUnaryExpressionType(KID, null, Z);
		assertUnaryExpressionType(KID, rSS, pS);
		assertUnaryExpressionType(KID, REL(ST, ST), rST);

		for (int tag : Arrays.asList(KMIN, KMAX)) {
			assertUnaryExpressionType(tag, null, null);
			assertUnaryExpressionType(tag, null, B);
			assertUnaryExpressionType(tag, null, S);
			assertUnaryExpressionType(tag, null, Z);
			assertUnaryExpressionType(tag, null, ST);
			assertUnaryExpressionType(tag, null, pB);
			assertUnaryExpressionType(tag, null, pS);
			assertUnaryExpressionType(tag, null, rST);
			assertUnaryExpressionType(tag, Z, pZ);
		}
	}

	@Test 
	public void testUnaryPredicate() throws Exception {
		assertUnaryPredicate(NOT, true, true);
		assertUnaryPredicate(NOT, false, false);
	}
	

	/**
	 * Ensures that given sets are taken into account when synthesizing the
	 * result type. This can happen only when there are several children,
	 * therefore atomic and unary operators are not tested.
	 */
	@Test
	public void givenSetErrors() throws Exception {
		
		assertAssocExprGivenSets("S");
		assertAssocExprGivenSets("T");
		
		assertAssocPredGivenSets(S);
		assertAssocPredGivenSets(T);
		
		// AtomicExpression: atomic operator
		
		assertBecEqualToGivenSets("S");
		assertBecEqualToGivenSets("T");
		
		assertBecMemberOfGivenSets("S");
		assertBecMemberOfGivenSets("T");
		
		assertBecSuchThatGivenSets(S);
		assertBecSuchThatGivenSets(T);
		assertBecSuchThatGivenSets(S, T);
		assertBecSuchThatGivenSets(T, S);

		assertBinExprGivenSets(pS);
		assertBinExprGivenSets(pT);
		
		assertBinPredGivenSets(S);
		assertBinPredGivenSets(T);
		
		// BoolExpression: unary operator
		// BoundIdentDecl: atomic operator
		// BoundIdentifier: atomic operator

		assertExtExprGivenSets("S");
		assertExtExprGivenSets("T");
		
		assertExtPredGivenSets("S");
		assertExtPredGivenSets("T");

		assertFreeIdGivenSet(S);
		assertFreeIdGivenSet(pS);
		assertFreeIdGivenSet(T);
		
		// IntegerLiteral: atomic operator
		// LiteralPredicate: atomic operator
		
		assertMultPredGivenSets("S");
		assertMultPredGivenSets("T");

		assertQExprGivenSets(S);
		assertQExprGivenSets(T);
		assertQExprGivenSets(S, T);
		assertQExprGivenSets(T, S);

		assertQPredGivenSets(S);
		assertQPredGivenSets(T);
		assertQPredGivenSets(S, T);
		assertQPredGivenSets(T, S);
		
		assertRelPredGivenSets("S");
		assertRelPredGivenSets("T");
		
		assertSetExtGivenSets("S");
		assertSetExtGivenSets("T");
		
		// SimplePredicate: unary operator
		// UnaryExpression: unary operator
		// UnaryPredicate: unary operator
	}
	
}
