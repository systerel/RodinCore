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

import static org.eventb.internal.core.ast.extension.datatype2.DatatypeLexer.makeDatatypeScanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;
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

	private final String name;
	private final FormulaFactory ff;
	private final List<GivenType> typeParameters;
	private final ArrayList<ConstructorBuilder> constructors;

	private Datatype2 finalized = null;

	/**
	 * Initialize a datatype builder with a formula factory, a datatype name and
	 * the list of type parameters as given types.
	 * 
	 * @param ff
	 *            the initial formula factory used to build the datatype
	 * @param name
	 *            the name of the datatype
	 * @param typeParams
	 *            the type parameters of the datatype
	 * @throws IllegalArgumentException
	 *             if a given type do not use the given factory for building the
	 *             datatype
	 * @throws IllegalArgumentException
	 *             if the given datatype name is not a valid identifier in the
	 *             given factory
	 * @throws IllegalArgumentException
	 *             if one of the given types parameters name is not a valid
	 *             identifier in the given factory or has the same name than the
	 *             datatype
	 * @throws IllegalArgumentException
	 *             if one of the given types parameters has not the given
	 *             factory
	 */
	public DatatypeBuilder(FormulaFactory ff, String name,
			List<GivenType> typeParams) {
		this.ff = ff;
		List<String> names = new ArrayList<String>(1 + typeParams.size());
		checkDatatypeName(names, name);
		this.name = name;
		checkTypeParameters(names, typeParams);
		this.typeParameters = typeParams;
		this.constructors = new ArrayList<ConstructorBuilder>();
	}

	private void checkNotFinalized() {
		if (finalized != null) {
			throw new IllegalStateException(
					"This operation is forbidden on a finalized DatatypeBuilder");
		}
	}

	private void checkDatatypeName(List<String> names, String dtName) {
		if (!ff.isValidIdentifierName(dtName)) {
			throw new IllegalArgumentException("The datatype name: " + dtName
					+ " is not a valid identifier in the factory: " + ff);
		}
		names.add(dtName);
	}

	private void checkTypeParameter(List<String> names, GivenType givenType) {
		final String gtName = givenType.getName();
		if (names.contains(gtName)) {
			throw new IllegalArgumentException("The type parameter name: "
					+ gtName + " is not a valid identifier in the factory: "
					+ ff);
		}
		names.add(gtName);
		final FormulaFactory typeFactory = givenType.getFactory();
		if (this.ff != typeFactory) {
			throw new IllegalArgumentException("The given type " + givenType
					+ " has an incompatible factory: " + typeFactory
					+ " instead of the factory used to build the datatype: "
					+ this.ff);
		}
	}

	private void checkTypeParameters(List<String> names,
			List<GivenType> givenTypes) {
		for (GivenType givenType : givenTypes) {
			checkTypeParameter(names, givenType);
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
		final ConstructorBuilder cons = new ConstructorBuilder(this, consName);
		constructors.add(cons);
		return cons;
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
		if (typeParameters.size() == 0) {
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

	public List<GivenType> getTypeParameters() {
		return typeParameters;
	}

	public String getName() {
		return name;
	}

	public List<ConstructorBuilder> getConstructors() {
		return constructors;
	}

	@Override
	public Datatype2 finalizeDatatype() {
		if (finalized == null) {
			checkHasBasicConstructor();
			finalized = Datatype2.makeDatatype(this);
		}
		return finalized;
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
			final Object origin = extn.getOrigin();
			if (origin instanceof IDatatype2) {
				newExtns.addAll(((IDatatype2) origin).getExtensions());
			}
		}
		extns.addAll(newExtns);
	}

	@Override
	public FormulaFactory getFactory() {
		return ff;
	}

}
