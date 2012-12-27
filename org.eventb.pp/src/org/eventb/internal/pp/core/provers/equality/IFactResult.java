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

/**
 * Interface for fact results.
 * <p>
 * When adding a fact, a contradiction may be derived and/or queries may
 * be solved and/or instantiations may be triggered. In case there is a contradiction, 
 * this interface gives access to the origin of the contradiction and to its level.
 * In case there are query or instantiation results, this interface gives access to those
 * results.
 *
 * @author François Terrier
 *
 */
public interface IFactResult {

	/**
	 * Returns <code>true</code> if this fact result is a contradiction.
	 * 
	 * @return <code>true</code> if this fact result is a contradiction.
	 */
	public boolean hasContradiction();
	
	/**
	 * Returns the list of clauses that caused the contradiction.
	 * 
	 * @return the list of clauses that caused the contradiction
	 */
	public List<Clause> getContradictionOrigin();
	
	/**
	 * Returns the level of the contradiction.
	 * <p>
	 * TODO is this really necessary ? it can be computed from the 
	 * list of clauses causing the contradiction.
	 * 
	 * @return the level of the contradiction
	 */
	public Level getContradictionLevel();
	
	/**
	 * Returns the list of solved queries if this fact result
	 * contains solved queries or <code>null</code> otherwise.
	 * 
	 * @return the list of solved queries if this fact result
	 * contains solved queries or <code>null</code> otherwise
	 */
	public List<? extends IQueryResult> getSolvedQueries();
	
	/**
	 * Returns the list of possible instantiations.
	 * 
	 * @return the list of possible instantiations
	 */
	public List<? extends IInstantiationResult> getSolvedInstantiations();
	
}
