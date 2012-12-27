/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added a load() method from a simple sequent
 *******************************************************************************/
package org.eventb.internal.pp.loader.predicate;

import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;

/**
 * This is the interface for the builder context. It is updated each time
 * {@link PredicateLoader#build()} is called. It is used to retrieve the list of
 * all labels and normalized formulas that are available after the first phase
 * of the normalization process.
 * <p>
 * TODO review this interface
 * 
 * @author François Terrier
 * 
 */
public interface IContext {

	/**
	 * Loads the given sequent in this context.
	 * 
	 * @param sequent
	 *            the sequent to load
	 */
	void load(ISimpleSequent sequent);
	
	/**
	 * Loads the given predicate in this context.
	 * <p>
	 * If <code>isGoal</code> is <code>true</code>, the predicate will be
	 * loaded as a goal (negated).
	 * </p>
	 * 
	 * @param predicate
	 *            the predicate to load
	 * @param isGoal
	 *            <code>true</code> iff the predicate should be loaded as a
	 *            goal
	 */
	void load(Predicate predicate, boolean isGoal);

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
	 * Returns a list containing the normalized formula of each predicate that
	 * has been loaded into this context.
	 * 
	 * @return a list of all normalized formulas of the loaded predicates
	 */
	List<INormalizedFormula> getResults();

	/**
	 * Returns the normalized formula of the last predicate that
	 * has been loaded into this context.
	 * 
	 * @return the normalized formulas of the last loaded predicates
	 */
	INormalizedFormula getLastResult();

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
