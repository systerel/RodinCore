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
package org.eventb.internal.core.typecheck;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

/**
 * Denotes a type variable used internally by the type-checker.
 * 
 * @author Laurent Voisin
 */
public class TypeVariable extends Type {
	
	// Index of this type variable
	private final int index;
	
	// Location for which this type variable was created
	private final SourceLocation location;

	// Value associated to this type variable
	private Type value;
	
	/**
	 * Creates a new instance of this type.
	 */
	protected TypeVariable(int index, SourceLocation location) {
		super(false);
		assert 0 <= index;
		this.index = index;
		this.location = location;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		assert false;
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		assert false;
		return null;
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append("'" + index);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (! (o instanceof TypeVariable)) return false;
		TypeVariable other = (TypeVariable) o;
		return index == other.index;
	}

	public SourceLocation getSourceLocation() {
		return location;
	}
	
	/**
	 * @return Returns the value.
	 */
	public Type getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return index;
	}

	public boolean hasSourceLocation() {
		return location != null;
	}
	
	/**
	 * @param value The value to set.
	 */
	public void setValue(Type value) {
		this.value = value;
	}

	@Override
	public void accept(ITypeVisitor visitor) {
		assert false : "TypeVariable.visit()";
	}

}
