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

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public final class Plus extends AssociativeTerm {

	private static final int PRIORITY = 7;
	
	
	public Plus (List<Term> children) {
		super(children, PRIORITY);
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
	public boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map) {
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
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return new Plus(substituteHelper(map));
	}

	@Override
	protected String getSymbol() {
		return "+";
	}
}
