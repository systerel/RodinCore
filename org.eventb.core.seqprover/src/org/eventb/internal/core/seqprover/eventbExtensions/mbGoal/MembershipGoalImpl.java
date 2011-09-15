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
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

public class MembershipGoalImpl {

	// Formula factory to use everywhere
	private final FormulaFactory ff;

	// Rule factory to use everywhere
	private final MembershipGoalRules rf;

	// Available hypotheses
	private final List<Predicate> hyps;

	// Subset of hypotheses that take a membership form
	private final Set<Predicate> msHyps;

	// Initial goal
	private final Membership goal;

	// Goals already tried in this search thread
	// Used to prevent loops
	private final Set<Membership> tried;

	private static final Set<Predicate> extractMembership(List<Predicate> hyps) {
		final Set<Predicate> result = new HashSet<Predicate>();
		for (final Predicate hyp : hyps) {
			if (hyp.getTag() == IN) {
				result.add(hyp);
			}
		}
		return result;
	}

	public MembershipGoalImpl(Predicate goal, List<Predicate> hyps,
			FormulaFactory ff) {
		this.goal = new Membership(goal);
		this.hyps = hyps;
		this.msHyps = extractMembership(hyps);
		this.ff = ff;
		this.rf = new MembershipGoalRules(ff);
		this.tried = new HashSet<Membership>();
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	public boolean search() {
		return search(goal) != null;
	}

	public Rule<?> search(Membership ms) {
		return ms.getRule(this);
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses. The member
	 * variable <code>tried</code> is used to prevent looping.
	 * 
	 * @param ms
	 *            membership to discharge
	 * @return a justification for the given membership
	 */
	Rule<?> searchNoLoop(Membership ms) {
		if (tried.contains(ms)) {
			// Don't loop
			return null;
		}
		tried.add(ms);
		final Rule<?> result = doSearch(ms);
		tried.remove(ms);
		return result;
	}

	// Must be called only by searchNoLoop
	private Rule<?> doSearch(Membership ms) {
		final Predicate predicate = ms.predicate();
		if (msHyps.contains(predicate)) {
			return Membership.asHypothesis(predicate).getRule(this);
		}
		for (Predicate hyp : hyps) {
			if (ms.set().equals(getRight(hyp))) {
				final Membership child = new Membership(ms.member(),
						getLeft(hyp), ff);
				final Rule<?> rule = child.getRule(this);
				if (rule != null) {
					return rf.compose(rule, Inclusion.asHypothesis(hyp)
							.getRule(this));
				}
			}
		}
		return null;
	}

	private Expression getLeft(Predicate pred) {
		final int tag = pred.getTag();
		if (tag != SUBSET && tag != SUBSETEQ) {
			return null;
		}
		final RelationalPredicate rel = (RelationalPredicate) pred;
		return rel.getLeft();
	}

	private Expression getRight(Predicate pred) {
		final int tag = pred.getTag();
		if (tag != SUBSET && tag != SUBSETEQ) {
			return null;
		}
		final RelationalPredicate rel = (RelationalPredicate) pred;
		return rel.getRight();
	}

	Rule<?> hypothesis(Rationale rat) {
		final Predicate predicate = rat.predicate();
		// FIXME optimize hyps for search
		assert hyps.contains(predicate);
		return rf.hypothesis(predicate);
	}

}
