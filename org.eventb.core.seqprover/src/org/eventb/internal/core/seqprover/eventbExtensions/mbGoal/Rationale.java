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
 * @author Emmanuel Billaud
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

	public static class Composition extends Rationale {

		private final Rationale in;
		private final Rationale subset;

		public Composition(Predicate predicate, Rationale in, Rationale subset) {
			super(predicate, in.ruleFactory());
			this.in = in;
			this.subset = subset;
		}

		@Override
		public Rule<?> makeRule() {
			return rf.compose(in.makeRule(), subset.makeRule());
		}

	}

	private static abstract class Unary extends Rationale {

		private final Rationale child;

		public Unary(Predicate predicate, Rationale child) {
			super(predicate, child.ruleFactory());
			this.child = child;
		}

		@Override
		public final Rule<?> makeRule() {
			return makeRule(child.makeRule());
		}

		public abstract Rule<?> makeRule(Rule<?> childRule);

	}

	public static abstract class Projection extends Unary {

		protected final boolean simplify;

		public Projection(boolean simplify, Predicate predicate, Rationale child) {
			super(predicate, child);
			this.simplify = simplify;
		}

	}

	public static class DomProjection extends Projection {

		public DomProjection(boolean simplify, Predicate predicate,
				Rationale child) {
			super(simplify, predicate, child);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return simplify ? rf.domPrjS(childRule) : rf.domPrj(childRule);
		}

	}

	public static class RanProjection extends Projection {

		public RanProjection(boolean simplify, Predicate predicate,
				Rationale child) {
			super(simplify, predicate, child);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return simplify ? rf.ranPrjS(childRule) : rf.ranPrj(childRule);
		}

	}

	public static class SetExtensionMember extends Unary {

		private final Expression member;

		public SetExtensionMember(Expression member, Predicate predicate,
				Rationale child) {
			super(predicate, child);
			this.member = member;
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.setExtMember(member, childRule);
		}

	}

	public static class RelationToCartesian extends Unary {

		public RelationToCartesian(Predicate predicate, Rationale child) {
			super(predicate, child);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.relToCprod(childRule);
		}

	}

	public static class EqualToSubset extends Unary {

		private final boolean leftToRight;

		public EqualToSubset(boolean leftToRight, Predicate predicate,
				Rationale child) {
			super(predicate, child);
			this.leftToRight = leftToRight;
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.eqToSubset(leftToRight, childRule);
		}

	}

	public static class LastOverride extends Unary {

		public LastOverride(Predicate predicate, Rationale child) {
			super(predicate, child);
		}

		@Override
		public Rule<?> makeRule(Rule<?> childRule) {
			return rf.lastOvr(childRule);
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

	public MembershipGoalRules ruleFactory() {
		return rf;
	}

	/**
	 * Returns a justification for this predicate.
	 * 
	 * @return a justification as a rule
	 */
	public abstract Rule<?> makeRule();

	@Override
	public String toString() {
		return "Rat: " + predicate;
	}

}
