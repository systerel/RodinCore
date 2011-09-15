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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Represents an inclusion predicate as used in the Membership Goal reasoner.
 * 
 * @author Laurent Voisin
 */
public abstract class Inclusion extends Rationale {

	public static Inclusion asHypothesis(Predicate predicate) {
		return new Inclusion(predicate) {
			@Override
			public Rule<?> getRule(MembershipGoalImpl impl) {
				return impl.hypothesis(this);
			}
		};
	}

	private final boolean isStrict;
	
	// Left and right member of this inclusion
	private final Expression left;
	private final Expression right;

	public Inclusion(Predicate predicate) {
		super(predicate);
		switch (predicate.getTag()) {
		case SUBSETEQ:
			this.isStrict = false;
			break;
		case SUBSET:
			this.isStrict = true;
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

	public abstract Rule<?> getRule(MembershipGoalImpl impl);

}
