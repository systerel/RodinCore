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

public class Times extends AssociativeTerm {

	public Times (List<Term> children) {
		super(children);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Times) {
			Times temp = (Times) obj;
			return super.equals(temp) && super.equals(temp);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		if (term instanceof Times) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	public int getPriority() {
		return 4;
	}

	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new Times(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "*";
	}
}
