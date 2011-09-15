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

/**
 * A predicate that we can justify by a rule if needed.
 * 
 * @author Laurent Voisin
 */
public abstract class Rationale {

	public static class Hypothesis extends Rationale {

		public Hypothesis(Predicate predicate, MembershipGoalRules rf) {
			super(predicate, rf);
		}

		@Override
		public Rule<?> makeRule() {
			return rf.hypothesis(predicate);
		}

	}

	private static abstract class Unary extends Rationale {

		private final Rationale child;

		public Unary(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, rf);
			this.child = child;
		}

		@Override
		public final Rule<?> makeRule() {
			return makeRule(child.makeRule());
		}

		public abstract Rule<?> makeRule(Rule<?> childRule);

	}

	public static class DomProjection extends Unary {

		public DomProjection(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, child, rf);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.domPrj(childRule);
		}

	}

	public static class RanProjection extends Unary {

		public RanProjection(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, child, rf);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.ranPrj(childRule);
		}

	}

	public static class SetExtensionMember extends Unary {

		private final Expression member;

		public SetExtensionMember(Expression member, Predicate predicate,
				Rationale child, MembershipGoalRules rf) {
			super(predicate, child, rf);
			this.member = member;
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.setExtMember(member, childRule);
		}

	}

	public static class RelationToCartesian extends Unary {

		public RelationToCartesian(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, child, rf);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.relToCprod(childRule);
		}

	}

	public static class EqualToSubset extends Unary {

		private final boolean leftToRight;

		public EqualToSubset(boolean leftToRight, Predicate predicate,
				Rationale child, MembershipGoalRules rf) {
			super(predicate, child, rf);
			this.leftToRight = leftToRight;
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.eqToSubset(leftToRight, childRule);
		}

	}

	protected final Predicate predicate;
	protected final MembershipGoalRules rf;

	public Rationale(Predicate predicate, MembershipGoalRules rf) {
		this.predicate = predicate;
		this.rf = rf;
	}

	public Predicate predicate() {
		return predicate;
	}

	/**
	 * Returns a justification for this predicate.
	 * 
	 * @return a justification as a rule
	 */
	public abstract Rule<?> makeRule();

}
