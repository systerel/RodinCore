/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.ast;

import java.util.Collection;
import java.util.HashMap;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Substitution that binds the free identifiers passed to the constructor.
 * 
 * @author Laurent Voisin
 */
public class BindingSubstitution extends SimpleSubstitution {
	
	// Offset introduced by the identifiers bound by this substitution
	final int offset;

	/**
	 * Creates a binding substitution where the given free identifiers become
	 * bound.
	 * 
	 * @param ff
	 *            factory to use for building substitutes
	 */
	public BindingSubstitution(Collection<FreeIdentifier> identsToBind,
			FormulaFactory ff) {
		
		super(ff);
		this.offset = identsToBind.size();
		map = new HashMap<FreeIdentifier, Substitute>(offset * 4 / 3 + 1);

		int index = offset - 1;
		for (FreeIdentifier ident : identsToBind) {
			map.put(ident, Substitute.makeSubstitute(index--, ff));
		}
	}

	@Override
	public Expression getReplacement(BoundIdentifier ident) {
		final int index = ident.getBoundIndex();
		if (index < nbOfInternallyBound)
			return ident;
		return ff.makeBoundIdentifier(
				index + offset, 
				ident.getSourceLocation(),
				ident.getType());
	}
	
}
