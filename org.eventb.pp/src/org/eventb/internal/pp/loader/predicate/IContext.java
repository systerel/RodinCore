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

import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;

/**
 * This is the interface for the builder context. It is updated each time
 * {@link ILiteralBuilder#build(org.eventb.core.ast.Formula)} is called. It is 
 * used to retrieve the list of all labels and normalized formulas that are 
 * available after the first phase of the normalization process.
 * <p>
 * TODO review this interface
 *
 * @author François Terrier
 * 
 */
public interface IContext {

	/**
	 * Returns all predicate descriptors produced by the normalization process.
	 * <p>
	 * TODO this method should not be in this interface
	 * 
	 * @return all predicate descriptors produced by the normalization process
	 */
	Collection<PredicateDescriptor> getAllPredicateDescriptors();
	
	/**
	 * Returns all descriptors.
	 * <p>
	 * TODO this method should not be in this interface
	 * 
	 * @return all descriptors
	 */
	Collection<LiteralDescriptor> getAllDescriptors();
	
	/**
	 * Returns a list containing the normalized formula of each formula 
	 * appearing in the sequent.
	 * 
	 * @return a list with all normalized formulas of the sequent
	 */
	List<INormalizedFormula> getResults();
	
	/**
	 * Returns the next literal identifier.
	 * <p>
	 * Use to create new {@link IndexedDescriptor}.
	 * 
	 * @return the next literal identifier
	 */
	int getNextLiteralIdentifier();
	
	/**
	 * Returns a fresh variable index.
	 * 
	 * @return a fresh variable index
	 */
	int getFreshVariableIndex();
}
