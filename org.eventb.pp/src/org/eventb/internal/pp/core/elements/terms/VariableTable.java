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
import java.util.Hashtable;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * Used by the loader to retrieve the instances of the variables,
 * local variables and constants corresponding to the given index or
 * name.
 *
 * @author François Terrier
 *
 */
public class VariableTable {

	protected final Hashtable<Integer, Variable> variableTable = new Hashtable<Integer, Variable>();
	protected final Hashtable<Integer, LocalVariable> localVariableTable = new Hashtable<Integer, LocalVariable>();
	protected final Hashtable<BigInteger, IntegerConstant> integerTable = new Hashtable<BigInteger, IntegerConstant>();
	protected final Hashtable<String, Constant> constantTable = new Hashtable<String, Constant>();
	
	private final VariableContext context;
	
	public VariableTable(VariableContext context){
		this.context = context;
	}
	
	/**
	 * Returns the variable corresponding to the given index
	 * or creates a new one.
	 * 
	 * @param index the index of the variable
	 * @param sort the sort of the variable
	 * @return the variable corresponding to the given index or
	 * a new one if the variable does not exist yet
	 */
	public Variable getVariable(int index, Sort sort) {
		Variable var = variableTable.get(index);
		if (var == null) {
			var = context.getNextVariable(sort);
			variableTable.put(index, var);
		}
		assert var.sort.equals(sort);
		return var;
	}
	
	/**
	 * Returns the local variable corresponding to the given index
	 * or creates a new one.
	 * 
	 * @param index the index of the variable
	 * @param sort the sort of the variable
	 * @param isForall the forall flag
	 * @return the local variable corresponding to the given index or
	 * a new one if the variable does not exist yet
	 */
	public LocalVariable getLocalVariable(int index, boolean isForall, Sort sort) {
		LocalVariable var = localVariableTable.get(index);
		if (var == null) {
			var = context.getNextLocalVariable(isForall, sort);
			localVariableTable.put(index, var);
		}
		assert var.isForall() == isForall;
		assert var.sort.equals(sort);
		return var;
	}
	
	/**
	 * Returns the constant corresponding to the given name or
	 * a new constant if none has been created yet.
	 * 
	 * @param name the name of the constant
	 * @param sort the sort of the constant
	 * @return the constant corresponding to the given name or
	 * a new one if none has been created yet
	 */
	public Constant getConstant(String name, Sort sort) {
		Constant constant = constantTable.get(name);
		if (constant == null) {
			constant = new Constant(name, sort);
			constantTable.put(name, constant);
		}
		assert constant.sort.equals(sort);
		return constant;
	}
	
	/**
	 * Returns the integer constant corresponding to the given name or
	 * a new constant if none has been created yet.
	 * 
	 * @param value the value of the constant
	 * @return the constant corresponding to the given name or
	 * a new one if none has been created yet
	 */
	public IntegerConstant getInteger(BigInteger value) {
		IntegerConstant constant = integerTable.get(value);
		if (constant == null) {
			constant = new IntegerConstant(value);
			integerTable.put(value, constant);
		}
		return constant;
	}
	
}
