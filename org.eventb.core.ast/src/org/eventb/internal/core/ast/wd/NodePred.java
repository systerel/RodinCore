/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.wd;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * Implementation of the leaf, used for regular predicates.
 */
public class NodePred extends Node {

	private final Predicate predicate;
	private Predicate normalized;

	public NodePred(Predicate pred) {
		this.predicate = pred;
	}

	@Override
	protected int maxBindingDepth() {
		return 0;
	}

	@Override
	protected void boundIdentifiersEqualizer(int offset, FormulaFactory ff) {
		normalized = predicate.shiftBoundIdentifiers(offset, ff);
	}

	@Override
	protected Predicate internalAsPredicate(FormulaBuilder fb, boolean original) {
		return original ? predicate : normalized;
	}

	@Override
	protected void collectAntecedents(Set<Predicate> antecedents,
			FormulaBuilder fb) {
		addPredicateToSet(antecedents, fb);
	}

	@Override
	protected void internalSimplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb) {
		if (antecedents.contains(normalized)) {
			setNodeSubsumed();
			return;
		}

		final Lemma lemma = new Lemma(antecedents, normalized, this);
		lemma.addToSet(knownLemmas);
	}

	@Override
	protected void internalToString(StringBuilder sb, String indent) {
		sb.append(normalized);
		sb.append('\n');

	}

}