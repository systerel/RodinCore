/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich.
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

public class BooleanEqualityFormula extends EqualityFormula {

	public BooleanEqualityFormula(List<TermSignature> terms,
			EqualityDescriptor descriptor) {
		super(terms,descriptor);
		
		assert descriptor.getSort().equals(Sort.BOOLEAN);
	}
	
	@Override
	Literal<?,?> getLabelPredicate(List<TermSignature> termList, ClauseContext context) {
		List<TermSignature> newList = descriptor.getUnifiedResults();
		Literal<?,?> result;
		if (isSpecialTrue(newList)) {
			TermSignature sig = termList.get(0);
			int i = context.getBooleanTable().getIntegerForTermSignature(sig);
			PredicateLiteralDescriptor predicateDescriptor =
				getPredicateDescriptor(context.getPredicateTable(),
				i, 0, terms.size(), false, false, new ArrayList<Sort>(), null);
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

	/*
	 * Special case when all boolean equalities have form "t = TRUE" and "t" is
	 * a constant.
	 */
	private static boolean isSpecialTrue(List<TermSignature> unified) {
		final TermSignature left = unified.get(0);
		final TermSignature right = unified.get(1);
		return left.isConstant() && right instanceof TrueConstantSignature;
	}
	
}
