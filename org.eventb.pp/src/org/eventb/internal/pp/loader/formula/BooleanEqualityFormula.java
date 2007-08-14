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
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TrueConstantSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class BooleanEqualityFormula extends EqualityFormula {

	public BooleanEqualityFormula(List<TermSignature> terms,
			EqualityDescriptor descriptor) {
		super(terms,descriptor);
		
		assert descriptor.getSort().equals(Sort.BOOLEAN);
	}
	
	@Override
	void split() {
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		for (IIntermediateResult res : getLiteralDescriptor().getResults()) {
			if (contains(res.getTerms(), getTerms().get(0)) ||
				contains(res.getTerms(), getTerms().get(1))) {
					result.add(res);
			}
		}
		
		if (result.size() != descriptor.getResults().size()) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Splitting "+this+", terms remaining: "+result.toString());
		}
		descriptor = new EqualityDescriptor(descriptor.getContext(), result, descriptor.getSort());
	}
	
	private boolean contains(List<TermSignature> list, TermSignature sig) {
		return sig instanceof TrueConstantSignature ? false : list.contains(sig);
	}
	
	@Override
	Literal<?,?> getLabelPredicate(List<TermSignature> termList, ClauseContext context) {
		List<TermSignature> newList = descriptor.getUnifiedResults();
		Literal<?,?> result;
		if (newList.get(1) instanceof TrueConstantSignature) {
			TermSignature sig = termList.get(0);
			Integer i;
			if (context.getBooleanTable().containsKey(sig)) i = context.getBooleanTable().get(sig);
			else {
				i = context.getBooleanTable().getNextLiteralIdentifier();
				context.getBooleanTable().put(sig, i);
			}
			PredicateLiteralDescriptor predicateDescriptor =
				getPredicateDescriptor(context.getPredicateTable(),
				i, 0, terms.size(), false, new ArrayList<Sort>(), null);
			result = new AtomicPredicateLiteral(predicateDescriptor, context.isPositive());
		} else {
			List<Term> newTerms = getTermsFromTermSignature(termList, context);
			SimpleTerm term1 = (SimpleTerm)newTerms.get(0);
			SimpleTerm term2 = (SimpleTerm)newTerms.get(1);
			result = new EqualityLiteral(term1,term2, context.isPositive());
		}
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Creating literal from "+this+": "+result);
		return result;
	}
	
}
