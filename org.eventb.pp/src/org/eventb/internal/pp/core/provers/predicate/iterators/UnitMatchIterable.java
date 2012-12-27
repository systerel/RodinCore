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

import java.util.HashMap;
import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.search.RandomAccessList;
import org.eventb.internal.pp.core.search.ResetIterator;

/**
 * Match iterable that returns all clauses matching the given predicate
 * from a {@link UnitMatcher}.
 *
 * @author François Terrier
 *
 */
public class UnitMatchIterable implements IMatchIterable {

	private UnitMatcher unitMatcher;
	private HashMap<RandomAccessList<Clause>, ResetIterator<Clause>> setIteratorMap = new HashMap<RandomAccessList<Clause>, ResetIterator<Clause>>(); 

	public UnitMatchIterable(UnitMatcher unitMatcher) {
		this.unitMatcher = unitMatcher;
	}
	
	private ResetIterator<Clause> getMatchingUnitIterator(PredicateLiteralDescriptor predicate, boolean isPositive) {
		RandomAccessList<Clause> set = unitMatcher.getMatchingClauses(predicate, isPositive);
		return getIterator(set);
	}
	
	private ResetIterator<Clause> getIterator(RandomAccessList<Clause> set) {
		if (!setIteratorMap.containsKey(set)) {
			ResetIterator<Clause> iterator = set.iterator();
			setIteratorMap.put(set, iterator);
		}
		return setIteratorMap.get(set);
	}

	/**
	 * Returns an iterator over the matching unit clauses of the 
	 * predicate passed as a parameter. Two subsequent calls of
	 * this method with the same predicate return the same iterator.
	 * 
	 * @param predicate 
	 * @return 
	 * @see org.eventb.internal.pp.core.provers.predicate.iterators.IMatchIterable#iterator(org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor)
	 */
	@Override
	public Iterator<Clause> iterator(PredicateLiteralDescriptor predicate, boolean isPositive) {
		ResetIterator<Clause> iterator = getMatchingUnitIterator(predicate, isPositive);
		iterator.reset();
		return iterator;
	}

}
