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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Represents a membership predicate that the Membership Goal reasoner try to
 * discharge. Therefore, the truthness of this predicate is yet unknown.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
public abstract class Goal {

	private final Predicate predicate;
	private final Expression member;
	private final Expression set;

	public Goal(Predicate predicate) {
		this.predicate = predicate;
		final RelationalPredicate rel = (RelationalPredicate) predicate;
		this.member = rel.getLeft();
		this.set = rel.getRight();
	}

	public Goal(Expression member, Expression set, MembershipGoalRules rf) {
		this.predicate = rf.in(member, set);
		this.member = member;
		this.set = set;
	}

	public Predicate predicate() {
		return predicate;
	}

	public Expression member() {
		return member;
	}

	public Expression set() {
		return set;
	}

	@Override
	public String toString() {
		return "G: " + predicate.toString();
	}

	@Override
	public int hashCode() {
		return predicate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Goal)) {
			return false;
		}
		final Goal other = (Goal) obj;
		return this.predicate.equals(other.predicate);
	}

	public abstract Rationale makeRationale(Rationale rat);
	
}
