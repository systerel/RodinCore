package org.eventb.internal.pp.loader.predicate;

import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class is used to represent an intermediate result for the construction
 * of a literal or subformula.
 *
 * Calls to {@link IIntermediateResult#getTerms()} and {@link IIntermediateResult#
 * getIndexes()} should always return two lists of equal size and the indexes
 * should correspond to the terms.
 *
 * @author François Terrier
 *
 */
public interface IIntermediateResult {

	/**
	 * Returns the terms currently contained in this result.
	 * 
	 * @return the list of terms contained in this result
	 */
	public List<TermSignature> getTerms();
	
	
	public List<IIntermediateResult> getResultList();
	
	
}
