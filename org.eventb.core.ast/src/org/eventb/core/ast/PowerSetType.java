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
 * Denotes a power-set type.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PowerSetType extends Type {
	
	// Name of the carrier-set corresponding to this type.
	private final Type base;

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makePowerSetType(Type)
	 * @see FormulaFactory#makeRelationalType(Type, Type)
	 * @since 3.0
	 */
	protected PowerSetType(FormulaFactory ff, Type base) {
		super(ff, base.isSolved());
		this.base = base;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		base.addGivenTypes(set);
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		Expression baseExpr = base.buildExpression(factory);
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
