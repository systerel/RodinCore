/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
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
import static org.eventb.core.ast.Formula.EMPTYSET;
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
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRED;
import static org.eventb.core.ast.Formula.KPRJ1;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
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
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.ExtendedFormulas.EFF;
import static org.eventb.core.ast.tests.ExtendedFormulas.barL;
import static org.eventb.core.ast.tests.ExtendedFormulas.barS;
import static org.eventb.core.ast.tests.ExtendedFormulas.fooL;
import static org.eventb.core.ast.tests.ExtendedFormulas.fooS;
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
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mPredicateVariable;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

/**
 * Unit tests for WD strictness.
 * 
 * @author Laurent Voisin
 */
public class TestWDStrict extends AbstractTests {

	private static final BoundIdentDecl[] bids = mList(mBoundIdentDecl("x"));

	private static final FreeIdentifier id_x = mFreeIdentifier("x");

	private static final Expression empty = ff.makeEmptySet(null, null);

	private static final Predicate T = mLiteralPredicate(BTRUE);

	private static void assertWDStrict(Formula<?> formula) {
		assertTrue(formula.isWDStrict());
	}

	private static void assertNotWDStrict(Formula<?> formula) {
		assertFalse(formula.isWDStrict());
	}

	private static void assertWDStrict(Formula<?> formula, String posImage) {
		assertTrue(formula.isWDStrict(makePosition(posImage)));
	}

	private static void assertNotWDStrict(Formula<?> formula, String posImage) {
		assertFalse(formula.isWDStrict(makePosition(posImage)));
	}

	/**
	 * Ensures that WD strictness is correctly implemented for all predefined
	 * operators.
	 */
	@SuppressWarnings("deprecation")
	@Test 
	public void testWDStrict() {

		// FREE_IDENT
		assertWDStrict(mFreeIdentifier("x"));

		// BOUND_IDENT_DECL
		assertWDStrict(mBoundIdentDecl("x"));

		// BOUND_IDENT
		assertWDStrict(mBoundIdentifier(0));

		// INTLIT
		assertWDStrict(mIntegerLiteral());

		// SETEXT
		assertWDStrict(mSetExtension());

		// BECOMES_EQUAL_TO
		assertWDStrict(mBecomesEqualTo(id_x, mIntegerLiteral()));

		// BECOMES_MEMBER_OF
		assertWDStrict(mBecomesMemberOf(id_x, empty));

		// BECOMES_SUCH_THAT
		assertWDStrict(mBecomesSuchThat(mList(id_x), mLiteralPredicate()));

		// PREDICATE_VARIABLE
		assertWDStrict(mPredicateVariable("$P"));

		// EQUAL
		assertWDStrict(mRelationalPredicate(EQUAL, id_x, id_x));

		// NOTEQUAL
		assertWDStrict(mRelationalPredicate(NOTEQUAL, id_x, id_x));

		// LT
		assertWDStrict(mRelationalPredicate(LT, id_x, id_x));

		// LE
		assertWDStrict(mRelationalPredicate(LE, id_x, id_x));

		// GT
		assertWDStrict(mRelationalPredicate(GT, id_x, id_x));

		// GE
		assertWDStrict(mRelationalPredicate(GE, id_x, id_x));

		// IN
		assertWDStrict(mRelationalPredicate(IN, id_x, id_x));

		// NOTIN
		assertWDStrict(mRelationalPredicate(NOTIN, id_x, id_x));

		// SUBSET
		assertWDStrict(mRelationalPredicate(SUBSET, id_x, id_x));

		// NOTSUBSET
		assertWDStrict(mRelationalPredicate(NOTSUBSET, id_x, id_x));

		// SUBSETEQ
		assertWDStrict(mRelationalPredicate(SUBSETEQ, id_x, id_x));

		// NOTSUBSETEQ
		assertWDStrict(mRelationalPredicate(NOTSUBSETEQ, id_x, id_x));

		// MAPSTO
		assertWDStrict(mBinaryExpression(MAPSTO, id_x, id_x));

		// REL
		assertWDStrict(mBinaryExpression(REL, id_x, id_x));

		// TREL
		assertWDStrict(mBinaryExpression(TREL, id_x, id_x));

		// SREL
		assertWDStrict(mBinaryExpression(SREL, id_x, id_x));

		// STREL
		assertWDStrict(mBinaryExpression(STREL, id_x, id_x));

		// PFUN
		assertWDStrict(mBinaryExpression(PFUN, id_x, id_x));

		// TFUN
		assertWDStrict(mBinaryExpression(TFUN, id_x, id_x));

		// PINJ
		assertWDStrict(mBinaryExpression(PINJ, id_x, id_x));

		// TINJ
		assertWDStrict(mBinaryExpression(TINJ, id_x, id_x));

		// PSUR
		assertWDStrict(mBinaryExpression(PSUR, id_x, id_x));

		// TSUR
		assertWDStrict(mBinaryExpression(TSUR, id_x, id_x));

		// TBIJ
		assertWDStrict(mBinaryExpression(TBIJ, id_x, id_x));

		// SETMINUS
		assertWDStrict(mBinaryExpression(SETMINUS, id_x, id_x));

		// CPROD
		assertWDStrict(mBinaryExpression(CPROD, id_x, id_x));

		// DPROD
		assertWDStrict(mBinaryExpression(DPROD, id_x, id_x));

		// PPROD
		assertWDStrict(mBinaryExpression(PPROD, id_x, id_x));

		// DOMRES
		assertWDStrict(mBinaryExpression(DOMRES, id_x, id_x));

		// DOMSUB
		assertWDStrict(mBinaryExpression(DOMSUB, id_x, id_x));

		// RANRES
		assertWDStrict(mBinaryExpression(RANRES, id_x, id_x));

		// RANSUB
		assertWDStrict(mBinaryExpression(RANSUB, id_x, id_x));

		// UPTO
		assertWDStrict(mBinaryExpression(UPTO, id_x, id_x));

		// MINUS
		assertWDStrict(mBinaryExpression(MINUS, id_x, id_x));

		// DIV
		assertWDStrict(mBinaryExpression(DIV, id_x, id_x));

		// MOD
		assertWDStrict(mBinaryExpression(MOD, id_x, id_x));

		// EXPN
		assertWDStrict(mBinaryExpression(EXPN, id_x, id_x));

		// FUNIMAGE
		assertWDStrict(mBinaryExpression(FUNIMAGE, id_x, id_x));

		// RELIMAGE
		assertWDStrict(mBinaryExpression(RELIMAGE, id_x, id_x));

		// LIMP
		assertNotWDStrict(mBinaryPredicate(LIMP, T, T));

		// LEQV
		assertWDStrict(mBinaryPredicate(LEQV, T, T));

		// BUNION
		assertWDStrict(mAssociativeExpression(BUNION, id_x, id_x));

		// BINTER
		assertWDStrict(mAssociativeExpression(BINTER, id_x, id_x));

		// BCOMP
		assertWDStrict(mAssociativeExpression(BCOMP, id_x, id_x));

		// FCOMP
		assertWDStrict(mAssociativeExpression(FCOMP, id_x, id_x));

		// OVR
		assertWDStrict(mAssociativeExpression(OVR, id_x, id_x));

		// PLUS
		assertWDStrict(mAssociativeExpression(PLUS, id_x, id_x));

		// MUL
		assertWDStrict(mAssociativeExpression(MUL, id_x, id_x));

		// LAND
		assertNotWDStrict(mAssociativePredicate(LAND, T, T));

		// LOR
		assertNotWDStrict(mAssociativePredicate(LOR, T, T));

		// INTEGER
		assertWDStrict(mAtomicExpression(INTEGER));

		// NATURAL
		assertWDStrict(mAtomicExpression(NATURAL));

		// NATURAL1
		assertWDStrict(mAtomicExpression(NATURAL1));

		// BOOL
		assertWDStrict(mAtomicExpression(BOOL));

		// TRUE
		assertWDStrict(mAtomicExpression(TRUE));

		// FALSE
		assertWDStrict(mAtomicExpression(FALSE));

		// EMPTYSET
		assertWDStrict(mAtomicExpression(EMPTYSET));

		// KPRED
		assertWDStrict(mAtomicExpression(KPRED));

		// KSUCC
		assertWDStrict(mAtomicExpression(KSUCC));

		// KPRJ1_GEN
		assertWDStrict(mAtomicExpression(KPRJ1_GEN));

		// KPRJ2_GEN
		assertWDStrict(mAtomicExpression(KPRJ2_GEN));

		// KID_GEN
		assertWDStrict(mAtomicExpression(KID_GEN));

		// KBOOL
		assertWDStrict(mBoolExpression(T));

		// BTRUE
		assertWDStrict(mLiteralPredicate(BTRUE));

		// BFALSE
		assertWDStrict(mLiteralPredicate(BFALSE));

		// KFINITE
		assertWDStrict(mSimplePredicate(id_x));

		// NOT
		assertWDStrict(mUnaryPredicate(NOT, T));

		// KCARD
		assertWDStrict(mUnaryExpression(KCARD, id_x));

		// POW
		assertWDStrict(mUnaryExpression(POW, id_x));

		// POW1
		assertWDStrict(mUnaryExpression(POW1, id_x));

		// KUNION
		assertWDStrict(mUnaryExpression(KUNION, id_x));

		// KINTER
		assertWDStrict(mUnaryExpression(KINTER, id_x));

		// KDOM
		assertWDStrict(mUnaryExpression(KDOM, id_x));

		// KRAN
		assertWDStrict(mUnaryExpression(KRAN, id_x));

		// KPRJ1
		assertWDStrict(ffV1.makeUnaryExpression(KPRJ1, ffV1.makeFreeIdentifier("x", null), null));

		// KPRJ2
		assertWDStrict(ffV1.makeUnaryExpression(KPRJ2, ffV1.makeFreeIdentifier("x", null), null));

		// KID
		assertWDStrict(ffV1.makeUnaryExpression(KID, ffV1.makeFreeIdentifier("x", null), null));

		// KMIN
		assertWDStrict(mUnaryExpression(KMIN, id_x));

		// KMAX
		assertWDStrict(mUnaryExpression(KMAX, id_x));

		// CONVERSE
		assertWDStrict(mUnaryExpression(CONVERSE, id_x));

		// UNMINUS
		assertWDStrict(mUnaryExpression(UNMINUS, id_x));

		// QUNION
		assertNotWDStrict(mQuantifiedExpression(QUNION, Explicit, bids, T, id_x));

		// QINTER
		assertNotWDStrict(mQuantifiedExpression(QINTER, Explicit, bids, T, id_x));

		// CSET
		assertNotWDStrict(mQuantifiedExpression(CSET, Explicit, bids, T, id_x));

		// FORALL
		assertNotWDStrict(mQuantifiedPredicate(FORALL, bids, T));

		// EXISTS
		assertNotWDStrict(mQuantifiedPredicate(EXISTS, bids, T));

		// KPARTITION
		assertWDStrict(mMultiplePredicate(KPARTITION, id_x));
	}

	/**
	 * Ensures that WD strictness is correctly implemented for extension
	 * operators.
	 */
	@Test 
	public void testWDStrictExtensions() {
		final Expression idE_x = EFF.makeFreeIdentifier("x", null);
		final Expression[] TWO_EXPRS = new Expression[] { idE_x, idE_x };
		final Predicate TE = EFF.makeLiteralPredicate(BTRUE, null);
		final Predicate[] TWO_PREDS = new Predicate[] { TE, TE };

		assertWDStrict(EFF.makeExtendedPredicate(fooS, TWO_EXPRS, TWO_PREDS,
				null));
		assertWDStrict(EFF.makeExtendedExpression(barS, TWO_EXPRS, TWO_PREDS,
				null));
		assertNotWDStrict(EFF.makeExtendedPredicate(fooL, TWO_EXPRS, TWO_PREDS,
				null));
		assertNotWDStrict(EFF.makeExtendedExpression(barL, TWO_EXPRS,
				TWO_PREDS, null));
	}

	/**
	 * Ensures that WD strictness of positions is correctly implemented.
	 */
	@Test 
	public void testWDStrictPosition() {
		final Predicate P = mUnaryPredicate(NOT, mBinaryPredicate(LIMP, T, T));
		assertWDStrict(P, "");
		assertWDStrict(P, "0");
		assertNotWDStrict(P, "0.0");
		assertNotWDStrict(P, "0.1");
		assertNotWDStrict(P, "1");

		final Predicate Q = mUnaryPredicate(NOT, mBinaryPredicate(LEQV, T, T));
		assertWDStrict(Q, "");
		assertWDStrict(Q, "0");
		assertWDStrict(Q, "0.0");
		assertWDStrict(Q, "0.1");
		assertNotWDStrict(Q, "0.0.0");

		final Predicate R = mBinaryPredicate(LIMP, T, T);
		assertWDStrict(R, "");
		assertNotWDStrict(R, "0");
		assertNotWDStrict(R, "1");
	}

}
