/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.eventb.core.ast.tests.ExtensionHelper.getAlphaExtension;
import static org.eventb.core.ast.tests.FastFactory.addToTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.TestGenParser.DIRECT_PRODUCT;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
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
 */
public class TestFormulaSpecialization extends AbstractTests {

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

	/*
	 * Type environment already initialized for most tests.
	 */
	private ISealedTypeEnvironment te = mTypeEnvironment(//
			"S", "ℙ(S)",//
			"T", "ℙ(T)",//
			"A", "ℙ(S)",//
			"a", "S",//
			"b", "T",//
			"c", "S",//
			"d", "T").makeSnapshot();

	private ISpecialization spec = ff.makeSpecialization();

	/**
	 * Ensures that assignment specialization is not supported.
	 */
	@Test 
	public void testAssignment() {
		ITypeEnvironmentBuilder teb = mTypeEnvironment("a", "ℤ");
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
	}

	/**
	 * Ensures that a bound identifier declaration gets specialized (type only).
	 */
	@Test 
	public void testBoundIdentDecl() {
		final BoundIdentDecl aS = mBoundIdentDecl("a", S);
		final BoundIdentDecl aT = mBoundIdentDecl("a", T);
		assertSpecialization(te, aS, "S := T", aT);
		assertSpecialization(te, aS, "S := T || a := b", aT);
	}

	/**
	 * Ensures that a bound identifier gets specialized (type only).
	 */
	@Test 
	public void testBoundIdentifier() {
		final Expression bS = FastFactory.mBoundIdentifier(0, S);
		final Expression bT = FastFactory.mBoundIdentifier(0, T);
		assertSpecialization(te, bS, "S := T", bT);
	}

	/**
	 * Tests that an extended expression gets specialized.
	 */
	@Test 
	public void testExtendedExpression() {
		final FormulaFactory extFac = FormulaFactory
				.getInstance(DIRECT_PRODUCT);
		final ITypeEnvironmentBuilder teb = extFac.makeTypeEnvironment();
		addToTypeEnvironment(teb, "S", "ℙ(S)", "T", "ℙ(T)", "V", "ℙ(V)",//
				"A", "ℙ(S×T)", "B", "ℙ(S×V)");
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
		ITypeEnvironmentBuilder teb = extFac.makeTypeEnvironment();
		addToTypeEnvironment(teb, "S", "ℙ(S)", "a", "S");
		te = teb.makeSnapshot();
		assertPredicateSpecialization(te,//
				"α(a∈A, a)",//
				"S := T || a := b",//
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

	private static void assertExpressionSpecialization(ISealedTypeEnvironment typenv,
			String srcImage, String specImage, String expectedImage) {
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Expression src = parseExpression(srcImage, fac);
		ITypeEnvironmentBuilder typenvb = typeCheck(src, typenv);
		final ISpecialization spec = mSpecialization(typenvb, specImage);
		final Expression expected = parseExpression(expectedImage, fac);
		typeCheck(expected, typenvb.specialize(spec));
		assertSpecialization(typenvb, src, spec, expected);
	}

	private static void assertPredicateSpecialization(
			ISealedTypeEnvironment baseTypenv, String srcImage, String specImage,
			String expectedImage) {
		ITypeEnvironment typenv = baseTypenv.makeBuilder();
		final FormulaFactory fac = typenv.getFormulaFactory();
		final Predicate src = parsePredicate(srcImage, fac);
		typenv = typeCheck(src, typenv);
		final ISpecialization spec = mSpecialization(typenv, specImage);
		final Predicate expected = parsePredicate(expectedImage, fac);
		typeCheck(expected, typenv.specialize(spec));
		assertSpecialization(typenv, src, spec, expected);
	}

	private static <T extends Formula<T>> void assertSpecialization(
			ITypeEnvironment typenv, T src, String speImage, T expected) {
		final ISpecialization spe = mSpecialization(typenv, speImage);
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
