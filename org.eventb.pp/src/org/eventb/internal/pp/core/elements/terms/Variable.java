/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * 
 * @author François Terrier
 *
 */
public final class Variable extends SimpleTerm {
//	protected int index;

	public Variable(/* int index, */Sort sort) {
		super(sort);
		
//		this.index = index;
	}
	
	// in a same clause, a same variable is represented by the same object
	// in 2 different clauses, variables are different objects
	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof Variable) {
//			Variable temp = (Variable) obj;
//			return index == temp.index && super.equals(temp);
//		}
//		return false;
//	}

	@Override
	public boolean isConstant() {
		return false;
	}

	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		if (!variableMap.containsKey(this)) {
			variableMap.put(this,"x"+variableMap.size() + "["+numberOfInferences+"]" );
		}
		return variableMap.get(this);
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
	public boolean isForall() {
		return false;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}


	public int compareTo(Term o) {
		if (equals(o)) return 0;
		else if (getPriority() == o.getPriority()) return hashCode()-o.hashCode();
		else return getPriority() - o.getPriority();
	}

	@Override
	public final int hashCodeWithDifferentVariables() {
		return 1;
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

}
