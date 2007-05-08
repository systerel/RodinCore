/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.PPPredicate;
import org.eventb.internal.pp.core.elements.PPProposition;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Abstract class for formulas that have a term index.
 *
 * @author François Terrier
 */
public abstract class AbstractFormula<T extends LiteralDescriptor> implements ISubFormula<T> {

	public int getIndexSize() {
		return descriptor.getResults().get(0).getTerms().size();
	}

//	/**
//	 * Returns the factored term list of this literal, 
//	 * 
//	 * Precondition to this method is that the term list passed as a parameter must
//	 * correspond to the index list of this signature, i.e. if two indexes are equal in
//	 * the index list, the two corresponding terms are equal in the term list.
//	 * @param indexList TODO
//	 * @param termList
//	 * @param indexList
//	 * 
//	 * @return 
//	 */
//	protected static List<Term> getSimplifiedTerms(List<Index> indexList, List<Term> termList) {
//		HashSet<Index> set = new HashSet<Index>();
//		List<Term> result = new ArrayList<Term>();
//		for (int i = 0; i < indexList.size(); i++) {
//			Index key = indexList.get(i);
//			if (!(key instanceof ConstantIndex) && !set.contains(key)) {
//				set.add(key);
//				result.add(termList.get(i));
//			}
//		}
//		return result;
//	}
	
	protected List<TermSignature> terms;
	protected T descriptor;
	
	public AbstractFormula(List<TermSignature> terms, T descriptor) {
		this.descriptor = descriptor;
		this.terms = terms;
	}
	
//	@Deprecated
//	public AbstractFormula(List<TermSignature> terms, T descriptor) {
//		this.descriptor = descriptor;
//		this.terms = terms;
//	}

	public T getLiteralDescriptor() {
		return descriptor;
	}
	
	public List<TermSignature> getTerms() {
		return terms;
	}
	
	protected ILiteral<?> getLiteral(int index, List<TermSignature> terms,
			TermVisitorContext context, VariableTable table) {
		List<TermSignature> newList = descriptor.getSimplifiedList(terms);
		ClauseBuilder.debug("Simplified term list for "+this+" is: "+newList);
		ILiteral<?> result;
		if (newList.size() == 0) {
			result = new PPProposition(index, context.isPositive);
		} else {
			List<Term> newTerms = getTermsFromTermSignature(newList, context, table);
			result = new PPPredicate(index, context.isPositive, newTerms);
		}
		ClauseBuilder.debug("Creating literal from "+this+": "+result);
		return result;
	}
	
	protected List<Term> getTermsFromTermSignature(List<TermSignature> termList, TermVisitorContext context, VariableTable table) {
		// transform the terms
//		table.pushTable();
		List<Term> terms = new ArrayList<Term>();
		for (TermSignature term : termList) {
			terms.add(term.getTerm(table, context));
		}
//		table.popTable();
		return terms;
	}
	
	public List<List<ILiteral<?>>> getClauses(List<TermSignature> terms, LabelManager manager, VariableTable table, TermVisitorContext flags, BooleanEqualityTable bool) {
		List<List<ILiteral<?>>> result = new ArrayList<List<ILiteral<?>>>();
		result.add(new ArrayList<ILiteral<?>>());
		return getClauses(terms, manager, result, flags, table, bool);
	}
	
    /**
     * Returns the string representation of the dependencies of this
     * signature. Used by the {@link Object#toString()} method.
     *
     * @return the string representation of the dependencies of this
     * signature
     */
    public String getStringDeps() {
    	return "";
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof AbstractFormula) {
    		AbstractFormula<?> temp = (AbstractFormula)obj;
    		return descriptor.equals(temp.descriptor);
    	}
    	return false;
    }
    
    // terms are not taken into account for the computation of the hashcode and
    // for the equals method. This is because AbstractFormulas are used in ClauseKey
    // for the clause hash key, which does must not take terms into account
    @Override
    public int hashCode() {
    	return descriptor.hashCode();
    }

    @Override 
    public String toString() {
    	return descriptor.toString();
    }

//    private Predicate originalPredicate;
//    public Predicate getOriginalPredicate() {
//    	return originalPredicate;
//    }
    
}
