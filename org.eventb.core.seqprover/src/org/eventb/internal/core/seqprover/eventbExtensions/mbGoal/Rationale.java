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
 * Represents a predicate for which the Membership Goal reasoner can provide a
 * justification based on rules.
 * 
 * @author Laurent Voisin
 */
public abstract class Rationale {

	protected final Predicate predicate;

	public Rationale(Predicate predicate) {
		this.predicate = predicate;
	}

	public Predicate predicate() {
		return predicate;
	}

	public abstract Rule<?> getRule(MembershipGoalImpl impl);

	@Override
	public String toString() {
		return predicate.toString();
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
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Membership other = (Membership) obj;
		return this.predicate.equals(other.predicate);
	}

}