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

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.Sort;

/**
 * The same instance of one variable exists for the same variable in the scope
 * of one literal. In two different literal, variable instances are always disjoint.
 * This means that {@link #equals(Object)} always return false for variables that are in
 * two different predicates.
 * <p>
 * As for variables, the index is only used for {@link #compareTo(Term)}.
 * 
 * TODO move the isForall boolean to {@link Literal}, since a universal or 
 * existential quantifier spans over a whole literal, all terms in this 
 * literal have the same quantification.
 * 
 * @author François Terrier
 * 
 */
public final class LocalVariable extends SimpleTerm {
	
	private static final int PRIORITY = 1;
	
	private int index;

	private boolean isForall;
	
	LocalVariable(int index, boolean isForall, Sort sort) {
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
	public Variable getVariable(VariableContext context) {
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
	
	@Override
	public int compareTo(Term o) {
		if (equals(0)) return 0;
		else if (getPriority() == o.getPriority()) {
			LocalVariable tmp = (LocalVariable)o;
			if (tmp.isForall==isForall) return index-tmp.index;
			else if (isForall) return 1;
			else return -1;
		}
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
