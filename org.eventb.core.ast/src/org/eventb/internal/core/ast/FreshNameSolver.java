/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted and refactored solve() from QuantifiedUtil
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.core.typecheck.TypeEnvironment;

/**
 * Helper class to compute fresh names. This class provides two static methods
 * for the case where only one fresh name needs to be computed. However, when
 * computing several names in a row, it is more efficient to directly
 * instantiate this class and use its public method.
 * <p>
 * This class does not use any kind of cache, it is therefore possible to modify
 * the type environment or the set of used names between two calls of the
 * <code>solve()</code> method.
 * </p>
 * 
 */
public class FreshNameSolver {

	/**
	 * Returns an identifier name resembling the given name and that does not
	 * occur in the given type environment. The returned name is guaranteed to
	 * be a valid identifier name in the mathematical language of the given type
	 * environment.
	 * 
	 * @param name
	 *            some identifier name
	 * @param environment
	 *            the type environment in which the returned name must be fresh
	 * 
	 * @return a name fresh in the given type environment
	 */
	public static String solve(TypeEnvironment environment, String name) {
		return new FreshNameSolver(environment).solve(name);
	}

	/**
	 * Returns an identifier name resembling the given name and that does not
	 * occur in the given set of used names. The returned name is guaranteed to
	 * be a valid identifier name in the mathematical language specified by the
	 * given formula factory.
	 * <p>
	 * This method is an alternative to {{@link #solve(TypeEnvironment, String)}
	 * as in certain cases, the type environment can not be built (e.g. when
	 * formulas are untyped).
	 * </p>
	 * 
	 * @param name
	 *            some identifier name
	 * @param usedNames
	 *            identifier names already used
	 * @param factory
	 *            the formula factory that defines the mathematical language,
	 *            and in particular the reserved names of the language
	 * 
	 * @return an identifier name that does not occur in the given used names
	 */
	public static String solve(String name, Set<String> usedNames,
			FormulaFactory factory) {
		return new FreshNameSolver(usedNames, factory).solve(name);
	}

	private final FormulaFactory factory;
	private final ITypeEnvironment typeEnvironment;
	private final Set<String> usedNames;

	public FreshNameSolver(ITypeEnvironment typeEnvironment) {
		this.factory = typeEnvironment.getFormulaFactory();
		this.typeEnvironment = typeEnvironment;
		this.usedNames = null;
	}

	public FreshNameSolver(Set<String> usedNames, FormulaFactory factory) {
		this.factory = factory;
		this.usedNames = usedNames;
		this.typeEnvironment = null;
	}

	public String solve(String name) {
		if (!contains(name)) {
			// Not used, this name is OK.
			return name;
		}
		// We have a name conflict, so we try with other names
		final StructuredName sname = new StructuredName(name);
		String newName;
		do {
			sname.increment();
			newName = sname.toString();
		} while (contains(newName) || !factory.isValidIdentifierName(newName));
		return newName;
	}

	private boolean contains(String name) {
		if (typeEnvironment != null) {
			return typeEnvironment.contains(name);
		}
		return usedNames.contains(name);
	}

	/**
	 * Representation of an identifier name in three components:
	 * <ul>
	 * <li>an arbitrary prefix</li>
	 * <li>an optional numeric suffix</li>
	 * <li>an optional string of quote characters</li>
	 * </ul>
	 * <p>
	 * The aim of this class is to create new identifier names by incrementing
	 * the numeric suffix, while keeping the other components unchanged.
	 * </p>
	 */
	private static class StructuredName {
		private final String prefix;
		private int suffix;
		private final String quotes;

		static Pattern suffixExtractor = Pattern.compile(
				"^(.*[^\\d'])(\\d*)('*)$", Pattern.DOTALL);

		StructuredName(String name) {
			final Matcher matcher = suffixExtractor.matcher(name);
			final boolean result = matcher.matches();
			assert result;
			prefix = matcher.group(1);
			final String digits = matcher.group(2);
			if (digits.length() != 0)
				suffix = Integer.valueOf(digits);
			else
				suffix = -1;
			quotes = matcher.group(3);
		}

		public void increment() {
			++suffix;
		}

		@Override
		public String toString() {
			if (suffix < 0) {
				return prefix + quotes;
			}
			return prefix + suffix + quotes;
		}
	}

}