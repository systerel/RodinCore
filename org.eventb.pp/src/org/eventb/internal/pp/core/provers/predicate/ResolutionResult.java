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
 * Result of a resolution step.
 *
 * @author François Terrier
 *
 */
public class ResolutionResult {

	private Clause derivedClause;
	private Clause subsumedClause;
	
	public ResolutionResult(Clause derivedClause, Clause subsumedClause) {
		this.derivedClause = derivedClause;
		this.subsumedClause = subsumedClause;
	}
	
	/**
	 * The derived clause. Is never <code>null</code>.
	 * 
	 * @return the derived clause
	 */
	public Clause getDerivedClause() {
		return derivedClause;
	}
	
	/**
	 * The subsumed clause or <code>null</code> if no clause is subsumed.
	 * 
	 * @return the subsumed clause or <code>null</code>
	 */
	public Clause getSubsumedClause() {
		return subsumedClause;
	}	
	
}
