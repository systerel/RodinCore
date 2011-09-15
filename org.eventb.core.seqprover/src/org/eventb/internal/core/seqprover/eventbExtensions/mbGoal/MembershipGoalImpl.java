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
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.Hypothesis;

/**
 * Common implementation of the Membership goal reasoner and tactic.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
public class MembershipGoalImpl {

	// Formula factory to use everywhere
	final FormulaFactory ff;

	// Rule factory to use everywhere
	private final MembershipGoalRules rf;

	// Available hypotheses
	private final Set<Predicate> hyps;

	// Memberships that have a rationale
	private final Map<Predicate, Rationale> knownMemberships;

	// Subset of hypotheses that take an inclusion form
	private final List<Inclusion> inclHyps;

	// Initial goal
	private final Goal goal;

	// Goals already tried in this search thread
	// Used to prevent loops
	private final Set<Goal> tried;

	private static final List<Inclusion> extractInclusions(Set<Predicate> hyps,
			final MembershipGoalRules rf) {
		final List<Inclusion> result = new ArrayList<Inclusion>();
		for (final Predicate hyp : hyps) {
			switch (hyp.getTag()) {
			case SUBSET:
			case SUBSETEQ:
				result.add(new Inclusion(new Hypothesis(hyp, rf)));
				break;
			case EQUAL:
				// TODO implement double inclusion
				break;
			case IN:
				// TODO implement membership in relation set
				break;
			default:
				// Ignore
				break;
			}
		}
		return result;
	}

	public MembershipGoalImpl(Predicate goal, Set<Predicate> hyps,
			FormulaFactory ff) {
		if (goal == null || goal.getTag() != IN) {
			throw new IllegalArgumentException("Not a membership : " + goal);
		}
		this.goal = new Goal(goal) {
			@Override
			public Rule<?> makeRule(Rule<?> rule) {
				return rule;
			}
		};
		this.ff = ff;
		this.rf = new MembershipGoalRules(ff);
		this.tried = new HashSet<Goal>();
		this.hyps = hyps;
		this.knownMemberships = new HashMap<Predicate, Rationale>();
		this.inclHyps = extractInclusions(hyps, rf);
		computeKnownMemberships();
	}

	private void computeKnownMemberships() {
		final MembershipExtractor extractor = new MembershipExtractor(rf,
				this.goal.member(), hyps);
		for (final Rationale rat : extractor.extract()) {
			knownMemberships.put(rat.predicate, rat);
		}
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	public Rule<?> search() {
		return search(goal);
	}

	public boolean verify(Rule<?> rule) {
		if (!rule.consequent.equals(goal.predicate())) {
			return false;
		}
		final Set<Predicate> neededHyps = rule.getHypotheses();
		return hyps.containsAll(neededHyps);
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses. The member
	 * variable <code>tried</code> is used to prevent looping.
	 * 
	 * @param goal
	 *            membership to discharge
	 * @return a justification for the given membership
	 */
	public Rule<?> search(Goal goal) {
		if (tried.contains(goal)) {
			// Don't loop
			return null;
		}
		tried.add(goal);
		final Rule<?> result = doSearch(goal);
		tried.remove(goal);
		return result;
	}

	// Must be called only by search
	private Rule<?> doSearch(Goal goal) {
		final Predicate predicate = goal.predicate();
		final Rationale rationale = knownMemberships.get(predicate);
		if (rationale != null) {
			return rationale.makeRule();
		}
		for (final Inclusion hyp : inclHyps) {
			for (final Goal subGoal : hyp.generate(goal)) {
				final Rule<?> rule = search(subGoal);
				if (rule != null) {
					return subGoal.makeRule(rule);
				}
			}
		}
		return null;
	}

}
