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

	protected final Collection<DependPredicate> predicates = new ArrayList<DependPredicate>();

	public DependSequent(Iterable<Predicate> hyps, Predicate goal) {
		for (Predicate hyp : hyps) {
			predicates.add(new DependPredicate(hyp, false));
		}
		if (goal != null) {
			predicates.add(new DependPredicate(goal, true));
		}
	}
	
	protected static void seqToString(Collection<DependPredicate> preds, StringBuilder sb) {
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
		return sb.toString();
	}
}