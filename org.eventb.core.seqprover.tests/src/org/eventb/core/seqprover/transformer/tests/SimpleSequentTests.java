/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer.tests;

import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeGoal;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.junit.Test;

/**
 * Unit tests for interface {@link ISimpleSequent}.
 * 
 * @author Laurent Voisin
 */
public class SimpleSequentTests extends AbstractTransformerTests {

	private static void assertEmpty(Predicate goal, Predicate... hyps) {
		assertEmpty(makeSequent(goal, hyps));
	}

	private static void assertEmpty(ISimpleSequent sequent) {
		assertSame(ff, sequent.getFormulaFactory());
		assertTrue(sequent.getTypeEnvironment().isEmpty());
		assertEquals(0, sequent.getPredicates().length);
		assertNull(sequent.getTrivialPredicate());
	}

	private static void assertGoalOnly(Predicate goal, Predicate... hyps) {
		final ISimpleSequent sequent = makeSequent(goal, hyps);
		assertSame(ff, sequent.getFormulaFactory());
		assertTrue(sequent.getTypeEnvironment().isEmpty());
		final ITrackedPredicate[] predicates = sequent.getPredicates();
		assertEquals(1, predicates.length);
		assertFalse(predicates[0].isHypothesis());
		assertEquals(goal, predicates[0].getOriginal());
	}

	private static void assertTrivial(Predicate goal, Predicate... hyps) {
		final ISimpleSequent sequent = makeSequent(goal, hyps);
		assertSame(ff, sequent.getFormulaFactory());
		assertTrue(sequent.getTypeEnvironment().isEmpty());
		final ITrackedPredicate trivial = sequent.getTrivialPredicate();
		assertNotNull(trivial);
		assertTrue(trivial.holdsTrivially());

		final ITrackedPredicate[] predicates = sequent.getPredicates();
		assertEquals(1, predicates.length);
		assertEquals(trivial, predicates[0]);
	}

	private static void assertRegular(Predicate goal, Predicate... hyps) {
		final ISimpleSequent sequent = makeSequent(goal, hyps);
		assertSame(ff, sequent.getFormulaFactory());
		assertTrue(sequent.getTypeEnvironment().isEmpty());
		final ITrackedPredicate[] expecteds = makeExpecteds(goal, hyps);
		assertNull(sequent.getTrivialPredicate());
		assertArrayEquals(expecteds, sequent.getPredicates());
	}

	private static ITrackedPredicate[] makeExpecteds(Predicate goal,
			Predicate... hyps) {
		List<ITrackedPredicate> preds = new ArrayList<ITrackedPredicate>();
		for (Predicate hyp : hyps) {
			preds.add(makeHyp(hyp));
		}
		preds.add(makeGoal(goal));
		return preds.toArray(new ITrackedPredicate[preds.size()]);
	}

	private static void assertTypeEnvironment(String typenvImage,
			String goalImage, String... hypImages) {
		final ITypeEnvironmentBuilder expected = mTypeEnvironment(typenvImage);
		final ITypeEnvironmentBuilder typenv = expected.makeBuilder(); // Preserve expected
		final ISimpleSequent sequent = makeSequent(typenv, goalImage, hypImages);
		assertEquals(expected, typenv); // Ensure no change to environment
		assertEquals(expected, sequent.getTypeEnvironment().makeBuilder());
	}

	/**
	 * Ensures that empty simple sequents can be created.
	 */
	@Test
	public void emptySequent() {
		assertEmpty(FALSE);
		assertEmpty(null, (Predicate[]) null);
		assertEmpty(FALSE, TRUE);
		assertEmpty(FALSE, TRUE, TRUE);
		assertEmpty(null, TRUE, null);
	}

	/**
	 * Ensures that sequents reduced to a goal can be created.
	 */
	@Test
	public void goalOnly() {
		assertGoalOnly(P0);
		assertGoalOnly(P0, (Predicate[]) null);
		assertGoalOnly(P0, TRUE);
		assertGoalOnly(P0, TRUE, null);
	}

	/**
	 * Ensures that trivial sequents can be created.
	 */
	@Test
	public void trivial() {
		assertTrivial(TRUE);
		assertTrivial(TRUE, FALSE);
		assertTrivial(TRUE, P0);
		assertTrivial(TRUE, P0, TRUE);

		assertTrivial(P0, FALSE);
		assertTrivial(P0, TRUE, FALSE);
		assertTrivial(FALSE, P0, FALSE, TRUE);
	}

	/**
	 * Ensures that regular sequents can be created.
	 */
	@Test
	public void regular() {
		assertRegular(P0, P1);
		assertRegular(P0, P0, P1);
		assertRegular(P0, P1, P1);
	}

	/**
	 * Ensures that type environments are correctly inferred.
	 */
	@Test
	public void typeEnvironment() {
		// Simple identifier
		assertTypeEnvironment("A=ℤ", "A=1");
		assertTypeEnvironment("A=ℤ", "⊥", "A=1");

		// Merging
		assertTypeEnvironment("A=ℤ; B=BOOL", "A=1", "B=TRUE");

		// Hidden carrier set
		assertTypeEnvironment("S=ℙ(S)", "(∅⦂ℙ(S))=∅");
		assertTypeEnvironment("S=ℙ(S)", "⊥", "(∅⦂ℙ(S))=∅");

		// All of the above
		assertTypeEnvironment("A=ℤ; B=BOOL; C=ℙ(S); S=ℙ(S)", //
				"A=1", "B=TRUE", "(∅⦂ℙ(S))=C");
	}

	/**
	 * Ensures that the identity transformer produces the same sequent.
	 */
	@Test
	public void identityTranform() {
		final ISimpleSequent sequent = makeSequent(P0, P1);
		assertSame(sequent, sequent.apply(identity));
	}

	/**
	 * Ensures that the fixed transformer produces the expected sequent.
	 */
	@Test
	public void fixedTranform() {
		assertEquals(makeSequent(P1, P1), //
				makeSequent(P0, P1).apply(fixed));
		assertEquals(makeSequent(P2, P1, P3), //
				makeSequent(P2, P0, P3).apply(fixed));
	}

	/**
	 * Ensures that the discard transformer produces an empty sequent.
	 */
	@Test
	public void discardTranform() {
		final ISimpleSequent sequent = makeSequent(P0, P1);
		assertEmpty(sequent.apply(discard));
	}

	/**
	 * Ensures that toString() produces expected strings.
	 */
	@Test
	public void string() {
		assertEquals("", makeSequent(FALSE).toString());
		assertEquals("|- 0=0", makeSequent(P0).toString());
		assertEquals("0=0 |- 1=1", makeSequent(P1, P0).toString());
		assertEquals("0=0 ;; 1=1 |- 2=2", makeSequent(P2, P0, P1).toString());
	}

	/**
	 * Ensures that method equals() behaves properly. Positive cases are already
	 * handled by the other tests, here we just check negative cases.
	 */
	@Test
	public void notEquals() {
		// Different set of hypotheses
		final ISimpleSequent p0 = makeSequent(P0);
		final ISimpleSequent p0p1 = makeSequent(P0, P1);
		assertNotEquals(p0, p0p1);

		final ISimpleSequent p0p2 = makeSequent(P0, P2);
		assertNotEquals(p0p1, p0p2);

		// Hypothesis and goal permuted
		final ISimpleSequent p1p0 = makeSequent(P1, P0);
		assertNotEquals(p0p1, p1p0);

		// Hypothesis in different order
		final ISimpleSequent p0p1p2 = makeSequent(P0, P1, P2);
		final ISimpleSequent p0p2p1 = makeSequent(P0, P2, P1);
		assertNotEquals(p0p1p2, p0p2p1);
	}

}
