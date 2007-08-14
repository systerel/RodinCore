/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.terms;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.ClauseContext;

/**
 * This class represents an arithmetic term signature. It is the abstract base
 * class for complex term signatures. The sort is always the same and is given
 * by {@link Sort#NATURAL}.
 *
 * @author François Terrier
 *
 */
public abstract class AssociativeTermSignature extends TermSignature {

	protected List<TermSignature> terms;
	
	public AssociativeTermSignature(List<TermSignature> terms) {
		super(Sort.NATURAL);
		
		this.terms = terms;
	}
	
	public AssociativeTermSignature(TermSignature term) {
		super(Sort.NATURAL);
		
		this.terms = new ArrayList<TermSignature>();
		this.terms.add(term);
	}
	
	/**
	 * Returns a copy of the children of this term signature. This is 
	 * a helper method intended to be used by subclasses.
	 * 
	 * @return a copy of the children
	 */
	protected List<TermSignature> deepCopyHelper() {
		List<TermSignature> copy = new ArrayList<TermSignature>();
		for (TermSignature term : terms) {
			copy.add(term.deepCopy());
		}
		return copy;
	}

	/**
	 * Returns a copy of the children of this term signature where locally quantified variables
	 * are replaced by its corresponding instance of {@link QuantifiedVariableHolder},
	 * and where sub-trees that are not quantified (with respect to the
	 * {@link TermSignature#isQuantified(int, int)} method) are replaced by an
	 * instance of {@link VariableHolder}. The terms that are not quantified are fetched
	 * added to the {@link TermSymbolTable} table passed as a parameter, their index
	 * is added to the List indexList and the term itself is appended to termList.
	 * @param startOffset TODO
	 * @param endOffset TODO
	 * @param termList the term list where each free term is added
	 * @param lastQuantifiedOffset the offset of locally bound variables.
	 * @param table the symbol table for terms, to retrieve existing terms
	 * @param indexList the index list where each free term index is added
	 * 
	 * @return a copy of this signature where free terms are replaced by {@link VariableHolder}
	 * and quantified terms are replaced by {@link QuantifiedVariableHolder}
	 */
	protected List<TermSignature> getUnquantifiedTermHelper(int startOffset, int endOffset, List<TermSignature> termList) {
		List<TermSignature> signatures = new ArrayList<TermSignature>();
		for (TermSignature term : terms) {
			TermSignature sig;
			if (term.isQuantified(startOffset,endOffset)) {
				sig = term.getUnquantifiedTerm(startOffset, endOffset, termList);
			} else {
				addTermCopy(term, termList);
				sig = new VariableHolder(sort); 
			}
			signatures.add(sig);
		}
		return signatures;
	}
	
	protected List<TermSignature> getSimpleTermHelper(List<TermSignature> termList) {
		List<TermSignature> signatures = new ArrayList<TermSignature>();
		for (TermSignature term : terms) {
			TermSignature sig = term.getSimpleTerm(termList);
			signatures.add(sig);
		}
		return signatures;
	}
	
	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		for (TermSignature term : terms) {
			term.appendTermFromTermList(indexList, newList, startOffset, endOffset);
		}
	}
	
	public List<Term> getTermHelper(ClauseContext context) {
		List<Term> result = new ArrayList<Term>();
		for (TermSignature term : terms) {
			result.add(term.getTerm(context));
		}
		return result;
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		for (TermSignature term : terms) {
			if (term.isQuantified(startOffset, endOffset)) return true;
		}
		return false;
	}

	@Override
	public boolean isConstant() {
		for (TermSignature term : terms) {
			if (!term.isConstant()) return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociativeTermSignature) {
			AssociativeTermSignature temp = (AssociativeTermSignature) obj;
			return terms.equals(temp.terms);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("(");
		for (TermSignature term : terms) {
			str.append(term.toString() + " ");
		}
		str.deleteCharAt(str.length()-1);
		str.append(")");
		return str.toString();
	}
}
