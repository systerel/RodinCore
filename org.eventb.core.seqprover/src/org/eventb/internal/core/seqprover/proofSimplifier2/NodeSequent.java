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
 * 
 * Note: equals() and hashcode() must not be overridden.
 * 
 * @author Nicolas Beauger
 */
public abstract class NodeSequent {

	protected final Collection<DependPredicate> predicates = new ArrayList<DependPredicate>();

	private final DependNode node;
	protected boolean deleted = false;

	public NodeSequent(Collection<Predicate> hyps, Predicate goal, DependNode node) {
		for (Predicate hyp : hyps) {
			predicates.add(new DependPredicate(hyp, false));
		}
		if (goal != null) {
			predicates.add(new DependPredicate(goal, true));
		}
		this.node = node;
	}

	public DependNode getNode() {
		return node;
	}
	
	public final void delete() {
		if (deleted) {
			return;
		}
		deleted = true;
	}
	
	protected abstract void propagateDelete();
	
	protected static void seqToString(Collection<DependPredicate> preds, StringBuilder sb) {
		boolean hasGoal = false;
		String sep = "";
		for (DependPredicate pred : preds) {
			if (pred.isGoal()) {
				hasGoal = true;
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
		if (!hasGoal) {
			sb.append(" |-");
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		seqToString(predicates, sb);
		return sb.toString();
	}

}
