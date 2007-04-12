/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

public abstract class AssociativeTerm extends Term {

	protected List<Term> children;
	
	public AssociativeTerm(List<Term> children) {
		super(Sort.ARITHMETIC);
		
		this.children = children;
	}
	
	public List<Term> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssociativeTerm) {
			AssociativeTerm temp = (AssociativeTerm) obj;
			return children.equals(temp.children);
		}
		return false;
	}


	@Override
	public boolean isConstant() {
		for (Term term : children) {
			if (!term.isConstant()) return false;
		}
		return true;
	}

	@Override
	public String toString(HashMap<Variable, String> variable) {
		StringBuffer str = new StringBuffer();
		for (Term term : children) {
			str.append(term.toString(variable)+" ");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString();
	}
	
	protected List<Term> substituteHelper(Map<AbstractVariable, ? extends Term> map) {
		List<Term> result = new ArrayList<Term>();
		for (Term child : children) {
			result.add(child.substitute(map));
		}
		return result;
	}
	
	
	@Override
	public boolean isQuantified() {
		for (Term child : children) {
			if (child.isQuantified()) return true;
		}
		return false;
	}

	@Override
	public void collectLocalVariables(List<LocalVariable> existential) {
		for (Term child : children) {
			child.collectLocalVariables(existential);
		}
	}
	
	@Override
	public void collectVariables(Set<Variable> variables) {
		for (Term child : children) {
			child.collectVariables(variables);
		}
	}

	@Override
	public boolean contains(AbstractVariable variables) {
		for (Term child : children) {
			if (child.contains(variables)) return true;
		}
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		if (term instanceof AssociativeTerm) {
			AssociativeTerm temp = (AssociativeTerm)term;
			if (temp.children.size() != children.size()) return false;
			for (int i = 0; i < children.size(); i++) {
				Term term1 = children.get(i);
				Term term2 = temp.children.get(i);
				if (!term1.equalsWithDifferentVariables(term2, map)) return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCodeWithDifferentVariables() {
		int hashCode = 1;
		for (Term term : children) {
			hashCode = hashCode * 31 + term.hashCodeWithDifferentVariables();
		}
		return hashCode;
	}


}
