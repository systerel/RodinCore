/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

/**
 * Helper class for implementing quantified formulae of event-B.
 * <p>
 * Provides methods which implement common behavior of classes
 * <code>QuantifiedPredicate</code> and <code>QuantifiedExpression</code>.
 * </p>
 * <p>
 * This class is not part of the public API of the AST package.
 * </p>
 * 
 * @author Laurent Voisin
 */
abstract class QuantifiedHelper {

	protected static String getSyntaxTreeQuantifiers(String[] boundBelow, String tabs, BoundIdentDecl[] boundHere) {
		StringBuffer str = new StringBuffer();
		str.append(tabs + "**quantifiers**" + "\n");
		String newTabs = tabs + "\t";
		for (BoundIdentDecl ident: boundHere) {
			str.append(ident.getSyntaxTree(boundBelow, newTabs));
		}
		return str.toString();
	}
	
	protected static boolean areEqualQuantifiers(BoundIdentDecl[] leftBound, BoundIdentDecl[] rightBound, boolean withAlphaConversion) {
		if (leftBound.length != rightBound.length) {
			return false;
		}
		if (! withAlphaConversion) {
			// No alpha conversion, check each identifier.
			for (int i = 0; i < leftBound.length; i++) {
				if (! leftBound[i].equals(rightBound[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * Returns the list of names as a string.
	 */
	protected static String getBoundIdentifiersString(String[] names) {
		StringBuffer str = new StringBuffer();
		str.append(names[0]);
		for (int i= 1; i < names.length; ++i) {
			str.append("," + names[i]);
		}
		return str.toString();
	}

}
