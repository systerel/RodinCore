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

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * This class represents a predicate with arguments.
 *
 * @author François Terrier
 *
 */
public class ComplexPredicateLiteral extends PredicateLiteral {

	public ComplexPredicateLiteral (PredicateDescriptor descriptor, List<SimpleTerm> terms) {
		super(descriptor, terms);
		assert terms != null;
	}

	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(descriptor.isPositive()?"":"¬");
		str.append("P" + descriptor.getIndex());
		str.append(super.toString(variableMap));
		return str.toString();
	}

	@Override
	public ComplexPredicateLiteral getInverse() {
		return new ComplexPredicateLiteral(descriptor.getInverse(), getInverseHelper(terms));
	}
	
	@Override
	public ComplexPredicateLiteral substitute(Map<SimpleTerm, SimpleTerm> map) {
		return new ComplexPredicateLiteral(descriptor, substituteHelper(map,terms));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComplexPredicateLiteral) {
			ComplexPredicateLiteral temp = (ComplexPredicateLiteral) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(PredicateLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		return literal instanceof ComplexPredicateLiteral && super.equalsWithDifferentVariables(literal, map);
	}

}
