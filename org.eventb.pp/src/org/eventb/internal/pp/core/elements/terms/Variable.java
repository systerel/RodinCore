/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.elements.terms;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * The same instance of one variable exists for the same variable in the scope
 * of one clause. In two different clauses, variable instances are always disjoint.
 * This means {@link #equals(Object)} always return false for variables that are in
 * two different clauses.
 * <p>
 * The index field of a variable is used only for comparing two distinct variables.
 * TODO see if index should not be implemented using {@link BigInteger}. Using an int,
 * the contract of {@link Comparable} stating that (o1.compareTo(o2)==0) == 
 * o1.equals(o2) might be broken when {@link Integer#MAX_VALUE} is reached. This
 * is also valid for {@link LocalVariable} and {@link Constant}. 
 * 
 * @author François Terrier
 *
 */
public final class Variable extends SimpleTerm {

	private static final int PRIORITY = 0;
	private Set<Constant> instantiationValues = new HashSet<Constant>();
	private final int index;
	
	Variable(int index, Sort sort) {
		super(sort, PRIORITY, index, 1);
		
		this.index = index;
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isQuantified() {
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		assert sort != null;
		
		if (map.containsKey(this)) return term.equals(map.get(this));
		else if (term instanceof Variable) {
			if (map.containsValue(term) || !getSort().equals(term.getSort())) return false;
			map.put(this, (Variable)term);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public boolean isForall() {
		return false;
	}

	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return map.containsKey(this)?map.get(this):this;
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		variables.add(this);
	}

	@Override
	public void collectLocalVariables(Set<LocalVariable> localVariables) {
		return;
	}
	
	@Override
	public int compareTo(Term o) {
		if (equals(0)) return 0;
		else if (getPriority() == o.getPriority()) return index - ((Variable)o).index;
		else return getPriority() - o.getPriority();
	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		if (!variableMap.containsKey(this)) {
			variableMap.put(this,"$x"+variableMap.size() /* + "["+numberOfInferences+"]" */);
		}
		return variableMap.get(this);
	}

	public void addInstantiationValue(Constant constant) {
		instantiationValues.add(constant);
	}
	
	public boolean hasInstantiation(Constant constant) {
		return instantiationValues.contains(constant);
	}
	
}
