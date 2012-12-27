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
package org.eventb.internal.pp.loader.predicate;

import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class is used to represent an intermediate result for the construction
 * of a literal or subformula.
 *
 * Calls to {@link IIntermediateResult#getTerms()} and {@link IIntermediateResult#
 * getIndexes()} must always return two lists of equal size and the indexes
 * must correspond to the terms.
 *
 * @author François Terrier
 *
 */
public interface IIntermediateResult {

	/**
	 * Returns the terms currently contained in this result.
	 * 
	 * @return the list of terms contained in this result
	 */
	public List<TermSignature> getTerms();
	
	/**
	 * Returns the list of intermediate result contained in this result.
	 * 
	 * @return the list of intermediate result contained in this result
	 */
	public List<IIntermediateResult> getResultList();
	
}
