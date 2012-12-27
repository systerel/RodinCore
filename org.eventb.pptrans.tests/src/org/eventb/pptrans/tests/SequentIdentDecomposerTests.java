/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.pptrans.Translator;
import org.junit.Test;

/**
 * Acceptance tests for identifier decomposition at the sequent level.
 * 
 * @see Translator#decomposeIdentifiers(ISimpleSequent)
 * @author Laurent Voisin
 */
public class SequentIdentDecomposerTests extends AbstractTranslationTests {

	/**
	 * Ensures that the same sequent is returned if no decomposition occurs.
	 */
	@Test
	public void testNoDecomposition() throws Exception {
		final ISimpleSequent sequent = make("a = 1", "∃x⦂ℤ·x∈A ∧ x↦3∈R");
		assertSame(sequent, Translator.decomposeIdentifiers(sequent));
	}

	/**
	 * Ensures that bound identifiers are decomposed in both hypothesis and
	 * goal.
	 */
	@Test
	public void testBoundDecomposition() throws Exception {
		final ISimpleSequent sequent = make("∃x⦂ℤ×ℤ·x∈A", "∃x⦂ℤ×ℤ·x∈B");
		final ISimpleSequent expected = make("∃x⦂ℤ,y⦂ℤ·x↦y∈A", "∃x⦂ℤ,y⦂ℤ·x↦y∈B");
		assertEquals(expected, Translator.decomposeIdentifiers(sequent));
	}

	/**
	 * Ensures that free identifiers are decomposed consistently in both
	 * hypothesis and goal.
	 */
	@Test
	public void testFreeDecomposition() throws Exception {
		final ISimpleSequent sequent = make("x=1↦2", "x=2↦3");
		final ISimpleSequent expected = make("x_1↦x_2=1↦2", "x_1↦x_2=2↦3");
		assertEquals(expected, Translator.decomposeIdentifiers(sequent));
	}

	/**
	 * Another free identifier decomposition test that raised a
	 * ConcurrentModificationException in Java 6.
	 */
	@Test
	public void testConcurrentModification() throws Exception {
		final ISimpleSequent sequent = make("a ∈ A × C", "A ⊆ {1,2}",
				"B ⊆ {1,2}", "a ∈ A × B", "B ⊆ C");
		final ISimpleSequent expected = make("a_1↦a_2 ∈ A × C", "A ⊆ {1,2}",
				"B ⊆ {1,2}", "a_1↦a_2 ∈ A × B", "B ⊆ C");
		assertEquals(expected, Translator.decomposeIdentifiers(sequent));
	}

	/**
	 * Ensures that math extensions are not supported.
	 */
	@Test
	public void testMathExtensions() throws Exception {
		final ISimpleSequent sequent = make(DT_FF, "p = dt");
		try {
			Translator.decomposeIdentifiers(sequent);
			fail("Should have raised an exception");
		} catch (UnsupportedOperationException e) {
			// success
		}
	}

}
