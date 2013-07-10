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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;

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

	// When non-null, the final constructor built from this instance
	private boolean finalized = false;

	ConstructorBuilder(DatatypeBuilder dtBuilder, String name) {
		this.dtBuilder = dtBuilder;
		this.name = name;
		this.arguments = new ArrayList<DatatypeArgument>();
	}

	private void checkNotFinalized() {
		if (finalized) {
			throw new IllegalStateException(
					"This operation is forbidden on a finalized DatatypeConstructor");
		}
	}

	@Override
	public void addArgument(Type argType, String argName) {
		checkNotFinalized();
		dtBuilder.getArgumentTypeChecker().check(argType);
		if (argName != null) {
			dtBuilder.checkName(argName, "destructor");
		}
		arguments.add(new DatatypeArgument(argName, argType));
	}

	@Override
	public void addArgument(Type argType) {
		addArgument(argType, null);
	}

	@Override
	public boolean isBasic() {
		final GivenType datatypeType = dtBuilder.asGivenType();
		final ContainsTypeVisitor visitor = new ContainsTypeVisitor(
				datatypeType);
		for (DatatypeArgument arg : arguments) {
			if (visitor.containsType(arg.getType())) {
				return false;
			}
		}
		return true;
	}

	public ConstructorExtension finalize(Datatype2 origin) {
		assert (origin.getTypeConstructor() != null);
		final GivenType datatypeType = dtBuilder.asGivenType();
		final ConstructorExtension consExt = new ConstructorExtension(origin,
				datatypeType, dtBuilder.getTypeParameters(), name, arguments);
		finalized = true;
		return consExt;
	}

	public void harvest(ExtensionHarvester harvester) {
		for (final DatatypeArgument arg : arguments) {
			arg.harvest(harvester);
		}
	}

}
