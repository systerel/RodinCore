/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides some static method which are useful when manipulating
 * quantified formulas.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class QuantifiedUtil {

	private static class StructuredName {
		private final String prefix;
		private int suffix;
		private final String quotes;
		
		static Pattern suffixExtractor = 
			Pattern.compile("^(.*[^\\d'])(\\d*)('*)$", Pattern.DOTALL);		
		
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
			++ suffix;
		}
		
		@Override 
		public String toString() {
			if (suffix < 0) {
				return prefix + quotes;
			}
			return prefix + suffix + quotes;
		}
	}

	/**
	 * Concatenates the two given arrays of bound identifier declarations into one.
	 * 
	 * @param bound1
	 *            first array to concatenate
	 * @param bound2
	 *            second array to concatenate
	 * @return the result of concatenating the second array after the first one
	 */
	public static BoundIdentDecl[] catenateBoundIdentLists(BoundIdentDecl[] bound1, BoundIdentDecl[] bound2) {
		BoundIdentDecl[] newBoundIdents = new BoundIdentDecl[bound1.length + bound2.length];
		System.arraycopy(bound1, 0, newBoundIdents, 0, bound1.length);
		System.arraycopy(bound2, 0, newBoundIdents, bound1.length, bound2.length);
		return newBoundIdents;
	}

	/**
	 * Concatenates the two given arrays into one array of identifier names.
	 * 
	 * @param boundNames
	 *            array of identifier names
	 * @param quantifiedIdents
	 *            array of quantifier identifier declarations
	 * @return the result of concatenating the names in the second array after the first one
	 */
	public static String[] catenateBoundIdentLists(String[] boundNames, BoundIdentDecl[] quantifiedIdents) {
		String[] newBoundNames = new String[boundNames.length + quantifiedIdents.length];
		System.arraycopy(boundNames, 0, newBoundNames, 0, boundNames.length);
		int idx = boundNames.length;
		for (BoundIdentDecl ident : quantifiedIdents) {
			newBoundNames[idx ++] = ident.getName();
		}
		return newBoundNames;
	}

	/**
	 * Concatenates the two given arrays into one.
	 * 
	 * @param bound1
	 *            first array of names
	 * @param bound2
	 *            second array of names
	 * @return the result of concatenating the second array after the first one
	 */
	public static String[] catenateBoundIdentLists(String[] bound1, String[] bound2) {
		String[] newBoundIdents = new String[bound1.length + bound2.length];
		System.arraycopy(bound1, 0, newBoundIdents, 0, bound1.length);
		System.arraycopy(bound2, 0, newBoundIdents, bound1.length, bound2.length);
		return newBoundIdents;
	}

	/**
	 * Find new names for the given quantified identifiers so that they don't
	 * conflict with the given names.
	 * 
	 * @param boundHere
	 *            array of bound identifier declarations to make free.
	 * @param usedNames
	 *            set of names that are reserved (usually occurring already
	 *            free in the formula)
	 * @return a list of new names that are distinct from each other and do not
	 *         occur in the list of used names
	 */
	public static String[] resolveIdents(BoundIdentDecl[] boundHere, Set<String> usedNames) {
		final int length = boundHere.length;
		String[] result = new String[length];
		
		// Currently, there is no way to pass a formula factory to this method,
		// as it might be called from the classical toString() method.  So, we use
		// the default factory provided with the AST library.  But, that prevents
		// clients from adding new reserved identifier names!
		// TODO how to add new reserved identifier names
		final FormulaFactory factory = FormulaFactory.getDefault();
		
		// Create the new identifiers.
		for (int i = 0; i < length; i++) {
			result[i] = solve(boundHere[i].getName(), usedNames, factory);
			usedNames.add(result[i]);
		}
		
		return result;
	}

	private static String solve(String name, Set<String> usedNames, FormulaFactory factory) {
		if (! usedNames.contains(name)) {
			// Not used, this name is OK.
			return name;
		}
		
		// We have a name conflict, so we try with another name
		QuantifiedUtil.StructuredName sname = new QuantifiedUtil.StructuredName(name);
		String newName;
		do {
			sname.increment();
			newName = sname.toString();
		} while (usedNames.contains(newName) || !factory.isValidIdentifierName(newName));
		
		return newName;
	}

	// resolve (locally) quantified names so that they do not conflict with the
	// given type environment.
	//
	// @see FormulaFactory#makeFreshIdentifiers(BoundIdentDecl[], ITypeEnvironment)
	//
	protected static FreeIdentifier[] resolveIdents(BoundIdentDecl[] boundHere,
			ITypeEnvironment environment, FormulaFactory factory) {
		
		final int length = boundHere.length;
		FreeIdentifier[] result = new FreeIdentifier[length];
		
		// Create the new identifiers.
		for (int i = 0; i < length; i++) {
			assert boundHere[i].getType() != null;
			
			String name = solve(boundHere[i].getName(), environment.getNames(), factory);
			result[i] = factory.makeFreeIdentifier(
					name, 
					boundHere[i].getSourceLocation(),
					boundHere[i].getType());
			environment.addName(name, result[i].getType());
		}
		
		return result;
	}

}
