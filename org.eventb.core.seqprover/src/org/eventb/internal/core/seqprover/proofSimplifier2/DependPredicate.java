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
package org.eventb.internal.core.seqprover.proofSimplifier2;

import org.eventb.core.ast.Predicate;

/**
 * A predicate type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 * 
 */
public class DependPredicate {

	private final Predicate predicate;
	private final boolean isGoal;

	public DependPredicate(Predicate predicate, boolean isGoal) {
		this.predicate = predicate;
		this.isGoal = isGoal;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public boolean isGoal() {
		return isGoal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isGoal ? 1231 : 1237);
		result = prime * result + predicate.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DependPredicate)) {
			return false;
		}
		DependPredicate other = (DependPredicate) obj;
		if (isGoal != other.isGoal) {
			return false;
		}
		if (!predicate.equals(other.predicate)) {
			return false;
		}
		return true;
	}

}
