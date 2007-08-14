/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate.iterators;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.search.IterableHashSet;

public class UnitMatcher {
	// this class has a state
	private HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>> positiveUnitClauseMap = new HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>>();
	private HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>> negativeUnitClauseMap = new HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>>();
	
//	public UnitMatcher(IObservable unitClauses) {
//		unitClauses.addChangeListener(this);
//		
////		this.unitClauses = unitClauses;
//	}

//	// this iterator DOES take into account
//	// changes in the original collection ! BUT > to test 
//	public IterableHashSet<Clause> getMatchingClauses(Clause unit, boolean match) {
//		return getMatchingClauses(unit, match);
//	}

	// only way to add a unit clause
	public void newClause(Clause clause) {
		assert clause.isUnit();

//		unitClauses.appends(clause);

		// update index, add clause to index
		addToIndex(clause);
	}

	// only way to remove a unit clause
	public void removeClause(Clause clause) {
		assert clause.isUnit();

//		unitClauses.remove(clause);

		// update index, remove clause from index
		removeFromIndex(clause);
	}

	public IterableHashSet<Clause> getMatchingClauses(PredicateLiteralDescriptor predicate, boolean isPositive) {
		HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>> map = !isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		IterableHashSet<Clause> result;
		result = map.get(predicate);
		if (result == null) {
			result = new IterableHashSet<Clause>();
			map.put(predicate, result);
		}
		return result;
	}

	private void addToIndex(Clause clause) {
		PredicateLiteralDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
		boolean isPositive = clause.getPredicateLiterals().get(0).isPositive();
		HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>> map = isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		
		IterableHashSet<Clause> list = map.get(desc);
		if (list == null) {
			list = new IterableHashSet<Clause>();
			map.put(desc, list);
		}
		list.appends(clause);
	}

	private void removeFromIndex(Clause clause) {
		PredicateLiteralDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
		boolean isPositive = clause.getPredicateLiterals().get(0).isPositive();
		HashMap<PredicateLiteralDescriptor, IterableHashSet<Clause>> map = isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		
		IterableHashSet<Clause> list = map.get(desc);
		if (list != null) list.remove(clause);
	}
}