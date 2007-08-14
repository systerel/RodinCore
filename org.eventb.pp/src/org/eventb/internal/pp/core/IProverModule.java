/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;

public interface IProverModule {
	
	// can return true/false or a clause or null
	// if it cannot infer a new clause
	// never calls IDispatcher.contradiction
	// those clauses are not simplified
	
	public ProverResult next(boolean force);

//	public boolean isSubsumed(Clause clause);

	public ProverResult addClauseAndDetectContradiction(Clause clause);

	public void removeClause(Clause clause);
	
	public void initialize(ClauseSimplifier simplifier);
	
	public void contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies);
	
	public void registerDumper(Dumper dumper);
	
}
