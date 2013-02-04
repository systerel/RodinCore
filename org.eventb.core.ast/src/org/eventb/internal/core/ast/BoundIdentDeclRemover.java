/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - always rewrite leaf node when factory changed
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

/**
 * A substitution that removes some bound identifier declarations, renumbering
 * the remaining bound identifiers corresponding to kept declaration.
 * 
 * @author Laurent Voisin
 * 
 */
public class BoundIdentDeclRemover extends Substitution {

	// New declarations to use after substitution
	protected final List<BoundIdentDecl> newDecls;
	
	// Reversed array of substitutes
	protected final Substitute[] substitutes;
	
	/**
	 * Creates a new substitution for removing some bound identifier
	 * declarations.
	 * <p>
	 * Both arrays must have the same length and describe the substitution (a
	 * declaration is kept only is the second array contains <code>true</code>
	 * at the same index).
	 * </p>
	 * 
	 * @param decls
	 *            array of bound identifier declarations
	 * @param keep
	 *            array of booleans. A <code>true</code> element indicates that
	 *            the corresponding declaration is kept, <code>false</code> denotes
	 *            a declaration that is removed
	 * @param ff
	 *            factory to use for building the result
	 */
	public BoundIdentDeclRemover(BoundIdentDecl[] decls, boolean[] keep,
			FormulaFactory ff) {

		super(ff);
		assert decls.length == keep.length;

		final int lastIndex = decls.length - 1;
		substitutes = new Substitute[keep.length];
		newDecls = new ArrayList<BoundIdentDecl>(decls.length);
		int newIndex = 0;
		// We need to traverse both arrays backward to compute the new indexes
		// of the identifiers that are not removed. The array of new
		// declarations is built in reverse order.
		for (int i = 0; i <= lastIndex; i++) {
			if (keep[lastIndex - i]) {
				substitutes[i] = Substitute.makeSubstitute(newIndex ++, ff);
				newDecls.add(decls[lastIndex - i]);
			}
		}
		assert newIndex == newDecls.size();
		Collections.reverse(newDecls);
	}

	@Override
	public Expression rewrite(BoundIdentifier ident) {
		final int nbOfInternallyBound = getBindingDepth();
		final int index = ident.getBoundIndex();
		// Locally bound identifier ?
		if (index < nbOfInternallyBound) {
			return super.rewrite(ident);
		}
		// Substituted identifier ?
		final int rootIndex = index - nbOfInternallyBound;
		if (rootIndex < substitutes.length) {
			assert substitutes[rootIndex] != null :
				"Should not substitute a removed identifier";
			return substitutes[rootIndex].getSubstitute(ident, nbOfInternallyBound);
		}
		// Externally bound identifier
		final int newIndex = index - substitutes.length + newDecls.size();
		if (newIndex == index) {
			return super.rewrite(ident);
		} else {
			return ff.makeBoundIdentifier(newIndex, ident.getSourceLocation(),
					typeRewriter.rewrite(ident.getType()));
		}
	}

	public List<BoundIdentDecl> getNewDeclarations() {
		return newDecls;
	}

}
