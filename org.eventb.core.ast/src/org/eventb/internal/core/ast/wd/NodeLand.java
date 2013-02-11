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

import org.eventb.core.ast.Predicate;

/**
 * Implementation of the conjunction
 */

public class NodeLand extends Node {

	private final Node[] children;

	public NodeLand(Node[] children) {
		this.children = children;
	}

	@Override
	protected int maxBindingDepth() {
		int depth = 0;
		for (Node child : children) {
			depth = Math.max(depth, child.maxBindingDepth());
		}
		return depth;
	}

	@Override
	protected void boundIdentifiersEqualizer(int offset) {
		for (Node child : children) {
			child.boundIdentifiersEqualizer(offset);
		}
	}

	@Override
	protected Predicate internalAsPredicate(FormulaBuilder fb, boolean original) {
		final int length = children.length;
		final Predicate[] childPreds = new Predicate[length];
		for (int i = 0; i < length; i++) {
			childPreds[i] = children[i].asPredicate(fb, original);
		}
		return fb.land(childPreds);
	}

	@Override
	protected void collectAntecedents(Set<Predicate> antecedents,
			FormulaBuilder fb) {
		for (Node child : children) {
			child.collectAntecedents(antecedents, fb);
		}
	}

	@Override
	protected void internalSimplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb) {
		for (Node child : children) {
			child.simplify(knownLemmas, antecedents, fb);
		}
	}

	@Override
	protected void internalToString(StringBuilder sb, String indent) {
		sb.append("LAND\n");
		for (Node child : children) {
			child.toString(sb, indent + "  ");
		}
	}

}