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
package org.eventb.internal.pp.core.provers.predicate.iterators;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;

/**
 * Interface of a match iterable.
 * <p>
 * The iterator returned by a call to {@link #iterator(PredicateLiteralDescriptor, boolean)}
 * iterates over all clauses the match the given {@link PredicateLiteralDescriptor}.
 *
 * @author François Terrier
 *
 */
public interface IMatchIterable {

	/**
	 * Returns an iterator that iterates over all clauses matching the given predicate.
	 * 
	 * @param predicate the predicate
	 * @param isPositive if it is a positive or a negative match
	 * @return an iterator that iterates over all clauses matching the given predicate
	 */
	public Iterator<Clause> iterator(PredicateLiteralDescriptor predicate, boolean isPositive);
	
}
