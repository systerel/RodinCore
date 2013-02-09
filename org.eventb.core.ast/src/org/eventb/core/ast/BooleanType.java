/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add type visitor
 *     Systerel - store factory used to build a type
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;

/**
 * Denotes the predefined boolean type which corresponds to the set of boolean values.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BooleanType extends Type {

	private static String NAME = "BOOL";
	private static int HASH_CODE = NAME.hashCode();
	
	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeBooleanType()
	 * @since 3.0
	 */
	protected BooleanType(FormulaFactory ff) {
		super(ff, true);
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

	@Override
	public void accept(ITypeVisitor visitor) {
		visitor.visit(this);
	}

}
