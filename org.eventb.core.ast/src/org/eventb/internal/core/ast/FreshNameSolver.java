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
 * Helper class to compute free names.
 */
public class FreshNameSolver {

	private static class StructuredName {
		private final String prefix;
		private int suffix;
		private final String quotes;

		static Pattern suffixExtractor = Pattern.compile(
				"^(.*[^\\d'])(\\d*)('*)$", Pattern.DOTALL);

		StructuredName(String name) {
			Matcher matcher = suffixExtractor.matcher(name);
			boolean result = matcher.matches();
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
	
	private FreshNameSolver() {
		// singleton
	}
	
	private static class NameSolver {

		private FormulaFactory factory;
		private ITypeEnvironment typeEnvironment;
		private Set<String> usedNames;
				
		public NameSolver() {
			// Nothing to do
		}

		private boolean contains(String name) {
			if (typeEnvironment != null) {
				return typeEnvironment.contains(name);
			}
			return usedNames.contains(name);
		}

		private FormulaFactory getFormulaFactory() {
			return factory;
		}
		
		protected String solve(ITypeEnvironment environment, String name) {
			this.factory = environment.getFormulaFactory();
			this.typeEnvironment = environment;
			this.usedNames = null;
			return solve(name);
		}
		
		protected String solve(FormulaFactory forumlaFactory, Set<String> reservedNames, String name){
			this.factory = forumlaFactory;
			this.typeEnvironment = null;
			this.usedNames = reservedNames;
			return solve(name);
		}
		
		private String solve(String name) {
			if (!contains(name)) {
				// Not used, this name is OK.
				return name;
			}
			// We have a name conflict, so we try with another name
			final StructuredName sname = new StructuredName(name);
			String newName;
			do {
				sname.increment();
				newName = sname.toString();
			} while (contains(newName)
					|| !getFormulaFactory().isValidIdentifierName(newName));
			return newName;
		}

	}
	
	private static final NameSolver SOLVER = new NameSolver();

	/**
	 * Method which returns from the given name a solved free name that does not
	 * appear in the given type environment.
	 * 
	 * @param name
	 *            the name to solve
	 * 
	 * @return a solved name that does not appear in the type environment
	 */
	public static String solve(TypeEnvironment environment, String name) {
		return SOLVER.solve(environment, name);
	}

	/**
	 * Method which returns from the given name a solved free name that does not
	 * appear in the given set of used names, checking that the name is valid by
	 * using the given formula factory.
	 * 
	 * This method is an alternative to {{@link #solve(TypeEnvironment, String)}
	 * as in certain cases, the type environment can not be given (e.g. when
	 * simply parsing formulas).
	 * 
	 * @param name
	 *            the name to solve
	 * @return a solved name that does not appear in the used names
	 */
	public static String solve(String name, Set<String> usedNames,
			FormulaFactory factory) {
		return SOLVER.solve(factory, usedNames, name);
	}

}
