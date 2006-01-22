/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Substitution of some locally bound identifiers by arbitrary expressions. Some
 * bound identifiers might not be substituted, in which case they must be
 * renumbered.
 * 
 * @author Laurent Voisin
 * 
 */
public class BoundIdentSubstitution extends Substitution {

	// New declarations to use after substitution
	final List<BoundIdentDecl> newDecls;
	
	// Size of the new declarations
	final int newDeclSize;
	
	// Reversed array of substitutes
	final Substitute[] substitutes;
	
	/**
	 * Creates a new substitution for bound identifiers.
	 * <p>
	 * Both arrays must have the same length and describe the substitution (a
	 * declaration is substituted by the expression of the same index).
	 * </p>
	 * 
	 * @param decls
	 *            array of bound identifier declarations
	 * @param exprs
	 *            array of expressions to substitute. Can contain
	 *            <code>null</code> elements to indicate that a bound
	 *            identifier is not substituted. Must have same length as
	 *            <code>decls</code>.
	 * @param ff
	 *            factory to use for building the result
	 */
	public BoundIdentSubstitution(BoundIdentDecl[] decls, Expression[] exprs,
			FormulaFactory ff) {

		super(ff);
		assert decls.length == exprs.length;

		final int lastIndex = decls.length - 1;
		substitutes = new Substitute[exprs.length];
		newDecls = new ArrayList<BoundIdentDecl>(decls.length);
		int newIndex = 0;
		// We need to traverse both arrays backward to compute the new indexes
		// of the identifiers that are not substituted. The array of new
		// declarations is built in reverse order.
		for (int i = 0; i <= lastIndex; i++) {
			Expression expr = exprs[lastIndex - i];
			if (expr == null) {
				substitutes[i] = Substitute.makeSubstitute(newIndex ++, ff);
				newDecls.add(decls[lastIndex - i]);
			}
		}
		assert newIndex == newDecls.size();
		this.newDeclSize = newIndex;
		Collections.reverse(newDecls);
		
		// Now we know the systematic bias for the substituted expressions and
		// can construct them.
		for (int i = 0; i <= lastIndex; i++) {
			Expression expr = exprs[lastIndex - i];
			if (expr != null) {
				substitutes[i] = Substitute.makeSubstitute(expr, newDeclSize, ff);
			}
		}
	}

	@Override
	public Expression getReplacement(FreeIdentifier ident) {
		return ident;
	}

	@Override
	public Expression getReplacement(BoundIdentifier ident) {
		final int index = ident.getBoundIndex();
		// Locally bound identifier ?
		if (index < nbOfInternallyBound)
			return ident;
		// Substituted identifier ?
		final int rootIndex = index - nbOfInternallyBound;
		if (rootIndex < substitutes.length)
			return substitutes[rootIndex].getSubstitute(ident, nbOfInternallyBound);
		// Externally bound identifier
		final int newIndex = index - substitutes.length + newDecls.size();
		return ff.makeBoundIdentifier(newIndex, ident.getSourceLocation());
	}

	public List<BoundIdentDecl> getNewDeclarations() {
		return newDecls;
	}
	
}
