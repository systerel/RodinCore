/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class PPProposition implements IPredicate {

	protected int index;
	protected boolean isPositive;
	
	public PPProposition(int index, boolean isPositive) {
		this.index = index;
		this.isPositive = isPositive;
	}

	public List<Term> getTerms() {
		return new ArrayList<Term>();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof PPProposition) {
			PPProposition temp = (PPProposition) obj;
			return index == temp.index && isPositive == temp.isPositive;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return index * 31 + (isPositive?0:1);
	}
	
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(isPositive?"":"¬");
		str.append("R" + index);
		return str.toString();
	}

	public ILiteralDescriptor getDescriptor() {
		return new PropositionDescriptor(index);
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public void setBit(BitSet set) {
		set.set(index);
	}

	public List<IEquality> getConditions(IPredicate predicate) {
		return new ArrayList<IEquality>();
	}

	public int getIndex() {
		return index;
	}

//	public boolean contains(IPredicate predicate) {
//		return getIndex() == predicate.getIndex() && isPositive()==predicate.isPositive();
//	}

//	public boolean matches(IPredicate predicate) {
//		return getIndex() == predicate.getIndex() && isPositive()!=predicate.isPositive();
//	}

	public IPredicate getInverse() {
		return new PPProposition(index, !isPositive);
	}

	public IPredicate substitute(Map<AbstractVariable, ? extends Term> map) {
		return this;
	}

	public boolean isQuantified() {
		return false;
	}

	public boolean isConstant() {
		return true;
	}

	public IPredicate getCopyWithNewVariables(IVariableContext context, HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		return substitute(new HashMap<AbstractVariable, Term>());
	}

	public boolean equalsWithDifferentVariables(IPredicate literal, HashMap<AbstractVariable, AbstractVariable> map) {
		return equals(literal);
	}

	public boolean updateInstantiationCount(IPredicate predicate) {
		// do nothing
		return false;
	}

	public void resetInstantiationCount() {
		//do nothing
	}

	public int hashCodeWithDifferentVariables() {
		return index * 31 + (isPositive?0:1);
	}
}
