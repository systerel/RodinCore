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
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.Composition;

/**
 * Represents an inclusion predicate as used in the Membership Goal reasoner.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
public class Inclusion extends Generator {

	private final MembershipGoalRules rf;
	private final Rationale rationale;

	private final boolean isStrict;

	// Left and right member of this inclusion
	private final Expression left;
	private final Expression right;

	public Inclusion(Rationale rationale) {
		this.rf = rationale.ruleFactory();
		this.rationale = rationale;
		final Predicate predicate = rationale.predicate();
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
		return "Gen: " + rationale;
	}

	public final Rule<?> makeRule() {
		return rationale.makeRule();
	}

	public List<Goal> generate(final Goal goal) {
		final List<Goal> result = new ArrayList<Goal>();
		if (right.equals(goal.set())) {
			result.add(new Goal(goal.member(), left, rf) {
				@Override
				public Rationale makeRationale(Rationale rat) {
					return new Composition(goal.predicate(), rat,
							Inclusion.this.rationale);
				}
			});
		}
		return result;
	}

}
