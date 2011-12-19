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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProducedSequent extends NodeSequent {

	// new hypotheses are:
	// - added hypotheses
	// - forward inferred hypotheses
	private final List<RequiredSequent> dependents = new ArrayList<RequiredSequent>();

	public ProducedSequent(Collection<Predicate> hyps, Predicate goal, DependNode node) {
		super(hyps, goal, node);
	}

	public void addDependentSequent(RequiredSequent dependent) {
		if (!dependents.contains(dependent)) {
			dependents.add(dependent);
		}
	}

	@Override
	public void propagateDelete() {
		for (RequiredSequent dependent : dependents) {
			dependent.getNode().delete();
		}
		dependents.clear();
	}

	public void deleteDependent(RequiredSequent dependent) {
		if (this.getNode().isDeleted()) {
			// this.getNode() is being deleted:
			// avoid concurrent modification
			return;
		}
		dependents.remove(dependent);
		if (!hasDependents()) {
			getNode().delete();
		}
	}

	public boolean hasDependents() {
		return !dependents.isEmpty();
	}

	public Set<Predicate> getUsedPredicates() {
		final List<DependPredicate> used = new ArrayList<DependPredicate>();

		for(RequiredSequent req: dependents) {
			final Collection<DependPredicate> preds = req.getPredicates();
			preds.retainAll(predicates);
			used.addAll(preds);
		}
		final Set<Predicate> result = new HashSet<Predicate>();
		for (DependPredicate usedPred : used) {
			result.add(usedPred.getPredicate());
		}
		return result;
	}
}
