/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add type visitor
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;

/**
 * Denotes a power-set type.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PowerSetType extends Type {
	
	// Name of the carrier-set corresponding to this type.
	private Type base;

	/**
	 * Creates a new instance of this type.
	 */
	protected PowerSetType(Type base) {
		super(base.isSolved());
		this.base = base;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		base.addGivenTypes(set);
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		Expression baseExpr = base.toExpression(factory);
		return factory.makeUnaryExpression(Formula.POW, baseExpr, null);
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append("\u2119(");
		base.buildString(buffer);
		buffer.append(')');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (! (o instanceof PowerSetType)) return false;
		PowerSetType other = (PowerSetType) o;
		return base.equals(other.base);
	}
	
	/**
	 * Returns the base type of this type.
	 * 
	 * @return Returns the base type of this type
	 */
	@Override
	public Type getBaseType() {
		return base;
	}

	@Override
	public int hashCode() {
		return base.hashCode() << 1;
	}

	@Override
	public void accept(ITypeVisitor visitor) {
		visitor.visit(this);
	}
	
}
