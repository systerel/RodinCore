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
package org.eventb.internal.pp.core.elements;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;

/**
 * Abstract implementation of {@link Literal} for predicates.
 * <p>
 * There are two types of predicate literals, propositions {@link AtomicPredicateLiteral} 
 * and predicates {@link ComplexPredicateLiteral}.
 *
 * @author François Terrier
 *
 */
public abstract class PredicateLiteral extends Literal<PredicateLiteral,SimpleTerm> {

	final protected PredicateLiteralDescriptor descriptor;
	final protected boolean isPositive;
	
	// Checks that the arguments are compatible with the predicate descriptor
	private static boolean checkArgs(PredicateLiteralDescriptor descriptor,
			List<SimpleTerm> terms) {

		final List<Sort> sorts = descriptor.getSortList();
		final int arity = sorts.size();
		if (arity != terms.size()) {
			return false;
		}
		for (int i = 0; i < arity; ++ i) {
			final SimpleTerm term = terms.get(i);
			if (!sorts.get(i).equals(term.getSort())) {
				return false;
			}
		}
		return true;
	}
	
	public PredicateLiteral(PredicateLiteralDescriptor descriptor, boolean isPositive, List<SimpleTerm> terms) {
		super(terms, 37*descriptor.hashCode()+(isPositive?0:1));
		assert checkArgs(descriptor, terms);
		this.descriptor = descriptor;
		this.isPositive = isPositive;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateLiteral) {
			PredicateLiteral temp = (PredicateLiteral) obj;
			return descriptor.equals(temp.descriptor) && isPositive == temp.isPositive && super.equals(obj);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(PredicateLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		return descriptor.equals(literal.descriptor) && isPositive == literal.isPositive && super.equalsWithDifferentVariables(literal, map);
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public void setBit(BitSet set) {
		set.set(descriptor.getIndex());
	}

	public PredicateLiteralDescriptor getDescriptor() {
		return descriptor;
	}
	
}