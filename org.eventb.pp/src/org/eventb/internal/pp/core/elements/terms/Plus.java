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

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public class Plus extends AssociativeTerm {

	public Plus (List<Term> children) {
		super(children);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Plus) {
			Plus temp = (Plus) obj;
			return super.equals(temp);
		}
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<AbstractVariable, AbstractVariable> map) {
		if (term instanceof Plus) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "+ (" + super.toString() + ")";
	}
	
	@Override
	public int getPriority() {
		return 6;
	}

	
	@Override
	public Term substitute(Map<AbstractVariable, ? extends Term> map) {
		return new Plus(substituteHelper(map));
	}
}
