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

import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CSET;
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
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class checking factory translation
 * 
 * @author Vincent Monfort
 */
public class TestFactoryTranslation extends AbstractTests {

	private static void assertToEqualsTargetPredSample(FormulaFactory from, FormulaFactory to){
		final String predstr = "∀X· ((((S ↔ S)  T)  (S  (S ⇸ T))) → S) ⤔ (((S ↣ T) ⤀(S ↠ T))⤖ S)⊆ X";
		final Predicate pred = from.parsePredicate(predstr,
				LanguageVersion.V2, null).getParsedPredicate();
		assertToEqualsTargetFactory(from, to, pred);
	}
	
	private static void assertToEqualsTargetFactory(FormulaFactory from, FormulaFactory to, Formula<?> f){
		Formula<?> f2 = f.translate(to);
		final FormulaFactory after = f2.getFactory();
		assertEquals("Formula has not been translated correctly: " + after
				+ " instead of " + to + " expected", to, after);
	}
	
	/**
	 * Compatible translation on a node from a factory with extensions to a
	 * factory with a subset of those extensions
	 */
	@Test 
	public void testCompatibleTranslationToSubSet() {
		assertToEqualsTargetPredSample(FastFactory.ff_extns, ff);
	}
	
	/**
	 * Compatible translation on a node from a factory with extensions to a
	 * factory with a superset of those extensions
	 */
	@Test 
	public void testCompatibleTranslationToSuperSet() {
		assertToEqualsTargetPredSample(ff, FastFactory.ff_extns);
	}


	/**
	 * Incompatible translation to a factory with an incompatible extension
	 * subset
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testIncompatibleTranslation() {
		final IntegerType INTe = ff_extns.makeIntegerType();
		final Expression nil = mListCons(INTe);
		nil.translate(ff);
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

	private static final Predicate EFFP = EFF.makeLiteralPredicate(Formula.BTRUE, null);
	private static final Predicate EFFQ = EFF.makeLiteralPredicate(Formula.BTRUE, null);

	
	/*----------------------------------------------------------------
	 *  NO POSSIBLE TRANSLATION OF ASSIGNEMENTS
	 *----------------------------------------------------------------*/

	@Test(expected=UnsupportedOperationException.class)
	public void becomesEqualTo() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBecomesEqualTo(mList(iS), mList(eS), null));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void becomesMember() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBecomesMemberOf(iS, eS, null));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void becomesSuchThat() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBecomesSuchThat(mList(iS, iT), mList(dS, dT), P, null));
	}


	/*----------------------------------------------------------------
	 *  TRANSLATION OF IDENTIFIER OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void boundIdentDecl() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBoundIdentDecl("x", null));
	}

	@Test
	public void freeIdentifier() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeFreeIdentifier("s", null, tS));
	}

	@Test
	public void boundIdentifier() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBoundIdentifier(0, null));
	}

	@Test
	public void predicateVariable() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makePredicateVariable(LEADING_SYMBOL + "p", null));
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF REGULAR PREDICATE OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void associativePredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeAssociativePredicate(LOR, mList(P, Q), null));
	}

	@Test
	public void binaryPredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBinaryPredicate(LIMP, P, Q, null));
	}

	@Test
	public void literalPredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeLiteralPredicate(Formula.BTRUE, null));
	}

	@Test
	public void multiplePredicate() {
		ff.makeMultiplePredicate(KPARTITION, mList(eS), null);
	}

	@Test
	public void quantifiedPredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeQuantifiedPredicate(FORALL, mList(dS), P, null));
	}

	@Test
	public void relationalPredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeRelationalPredicate(EQUAL, eT, eS, null));
	}

	@Test
	public void simplePredicate() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeSimplePredicate(KFINITE, eS, null));
	}

	@Test
	public void unaryPredicate_NullChild() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeUnaryPredicate(NOT, P, null));
	}

	/*----------------------------------------------------------------
	 *  TRANSLATION OF REGULAR EXPRESSION OBJECTS
	 *----------------------------------------------------------------*/

	@Test
	public void associativeExpression_OneChild() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeAssociativeExpression(BUNION, mList(eS, eT), null));
	}

	@Test
	public void atomicExpression() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeAtomicExpression(KID_GEN, null, null));
	}

	@Test
	public void emptySet() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeEmptySet(POW(tS), null));
	}

	@Test
	public void binaryExpression() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBinaryExpression(MAPSTO, eS, eS, null));
	}

	@Test
	public void boolExpression() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeBoolExpression(P, null));
	}

	@Test
	public void integerLiteral() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeIntegerLiteral(BigInteger.ONE, null));
	}

	@Test
	public void quantifiedExpression() {
		assertToEqualsTargetFactory(ff, ff_extns, ff.makeQuantifiedExpression(
				CSET, mList(dS), P, eS, null, Explicit));
	}

	@Test
	public void setExtension() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeSetExtension(mList(eS), null));
	}

	@Test
	public void unaryExpression() {
		assertToEqualsTargetFactory(ff, ff_extns,
				ff.makeUnaryExpression(KCARD, eS, null));
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF EXTENSION OBJECTS
	 *----------------------------------------------------------------*/
	private static FormulaFactory EFFPlus;
	
	@BeforeClass
	public static void initEFFPlus(){
		Set<IFormulaExtension> extns = EFF.getExtensions();
		extns.addAll(ff_extns.getExtensions());
		EFFPlus = FormulaFactory.getInstance(extns);
	}
	
	@Test
	public void extendedPredicate() {
		assertToEqualsTargetFactory(
				EFF,
				EFFPlus,
				EFF.makeExtendedPredicate(fooS, mList(EFFeS, EFFeT),
						mList(EFFP, EFFQ), null));
	}

	@Test
	public void extendedExpression() {
		ExtendedExpression extendedExpr = EFF.makeExtendedExpression(barS,
				mList(EFFeS, EFFeS), mList(EFFP, EFFP), null);
		assertToEqualsTargetFactory(EFF, EFFPlus, extendedExpr);
	}
	
}
