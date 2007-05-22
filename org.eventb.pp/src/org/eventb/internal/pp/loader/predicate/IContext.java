/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;

/**
 * This is the interface for the builder context. It is updated each time
 * {@link ILiteralBuilder#build(org.eventb.core.ast.Formula)} is called. It is 
 * used to retrieve the list of all labels and normalized formulas that are 
 * available after the first phase of the normalization process.
 *
 * @author François Terrier
 * 
 */
public interface IContext {

	/**
	 * Returns all the signatures (predicates, equalities, arithmetic ...) 
	 * produced by the normalizing process until now.
	 * 
	 * @return all the signatures created by the normalizer. The list can
	 * be empty.
	 */
//	public Collection<ISubFormula> getAllSignatures();
	
	public Collection<PredicateDescriptor> getAllPredicateDescriptors();
	
	/**
	 * Returns all the signatures produced by the normalizer that can be
	 * transformed to labels. It returns quantified literals and complex
	 * formulas.
	 * 
	 * @return 
	 */
	public Collection<LiteralDescriptor> getAllDescriptors();
	
	/**
	 * Returns a list containing the normalized formula of each formula 
	 * appearing in the sequent.
	 * 
	 * @return a list with all normalized formulas of the sequent
	 */
	public List<INormalizedFormula> getResults();
	
	public int getNextLiteralIdentifier();
	
	public int getNumberOfVariables();
	
	public int getFreshVariable();
}
