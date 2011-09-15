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

import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Represents an inclusion predicate as used in the Membership Goal reasoner.
 * 
 * @author Laurent Voisin
 */
public abstract class Inclusion extends Generator {

	private final Predicate predicate;

	private final boolean isStrict;

	// Left and right member of this inclusion
	private final Expression left;
	private final Expression right;

	public Inclusion(Predicate predicate) {
		this.predicate = predicate;
		switch (predicate.getTag()) {
		case SUBSET:
			this.isStrict = true;
			break;
		case SUBSETEQ:
			this.isStrict = false;
			break;
		default:
			throw new IllegalArgumentException("Not an inclusion :" + predicate);
		}
		final RelationalPredicate rel = (RelationalPredicate) predicate;
		this.left = rel.getLeft();
		this.right = rel.getRight();
	}

	public Predicate predicate() {
		return predicate;
	}

	public boolean isStrict() {
		return isStrict;
	}

	public Expression left() {
		return left;
	}

	public Expression right() {
		return right;
	}

	@Override
	public String toString() {
		return "Gen: " + predicate.toString();
	}

	public abstract Rule<?> makeRule();

	public List<Goal> generate(Goal goal, final MembershipGoalImpl impl) {
		final List<Goal> result = new ArrayList<Goal>();
		if (right.equals(goal.set())) {
			result.add(new Goal(goal.member(), left, impl) {
				@Override
				public Rule<?> makeRule(Rule<?> rule) {
					return impl.compose(rule, Inclusion.this.makeRule());
				}
			});
		}
		return result;
	}

}
