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
import org.eventb.internal.core.seqprover.ProverSequent;

/**
 * A sequent type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 * 
 */
public class DependSequent {

	private final Collection<DependPredicate> predicates = new ArrayList<DependPredicate>();

	public DependSequent(ProverSequent sequent) {
		for (Predicate hyp : sequent.selectedHypIterable()) {
			predicates.add(new DependPredicate(hyp, false));
		}
		predicates.add(new DependPredicate(sequent.goal(), true));
	}
	
	public DependSequent(Collection<DependPredicate> predicates) {
		this.predicates.addAll(predicates);
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