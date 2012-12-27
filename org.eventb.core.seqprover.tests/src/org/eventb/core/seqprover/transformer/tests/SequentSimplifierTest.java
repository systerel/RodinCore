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
package org.eventb.core.seqprover.transformer.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.core.seqprover.transformer.SimpleSequents.SimplificationOption;
import org.eventb.internal.core.seqprover.transformer.SequentSimplifier;
import org.junit.Test;

/**
 * Acceptance tests for class {@link SequentSimplifier}. Test methods are named
 * after rule names in both the PP translator specification (PR* and AR*) and
 * the rewrite rules on the event-B wiki.
 * 
 * @author Laurent Voisin
 */
public class SequentSimplifierTest extends AbstractTransformerTests {

	private static void assertTransformed(ISimpleSequent sequent,
			ISimpleSequent expected, SimplificationOption... options) {
		final ISimpleSequent actual = SimpleSequents.simplify(sequent,
				options);
		assertEquals(expected, actual);
	}

	private static void assertNotTransformed(ISimpleSequent sequent,
			SimplificationOption... options) {
		final ISimpleSequent actual = SimpleSequents.simplify(sequent,
				options);
		assertSame(sequent, actual);
	}

	/*
	 * Ensures that the predicate is rewritten as expected when the given option
	 * is present and is unchanged without the option (if any).
	 */
	private static void assertChanged(String srcImage, String expImage,
			SimplificationOption option) {
		final ISimpleSequent src = makeSequent(srcImage);
		final ISimpleSequent expected = makeSequent(expImage);
		if (option == null) {
			assertTransformed(src, expected);
			assertTransformed(src, expected, aggr);
		} else {
			assertTransformed(src, expected, option);
			assertNotTransformed(src);
		}
	}

	// Shortcut for simpler tests
	private static SimplificationOption aggr = SimplificationOption.aggressiveSimplification;

	@Test
	public void PR1_SIMP_SPECIAL_AND_BFALSE() {
		assertChanged("0=0 ∧ ⊥ ∧ 1=1", "⊥", null);
	}

	@Test
	public void PR2_SIMP_SPECIAL_OR_BTRUE() {
		assertChanged("0=0 ∨ ⊤ ∨ 1=1", "⊤", null);
	}

	@Test
	public void PR4_SIMP_SPECIAL_OR_BFALSE() {
		assertChanged("0=0 ∨ ⊥ ∨ 1=1", "0=0 ∨ 1=1", null);
	}

	@Test
	public void PR5_SIMP_SPECIAL_IMP_BTRUE_R() {
		assertChanged("0=0 ⇒ ⊤", "⊤", null);
	}

	@Test
	public void PR6_SIMP_SPECIAL_IMP_BFALSE_L() {
		assertChanged("⊥ ⇒ 0=0", "⊤", null);
	}

	@Test
	public void PR7_SIMP_SPECIAL_IMP_BTRUE_L() {
		assertChanged("⊤ ⇒ 0=0", "0=0", null);
	}

	@Test
	public void PR8_SIMP_SPECIAL_IMP_BFALSE_R() {
		assertChanged("0=0 ⇒ ⊤", "⊤", null);
	}

	@Test
	public void PR9_SIMP_SPECIAL_NOT_BTRUE() {
		assertChanged("¬⊤", "⊥", null);
	}

	@Test
	public void PR10_SIMP_SPECIAL_NOT_BFALSE() {
		assertChanged("¬⊥", "⊤", null);
	}

	@Test
	public void PR11_SIMP_NOT_NOT() {
		assertChanged("¬¬0=0", "0=0", null);
	}

	@Test
	public void PR12_SIMP_MULTI_EQV() {
		assertChanged("0=0 ⇔ 0=0", "⊤", null);
	}

	@Test
	public void PR13_SIMP_SPECIAL_EQV_BTRUE() {
		assertChanged("0=0 ⇔ ⊤", "0=0", null);
	}

	@Test
	public void PR14_SIMP_SPECIAL_EQV_BFALSE() {
		assertChanged("0=0 ⇔ ⊥", "¬0=0", null);
	}

	@Test
	public void PR15_SIMP_FORALL() {
		assertChanged("∀x⦂ℤ·0=0", "0=0", null);
	}

	@Test
	public void PR16_SIMP_EXISTS() {
		assertChanged("∃x⦂ℤ·0=0", "0=0", null);
	}

	@Test
	public void AR1_SIMP_MULTI_AND() {
		assertChanged("0=0 ∧ 0=0", "0=0", aggr);
	}

	@Test
	public void AR2_SIMP_MULTI_OR() {
		assertChanged("0=0 ∨ 0=0", "0=0", aggr);
	}

	@Test
	public void AR3_SIMP_MULTI_AND_NOT() {
		assertChanged("0=0 ∧ ¬0=0", "⊥", aggr);
	}

	@Test
	public void AR4_SIMP_MULTI_OR_NOT() {
		assertChanged("0=0 ∨ ¬0=0", "⊤", aggr);
	}

	@Test
	public void AR5_SIMP_MULTI_IMP() {
		assertChanged("0=0 ⇒ 0=0", "⊤", aggr);
	}

	@Test
	public void AR6_SIMP_MULTI_IMP_NOT_L() {
		assertChanged("¬0=0 ⇒ 0=0", "0=0", aggr);
	}

	@Test
	public void AR7_SIMP_MULTI_IMP_NOT_R() {
		assertChanged("0=0 ⇒ ¬0=0", "¬0=0", aggr);
	}

	@Test
	public void AR8_SIMP_MULTI_EQV_NOT() {
		assertChanged("0=0 ⇔ ¬0=0", "⊥", aggr);
	}

	@Test
	public void AR9_SIMP_MULTI_IMP_AND() {
		assertChanged("0=0 ∧ 1=1 ⇒ 0=0", "⊤", aggr);
	}

	@Test
	public void AR10_SIMP_MULTI_IMP_AND_NOT_R() {
		assertChanged("0=0 ∧ 1=1 ⇒ ¬0=0", "¬(0=0 ∧ 1=1)", aggr);
	}

	@Test
	public void AR11_SIMP_MULTI_IMP_AND_NOT_L() {
		assertChanged("¬0=0 ∧ 1=1 ⇒ 0=0", "¬(¬0=0 ∧ 1=1)", aggr);
	}

	@Test
	public void AR12_SIMP_FORALL_AND() {
		assertChanged("∀x·x=1 ∧ x=2", "(∀x·x=1) ∧ (∀x·x=2)", aggr);
	}

	@Test
	public void AR13_SIMP_EXISTS_OR() {
		assertChanged("∃x·x=1 ∨ x=2", "(∃x·x=1) ∨ (∃x·x=2)", aggr);
	}

	@Test
	public void AR14_SIMP_EXISTS_IMP() {
		assertChanged("∃x·x=1 ⇒ x=2", "(∀x·x=1) ⇒ (∃x·x=2)", aggr);
	}

}
