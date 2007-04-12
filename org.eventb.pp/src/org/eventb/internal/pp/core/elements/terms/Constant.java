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
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

public class Constant extends Term {

	// TODO change this
	public static int uniqueIdentifier = 0;
	
	private String name;
	
	public Constant(String name, Sort type) {
		super(type);
		
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	// TODO eventually same object ! -> not important for now
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Constant) {
			Constant temp = (Constant) obj;
			return name.equals(temp.name);
		}
		return false;
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
	public Term substitute(Map<AbstractVariable, ? extends Term> map) {
		return this;
	}

	@Override
	public boolean isQuantified() {
		return false;
	}

	@Override
	public void collectLocalVariables(List<LocalVariable> existential) {
		return;
	}

	@Override
	public boolean contains(AbstractVariable variables) {
		return false;
	}

	@Override
	public void collectVariables(Set<Variable> variables) {
		
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		return equals(term);
	}

	@Override
	public int hashCodeWithDifferentVariables() {
		return hashCode();
	}

	
}
