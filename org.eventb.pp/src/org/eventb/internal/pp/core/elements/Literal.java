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
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public abstract class Literal<S extends Literal<S,T>, T extends Term> extends Hashable {

	final protected List<T> terms;
	
	public Literal(List<T> terms, int hashCode) {
		super( 
				combineHashCodes(combineHashCodesWithSameVariables(terms),hashCode),
				combineHashCodes(combineHashCodesWithDifferentVariables(terms),hashCode)
		);
		
		this.terms = terms;
	}

	public List<T> getTerms() {
		return new ArrayList<T>(terms);
	}
	
	public T getTerm(int index) {
		return terms.get(index);
	}
	
	public int getTermsSize() {
		return terms.size();
	}
	
	public boolean isQuantified() {
		for (T term : terms) {
			if (term.isQuantified()) return true;
		}
		return false;
	}

	public boolean isConstant() {
		for (T term : terms) {
			if (!term.isConstant()) return false;
		}
		return true;
	}
	
	public boolean isForall() {
		for (T term : terms) {
			if (term.isForall()) return true;
		}
		return false;
	}
	
	public S getCopyWithNewVariables(IVariableContext context, 
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap) {
		Set<Variable> variables = new HashSet<Variable>();
		Set<LocalVariable> localVariables = new HashSet<LocalVariable>();
		for (Term term : terms) {
			term.collectVariables(variables);
			term.collectLocalVariables(localVariables);
		}
		for (Variable variable : variables) {
			Variable copy = context.getNextVariable(variable.getSort());
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, copy);
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextLocalVariable(variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}
	
	public void collectLocalVariables(Set<LocalVariable> variables) {
		for (Term term : terms) {
			term.collectLocalVariables(variables);
		}
	}
	
	public void collectVariables(Set<Variable> variables) {
		for (Term term : terms) {
			term.collectVariables(variables);
		}
	}
	
	public abstract S substitute(Map<SimpleTerm, SimpleTerm> map);
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Literal) {
			Literal<?, ?> temp = (Literal<?,?>)obj;
			return terms.equals(temp.terms);
		}
		return false;
	}
	
	public boolean equalsWithDifferentVariables(S literal, HashMap<SimpleTerm, SimpleTerm> map) {
		if (terms.size() != literal.terms.size()) return false;
		else {
			for (int i = 0; i < terms.size(); i++) {
				T term1 = terms.get(i);
				Term term2 = literal.terms.get(i);
				if (!term1.equalsWithDifferentVariables(term2, map)) return false;
			}
			return true;
		}
	}
	
	protected static <S extends Term> List<S> substituteHelper(Map<SimpleTerm, SimpleTerm> map, List<S> terms) {
		List<S> result = new ArrayList<S>();
		for (S term : terms) {
			result.add(Term.substituteSimpleTerms(map, term));
		}
		return result;
	}
	
	protected static <U extends Term> List<U> getInverseHelper(List<U> terms) {
		List<U> result = new ArrayList<U>();
		for (U term : terms) {
			result.add(Term.getInverse(term));
		}
		return result;
	}
	
	public abstract S getInverse();
	
	@Override
	public String toString() {
		return toString(new HashMap<Variable, String>());
	}
	
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append("[");
		for (T term : terms) {
			str.append(term.toString(variableMap));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("]");
		return str.toString();
	}
	
}
