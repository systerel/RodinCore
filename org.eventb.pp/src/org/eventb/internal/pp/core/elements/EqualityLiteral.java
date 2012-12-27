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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * Concrete implementation of {@link Literal} for equality literals.
 * <p>
 * Terms in an equality literal are ordered.
 *
 * @author François Terrier
 *
 */
public final class EqualityLiteral extends Literal<EqualityLiteral,SimpleTerm> {

	private static final int BASE_HASHCODE = 37;
	
	private final boolean isPositive;
	
	public EqualityLiteral (SimpleTerm term1, SimpleTerm term2, boolean isPositive) {
//		super(Arrays.asList(new Term[]{term1,term2}));
		super(Arrays.asList(term1.compareTo(term2)<0?new SimpleTerm[]{term1,term2}:new SimpleTerm[]{term2,term1}), BASE_HASHCODE+(isPositive?0:1));
		// TODO term must be ordered
		
		assert term1.getSort().equals(term2.getSort()):"incompatible terms: "+term1+", "+term2;
		
		this.isPositive = isPositive;
	}
	
	private EqualityLiteral(List<SimpleTerm> terms, boolean isPositive) {
		super(Arrays.asList(terms.get(0).compareTo(terms.get(1))<0?new SimpleTerm[]{terms.get(0),terms.get(1)}:new SimpleTerm[]{terms.get(1),terms.get(0)}), BASE_HASHCODE+(isPositive?0:1));
		
		this.isPositive = isPositive;
	}
	
	public SimpleTerm getTerm1() {
		return terms.get(0);
	}
	
	public SimpleTerm getTerm2() {
		return terms.get(1);
	}
	
	public Sort getSort() {
		return terms.get(0).getSort();
	}

	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(getTerm1().toString(variableMap));
		str.append(isPositive?"=":"≠");
		str.append(getTerm2().toString(variableMap));
		return str.toString();
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	@Override
	public EqualityLiteral getInverse() {
		return new EqualityLiteral(getInverseHelper(terms),!isPositive);
	}

	@Override
	public EqualityLiteral substitute(Map<SimpleTerm, SimpleTerm> map) {
		return new EqualityLiteral(substituteHelper(map,terms),isPositive);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EqualityLiteral) {
			EqualityLiteral temp = (EqualityLiteral) obj;
			return isPositive == temp.isPositive && super.equals(temp);
		}
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(EqualityLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		return (isPositive == literal.isPositive) && super.equalsWithDifferentVariables(literal, map);
	}
	
}
