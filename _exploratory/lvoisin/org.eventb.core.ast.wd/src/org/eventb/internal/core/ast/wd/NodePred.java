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

	private Predicate predicate;
	private boolean used;

	public NodePred(Predicate pred) {
		this.predicate = pred;
		this.used = true; 
	}

	@Override
	public int depth() {
		return 0;
	}

	@Override
	public void quantifierShifter(int offset, FormulaFactory ff) {
		predicate = predicate.shiftBoundIdentifiers(offset, ff);
	}

	@Override
	public void quantifierUnShifter(int offset, FormulaFactory ff) {
		predicate = predicate.shiftBoundIdentifiers(-offset, ff);
	}

	@Override
	public Predicate asPredicate(FormulaBuilder fb) {
		if (used)
			return predicate;
		else
			return fb.btrue;
	}

	@Override
	public String toString() {
		return predicate.toString();
	}

	@Override
	public void simplifyImplications(Set<Predicate> knownPredicates) {
		// Do nothing
	}

	@Override
	public void simplifyIsolatedPredicates(Set<Predicate> knownPredicates) {
		if (!knownPredicates.add(predicate))
			used = false;
	}
}