/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula.terms;

import java.math.BigInteger;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.ClauseContext;

public class IntegerSignature extends AbstractConstantSignature {

	private final BigInteger literal;
	
	public IntegerSignature(BigInteger literal) {
		super(Sort.NATURAL);
		this.literal = literal;
	}

	@Override
	public Term getTerm(ClauseContext context) {
		return context.getVariableTable().getInteger(literal);
	}

	@Override
	protected TermSignature deepCopy() {
		return new IntegerSignature(literal);
	}

	@Override
	public String toString() {
		return literal.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntegerSignature) {
			IntegerSignature temp = (IntegerSignature) obj;
			return literal.equals(temp.literal);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return literal.hashCode();
	}

	
}
