/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Free identifier substitution.
 * 
 * @author halstefa
 *
 */
public class SimpleSubstitution extends Substitution {
	
	protected Map<FreeIdentifier, Substitute> map;

	// Constructor for sub-classes that build directly the map.
	protected SimpleSubstitution(FormulaFactory ff) {
		super(ff);
	}

	/**
	 * Creates a new substitution as specified in the given map.
	 * 
	 * @param map
	 *            substitution to apply
	 * @param ff
	 *            factory to use for building substitute expressions
	 */
	public SimpleSubstitution(Map<FreeIdentifier, Expression> map, FormulaFactory ff) {
		super(ff);
		this.map = new HashMap<FreeIdentifier, Substitute>(map.size() * 4/3 + 1);
		for (Map.Entry<FreeIdentifier, Expression> entry : map.entrySet()) {
			FreeIdentifier ident = entry.getKey();
			Expression expr = entry.getValue();
			assert ident.getType() == null || ident.getType().equals(expr.getType());
			this.map.put(entry.getKey(), Substitute.makeSubstitute(entry.getValue(), ff));
		}
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Expression rewrite(FreeIdentifier ident) {
		Substitute repl = map.get(ident);
		if (repl == null) {
			return super.rewrite(ident);
		} 
		return repl.getSubstitute(ident, getBindingDepth());
	}

}
