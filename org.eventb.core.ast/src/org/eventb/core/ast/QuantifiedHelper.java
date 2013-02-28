/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - externalized wd lemmas generation
 *     Systerel - bug #3574162: AST does not compare bound ident decl types
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.List;

import org.eventb.internal.core.ast.BoundIdentDeclRemover;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;

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
	
	/*
	 * Compares two lists of bound identifier declarations. Note that the
	 * comparison is not equality, but equality modulo alpha-conversion
	 * (declaration names are not compared). This is why we cannot use regular
	 * equality {@link Object#equals(Object)}.
	 */
	protected static boolean areEqualDecls(BoundIdentDecl[] leftDecls,
			BoundIdentDecl[] rightDecls) {
		if (leftDecls.length != rightDecls.length) {
			return false;
		}
		for (int i = 0; i < leftDecls.length; i++) {
			if (!leftDecls[i].equalsWithAlphaConversion(rightDecls[i])) {
				return false;
			}
		}
		return true;
	}

	static boolean sameType(Type left, Type right) {
		return left == null ? right == null : left.equals(right);
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

	protected static BoundIdentDecl[] rewriteDecls(BoundIdentDecl[] decls,
			ITypeCheckingRewriter rewriter) {
		final int length = decls.length;
		final BoundIdentDecl[] result = new BoundIdentDecl[length];
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			result[i] = decls[i].rewrite(rewriter);
			changed |= result[i] != decls[i];
		}
		if (!changed) {
			return decls;
		}
		return result;
	}

}
