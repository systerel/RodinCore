/*******************************************************************************
 * Copyright (c) 2013, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level.L1;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level;
import org.junit.Test;

/**
 * Unit tests for class {@link Variations}.
 * 
 * @author Josselin Dolhen
 */
public class VariationTests {

	// Type environment common to all tests
	private static final ISealedTypeEnvironment TYPENV = mTypeEnvironment( //
			"S=ℙ(S); A=ℙ(S); B=ℙ(S); a=S; b=S; n=ℤ; m=ℤ; p=BOOL").makeSnapshot();

	protected Variations variations;
	protected Level level;

	protected VariationTests(Variations variations) {
		this.variations = variations;
		this.level = variations.getLevel();
	}

	public VariationTests() {
		this(Variations.INSTANCE_L0);
	}

	/**
	 * Unit tests for {@link Variations#getWeakerPositive(Predicate)}. The first
	 * predicate implies all the others.
	 */
	@Test
	public void weakerPositiveVariations() {
		// Integer relations
		assertWeakerPositive("n = m", //
				"m = n", "n ≤ m", "n ≥ m", "m ≤ n", "m ≥ n");
		assertWeakerPositive("¬n = m", //
				"¬ m = n");
		assertWeakerPositive("n < m", //
				"m > n", "n ≤ m", "m ≥ n", "¬ n = m", "¬ m = n");
		assertWeakerPositive("n > m", //
				"m < n", "n ≥ m", "m ≤ n", "¬ n = m", "¬ m = n");
		assertWeakerPositive("n ≤ m", //
				"m ≥ n");
		assertWeakerPositive("n ≥ m", //
				"m ≤ n");
		// Set relations
		assertWeakerPositive("A = B", //
				"B = A", "A ⊆ B", "B ⊆ A", "¬ A ⊂ B", "¬ B ⊂ A");
		assertWeakerPositive("A ⊆ B", //
				"¬ B ⊂ A");
		assertWeakerPositive("A ⊂ B", //
				"A ⊆ B", "¬ B ⊂ A", "¬ B ⊆ A", "¬ A = B", "¬ B = A");
		assertWeakerPositive("¬ A = B", //
				"¬ B = A");
		assertWeakerPositive("¬ A ⊆ B", //
				"¬ A ⊂ B", "¬ A = B", "¬ B = A");
		assertWeakerPositive("¬ A ⊂ B");
		// Scalars equality
		assertWeakerPositive("a = b", //
				"b = a");
		assertWeakerPositive("¬ a = b", //
				"¬ b = a");
		// General case P
		assertWeakerPositive("finite(A)");
		// General case ¬P
		assertWeakerPositive("¬ finite(A)");
		// Natural numbers sets
		if (level.from(L1)) {
			assertWeakerPositive("12 ≤ n", "n ≥ 12", "11 < n", "n > 11", "n ∈ ℕ", "n ∈ ℕ1", "n ≥ 0", "0 ≤ n", "n > −1",
					"−1 < n", "n > 0", "0 < n", "n ≥ 1", "1 ≤ n");
			assertWeakerPositive("12 < n", "n > 12", "13 ≤ n", "n ≥ 13", "12 ≤ n", "n ≥ 12", "¬ 12 = n", "¬ n = 12",
					"n ∈ ℕ", "n ∈ ℕ1", "n ≥ 0", "0 ≤ n", "n > −1", "−1 < n", "n > 0", "0 < n", "n ≥ 1", "1 ≤ n");
			assertWeakerPositive("n ∈ ℕ", "n ≥ 0", "0 ≤ n", "n > −1", "−1 < n");
			assertWeakerPositive("n ∈ ℕ1", "n > 0", "0 < n", "n ≥ 1", "1 ≤ n", "¬ 0 = n", "¬ n = 0", "n ∈ ℕ", "n ≥ 0",
					"0 ≤ n", "n > −1", "−1 < n");
			assertWeakerPositive("−12 ≤ n", "n ≥ −12", "−13 < n", "n > −13");
			assertWeakerPositive("−12 < n", "n > −12", "−11 ≤ n", "n ≥ −11", "−12 ≤ n", "n ≥ −12", "¬ −12 = n", "¬ n = −12");
		} else {
			assertWeakerPositive("12 ≤ n", "n ≥ 12");
			assertWeakerPositive("12 < n", "n > 12", "12 ≤ n", "n ≥ 12", "¬ 12 = n", "¬ n = 12");
			assertWeakerPositive("n ∈ ℕ");
			assertWeakerPositive("n ∈ ℕ1");
			assertWeakerPositive("−12 ≤ n", "n ≥ −12");
			assertWeakerPositive("−12 < n", "n > −12", "−12 ≤ n", "n ≥ −12", "¬ −12 = n", "¬ n = −12");
		}
	}

	/**
	 * Unit tests for {@link Variations#getWeakerPositive(Predicate)}. The first
	 * predicate is implied by each of the others.
	 */
	@Test
	public void strongerPositiveVariations() {
		// Integer relations
		assertStrongerPositive("n = m", //
				"m = n");
		assertStrongerPositive("n < m", //
				"m > n");
		assertStrongerPositive("n > m", //
				"m < n");
		assertStrongerPositive("n ≤ m", //
				"m ≥ n", "n < m", "m > n", "n = m", "m = n");
		assertStrongerPositive("n ≥ m", //
				"m ≤ n", "n > m", "m < n", "n = m", "m = n");
		assertStrongerPositive("¬ n = m", //
				"¬ m = n", "m < n", "m > n", "n < m", "n > m");
		// Set relations
		assertStrongerPositive("A = B", //
				"B = A");
		assertStrongerPositive("A ⊆ B", //
				"A = B", "B = A", "A ⊂ B");
		assertStrongerPositive("A ⊂ B");
		assertStrongerPositive("¬ A = B", //
				"¬ B = A", "¬ A ⊆ B", "¬ B ⊆ A", "A ⊂ B", "B ⊂ A");
		assertStrongerPositive("¬ A ⊆ B", //
				"B ⊂ A");
		assertStrongerPositive("¬ A ⊂ B", //
				"¬ A ⊆ B", "B ⊂ A", "B ⊆ A", "A = B", "B = A");
		// Scalars equality
		assertStrongerPositive("a = b", //
				"b = a");
		assertStrongerPositive("¬ a = b", //
				"¬ b = a");
		// General case P
		assertStrongerPositive("finite(A)");
		// General case ¬P
		assertStrongerPositive("¬ finite(A)");
		// Natural numbers sets
		if (level.from(L1)) {
			assertStrongerPositive("−12 ≤ n", "n ≥ −12", "−13 < n", "n > −13", "−12 < n", "n > −12", "−12 = n",
					"n = −12", "n ∈ ℕ", "n ∈ ℕ1", "0 ≤ n", "n ≥ 0", "n > −1", "−1 < n", "n > 0", "0 < n", "n ≥ 1",
					"1 ≤ n");
			assertStrongerPositive("−12 < n", "n > −12", "−11 ≤ n", "n ≥ −11", "n ∈ ℕ", "n ∈ ℕ1", "0 ≤ n", "n ≥ 0",
					"n > −1", "−1 < n", "n > 0", "0 < n", "n ≥ 1", "1 ≤ n");
			assertStrongerPositive("n ∈ ℕ", "0 ≤ n", "n ≥ 0", "n ∈ ℕ1", "n > −1", "−1 < n", "n > 0", "0 < n", "n ≥ 1",
					"1 ≤ n");
			assertStrongerPositive("n ∈ ℕ1", "n > 0", "0 < n", "n ≥ 1", "1 ≤ n");
			assertStrongerPositive("¬ n = 0", "n < 0", "n > 0", "0 < n", "0 > n", "¬ 0 = n", "n ∈ ℕ1", "n ≥ 1",
					"1 ≤ n");
			assertStrongerPositive("¬ 0 = n", "n < 0", "n > 0", "0 < n", "0 > n", "¬ n = 0", "n ∈ ℕ1", "n ≥ 1",
					"1 ≤ n");
			assertStrongerPositive("12 ≤ n", "n ≥ 12", "11 < n", "n > 11", "12 < n", "n > 12", "12 = n", "n = 12");
			assertStrongerPositive("12 < n", "n > 12", "13 ≤ n", "n ≥ 13");
		} else {
			assertStrongerPositive("−12 ≤ n", "n ≥ −12", "−12 < n", "n > −12", "−12 = n", "n = −12");
			assertStrongerPositive("−12 < n", "n > −12");
			assertStrongerPositive("n ∈ ℕ");
			assertStrongerPositive("n ∈ ℕ1");
			assertStrongerPositive("¬ n = 0", "n < 0", "n > 0", "0 < n", "0 > n", "¬ 0 = n");
			assertStrongerPositive("¬ 0 = n", "n < 0", "n > 0", "0 < n", "0 > n", "¬ n = 0");
			assertStrongerPositive("12 ≤ n", "n ≥ 12", "12 < n", "n > 12", "12 = n", "n = 12");
			assertStrongerPositive("12 < n", "n > 12");
		}
	}

	/**
	 * Unit tests for {@link Variations#getWeakerNegative(Predicate)}. The
	 * negation of the first predicate implies all the others.
	 */
	@Test
	public void weakerNegativeVariations() {
		// Integer relations
		assertWeakerNegative("n = m", //
				"¬ m = n");
		assertWeakerNegative("n < m", //
				"n ≥ m", "m ≤ n");
		assertWeakerNegative("n > m", //
				"n ≤ m", "m ≥ n");
		assertWeakerNegative("n ≤ m", //
				"n > m", "m < n", "n ≥ m", "m ≤ n", "¬ n = m", "¬ m = n");
		assertWeakerNegative("n ≥ m", //
				"n < m", "m > n", "n ≤ m", "m ≥ n", "¬ n = m", "¬ m = n");
		assertWeakerNegative("¬ n = m", //
				"m = n", "n ≤ m", "m ≤ n", "m ≥ n", "n ≥ m");
		// Set relations
		assertWeakerNegative("A = B", //
				"¬ B = A");
		assertWeakerNegative("A ⊆ B", //
				"¬ A ⊂ B", "¬ A = B", "¬ B = A");
		assertWeakerNegative("A ⊂ B");
		assertWeakerNegative("¬ A = B", //
				"B = A", "A ⊆ B", "B ⊆ A", "¬ A ⊂ B", "¬ B ⊂ A");
		assertWeakerNegative("¬ A ⊆ B", //
				"¬ B ⊂ A");
		assertWeakerNegative("¬ A ⊂ B", //
				"A ⊆ B", "¬ B ⊆ A", "¬ B ⊂ A", "¬ A = B", "¬ B = A");
		// Scalars equality
		assertWeakerNegative("a = b", //
				"¬ b = a");
		assertWeakerNegative("¬ a = b", //
				"b = a");
		// General case P
		assertWeakerNegative("finite(A)");
		// General case ¬P
		assertWeakerNegative("¬ finite(A)");
		// Natural numbers sets
		if (level.from(L1)) {
			assertWeakerNegative("n ∈ ℕ", "n < 0", "0 > n", "n ≤ −1", "−1 ≥ n", "¬ n ∈ ℕ1", "n ≤ 0", "0 ≥ n", "n < 1", "1 > n");
			assertWeakerNegative("n ∈ ℕ1", "n ≤ 0", "0 ≥ n", "n < 1", "1 > n");
			assertWeakerNegative("12 ≤ n", "n ≤ 11", "11 ≥ n", "n < 12", "12 ≥ n", "n ≤ 12", "¬ 12 = n", "¬ n = 12");
			assertWeakerNegative("12 < n", "n ≤ 12", "13 > n", "n < 13");
			assertWeakerNegative("−12 < n", "n ≤ −12", "n < −11", "−11 > n");
			assertWeakerNegative("−12 ≤ n", "n ≤ −12", "n ≤ −13", "−13 ≥ n", "−12 ≥ n", "−12 > n", "n < −12", "¬ −12 = n", "¬ n = −12");
		} else {
			assertWeakerNegative("n ∈ ℕ");
			assertWeakerNegative("n ∈ ℕ1");
			assertWeakerNegative("12 ≤ n", "n < 12", "12 ≥ n", "n ≤ 12", "¬ 12 = n", "¬ n = 12");
			assertWeakerNegative("12 < n", "n ≤ 12");
			assertWeakerNegative("−12 < n", "n ≤ −12");
			assertWeakerNegative("−12 ≤ n", "n ≤ −12", "−12 ≥ n", "−12 > n", "n < −12", "¬ −12 = n", "¬ n = −12");
		}
	}

	/**
	 * Unit tests for {@link Variations#getStrongerNegative(Predicate)}. The
	 * negation of the first predicate is implied by each of the others.
	 */
	@Test
	public void strongerNegativeVariations() {
		// Integer relations
		assertStrongerNegative("n = m", //
				"¬ m = n", "n < m", "n > m", "m < n", "m > n");
		assertStrongerNegative("n < m", //
				"m ≤ n", "n > m", "m < n", "n = m", "m = n");
		assertStrongerNegative("n > m", //
				"m ≥ n", "n < m", "m > n", "n = m", "m = n");
		assertStrongerNegative("n ≤ m", //
				"m < n");
		assertStrongerNegative("n ≥ m", //
				"m > n");
		assertStrongerNegative("n = m", //
				"¬ m = n", "n > m", "n < m", "m < n", "m > n");
		// Set relations
		assertStrongerNegative("A = B", //
				"¬ B = A", "A ⊂ B", "B ⊂ A", "¬ A ⊆ B", "¬ B ⊆ A");
		assertStrongerNegative("A ⊆ B", //
				"B ⊂ A");
		assertStrongerNegative("A ⊂ B", //
				"¬ A ⊆ B", "B ⊆ A", "B ⊂ A", "A = B", "B = A");
		assertStrongerNegative("¬ A = B", //
				"B = A");
		assertStrongerNegative("¬ A ⊆ B", //
				"A ⊆ B", "A ⊂ B", "A = B", "B = A");
		assertStrongerNegative("¬ A ⊂ B");
		// Scalars equality
		assertStrongerNegative("a = b", //
				"¬ b = a");
		assertStrongerNegative("¬ a = b", //
				"b = a");
		// General case P
		assertStrongerNegative("finite(A)");
		// General case ¬P
		assertStrongerNegative("¬ finite(A)");
		// Natural numbers sets
		if (level.from(L1)) {
			assertStrongerNegative("n ∈ ℕ", "0 > n", "n < 0", "n ≤ −1", "−1 ≥ n");
			assertStrongerNegative("n ∈ ℕ1", "n ≤ 0", "0 ≥ n", "n < 1", "1 > n", "¬ n ∈ ℕ", "0 > n", "n < 0", "n ≤ −1",
					"−1 ≥ n");
			assertStrongerNegative("−12 ≤ n", "−12 > n", "n < −12", "n ≤ −13", "−13 ≥ n");
			assertStrongerNegative("−12 < n", "n ≤ −12", "−11 > n", "n < −11", "−12 > n", "n < −12", "−12 = n", "n = −12");
			assertStrongerNegative("12 ≤ n", "n < 12", "n ≤ 11", "11 ≥ n");
			assertStrongerNegative("12 < n", "n ≤ 12", "13 > n", "n < 13", "12 > n", "n < 12", "12 = n", "n = 12");
		} else {
			assertStrongerNegative("n ∈ ℕ");
			assertStrongerNegative("n ∈ ℕ1");
			assertStrongerNegative("−12 ≤ n", "−12 > n", "n < −12");
			assertStrongerNegative("−12 < n", "n ≤ −12", "−12 > n", "n < −12", "−12 = n", "n = −12");
			assertStrongerNegative("12 ≤ n", "n < 12");
			assertStrongerNegative("12 < n", "n ≤ 12", "12 > n", "n < 12", "12 = n", "n = 12");
		}
	}

	/**
	 * Unit tests for {@link Variations#getEquivalent(Predicate)}.The first
	 * predicate is equivalent to each of the others.
	 */
	@Test
	public void equivalentVariations() {
		// Integer relations
		assertEquivalent("n = m", "m = n");
		assertEquivalent("n < m", "m > n");
		assertEquivalent("n > m", "m < n");
		assertEquivalent("n ≤ m", "m ≥ n");
		assertEquivalent("n ≥ m", "m ≤ n");
		assertEquivalent("¬ n = m", "¬ m = n");
		// Set relations
		assertEquivalent("A = B", "B = A");
		assertEquivalent("A ⊆ B");
		assertEquivalent("A ⊂ B");
		assertEquivalent("¬ A = B", "¬ B = A");
		assertEquivalent("¬ A ⊆ B");
		assertEquivalent("¬ A ⊂ B");
		// Scalars equality
		assertEquivalent("a = b", "b = a");
		assertEquivalent("¬ a = b", "¬ b = a");
		// General case P
		assertEquivalent("finite(A)");
		// General case ¬P
		assertEquivalent("¬ finite(A)");
		// Natural numbers sets
		if (level.from(L1)) {
			assertEquivalent("n ∈ ℕ", "0 ≤ n", "n ≥ 0", "n > −1", "−1 < n");
			assertEquivalent("n ∈ ℕ1", "0 < n", "n > 0", "n ≥ 1", "1 ≤ n");
			assertEquivalent("¬ n ∈ ℕ", "0 > n", "n < 0", "n ≤ −1", "−1 ≥ n");
			assertEquivalent("¬ n ∈ ℕ1", "0 ≥ n", "n ≤ 0", "n < 1", "1 > n");
			assertEquivalent("0 ≤ n", "n ≥ 0", "n > −1", "−1 < n", "n ∈ ℕ");
			assertEquivalent("n ≥ 0", "0 ≤ n", "n > −1", "−1 < n", "n ∈ ℕ");
			assertEquivalent("0 < n", "n > 0", "n ≥ 1", "1 ≤ n", "n ∈ ℕ1");
			assertEquivalent("n > 0", "0 < n", "n ≥ 1", "1 ≤ n", "n ∈ ℕ1");
			assertEquivalent("12 ≤ n", "n ≥ 12", "n > 11", "11 < n");
			assertEquivalent("12 < n", "n > 12", "n ≥ 13", "13 ≤ n");
			assertEquivalent("−12 ≤ n", "n ≥ −12", "n > −13", "−13 < n");
			assertEquivalent("−12 < n", "n > −12", "−11 ≤ n", "n ≥ −11");
			assertEquivalent("p = TRUE", "TRUE = p", "¬ p = FALSE", "¬ FALSE = p");
			assertEquivalent("p = FALSE", "FALSE = p", "¬ p = TRUE", "¬ TRUE = p");
		} else {
			assertEquivalent("n ∈ ℕ");
			assertEquivalent("n ∈ ℕ1");
			assertEquivalent("¬ n ∈ ℕ");
			assertEquivalent("¬ n ∈ ℕ1");
			assertEquivalent("0 ≤ n", "n ≥ 0");
			assertEquivalent("n ≥ 0", "0 ≤ n");
			assertEquivalent("0 < n", "n > 0");
			assertEquivalent("n > 0", "0 < n");
			assertEquivalent("12 ≤ n", "n ≥ 12");
			assertEquivalent("12 < n", "n > 12");
			assertEquivalent("−12 ≤ n", "n ≥ −12");
			assertEquivalent("−12 < n", "n > −12");
			assertEquivalent("p = TRUE", "TRUE = p");
			assertEquivalent("p = FALSE", "FALSE = p");
		}
	}

	private void assertStrongerPositive(String predImage,
			String... expectedImages) {
		new StrongerPositiveCase().runTest(variations, predImage, expectedImages);
	}

	private void assertWeakerPositive(String predImage,
			String... expectedImages) {
		new WeakerPositiveCase().runTest(variations, predImage, expectedImages);
	}

	private void assertStrongerNegative(String predImage,
			String... expectedImages) {
		new StrongerNegativeCase().runTest(variations, predImage, expectedImages);
	}

	private void assertWeakerNegative(String predImage,
			String... expectedImages) {
		new WeakerNegativeCase().runTest(variations, predImage, expectedImages);
	}

	private void assertEquivalent(String predImage, String... expectedImages) {
		new EquivalentCase().runTest(variations, predImage, expectedImages);
	}

	/*
	 * Captures the code common to all tests, add a sub-class for each
	 * Variations method to test.
	 */
	private static abstract class TestCase {

		public void runTest(Variations variations, String predImage, String... expectedImages) {
			final Predicate pred = mPred(predImage);
			final List<Predicate> actual = getActual(variations, pred);
			final Set<Predicate> expected = makeSet(
					getExpectedFromSource(pred), expectedImages);
			assertEqualsSet(expected, actual);
		}

		protected abstract List<Predicate> getActual(Variations variations, Predicate pred);

		protected abstract Predicate getExpectedFromSource(Predicate pred);

		private Set<Predicate> makeSet(Predicate pred, String[] expectedImages) {
			final Set<Predicate> result = new HashSet<Predicate>();
			result.add(pred);
			for (final String expectedImage : expectedImages) {
				result.add(mPred(expectedImage));
			}
			return result;
		}

		private Predicate mPred(String expectedImage) {
			return genPred(TYPENV, expectedImage);
		}

		private void assertEqualsSet(Set<Predicate> expected,
				List<Predicate> actual) {
			assertEquals(expected, new HashSet<Predicate>(actual));
		}

	}

	private static class WeakerPositiveCase extends TestCase {

		protected List<Predicate> getActual(Variations variations, final Predicate pred) {
			return variations.getWeakerPositive(pred);
		}

		protected Predicate getExpectedFromSource(Predicate pred) {
			return pred;
		}

	}

	private static class StrongerPositiveCase extends TestCase {

		protected List<Predicate> getActual(Variations variations, final Predicate pred) {
			return variations.getStrongerPositive(pred);
		}

		protected Predicate getExpectedFromSource(Predicate pred) {
			return pred;
		}

	}

	private static class StrongerNegativeCase extends TestCase {

		protected List<Predicate> getActual(Variations variations, final Predicate pred) {
			return variations.getStrongerNegative(pred);
		}

		protected Predicate getExpectedFromSource(Predicate pred) {
			return makeNormalizedNeg(pred);
		}

	}

	private static class WeakerNegativeCase extends TestCase {

		protected List<Predicate> getActual(Variations variations, final Predicate pred) {
			return variations.getWeakerNegative(pred);
		}

		protected Predicate getExpectedFromSource(Predicate pred) {
			return makeNormalizedNeg(pred);
		}

	}

	private static class EquivalentCase extends TestCase {

		protected List<Predicate> getActual(Variations variations, final Predicate pred) {
			return variations.getEquivalent(pred);
		}

		protected Predicate getExpectedFromSource(Predicate pred) {
			return pred;
		}

	}

	// Normalizes the predicate specified
	public static Predicate makeNormalizedNeg(Predicate pred) {
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final Expression rhs = relPred.getRight();
			final Expression lhs = relPred.getLeft();

			switch (relPred.getTag()) {
			case LT:
				return makePredicate(GE, lhs, rhs);
			case GT:
				return makePredicate(LE, lhs, rhs);
			case LE:
				return makePredicate(GT, lhs, rhs);
			case GE:
				return makePredicate(LT, lhs, rhs);
			default:
				break;
			}
		}
		return makeNeg(pred);
	}

	private static RelationalPredicate makePredicate(int tag, Expression left,
			Expression right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

}