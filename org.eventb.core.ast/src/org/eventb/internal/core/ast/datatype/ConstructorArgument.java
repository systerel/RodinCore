/*******************************************************************************
 * Copyright (c) 2013, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.datatype.ITypeInstantiation;

/**
 * Implements unnamed arguments of constructors.
 * <p>
 * This class must <strong>not</strong> override <code>equals</code> as this
 * would wreak havoc in formula factories. We rely on object identity for
 * identifying identical arguments.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class ConstructorArgument implements IConstructorArgument {

	protected final ConstructorExtension constructor;
	protected final Type formalType;
	protected final boolean isBasic;

	public ConstructorArgument(ConstructorExtension constructor, DatatypeArgument builderArgument) {
		this.constructor = constructor;
		this.formalType = builderArgument.getType();
		this.isBasic = builderArgument.isBasic();
	}

	@Override
	public Datatype getOrigin() {
		return constructor.getOrigin();
	}

	@Override
	public ConstructorExtension getConstructor() {
		return constructor;
	}

	public Type getFormalType() {
		return formalType;
	}

	@Override
	public boolean isDestructor() {
		return false;
	}

	@Override
	public DestructorExtension asDestructor() {
		if (isDestructor()) {
			return (DestructorExtension) this;
		}
		return null;
	}

	/*
	 * Even though equals() is not implemented, we provide a hash code that will be
	 * used with isSimilarTo() to implement Datatype's hashCode and equals.
	 */
	@Override
	public int hashCode() {
		return formalType.hashCode();
	}

	/*
	 * Implements pseudo-equality, that is equality up to constructor equality.
	 */
	public boolean isSimilarTo(ConstructorArgument other) {
		if (this == other) {
			return true;
		}
		if (this.getClass() != other.getClass()) {
			return false;
		}
		return this.formalType.equals(other.formalType);
	}

	@Override
	public Type getType(ITypeInstantiation instantiation) {
		if (this.getOrigin() != instantiation.getOrigin()) {
			throw new IllegalArgumentException("Instantiation built for "
					+ instantiation.getOrigin() + " but used with "
					+ this.getOrigin());
		}
		return ((TypeSubstitution) instantiation).rewrite(formalType);
	}

	@Override
	public Expression getSet(ISetInstantiation instantiation) {
		if (this.getOrigin() != instantiation.getOrigin()) {
			throw new IllegalArgumentException("Instantiation built for "
					+ instantiation.getOrigin() + " but used with "
					+ this.getOrigin());
		}
		return ((SetSubstitution) instantiation).substitute(formalType);
	}

	@Override
	public boolean isBasic() {
		return isBasic;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	
	public void toString(StringBuilder sb) {
		sb.append(formalType);
	}

}
