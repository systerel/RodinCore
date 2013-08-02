/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;

/**
 * Parser for {@link IDatatype}. This allows to specify a datatype with a
 * simple string which facilitates writing tests.
 * 
 * @author Thomas Muller
 * @author Vincent Monfort
 * @author Laurent Voisin
 */
public class DatatypeParser {

	/**
	 * Returns a datatype instance built with the given factory and satisfying
	 * the given specification. Datatype are specified with a simple string, in
	 * a manner similar to the following (for the classical List datatype):
	 * 
	 * <pre>
	 * List[T] ::= nil || cons[head: T; tail: List]
	 * </pre>
	 * 
	 * @param factory
	 *            some formula factory
	 * @param specification
	 *            the specification of the datatype as a string
	 * @return a datatype instance
	 */
	public static IDatatype parse(FormulaFactory factory, String specification) {
		return new DatatypeParser(factory).parse(specification);
	}

	// Pattern for the left-hand side of a datatype specification
	private static final Pattern DECL_PATTERN = Pattern.compile("\\s*"
			+ "(\\w+)" // Datatype name
			+ "\\s*" //
			+ "(?:\\[" //
			+ "(.*)" // Optional parameters
			+ "\\])?\\s*");

	// Pattern for a constructor specification
	private static final Pattern CONS_PATTERN = Pattern.compile("\\s*"
			+ "(\\w+)" // Constructor name
			+ "\\s*" //
			+ "(?:\\[" //
			+ "(.*)" // Optional arguments
			+ "\\])?\\s*");

	private final FormulaFactory factory;
	private IDatatypeBuilder builder;
	private IConstructorBuilder cons;

	private DatatypeParser(FormulaFactory factory) {
		this.factory = factory;
	}

	private IDatatype parse(String specification) {
		final String[] parts = specification.split("::=");
		assertEquals(2, parts.length);
		builder = parseDeclaration(parts[0]);
		parseConstructors(parts[1]);
		return builder.finalizeDatatype();
	}

	private IDatatypeBuilder parseDeclaration(String decl) {
		final Matcher matcher = DECL_PATTERN.matcher(decl);
		assertTrue("Invalid declaration " + decl, matcher.matches());
		final String name = matcher.group(1);
		final GivenType[] params = parseParameters(matcher.group(2));
		return factory.makeDatatypeBuilder(name, params);
	}

	private GivenType[] parseParameters(String params) {
		if (params == null) {
			return new GivenType[0];
		}
		final String[] parts = params.split(",");
		final GivenType[] result = new GivenType[parts.length];
		for (int i = 0; i < result.length; i++) {
			final String name = parts[i].trim();
			result[i] = factory.makeGivenType(name);
		}
		return result;
	}

	private void parseConstructors(String string) {
		final String[] parts = string.split("\\|\\|");
		for (final String part : parts) {
			parseConstructor(part);
		}
	}

	private void parseConstructor(String spec) {
		final Matcher matcher = CONS_PATTERN.matcher(spec);
		assertTrue("Invalid constructor " + spec, matcher.matches());
		final String name = matcher.group(1);
		cons = builder.addConstructor(name);
		final String argSpecs = matcher.group(2);
		if (argSpecs != null) {
			parseArguments(argSpecs);
		}
	}

	private void parseArguments(String argSpecs) {
		final String[] parts = argSpecs.split(";");
		for (final String part : parts) {
			parseArgument(part);
		}
	}

	private void parseArgument(String argSpec) {
		final String[] parts = argSpec.split(":");
		final String name;
		final Type type;
		switch (parts.length) {
		case 1:
			// Nameless argument
			type = parseType(parts[0], factory);
			cons.addArgument(type);
			return;
		case 2:
			// Destructor
			name = parts[0].trim();
			type = parseType(parts[1], factory);
			cons.addArgument(name, type);
			return;
		default:
			fail("inconsistent argument " + argSpec);
		}
	}

}
