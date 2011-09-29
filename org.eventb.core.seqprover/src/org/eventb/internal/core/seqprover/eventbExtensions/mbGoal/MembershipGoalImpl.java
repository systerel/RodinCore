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

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal.DEBUG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

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
	private final List<Generator> inclHyps;

	// Initial goal
	private final Goal goal;

	// Goals already tried in this search thread
	// Used to prevent loops
	private final Set<Goal> tried;

	// To support cancellation by end user
	private final IProofMonitor pm;

	private static final String child = "├── ";
	private static final String space = "│   ";
	private static final String end = "└── ";
	private static final String EMPTY_STRING = "";
	private String indent = child;
	private String finish;

	public MembershipGoalImpl(Predicate goal, Set<Predicate> hyps,
			FormulaFactory ff, IProofMonitor pm) {
		assert goal.getTag() == IN;
		this.goal = new Goal(goal) {
			@Override
			public Rationale makeRationale(Rationale rat) {
				return rat;
			}
		};
		this.ff = ff;
		this.rf = new MembershipGoalRules(ff);
		this.tried = new HashSet<Goal>();
		this.hyps = hyps;
		this.knownMemberships = new HashMap<Predicate, Rationale>();
		this.pm = pm;
		this.inclHyps = new GeneratorExtractor(rf, hyps, pm).extract();
		computeKnownMemberships();
		if (DEBUG) {
			System.out.println("# Goal to discharge : " + goal);
			System.out
					.println("# Informations about the goal's member : "
							+ knownMemberships.keySet());
			System.out
					.println("# Inclusions inferred from hypotheses :"
							+ inclHyps);
			System.out.println(indent + this.goal);

		}
	}

	private void computeKnownMemberships() {
		final MembershipExtractor extractor = new MembershipExtractor(rf,
				this.goal.member(), hyps, pm);
		for (final Rationale rat : extractor.extract()) {
			knownMemberships.put(rat.predicate, rat);
		}
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	public Rationale search() {
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
	private Rationale search(Goal goal) {
		if (tried.contains(goal)) {
			if (DEBUG) {
				addIndentDEBUG();
				makeEndDEBUG();
				System.out.println(finish + "### " + goal
						+ " has already been tested ###");
				removeIndentDEBUG();
			}
			// Don't loop
			return null;
		}
		tried.add(goal);
		final Rationale result = doSearch(goal);
		return result;
	}

	// Must be called only by search
	private Rationale doSearch(Goal goal) {
		if (pm.isCanceled()) {
			return null;
		}
		final Predicate predicate = goal.predicate();
		final Rationale rationale = knownMemberships.get(predicate);
		if (rationale != null) {
			if (DEBUG) {
				makeEndDEBUG();
				System.out.println(finish + "Goal can be discharged thanks to "
						+ rationale);
			}
			return rationale;
		}
		if (DEBUG) {
			addIndentDEBUG();
		}
		for (final Generator hyp : inclHyps) {
			for (final Goal subGoal : hyp.generate(goal)) {
				if (DEBUG) {
					System.out.println(indent + subGoal + " (" + hyp + ")");
				}
				final Rationale rat = search(subGoal);
				if (rat != null) {
					return subGoal.makeRationale(rat);
				}
			}
		}
		if (DEBUG) {
			removeIndentDEBUG();
		}
		return null;
	}

	private void addIndentDEBUG() {
		indent = space + indent;
	}

	private void removeIndentDEBUG() {
		if (indent.equals(child)) {
			return;
		}
		indent = indent.replaceFirst(space, EMPTY_STRING);
	}

	private void makeEndDEBUG() {
		finish = indent.replaceFirst(child, end);
	}

}
