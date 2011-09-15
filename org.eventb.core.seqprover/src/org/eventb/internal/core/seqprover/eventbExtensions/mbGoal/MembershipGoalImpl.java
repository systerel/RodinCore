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

	// Member in the initial goal
	private final Expression goalMember;

	// Set in the initial goal
	private final Expression goalSet;

	public MembershipGoalImpl(Expression goalMember, Expression goalSet,
			List<Predicate> hyps, FormulaFactory ff) {
		this.goalMember = goalMember;
		this.goalSet = goalSet;
		this.hyps = hyps;
		this.ff = ff;
		this.rf = new MembershipGoalRules(ff);
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	public boolean search() {
		return search(goalMember, goalSet) != null;
	}

	/**
	 * Tells whether there is a proof for the goal from hypotheses.
	 */
	// FIXME Can loop forever
	public Rule<?> search(Expression member, Expression set) {
		for (Predicate hyp : hyps) {
			if (match(hyp, member, set)) {
				return rf.hypothesis(hyp);
			}
			if (set.equals(getRight(hyp))) {
				final Rule<?> rule = search(member, getLeft(hyp));
				if (rule != null) {
					return rf.compose(rule, rf.hypothesis(hyp));
				}
			}
		}
		return null;
	}

	/**
	 * Tells whether the given predicate is "member ∈ set".
	 */
	private boolean match(Predicate pred, Expression member, Expression set) {
		if (pred.getTag() != IN) {
			return false;
		}
		final RelationalPredicate rel = (RelationalPredicate) pred;
		return member.equals(rel.getLeft()) && set.equals(rel.getRight());
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
