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
package org.eventb.internal.core.ast.datatype;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatypeBuilder;

/**
 * Class used to describe a datatype constructor. The constructor is first built
 * on a datatype builder with a string name. Then arguments could be added until
 * its finalization caused by a call to
 * {@link IDatatypeBuilder#finalizeDatatype()} on the datatype builder.
 * 
 * @author Vincent Monfort
 */
public final class ConstructorBuilder implements IConstructorBuilder {

	// Parent datatype builder
	private final DatatypeBuilder dtBuilder;

	// Constructor name
	private final String name;

	// Arguments so far
	private final List<DatatypeArgument> arguments;

	ConstructorBuilder(DatatypeBuilder dtBuilder, String name) {
		this.dtBuilder = dtBuilder;
		this.name = name;
		this.arguments = new ArrayList<DatatypeArgument>();
	}

	@Override
	public void addArgument(String argName, Type argType) {
		dtBuilder.checkNotFinalized();
		arguments.add(new DatatypeArgument(dtBuilder, argName, argType));
	}

	@Override
	public void addArgument(Type argType) {
		addArgument(null, argType);
	}

	@Override
	public boolean isBasic() {
		for (final DatatypeArgument arg : arguments) {
			if (!arg.isBasic()) {
				return false;
			}
		}
		return true;
	}

	/* Must be called only when finalizing the datatype */
	public ConstructorExtension makeExtension(Datatype origin) {
		return new ConstructorExtension(origin, name, arguments);
	}

	public void harvest(ExtensionHarvester harvester) {
		for (final DatatypeArgument arg : arguments) {
			arg.harvest(harvester);
		}
	}

}
