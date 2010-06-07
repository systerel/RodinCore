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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * NodeForAll represents a quantifier "for all".
 */
public class NodeForAll extends Node {

	private final BoundIdentDecl[] decls;
	private Node child;

	public NodeForAll(BoundIdentDecl[] decls, Node child) {
		this.decls = decls;
		this.child = child;
	}

	@Override
	public int depth() {
		return child.depth() + decls.length;
	}

	@Override
	public void quantifierShifter(int offset, FormulaFactory ff) {
		assert offset >= 0;
		child.quantifierShifter(offset - decls.length, ff);
	}

	@Override
	public void quantifierUnShifter(int offset, FormulaFactory ff) {
		assert offset >= 0;
		child.quantifierUnShifter(offset - decls.length, ff);
	}

	@Override
	public Predicate asPredicate(FormulaBuilder fb) {
		return fb.forall(decls, child.asPredicate(fb));
	}

	@Override
	public void simplifyImplications(Set<Predicate> knownPredicates) {
		child.simplifyImplications(knownPredicates);
	}

	@Override
	public void simplifyIsolatedPredicates(Set<Predicate> knownPredicates) {
		child.simplifyIsolatedPredicates(knownPredicates);
	}

}