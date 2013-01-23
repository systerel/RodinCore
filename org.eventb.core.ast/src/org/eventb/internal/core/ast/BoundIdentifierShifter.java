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

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

/**
 * This substitution applies a standard offset to all externally bound
 * identifiers occurring in a formula.
 * 
 * @author Laurent Voisin
 */
public class BoundIdentifierShifter extends Substitution {

	final int offset;
	
	/**
	 * Creates a new substitution.
	 * @param offset
	 *            offset to apply to all externally bound identifiers
	 * @param ff
	 *            factory to use for building new bound identifiers
	 */
	public BoundIdentifierShifter(int offset, FormulaFactory ff) {
		super(ff);
		this.offset = offset;
	}

	@Override
	public Expression rewrite(BoundIdentifier ident) {
		final int index = ident.getBoundIndex();
		if (index < getBindingDepth() || offset == 0) {
			// Internally bound, no change
			return super.rewrite(ident);
		}
		return ff.makeBoundIdentifier(
				index + offset, 
				ident.getSourceLocation(),
				ident.getType());
	}

}
