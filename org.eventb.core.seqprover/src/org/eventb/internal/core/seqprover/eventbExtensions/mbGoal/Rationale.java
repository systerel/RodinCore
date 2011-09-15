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

	public static class DomProjection extends Rationale {
		
		private final Rationale child;

		public DomProjection(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, rf);
			this.child = child;
		}

		@Override
		public Rule<?> makeRule() {
			return rf.domPrj(child.makeRule());
		}

	}

	public static class RanProjection extends Rationale {
		
		private final Rationale child;

		public RanProjection(Predicate predicate, Rationale child,
				MembershipGoalRules rf) {
			super(predicate, rf);
			this.child = child;
		}

		@Override
		public Rule<?> makeRule() {
			return rf.ranPrj(child.makeRule());
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
