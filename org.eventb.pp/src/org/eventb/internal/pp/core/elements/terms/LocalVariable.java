/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Sort;

/**
 * TODO comment
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
public class LocalVariable extends AbstractVariable {

	private int index;
	private boolean isForall;
	
	public LocalVariable(int index, boolean isForall, Sort sort) {
		super(sort);
		
		this.index = index;
		this.isForall = isForall;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LocalVariable) {
			LocalVariable temp = (LocalVariable) obj;
			return index == temp.index && isForall == temp.isForall;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return index;
	}
	
	@Override
	public boolean isConstant() {
		//TODO think about this
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

	public boolean isForall() {
		return isForall;
	}
	
	@Override
	public void collectLocalVariables(List<LocalVariable> existential) {
		if (!existential.contains(this)) existential.add(this);
	}
	
	
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

	@Override
	public void collectVariables(Set<Variable> variables) {
		
	}
	
//	// FOR TESTING ONLY
//	public void clean() {
//		varCache = null;
//		inverseCache = null;
//	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		if (map.containsKey(this)) return term.equals(map.get(this));
		else if (term instanceof LocalVariable) {
			if (isForall != ((LocalVariable)term).isForall || !sort.equals(term.sort)) return false;
			if (map.containsValue(term)) return false;
			map.put(this, (LocalVariable)term);
			return true;
		}
		return false;
	}

	

}
