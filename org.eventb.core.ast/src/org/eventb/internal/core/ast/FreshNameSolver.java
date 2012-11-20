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

	private final FormulaFactory factory;
	private final ITypeEnvironment typeEnvironment;
	private final Set<String> usedNames;

	/**
	 * Creates a fresh name solver in the context of the given type environment,
	 * which defines both the mathematical language (through its formula
	 * factory) and a set of already used names.
	 * 
	 * @param typeEnvironment
	 *            some type environment
	 */
	public FreshNameSolver(ITypeEnvironment typeEnvironment) {
		this.factory = typeEnvironment.getFormulaFactory();
		this.typeEnvironment = typeEnvironment;
		this.usedNames = null;
	}

	/**
	 * Creates a fresh name solver in the context of the given set of already
	 * used names and the mathematical language of the given formula factory.
	 * 
	 * @param usedNames
	 *            identifier names already used
	 * @param factory
	 *            the formula factory of the mathematical language to consider
	 */
	public FreshNameSolver(Set<String> usedNames, FormulaFactory factory) {
		this.factory = factory;
		this.usedNames = usedNames;
		this.typeEnvironment = null;
	}

	/**
	 * Returns an identifier name resembling the given name and that does not
	 * occur in the context of this solver (type environment or set of used
	 * names). The returned name is guaranteed to be a valid identifier name in
	 * the mathematical language of this solver.
	 * 
	 * @param name
	 *            some identifier name
	 * 
	 * @return a name fresh in the context of this solver
	 */
	public String solve(String name) {
		if (isValid(name)) {
			return name;
		}
		// We have a name conflict, so we try with other names
		final StructuredName sname = new StructuredName(name);
		String newName;
		do {
			sname.increment();
			newName = sname.toString();
		} while (!isValid(newName));
		return newName;
	}

	/**
	 * Returns an identifier name resembling the given name and that does not
	 * occur in the context of this solver, in this specific case, the set of
	 * used names. The returned name is guaranteed to be a valid identifier name
	 * in the mathematical language of this solver.
	 * <p>
	 * However, this method can only be called if the context manipulated is a
	 * set of used names (i.e. the constructor that has been used is
	 * {@link FreshNameSolver#FreshNameSolver(Set, FormulaFactory)}) to avoid
	 * defining an ambiguous context for name solving.
	 * </p>
	 * 
	 * @param name
	 *            some identifier name
	 * 
	 * @return a name fresh that has been added to the context of this solver
	 */
	public String solveAndAdd(String name) {
		if (typeEnvironment != null) {
			throw new UnsupportedOperationException(
					"The context of name solving is ambiguous.");
		}
		final String solvedName = solve(name);
		usedNames.add(solvedName);
		return solvedName;
	}

	private boolean isValid(String name) {
		if (!factory.isValidIdentifierName(name)) {
			return false;
		}
		if (typeEnvironment != null) {
			return !typeEnvironment.contains(name);
		} else {
			return !usedNames.contains(name);
		}
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