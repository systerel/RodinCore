/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added helper method for extensions
 *******************************************************************************/
package org.eventb.core.ast;

import org.eventb.internal.core.ast.LegibilityResult;


/**
 * Helper class for implementing associative formulae of event-B.
 * <p>
 * Provides methods which implement common behavior of classes
 * <code>AssociativePredicate</code> and <code>AssociativeExpression</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
/* package */ class AssociativeHelper {

	protected static boolean equalsHelper(Formula<?>[] list1,
			Formula<?>[] list2, boolean withAlphaConversion) {

		if (list1.length != list2.length) { 
			return false;
		}
		for (int i = 0, length = list1.length; i < length; i++) {
			if (! list1[i].equals(list2[i], withAlphaConversion)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Formula#getSyntaxTree(FreeIdentifier[], String)
	 */
	protected static String getSyntaxTreeHelper(String[] boundNames,
			String tabs, Formula<?>[] children, String tagOperator,
			String typeName, String className) {
		StringBuilder str = new StringBuilder();
		str.append(tabs + className + " [" + tagOperator + "]" + typeName
				+ "\n");
		String childIndent = tabs + "\t";
		for (Formula<?> child : children) {
			str.append(child.getSyntaxTree(boundNames, childIndent));
		}
		return str.toString();
	}

	// Disable default constructor.
	private AssociativeHelper() {
		assert false;
	}

	/*
	 * Helper for computing well-formedness for a list of formulae.
	 * 
	 * @param formulae
	 *            an array of formulae
	 * @param result
	 *            result of this operation
	 * @param quantifiedIdents
	 *            a list of currently bound identifiers
	 */
	protected static <T extends Formula<T>> void isLegibleList(T[] formulae, LegibilityResult result) {
		for (T formula : formulae) {
			formula.isLegible(result);
		}
	}
	
}
