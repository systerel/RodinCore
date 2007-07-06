/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnaryMinus extends AssociativeTerm {

	public UnaryMinus(Term child) {
		super(Arrays.asList(new Term[]{child}));
	}
	
	public UnaryMinus(List<Term> child) {
		super(child);
		assert child.size() == 1;
	}
	
	
	public Term getChild() {
		return children.get(0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnaryMinus) {
			UnaryMinus temp = (UnaryMinus) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		if (term instanceof UnaryMinus) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public boolean isConstant() {
		if (!getChild().isConstant()) return false;
		return true;
	}
	
	@Override
	public int getPriority() {
		return 3;
	}

	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new UnaryMinus(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "-";
	}
}
