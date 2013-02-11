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

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Predicate;

/**
 * Implementation of the universal quantifier.
 */
public class NodeForAll extends Node {

	private final BoundIdentDecl[] decls;
	private Node child;

	public NodeForAll(BoundIdentDecl[] decls, Node child) {
		this.decls = decls;
		this.child = child;
	}

	@Override
	protected int maxBindingDepth() {
		return child.maxBindingDepth() + decls.length;
	}

	@Override
	protected void boundIdentifiersEqualizer(int offset) {
		assert offset >= 0;
		child.boundIdentifiersEqualizer(offset - decls.length);
	}

	@Override
	protected Predicate internalAsPredicate(FormulaBuilder fb, boolean original) {
		if (original) {
			return fb.forall(decls, child.asPredicate(fb, original));
		} else {
			return child.asPredicate(fb, original);
		}
	}

	@Override
	protected void collectAntecedents(Set<Predicate> antecedents,
			FormulaBuilder fb) {
		child.collectAntecedents(antecedents, fb);
	}

	@Override
	protected void internalSimplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb) {
		child.simplify(knownLemmas, antecedents, fb);
	}

	@Override
	protected void internalToString(StringBuilder sb, String indent) {
		sb.append("FORALL\n");
		child.toString(sb, indent + "  ");
	}

}