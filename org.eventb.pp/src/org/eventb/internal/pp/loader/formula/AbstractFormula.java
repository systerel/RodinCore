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

import org.eventb.internal.pp.core.elements.AtomicPredicateLiteral;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateDescriptor;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
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
	
	abstract boolean getContextAndSetLabels(LabelVisitor context,
			LabelManager manager);
	
	
	final Literal<?, ?> getLiteral(int index, List<TermSignature> terms,
			TermVisitorContext context, VariableTable table) {
		List<TermSignature> newList = descriptor.getSimplifiedList(terms);
		if (ClauseBuilder.DEBUG)
			ClauseBuilder.debug("Simplified term list for " + this + " is: " + newList);
		Literal<?, ?> result;
		if (newList.size() == 0) {
			result = new AtomicPredicateLiteral(new PredicateDescriptor(index, 
					context.isPositive));
		} else {
			List<Term> newTerms = getTermsFromTermSignature(newList, context, table);
			result = new ComplexPredicateLiteral(new PredicateDescriptor(index, 
					context.isPositive), getSimpleTermsFromTerms(newTerms));
		}
		if (ClauseBuilder.DEBUG)
			ClauseBuilder.debug("Creating literal from " + this + ": " + result);
		return result;
	}

	final List<SimpleTerm> getSimpleTermsFromTerms(List<Term> terms) {
		// TODO find how to remove this ugly code
		List<SimpleTerm> result = new ArrayList<SimpleTerm>();
		for (Term term : terms) {
			result.add((SimpleTerm) term);
		}
		return result;
	}

	final List<Term> getTermsFromTermSignature(
			List<TermSignature> termList, TermVisitorContext context,
			VariableTable table) {
		// transform the terms
		List<Term> terms = new ArrayList<Term>();
		for (TermSignature term : termList) {
			terms.add(term.getTerm(table, context));
		}
		return terms;
	}

	abstract List<List<Literal<?, ?>>> getClauses(
			List<TermSignature> termList, LabelManager manager,
			List<List<Literal<?, ?>>> prefix, TermVisitorContext flags,
			VariableTable table, BooleanEqualityTable bool);

	abstract Literal<?, ?> getLiteral(List<TermSignature> terms,
			TermVisitorContext flags, VariableTable table /* , context */,
			BooleanEqualityTable bool);

	abstract void split();

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
	// for the clause hash key, which does must not take terms into account
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
