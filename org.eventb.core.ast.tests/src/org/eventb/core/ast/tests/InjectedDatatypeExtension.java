/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
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
import static org.eventb.core.ast.tests.AbstractTests.parseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;

/**
 * Helper class to create instances of datatypes.
 * 
 * @author "Thomas Muller"
 */
public class InjectedDatatypeExtension implements IDatatypeExtension {

	/**
	 * The constructor destructor pattern. Group #2 is the constructor or
	 * destructor identifier, group #4 identifies the type
	 */
	private static final Pattern cdPattern = compile("(\\s*)([^\\[\\s]+)(\\[?\\s*)(([^\\]]+)*)(\\]?\\s*)");

	/**
	 * Global pattern matching the extension definition. Group #1 concerns the
	 * type constructor, group #3 concerns the definition of constructor and
	 * destructors.
	 */
	private static final Pattern extensionDefPattern = compile("(.*)(::=)(.+)");

	private final FormulaFactory ff;
	private final String extensionExpression;
	private final String typeConsSymbol;

	public static IDatatypeExtension injectExtension(String extensionExpr) {
		return new InjectedDatatypeExtension(extensionExpr);
	}

	public static IDatatypeExtension injectExtension(String extensionExpr,
			FormulaFactory ff) {
		return new InjectedDatatypeExtension(extensionExpr, ff);
	}

	private InjectedDatatypeExtension(String extensionExpression) {
		this(extensionExpression, FormulaFactory.getDefault());
	}

	private InjectedDatatypeExtension(String extensionExpression,
			FormulaFactory ff) {
		this.ff = ff;
		this.extensionExpression = extensionExpression;
		this.typeConsSymbol = getTypeConstructor(extensionExpression);
	}

	private static String getTypeConstructor(String definition) {
		final String typeDefStr = getGroup(extensionDefPattern, 1, definition);
		return getGroup(cdPattern, 2, typeDefStr);
	}

	private static String getGroup(Pattern pattern, int group,
			final String input) {
		final Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group(group);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getTypeName() {
		return typeConsSymbol;
	}

	@Override
	public String getId() {
		return "org.eventb.core.ast.tests." + typeConsSymbol.toUpperCase();
	}

	@Override
	public void addTypeParameters(ITypeConstructorMediator mediator) {
		for (String arg : getTypeArguments(extensionExpression)) {
			mediator.addTypeParam(arg);
		}
	}

	private static String[] splitOn(String toSplit, String splitOnSymbol) {
		return toSplit.split("\\s*" + splitOnSymbol + "\\s*");
	}

	private static List<String> getTypeArguments(String definition) {
		final String typeDefStr = getGroup(extensionDefPattern, 1, definition);
		final String typesStr = getGroup(cdPattern, 4, typeDefStr);
		if (typesStr.isEmpty()) {
			return emptyList();
		}
		final String[] typeArgs = splitOn(typesStr, ",");
		return Arrays.asList(typeArgs);
	}

	@Override
	public void addConstructors(IConstructorMediator mediator) {
		final Map<String, Map<String, String>> constructors = getConstructors(extensionExpression);
		for (String cons : constructors.keySet()) {
			final Map<String, String> destructors = constructors.get(cons);
			if (destructors.size() == 0) {
				mediator.addConstructor(cons, cons.toUpperCase());
				continue;
			}
			final ArrayList<IArgument> arguments = new ArrayList<IArgument>();
			for (String dest : destructors.keySet()) {
				final IArgumentType argumentType = getType(mediator,
						destructors.get(dest));
				arguments.add(mediator.newArgument(dest, argumentType));
			}
			mediator.addConstructor(cons, cons.toUpperCase(), arguments);
		}
	}

	private static Map<String, Map<String, String>> getConstructors(
			String typeSpec) {
		final Map<String, Map<String, String>> result = new LinkedHashMap<String, Map<String, String>>();
		final String condDestStr = getGroup(extensionDefPattern, 3, typeSpec);
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
			final String destName = getGroup(cdPattern, 2, dest);
			final String destType = getGroup(cdPattern, 4, dest);
			result.put(destName, destType);
		}
		return result;
	}

	private static String removeSpaces(final String str) {
		return str.replaceAll("\\s*", "");
	}

	private IArgumentType getType(IConstructorMediator mediator, String dest) {
		final Pattern prodPattern = Pattern.compile("(.+)×(.+)");
		final Matcher prodTypeMatcher = prodPattern.matcher(dest);
		if (prodTypeMatcher.find()) {
			return mediator.makeProductType(
					getType(mediator, prodTypeMatcher.group(1)),
					getType(mediator, prodTypeMatcher.group(2)));
		}
		final String regex = "(" + typeConsSymbol + ")" + "(\\()(.+)(\\))";
		final Pattern currentDatatypePattern = Pattern.compile(regex);
		final Matcher currentMatcher = currentDatatypePattern.matcher(dest);
		if (currentMatcher.find()) {
			final String types = currentMatcher.group(3);
			return mediator.makeParametricType(mediator.getTypeConstructor(),
					getListOfTypeArgs(mediator, types));
		}
		final Type type = parseType(dest, ff);
		return mediator.newArgumentType(type);
	}

	private List<IArgumentType> getListOfTypeArgs(
			IConstructorMediator mediator, String typeStrs) {
		final List<IArgumentType> result = new ArrayList<IArgumentType>();
		final String[] typeStrsArray = splitOn(typeStrs, ",");
		for (String typeStr : typeStrsArray) {
			final Type type = parseType(typeStr, ff);
			result.add(mediator.newArgumentType(type));
		}
		return result;
	}

}