/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import java.util.Set;

/**
 * Denotes the predefined boolean type which corresponds to the set of boolean values.
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class BooleanType extends Type {

	private static String NAME = "BOOL";
	private static int HASH_CODE = NAME.hashCode();
	
	/**
	 * Creates a new instance of this type.
	 */
	protected BooleanType() {
		super(true);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to do
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		return factory.makeAtomicExpression(Formula.BOOL, null);
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(NAME);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return (o instanceof BooleanType);
	}
	
	@Override
	public int hashCode() {
		return HASH_CODE;
	}

}
