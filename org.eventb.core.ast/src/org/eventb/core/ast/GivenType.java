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

import static org.eventb.internal.core.ast.FormulaChecks.ensureValidIdentifierName;

import java.util.Set;

/**
 * Denotes a type defined by the user. For instance, this type can correspond to
 * a carrier-set declared in a context.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class GivenType extends Type {
	
	// Name of the carrier-set corresponding to this type.
	private String name;

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeGivenType(String)
	 * @since 3.0
	 */
	protected GivenType(FormulaFactory ff, String name) {
		super(ff, true);
		ensureValidIdentifierName(name, ff);
		this.name = name;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		set.add(this);
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		return factory.makeFreeIdentifier(
				name, 
				null, 
				factory.makePowerSetType(this));
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (! (o instanceof GivenType)) return false;
		GivenType other = (GivenType) o;
		return name.equals(other.name);
	}
	
	/**
	 * Returns the name of the carrier-set corresponding to this type.
	 * 
	 * @return Returns the name of this type
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Returns the free identifier that denotes the set corresponding to this
	 * given type.
	 * 
	 * @return the set corresponding to this type
	 * @since 3.0
	 */
	@Override
	public FreeIdentifier toExpression() {
		return (FreeIdentifier) super.toExpression();
	}

	/**
	 * Returns the free identifier that denotes the set corresponding to this
	 * given type.
	 * 
	 * @param factory
	 *            factory to use for building the result identifier
	 * @return the set corresponding to this type
	 * @since 2.6
	 */
	@Deprecated
	@Override
	public FreeIdentifier toExpression(FormulaFactory factory) {
		return (FreeIdentifier) super.toExpression(factory);
	}

	@Override
	public void accept(ITypeVisitor visitor) {
		visitor.visit(this);
	}

}
