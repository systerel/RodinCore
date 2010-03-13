/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.sequent;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import org.eventb.internal.pp.CancellationChecker;
import org.eventb.internal.pp.sequent.InputSequent;
import org.eventb.pp.AbstractRodinTest;
import org.eventb.pp.TestSequent;
import org.junit.Test;

/**
 * Unit tests for class {@link InputSequent}.
 * 
 * @author Laurent Voisin
 */
public class TestInputSequent extends AbstractRodinTest {

	private static final List<String> NO_TE = emptyList();
	private static final CancellationChecker NO_CHECKER = CancellationChecker
			.newChecker(null);

	private static InputSequent makeInputSequent(final TestSequent s) {
		return new InputSequent(s.hypotheses(), s.goal(), ff, NO_CHECKER);
	}

	private static InputSequent makeInputSequent(final List<String> typenv,
			final List<String> hyps, final String goal) {
		return makeInputSequent(new TestSequent(typenv, hyps, goal));
	}

	/**
	 * Ensures that a closed sequent is properly normalized and translated.
	 */
	@Test
	public void closedSequent() throws Exception {
		final TestSequent sequent = new TestSequent(NO_TE, asList("⊤"), "⊤");
		final InputSequent is = makeInputSequent(sequent);
		is.translate();
		sequent.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that a sequent containing only scalar variables is properly
	 * normalized and translated.
	 */
	@Test
	public void simpleSequent() throws Exception {
		final InputSequent is = makeInputSequent(NO_TE, asList("a = 0"),
				"b = 1");
		is.translate();

		final TestSequent expected = new TestSequent(
				asList("a", "ℤ", "b", "ℤ"), asList("a = 0"), "b = 1");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that a sequent is properly normalized before translation.
	 */
	@Test
	public void normalization() throws Exception {
		final InputSequent is = makeInputSequent(NO_TE, asList("p = 0 ↦ TRUE"),
				"q = FALSE ↦ 1");
		is.translate();
		final TestSequent expected = new TestSequent(asList("p_1", "ℤ", "p_2",
				"BOOL", "q_1", "BOOL", "q_2", "ℤ"),
				asList("p_1 = 0 ∧ p_2 = TRUE"), "¬ q_1 = TRUE ∧ q_2 = 1");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that two occurrences of the same variable are normalized in the
	 * same way before translation.
	 */
	@Test
	public void normalizationDouble() throws Exception {
		final InputSequent is = makeInputSequent(NO_TE,
				asList("p = 0 ↦ 1  ↦ (2 ↦ 3)"), "p = q ↦ r");
		is.translate();
		final TestSequent expected = new TestSequent(NO_TE,
				asList("p_1 = 0 ∧ p_2 = 1 ∧ p_3 = 2 ∧ p_4 = 3"),
				"p_1 = q_1 ∧ p_2 = q_2 ∧ p_3 = r_1 ∧ p_4 = r_2");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that normalization takes care of name conflicts.
	 */
	@Test
	public void normalizationConflict() throws Exception {
		final InputSequent is = makeInputSequent(NO_TE, asList("p = 0 ↦ 1",
				"p_1 = 2"), "p_2 = TRUE");
		is.translate();
		final TestSequent expected = new TestSequent(NO_TE, asList(
				"p_3 = 0 ∧ p_4 = 1", "p_1 = 2"), "p_2 = TRUE");
		expected.assertTranslatedSequentOf(is);
	}

}
