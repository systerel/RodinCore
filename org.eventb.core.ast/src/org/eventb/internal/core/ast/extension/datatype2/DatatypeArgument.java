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

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;

/**
 * Internal class used to describe a datatype constructor argument in the
 * context of a datatype builder. The datatype constructor argument is built on
 * a datatype constructor with a type, which could reference the datatype type
 * and its type parameters, and optionally a name which corresponds to a
 * destructor for the datatype.
 * 
 * @author Vincent Monfort
 */
public final class DatatypeArgument {

	private final String destName;
	private final Type argType;

	DatatypeArgument(ConstructorBuilder cons, String name, Type argType) {
		this.destName = name;
		this.argType = argType;
	}

	DatatypeArgument(ConstructorBuilder cons, Type argType) {
		this.destName = null;
		this.argType = argType;
	}

	public Type getType() {
		return argType;
	}

	public Type substitute(FormulaFactory ff, Map<GivenType, Type> instantiated) {
		TypeSubstitutionRewriter tsRewriter = new TypeSubstitutionRewriter(ff,
				instantiated);
		return tsRewriter.rewrite(argType);
	}

	public Expression substituteToSet(FormulaFactory ff,
			Map<GivenType, Expression> instantiated) {
		TypeSubstitutionToSet tsToSet = new TypeSubstitutionToSet(ff,
				instantiated);
		return tsToSet.toSet(argType);
	}

	public boolean hasDestructor() {
		return destName != null;
	}

	public String getDestructorName() {
		return destName;
	}

	public DestructorExtension finalizeConstructorArgument(Datatype2 origin,
			ConstructorExtension constructorExt) {
		assert (origin.getTypeConstructor() != null);
		if (!hasDestructor()) {
			return null;
		}
		return new DestructorExtension(origin, constructorExt, destName, this);
	}

	public void harvest(ExtensionHarvester harvester) {
		harvester.harvest(argType);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * argType.hashCode()
				+ ((destName == null) ? 0 : destName.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final DatatypeArgument other = (DatatypeArgument) obj;
		if (!this.argType.equals(other.argType)) {
			return false;
		}
		if (this.destName == null) {
			return other.destName == null;
		}
		return this.destName.equals(other.destName);
	}

}