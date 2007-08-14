/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.clause;

import java.math.BigInteger;
import java.util.Hashtable;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.IntegerConstant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * After the construction, all variables have different indexes;
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
	
	public Variable getVariable(int index, Sort sort) {
		Variable var = variableTable.get(index);
		if (var == null) {
			var = new Variable(context.getAndIncrementGlobalVariableID(), sort);
			variableTable.put(index, var);
		}
		return var;
	}
	
	public LocalVariable getLocalVariable(int index, boolean isForall, Sort sort) {
		LocalVariable var = localVariableTable.get(index);
		if (var == null) {
			var = new LocalVariable(context.getAndIncrementLocalVariableID(),isForall,sort);
			localVariableTable.put(index, var);
		}
		return var;
	}
	
	public Constant getConstant(String name, Sort sort) {
		Constant constant = constantTable.get(name);
		if (constant == null) {
			constant = new Constant(name, sort);
			constantTable.put(name, constant);
		}
		return constant;
	}
	
	public IntegerConstant getInteger(BigInteger value) {
		IntegerConstant constant = integerTable.get(value);
		if (constant == null) {
			constant = new IntegerConstant(value);
			integerTable.put(value, constant);
		}
		return constant;
	}
	
}
