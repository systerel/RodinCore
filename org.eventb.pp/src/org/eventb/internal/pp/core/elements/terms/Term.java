/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

public abstract class Term implements Comparable<Term> {

	final protected Sort sort;
	// position of the term inside a literal
	
	protected Term(Sort sort) {
		this.sort = sort;
	}
	
	public Sort getSort() {
		return sort;
	}

	// true if this term is constant or quantified
	public abstract boolean isConstant();
	
	// true if this term is quantified
	public abstract boolean isQuantified();

	// true if this term is quantified and is a forall
	public abstract boolean isForall();
	
	@SuppressWarnings("unchecked")
	public static <T extends Term> T getInverse(T term) {
		Set<LocalVariable> variables = new HashSet<LocalVariable>();
		term.collectLocalVariables(variables);
		Map<SimpleTerm, Term> map = new HashMap<SimpleTerm, Term>();
		for (LocalVariable variable : variables) {
			map.put(variable, variable.getInverseVariable());
		}
		return (T)term.substitute(map);
	}
	
	/**
	 * Substitutes variables and local variables in the term according
	 * to the specified map. Does not work for constants.
	 * 
	 * @param <T>
	 * @param map
	 * @param term
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Term> T substituteSimpleTerms(Map<SimpleTerm, SimpleTerm> map, T term) {
		return (T)term.substitute(map);
	}
	
	public abstract String toString(HashMap<Variable, String> variableMap);
	
	protected abstract int getPriority();
	
	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}
	
	public abstract boolean contains(SimpleTerm variable);
	
	public abstract void collectVariables(Set<Variable> variables);
	
	public abstract void collectLocalVariables(Set<LocalVariable> localVariables);
	
	protected abstract <S extends Term> Term substitute(Map<SimpleTerm, S> map);
	
	public abstract boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map);

	public abstract int hashCodeWithDifferentVariables();
	
	public boolean isBlocked() {
		if (numberOfInferences == 0) {
			numberOfInferences = Term.MAX_NUMBER_OF_INFERENCES;
			return true;
		}
		return false;
	}
	private static final int MAX_NUMBER_OF_INFERENCES = 3;
	protected int numberOfInferences = Term.MAX_NUMBER_OF_INFERENCES;
	public void incrementInstantiationCount() {
		this.numberOfInferences--;
	}
	
}