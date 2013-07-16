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

	private final String name;
	private final Type type;
	private final boolean isBasic;

	public DatatypeArgument(DatatypeBuilder dtBuilder, String name, Type argType) {
		final ArgumentTypeChecker checker = dtBuilder.getArgumentTypeChecker();
		checker.check(argType);
		if (name != null) {
			dtBuilder.checkName(name, "destructor");
		}
		this.name = name;
		this.type = argType;
		this.isBasic = checker.isBasic();
	}

	public Type getType() {
		return type;
	}

	public boolean isBasic() {
		return isBasic;
	}

	public boolean hasDestructor() {
		return name != null;
	}

	public String getDestructorName() {
		return name;
	}

	public ConstructorArgument finalize(Datatype2 origin,
			ConstructorExtension constructorExt) {
		if (hasDestructor()) {
			return new DestructorExtension(origin, constructorExt, name, type);
		} else {
			return new ConstructorArgument(constructorExt, type);
		}
	}

	public void harvest(ExtensionHarvester harvester) {
		harvester.harvest(type);
	}

}