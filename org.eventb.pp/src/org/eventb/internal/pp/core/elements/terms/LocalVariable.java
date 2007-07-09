/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.HashMap;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Sort;

/**
 * The same instance of one variable exists for the same variable in the scope
 * of one predicate. In two different predicates, variable instances are always disjoint.
 * This means {@link #equals(Object)} always return false for variables that are in
 * two different predicates.
 *
 * @author François Terrier
 *
 * IMPORTANT : in 2 different literals of the same clause, existential
 * varibles MUST be different objects ! 
 * To avoid situations like this: 
 * 	Py <=> Qy or #x.x\=y
 * after one point:
 * 	#x.Px <=> #x.Qx
 * if both x are the same variable and there is a match with Px and Qx ...
 * 
 * 
 * INVARIANT :
 * 	- no equal local variables in different literals
 *  + invariant of AbstractVariable : no equal variable in different clauses
 * 
 */
public final class LocalVariable extends SimpleTerm {
	private static final int PRIORITY = 1;
	
	// used only for toString(), does not enter into account for other calculations
	private int index;
	
	
	private boolean isForall;
	
	public LocalVariable(int index, boolean isForall, Sort sort) {
		super(sort, PRIORITY, index+(isForall?1:0), 2+(isForall?1:0));
		
		this.index = index;
		this.isForall = isForall;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		if (map.containsKey(this)) return term.equals(map.get(this));
		else if (term instanceof LocalVariable) {
			if (isForall != ((LocalVariable)term).isForall || !sort.equals(term.sort)) return false;
			if (map.containsValue(term)) return false;
			map.put(this, (LocalVariable)term);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		return "{"+index+(isForall?"∀":"∃")+"}";
	}

	@Override
	public boolean isQuantified() {
		return true;
	}

	@Override
	public boolean isForall() {
		return isForall;
	}
	
	// returns the variable corresponding to this local variable
	private Variable varCache = null;
	public Variable getVariable(IVariableContext context) {
		if (varCache == null) {
			varCache = context.getNextVariable(sort);
		}
		return varCache;
	}
	private LocalVariable inverseCache = null;
	public LocalVariable getInverseVariable() {
		if (inverseCache == null) {
			inverseCache = new LocalVariable(index,!isForall,sort);
		}
		return inverseCache;
	}
	
	public int compareTo(Term o) {
		if (equals(0)) return 0;
		else if (getPriority() == o.getPriority()) return hashCode() - o.hashCode();
		else return getPriority() - o.getPriority();
	}
	
	@Override
	public void collectVariables(Set<Variable> variables) {
		return;
	}

	@Override
	public void collectLocalVariables(Set<LocalVariable> localVariables) {
		localVariables.add(this);
	}

}
