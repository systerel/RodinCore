/*******************************************************************************
 * Copyright (c) 2012, 2021 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - added tests for predicate variables
 *     Université de Lorraine - tests for extension specialization
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.ExtensionHelper.DIFFERENT;
import static org.eventb.core.ast.tests.ExtensionHelper.DIFFERENT_PREFIX;
import static org.eventb.core.ast.tests.ExtensionHelper.DIRECT_PRODUCT;
import static org.eventb.core.ast.tests.ExtensionHelper.DIRECT_PRODUCT_FALSE_WD;
import static org.eventb.core.ast.tests.ExtensionHelper.DIRECT_PRODUCT_PREFIX;
import static org.eventb.core.ast.tests.ExtensionHelper.getAlphaExtension;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mPredicateVariable;
import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.junit.Test;

/**
 * Acceptance tests for specialization of formulas.
 * <p>
 * As most of the code is shared with rewriting, we do not check all cases, but
 * rather check that specialization is actually implemented for all sub-classes
 * of Formula, except assignments.
 * </p>
 * <p>
 * For each test, we specify a type environment, an input formula, a
 * specialization and the expected result of applying this specialization to the
 * input formula
 * </p>
 * 
 * @author Thomas Muller
 * @author Laurent Voisin
 * @author htson - Added tests for specializing predicate variables
 * @author Guillaume Verdier - tests for extension specialization
 */
public class TestFormulaSpecialization extends AbstractTests {

	/*
	 * Set of unused extensions to be used for constructing a different
	 * destination formula factory.
	 */
	private static final Set<IFormulaExtension> OTHER_EXTNS = EXTS_FAC
			.getExtensions();

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

	/*
	 * Type environment already initialized for most tests.
	 */
	private ITypeEnvironment te = mTypeEnvironment(
			"S=ℙ(S); T=ℙ(T); A=ℙ(S); a=S; b=T; c=S; d=T", ff);

	private ISpecialization spec = ff.makeSpecialization();

	/**
	 * Ensures that assignment specialization is not supported.
	 */
	@Test 
	public void testAssignment() {
		ITypeEnvironmentBuilder teb = mTypeEnvironment("a=ℤ",ff);
		final Assignment assign = parseAssignment("a ≔ a + 1");
		typeCheck(assign, teb);
		spec = mSpecialization(teb, "a := b");
		try {
			assign.specialize(spec);
			fail("Should have thrown an unsupported operation error");
		} catch (UnsupportedOperationException e) {
			// pass
		}
	}

	/**
	 * Ensures that an associative expression gets specialized.
	 */
	@Test 
	public void testAssociativeExpression() {
		assertExpressionSpecialization(te, //
				"A ∪ B ∪ C", //
				"S := ℤ || B := D",//
				"A ∪ D ∪ C");
	}

	/**
	 * Ensures that an associative predicate gets specialized.
	 */
	@Test 
	public void testAssociativePredicate() {
		assertPredicateSpecialization(te, //
				"a ∈ A ∧ b ∈ B ∧ c ∈ C", //
				"S := T || a := x",//
				"x ∈ A ∧ b ∈ B ∧ c ∈ C");
		assertPredicateSpecialization(te, //
				"a ∈ A ∧ b ∈ B ∧ $P", //
				"S := T || a := x || $P := c ∈ C",//
				"x ∈ A ∧ b ∈ B ∧ c ∈ C");
	}

	/**
	 * Ensures that an atomic expression gets specialized (including generic
	 * operators).
	 */
	@Test 
	public void testAtomicExpression() {
		assertExpressionSpecialization(te, "ℤ", "S := T", "ℤ");
		assertExpressionSpecialization(te, "∅⦂ℙ(S)", "S := T", "∅⦂ℙ(T)");
		assertExpressionSpecialization(te, "id⦂S↔S", "S := T", "id⦂T↔T");
		assertExpressionSpecialization(te, "prj1⦂S×T↔S", "S := U", "prj1⦂U×T↔U");
		assertExpressionSpecialization(te, "prj2⦂S×T↔T", "S := U", "prj2⦂U×T↔T");
	}

	/**
	 * Ensures that a binary expression gets specialized.
	 */
	@Test 
	public void testBinaryExpression() {
		assertExpressionSpecialization(te, "a ↦ b", "S := U || a := c", "c ↦ b");
	}

	/**
	 * Ensures that a binary predicate gets specialized.
	 */
	@Test 
	public void testBinaryPredicate() {
		assertPredicateSpecialization(te,//
				"a ∈ A ⇒ b ∈ B",//
				"S := T || a := c",//
				"c ∈ A ⇒ b ∈ B");
		assertPredicateSpecialization(te,//
				"a ∈ A ⇒ $P",//
				"S := T || a := c || $P := b ∈ B",//
				"c ∈ A ⇒ b ∈ B");
	}

	/**
	 * Ensures that a bool expression gets specialized.
	 */
	@Test 
	public void testBoolExpression() {
		assertExpressionSpecialization(te,//
				"bool(a ∈ A)",//
				"S := T || a := c",//
				"bool(c ∈ A)");
		assertExpressionSpecialization(te,//
				"bool($P)",//
				"$P := c ∈ A",//
				"bool(c ∈ A)");
	}

	/**
	 * Ensures that a bound identifier declaration gets specialized (type only).
	 */
	@Test 
	public void testBoundIdentDecl() {
		final BoundIdentDecl aS = mBoundIdentDecl("a", S);
		final BoundIdentDecl aT = mBoundIdentDecl("a", T).translate(EXTS_FAC);
		assertSpecialization(te, aS, "S := T", aT);
		assertSpecialization(te, aS, "S := T || a := b", aT);
	}

	/**
	 * Ensures that a bound identifier gets specialized (type only).
	 */
	@Test 
	public void testBoundIdentifier() {
		final Expression bS = mBoundIdentifier(0, S);
		final Expression bT = mBoundIdentifier(0, T).translate(EXTS_FAC);
		assertSpecialization(te, bS, "S := T", bT);
	}

	/**
	 * Tests that an extended expression gets specialized.
	 */
	@Test 
	public void testExtendedExpression() {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment(
				"S=ℙ(S); T=ℙ(T); V=ℙ(V); A=ℙ(S×T); B=ℙ(S×V)", extFac);
		te = teb.makeSnapshot();
		assertExpressionSpecialization(te, "A§B", "S := X", "A§B");
	}

	/**
	 * Tests that an extended predicate gets specialized.
	 */
	@Test 
	public void testExtendedPredicate() {
		final IPredicateExtension alphaExt = getAlphaExtension();
		final FormulaFactory extFac = FormulaFactory.getInstance(alphaExt);
		ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); a=S", extFac);
		te = teb.makeSnapshot();
		assertPredicateSpecialization(te,//
				"α(a∈A, a)",//
				"S := T || a := b",//
				"α(b∈A, b)");
		assertPredicateSpecialization(te,//
				"α($P, a)",//
				"S := T || a := b || $P := b∈A",//
				"α(b∈A, b)");
	}

	/**
	 * Ensures that a free identifier get specialized.
	 */
	@Test 
	public void testFreeIdentifier() {
		assertExpressionSpecialization(te, "a", "S := T", "a");
		assertExpressionSpecialization(te, "a", "S := T || b := d", "a");
		assertExpressionSpecialization(te, "a", "S := T || a := c", "c");
		assertExpressionSpecialization(te, "S", "S := T || a := c", "T");
	}

	/**
	 * Ensures that an integer literal is not modified by specialization.
	 */
	@Test 
	public void testIntegerLiteral() {
		assertExpressionSpecialization(te, "2", "S := T", "2");
	}

	/**
	 * Ensures that a literal predicate is not modified by specialization.
	 */
	@Test 
	public void testLiteralPredicate() {
		assertPredicateSpecialization(te, "⊤", "S := T", "⊤");
	}

	/**
	 * Ensures that a multiple predicate gets specialized.
	 */
	@Test 
	public void testMultiplePredicate() {
		assertPredicateSpecialization(te,//
				"partition(A, s, t, u)",//
				"s := x", "partition(A, x, t, u)");
	}

	/**
	 * Ensures that an predicate variables gets specialized.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPredicateVariable() {
		assertPredicateSpecialization(te,//
				"$P", "$P := $Q", "$Q");
		assertPredicateSpecialization(te,//
				"$P", "$P := x∈A", "x∈A");
	}

	/**
	 * Ensures that quantified expressions get specialized.
	 */
	@Test 
	public void testQuantifiedExpression() {
		assertExpressionSpecialization(te, "{x∣x∈A}", "S := T", "{x∣x∈A}");
		assertExpressionSpecialization(te, "{x⦂S·⊤∣x}", "S := T", "{x⦂T·⊤∣x}");
		assertExpressionSpecialization(te,//
				"{x∣x∈A}", "S := T || A := B", "{x∣x∈B}");
		assertExpressionSpecialization(te,//
				"{a∣a∈A}", "S := ℤ || a := 5 || A := {2}", "{a∣a∈{2}}");
		assertExpressionSpecialization(te, "{x∣x∈A}", "S := T", "{x∣x∈A}");
	}

	/**
	 * Ensures that quantified predicates get specialized.
	 */
	@Test 
	public void testQuantifiedPredicate() {
		assertPredicateSpecialization(te, "∀x⦂S·x∈A", "S := T", "∀x⦂T·x∈A");
		assertPredicateSpecialization(te, "∀x⦂S·⊤", "S := T", "∀x⦂T·⊤");
		assertPredicateSpecialization(te,//
				"∀x⦂S·x∈A", "S := T || A := B", "∀x⦂T·x∈B");
		assertPredicateSpecialization(te,//
				"∀a⦂S·a∈A", "S := ℤ || a := 5 || A := {2}", "∀a⦂ℤ·a∈{2}");
	}

	/**
	 * Ensures that a specialization containing non-locally bound identifiers
	 * properly renumbers de Bruijn indices. For this, we check that we get the
	 * same result as a free identifier substitution, when using a
	 * specialization that does not substitute types.
	 */
	@Test
	public void testQuantifiedPredicateNotFree() {
		final Predicate src = parsePredicate("∀x⦂S·x∈A", te);
		final FreeIdentifier sIdent = src.getFreeIdentifiers()[0];
		final BoundIdentifier expr = ff.makeBoundIdentifier(0, null, POW(S));
		final Map<FreeIdentifier, Expression> subst;
		subst = new HashMap<FreeIdentifier, Expression>();
		subst.put(sIdent, expr);
		final ISpecialization spe = EXTS_FAC.makeSpecialization();
		spe.put(sIdent, expr.translate(EXTS_FAC));
		final Predicate expected = src.substituteFreeIdents(subst).translate(
				EXTS_FAC);
		assertSpecialization(te, src, spe, expected);
	}

	/**
	 * Ensures that a relational predicate gets specialized.
	 */
	@Test 
	public void testRelationalPredicate() {
		assertPredicateSpecialization(te, "a ∈ S", "S := T || a := b", "b ∈ T");
	}

	/**
	 * Ensures that a set in extension gets specialized.
	 */
	@Test 
	public void testSetExtension() {
		assertExpressionSpecialization(te,//
				"{a, c, e}", "S := T || a := b", "{b, c, e}");
	}

	/**
	 * Ensures that an empty set in extension gets specialized.
	 */
	@Test 
	public void testEmptySetExtension() {
		assertPredicateSpecialization(te, "{} ⊆ A", "S := T", "{} ⊆ A");
	}

	/**
	 * Ensures that a simple predicate gets specialized.
	 */
	@Test 
	public void testSimplePredicate() {
		assertPredicateSpecialization(te,//
				"finite(A)", "S := T || A := B", "finite(B)");
	}

	/**
	 * Ensures that an unary expression gets specialized.
	 */
	@Test 
	public void testUnaryExpression() {
		assertExpressionSpecialization(te, //
				"card(A)", "S := T || A := B", "card(B)");
	}

	/**
	 * Ensures that an unary predicate gets specialized.
	 */
	@Test 
	public void testUnaryPredicate() {
		assertPredicateSpecialization(te,//
				"¬(a ∈ A)", "S := T || a := b", "¬(b ∈ A)");
	}

	/**
	 * Ensures that bound identifiers are correctly managed when specializing
	 * under a quantifier with a quantified formula.
	 */
	@Test 
	public void testBindings() {
		assertPredicateSpecialization(te,//
				"∀x⦂S·x↦1 ∈ y",//
				"S := T || y := {x↦z∣x∈A ∧ z∈B}",//
				"∀x⦂T·x↦1 ∈ {x↦z∣x∈A ∧ z∈B}");
	}

	/**
	 * Ensures that specializing a free identifier which has no substitution
	 * prevents adding later a substitution on a given type with the same name.
	 */
	@Test
	public void identBlocksType() {
		final FreeIdentifier src = mFreeIdentifier("S", INT_TYPE);
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(ff.makeGivenType(src.getName()), INT_TYPE);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := S", "S=ℤ");
	}

	/**
	 * Ensures that specializing a free identifier which has no substitution
	 * prevents adding later a substitution on the same identifier.
	 */
	@Test
	public void identBlocksIdent() {
		final FreeIdentifier src = mFreeIdentifier("S", INT_TYPE);
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(src, mFreeIdentifier("T", INT_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := S", "S=ℤ");
	}

	/**
	 * Ensures that specializing a free identifier which has no substitution
	 * prevents adding later a substitution on an identifier with the same name,
	 * but a different type.
	 */
	@Test
	public void identBlocksIdentDifferentType() {
		final FreeIdentifier src = mFreeIdentifier("S", INT_TYPE);
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(mFreeIdentifier(src.getName(), BOOL_TYPE),
					mFreeIdentifier("T", BOOL_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := S", "S=ℤ");
	}

	/**
	 * Ensures that specializing a predicate variable which has no substitution
	 * prevents adding later a substitution on the same predicate variable.
	 */
	@Test
	public void predBlocksPred() {
		final PredicateVariable P = mPredicateVariable("$P");
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(P, P.specialize(spe));

		assertFalse(spe.put(P, mLiteralPredicate()));
		SpecializationChecker.verify(spe, "", "$P := $P", "");
	}

	/**
	 * Ensures that specializing a formula containing a given type which has no
	 * substitution cannot conflict with an existing substitution.
	 */
	@Test
	public void typeBlocksFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("S", INT_TYPE), mIntegerLiteral(0));

		try {
			mFreeIdentifier("a", ff.makeGivenType("S")).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := 0", "");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which has no
	 * substitution cannot conflict with an existing substitution.
	 */
	@Test
	public void identBlocksFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mIntegerLiteral(0));

		try {
			mFreeIdentifier("a", ff.makeGivenType("S")).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := 0", "");
	}

	/**
	 * Ensures that specializing a formula containing a given type which gets
	 * substituted does not conflict with a substituted identifier.
	 */
	@Test
	public void typeDoesNotBlockFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(ff.makeGivenType("S"), ff.makeGivenType("T"));
		spe.put(mFreeIdentifier("b", INT_TYPE), mFreeIdentifier("S", INT_TYPE));

		assertEquals(mFreeIdentifier("a", ff.makeGivenType("T")),
				mFreeIdentifier("a", ff.makeGivenType("S")).specialize(spe));

		SpecializationChecker.verify(spe, //
				"b=ℤ; a=S", "S := T || b := S || a := a", "S=ℤ; a=T");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which gets
	 * substituted cannot conflict with an existing substitution.
	 */
	@Test
	public void identDoesNotBlockFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", ff.makeGivenType("S")),
				mFreeIdentifier("b", ff.makeGivenType("S")));
		spe.put(mFreeIdentifier("b", INT_TYPE), mFreeIdentifier("a", INT_TYPE));

		assertEquals(mFreeIdentifier("b", ff.makeGivenType("S")),
				mFreeIdentifier("a", ff.makeGivenType("S")).specialize(spe));

		SpecializationChecker.verify(spe, //
				"a=S; b=ℤ", //
				"a := b || b := a || S := S", //
				"b=S; a=ℤ");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which gets
	 * substituted cannot conflict with an existing substitution.
	 */
	@Test
	public void predDoesNotBlockFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", ff.makeGivenType("S")),
				mFreeIdentifier("b", ff.makeGivenType("S")));
		spe.put(mPredicateVariable("$P"),
				parsePredicate("a=0", mTypeEnvironment()));

		assertEquals(mFreeIdentifier("b", ff.makeGivenType("S")),
				mFreeIdentifier("a", ff.makeGivenType("S")).specialize(spe));

		SpecializationChecker.verify(spe, //
				"a=S", //
				"S := S || a := b || $P := a=0", //
				"b=S; a=ℤ");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which has no
	 * substitution prevents adding later a substitution using the same
	 * identifier in its replacement but with a different type.
	 */
	@Test
	public void identBlocksDstType() {
		final Expression src = mFreeIdentifier("T", INT_TYPE);
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(ff.makeGivenType("S"), ff.makeGivenType("T"));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "T=ℤ", "T := T", "T=ℤ");
	}

	/**
	 * Ensures that specializing a formula containing a given type which has no
	 * substitution prevents adding later a substitution using the same
	 * identifier in its replacement but with a different type.
	 */
	@Test
	public void typeBlocksDstIdent() {
		final Expression src = mFreeIdentifier("b", ff.makeGivenType("S"));
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(mFreeIdentifier("a", INT_TYPE),
					mFreeIdentifier("S", INT_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "b=S", "S := S || b := b", "b=S");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which has no
	 * substitution prevents adding later a substitution using the same
	 * identifier in its replacement but with a different type.
	 */
	@Test
	public void identBlocksDstIdent() {
		final Expression src = mFreeIdentifier("b", ff.makeGivenType("S"));
		final ISpecialization spe = ff.makeSpecialization();
		assertSame(src, src.specialize(spe));

		try {
			spe.put(mFreeIdentifier("a", INT_TYPE),
					mFreeIdentifier("b", INT_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "b=S", "S := S || b := b", "b=S");
	}

	/**
	 * Ensures that specializing a formula containing a given type which has no
	 * substitution cannot conflict with the right-hand side of an existing
	 * type substitution.
	 */
	@Test
	public void dstTypeBlocksFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("S", INT_TYPE));

		try {
			mFreeIdentifier("b", ff.makeGivenType("S")).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := S", "S=ℤ");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which has no
	 * substitution cannot conflict with the right-hand side of an existing
	 * identifier substitution.
	 */
	@Test
	public void dstIdentBlocksFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("b", INT_TYPE));

		try {
			mFreeIdentifier("b", ff.makeGivenType("S")).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := b", "b=ℤ");
	}

	/**
	 * Ensures that specializing a formula containing an identifier which has no
	 * substitution cannot conflict with the right-hand side of an existing
	 * predicate variable substitution.
	 */
	@Test
	public void dstPredBlocksFormulaSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mPredicateVariable("$P"),
				parsePredicate("b=0", mTypeEnvironment()));

		try {
			mFreeIdentifier("b", ff.makeGivenType("S")).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "", "$P := b=0", "b=ℤ");
	}

	/**
	 * Tests that an extended expression can be specialized with the same extension.
	 */
	@Test
	public void testSpecializeExprExtWithItself() {
		final FormulaFactory extFac = FormulaFactory.getInstance(DIRECT_PRODUCT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); T=ℙ(T); A=ℙ(S×T); B=ℙ(S×T)", extFac);
		assertExpressionSpecialization(teb, "A§B", "@DirectProduct := @DirectProduct", "A§B",
				extFac.withExtensions(OTHER_EXTNS));
	}

	/**
	 * Tests that an extended expression can be specialized with an extension that
	 * has a different WD.
	 */
	@Test
	public void testSpecializeExprExtDifferentWD() {
		final FormulaFactory extFac = FormulaFactory.getInstance(DIRECT_PRODUCT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); T=ℙ(T); A=ℙ(S×T); B=ℙ(S×T)", extFac);
		assertExpressionSpecialization(teb, "A§B", "@DirectProduct := @DirectProductFalseWD", "A§B",
				FormulaFactory.getInstance(DIRECT_PRODUCT_FALSE_WD));
	}

	/**
	 * Tests that an extended infix expression can be specialized with the same
	 * extension, but prefixed.
	 */
	@Test
	public void testSpecializeExprExtInfixPrefix() {
		final FormulaFactory extFac = FormulaFactory.getInstance(DIRECT_PRODUCT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); T=ℙ(T); A=ℙ(S×T); B=ℙ(S×T)", extFac);
		assertExpressionSpecialization(teb, "A§B", "@DirectProduct := @DirectProductPrefix", "§(A,B)",
				FormulaFactory.getInstance(DIRECT_PRODUCT_PREFIX));
	}

	/**
	 * Tests that an extended predicate can be specialized with the same extension.
	 */
	@Test
	public void testSpecializePredExtWithItself() {
		final FormulaFactory extFac = FormulaFactory.getInstance(DIFFERENT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); x=S; y=S", extFac);
		assertPredicateSpecialization(teb, "x<>y", "@Different := @Different", "x<>y",
				extFac.withExtensions(OTHER_EXTNS));
	}

	/**
	 * Tests that an extended infix predicate can be specialized with the same
	 * extension, but prefixed.
	 */
	@Test
	public void testSpecializePredExtInfixPrefix() {
		final FormulaFactory extFac = FormulaFactory.getInstance(DIFFERENT);
		final ITypeEnvironmentBuilder teb = mTypeEnvironment("S=ℙ(S); x=S; y=S", extFac);
		assertPredicateSpecialization(teb, "x<>y", "@Different := @DifferentPrefix", "<>(x, y)",
				FormulaFactory.getInstance(DIFFERENT_PREFIX));
	}

	private static void assertExpressionSpecialization(ITypeEnvironment typenv,
			String srcImage, String specImage, String expectedImage, FormulaFactory dstFac) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Expression src = parseExpression(srcImage, fac);
		final ITypeEnvironmentBuilder typenvb = typeCheck(src, typenv);
		final ISpecialization spec = mSpecialization(typenvb, specImage, dstFac);
		final Expression expected = parseExpression(expectedImage, dstFac);
		typeCheck(expected, typenvb.specialize(spec));
		assertSpecialization(typenvb, src, spec, expected);
	}

	private static void assertExpressionSpecialization(ITypeEnvironment typenv,
			String srcImage, String specImage, String expectedImage) {
		FormulaFactory dstFac = typenv.getFormulaFactory().withExtensions(OTHER_EXTNS);
		assertExpressionSpecialization(typenv, srcImage, specImage, expectedImage, dstFac);
	}

	private static void assertPredicateSpecialization(
			ITypeEnvironment baseTypenv, String srcImage, String specImage,
			String expectedImage, FormulaFactory dstFac) {
		final FormulaFactory fac = baseTypenv.getFormulaFactory();
		final Predicate src = parsePredicate(srcImage, fac);
		final ITypeEnvironment typenv = typeCheck(src, baseTypenv);
		final ISpecialization spec = mSpecialization(typenv, specImage, dstFac);
		final Predicate expected = parsePredicate(expectedImage, dstFac);
		typeCheck(expected, typenv.specialize(spec));
		assertSpecialization(typenv, src, spec, expected);
	}

	private static void assertPredicateSpecialization(
			ITypeEnvironment baseTypenv, String srcImage, String specImage,
			String expectedImage) {
		FormulaFactory dstFac = baseTypenv.getFormulaFactory().withExtensions(OTHER_EXTNS);
		assertPredicateSpecialization(baseTypenv, srcImage, specImage, expectedImage, dstFac);
	}

	private static <T extends Formula<T>> void assertSpecialization(
			ITypeEnvironment typenv, T src, String speImage, T expected) {
		final FormulaFactory dstFac = expected.getFactory();
		final ISpecialization spe = mSpecialization(typenv, speImage, dstFac);
		assertSpecialization(typenv, src, spe, expected);
	}

	private static <T extends Formula<T>> void assertSpecialization(
			ITypeEnvironment typenv, T src, ISpecialization spe, T expected) {
		final T actual = src.specialize(spe);
		assertEquals(expected, actual);

		final ISpecialization empty = mSpecialization(typenv, "");
		final T unchanged = src.specialize(empty);
		assertSame(src, unchanged);
	}

}
