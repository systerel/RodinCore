/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate;

import java.util.HashMap;

import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateDescriptor;
import org.eventb.internal.pp.core.search.IterableHashSet;

class UnitMatcher extends DefaultChangeListener {
	// this class has a state
	private HashMap<PredicateDescriptor, IterableHashSet<Clause>> unitClauseMap = new HashMap<PredicateDescriptor, IterableHashSet<Clause>>();

	public UnitMatcher(IObservable unitClauses) {
		unitClauses.addChangeListener(this);
		
//		this.unitClauses = unitClauses;
	}

//	// this iterator DOES take into account
//	// changes in the original collection ! BUT > to test 
//	public IterableHashSet<Clause> getMatchingClauses(Clause unit, boolean match) {
//		return getMatchingClauses(unit, match);
//	}

	// only way to add a unit clause
	@Override
	public void newClause(Clause clause) {
		assert clause.isUnit();

//		unitClauses.appends(clause);

		// update index, add clause to index
		addToIndex(clause);
	}

	// only way to remove a unit clause
	@Override
	public void removeClause(Clause clause) {
		assert clause.isUnit();

//		unitClauses.remove(clause);

		// update index, remove clause from index
		removeFromIndex(clause);
	}

	public IterableHashSet<Clause> getMatchingClauses(PredicateDescriptor predicate) {
		IterableHashSet<Clause> result;
		result = unitClauseMap.get(predicate.getInverse());
		if (result == null) {
			result = new IterableHashSet<Clause>();
			unitClauseMap.put(predicate.getInverse(), result);
		}
		return result;
	}

	private void addToIndex(Clause clause) {
		PredicateDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
		IterableHashSet<Clause> list = unitClauseMap.get(desc);
		if (list == null) {
			list = new IterableHashSet<Clause>();
			unitClauseMap.put(desc, list);
		}
		list.appends(clause);
	}

	private void removeFromIndex(Clause clause) {
		PredicateDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
		IterableHashSet<Clause> list = unitClauseMap.get(desc);
		if (list != null) list.remove(clause);
	}
}