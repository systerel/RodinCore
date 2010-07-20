/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;

/**
 * Denotes the predefined integer type which corresponds to the set of all integers.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class IntegerType extends Type {

	private static String NAME = "\u2124";
	private static int HASH_CODE = NAME.hashCode();
	
	/**
	 * Creates a new instance of this type.
	 */
	protected IntegerType() {
		super(true);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to do
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		return factory.makeAtomicExpression(Formula.INTEGER, null);
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(NAME);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return (o instanceof IntegerType);
	}
	
	@Override
	public int hashCode() {
		return HASH_CODE;
	}
}
