/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;

public abstract class AbstractPPPredicate extends AbstractPPLiteral<IPredicate> implements IPredicate {

	final protected int index;
	final protected boolean isPositive;
	
	public AbstractPPPredicate(int index, boolean isPositive, List<Term> terms) {
		super(terms);
		
		this.index = index;
		this.isPositive = isPositive;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPPPredicate) {
			AbstractPPPredicate temp = (AbstractPPPredicate) obj;
			return index == temp.index && isPositive == temp.isPositive && super.equals(obj);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof AbstractPPPredicate) {
			AbstractPPPredicate temp = (AbstractPPPredicate)literal;
			return index == temp.index && isPositive == temp.isPositive && super.equalsWithDifferentVariables(literal, map);
		}
		return false;
	}
	
	@Override
	public int hashCodeWithDifferentVariables() {
		return super.hashCodeWithDifferentVariables() + index * 31 + (isPositive?0:1);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() + index * 31 + (isPositive?0:1);
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public void setBit(BitSet set) {
		set.set(index);
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



}
