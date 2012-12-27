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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * Abstract base class for associative terms.
 *
 * @author François Terrier
 * 
 */
public abstract class AssociativeTerm extends Term {

	final protected List<Term> children;

	public AssociativeTerm(List<Term> children, int priority) {
		super(Sort.NATURAL, priority, combineHashCodesWithSameVariables(children),
				combineHashCodesWithDifferentVariables(children));

		this.children = children;
	}

	public List<Term> getChildren() {
		return children;
	}

	@Override
	public boolean isConstant() {
		for (Term term : children) {
			if (!term.isConstant())
				return false;
		}
		return true;
	}

	@Override
	public boolean isForall() {
		for (Term term : children) {
			if (term.isForall())
				return true;
		}
		return false;
	}

	protected abstract String getSymbol();

	@Override
	public String toString(HashMap<Variable, String> variable) {
		StringBuffer str = new StringBuffer();
		str.append(getSymbol() + "(");
		for (Term term : children) {
			str.append(term.toString(variable) + " ");
		}
		str.deleteCharAt(str.length() - 1);
		str.append(")");
		return str.toString();
	}

	protected <S extends Term> List<Term> substituteHelper(
			Map<SimpleTerm, S> map) {
		List<Term> result = new ArrayList<Term>();
		for (Term child : children) {
			result.add(child.substitute(map));
		}
		return result;
	}

	@Override
	public boolean isQuantified() {
		for (Term child : children) {
			if (child.isQuantified())
				return true;
		}
		return false;
	}

	@Override
	public void collectLocalVariables(Set<LocalVariable> existential) {
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
	public boolean contains(SimpleTerm variables) {
		for (Term child : children) {
			if (child.contains(variables))
				return true;
		}
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term,
			HashMap<SimpleTerm, SimpleTerm> map) {
		if (term instanceof AssociativeTerm) {
			AssociativeTerm temp = (AssociativeTerm) term;
			if (temp.children.size() != children.size())
				return false;
			for (int i = 0; i < children.size(); i++) {
				Term term1 = children.get(i);
				Term term2 = temp.children.get(i);
				if (!term1.equalsWithDifferentVariables(term2, map))
					return false;
			}
			return true;
		}
		return false;
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
	public int compareTo(Term o) {
		if (equals(o))
			return 0;
		else if (getPriority() == o.getPriority()) {
			AssociativeTerm term = (AssociativeTerm) o;
			if (getPriority() == term.getPriority()) {
				if (children.size() == term.children.size()) {
					for (int i = 0; i < children.size(); i++) {
						int c = children.get(i).compareTo(term.children.get(i));
						if (c != 0)
							return c;
					}
				} else
					return children.size() - term.children.size();
			} else
				return getPriority() - term.getPriority();
		} else
			return getPriority() - o.getPriority();

		assert false;
		return 1;
	}

}
