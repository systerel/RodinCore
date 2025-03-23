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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatypeBuilder;

/**
 * Class used to describe a datatype constructor. The constructor is first built
 * on a datatype builder with a string name. Then arguments could be added until
 * its finalization caused by a call to
 * {@link IDatatypeBuilder#finalizeDatatype()} on the datatype builder.
 * <p>
 * <b>Implementation note</b>: It is important to prevent the use of an unknown
 * given type in argument types. If it were allowed, then type-checking could
 * introduce new given type names in some destructor type and break completely
 * the computation of free identifiers in formulas.
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

	// Formal type parameters that can be inferred from arguments so far
	// May include the datatype itself if this constructor is not basic.
	private final Set<GivenType> knownFormalTypeParameters;

	ConstructorBuilder(DatatypeBuilder dtBuilder, String name) {
		this.dtBuilder = dtBuilder;
		this.name = name;
		this.arguments = new ArrayList<DatatypeArgument>();
		this.knownFormalTypeParameters = new HashSet<>();
	}

	@Override
	public void addArgument(String argName, Type argType) {
		dtBuilder.checkNotFinalized();
		arguments.add(new DatatypeArgument(dtBuilder, argName, argType));
		knownFormalTypeParameters.addAll(argType.getGivenTypes());
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

	public String getName() {
		return name;
	}

	public List<DatatypeArgument> getArguments() {
		return arguments;
	}

	public boolean needsTypeAnnotation() {
		if (!isBasic()) {
			// The full datatype is in the type of at least one argument.
			return false;
		}
		/*
		 * We know that arguments can only use type parameters for their formal types,
		 * in addition to the datatype itself (which is taken care in the previous
		 * test). So we can just count, rather than compare sets.
		 */
		return knownFormalTypeParameters.size() != dtBuilder.getTypeParameters().length;
	}

	/* Must be called only when finalizing the datatype */
	public ConstructorExtension makeExtension(Datatype origin) {
		return new ConstructorExtension(origin, this);
	}

	public void harvest(ExtensionHarvester harvester) {
		for (final DatatypeArgument arg : arguments) {
			arg.harvest(harvester);
		}
	}

}
