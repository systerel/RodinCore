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
 * NodeLand represents a conjunction.
 */

public class NodeLand extends Node {

	private final Node[] children;

	public NodeLand(Node[] children) {
		this.children = children;
	}

	@Override
	public int depth() {
		int depth = 0;
		for (Node child : children) {
			depth = Math.max(depth, child.depth());
		}
		return depth;
	}

	@Override
	public void quantifierShifter(int offset, FormulaFactory ff) {
		for (Node child : children) {
			child.quantifierShifter(offset, ff);
		}
	}

	@Override
	public void quantifierUnShifter(int offset, FormulaFactory ff) {
		for (Node child : children) {
			child.quantifierUnShifter(offset, ff);
		}
	}

	@Override
	public Predicate asPredicate(FormulaBuilder fb) {
		final int length = children.length;
		final Predicate[] childPreds = new Predicate[length];
		for (int i = 0; i < length; i++) {
			childPreds[i] = children[i].asPredicate(fb);
		}
		return fb.land(childPreds);
	}

	@Override
	public void simplifyImplications(Set<Predicate> knownPredicates) {
		for (Node child : children) {
			child.simplifyImplications(knownPredicates);
		}
	}

	@Override
	public void simplifyIsolatedPredicates(Set<Predicate> knownPredicates) {
		for (Node child : children) {
			child.simplifyIsolatedPredicates(knownPredicates);
		}
	}

}