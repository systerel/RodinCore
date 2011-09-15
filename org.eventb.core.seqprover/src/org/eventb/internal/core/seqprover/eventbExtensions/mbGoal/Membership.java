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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Represents a membership predicate as used in the Membership Goal reasoner.
 * 
 * @author Laurent Voisin
 */
public class Membership extends Rationale {

	public static Membership asHypothesis(Predicate predicate) {
		return new Membership(predicate) {
			@Override
			public Rule<?> getRule(MembershipGoalImpl impl) {
				return impl.hypothesis(this);
			}
		};
	}

	private final Expression member;
	private final Expression set;

	public Membership(Predicate predicate) {
		super(predicate);
		final RelationalPredicate rel = (RelationalPredicate) predicate;
		this.member = rel.getLeft();
		this.set = rel.getRight();
	}

	public Membership(Expression member, Expression set, FormulaFactory ff) {
		super(ff.makeRelationalPredicate(IN, member, set, null));
		this.member = member;
		this.set = set;
	}

	public Expression member() {
		return member;
	}

	public Expression set() {
		return set;
	}

	@Override
	public Rule<?> getRule(MembershipGoalImpl impl) {
		return impl.searchNoLoop(this);
	}

	public boolean match(Predicate other) {
		return this.predicate.equals(other);
	}

}
