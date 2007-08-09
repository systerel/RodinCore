/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.pp;

import java.util.List;

import org.eventb.core.ast.Predicate;

public interface ITracer {

//	public abstract List<Clause> getClauses();

	public List<Predicate> getOriginalPredicates();

	public boolean isGoalNeeded();
	
}