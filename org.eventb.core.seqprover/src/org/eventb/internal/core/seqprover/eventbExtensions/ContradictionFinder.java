/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.internal.core.seqprover.eventbExtensions.ContrHyps.contradictingPredicates;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;

/**
 * Protocol for finding a hypothesis which is contradicted by some of the others
 * <p>
 * For each hypothesis, generates a list of contradiction and search at least
 * one hypothesis is contained into the list of contradiction.
 * </p>
 */
public abstract class ContradictionFinder {

	/**
	 * Find and return a predicate which is contradicted by the others or
	 * <code>null</code> if not found.
	 * 
	 * @return a predicate contradicted by some others
	 */
	public Predicate getContrHyp() {
		for (final Predicate hyp : getCandidates()) {
			final Map<Predicate, List<Predicate>> cntrs = contradictingPredicates(hyp);
			if (cntrs != null && areContained(cntrs)) {
				return hyp;
			}
		}
		return null;
	}

	/**
	 * Tells whether every entry is covered in the set, that is contains a
	 * predicate which is a member of the set.
	 * 
	 * @param cntrs
	 *            the map (sub-predicate ↦ set of contradictory predicates)
	 * 
	 * @return <code>true</code> iff each conjunct has a contradictory predicate
	 *         in hypotheses
	 */
	protected boolean areContained(Map<Predicate, List<Predicate>> cntrs) {
		boolean contain;

		for (Entry<Predicate, List<Predicate>> entry : cntrs.entrySet()) {
			contain = false;
			for (Predicate cntr : entry.getValue()) {
				if (isContained(cntr)) {
					contain = true;
				}
			}
			if (!contain) {
				return false;
			}
		}
		return true;
	}

	protected abstract Iterable<Predicate> getCandidates();

	protected abstract boolean isContained(Predicate pred);

	/**
	 * Find a predicate which is contradicted by the other predicates of a set
	 * (used in the reasoner).
	 */
	public static class ContradictionInSetFinder extends ContradictionFinder {

		private final Set<Predicate> neededHyps;

		public ContradictionInSetFinder(Set<Predicate> neededHyps) {
			this.neededHyps = neededHyps;
		}

		@Override
		protected Iterable<Predicate> getCandidates() {
			return neededHyps;
		}

		@Override
		protected boolean isContained(Predicate pred) {
			return neededHyps.contains(pred);
		}

	}

	/**
	 * Find a hypothesis which is contradicted by the other hypotheses of a
	 * sequent (used in the automated tactic).
	 */
	public static class ContradictionInSequentFinder extends
			ContradictionFinder {

		private final IProverSequent sequent;

		public ContradictionInSequentFinder(IProverSequent sequent) {
			this.sequent = sequent;
		}

		@Override
		protected Iterable<Predicate> getCandidates() {
			return sequent.selectedHypIterable();
		}

		@Override
		protected boolean isContained(Predicate pred) {
			return sequent.containsHypothesis(pred);
		}

	}

}
