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
package org.eventb.internal.core.ast;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

/**
 * Substitution of some locally bound identifiers by arbitrary expressions. Some
 * bound identifiers might not be substituted, in which case they must be
 * renumbered.
 * 
 * @author Laurent Voisin
 * 
 */
public class BoundIdentSubstitution extends BoundIdentDeclRemover {

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

		super(decls, make_keep(exprs), ff);

		final int lastIndex = decls.length - 1;
		int newDeclSize = newDecls.size();
		
		// Now we build the substituted expressions, applying to them the
		// systematic bias introduced by the removal of the declarations of
		// substituted identifiers.
		for (int i = 0; i <= lastIndex; i++) {
			Expression expr = exprs[lastIndex - i];
			if (expr != null) {
				substitutes[i] = Substitute.makeSubstitute(expr, newDeclSize, ff);
			}
		}
	}

	private static boolean[] make_keep(Expression[] exprs) {
		final int length = exprs.length;
		final boolean[] result = new boolean[length];
		for (int i = 0; i < length; i++) {
			result[i] = (exprs[i] == null);
		}
		return result;
	}

}
