/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;

/**
 * Common protocol for the normalizer. Instances of this interface are intended
 * to be used to normalize each formula of a particular sequent, by calling
 * <code>build</code> for each predicate. The result of the normalization can
 * be retrieved from the {@link IContext}.
 * <p>
 * This interface is used to perform the first phase of the normalization of a
 * formula. The second phase is done by {@link ClauseBuilder}.
 * </p>
 * 
 * @author François Terrier
 */
public interface ILiteralBuilder {

	/**
	 * Builds the given predicate and use originalPredicate as its origin.
	 * <p>
	 * If isGoal is true, the predicate will be loaded as a goal (negated).
	 * Extends the current context with the normalization of this formula.
	 * After calling this method, the context (retrieved using {@link
	 * ILiteralBuilder#getContext()}) is extending with the labels and the
	 * normalized form of the formula passed as parameter.
	 * 
	 * @param predicate the predicate to load
	 * @param originalPredicate the original predicate to use in the origin
	 * @param isGoal <code>true</code> if the predicate should be loaded as the goal
	 */
	void build(Predicate predicate, Predicate originalPredicate, boolean isGoal);
	
	/**
	 * Builds the given predicate.
	 * <p>
	 * Extends the current context with the normalization of this formula.
	 * After calling this method, the context (retrieved using {@link
	 * ILiteralBuilder#getContext()}) is extending with the labels and the
	 * normalized form of the formula passed as parameter.
	 * 
	 * @param formula the formula to be normalized
	 */
	void build(Predicate formula, boolean isGoal);

	/**
	 * Returns the context associated with this builder. For a particular
	 * instance of this interface, this method always returns the same
	 * instance of the context.
	 * 
	 * @return the contxt associated with this builder
	 */
	IContext getContext();
	
}
