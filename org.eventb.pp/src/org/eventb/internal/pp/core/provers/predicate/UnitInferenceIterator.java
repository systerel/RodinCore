package org.eventb.internal.pp.core.provers.predicate;

import java.util.HashMap;
import java.util.Iterator;

import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteralDescriptor;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.search.IterableHashSet;

public class UnitInferenceIterator extends DefaultChangeListener {

	private UnitMatcher unitMatcher;
	private Iterator<IClause> matchingUnitIterator;
	
	public UnitInferenceIterator() {
		unitMatcher = new UnitMatcher();
	}
	
	private IClause unit;
	
	public IClause next() {
		IClause result;
		if (matchingUnitIterator == null || !matchingUnitIterator.hasNext()) {
			result = null;
		}
		else {
			result = matchingUnitIterator.next();
		}
		return result;
	}

	public void initialize(IClause unit) {
		this.unit = unit;
		matchingUnitIterator = unitMatcher.iterator(unit, true);
	}

	@Override
	public void removeClause(IClause clause) {
		unitMatcher.removeClause(clause);
		
		if (unit == clause) {
			unit = null;
			matchingUnitIterator = null;
		}
	}
	
	@Override
	public void newClause(IClause clause) {
		unitMatcher.newClause(clause);
	}
	
	private static class UnitMatcher extends DefaultChangeListener {
		// this class has a state
		private HashMap<ILiteralDescriptor, IterableHashSet<IClause>> positiveUnitClauseMap = new HashMap<ILiteralDescriptor, IterableHashSet<IClause>>();
		private HashMap<ILiteralDescriptor, IterableHashSet<IClause>> negativeUnitClauseMap = new HashMap<ILiteralDescriptor, IterableHashSet<IClause>>();
		
		
		public UnitMatcher() {
			
//			this.unitClauses = unitClauses;
		}
		
		// this iterator DOES take into account
		// changes in the original collection ! BUT > to test 
		public Iterator<IClause> iterator(IClause unit, boolean match) {
			return getMatchingClauses(unit, match);
		}
		
		// only way to add a unit clause
		@Override
		public void newClause(IClause clause) {
			assert clause.isUnit();
			
//			unitClauses.appends(clause);
				
			// update index, add clause to index
			addToIndex(clause);
		}
		
		// only way to remove a unit clause
		@Override
		public void removeClause(IClause clause) {
			assert clause.isUnit();
			
//			unitClauses.remove(clause);
			
			// update index, remove clause from index
			removeFromIndex(clause);
		}
		
		private Iterator<IClause> getMatchingClauses(IClause clause, boolean match) {
			assert clause.isUnit();
			
			IPredicate pred = clause.getPredicateLiterals().get(0);
			IterableHashSet<IClause> result;
			if (match?pred.isPositive():!pred.isPositive()) {
				result = negativeUnitClauseMap.get(pred.getDescriptor());
				if (result == null) result = new IterableHashSet<IClause>();
			}
			else {
				result = positiveUnitClauseMap.get(pred.getDescriptor());
				if (result == null) result = new IterableHashSet<IClause>();
			}
			return result.iterator();
		}
		
		private void addToIndex(IClause clause) {
			HashMap<ILiteralDescriptor, IterableHashSet<IClause>> map;
			if (clause.getPredicateLiterals().get(0).isPositive()) map = positiveUnitClauseMap;
			else map = negativeUnitClauseMap;
			ILiteralDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
			IterableHashSet<IClause> list = map.get(desc);
			if (list == null) {
				list = new IterableHashSet<IClause>();
				map.put(desc, list);
			}
			list.appends(clause);
		}
		
		private void removeFromIndex(IClause clause) {
			HashMap<ILiteralDescriptor, IterableHashSet<IClause>> map;
			if (clause.getPredicateLiterals().get(0).isPositive()) map = positiveUnitClauseMap;
			else map = negativeUnitClauseMap;
			ILiteralDescriptor desc = clause.getPredicateLiterals().get(0).getDescriptor();
			IterableHashSet<IClause> list = map.get(desc);
			if (list != null) list.remove(clause);
		}
	}

}
