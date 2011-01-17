/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.OFTYPE;

import java.util.List;

import org.eventb.internal.core.ast.BoundIdentDeclRemover;

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
	 * Appends the list of names to the given string builder.
	 */
	protected static void appendBoundIdentifiersString(StringBuilder builder,
			String[] names, BoundIdentDecl[] decls, boolean withTypes) {
		
		String separator = "";
		for (int i = 0; i < names.length; ++i) {
			builder.append(separator);
			separator = ",";
			builder.append(names[i]);
			if (withTypes) {
				final BoundIdentDecl decl = decls[i];
				if (decl.isTypeChecked()) {
					builder.append(OFTYPE.getImage());
					builder.append(decl.getType());
				}
			}
		}
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
		return Formula.NO_BOUND_IDENT;
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
	
	// Sets to true the element of <code>used</code> when the corresponding
	// bound identifier occurs in the given formula.
	protected static void addUsedBoundIdentifiers(boolean[] used, Formula<?> formula) {
		final int lastIndex = used.length - 1;
		for (BoundIdentifier ident: formula.boundIdents) {
			final int index = ident.getBoundIndex();
			if (index <= lastIndex) {
				used[lastIndex - index] = true;
			}
		}
	}
	
	// Returns true if the given array contains only true elements
	protected static boolean areAllUsed(boolean[] used) {
		for (boolean element: used) {
			if (! element) {
				return false;
			}
		}
		return true;
	}
	
	protected static Predicate getWDSimplifyQ(FormulaFactory formulaFactory,
			int quant, BoundIdentDecl[] decls, Predicate pred,
			SourceLocation loc) {
		
		if (pred.getTag() == Formula.BTRUE)
			return pred;
		
		final boolean[] used = new boolean[decls.length];
		addUsedBoundIdentifiers(used, pred);
		if (! areAllUsed(used)) {
			BoundIdentDeclRemover subst = 
				new BoundIdentDeclRemover(decls, used, formulaFactory);
			final List<BoundIdentDecl> newDecls = subst.getNewDeclarations();
			final Predicate newPred = pred.rewrite(subst);
			if (newDecls.size() == 0) {
				return newPred;
			}
			return formulaFactory.makeQuantifiedPredicate(quant,
					newDecls,
					newPred,
					loc);
		}
		return formulaFactory.makeQuantifiedPredicate(quant, decls, pred, loc);
	}
	

}
