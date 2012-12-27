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
package org.eventb.internal.pp.core.provers.predicate;

import org.eventb.internal.pp.core.elements.Clause;

/**
 * Interface for resolvers.
 *
 * @author François Terrier
 *
 */
public interface IResolver {

	/**
	 * Returns the result of this resolution step or <code>null</code>
	 * if no more resolution can be applied.
	 * 
	 * @return the result of this resolution step or <code>null</code>
	 * if no more resolution can be applied
	 */
	public ResolutionResult next();	

	/**
	 * Returns <code>true</code> if this resolver is initialized with a
	 * clause, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this resolver is initialized with a
	 * clause, <code>false</code> otherwise
	 */
	public boolean isInitialized();
	
	/**
	 * Initialize this resolver with the given clause.
	 * 
	 * @param matcher the clause
	 */
	public void initialize(Clause clause);
	
}
