/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.extension;

import static org.eventb.core.ast.Formula.BOOL;
import static org.eventb.core.ast.FormulaFactory.getCond;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.tests.FastFactory.addToTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.tests.AbstractTests;
import org.eventb.internal.core.ast.extension.ExtensionTranslation;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for extension translation provided by {@link ExtensionTranslation}
 * .
 * 
 * @author Thomas Muller
 */
public class TestExtensionTranslation extends AbstractTests {

	private static final FormulaFactory COND_FAC = getInstance(getCond());

	private ITypeEnvironmentBuilder srcTypeEnv;
	private IExtensionTranslation translation;
	private ITypeEnvironmentBuilder trgTypeEnv;

	/**
	 * Common setup, that can be overridden by a specific test.
	 */
	@Before
	public void setUp() {
		setUp("", "");
	}

	/**
	 * Starts a translation with the given initial type environment.
	 */
	private void setUp(String srcTypenvImage, String trgTypenvImage) {
		srcTypeEnv = mTypeEnvironment(srcTypenvImage, EXTS_FAC);
		trgTypeEnv = mTypeEnvironment(trgTypenvImage, COND_FAC);
		translation = srcTypeEnv.makeExtensionTranslation();
	}

	/**
	 * Starts a translation with the given initial type environment.
	 */
	private void setUp(String srcTypenvImage, FormulaFactory srcFactory,
			String trgTypenvImage, FormulaFactory trgFactory) {
		srcTypeEnv = mTypeEnvironment(srcTypenvImage, srcFactory);
		trgTypeEnv = mTypeEnvironment(trgTypenvImage, trgFactory);
		translation = srcTypeEnv.makeExtensionTranslation();
	}

	/**
	 * Ensures that the And predicate extension is correctly translated.
	 */
	@Test
	public void simplePredicateAnd() {
		assertPredTranslation("∧∧(⊤)", "ext(bool(⊤))=TRUE", "ext=ℙ(BOOL×BOOL)");
	}

	/**
	 * Ensures that the belongs predicate extension is correctly translated.
	 */
	@Test
	public void simplePredicateBelongs() {
		assertPredTranslation("belongs(1, ⊤, {1,2})", //
				"belongs(1↦{1,2}↦bool(⊤)) = TRUE", //
				"belongs=ℤ×ℙ(ℤ)×BOOL↔BOOL");
	}

	/**
	 * Ensures that the expression of the form 'bool(exp = TRUE)' is rewritten
	 * to 'exp'.
	 */
	@Test
	public void simplifiedExpression() {
		assertPredTranslation("∧∧(⊤, belongs(1, ⊤, {1, 2}))", //
				"ext(bool(⊤) ↦ belongs(1 ↦ {1,2} ↦ bool(⊤)))=TRUE", //
				"ext=BOOL×BOOL↔BOOL; belongs=ℤ×ℙ(ℤ)×BOOL↔BOOL");
	}

	/**
	 * Ensures that nested predicate extensions are correctly translated.
	 */
	@Test
	public void nestedPredicate() {
		assertPredTranslation("belongs(TRUE, belongs(2, ⊤, ∅), ∅)", //
				"belongs0(TRUE ↦ ∅ ↦ belongs(2 ↦ ∅ ↦ bool(⊤)))=TRUE", //
				"belongs0=BOOL×ℙ(BOOL)×BOOL↔BOOL; belongs=ℤ×ℙ(ℤ)×BOOL↔BOOL");
	}

	/**
	 * Ensures that multiple predicate extensions are correctly translated.
	 */
	@Test
	public void multiplePredicate() {
		assertPredTranslation(
				"∧∧(⊤, ⊤) ∧ ∧∧(⊤, ⊤, ⊤)", //
				"ext(bool(⊤)↦bool(⊤))=TRUE ∧ ext0(bool(⊤)↦bool(⊤)↦bool(⊤))=TRUE", //
				"ext=BOOL×BOOL↔BOOL; ext0=BOOL×BOOL×BOOL↔BOOL");
		assertPredTranslation("∧∧(⊤, ⊤, ⊤, ⊤)", //
				"ext1(bool(⊤)↦bool(⊤)↦bool(⊤)↦bool(⊤))=TRUE", //
				"ext1=BOOL×BOOL×BOOL×BOOL↔BOOL");
	}

	/**
	 * Ensures that the union2 extension is correctly translated.
	 */
	@Test
	public void simpleExpressionUnion2() {
		assertExprTranslation("union2({1},{1,2},{3})", //
				"union2({1}↦{1,2}↦{3})", //
				"union2=ℙ(ℤ)×ℙ(ℤ)×ℙ(ℤ)↔ℙ(ℤ)");
	}

	/**
	 * Ensures that the empty extension is correctly translated.
	 */
	@Test
	public void simpleExpressionEmpty() {
		assertExprTranslation("empty⦂ℙ(ℤ)", "empty", "empty=ℙ(ℤ)");
		assertExprTranslation("empty⦂ℙ(BOOL)", "empty0", "empty0=ℙ(BOOL)");
	}

	/**
	 * Ensures that multiple expression extensions are correctly translated.
	 */
	@Test
	public void multipleExpression() {
		assertExprTranslation("empty⦂ℙ(ℤ)", "empty", "empty=ℙ(ℤ)");
		assertExprTranslation("union2({1},{1,2},{3})", //
				"union2({1}↦{1,2}↦{3})", //
				"union2=ℙ(ℤ)×ℙ(ℤ)×ℙ(ℤ)↔ℙ(ℤ)");
		assertExprTranslation("union2({1},{1,2})", //
				"union3({1}↦{1,2})", //
				"union3=ℙ(ℤ)×ℙ(ℤ)↔ℙ(ℤ)");
		assertExprTranslation("empty⦂ℙ(ℤ)", "empty", "");
	}

	/**
	 * Ensures that a formula containing mixed extensions is correctly
	 * translated.
	 */
	@Test
	public void mixed() {
		assertPredTranslation("belongs(1, ⊤, union2({1},{3}))", //
				"belongs(1↦union2({1}↦{3})↦bool(⊤)) = TRUE", //
				"union2=ℙ(ℤ)×ℙ(ℤ)↔ℙ(ℤ); belongs=ℤ×ℙ(ℤ)×BOOL↔BOOL");
	}

	/**
	 * Ensures that fresh names are used for the functions created by the
	 * translation.
	 */
	@Test
	public void namesAreFresh() {
		setUp("belongs0=ℙ(BOOL)", "belongs0=ℙ(BOOL)");

		assertPredTranslation("belongs(1, ⊤, ∅)", //
				"belongs(1↦∅↦bool(⊤)) = TRUE", //
				"belongs=ℤ×ℙ(ℤ)×BOOL↔BOOL");
		assertPredTranslation("belongs(TRUE, ⊤, ∅)", //
				"belongs1(TRUE↦∅↦bool(⊤)) = TRUE", //
				"belongs1=BOOL×ℙ(BOOL)×BOOL↔BOOL");
	}

	/**
	 * Ensures that datatype operators are not translated.
	 */
	@Test
	public void noDatatypeTranslation() {
		final FormulaFactory ffExtended = extendFactory();
		setUp("a=List(ℤ)", ffExtended, "a=List(ℤ)", LIST_FAC);
		assertPredTranslation("a = nil", "a = nil", "");
		assertPredTranslation("a = cons(1, nil)", "a = cons(1, nil)", "");
		assertPredTranslation("1 = head(a)", "1 = head(a)", "");
		assertPredTranslation("a ∈ List({1})", "a ∈ List({1})", "");
	}

	/**
	 * Ensures that the set of real numbers gets translated.
	 */
	@Test
	public void Real() {
		assertExprTranslation("ℝ", "ℝ", "ℝ=ℙ(ℝ)");
	}

	/**
	 * Ensures that the real zero gets translated.
	 */
	@Test
	public void RealZero() {
		assertExprTranslation("zero", "zero", "zero=ℝ");
	}

	/**
	 * Ensures that real addition gets translated.
	 */
	@Test
	public void RealPlus() {
		setUp("r=ℝ;s=ℝ", "r=ℝ;s=ℝ");
		assertExprTranslation("r +. s", "ext(r ↦ s)", "ext=ℝ×ℝ↔ℝ");
	}

	/**
	 * Ensures that real empty set gets translated.
	 */
	@Test
	public void RealEmpty() {
		assertExprTranslation("emptyR", "emptyR", "emptyR=ℙ(ℝ)");
	}

	private FormulaFactory extendFactory() {
		final Set<IFormulaExtension> extensions = LIST_FAC.getExtensions();
		extensions.addAll(EXTS_FAC.getExtensions());
		final FormulaFactory newff = FormulaFactory.getInstance(extensions);
		return newff;
	}

	private void assertPredTranslation(String srcImage, String trgImage,
			String typenvExtension) {
		final Predicate src = parsePredicate(srcImage, srcTypeEnv);
		extendTargetTypenv(typenvExtension);
		final Predicate expected = parsePredicate(trgImage, trgTypeEnv);
		assertTranslation(src, expected);
	}

	private void assertExprTranslation(String srcImage, String trgImage,
			String typenvExtension) {
		final Expression src = parseExpression(srcImage, srcTypeEnv);
		extendTargetTypenv(typenvExtension);
		final Expression expected = parseExpression(trgImage, trgTypeEnv);
		assertTranslation(src, expected);
	}

	private void extendTargetTypenv(String typenvExtension) {
		addToTypeEnvironment(trgTypeEnv, typenvExtension);
	}

	/*
	 * Ensures that we produce the expected formula and that the target type
	 * environment evolves as expected.
	 */
	private <T extends Formula<T>> void assertTranslation(T src, T expected) {
		selfVerifyExpected(expected);
		final T actual = src.translateExtensions(translation);
		assertEquals(trgTypeEnv.makeSnapshot(),
				translation.getTargetTypeEnvironment());
		assertEquals(expected, actual);
	}

	/*
	 * Ensures that the expected formula does indeed type-check within the
	 * target type environment.
	 */
	private <T extends Formula<T>> void selfVerifyExpected(T expected) {
		final ITypeCheckResult typeCheck = expected.typeCheck(trgTypeEnv);
		assertFalse(typeCheck.hasProblem());
		assertTrue(typeCheck.getInferredEnvironment().isEmpty());
	}

	public static class ExtensionTranslationErrors {

		final ISealedTypeEnvironment srcTypenv = mTypeEnvironment()
				.makeSnapshot();
		final ExtensionTranslation trans = new ExtensionTranslation(srcTypenv);

		final Expression untyped = mEmptySet(null);
		final Expression badFactory = LIST_FAC.makeAtomicExpression(BOOL, null);
		final Expression empty_S = mEmptySet(POW(ff.makeGivenType("S")));

		@Test(expected = IllegalStateException.class)
		public void notTyped() throws Exception {
			assertFalse(untyped.isTypeChecked());
			untyped.translateExtensions(trans);
		}

		@Test(expected = IllegalArgumentException.class)
		public void invalidFactory() throws Exception {
			assertTrue(badFactory.isTypeChecked());
			badFactory.translateExtensions(trans);
		}

		// The given set S is not in the source type environment
		@Test(expected = IllegalArgumentException.class)
		public void notInSourceTypenv() throws Exception {
			assertTrue(empty_S.isTypeChecked());
			empty_S.translateExtensions(trans);
		}

	}

}
