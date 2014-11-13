/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static org.eventb.internal.core.ast.datatype.DatatypeLexer.makeDatatypeScanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.parser.GenParser;
import org.eventb.internal.core.parser.ParseResult;

/**
 * The datatype builder provides a way to build a new datatype. The datatype
 * builder is first built with its string name and all its type parameters. Then
 * some constructors could be added until the datatype finalization caused by
 * {@link IDatatypeBuilder#finalizeDatatype()}.
 * <p>
 * To ensure uniqueness of datatype objects, we maintain a registry of known
 * datatypes and take care of not leaking copies of already known datatypes.
 * </p>
 * 
 * @author Vincent Monfort
 */
public final class DatatypeBuilder implements IDatatypeBuilder {

	/**
	 * Implements checks of formal type parameters. The parameter names must be
	 * pairwise distinct and different from the datatype name. Moreover, the
	 * parameters must have been created with the same factory as the datatype
	 * builder.
	 */
	private static class FormalNameChecker {

		private final FormulaFactory ff;
		private final Set<String> names;

		public FormalNameChecker(FormulaFactory ff, String datatypeName) {
			this.ff = ff;
			this.names = new HashSet<String>();
			this.names.add(datatypeName);
		}

		public void check(GivenType param) {
			if (ff != param.getFactory()) {
				throw new IllegalArgumentException("The type parameter "
						+ param + " has an incompatible factory: "
						+ param.getFactory() + " instead of: " + ff);
			}

			final String name = param.getName();
			if (!names.add(name)) {
				throw new IllegalArgumentException("The type parameter name "
						+ name + " is duplicated");
			}
		}

	}

	// Formula factory for this builder
	private final FormulaFactory ff;

	// Names defined by the datatype
	private final Set<String> names;

	// Name of the datatype
	private final String name;

	// The datatype as a given type for recursive occurrences
	private final GivenType givenType;

	// Well-formedness checker for argument types
	private final ArgumentTypeChecker argumentTypeChecker;
	
	// Formal type parameters
	private final GivenType[] typeParameters;

	// Constructors added so far.
	private final List<ConstructorBuilder> constructors;

	// Origin of the datatype, or null
	private final Object origin;
	
	// The resulting datatype if finalized, null otherwise
	private Datatype finalized = null;


	/**
	 * Initialize a datatype builder with a formula factory, a datatype name and
	 * the list of type parameters as given types.
	 * 
	 * @param ff
	 *            the initial formula factory used to build the datatype
	 * @param name
	 *            the name of the datatype
	 * @param params
	 *            the type parameters of the datatype
	 * @param origin
	 *            the origin of the datatype, or <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the given datatype name is not a valid identifier in the
	 *             given factory
	 * @throws IllegalArgumentException
	 *             if a type parameter was not created by the given factory
	 * @throws IllegalArgumentException
	 *             if one of the type parameters has the same name as the
	 *             datatype or another type parameter
	 */
	public DatatypeBuilder(FormulaFactory ff, String name,
			List<GivenType> params, Object origin) {
		this.ff = ff;
		this.name = name;
		this.origin = origin;
		this.givenType = ff.makeGivenType(name);
		this.argumentTypeChecker = new ArgumentTypeChecker(givenType);
		checkTypeParameters(params);
		this.typeParameters = params.toArray(new GivenType[params.size()]);
		this.names = new HashSet<String>();
		names.add(name);
		this.constructors = new ArrayList<ConstructorBuilder>();
	}

	private void checkTypeParameters(List<GivenType> typeParams) {
		final FormalNameChecker checker = new FormalNameChecker(ff, name);
		for (final GivenType typeParam : typeParams) {
			checker.check(typeParam);
		}
	}

	@Override
	public boolean hasBasicConstructor() {
		for (final ConstructorBuilder cons : constructors) {
			if (cons.isBasic()) {
				return true;
			}
		}
		return false;
	}

	private void checkHasBasicConstructor() {
		if (!hasBasicConstructor()) {
			throw new IllegalStateException(
					"The datatype cannot be finalized without a basic constructor defined.");
		}
	}

	@Override
	public IConstructorBuilder addConstructor(String consName) {
		checkNotFinalized();
		checkName(consName, "constructor");
		final ConstructorBuilder cons = new ConstructorBuilder(this, consName);
		constructors.add(cons);
		return cons;
	}

	void checkName(String consName, String nature) {
		if (!ff.isValidIdentifierName(consName)) {
			throw new IllegalArgumentException("The " + nature + " name: "
					+ consName + " is not a valid identifier in the factory: "
					+ ff);
		}
		if (!names.add(consName)) {
			throw new IllegalArgumentException("The " + nature + " name: "
					+ consName + " has already been defined for this datatype");
		}
	}

	/*
	 * If this type has no type parameters, we just use the regular parser.
	 * Otherwise, we use a parser with a special scanner to parse types. The
	 * purpose of this special scanner is to jump over the type parameters of
	 * any occurrence of this datatype, while verifying that the parameters are
	 * exactly the same as given to this builder.
	 */
	@Override
	public IParseResult parseType(String strType) {
		if (typeParameters.length == 0) {
			return ff.parseType(strType);
		}
		final ParseResult result = new ParseResult(ff, null);
		final Scanner scanner = makeDatatypeScanner(this, strType, result,
				ff.getGrammar());
		final GenParser parser = new GenParser(Type.class, scanner, result,
				false);
		parser.parse();
		return parser.getResult();
	}

	public GivenType[] getTypeParameters() {
		return typeParameters;
	}

	public String getName() {
		return name;
	}

	public GivenType asGivenType() {
		return givenType;
	}

	public ArgumentTypeChecker getArgumentTypeChecker() {
		return argumentTypeChecker;
	}
	
	public List<ConstructorBuilder> getConstructors() {
		return constructors;
	}

	@Override
	public Datatype finalizeDatatype() {
		if (finalized == null) {
			checkHasBasicConstructor();
			finalized = Datatype.makeDatatype(this);
		}
		return finalized;
	}

	protected void checkNotFinalized() {
		if (finalized != null) {
			throw new IllegalStateException(
					"This operation is forbidden on a finalized DatatypeBuilder");
		}
	}

	public FormulaFactory getBaseFactory() {
		final ExtensionHarvester harvester = new ExtensionHarvester();
		for (final ConstructorBuilder cbuilder : constructors) {
			cbuilder.harvest(harvester);
		}
		final Set<IFormulaExtension> extns = harvester.getResult();
		completeDatatypes(extns);
		return FormulaFactory.getInstance(extns);
	}

	/*
	 * Completes a set of extensions by adding all extensions of any datatype
	 * whose extension is already contained.
	 */
	private final void completeDatatypes(Set<IFormulaExtension> extns) {
		final Set<IFormulaExtension> newExtns = new HashSet<IFormulaExtension>();
		for (final IFormulaExtension extn : extns) {
			final Object extnOrigin = extn.getOrigin();
			if (extnOrigin instanceof IDatatype) {
				final IDatatype dt = (IDatatype) extnOrigin;
				newExtns.addAll(dt.getBaseFactory().getExtensions());
				newExtns.addAll(dt.getExtensions());
			}
		}
		extns.addAll(newExtns);
	}

	@Override
	public FormulaFactory getFactory() {
		return ff;
	}

	/**
	 * Returns the origin of the datatype.
	 * 
	 * @return an Object, or <code>null</code>
	 */
	public Object getOrigin() {
		return origin;
	}

}
