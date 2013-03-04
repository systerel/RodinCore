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

import org.eventb.core.ast.FormulaFactory;
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

	private void checkArgumentType(Type argType) {
		final FormulaFactory typeFactory = argType.getFactory();
		final FormulaFactory ff = dtBuilder.getFactory();
		if (ff != typeFactory) {
			throw new IllegalArgumentException("The given argument type "
					+ argType + " has an incompatible factory: " + typeFactory
					+ " instead of the factory used to build the datatype: "
					+ ff);
		}
	}

	@Override
	public void addArgument(Type argType, String argName) {
		checkNotFinalized();
		checkArgumentType(argType);
		arguments.add(new DatatypeArgument(this, argName, argType));
	}

	@Override
	public void addArgument(Type argType) {
		checkNotFinalized();
		checkArgumentType(argType);
		arguments.add(new DatatypeArgument(this, argType));
	}

	public DatatypeBuilder getDatatypeBuilder() {
		return this.dtBuilder;
	}

	@Override
	public boolean isBasic() {
		GivenType datatypeType = dtBuilder.getFactory().makeGivenType(
				dtBuilder.getName());
		ContainsTypeVisitor visitor = new ContainsTypeVisitor(datatypeType);
		for (DatatypeArgument arg : arguments) {
			if (visitor.containsType(arg.getType())) {
				return false;
			}
		}
		return true;
	}

	public ConstructorExtension finalize(Datatype2 origin) {
		assert (origin.getTypeConstructor() != null);
		GivenType datatypeType = dtBuilder.getFactory().makeGivenType(
				dtBuilder.getName());
		ConstructorExtension consExt = new ConstructorExtension(origin,
				datatypeType, dtBuilder.getTypeParameters(), name, arguments);
		finalized = true;
		return consExt;
	}

	public void harvest(ExtensionHarvester harvester) {
		for (final DatatypeArgument arg: arguments) {
			arg.harvest(harvester);
		}
	}

}
