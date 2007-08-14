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

public class Constant extends SimpleTerm {

	private static final int PRIORITY = 2;
	
	private String name;
	
	public Constant(String name, Sort type) {
		super(type, PRIORITY, name.hashCode(), name.hashCode());
		
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

	public int compareTo(Term o) {
		if (equals(o)) return 0;
		else if (o instanceof Constant) return name.compareTo(((Constant)o).name);
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
