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
package org.eventb.internal.pp.core.provers.equality;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;

/**
 * Interface to the equivalence manager.
 * <p>
 * The equivelance manager is responsible for storing fact and query
 * equalities and provides mechanisms to add them, remove them and 
 * backtrack to a certain level.
 *
 * @author François Terrier
 *
 */
public interface IEquivalenceManager {
	
	
	/**
	 * Removes the given query equality from the equivalence manager.
	 * 
	 * @param equality the equality to remove
	 * @param clause the clause containing the equality
	 */
	public void removeQueryEquality(EqualityLiteral equality, Clause clause);
	
	/**
	 * Adds the given fact equality to the equivalence manager. Returns
	 * the associated fact result or <code>null</code> if nothing can
	 * be derived from this new fact.
	 * <p>
	 * The given clause must be a unit clause containing the given equality 
	 * either as an equality literal or as a condition.
	 * 
	 * @param equality the fact to add
	 * @param clause the clause containing the fact.
	 * @return the fact result or <code>null</code> if nothing is derived
	 */
	public IFactResult addFactEquality(EqualityLiteral equality, Clause clause);
	
	/**
	 * Adds the given query equality to the equivalence manager. Returns
	 * the associated query result or <code>null</code> if this query
	 * cannot be solved immediately.
	 * <p>
	 * The given clause must be non-unit and must contain the given equality.
	 * 
	 * @param equality the query to add
	 * @param clause the clause containing the query
	 * @return the query result or <code>null</code> if nothing is derived
	 */
	public IQueryResult addQueryEquality(EqualityLiteral equality, Clause clause);
	
	/**
	 * Backtracks the equivalence manager to the given level (not included).
	 * <p>
	 * This backtracks all information related to the backtracked levels. It
	 * removes all facts and query equalities from those levels. It also
	 * removes instantiations from those levels.
	 * 
	 * @param level the new level
	 */
	public void backtrack(Level level);
	
	/**
	 * Adds the given instantiation to the equivalence manager. Returns
	 * the corresponding instantiation result or <code>null</code> if no
	 * instantiation is immediately found.
	 * <p>
	 * The given equality must contain exactly one variable and one 
	 * constant.
	 * 
	 * @param equality the equality to add
	 * @param clause the clause which contains the equality
	 * @return
	 */
	public List<? extends IInstantiationResult> addInstantiationEquality(EqualityLiteral equality, Clause clause);
	
	/**
	 * Removes the given instantiation from the equivalence manager.
	 * 
	 * @param equality the equality to remove
	 * @param clause the clause containing the equality
	 */
	public void removeInstantiation(EqualityLiteral equality, Clause clause);
}