package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class VariableContext implements IVariableContext {

	private int currentVariableID = 0;
	
	public VariableContext(){
	}
	
//	public void setStartID(int startID) {
//		this.currentVariableID = startID;
//	}
	
//	public int getNextVariableID() {
//		return currentVariableID++;
//	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.elements.IVariableContext#getNextLocalVariable(boolean, org.eventb.internal.pp.core.elements.Sort)
	 */
	public int getNextLocalVariableID() {
		return currentVariableID++;
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
		if (!variableCache.containsKey(sort)) return newVariable(sort);
		List<Variable> variables = variableCache.get(sort);
		if (variables.isEmpty()) return newVariable(sort);
		return variables.remove(0);
	}
	
	private Variable newVariable(Sort sort) {
		return new Variable(sort);
	}
	
}
