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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UnaryMinus extends AssociativeTerm {

	private static final int PRIORITY = 4;
	
	public UnaryMinus(Term child) {
		super(Arrays.asList(new Term[]{child}), PRIORITY);
	}
	
	public UnaryMinus(List<Term> child) {
		super(child, PRIORITY);
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
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new UnaryMinus(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "-";
	}
}
