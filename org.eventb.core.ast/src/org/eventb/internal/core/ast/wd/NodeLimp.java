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

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * Implementation of the implication
 */

public class NodeLimp extends Node {

	private Node left;
	private Node right;

	public NodeLimp(Node left, Node right) {
		this.left = left;
		this.right = right;
	}

	@Override
	protected int maxBindingDepth() {
		return Math.max(left.maxBindingDepth(), right.maxBindingDepth());
	}

	@Override
	protected void boundIdentifiersEqualizer(int offset, FormulaFactory ff) {
		left.boundIdentifiersEqualizer(offset, ff);
		right.boundIdentifiersEqualizer(offset, ff);
	}

	@Override
	protected Predicate internalAsPredicate(FormulaBuilder fb, boolean original) {
		return fb.limp(left.asPredicate(fb, original),
				right.asPredicate(fb, original));
	}

	@Override
	protected void collectAntecedents(Set<Predicate> antecedents,
			FormulaBuilder fb) {
		addPredicateToSet(antecedents, fb);
	}

	@Override
	protected void internalSimplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb) {
		final Set<Predicate> leftAntes = new HashSet<Predicate>();
		final Set<Lemma> leftLemmas = new HashSet<Lemma>(knownLemmas);
		left.simplify(leftLemmas, leftAntes, fb);
		// Collect the antecedents from the left side of the implication to
		// simplify the right side
		final Set<Predicate> rightAntes = new HashSet<Predicate>(antecedents);
		left.collectAntecedents(rightAntes, fb);
		right.simplify(knownLemmas, rightAntes, fb);
	}

	@Override
	protected void internalToString(StringBuilder sb, String indent) {
		sb.append("LIMP\n");
		final String childIndent = indent + "  ";
		left.toString(sb, childIndent);
		right.toString(sb, childIndent);
	}

}