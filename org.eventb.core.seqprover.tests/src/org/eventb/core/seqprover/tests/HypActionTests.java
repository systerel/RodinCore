/*******************************************************************************
 * Copyright (c) 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.seqprover.ForwardInfHypAction;
import org.eventb.internal.core.seqprover.SelectionHypAction;
import org.junit.Test;

/**
 * Test class for {@link IHypAction} implementations.
 */
public class HypActionTests {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	/**
	 * Ensures that {@link ForwardInfHypAction} manipulates a copy of the
	 * collections of predicates and array of free identifiers which are given
	 * to its constructor.
	 */
	@Test
	public void testFwdInfHypActionField() {
		final Predicate p1 = TestLib.genPred("1=1");
		final Predicate p2 = TestLib.genPred("2=2");
		final FreeIdentifier x = factory.makeFreeIdentifier("x", null);

		final List<Predicate> s1 = getPredList(p1);
		final List<Predicate> s2 = getPredList(p2);
		final FreeIdentifier[] a1 = new FreeIdentifier[] { x };
		final IForwardInfHypAction action = ProverFactory
				.makeForwardInfHypAction(s1, a1, s2);
		s1.clear();
		final Collection<Predicate> hyps = action.getHyps();
		assertEquals(1, hyps.size());
		assertEquals(p1, hyps.iterator().next());
		s2.clear();
		final Collection<Predicate> inferredHyps = action.getInferredHyps();
		assertEquals(1, inferredHyps.size());
		assertEquals(p2, inferredHyps.iterator().next());
		final FreeIdentifier[] idents = action.getAddedFreeIdents();
		assertEquals(1, idents.length);
		assertEquals(x, idents[0]);
	}

	/**
	 * Ensures that {@link SelectionHypAction} manipulates a copy of the
	 * collection of predicates which are given to its constructor. This test is
	 * valid for all kind of action types managed by the selection hypothesis
	 * action.
	 */
	@Test
	public void testSelectHypActionField() {
		final Predicate p1 = TestLib.genPred("1=1");
		final List<Predicate> s1 = getPredList(p1);
		final ISelectionHypAction action = ProverFactory
				.makeSelectHypAction(s1);
		s1.clear();
		assertEquals(1, action.getHyps().size());
		assertEquals(p1, action.getHyps().iterator().next());
	}

	private static List<Predicate> getPredList(Predicate... predicates) {
		final ArrayList<Predicate> result = new ArrayList<Predicate>();
		for (Predicate p : predicates) {
			result.add(p);
		}
		return result;
	}

}
