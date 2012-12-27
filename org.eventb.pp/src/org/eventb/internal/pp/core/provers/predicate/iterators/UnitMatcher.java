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

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.search.RandomAccessList;

/**
 * This class implements an efficient way of retrieving unit clauses that
 * match a certain predicate.
 * <p>
 * Unit clauses must first be added to this matcher using the provided methods.
 *
 * @author François Terrier
 *
 */
public class UnitMatcher {
	// this class has a state
	private HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>> positiveUnitClauseMap = new HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>>();
	private HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>> negativeUnitClauseMap = new HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>>();
	
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

	public RandomAccessList<Clause> getMatchingClauses(PredicateLiteralDescriptor predicate, boolean isPositive) {
		HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>> map = !isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		RandomAccessList<Clause> result;
		result = map.get(predicate);
		if (result == null) {
			result = new RandomAccessList<Clause>();
			map.put(predicate, result);
		}
		return result;
	}

	private void addToIndex(Clause clause) {
		PredicateLiteralDescriptor desc = clause.getPredicateLiteral(0).getDescriptor();
		boolean isPositive = clause.getPredicateLiteral(0).isPositive();
		HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>> map = isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		
		RandomAccessList<Clause> list = map.get(desc);
		if (list == null) {
			list = new RandomAccessList<Clause>();
			map.put(desc, list);
		}
		list.add(clause);
	}

	private void removeFromIndex(Clause clause) {
		PredicateLiteralDescriptor desc = clause.getPredicateLiteral(0).getDescriptor();
		boolean isPositive = clause.getPredicateLiteral(0).isPositive();
		HashMap<PredicateLiteralDescriptor, RandomAccessList<Clause>> map = isPositive?positiveUnitClauseMap:negativeUnitClauseMap;
		
		RandomAccessList<Clause> list = map.get(desc);
		if (list != null) list.remove(clause);
	}
}