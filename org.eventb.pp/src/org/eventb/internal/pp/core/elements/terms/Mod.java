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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Mod extends BinaryTerm {

	private static final int PRIORITY = 9;
	
	
	public Mod(Term left, Term right) {
		super(left, right, PRIORITY);
	}

	public Mod(List<Term> terms) {
		super(terms, PRIORITY);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Mod) {
			Mod temp = (Mod) obj;
			return super.equals(temp) && super.equals(temp);
		}
		return false;
	}

	@Override
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
		if (term instanceof Mod) {
			return super.equalsWithDifferentVariables(term, map);
		}
		return false;
	}
	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new Mod(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "mod";
	}
}
