/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;

public final class VariableContext implements IVariableContext {

	private int currentLocalVariableID = 0;
	private int currentGlobalVariableID = 0;
	private int currentConstantID = 0;
	
	public VariableContext() {
		// do nothing
	}
	
	int getAndIncrementGlobalVariableID() {
		return currentGlobalVariableID++;
	}
	
	int getAndIncrementLocalVariableID() {
		return currentLocalVariableID++;
	}
	
	private Hashtable<Sort,List<Variable>> variableCache = new Hashtable<Sort, List<Variable>>();
	
	public void putInCache(List<Variable> variables) {
		for (Variable variable : variables) {
			putInCache(variable);
		}
	}
	
	public void putInCache(Variable variable) {
		if (!variableCache.containsKey(variable.getSort())) {
			variableCache.put(variable.getSort(), new ArrayList<Variable>());
		}
		variableCache.get(variable.getSort()).add(variable);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.elements.IVariableContext#getNextVariable(org.eventb.internal.pp.core.elements.Sort)
	 */
	public Variable getNextVariable(Sort sort) {
		assert sort != null;
		
		if (!variableCache.containsKey(sort)) return newVariable(sort);
		List<Variable> variables = variableCache.get(sort);
		if (variables.isEmpty()) return newVariable(sort);
		return variables.remove(0);
	}
	
	private Variable newVariable(Sort sort) {
		assert sort != null;
		
		return new Variable(currentGlobalVariableID++,sort);
	}

	public LocalVariable getNextLocalVariable(boolean isForall, Sort sort) {
		assert sort != null;
		
		return new LocalVariable(currentLocalVariableID++, isForall, sort);
	}

	public Constant getNextFreshConstant(Sort sort) {
		assert sort != null;
		
		return new Constant(Integer.toString(currentConstantID++),sort);
	}
	
}
