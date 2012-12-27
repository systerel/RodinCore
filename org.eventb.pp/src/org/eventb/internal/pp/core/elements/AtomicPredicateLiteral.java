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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * Implementation of {@link PredicateLiteral} for propositions.
 * <p>
 * Propositions are predicates with no terms.
 *
 * @author François Terrier
 *
 */
public final class AtomicPredicateLiteral extends PredicateLiteral {

	public AtomicPredicateLiteral(PredicateLiteralDescriptor descriptor, boolean isPositive) {
		super(descriptor, isPositive, new ArrayList<SimpleTerm>());
	}

	@Override
	public AtomicPredicateLiteral getInverse() {
		return new AtomicPredicateLiteral(descriptor, !isPositive());
	}

	@Override
	public AtomicPredicateLiteral substitute(Map<SimpleTerm, SimpleTerm> map) {
		return this;
	}
	
	@Override
	public AtomicPredicateLiteral getCopyWithNewVariables(VariableContext context, HashMap<SimpleTerm, SimpleTerm> substitutionsMap) {
		return this;
	}

	@Override
	public boolean equalsWithDifferentVariables(PredicateLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		return literal instanceof AtomicPredicateLiteral && super.equalsWithDifferentVariables(literal, map);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AtomicPredicateLiteral) {
			AtomicPredicateLiteral temp = (AtomicPredicateLiteral) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(isPositive()?"":"¬");
		str.append("R" + descriptor.getIndex());
		return str.toString();
	}
	
}
