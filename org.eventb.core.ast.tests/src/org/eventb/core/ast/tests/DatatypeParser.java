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

import static java.util.Arrays.copyOfRange;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.extension.datatype2.IConstructorBuilder;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.ast.extension.datatype2.IDatatypeBuilder;

/**
 * Parser for {@link IDatatype2}.
 * 
 * @author Thomas Muller
 * @author Vincent Monfort
 */
public class DatatypeParser {

	/**
	 * Returns a datatype instance built with the given factory and satisfying
	 * the given specification. Datatype are specified with a simple string, in
	 * a manner similar to the following (for the classical List datatype):
	 * 
	 * <pre>
	 * List[T] ::= nil || cons; head[T]; tail[List(T)]
	 * </pre>
	 * 
	 * @param ff
	 *            some formula factory
	 * @param specification
	 *            the specification of the datatype as a string
	 * @return a datatype instance
	 */
	public static IDatatype2 parse(FormulaFactory ff, String specification) {
		return new DatatypeParser(ff, specification).parse();
	}

	/**
	 * The constructor destructor pattern. Group #1 is the constructor or
	 * destructor identifier, group #2 identifies the type
	 */
	private static final Pattern cdPattern = compile("" //
			+ "\\s*" // initial spaces
			+ "([^\\[\\s]+)" // operator name
			+ "\\[?\\s*" // type start
			+ "([^\\[\\]]*)" // type
			+ "\\]?\\s*");

	/**
	 * Global pattern matching the extension definition. Group #1 concerns the
	 * type constructor, group #2 concerns the definition of constructor and
	 * destructors.
	 */
	private static final Pattern extensionDefPattern = compile("(.+)::=(.+)");

	private final FormulaFactory ff;
	private final String extensionExpression;

	private DatatypeParser(FormulaFactory ff, String extensionExpression) {
		this.ff = ff;
		this.extensionExpression = extensionExpression;
	}

	private IDatatype2 parse() {
		List<String> stringTypeParams = getTypeArguments();
		ArrayList<GivenType> typeParams = new ArrayList<GivenType>(
				stringTypeParams.size());
		for (String arg : stringTypeParams) {
			if (!arg.isEmpty())
				typeParams.add(ff.makeGivenType(arg));
		}
		IDatatypeBuilder dtBuilder = ff.makeDatatypeBuilder(
				getTypeConstructor(extensionExpression), typeParams);

		final Map<String, Map<String, String>> constructors = getConstructors();
		for (String cons : constructors.keySet()) {
			final Map<String, String> destructors = constructors.get(cons);
			IConstructorBuilder dtCons = dtBuilder.addConstructor(cons);
			for (String dest : destructors.keySet()) {
				dtCons.addArgument(dtBuilder.parseType(destructors.get(dest))
						.getParsedType(), dest);
			}
		}

		return dtBuilder.finalizeDatatype();
	}

	private static String getTypeConstructor(String definition) {
		final String typeDefStr = getGroup(extensionDefPattern, 1, definition);
		return getGroup(cdPattern, 1, typeDefStr);
	}

	private static String getGroup(Pattern pattern, int group,
			final String input) {
		final Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(group);
		}
		throw new IllegalArgumentException();
	}

	private static String[] splitOn(String toSplit, String splitOnSymbol) {
		return toSplit.split("\\s*" + splitOnSymbol + "\\s*");
	}

	private List<String> getTypeArguments() {
		final String typeDefStr = getGroup(extensionDefPattern, 1,
				extensionExpression);
		final String typesStr = getGroup(cdPattern, 2, typeDefStr);
		if (typesStr.isEmpty()) {
			return emptyList();
		}
		final String[] typeArgs = splitOn(typesStr, ",");
		return Arrays.asList(typeArgs);
	}

	private Map<String, Map<String, String>> getConstructors() {
		final String condDestStr = getGroup(extensionDefPattern, 2,
				extensionExpression);
		if (condDestStr.matches("\\s*")) // no constructor nor destructor
			return Collections.emptyMap();
		final Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
		final String[] split = splitOn(condDestStr, "\\|\\|");
		for (String constDest : split) {
			final String[] cdStrs = splitOn(constDest, ";");
			final int cdLength = cdStrs.length;
			final String consStr = cdStrs[0];
			if (cdLength == 1) {
				result.put(removeSpaces(consStr),
						Collections.<String, String> emptyMap());
				continue;
			}
			final String[] destStrArray = copyOfRange(cdStrs, 1, cdLength);
			result.put(removeSpaces(consStr), getDestuctors(destStrArray));

		}
		return result;
	}

	private static Map<String, String> getDestuctors(String[] destructorStrs) {
		final Map<String, String> result = new LinkedHashMap<String, String>();
		for (String dest : destructorStrs) {
			final String destName = getGroup(cdPattern, 1, dest);
			final String destType = getGroup(cdPattern, 2, dest);
			result.put(destName, destType);
		}
		return result;
	}

	private static String removeSpaces(final String str) {
		return str.replaceAll("\\s*", "");
	}

}