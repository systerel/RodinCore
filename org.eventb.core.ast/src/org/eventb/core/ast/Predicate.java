/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Represents predicates.
 * 
 * TODO: document Predicate.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Predicate extends Formula<Predicate> {

	/**
	 * Creates a new predicate with the specified tag and source location.
	 * 
	 * @param tag node tag of this predicate
	 * @param location source location of this predicate
	 * @param hashCode combined hash code for children
	 */
	protected Predicate(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
	}

	@Override
	protected final Predicate getTypedThis() {
		return this;
	}

	protected abstract void synthesizeType(FormulaFactory ff);
	
	@Override
	protected final boolean solveType(TypeUnifier unifier) {
		if (isTypeChecked()) {
			return true;
		}
		boolean success = solveChildrenTypes(unifier);
		if (success) {
			synthesizeType(unifier.getFormulaFactory());
		}
		return isTypeChecked();
	}

	// Calls recursively solveType on each child of this node and
	// returns true if all calls where successful.
	protected abstract boolean solveChildrenTypes(TypeUnifier unifier);
	
	@Override
	protected final Predicate getCheckedReplacement(SingleRewriter rewriter) {
		return checkReplacement(rewriter.getPredicate());
	}
	
	@Override
	protected final Predicate checkReplacement(Predicate replacement)  {
		if (this == replacement)
			return this;
		if (isTypeChecked() && ! replacement.isTypeChecked())
			throw new IllegalStateException(
					"Rewritten formula should be type-checked");
		return replacement;
	}

}
