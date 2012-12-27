/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;

/**
 * Base interface for a clause inferrer. The clause(s) inferred
 * by a clause inferrer must have disjoint sets of variables, i.e.
 * none of the variables of the original clause must be present in
 * the result of an inferrence.
 *
 * @author François Terrier
 *
 */
public interface IInferrer {

	/**
	 * Infers from an equivalence clause.
	 * 
	 * @param clause the clause from which to infer
	 */
	public void inferFromEquivalenceClause(EquivalenceClause clause);
	
	/**
	 * Infers from a disjunctive clause.
	 * 
	 * @param clause the clause from which to infer
	 */
	public void inferFromDisjunctiveClause(DisjunctiveClause clause);
	
}
