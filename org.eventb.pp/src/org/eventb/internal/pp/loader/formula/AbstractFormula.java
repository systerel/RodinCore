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
package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Abstract class for formulas that have a term index.
 * 
 * @author François Terrier
 */
public abstract class AbstractFormula<T extends LiteralDescriptor> {

	protected List<TermSignature> terms;
	protected T descriptor;

	public int getIndexSize() {
		return descriptor.getResults().get(0).getTerms().size();
	}

	public AbstractFormula(List<TermSignature> terms, T descriptor) {
		this.descriptor = descriptor;
		this.terms = terms;
	}

	public T getLiteralDescriptor() {
		return descriptor;
	}

	public List<TermSignature> getTerms() {
		return terms;
	}
	
	abstract boolean getContextAndSetLabels(LabelContext context, LabelManager manager);
	
	abstract ClauseResult getClauses(List<TermSignature> termList, LabelManager manager, ClauseResult prefix, ClauseContext context);

	abstract Literal<?, ?> getLabelPredicate(List<TermSignature> terms, ClauseContext context);

	abstract void split();
	
	
	static final List<Term> getTermsFromTermSignature(List<TermSignature> termList, ClauseContext context) {
		// transform the terms
		List<Term> terms = new ArrayList<Term>();
		for (TermSignature term : termList) {
			terms.add(term.getTerm(context));
		}
		return terms;
	}
	
	static final PredicateLiteralDescriptor getPredicateDescriptor(
			PredicateTable table, int index, int arity, int realArity,
			boolean isLabel, boolean isMembership, List<Sort> sortList,
			Sort sort) {
		final PredicateLiteralDescriptor predicateDescriptor;
		if (table.hasDescriptor(index)) {
			predicateDescriptor = table.getDescriptor(index);
		} else {
			predicateDescriptor = table.newDescriptor(index, arity, realArity,
					isLabel, isMembership, sortList);
			table.addDescriptor(index, predicateDescriptor);
			if (sort != null)
				table.addSort(sort, predicateDescriptor);
		}
		return predicateDescriptor;
	}
	
	static final List<Sort> getSortList(List<TermSignature> signatures) {
		List<Sort> result = new ArrayList<Sort>();
		for (TermSignature termSignature : signatures) {
			result.add(termSignature.getSort());
		}
		return result;
	}
	
	/**
	 * Returns the string representation of the dependencies of this signature.
	 * Used by the {@link Object#toString()} method.
	 * 
	 * @return the string representation of the dependencies of this signature
	 */
	public String getStringDeps() {
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractFormula) {
			AbstractFormula<?> temp = (AbstractFormula<?>) obj;
			return descriptor.equals(temp.descriptor);
		}
		return false;
	}

	// terms are not taken into account for the computation of the hashcode and
	// for the equals method. This is because AbstractFormulas are used in
	// ClauseKey
	// for the clause hash key, which does not take terms into account
	@Override
	public int hashCode() {
		return descriptor.hashCode();
	}

	@Override
	public String toString() {
		return descriptor.toString();
	}

	abstract String toTreeForm(String prefix);

}