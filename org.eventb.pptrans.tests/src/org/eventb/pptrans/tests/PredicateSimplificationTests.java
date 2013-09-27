/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - test with simple sequents
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.tests.FastFactory;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.pptrans.Translator;
import org.junit.Test;

public class PredicateSimplificationTests extends AbstractTranslationTests {
	
	public static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, FastFactory.mTypeEnvironment());
	}

	public static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironmentBuilder te) {
		final ISimpleSequent sinput = make(te, input);
		final ISimpleSequent sexpected = make(te, expected);
		doTest(sinput, sexpected);
	}
	
	private static void doTest(ISimpleSequent input, ISimpleSequent expected) {
		final ISimpleSequent actual = SimpleSequents.simplify(input);
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	
	/**
	 * Tests for PR1
	 */
	@Test
	public void testPR1_simple () {
		doTest( "a>b ∧ ⊥ ∧ c>d",
				"⊥", false);
	}
	
	/**
	 * Tests for PR2
	 */
	@Test
	public void testPR2_simple () {
		doTest( "a>b ∨ ⊤ ∨ c>d",
				"⊤", false);
	}

	/**
	 * Tests for PR3
	 */
	@Test
	public void testPR3_simple () {
		doTest( "a>b ∧ ⊤ ∧ c>d",
				"a>b ∧ c>d", false);
	}

	@Test
	public void testPR3_recursive () {
		doTest( "(⊤ ∧ ⊤) ∧ ⊤ ∧ c>d",
				"c>d", false);
	}

	/**
	 * Tests for PR4
	 */
	@Test
	public void testPR4_simple () {
		doTest( "a>b ∨ ⊥ ∨ c>d",
				"a>b ∨ c>d", false);
	}

	@Test
	public void testPR4_recursive () {
		doTest( "(⊥ ∨ ⊥) ∨ ⊥ ∨ c>d",
				"c>d", false);
	}
	
	/**
	 * Tests for PR5
	 */
	@Test
	public void testPR5_simple () {
		doTest( "a>b ⇒ ⊤",
				"⊤", false);
	}

	/**
	 * Tests for PR6
	 */
	@Test
	public void testPR6_simple () {
		doTest( "⊥ ⇒ a>b",
				"⊤", false);
	}
	
	/**
	 * Tests for PR7
	 */
	@Test
	public void testPR7_simple () {
		doTest( "⊤ ⇒ a>b",
				"a>b", false);
	}

	@Test
	public void testPR7_recursive () {
		doTest( "⊤ ⇒ (⊤ ⇒ a>b)",
				"a>b", false);
	}

	/**
	 * Tests for PR8
	 */
	@Test
	public void testPR8_simple () {
		doTest( "a>b ⇒ ⊥",
				"¬(a>b)", false);
	}

	@Test
	public void testPR8_recursive () {
		doTest( "(a>b ⇒ ⊥) ⇒ ⊥",
				"a>b", false);
	}
	
	/**
	 * Tests for PR9
	 */
	@Test
	public void testPR9_simple() {
		doTest( "¬⊤",
				"⊥", false);
	}
	
	/**
	 * Tests for PR10
	 */
	@Test
	public void testPR10_simple() {
		doTest( "¬⊥",
				"⊤", false);
	}

	/**
	 * Tests for PR11
	 */
	@Test
	public void testPR11_simple() {
		doTest( "¬¬(a>b)",
				"a>b", false);
	}

	@Test
	public void testPR11_recursive() {
		doTest( "¬¬(¬¬(a>b))",
				"a>b", false);
	}

	@Test
	public void testPR11_recursive2() {
		doTest( "¬¬¬(a>b)",
				"¬(a>b)", false);
	}

	/**
	 * Tests for PR12
	 */
	@Test
	public void testPR12_simple() {
		doTest( "a>b ⇔ a>b",
				"⊤", false);
	}
	
	/**
	 * Tests for PR13
	 */
	@Test
	public void testPR13_simple() {
		doTest( "a>b ⇔ ⊤",
				"a>b", false);
	}

	@Test
	public void testPR13_recursive() {
		doTest( "(a>b⇔ ⊤) ⇔ ⊤",
				"a>b", false);
	}

	@Test
	public void testPR13_reversed() {
		doTest( "⊤ ⇔ a>b",
				"a>b", false);
	}

	/**
	 * Tests for PR14
	 */
	@Test
	public void testPR14_simple() {
		doTest( "a>b ⇔ ⊥",
				"¬(a>b)", false);
	}

	@Test
	public void testPR14_recursive() {
		doTest( "(a>b⇔ ⊥) ⇔ ⊥",
				"a>b", false);
	}

	@Test
	public void testPR14_reversed() {
		doTest( "⊥ ⇔ a>b",
				"¬(a>b)", false);
	}
	
	/**
	 * Tests for PR15
	 */
	@Test
	public void testPR15_simple() {
		doTest( "∀ x0⦂ℤ, x1⦂ℤ · ⊤",
			    "⊤", false);
	}
	
	@Test
	public void testPR15_simple2() {
		doTest( "∃ x0⦂ℤ, x1⦂ℤ · ⊤",
			    "⊤", false);
	}
	
	/**
	 * Tests for PR16
	 */
	@Test
	public void testPR16_simple() {
		doTest( "∀ x0⦂ℤ, x1⦂ℤ · ⊥",
				"⊥", false);
	}
	
	@Test
	public void testPR16_simple2() {
		doTest( "∃ x0⦂ℤ, x1⦂ℤ · ⊥",
				"⊥", false);
	}
}
