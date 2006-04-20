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

	/**
	 * Returns the array of bound identifiers which are bound outside of a
	 * quantified formula, and after renumbering them properly.
	 * 
	 * @param boundIdentsBelow
	 *            array of bound identifiers occurring in the quantified
	 *            formula.
	 * @param boundIdentDecls
	 *            declaration of bound identifiers at the quantified formula
	 * @param ff
	 *            formula factory to use for building the result
	 * @return an array of identifiers which are used in the formula, but bound
	 *         outside of it
	 */
	protected static BoundIdentifier[] getBoundIdentsAbove(
			BoundIdentifier[] boundIdentsBelow, BoundIdentDecl[] boundIdentDecls, FormulaFactory ff) {
		
		final int nbBoundBelow = boundIdentsBelow.length;
		final int nbBound = boundIdentDecls.length;
		for (int i = 0; i < nbBoundBelow; i++) {
			final int index = boundIdentsBelow[i].getBoundIndex(); 
			if (nbBound <= index) {
				// We're on the first identifier bound outside
				final int length = nbBoundBelow - i;
				final BoundIdentifier[] result = new BoundIdentifier[length];
				for (int j = 0; j < length; ++j, ++i) {
					result[j] = ff.makeBoundIdentifier(
							boundIdentsBelow[i].getBoundIndex() - nbBound,
							boundIdentsBelow[i].getSourceLocation(),
							boundIdentsBelow[i].getType());
				}
				return result;
			}
		}
		return Formula.NO_BOUND_IDENTS;
	}
	
	/**
	 * Checks that the given bound identifier declarations are typed and that
	 * their type are compatible with occurrences of the (bound) identifier.
	 * 
	 * @param boundIdentsBelow
	 *            array of bound identifiers occurring in the quantified formula
	 * @param boundIdentDecls
	 *            declaration of bound identifiers at the quantified formula
	 * @return <code>true</code> iff typecheck succeeds
	 */
	protected static boolean checkBoundIdentTypes(
			BoundIdentifier[] boundIdentsBelow, BoundIdentDecl[] boundIdentDecls) {
		
		final int nbBound = boundIdentDecls.length;
		
		// First check that all declarations are typed.
		for (BoundIdentDecl decl: boundIdentDecls) {
			if (! decl.isTypeChecked()) {
				return false;
			}
		}
		
		// Then, check that occurrences are typed, and with the same type as decl
		for (BoundIdentifier ident: boundIdentsBelow) {
			final int index = ident.getBoundIndex();
			if (nbBound <= index) {
				// We're on an identifier bound outside
				break;
			}
			final Type declType = ident.getDeclaration(boundIdentDecls).getType();
			if (! declType.equals(ident.getType())) {
				// incompatible with references.
				return false;
			}
		}
		return true;
	}
		
}
