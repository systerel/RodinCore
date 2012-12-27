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

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;

/**
 * Interface for instantiation results.
 * <p>
 * For now, instantiations are given only for equalities
 * that have exactly one constant and one variable.
 *
 * @author François Terrier
 *
 */
public interface IInstantiationResult {

	/**
	 * Returns the list of clause in which the instantiation
	 * can be applied. Corresponds to the clauses in which the
	 * equality {@link #getEquality()} appears.
	 * 
	 * @return the list of clause in which the instantiation
	 * can be applied
	 */
	public Set<Clause> getSolvedClauses();
	
	/**
	 * Returns the equality in which the instantiation can be applied.
	 * 
	 * @return the equality in which the instantiation can be applied
	 */
	public EqualityLiteral getEquality();
	
	/**
	 * Returns the value with which the variable in the equality
	 * {@link #getEquality()} can be instantiated.
	 * 
	 * @return the value with which the variable in the equality
	 * {@link #getEquality()} can be instantiated
	 */
	public Constant getInstantiationValue();
	
	/**
	 * Returns the clauses that were used to find the instantiation.
	 * 
	 * @return the clauses that were used to find the instantiation
	 */
	public Set<Clause> getSolvedValueOrigin();
	
}
