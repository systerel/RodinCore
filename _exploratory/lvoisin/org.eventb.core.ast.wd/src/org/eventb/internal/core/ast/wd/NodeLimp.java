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
 * NodeLand represents an implication
 */

public class NodeLimp extends Node {

	private Node left;
	private Node right;

	public NodeLimp(Node left, Node right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int depth() {
		return Math.max(left.depth(), right.depth());
	}

	@Override
	public void quantifierShifter(int offset, FormulaFactory ff) {
		left.quantifierShifter(offset, ff);
		right.quantifierShifter(offset, ff);
	}

	@Override
	public void quantifierUnShifter(int offset, FormulaFactory ff) {
		left.quantifierUnShifter(offset, ff);
		right.quantifierUnShifter(offset, ff);
	}

	@Override
	public Predicate asPredicate(FormulaBuilder fb) {
		return fb.limp(left.asPredicate(fb), right.asPredicate(fb));
	}

	@Override
	public void simplifyImplications(Set<Predicate> knownPredicates) {
		Set<Predicate> localKnownPredicates = new HashSet<Predicate>();
		localKnownPredicates.addAll(knownPredicates);
		left.simplifyIsolatedPredicates(localKnownPredicates);
		left.simplifyImplications(localKnownPredicates);
		right.simplifyIsolatedPredicates(localKnownPredicates);
		right.simplifyImplications(localKnownPredicates);
	}

	@Override
	public void simplifyIsolatedPredicates(Set<Predicate> knownPredicates) {
		// Do nothing and skip traversal of children
	}

}