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

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.Predicate;

/**
 * A sequent type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 * 
 */
public class DependSequent {

	private final Collection<DependPredicate> predicates = new ArrayList<DependPredicate>();

	public DependSequent(Collection<Predicate> hyps, Predicate goal) {
		for (Predicate hyp : hyps) {
			predicates.add(new DependPredicate(hyp, false));
		}
		if (goal != null) {
			predicates.add(new DependPredicate(goal, true));
		}
	}
	
	public Collection<DependPredicate> neededPredicates(DependSequent other) {
		final Collection<DependPredicate> result = new ArrayList<DependPredicate>(
				this.predicates);
		result.retainAll(other.predicates);
		return result;
	}
	
	public boolean remove(Collection<DependPredicate> preds) {
		return predicates.removeAll(preds);
	}
	
	public boolean isSatisfied() {
		return predicates.isEmpty();
	}
}