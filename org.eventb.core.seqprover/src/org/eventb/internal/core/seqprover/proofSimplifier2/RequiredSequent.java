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
import java.util.Iterator;
import java.util.List;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;

/**
 * @author Nicolas Beauger
 * 
 */
public class RequiredSequent extends NodeSequent {

	private final List<ProducedSequent> neededSequents = new ArrayList<ProducedSequent>();
	private final Collection<DependPredicate> unsatisfied;
	
	public RequiredSequent(IProofRule rule, DependNode node) {
		super(rule.getNeededHyps(), rule.getGoal(), node);
		this.unsatisfied = new ArrayList<DependPredicate>(predicates);
	}
	
	public void satisfyWith(ProducedSequent produced) {
		final boolean depends = unsatisfied.removeAll(produced.predicates);
		if (depends) {
			neededSequents.add(produced);
		}
	}
	
	// used for checking satisfaction by the root sequent
	// that is not a produced sequent and for which
	// no dependencies shall be created
	public void satisfyWith(IProverSequent sequent) {
		final Iterator<DependPredicate> iter = unsatisfied.iterator();
		while(iter.hasNext()) {
			final DependPredicate unsat = iter.next();
			if (unsat.isSatisfiedBy(sequent)) {
				iter.remove();
			}
		}
	}
	
	public boolean isSatisfied() {
		return unsatisfied.isEmpty();
	}

	public void addNeededSequent(ProducedSequent sequent) {
		if (!neededSequents.contains(sequent)) {
			neededSequents.add(sequent);
		}
	}
	
	public List<ProducedSequent> getNeededSequents() {
		return neededSequents;
	}

	@Override
	protected void propagateDelete() {
		for(ProducedSequent needed: neededSequents) {
			needed.deleteDependent(this);
		}
	}
	
	@Override
	public String toString() {
		final String predsStr = super.toString();
		final StringBuilder sb = new StringBuilder(predsStr);
		if (isSatisfied()) {
			sb.append("  >>SAT");
		} else {
			sb.append("  >>UNSAT: ");
			seqToString(unsatisfied, sb);
		}
		return sb.toString();
	}
}
