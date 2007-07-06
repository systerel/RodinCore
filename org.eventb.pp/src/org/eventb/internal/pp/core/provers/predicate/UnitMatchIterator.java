package org.eventb.internal.pp.core.provers.predicate;

import java.util.HashMap;
import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateDescriptor;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;

public class UnitMatchIterator implements IMatchIterator {

	private UnitMatcher unitMatcher;
	private HashMap<IterableHashSet<Clause>, ResetIterator<Clause>> setIteratorMap = new HashMap<IterableHashSet<Clause>, ResetIterator<Clause>>(); 

	public UnitMatchIterator(UnitMatcher unitMatcher) {
		this.unitMatcher = unitMatcher;
	}
	
	private ResetIterator<Clause> getMatchingUnitIterator(PredicateDescriptor predicate) {
		IterableHashSet<Clause> set = unitMatcher.getMatchingClauses(predicate);
		return getIterator(set);
	}
	
	private ResetIterator<Clause> getIterator(IterableHashSet<Clause> set) {
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
	 * @see org.eventb.internal.pp.core.provers.predicate.IMatchIterator#iterator(org.eventb.internal.pp.core.elements.PredicateDescriptor)
	 */
	public Iterator<Clause> iterator(PredicateDescriptor predicate) {
		ResetIterator<Clause> iterator = getMatchingUnitIterator(predicate);
		iterator.reset();
		return iterator;
	}

}
