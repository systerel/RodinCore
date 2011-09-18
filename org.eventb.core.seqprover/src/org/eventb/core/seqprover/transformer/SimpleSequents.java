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
package org.eventb.core.seqprover.transformer;

import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeGoal;
import static org.eventb.internal.core.seqprover.transformer.TrackedPredicate.makeHyp;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.transformer.SimpleSequent;
import org.eventb.internal.core.seqprover.transformer.TrackedPredicate;

/**
 * Common protocol for creating and transforming simple sequents.
 * 
 * @see ISimpleSequent
 * @author Laurent Voisin
 * @since 2.3
 * @noextend This class is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SimpleSequents {

	/**
	 * Returns a new simple sequent created with the given predicates. All
	 * non-null predicates must be type-checked. Null predicates will be
	 * ignored.
	 * 
	 * @param hypotheses
	 *            sequent hypotheses, can be or contain <code>null</code>
	 * @param goal
	 *            sequent goal, can be <code>null</code>
	 * @param factory
	 *            formula factory for the given predicates
	 * @return a new simple sequent with the given hypotheses and goal
	 */
	public static ISimpleSequent make(Predicate[] hypotheses, Predicate goal,
			FormulaFactory factory) {
		final List<TrackedPredicate> preds = makeTrackedPredicates(hypotheses,
				goal);
		final TrackedPredicate trivial = getTrivial(preds);
		if (trivial != null) {
			return new SimpleSequent(factory, trivial);
		}
		return new SimpleSequent(factory, preds);
	}

	private static List<TrackedPredicate> makeTrackedPredicates(
			Predicate[] hypotheses, Predicate goal) {
		final int capacity = (hypotheses != null ? hypotheses.length : 0) + 1;
		final List<TrackedPredicate> preds = new ArrayList<TrackedPredicate>(
				capacity);
		if (hypotheses != null) {
			for (Predicate hyp : hypotheses) {
				if (hyp != null) {
					preds.add(makeHyp(hyp));
				}
			}
		}
		if (goal != null) {
			preds.add(makeGoal(goal));
		}
		return preds;
	}

	private static TrackedPredicate getTrivial(List<TrackedPredicate> preds) {
		for (TrackedPredicate pred : preds) {
			if (pred.holdsTrivially()) {
				return pred;
			}
		}
		return null;
	}

	private SimpleSequents() {
		// singleton class
	}

}
