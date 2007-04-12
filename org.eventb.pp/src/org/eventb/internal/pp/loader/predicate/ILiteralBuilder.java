/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;

/**
 * {@link ILiteralBuilder} is the base interface for the normalizer. It is
 * intended to be used to normalize each formula of a particular sequent, by
 * calling {@link ILiteralBuilder#build(Formula)} for each predicate. The result
 * of the normalization can be retrieved from the {@link IContext}.
 *
 * This interface is used to perform the first phase of the normalization of a 
 * formula. The second phase is done by {@link ClauseBuilder}.
 *
 * @author François Terrier
 *
 */
public interface ILiteralBuilder {

	/**
	 * Extends the current context with the normalization of this formula.
	 * After calling this method, the context (retrieved using {@link
	 * ILiteralBuilder#getContext()}) is extending with the labels and the
	 * normalized form of the formula passed as parameter.
	 * 
	 * @param formula the formula to be normalized
	 */
	public void build(Predicate formula, boolean isGoal);

	/**
	 * Returns the context associated with this builder. For a particular
	 * instance of this interface, this method always returns the same
	 * instance of the context.
	 * 
	 * @return the contxt associated with this builder
	 */
	public IContext getContext();
	
}
