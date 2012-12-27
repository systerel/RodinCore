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
package org.eventb.internal.pp.core.elements.terms;

import java.math.BigInteger;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * All assumptions of {@link Constant} are valid for this class. IntegerConstant
 * represents the numeric constant appearing in a proof. IntegerConstant have sort
 * {@link Sort#NATURAL}.
 *
 * @author François Terrier
 * 
 */
public final class IntegerConstant extends Constant {
	
	private static final int PRIORITY = 3;
	
	private BigInteger value;
	
	IntegerConstant(BigInteger value) {
		super(value.toString(), PRIORITY, Sort.NATURAL);
		
		this.value = value;
	}
	
	public BigInteger getValue() {
		return value;
	}
	
	@Override
	public int compareTo(Term o) {
		if (equals(o)) return 0;
		else if (o instanceof IntegerConstant) return value.compareTo(((IntegerConstant)o).value);
		else return getPriority() - o.getPriority();
	}

}
