/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import static org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException.checkCancel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException;

/**
 * @author Nicolas Beauger
 * 
 */
public class RequiredSequent extends NodeSequent {

	private final List<ProducedSequent> neededSequents = new ArrayList<ProducedSequent>();
	private final Collection<DependPredicate> unsatisfied;

	public RequiredSequent(Collection<Predicate> hyps, Predicate goal,
			DependNode node) {
		super(hyps, goal, node);
		this.unsatisfied = new ArrayList<DependPredicate>(predicates);
	}

	public void satisfyWith(ProducedSequent produced) {
		final boolean depends = unsatisfied.removeAll(produced.predicates);
		if (depends) {
			neededSequents.add(produced);
			produced.addDependentSequent(this);
		}
	}

	// used for checking satisfaction by the root sequent
	// that is not a produced sequent and for which
	// no dependencies shall be created
	public void satisfyWith(IProverSequent sequent) {
		final Iterator<DependPredicate> iter = unsatisfied.iterator();
		while (iter.hasNext()) {
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
	public void propagateDelete(IProofMonitor monitor) throws CancelException {
		for (ProducedSequent needed : neededSequents) {
			checkCancel(monitor);
			needed.deleteDependent(this, monitor);
		}
		neededSequents.clear();
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
