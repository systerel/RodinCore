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

import static org.eventb.core.ast.Formula.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
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
	private final Set<Membership> msHyps;

	// Initial goal
	private final Membership goal;

	private static final Set<Membership> extractMembership(
			List<Predicate> hyps) {
		final Set<Membership> result = new HashSet<Membership>();
		for (final Predicate hyp : hyps) {
			if (hyp.getTag() == IN) {
				result.add(new Membership(hyp));
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
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	public boolean search() {
		return search(goal) != null;
	}

	public Rule<?> search(Membership ms) {
		return searchNoLoop(ms, new HashSet<Membership>());
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses. The second
	 * parameter is used to prevent looping.
	 * 
	 * @param ms
	 *            membership to discharge
	 * @param tried
	 *            membership already tried previously
	 * @return a justification for the given membership
	 */
	private Rule<?> searchNoLoop(Membership ms, Set<Membership> tried) {
		if (tried.contains(ms)) {
			// Don't loop
			return null;
		}
		tried.add(ms);
		final Rule<?> result = search(ms, tried);
		tried.remove(ms);
		return result;
	}

	// Must be called only by searchNoLoop
	private Rule<?> search(Membership ms, Set<Membership> tried) {
		if (msHyps.contains(ms)) {
			return rf.hypothesis(ms.predicate());
		}
		for (Predicate hyp : hyps) {
			if (ms.set().equals(getRight(hyp))) {
				final Membership child = new Membership(ms.member(),
						getLeft(hyp), ff);
				final Rule<?> rule = searchNoLoop(child, tried);
				if (rule != null) {
					return rf.compose(rule, rf.hypothesis(hyp));
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

}
