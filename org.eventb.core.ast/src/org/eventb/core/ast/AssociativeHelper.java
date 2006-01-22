/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;


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

	protected static boolean equalsHelper(Formula[] list1, Formula[] list2, boolean withAlphaConversion) {
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

	protected static boolean getSubstitutedList(Formula[] list,
			Substitution subst, Formula[] newList, FormulaFactory ff) {

		assert list.length == newList.length;
		boolean equal = true;
		for (int i = 0; i < list.length; i++) {
			newList[i] = list[i].applySubstitution(subst, ff);
			if (newList[i] != list[i])
				equal = false;
		}
		return equal;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see Formula#getSyntaxTree(FreeIdentifier[], String)
	 */
	protected static String getSyntaxTreeHelper(String[] boundNames,
			String tabs, Formula[] children, String tagOperator,
			String typeName, String className) {
		StringBuilder str = new StringBuilder();
		str.append(tabs + className + " [" + tagOperator + "]" + typeName
				+ "\n");
		String childIndent = tabs + "\t";
		for (Formula child : children) {
			str.append(child.getSyntaxTree(boundNames, childIndent));
		}
		return str.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Formula#toStringFullyParenthesized(FreeIdentifier[])
	 */
	protected static String toStringFullyParenthesizedHelper(String[] boundNames,
			Formula[] children, String tagOperator) {
		StringBuilder str = new StringBuilder();
		str.append("(" + children[0].toStringFullyParenthesized(boundNames)
				+ ")");
		for (int i = 1; i < children.length; i++) {
			str.append(tagOperator
					+ "("
					+ children[i].toStringFullyParenthesized(boundNames)
					+ ")");
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
