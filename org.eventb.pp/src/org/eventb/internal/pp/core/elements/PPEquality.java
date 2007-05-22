/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class PPEquality extends AbstractPPLiteral<IEquality> implements IEquality {

	private boolean isPositive;
	
	public PPEquality (Term term1, Term term2, boolean isPositive) {
//		super(Arrays.asList(new Term[]{term1,term2}));
		super(Arrays.asList(term1.compareTo(term2)<0?new Term[]{term1,term2}:new Term[]{term2,term1}));
		// TODO term must be ordered
		
		if (term1.getSort() != null && term2.getSort()!=null) {
			assert term1.getSort().equals(term2.getSort());
		}
		
		this.isPositive = isPositive;
	}
	
	public Term getTerm1() {
		return terms.get(0);
	}
	
	public Term getTerm2() {
		return terms.get(1);
	}
	
	private PPEquality(List<Term> terms, boolean isPositive) {
		super(terms);
		
		this.isPositive = isPositive;
	}
	
	public Sort getSort() {
		return terms.get(0).getSort();
	}

	
//	public String toString() {
//		StringBuffer str = new StringBuffer();
//		str.append(isPositive?"":"¬");
//		str.append("E" + getSort() + "(");
//		for (Term term : getTerms()) {
//			str.append(term.toString());
//			str.append(",");
//		}
//		str.deleteCharAt(str.length()-1);
//		str.append(")");
//		return str.toString();
//	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(getTerm1().toString(variableMap));
		str.append(isPositive?"=":"≠");
		str.append(getTerm2().toString(variableMap));
		return str.toString();
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public IEquality getInverse() {
		return new PPEquality(getInverseHelper(),!isPositive);
	}

	public IEquality substitute(Map<AbstractVariable, ? extends Term> map) {
		return new PPEquality(substituteHelper(map),isPositive);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPEquality) {
			PPEquality temp = (PPEquality) obj;
			return isPositive == temp.isPositive && super.equals(temp);
		}
		return false;
	}

//	@Override
//	public boolean equalsWithDifferentVariables(IEquality literal, HashMap<AbstractVariable, AbstractVariable> map) {
//		if (literal instanceof PPEquality) {
//			PPEquality temp = (PPEquality) literal;
//			if (isPositive != temp.isPositive) return false;
//			else {
//				HashMap<AbstractVariable, AbstractVariable> copy = new HashMap<AbstractVariable, AbstractVariable>(map);
//				if (term1.equalsWithDifferentVariables(temp.term1, copy)
//				 && term2.equalsWithDifferentVariables(temp.term2, copy))
//					return true;
//				copy = new HashMap<AbstractVariable, AbstractVariable>(map);
//				return term1.equalsWithDifferentVariables(temp.term2, copy)
//					&& term2.equalsWithDifferentVariables(temp.term1, copy);
//			}
//		}
//		return false;
//	}
	
	@Override
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPEquality) {
			PPEquality temp = (PPEquality) literal;
			return (isPositive == temp.isPositive) && super.equalsWithDifferentVariables(literal, map);
		}
		return false;
	}
	
	@Override
	public int hashCodeWithDifferentVariables() {
		return super.hashCodeWithDifferentVariables() + (isPositive?1:2);
	}

}
