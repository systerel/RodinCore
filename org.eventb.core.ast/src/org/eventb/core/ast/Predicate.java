package org.eventb.core.ast;



/**
 * Represents predicates.
 * 
 * TODO: document Predicate.
 * 
 * @author Laurent Voisin
 */
public abstract class Predicate extends Formula<Predicate> {

	// True iff this formula has been type-checked
	private boolean typeChecked;
	
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
	protected Predicate getTypedThis() {
		return this;
	}

	@Override
	public boolean isTypeChecked() {
		return typeChecked;
	}

	protected final boolean finalizeTypeCheck(boolean childrenOK) {
		assert typeChecked == false || typeChecked == childrenOK;
		typeChecked = childrenOK;
		return typeChecked;
	}

}
