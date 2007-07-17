/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * {@link INormalizedFormula} is the interface for a normalized formula.
 *
 * @author François Terrier
 *
 */
public interface INormalizedFormula {

	/**
	 * Returns the signature of this formula.
	 * 
	 * @return the signature of this formula
	 */
	public SignedFormula<?> getSignature();
	
	/**
	 * Returns the list of terms appearing in this formula.
	 * 
	 * @return the list of terms appearing in this formula
	 */
	public List<TermSignature> getTerms();
	
	
	public Predicate getOriginalPredicate();

	public boolean isGoal();
	
}
