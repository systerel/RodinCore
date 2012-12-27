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
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;

/**
 * Interface for query results.
 *
 * @author François Terrier
 *
 */
public interface IQueryResult {

	/**
	 * Returns whether this equality is true or false.
	 * 
	 * @return whether this equality is true or false
	 */
	public boolean getValue();
	
	/**
	 * Returns the clauses that were necessary to derive
	 * the value of the equality.
	 * 
	 * @return the clauses that were necessary to derive
	 * the value of the equality
	 */
	public List<Clause> getSolvedValueOrigin();
	
	/**
	 * Returns the clauses were the solved equality occurs.
	 * 
	 * @return the clauses were the solved equality occurs
	 */
	public Set<Clause> getSolvedClauses();

	/**
	 * Returns the solved equality.
	 * 
	 * @return the solved equality
	 */
	public EqualityLiteral getEquality();
}
