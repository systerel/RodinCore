/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;


public abstract class Term implements Comparable<Term> {

	final protected Sort sort;
	// position of the term inside a literal
	
	protected Term(Sort sort) {
		this.sort = sort;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof Term) {
//			Term temp = (Term) obj;
//			return true;
//		}
//		return false;
//	}

	public Sort getSort() {
		return sort;
	}

	// true if this term is constant or quantified
	public abstract boolean isConstant();
	
	// true if this term is quantified
	public abstract boolean isQuantified();

	// true if this term is quantified and is a forall
	public abstract boolean isForall();
	
	public abstract Term substitute(Map<AbstractVariable, ? extends Term> map);
	
	public abstract boolean contains(AbstractVariable variables);
	
	public abstract void collectLocalVariables(List<LocalVariable> existential);
	
	public abstract void collectVariables(Set<Variable> variables);
	
	public Term getInverse() {
		List<LocalVariable> variables = new ArrayList<LocalVariable>();
		collectLocalVariables(variables);
		Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		for (LocalVariable variable : variables) {
			map.put(variable, variable.getInverseVariable());
		}
		return substitute(map);
	}
	
	public abstract String toString(HashMap<Variable, String> variableMap);
	
	public abstract int getPriority();
	
	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}
	
	public abstract boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map);

	public abstract int hashCodeWithDifferentVariables();
	
	public boolean isBlocked() {
		return numberOfInferences == 0;
	}
	public void resetInstantiationCount() {
		this.numberOfInferences = Term.MAX_NUMBER_OF_INFERENCES;
	}
	private static final int MAX_NUMBER_OF_INFERENCES = 1;
	protected int numberOfInferences = Term.MAX_NUMBER_OF_INFERENCES;
	public void incrementInstantiationCount() {
		this.numberOfInferences--;
	}
	
}