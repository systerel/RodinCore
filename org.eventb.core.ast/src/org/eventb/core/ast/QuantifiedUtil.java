/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.ast.FreshNameSolver.solve;

import java.util.Set;

import org.eventb.internal.core.typecheck.TypeEnvironment;

/**
 * This class provides some static method which are useful when manipulating
 * quantified formulas.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class QuantifiedUtil {

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
	public static String[] resolveIdents(BoundIdentDecl[] boundHere,
			Set<String> usedNames) {
		final int nbBoundIdentDecl = boundHere.length;
		final String[] result = new String[nbBoundIdentDecl];

		// Currently, there is no way to pass a type environment to this method,
		// as it might be called from the classical toString() method. So, we
		// use
		// the default factory provided with the AST library. But, that prevents
		// clients from adding new reserved identifier names!
		// TODO how to add new reserved identifier names

		// Create the new identifiers.
		for (int i = 0; i < nbBoundIdentDecl; i++) {
			result[i] = solve(boundHere[i].getName(), usedNames,
					FormulaFactory.getDefault());
			usedNames.add(result[i]);
		}

		return result;
	}

	// resolve (locally) quantified names so that they do not conflict with the
	// given type environment.
	//
	// @see FormulaFactory#makeFreshIdentifiers(BoundIdentDecl[], ITypeEnvironment)
	//
	// TODO : remove the formula factory parameter
	protected static FreeIdentifier[] resolveIdents(
			BoundIdentDecl[] bIdents, ITypeEnvironment environment,
			FormulaFactory factory) {
		final int nbBoundIdentDecl = bIdents.length;
		final FreeIdentifier[] result = new FreeIdentifier[nbBoundIdentDecl];
		
		// Create the new identifiers.
		for (int i = 0; i < nbBoundIdentDecl; i++) {
			final Type bType = bIdents[i].getType();
			final SourceLocation bSourceLoc = bIdents[i].getSourceLocation();
			final String bName = bIdents[i].getName();
			result[i] = ((TypeEnvironment) environment)
					.makeFreshFreeIdentifier(bName, bSourceLoc, bType);
		}
		
		return result;
	}

}
