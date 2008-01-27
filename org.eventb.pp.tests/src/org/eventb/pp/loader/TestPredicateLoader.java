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

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.PredicateLoader;

public class TestPredicateLoader extends AbstractPPTest {

	private static final FreeIdentifier a = mFreeIdentifier("a", ty_A);
	private static final FreeIdentifier b = mFreeIdentifier("b", ty_A);
	private static final FreeIdentifier c = mFreeIdentifier("c", ty_A);
	private static final FreeIdentifier A = mFreeIdentifier("A", POW(ty_A));

	private static final RelationalPredicate la = mIn(a, A);
	private static final RelationalPredicate lb = mIn(b, A);
	private static final RelationalPredicate lc = mIn(c, A);

	private static final BoundIdentDecl[] x = {mBoundIdentDecl("x", ty_A)};
	
	public void testNot() {
		doTest(mNot(la), "not P0[a, A]");
	}

	/*
	 * Test Not
	 *       |
	 *       *
	 */
	public void testNotNot() {
		doTest(mNot(mNot(la)), "P0[a, A]");
	}

	public void testNotOr() {
		doTest(mNot(mOr(la,lb)), 
				"not Ld1[a, A, b, A]\n" + 
				" P0[a, A]\n" + 
				" P0[b, A]"
		);
	}

	public void testNotAnd() {
		doTest(mNot(mAnd(la,lb)), 
				"Ld1[a, A, b, A]\n" + 
				" not P0[a, A]\n" + 
				" not P0[b, A]"
		);
	}

	public void testNotImp() {
		doTest(mNot(mImp(la,lb)), 
				"not Ld1[b, A, a, A]\n" + 
				" P0[b, A]\n" + 
				" not P0[a, A]"
		);
	}


	public void testNotEqv() {
		doTest(mNot(mEqu(la,lb)), 
				"not Le1[a, A, b, A]\n" + 
				" P0[a, A]\n" + 
				" P0[b, A]"
		);
	}

	public void testNotE() {
		doTest(mNot(mE(la)), 
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

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
	public void testExistLi() {
		doTest(mE(la),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testExistNot() {
		doTest(mE(mNot(la)),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testExistOr() {
		doTest(mE(mOr(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Ld1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

	public void testExistAnd() {
		doTest(mE(mAnd(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  not Ld1[a, A, b, A]\n" + 
				"   not P0[a, A]\n" + 
				"   not P0[b, A]"
		);
	}
	public void testExistImp() {
		doTest(mE(mImp(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][b, A, a, A]\n" + 
				"  Ld1[b, A, a, A]\n" + 
				"   P0[b, A]\n" + 
				"   not P0[a, A]"
		);
	}


	public void testExistEqv() {
		doTest(mE(mEqu(la,lb)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Le1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

	public void testExistE() {
		doTest(mE(mE(la)), 
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

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
	public void testForallLi() {
		doTest(mF(la),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testForallNot() {
		doTest(mF(mNot(la)),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}
	public void testForallOr() {
		doTest(mF(mOr(la,lb)),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Ld1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

	public void testForallAnd() {
		doTest(mF(mAnd(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  not Ld1[a, A, b, A]\n" + 
				"   not P0[a, A]\n" + 
				"   not P0[b, A]"
		);
	}

	public void testForallImp() {
		doTest(mF(mImp(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][b, A, a, A]\n" + 
				"  Ld1[b, A, a, A]\n" + 
				"   P0[b, A]\n" + 
				"   not P0[a, A]"
		);
	}

	public void testForallEqv() {
		doTest(mF(mEqu(la,lb)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A)), {&}(A), {&}(ℙ(A))][a, A, b, A]\n" + 
				"  Le1[a, A, b, A]\n" + 
				"   P0[a, A]\n" + 
				"   P0[b, A]"
		);
	}

	public void testForallE() {
		doTest(mF(mE(la)), 
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

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
	public void testOrNot() {
		doTest(mOr(lc,mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

	public void testOrOr() {
		doTest(mOr(lc,mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

	public void testOrAnd() {
		doTest(mOr(lc,mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

	public void testOrImp() {
		doTest(mOr(lc,mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testOrEqv() {
		doTest(mOr(lc,mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

	public void testOrE() {
		doTest(mOr(lc,mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

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
	public void testAndNot() {
		doTest(mAnd(lc,mNot(la)),
				"not Ld1[a, A, c, A]\n" + 
				" P0[a, A]\n" + 
				" not P0[c, A]"
		);
	}

	public void testAndOr() {
		doTest(mAnd(lc,mOr(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

	public void testAndAnd() {
		doTest(mAnd(lc,mAnd(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

	public void testAndImp() {
		doTest(mAnd(lc,mImp(la,lb)),
				"not Ld2[c, A, b, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" not Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testAndEqv() {
		doTest(mAnd(lc,mEqu(la,lb)),
				"not Ld2[c, A, a, A, b, A]\n" + 
				" not P0[c, A]\n" + 
				" not Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

	public void testAndE() {
		doTest(mAnd(lc,mE(la)),
				"not Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   not P0[a, A]"
		);
	}

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
	public void testEquNot() {
		doTest(mEqu(lc,mNot(la)),
				"Le1[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" P0[a, A]"
		);
	}

	public void testEquOr() {
		doTest(mEqu(lc,mOr(la,lb)),
				"Le2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

	public void testEquAnd() {
		doTest(mEqu(lc,mAnd(la,lb)),
				"Le2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

	public void testEquImp() {
		doTest(mEqu(lc,mImp(la,lb)),
				"Le2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testEquEqv() {
		doTest(mEqu(lc,mEqu(la,lb)),
				"Le2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

	public void testEquE() {
		doTest(mEqu(lc,mE(la)),
				"Le2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

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
	public void testImpNot() {
		doTest(mImp(lc,mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

	public void testImpOr() {
		doTest(mImp(lc,mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

	public void testImpAnd() {
		doTest(mImp(lc,mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" not P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

	public void testImpImp() {
		doTest(mImp(lc,mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testImpEqv() {
		doTest(mImp(lc,mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" not P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

	public void testImpE() {
		doTest(mImp(lc,mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" not P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}
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
	public void testNotLiImpNot() {
		doTest(mImp(mNot(lc),mNot(la)),
				"Ld1[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" not P0[a, A]"
		);
	}

	public void testNotLiImpOr() {
		doTest(mImp(mNot(lc),mOr(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" Ld1[a, A, b, A]\n" +
				"  P0[a, A]\n" +
				"  P0[b, A]"
		);
	}

	public void testNotLiImpAnd() {
		doTest(mImp(mNot(lc),mAnd(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" +
				" P0[c, A]\n" +
				" not Ld1[a, A, b, A]\n" + 
				"  not P0[a, A]\n" + 
				"  not P0[b, A]"
		);
	}

	public void testNotLiImpImp() {
		doTest(mImp(mNot(lc),mImp(la,lb)),
				"Ld2[c, A, b, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" Ld1[b, A, a, A]\n" + 
				"  P0[b, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testNotLiImpEqv() {
		doTest(mImp(mNot(lc),mEqu(la,lb)),
				"Ld2[c, A, a, A, b, A]\n" + 
				" P0[c, A]\n" + 
				" Le1[a, A, b, A]\n" + 
				"  P0[a, A]\n" + 
				"  P0[b, A]"
		);
	}

	public void testNotLiImpE() {
		doTest(mImp(mNot(lc),mE(la)),
				"Ld2[c, A, a, A]\n" + 
				" P0[c, A]\n" + 
				" ∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

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
	public void testENot() {
		doTest(mE(mNot(la)),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testEE() {
		doTest(mE(mE(la)),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

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
	public void testFNot() {
		doTest(mF(mNot(la)),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  not P0[a, A]"
		);
	}

	public void testFE() {
		doTest(mF(mE(la)),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    P0[a, A]"
		);
	}

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
	public void testNotNotNot() {
		doTest(mNot(mNot(mNot(la))),
				"not P0[a, A]"
		);
	}

	public void testNotNotE() {
		doTest(mNot(mNot(mE(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

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
	public void testNotENot() {
		doTest(mNot(mE(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testNotEE() {
		doTest(mNot(mE(mE(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

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
	public void testNotFNot() {
		doTest(mNot(mF(mNot(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testNotFE() {
		doTest(mNot(mF(mE(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

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
	public void testENotNot() {
		doTest(mE(mNot(mNot(la))),
				"∃ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testENotE() {
		doTest(mE(mNot(mE(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

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
	public void testEENot() {
		doTest(mE(mE(mNot(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	public void testEEE() {
		doTest(mE(mE(mE(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

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
	public void testEFNot() {
		doTest(mE(mF(mNot(la))),
				"∃ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	public void testEFE() {
		doTest(mE(mF(mE(la))),
				"∃ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

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
	public void testFNotNot() {
		doTest(mF(mNot(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testFNotE() {
		doTest(mF(mNot(mE(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

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
	public void testFENot() {
		doTest(mF(mNot(mNot(la))),
				"∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  P0[a, A]"
		);
	}

	public void testFEE() {
		doTest(mF(mE(mE(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∃ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

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
	public void testFFNot() {
		doTest(mF(mF(mNot(la))),
				"∀ [0-0]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    not P0[a, A]"
		);
	}

	public void testFFE() {
		doTest(mF(mF(mE(la))),
				"∀ [0-0]Q3[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"  ∀ [1-1]Q2[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"    ∃ [2-2]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"      P0[a, A]"
		);
	}

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
	public void test1() {
		doTest(mNot(mEqu(mF(la),lb)), 
				"not Le2[b, A, a, A]\n" + 
				" P0[b, A]\n" + 
				" ∀ [0-0]Q1[{&}(A), {&}(ℙ(A))][a, A]\n" + 
				"   P0[a, A]"
		);
	}

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
