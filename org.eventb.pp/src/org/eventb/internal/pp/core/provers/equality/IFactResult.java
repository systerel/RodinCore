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

public interface IFactResult {

	public boolean hasContradiction();
	
	public List<Clause> getContradictionOrigin();
	
	public Level getContradictionLevel();
	
	// returns a list of queries if some queries are solved or null if not
	public List<? extends IQueryResult> getSolvedQueries();
	
	public List<? extends IInstantiationResult> getSolvedInstantiations();
	
}
