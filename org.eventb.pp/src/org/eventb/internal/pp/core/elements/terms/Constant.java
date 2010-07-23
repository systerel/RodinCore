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

import org.eventb.internal.pp.core.elements.Sort;

/**
 * In all clauses derived during a proof, a particular constant is denoted
 * by the same instance of this class. New constants can be created using
 * {@link VariableContext#getNextFreshConstant(Sort)}.
 * <p>
 * As for the index of a variable, the name of a constant is only used for
 * the {@link #compareTo(Term)} method.
 *
 * @author François Terrier
 *
 */
public class Constant extends SimpleTerm {

	private static final int PRIORITY = 2;
	
	private String name;
	
	Constant(String name, Sort type) {
		super(type, PRIORITY, name.hashCode(), name.hashCode());
		
		this.name = name;
	}
	
	protected Constant(String name, int priority, Sort type) {
		super(type, priority, name.hashCode(), name.hashCode());
		
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		return equals(term);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		return name;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isQuantified() {
		return false;
	}

	@Override
	public boolean isForall() {
		return false;
	}

	@Override
	public int compareTo(Term o) {
		if (equals(o)) return 0;
		else if (o.getClass().equals(Constant.class)) return name.compareTo(((Constant)o).name);
		else return getPriority() - o.getPriority();
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		return;
	}

	@Override
	public void collectLocalVariables(Set<LocalVariable> localVariables) {
		return;
	}

}
