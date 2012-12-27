/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula.terms;

import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.ClauseContext;

/**
 * This class represents a term signature. It is the abstract base class
 * for all term signatures.
 * <p>
 * All instances of this class are immutable.
 * </p>
 * 
 * @author François Terrier
 */
public abstract class TermSignature {
	
	protected final Sort sort;
	
	public TermSignature (Sort sort) {
		this.sort = sort;
	}
	
	/**
	 * Returns the sort of this signature.
	 * 
	 * @return the sort of this signature
	 */
	public Sort getSort() {
		return sort;
	}
	
	/**
	 * Returns <code>true</code> if this signature contains no variables.
	 * Returns <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this signature contains no variables
	 */
	public abstract boolean isConstant();
	
	/**
	 * Returns a copy of this term signature where unquantified sub-trees 
	 * (with respect to the {@link TermSignature#isQuantified(int, int)} method) 
	 * are replaced by an instance of {@link VariableHolder}. Those unquantified 
	 * sub-trees are added to the given termList.
	 * 
	 * @param startOffset the first quantified index
	 * @param endOffset the last quantified index
	 * @param termList the term list where each free term is added
	 * 
	 * @return a copy of this signature where free terms are replaced by {@link VariableHolder}
	 */
	public abstract TermSignature getUnquantifiedTerm(
			int startOffset, int endOffset, List<TermSignature> termList);
	
	/**
	 * Returns a copy of this term signature where all variable terms are 
	 * replaced by a {@link VariableHolder}. Those variable terms are added
	 * to termList.
	 * 
	 * @param termList the list where the variables are added
	 * @return a copy of this signature.
	 */
	public abstract TermSignature getSimpleTerm(List<TermSignature> termList);
	
	/**
	 * Adds copy to the symbol table, adds the corresponding index to indexList,
	 * and adds the term to termList. This is an helper method intended to be called
	 * by clients overriding {@link TermSignature#getUnquantifiedTerm(int, TermSymbolTable, List)}
	 * 
	 * @param copy the term to add
	 * @param termList the term list where the term is appended
	 */
	protected void addTermCopy(TermSignature term, List<TermSignature> termList) {
		termList.add(term.deepCopy());
	}
	
	/**
	 * Appends to new list a copy of this term where each {@link VariableHolder} is
	 * replaced by its corresponding term, obtained from termList.
	 * The size of the term list must be equivalent to the number of variable
	 * holders in this term.
	 * 
	 * This method is similar to {@link TermSignature#appendTermFromIndexList(List, List, boolean)}
	 * 
	 * @param indexList the list giving what should be put instead of the holders 
	 * @param newList the list where to append the obtained term
	 * @param startOffset the first quantified index
	 * @param endOffset the last quantified index
	 */
	public abstract void appendTermFromTermList(List<TermSignature> indexList,
			List<TermSignature> newList, int startOffset, int endOffset);

	/**
	 * Returns the instance of {@link Term} corresponding to this signature.
	 * 
	 * @param the clause context
	 * @return the instance of {@link Term} corresponding to this signature
	 */
	public abstract Term getTerm(ClauseContext context);
	
	/**
	 * Returns a unchanged deep copy of this signature, where the whole returned
	 * tree is a copy from this one.
	 * 
	 * @return an unchanged deep copy of this signature
	 */
	protected abstract TermSignature deepCopy();
	
	/**
	 * Returns <code>true</code> if this term contains a quantified subterm or is a 
	 * quantified variable itself. Returns <code>false</code> otherwise.
	 * 
	 * @param startOffset TODO
	 * @param endOffset TODO
	 * 
	 * @return <code>true</code> if this term contains a quantified subterm or is a 
	 * quantified variable itself. Returns <code>false</code> otherwise
	 */
	public abstract boolean isQuantified(int startOffset, int endOffset);
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
}
