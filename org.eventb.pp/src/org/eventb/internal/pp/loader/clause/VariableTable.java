package org.eventb.internal.pp.loader.clause;

import java.util.Hashtable;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * After the construction, all variables have different indexes;
 *
 * @author François Terrier
 *
 */
public class VariableTable {

	private Hashtable<Integer, Variable> variableTable;
	private Hashtable<Integer, LocalVariable> localVariableTable;
	private IVariableContext context;
	
	public VariableTable(IVariableContext context){
		this.context = context;
		reset();
	}
	
	public void reset() {
		variableTable = new Hashtable<Integer, Variable>();
		localVariableTable = new Hashtable<Integer, LocalVariable>();
	}
	
	public Variable getVariable(int index, Sort sort) {
		Variable var = variableTable.get(index);
		if (var == null) {
			var = context.getNextVariable(sort);
			variableTable.put(index, var);
		}
		return var;
	}
	
	public LocalVariable getLocalVariable(int index, boolean isForall, Sort sort) {
		LocalVariable var = localVariableTable.get(index);
		if (var == null) {
			var = new LocalVariable(context.getNextLocalVariableID(),isForall,sort);
			localVariableTable.put(index, var);
		}
		return var;
	}
	
//	public void pushTable() {
////		variableTable.push(new Hashtable<Integer, Variable>());
//		localVariableTable.push(new Hashtable<Integer, LocalVariable>());
//	}
//	
//	public void popTable() {
////		variableTable.pop();
//		localVariableTable.pop();
//	}
}
