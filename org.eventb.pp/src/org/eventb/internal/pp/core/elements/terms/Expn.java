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

import org.eventb.internal.pp.core.elements.Sort;

public class Expn extends BinaryTerm {

	public Expn(Term left, Term right, Sort type) {
		super(left, right);
	}

	public Expn(List<Term> terms) {
		super(terms);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Expn) {
			Expn temp = (Expn) obj;
			return super.equals(temp) && super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		if (term instanceof Expn) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 9;
	}

	
	@Override
	public Term substitute(Map<AbstractVariable, ? extends Term> map) {
		return new Expn(substituteHelper(map));
	}
	
}
