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

public class Minus extends BinaryTerm {

	public Minus(Term left, Term right) {
		super(left, right);
	}
	
	public Minus(List<Term> terms) {
		super(terms);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Minus) {
			Minus temp = (Minus) obj;
			return super.equals(temp) && super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		if (term instanceof Minus) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 7;
	}

	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new Minus(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "-";
	}
}
