/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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

import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;


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

	protected static void toStringHelper(StringBuilder builder,
			String[] boundNames, boolean needsParen, Formula<?>[] children,
			String tagOperator, int tag, boolean withTypes) {

		if (needsParen)  builder.append('(');
		boolean isRight = false;
		String sep = "";
		for (Formula<?> child: children) {
			builder.append(sep);
			sep = tagOperator;
			child.toString(builder, isRight, tag, boundNames, withTypes);
			isRight = true;
		}
		if (needsParen) builder.append(')');
	}
	
	// TODO too many arguments
	protected static void toStringHelper(StringBuilder builder,
			String[] boundNames, boolean needsParen, boolean withTypes,
			int tag, IFormulaExtension extension, IExtendedFormula formula,
			FormulaFactory ff) {
		if (needsParen)
			builder.append('(');
		final ToStringMediator strMed = new ToStringMediator(builder,
				boundNames, extension.getSyntaxSymbol(), tag, withTypes);
		final IExtensionPrinter printer = ff.getGrammar().getPrinter(
				extension.getKind().getProperties(), true);
		// FIXME NPE: printer can be null
		
		printer.toString(strMed, formula);
		if (needsParen)
			builder.append(')');
	}

	protected static void toStringFullyParenthesizedHelper(
			StringBuilder builder, String[] boundNames,
			Formula<?>[] children, String tagOperator) {
		
		String sep = "";
		for (Formula<?> child : children) {
			builder.append(sep);
			sep = tagOperator;
			builder.append('(');
			child.toStringFullyParenthesized(builder, boundNames);
			builder.append(')');
		}
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
	protected static <T extends Formula<T>> void isLegibleList(T[] formulae, LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		for (T formula : formulae) {
			formula.isLegible(result, quantifiedIdents);
			if (!result.isSuccess()) {
				return;
			}
		}
	}
	
}
