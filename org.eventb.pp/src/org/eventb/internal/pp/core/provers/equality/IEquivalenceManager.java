/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.equality;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;

public interface IEquivalenceManager {
	
	// here is the mapping Constant -> Node
	// mapping EqualityFormula -> Source / one source per equality
	
	// nodes must be ordered
	public void removeQueryEquality(EqualityLiteral equality, Clause clause);
	
	// returns contradiction + source and solved queries
	// or null if nothing happens
	public IFactResult addFactEquality(EqualityLiteral equality, Clause clause);
	
	// returns solved query
	// or null if nothing happens
	public IQueryResult addQueryEquality(EqualityLiteral equality, Clause clause);
	
	// backtrack up to/exclusive level
	public void backtrack(Level level);

	
	public List<? extends IInstantiationResult> addInstantiationEquality(EqualityLiteral equality, Clause clause);
	
	public void removeInstantiation(EqualityLiteral equality, Clause clause);
}