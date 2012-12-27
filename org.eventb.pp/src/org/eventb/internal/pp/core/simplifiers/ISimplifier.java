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
package org.eventb.internal.pp.core.simplifiers;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;

/**
 * Base interface for a clause simplifier. The set of variables and local variables
 * of a simplified clause are not required to be disjoint from the original set
 * of variables. It is not required to make copies of variables before simplifying
 * a clause.
 *
 * @author François Terrier
 *
 */
public interface ISimplifier {

	/**
	 * Simplifies the specified equivalence clause.
	 * 
	 * @param clause the clause to simplify
	 * @return the simplified clause
	 */
	public Clause simplifyEquivalenceClause(EquivalenceClause clause);
	
	/**
	 * Simplifies the specified disjunctive clause.
	 * 
	 * @param clause the clause to simplify
	 * @return the simplified clause
	 */
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause);
	
	/**
	 * Returns <code>true</code> if this simplifier can simplify
	 * this clause, <code>false</code> otherwise.
	 * 
	 * @param clause the clause to test for simplification
	 * @return <code>true</code> if this simplifier can simplify the specified clause, 
	 * <code>false</code> otherwise
	 */
	public boolean canSimplify(Clause clause);
	
}
