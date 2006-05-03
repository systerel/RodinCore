package org.eventb.core.ast;

import org.eventb.internal.core.typecheck.TypeUnifier;



/**
 * Represents predicates.
 * 
 * TODO: document Predicate.
 * 
 * @author Laurent Voisin
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
	
}
