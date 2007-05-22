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


public class Divide extends BinaryTerm {

	public Divide(Term left, Term right) {
		super(left, right);
	}
	
	public Divide(List<Term> terms) {
		super(terms);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Divide) {
			Divide temp = (Divide) obj;
			return super.equals(temp) && super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		if (term instanceof Divide) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 5;
	}

	
	@Override
	public Term substitute(Map<AbstractVariable, ? extends Term> map) {
		return new Divide(substituteHelper(map));
	}

}
