/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class PPEquality implements IEquality {

	private Term term1;
	private Term term2;
	private boolean isPositive;
	
	public PPEquality (Term term1, Term term2, boolean isPositive) {
		// TODO term must be ordered
		
		if (term1.getSort() != null && term2.getSort()!=null) {
			assert term1.getSort().equals(term2.getSort());
		}
		
		this.isPositive = isPositive;
		this.term1 = term1;
		this.term2 = term2;
	}
	
	public Sort getSort() {
		return term1.getSort();
	}

	@Override
	public int hashCode() {
		return term1.hashCode() + term2.hashCode();
	}
	
	public int hashCodeWithDifferentVariables() {
		return term1.hashCodeWithDifferentVariables() + term2.hashCodeWithDifferentVariables();
	}
	
	public List<Term> getTerms() {
		List<Term> result = new ArrayList<Term>();
		result.add(term1);
		result.add(term2);
		return result;
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
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}

	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(getTerms().get(0).toString(variableMap));
		str.append(isPositive?"=":"≠");
		str.append(getTerms().get(1).toString(variableMap));
		return str.toString();
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public IEquality getInverse() {
		return new PPEquality(term1.getInverse(),term2.getInverse(),!isPositive);
	}

	public IEquality substitute(Map<AbstractVariable, ? extends Term> map) {
		Term newterm1 = term1.substitute(map);
		Term newterm2 = term2.substitute(map);
		return new PPEquality(newterm1,newterm2,isPositive);
	}
	
	public IEquality getCopyWithNewVariables(IVariableContext context, 
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		Set<Variable> variables = new HashSet<Variable>();
		List<LocalVariable> localVariables = new ArrayList<LocalVariable>();
		term1.collectVariables(variables);
		term1.collectLocalVariables(localVariables);
		term2.collectVariables(variables);
		term2.collectLocalVariables(localVariables);
			
		for (Variable variable : variables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextVariable(variable.getSort()));
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, new LocalVariable(context.getNextLocalVariableID(),variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}

	public boolean isQuantified() {
		return term1.isQuantified() || term2.isQuantified();
	}

	public boolean isConstant() {
		return term1.isConstant() && term2.isConstant();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPEquality) {
			PPEquality temp = (PPEquality) obj;
			return isPositive == temp.isPositive && ((term1.equals(temp.term1) && term2.equals(temp.term2))
			|| (term2.equals(temp.term1) && term1.equals(temp.term2)));
		}
		return false;
	}

	public boolean equalsWithDifferentVariables(IEquality literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPEquality) {
			PPEquality temp = (PPEquality) literal;
			if (isPositive != temp.isPositive) return false;
			else {
				HashMap<AbstractVariable, AbstractVariable> copy = new HashMap<AbstractVariable, AbstractVariable>(map);
				if (term1.equalsWithDifferentVariables(temp.term1, copy)
				 && term2.equalsWithDifferentVariables(temp.term2, copy))
					return true;
				copy = new HashMap<AbstractVariable, AbstractVariable>(map);
				return term1.equalsWithDifferentVariables(temp.term2, copy)
					&& term2.equalsWithDifferentVariables(temp.term1, copy);
			}
		}
		return false;
	}

	
}
