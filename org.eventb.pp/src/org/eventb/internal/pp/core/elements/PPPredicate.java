/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * This class represents a predicate with arguments.
 *
 * @author François Terrier
 *
 */
public class PPPredicate extends AbstractPPPredicate implements IPredicate {

	public PPPredicate (int index, boolean isPositive, List<Term> terms) {
		super(index, isPositive, terms);
		assert terms != null;
	}

	public ILiteralDescriptor getDescriptor() {
		return new PredicateDescriptor(index);
	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(isPositive?"":"¬");
		str.append("P" + index);
		str.append(super.toString(variableMap));
		return str.toString();
	}

	public IPredicate getInverse() {
		return new PPPredicate(index, !isPositive, getInverseHelper());
	}
	
	public IPredicate substitute(Map<AbstractVariable, ? extends Term> map) {
		return new PPPredicate(index, isPositive, substituteHelper(map));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPPredicate) {
			PPPredicate temp = (PPPredicate) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPPredicate) {
			return super.equalsWithDifferentVariables(literal, map);
		}
		return false;
	}

	public boolean updateInstantiationCount(IPredicate predicate) {
		boolean result = false;
		for (int i = 0; i < terms.size(); i++) {
			Term term1 = terms.get(i);
			Term term2 = predicate.getTerms().get(i);
			if (!term1.isConstant() && term2.isConstant()) {
				term1.incrementInstantiationCount();
				if (term1.isBlocked()) result = true;
			}
			// we do not increment the instantiation count of the unit clause !
//			if (!term2.isConstant() && term1.isConstant()) {
//				term2.incrementInstantiationCount();
//				if (term2.isBlocked()) result = true;
//			}
		}
		return result;
	}
	
	public void resetInstantiationCount() {
		for (Term term : terms) {
			term.resetInstantiationCount();
		}
	}
	

}
