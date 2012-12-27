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

import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeGoal;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.internal.core.seqprover.transformer.TrackedPredicate;
import org.junit.Test;

/**
 * Unit tests for interface {@link ITrackedPredicate}.
 * 
 * @author Laurent Voisin
 */
public class TrackedPredicateTests extends AbstractTransformerTests {

	private static void assertTransformed(TrackedPredicate origin,
			ISequentTransformer transformer, Predicate newPred) {
		final ITrackedPredicate transformed = origin.transform(transformer);
		assertNotSame(origin, transformed);
		assertEquals(origin.isHypothesis(), transformed.isHypothesis());
		assertEquals(origin.getOriginal(), transformed.getOriginal());
		assertEquals(newPred, transformed.getPredicate());
	}

	private static void assertNotTransformed(TrackedPredicate origin,
			ISequentTransformer transformer) {
		final ITrackedPredicate transformed = origin.transform(transformer);
		assertSame(origin, transformed);
	}

	/**
	 * Ensures that a tracked goal predicate holds correct information.
	 */
	@Test
	public void goalCreation() {
		final ITrackedPredicate tpred = makeGoal(P0);
		assertFalse(tpred.isHypothesis());
		assertFalse(tpred.holdsTrivially());
		assertEquals(P0, tpred.getOriginal());
		assertEquals(P0, tpred.getPredicate());
	}

	/**
	 * Ensures that a true goal holds trivially.
	 */
	@Test
	public void trivialGoal() {
		final TrackedPredicate tpred = makeGoal(TRUE);
		assertTrue(tpred.holdsTrivially());
		assertTrue(tpred.isUseful());
	}

	/**
	 * Ensures that a false goal is not useful.
	 */
	@Test
	public void uselessGoal() {
		final TrackedPredicate tpred = makeGoal(FALSE);
		assertFalse(tpred.holdsTrivially());
		assertFalse(tpred.isUseful());
	}

	/**
	 * Ensures that a tracked hypothesis predicate holds correct information.
	 */
	@Test
	public void hypothesisCreation() {
		final ITrackedPredicate tpred = makeHyp(P0);
		assertTrue(tpred.isHypothesis());
		assertFalse(tpred.holdsTrivially());
		assertEquals(P0, tpred.getOriginal());
		assertEquals(P0, tpred.getPredicate());
	}

	/**
	 * Ensures that a false hypothesis holds trivially.
	 */
	@Test
	public void trivialHypothesis() {
		final TrackedPredicate tpred = makeHyp(FALSE);
		assertTrue(tpred.holdsTrivially());
		assertTrue(tpred.isUseful());
	}

	/**
	 * Ensures that a true hypothesis is not useful.
	 */
	@Test
	public void uselessHypothesis() {
		final TrackedPredicate tpred = makeHyp(TRUE);
		assertFalse(tpred.holdsTrivially());
		assertFalse(tpred.isUseful());
	}

	/**
	 * Ensures that a tracked goal can be transformed and retains its origin.
	 */
	@Test
	public void transformGoal() {
		final TrackedPredicate tpred = makeGoal(P0);
		assertNotTransformed(tpred, identity);
		assertTransformed(tpred, fixed, P1);
		assertNull(tpred.transform(discard));
	}

	/**
	 * Ensures that a tracked hypothesis can be transformed and retains its
	 * origin.
	 */
	@Test
	public void transformHypothesis() {
		final TrackedPredicate tpred = makeHyp(P0);
		assertNotTransformed(tpred, identity);
		assertTransformed(tpred, fixed, P1);
		assertNull(tpred.transform(discard));
	}

	/**
	 * Ensures that toString() produces expected strings.
	 */
	@Test
	public void string() {
		assertEquals("goal: " + P0.toString(), makeGoal(P0).toString());
		assertEquals("hyp: " + P0.toString(), makeHyp(P0).toString());
	}

	/**
	 * Ensures that method equals() behaves properly.
	 */
	@Test
	public void equals() {
		// Not equal when different hypothesis status
		final TrackedPredicate goal_P0 = makeGoal(P0);
		final TrackedPredicate hyp_P0 = makeHyp(P0);
		assertNotEquals(goal_P0, hyp_P0);

		// Not equal when different predicate
		final TrackedPredicate goal_P1 = makeGoal(P1);
		assertNotEquals(goal_P0, goal_P1);

		// Equal despite different origin
		final TrackedPredicate goal_P1t = goal_P0.transform(fixed);
		assertEquals(goal_P1, goal_P1t);
		assertNotEquals(goal_P1.getOriginal(), goal_P1t.getOriginal());
	}

}
