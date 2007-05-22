package org.eventb.internal.pp.loader.clause;

import java.util.Set;

import org.eventb.internal.pp.core.elements.IClause;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public class LoaderResult {

	// TODO it is questionable whether we already separate this here
	// unit clauses are also clauses
	private Set<IClause> clauses;
//	private Collection<IClause> unitClauses;
	
	protected LoaderResult(Set<IClause> clauses) {
		this.clauses = clauses;
//		this.unitClauses = unitClauses;
	}
	
	/**
	 * Returns the non-unit clauses.
	 * 
	 * @return the non-unit clauses
	 */
	public Set<IClause> getClauses() {
		return clauses;
	}
	
	@Override
	public String toString() {
		return clauses.toString();
	}
	
//	/**
//	 * Returns the unit clauses.
//	 * 
//	 * @return the unit clauses
//	 */
//	public Collection<IClause> getLiterals() {
//		return unitClauses;
//	}
}
