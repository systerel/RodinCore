/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.displayString;
import static org.eventb.internal.pp.core.elements.terms.Util.mAnd;
import static org.eventb.internal.pp.core.elements.terms.Util.mBoundIdentDecl;
import static org.eventb.internal.pp.core.elements.terms.Util.mEqu;
import static org.eventb.internal.pp.core.elements.terms.Util.mFreeIdentifier;
import static org.eventb.internal.pp.core.elements.terms.Util.mImp;
import static org.eventb.internal.pp.core.elements.terms.Util.mIn;
import static org.eventb.internal.pp.core.elements.terms.Util.mNot;
import static org.eventb.internal.pp.core.elements.terms.Util.mOr;
import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.PredicateLoader;
import org.junit.Test;

public class TestPredicateLoader extends AbstractPPTest {

	private static final FreeIdentifier a = mFreeIdentifier("a", ty_A);
	private static final FreeIdentifier b = mFreeIdentifier("b", ty_A);
	private static final FreeIdentifier c = mFreeIdentifier("c", ty_A);
	private static final FreeIdentifier A = mFreeIdentifier("A", POW(ty_A));

	private static final RelationalPredicate la = mIn(a, A);
	private static final RelationalPredicate lb = mIn(b, A);
	private static final RelationalPredicate lc = mIn(c, A);

	private static final BoundIdentDecl[] x = {mBoundIdentDecl("x", ty_A)};
	
    @Test
	public void testNot() {
		doTest(mNot(la), "not P0[a, A]");
	}

	/*
	 * Test Not
	 *       |
	 *       *
	 */
    @Test
	public void testNotNot() {
		doTest(mNot(mNot(la)), "P0[a, A]");
	}

    @Test
	public void testNotOr() {
		doTest(mNot(mOr(la,lb)), 
				"not Ld1[a, A, b, A]\n" + 
				" P0[a, A]\n" + 
				" P0[b, A]"
		);
	}

    @Test
	public void testNotAnd() {
		doTest(mNot(mAnd(la,lb)), 
				"Ld1[a, A, b, A]\n" + 
				" not P0[a, A]\n" + 
				" not P0[b, A]"
		);
	}

    @Test
	public void testNotImp() {
		doTest(mNot(mImp(la,lb)), 
				"not Ld1[b, A, a, A]\n" + 
				" P0[b, A]\n" + 
				" not P0[a, A]"
		);
	}


    @Test
	public void testNotEqv() {
		doTest(mNot(mEqu(la,lb)), 
				"not Le1[a, A, b, A]\n" + 
				" P0[a, A]\n" + 
				" P0[b, A]"
		);
	}

    @Test
	public void testNotE() {
		doTest(mNot(mE(la)), 
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testNotF() {
		doTest(mNot(mF(la)), 
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

	/*
	 * test Exist 
	 * 		  |
	 * 		  *
	 */
    @Test
	public void testExistLi() {
		doTest(mE(la),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testExistNot() {
		doTest(mE(mNot(la)),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testExistOr() {
		doTest(mE(mOr(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Ld1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

    @Test
	public void testExistAnd() {
		doTest(mE(mAnd(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  not Ld1[a, A, b, A]\n" + 
				"   not P0[a, A]\n" + 
				"   not P0[b, A]"
		);
	}
    @Test
	public void testExistImp() {
		doTest(mE(mImp(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][b, A, a, A]\n" + 
				"  Ld1[b, A, a, A]\n" + 
				"   P0[b, A]\n" + 
				"   not P0[a, A]"
		);
	}


    @Test
	public void testExistEqv() {
		doTest(mE(mEqu(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Le1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

    @Test
	public void testExistE() {
		doTest(mE(mE(la)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

    @Test
	public void testExistF() {
		doTest(mE(mF(la)), 

				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

	/*
	 * Test Forall
	 *       |
	 *       *
	 */
    @Test
	public void testForallLi() {
		doTest(mF(la),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testForallNot() {
		doTest(mF(mNot(la)),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}
    @Test
	public void testForallOr() {
		doTest(mF(mOr(la,lb)),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Ld1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

    @Test
	public void testForallAnd() {
		doTest(mF(mAnd(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  not Ld1[a, A, b, A]\n" + 
				"   not P0[a, A]\n" + 
				"   not P0[b, A]"
		);
	}

    @Test
	public void testForallImp() {
		doTest(mF(mImp(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][b, A, a, A]\n" + 
				"  Ld1[b, A, a, A]\n" + 
				"   P0[b, A]\n" + 
				"   not P0[a, A]"
		);
	}

    @Test
	public void testForallEqv() {
		doTest(mF(mEqu(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Le1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

    @Test
	public void testForallE() {
		doTest(mF(mE(la)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

    @Test
	public void testForallF() {
		doTest(mF(mF(la)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

	/*
	 * test or
	 * 		/ \
	 * 	   li *
	 */
    @Test
	public void testOrNot() {
		doTest(mOr(lc,mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

    @Test
	public void testOrOr() {
		doTest(mOr(lc,mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

    @Test
	public void testOrAnd() {
		doTest(mOr(lc,mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

    @Test
	public void testOrImp() {
		doTest(mOr(lc,mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testOrEqv() {
		doTest(mOr(lc,mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

    @Test
	public void testOrE() {
		doTest(mOr(lc,mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

    @Test
	public void testOrF() {
		doTest(mOr(lc,mF(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

	/*
	 * test And
	 * 		/ \
	 * 	   li *
	 */
    @Test
	public void testAndNot() {
		doTest(mAnd(lc,mNot(la)),
				"not Ld1[a, A, c, A]\n" + 
				" P0[a, A]\n" + 
				" not P0[c, A]"
		);
	}

    @Test
	public void testAndOr() {
		doTest(mAnd(lc,mOr(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

    @Test
	public void testAndAnd() {
		doTest(mAnd(lc,mAnd(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

    @Test
	public void testAndImp() {
		doTest(mAnd(lc,mImp(la,lb)),
				"not Ld2[c, A, b, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" not Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testAndEqv() {
		doTest(mAnd(lc,mEqu(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" + 
				" not P0[c, A]\n" + 
				" not Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

    @Test
	public void testAndE() {
		doTest(mAnd(lc,mE(la)),
				"not Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   not P0[a, A]"
		);
	}

    @Test
	public void testAndF() {
		doTest(mAnd(lc,mF(la)),
				"not Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   not P0[a, A]"
		);
	}

	/*
	 * test <=>
	 * 		/ \
	 * 	   li *
	 */
    @Test
	public void testEquNot() {
		doTest(mEqu(lc,mNot(la)),
				"Le1[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" P0[a, A]"
		);
	}

    @Test
	public void testEquOr() {
		doTest(mEqu(lc,mOr(la,lb)),
				"Le2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

    @Test
	public void testEquAnd() {
		doTest(mEqu(lc,mAnd(la,lb)),
				"Le2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

    @Test
	public void testEquImp() {
		doTest(mEqu(lc,mImp(la,lb)),
				"Le2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testEquEqv() {
		doTest(mEqu(lc,mEqu(la,lb)),
				"Le2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

    @Test
	public void testEquE() {
		doTest(mEqu(lc,mE(la)),
				"Le2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

    @Test
	public void testEquF() {
		doTest(mEqu(lc,mF(la)),
				"Le2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

	/*
	 * test =>
	 * 		/ \
	 * 	   li *
	 */
    @Test
	public void testImpNot() {
		doTest(mImp(lc,mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

    @Test
	public void testImpOr() {
		doTest(mImp(lc,mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

    @Test
	public void testImpAnd() {
		doTest(mImp(lc,mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

    @Test
	public void testImpImp() {
		doTest(mImp(lc,mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testImpEqv() {
		doTest(mImp(lc,mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" not P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

    @Test
	public void testImpE() {
		doTest(mImp(lc,mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}
    @Test
	public void testImpF() {
		doTest(mImp(lc,mF(la)),
				"Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

	/*
	 * test =>
	 * 		/ \
	 * 	   not *
	 *      |
	 *      li
	 */
    @Test
	public void testNotLiImpNot() {
		doTest(mImp(mNot(lc),mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

    @Test
	public void testNotLiImpOr() {
		doTest(mImp(mNot(lc),mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

    @Test
	public void testNotLiImpAnd() {
		doTest(mImp(mNot(lc),mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

    @Test
	public void testNotLiImpImp() {
		doTest(mImp(mNot(lc),mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testNotLiImpEqv() {
		doTest(mImp(mNot(lc),mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

    @Test
	public void testNotLiImpE() {
		doTest(mImp(mNot(lc),mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

    @Test
	public void testNotLiImpF() {
		doTest(mImp(mNot(lc),mF(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

	/*
	 * Test  E
	 * 		 |
	 * 		 unary
	 */
    @Test
	public void testENot() {
		doTest(mE(mNot(la)),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testEE() {
		doTest(mE(mE(la)),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

    @Test
	public void testEF() {
		doTest(mE(mF(la)),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

	/*
	 * Test  F
	 * 		 |
	 * 		 unary
	 */
    @Test
	public void testFNot() {
		doTest(mF(mNot(la)),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

    @Test
	public void testFE() {
		doTest(mF(mE(la)),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

    @Test
	public void testFF() {
		doTest(mF(mF(la)),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

	/*
	 * Test  not
	 * 		  |
	 * 		 not
	 * 		  |
	 * 		 unary
	 */
    @Test
	public void testNotNotNot() {
		doTest(mNot(mNot(mNot(la))),
				"not P0[a, A]"
		);
	}

    @Test
	public void testNotNotE() {
		doTest(mNot(mNot(mE(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testNotNotF() {
		doTest(mNot(mNot(mF(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	/*
	 * Test  not
	 * 		  |
	 * 		  E
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testNotENot() {
		doTest(mNot(mE(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testNotEE() {
		doTest(mNot(mE(mE(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testNotEF() {
		doTest(mNot(mE(mF(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	/*
	 * Test  not
	 * 		  |
	 * 		  F
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testNotFNot() {
		doTest(mNot(mF(mNot(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testNotFE() {
		doTest(mNot(mF(mE(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testNotFF() {
		doTest(mNot(mF(mF(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	/*
	 * Test   E
	 * 		  |
	 * 		 not
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testENotNot() {
		doTest(mE(mNot(mNot(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testENotE() {
		doTest(mE(mNot(mE(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testENotF() {
		doTest(mE(mNot(mF(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	/*
	 * Test   E
	 * 		  |
	 * 		  E
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testEENot() {
		doTest(mE(mE(mNot(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testEEE() {
		doTest(mE(mE(mE(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

    @Test
	public void testEEF() {
		doTest(mE(mE(mF(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∀ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

	/*
	 * Test   E
	 * 		  |
	 * 		  F
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testEFNot() {
		doTest(mE(mF(mNot(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testEFE() {
		doTest(mE(mF(mE(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

    @Test
	public void testEFF() {
		doTest(mE(mF(mF(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∀ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

	/*
	 * Test   F
	 * 		  |
	 * 		 not
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testFNotNot() {
		doTest(mF(mNot(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testFNotE() {
		doTest(mF(mNot(mE(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testFNotF() {
		doTest(mF(mNot(mF(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	/*
	 * Test   F
	 * 		  |
	 * 		  E
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testFENot() {
		doTest(mF(mNot(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

    @Test
	public void testFEE() {
		doTest(mF(mE(mE(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

    @Test
	public void testFEF() {
		doTest(mF(mE(mF(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∀ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

	/*
	 * Test   F
	 * 		  |
	 * 		  F
	 * 		  |
	 * 		  unary
	 */
    @Test
	public void testFFNot() {
		doTest(mF(mF(mNot(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

    @Test
	public void testFFE() {
		doTest(mF(mF(mE(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

    @Test
	public void testFFF() {
		doTest(mF(mF(mF(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∀ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

	/*
	 * Others
	 */
    @Test
	public void test1() {
		doTest(mNot(mEqu(mF(la),lb)), 
				"not Le2[b, A, a, A]\n" + 
				" P0[b, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

    @Test
	public void test2() {
		doTest(mNot(mImp(mF(la),lb)), 
				"not Ld2[b, A, a, A]\n" + 
				" P0[b, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   not P0[a, A]"
		);
	}
	
	/*------------------------------------------------------------------------*/

	private Predicate mE(Predicate li2) {
		return Util.mE(x, li2);
	}

	private Predicate mF(Predicate li2) {
		return Util.mF(x, li2);
	}

	private void doTest(Predicate pred, String string) {
		doTest(pred, string, false);
	}

	private void doTest(Predicate pred, String result, boolean isGoal) {
		final AbstractContext aContext = new AbstractContext();
		final PredicateLoader loader = new PredicateLoader(aContext, pred, isGoal);
		loader.load();
		final INormalizedFormula res = loader.getResult();
		final String treeForm = res.getSignature().toTreeForm("");
		assertStringEquals(pred.toString(), result, treeForm);
	}

	private void assertStringEquals(String msg, String expected, String actual) {
		if (!expected.equals(actual)) {
			System.out.println(msg);
			System.out.println("Expected:\n" + expected);
			System.out.println("Got:\n" + displayString(actual, 2));
			assertEquals(msg, expected, actual);
		}
	}

}
