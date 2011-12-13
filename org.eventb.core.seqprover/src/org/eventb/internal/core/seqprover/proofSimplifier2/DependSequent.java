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
	private final Collection<DependPredicate> unsatisfied;

	public DependSequent(Iterable<Predicate> hyps, Predicate goal) {
		for (Predicate hyp : hyps) {
			predicates.add(new DependPredicate(hyp, false));
		}
		if (goal != null) {
			predicates.add(new DependPredicate(goal, true));
		}
		unsatisfied = new ArrayList<DependPredicate>(predicates);
	}
	
	public Collection<DependPredicate> neededPredicates(DependSequent other) {
		final Collection<DependPredicate> result = new ArrayList<DependPredicate>(
				this.predicates);
		result.retainAll(other.predicates);
		return result;
	}
	
	public boolean satisfy(DependSequent other) {
		//TODO remember dependencies
		return unsatisfied.removeAll(other.predicates);
	}
	
	public boolean isSatisfied() {
		return unsatisfied.isEmpty();
	}
	
	private static void seqToString(Collection<DependPredicate> preds, StringBuilder sb) {
		String sep = "";
		for (DependPredicate pred : preds) {
			if (pred.isGoal()) {
				if (sep.length() != 0) {
					sb.append(' ');
				}
				sb.append("|- ");
			} else {
				sb.append(sep);
				sep = " ;; ";
			}
			sb.append(pred.getPredicate());
		}

	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		seqToString(predicates, sb);
		if (isSatisfied()) {
			sb.append("  >>SAT");
		} else {
			sb.append("  >>UNSAT: ");
			seqToString(unsatisfied, sb);
		}
		return sb.toString();
	}
}