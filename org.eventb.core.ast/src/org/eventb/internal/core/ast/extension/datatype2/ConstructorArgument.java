/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype2;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IConstructorArgument;

/**
 * Implements unnamed arguments of constructors.
 * 
 * @author Laurent Voisin
 */
public class ConstructorArgument implements IConstructorArgument {

	protected final ConstructorExtension constructor;
	protected final Type formalType;

	public ConstructorArgument(ConstructorExtension constructor, Type formalType) {
		this.constructor = constructor;
		this.formalType = formalType;
	}

	@Override
	public Datatype2 getOrigin() {
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

	@Override
	public int hashCode() {
		return formalType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final ConstructorArgument other = (ConstructorArgument) obj;
		return this.formalType.equals(other.formalType);
	}

}
