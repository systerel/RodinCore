/*******************************************************************************
 * Copyright (c) 2005, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - operators of mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mId;
import static org.eventb.core.ast.tests.FastFactory.mList;
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
import static org.eventb.core.ast.tests.extension.Extensions.EITHER_DT;
import static org.eventb.core.ast.tests.extension.Extensions.EITHER_FAC;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IExpressionExtension2;
import org.eventb.core.ast.tests.extension.Extensions.LeftPlus;
import org.eventb.core.ast.tests.extension.Extensions.Real;
import org.eventb.core.ast.tests.extension.Extensions.RealPlus;
import org.eventb.core.ast.tests.extension.Extensions.Return;
import org.junit.Test;

/**
 * Main test class for formulas containing generic operators.
 *
 * <h2>Predefined operators</h2>
 *
 * Tests have been entered in the same order as the type-checker specification
 * in the Rodin Deliverable D7 "Event-B Language".
 * <p>
 * Only tests where an empty set can occur have been retained. For the other
 * generic atomic operators (KID_GEN, KPRJ1_GEN, KPRJ2_GEN) only one test is
 * present as they are parsed in the same way as empty sets.
 *
 * <h2>Extension operators</h2>
 *
 * The second part of this class concerns generic operators provided by
 * expression extensions. We test that one can recover a typed expression from
 * its serialization with <code>toStringWithTypes</code>.
 * <p>
 *
 * @see Formula#toStringWithTypes()
 */
public class TestTypedGeneric extends AbstractTests {

	// Types used in these tests
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	private static GivenType ty_Sv1 = ffV1.makeGivenType("S");
	private static GivenType ty_Tv1 = ffV1.makeGivenType("T");

	/**
	 * Main test routine for expressions containing generic atomic operators.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	@SuppressWarnings("deprecation")
	@Test 
	public void testExpressions () {

		final AtomicExpression eS = mEmptySet(POW(ty_S));
		final AtomicExpression eT = mEmptySet(POW(ty_T));
		final AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		final AtomicExpression eST = mEmptySet(REL(ty_S, ty_T));
		final AtomicExpression eSU = mEmptySet(REL(ty_S, ty_U));
		final AtomicExpression eTU = mEmptySet(REL(ty_T, ty_U));
		final AtomicExpression eUV = mEmptySet(REL(ty_U, ty_V));
		final AtomicExpression ePST = mEmptySet(REL(POW(ty_S), ty_T));

		final AtomicExpression eSv1 = ffV1.makeEmptySet(POW(ty_Sv1), null);
		final AtomicExpression eSTv1 = ffV1.makeEmptySet(REL(ty_Sv1, ty_Tv1), null);

		final BoundIdentDecl bd_x = mBoundIdentDecl("x", POW(ty_S));
		final BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));

		//--------------------
		//  Binary expressions
		//--------------------
		doTest(mBinaryExpression(Formula.FUNIMAGE, ePST, eS), ty_T);

		doTest(mBinaryExpression(Formula.RELIMAGE, eST, eS), POW(ty_T));
		
		doTest(mBinaryExpression(Formula.MAPSTO, eS, eT), CPROD(POW(ty_S), POW(ty_T)));

		doTest(mBinaryExpression(Formula.REL,   eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TREL,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.SREL,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.STREL, eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PFUN,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TFUN,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PINJ,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TINJ,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PSUR,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TSUR,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TBIJ,  eS, eT), POW(REL(ty_S, ty_T)));

		doTest(mAssociativeExpression(Formula.BUNION, eS, eS),     POW(ty_S));
		doTest(mAssociativeExpression(Formula.BUNION, eS, eS, eS), POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, eS, eS),     POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, eS, eS, eS), POW(ty_S));
		doTest(mBinaryExpression(Formula.SETMINUS, eS, eS), POW(ty_S));
		
		doTest(mBinaryExpression(Formula.CPROD, eS, eT), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DPROD, eST, eSU), REL(ty_S, CPROD(ty_T, ty_U)));
		
		doTest(mBinaryExpression(Formula.PPROD, eST, eUV), 
				REL(CPROD(ty_S, ty_U), CPROD(ty_T, ty_V)));
		
		doTest(mAssociativeExpression(Formula.BCOMP, eTU, eST),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.BCOMP, eUV, eTU, eST), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.FCOMP, eST, eTU),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.FCOMP, eST, eTU, eUV), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.OVR, eST, eST),      REL(ty_S, ty_T));
		doTest(mAssociativeExpression(Formula.OVR, eST, eST, eST), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DOMRES, eS, eST), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.DOMSUB, eS, eST), REL(ty_S, ty_T));

		doTest(mBinaryExpression(Formula.RANRES, eST, eT), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.RANSUB, eST, eT), REL(ty_S, ty_T));
		
		//-------------------
		//  Unary expressions
		//-------------------
		doTest(mUnaryExpression(Formula.CONVERSE, eST), REL(ty_T, ty_S));
		
		// doTest(mUnaryExpression(Formula.CARD, id_A), INT);

		doTest(mUnaryExpression(Formula.POW,  eS), POW(POW(ty_S)));
		doTest(mUnaryExpression(Formula.POW1, eS), POW(POW(ty_S)));
		
		doTest(mUnaryExpression(Formula.KUNION, ePS), POW(ty_S));
		doTest(mUnaryExpression(Formula.KINTER, ePS), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KDOM, eST), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KRAN, eST), POW(ty_T));
		
		doTest(ffV1.makeUnaryExpression(Formula.KPRJ1, eSTv1, null), REL(CPROD(ty_Sv1, ty_Tv1), ty_Sv1),
				ffV1);

		doTest(ffV1.makeUnaryExpression(Formula.KPRJ2, eSTv1, null), REL(CPROD(ty_Sv1, ty_Tv1), ty_Tv1),
				ffV1);

		doTest(ffV1.makeUnaryExpression(Formula.KID, eSv1, null), REL(ty_Sv1, ty_Sv1),
				ffV1);

		
		//--------------------
		//  Lambda expressions
		//--------------------
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x),
						mRelationalPredicate(Formula.EQUAL, b0S, eS),
						mMaplet(b0S, eT)),
				REL(POW(ty_S), POW(ty_T)));
		
		//------------------------
		//  Quantified expressions
		//------------------------
		doTest(mQuantifiedExpression(Formula.QUNION, QuantifiedExpression.Form.Explicit,
						mList(bd_x),
						mRelationalPredicate(Formula.EQUAL, b0S, eS),
						mSetExtension(eT)),
				POW(POW(ty_T)));
	
		//---------------
		//  Set extension
		//---------------
		doTest(mSetExtension(eS), POW(POW(ty_S)));

		//--------------------
		//  Atomic expressions
		//--------------------
		doTest(eS, POW(ty_S));
	}
	
	private void doTest(Expression expr, Type expected) {
		doTest(expr, expected, ALL_VERSION_FACTORIES);
	}
	
	private void doTest(Expression expr, FormulaFactory... factories) {
		doTest(expr, expr.getType(), factories);
	}

	private void doTest(Expression expr, Type expected, FormulaFactory... factories) {
		assertTrue("Input is not typed", expr.isTypeChecked());
		assertEquals("Bad type", expected, expr.getType());
		final String image = expr.toStringWithTypes();
		for (FormulaFactory factory : factories) {
			final Expression actual = parseExpression(image, factory);
			typeCheck(actual);
			assertEquals("Typed string is a different expression", expr, actual);
		}
	}

	/**
	 * Main test routine for predicates containing generic atomic operators.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	@Test 
	public void testPredicates () {
		
		AtomicExpression eS = mEmptySet(POW(ty_S));
		AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		
		RelationalPredicate atom = mRelationalPredicate(Formula.EQUAL, eS, eS);
		BoundIdentDecl bd_x = mBoundIdentDecl("x", POW(ty_S));
		BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));

		//--------------------
		//  Binary predicates
		//--------------------
		doTest(mBinaryPredicate(Formula.LIMP, atom, atom));
		doTest(mBinaryPredicate(Formula.LEQV, atom, atom));
		
		doTest(mAssociativePredicate(Formula.LAND, atom, atom));
		doTest(mAssociativePredicate(Formula.LAND, atom, atom, atom));
		doTest(mAssociativePredicate(Formula.LOR, atom, atom));
		doTest(mAssociativePredicate(Formula.LOR, atom, atom, atom));

		//-----------------
		//  Unary predicate
		//-----------------
		doTest(mUnaryPredicate(Formula.NOT, atom));

		//-----------------
		//  Multiple predicate
		//-----------------
		doTest(mMultiplePredicate(Formula.KPARTITION, eS), ff);
		doTest(mMultiplePredicate(Formula.KPARTITION, eS, eS), ff);

		//-----------------------
		//  Quantified predicates
		//-----------------------
		doTest(mQuantifiedPredicate(Formula.FORALL, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
		
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
		
		//--------------------
		//  Simple predicates
		//--------------------
		doTest(mSimplePredicate(eS));
		
		//-----------------------
		//  Relational predicates
		//-----------------------
		doTest(mRelationalPredicate(Formula.EQUAL, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTEQUAL, eS, eS));

		doTest(mRelationalPredicate(Formula.IN, eS, ePS));
		doTest(mRelationalPredicate(Formula.NOTIN, eS, ePS));

		doTest(mRelationalPredicate(Formula.SUBSET, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTSUBSET, eS, eS));
		doTest(mRelationalPredicate(Formula.SUBSETEQ, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTSUBSETEQ, eS, eS));

		//-------------------------------------
		//  Ensure no capture of given set name
		//-------------------------------------
		BoundIdentDecl bd_S = mBoundIdentDecl("S", POW(ty_S));
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_S),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
	}
	
	// test on all parser versions
	private void doTest(Predicate pred) {
		doTest(pred, ALL_VERSION_FACTORIES);
	}
	
	private void doTest(Predicate pred, FormulaFactory... factories) {
		assertTrue("Input is not typed", pred.isTypeChecked());
		final String image = pred.toStringWithTypes();
		for (FormulaFactory factory : factories){
			final Predicate actual = parsePredicate(image, factory);
			typeCheck(actual);
			assertEquals("Typed string is a different predicate", pred, actual);
		}
	}

	/**
	 * Main test routine for assignments.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	@Test 
	public void testAssignments () {
		
		AtomicExpression eS = mEmptySet(POW(ty_S));
		AtomicExpression eT = mEmptySet(POW(ty_T));
		AtomicExpression eU = mEmptySet(POW(ty_U));
		AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		
		
		FreeIdentifier id_x = mFreeIdentifier("x", POW(ty_S));
		FreeIdentifier id_y = mFreeIdentifier("y", POW(ty_T));
		FreeIdentifier id_z = mFreeIdentifier("z", POW(ty_U));

		BoundIdentDecl bd_x = mBoundIdentDecl("x'", POW(ty_S));
		BoundIdentDecl bd_y = mBoundIdentDecl("y'", POW(ty_T));
		BoundIdentDecl bd_z = mBoundIdentDecl("z'", POW(ty_U));
		
		BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));
		BoundIdentifier b0T = mBoundIdentifier(0, POW(ty_T));
		BoundIdentifier b0U = mBoundIdentifier(0, POW(ty_U));
		BoundIdentifier b1S = mBoundIdentifier(1, POW(ty_S));
		BoundIdentifier b1T = mBoundIdentifier(1, POW(ty_T));
		BoundIdentifier b2S = mBoundIdentifier(2, POW(ty_S));

		doTest(mBecomesEqualTo(id_x, eS));
		doTest(mBecomesEqualTo(mList(id_x, id_y), mList(eS, eT)));
		doTest(mBecomesEqualTo(mList(id_x, id_y, id_z), mList(eS, eT, eU)));
		
		doTest(mBecomesMemberOf(id_x, ePS));
		
		doTest(mBecomesSuchThat(mList(id_x), mList(bd_x), 
				mRelationalPredicate(Formula.EQUAL, b0S, eS)
		));
		doTest(mBecomesSuchThat(mList(id_x, id_y), mList(bd_x, bd_y),
				mRelationalPredicate(Formula.EQUAL,
						mMaplet(b1S,  b0T),
						mMaplet(eS, eT))
		));
		doTest(mBecomesSuchThat(mList(id_x, id_y, id_z), mList(bd_x, bd_y, bd_z),
				mRelationalPredicate(Formula.EQUAL,
						mMaplet(b2S, mMaplet(b1T, b0U)),
						mMaplet(eS,  mMaplet(eT,  eU)))
		));
	}

	private void doTest(Assignment assign) {
		assertTrue("Input is not typed", assign.isTypeChecked());
		final String image = assign.toStringWithTypes();
		for (FormulaFactory factory : ALL_VERSION_FACTORIES) {
			final Assignment actual = parseAssignment(image, factory);
			typeCheck(actual);
			assertEquals("Typed string is a different predicate", assign, actual);
		}
	}

	/**
	 * The other generic atomic expressions (KPRJ1_GEN, KPRJ2_GEN and KID_GEN)
	 * are parsed with the same code as empty set. We just ensure that they work
	 * in the simplest case.
	 */
	@Test 
	public void testOtherGenericAtomicExpressions() throws Exception {
		final Type rSTS = REL(CPROD(ty_S, ty_T), ty_S);
		doTest(mPrj1(rSTS), rSTS, ff);
		
		final Type rSTT = REL(CPROD(ty_S, ty_T), ty_T);
		doTest(mPrj2(rSTT), rSTT, ff);
		
		final Type rSS = REL(ty_S, ty_S);
		doTest(mId(rSS), rSS, ff);
	}

	/**
	 * Tests about a simple enumeration datatype. Here, the operators are atomic and
	 * non-generic, so we do not really need type annotations. However, it is useful
	 * to check that <code>toStringWithTypes</code> does not produce any parse
	 * problem.
	 */
	@Test
	public void enumeration() throws Exception {
		final IDatatype dt = DatatypeParser.parse(ff, "ENUM ::= c1 || c2");
		final FormulaFactory eff = dt.getFactory();

		final Type dtType = eff.makeParametricType(dt.getTypeConstructor());
		final Expression c1 = eff.makeExtendedExpression(dt.getConstructor("c1"), NO_EXPRS, NO_PREDS, null, dtType);
		final Expression c2 = eff.makeExtendedExpression(dt.getConstructor("c2"), NO_EXPRS, NO_PREDS, null, dtType);
		final Expression dtSet = eff.makeExtendedExpression(dt.getTypeConstructor(), NO_EXPRS, NO_PREDS, null,
				eff.makePowerSetType(dtType));
		final Predicate in = eff.makeRelationalPredicate(IN, c1, dtSet, null);

		doTest(dtSet, eff);
		doTest(c1, eff);
		doTest(c2, eff);
		doTest(in, eff);

		assertImage(dtSet, "ENUM");
		assertImage(c1, "c1");
		assertImage(c2, "c2");
	}

	/**
	 * Tests about a generic datatype with a single constructor. We do not really
	 * need type annotations. However, it is useful to check that
	 * <code>toStringWithTypes</code> does not produce any parse problem.
	 */
	@Test
	public void genericSingleConstructor() throws Exception {
		final IDatatype dt = DatatypeParser.parse(ff, "G[T] ::= c1[d1: T]");
		final FormulaFactory eff = dt.getFactory();
		final Type sType = eff.makeGivenType("S");
		final Type powSType = eff.makePowerSetType(sType);
		final Type dtType = eff.makeParametricType(dt.getTypeConstructor(), powSType);

		final Expression empty = eff.makeEmptySet(powSType, null);
		final Expression c1 = eff.makeExtendedExpression(dt.getConstructor("c1"), asList(empty), emptyList(), null,
				dtType);
		final Expression d1 = eff.makeExtendedExpression(dt.getDestructor("d1"), asList(c1), emptyList(), null,
				empty.getType());

		doTest(c1, eff);
		doTest(d1, eff);

		assertImage(c1, "c1(∅ ⦂ ℙ(S))");
		assertImage(d1, "d1(c1(∅ ⦂ ℙ(S)))");
	}

	/**
	 * Tests about a generic datatype with an atomic constructor which is generic.
	 * We do need type annotations.
	 */
	@Test
	public void genericAtomicConstructor() throws Exception {
		final FormulaFactory eff = LIST_FAC;
		final Type sType = eff.makeGivenType("S");
		final Type listSType = eff.makeParametricType(EXT_LIST, sType);

		final Expression nil = eff.makeExtendedExpression(EXT_NIL, NO_EXPRS, NO_PREDS, null, listSType);

		doTest(nil, eff);
		assertImage(nil, "nil ⦂ List(S)");
	}

	/**
	 * Tests <code>toStringWithTypes</code> on ill-typed formulas. The method should
	 * succeed without any type annotation as the type information is not present.
	 */
	@Test
	public void illTypedExpression() throws Exception {
		final FormulaFactory eff = LIST_FAC;
		final Expression empty = eff.makeEmptySet(null, null);
		assertImage(empty, "∅");

		final Expression nil = eff.makeExtendedExpression(EXT_NIL, NO_EXPRS, NO_PREDS, null);
		assertImage(nil, "nil");
	}

	/**
	 * Tests about the either datatype where both constructors need a type
	 * annotation. We also test other operators that also need type annotations.
	 */
	@Test
	public void eitherDatatype() throws Exception {
		final FormulaFactory eff = EITHER_FAC;
		final Type intType = eff.makeIntegerType();
		final Type boolType = eff.makeBooleanType();
		final Type either = eff.makeParametricType(EITHER_DT.getTypeConstructor(), intType, boolType);

		final var leftCons = EITHER_DT.getConstructor("Left");
		final Expression zero = eff.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression leftZero = eff.makeExtendedExpression(leftCons, //
				asList(zero), emptyList(), null, either);
		doTest(leftZero, eff);
		assertImage(leftZero, "Left(0) ⦂ either(ℤ,BOOL)");

		final Expression trueLit = eff.makeAtomicExpression(TRUE, null);
		final Expression returnTrue = eff.makeExtendedExpression(Return.EXT, //
				asList(trueLit), emptyList(), null, either);
		doTest(returnTrue, eff);
		assertImage(returnTrue, "return(TRUE) ⦂ either(ℤ,BOOL)");

		final Expression one = eff.makeIntegerLiteral(BigInteger.ONE, null);
		final Expression leftPlus = eff.makeExtendedExpression(LeftPlus.EXT, //
				asList(zero, one), emptyList(), null, either);
		doTest(leftPlus, eff);
		assertImage(leftPlus, "(0 ++ 1) ⦂ either(ℤ,BOOL)");

		final var rightDes = EITHER_DT.getDestructor("getRight");
		final Expression right = eff.makeExtendedExpression(rightDes, //
				asList(leftPlus), emptyList(), null);
		doTest(right, eff);
		assertImage(right, "getRight((0 ++ 1) ⦂ either(ℤ,BOOL))");
	}

	/**
	 * Ensures that old extensions not implementing the new
	 * {@link IExpressionExtension2} interface behave as before.
	 */
	@Test
	public void oldExtensions() {
		assertFalse(Real.EXT instanceof IExpressionExtension2);
		assertFalse(RealPlus.EXT instanceof IExpressionExtension2);

		final FormulaFactory eff = EXTS_FAC;
		Expression real = eff.makeExtendedExpression(Real.EXT, emptyList(), emptyList(), null);
		doTest(real, eff);
		assertImage(real, "(ℝ) ⦂ ℙ(ℝ)");

		Expression id1 = eff.makeFreeIdentifier("x", null, real.toType());
		Expression id2 = eff.makeFreeIdentifier("y", null, real.toType());
		Expression plus = eff.makeExtendedExpression(RealPlus.EXT, asList(id1, id2), emptyList(), null);
		Predicate in = eff.makeRelationalPredicate(IN, plus, real, null);
		doTest(in, eff);
		assertImage(in, "(x +. y)∈((ℝ) ⦂ ℙ(ℝ))");
	}

	private static void assertImage(Formula<?> formula, String expected) {
		assertEquals(expected, formula.toStringWithTypes());
	}
}
