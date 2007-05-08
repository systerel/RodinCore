package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.provers.NonUnitMatcher;
import org.eventb.internal.pp.core.search.ResetIterator;

public class NonUnitInferenceIterator extends DefaultChangeListener {
	
	private NonUnitMatcher nonUnitMatcher;
	private Iterator<IClause> unitClauses;
	private Iterator<IClause> matchingNonUnitIterator;
	
	private IClause currentMatchingNonUnit;
	private IClause currentMatchingUnit;
	
	public NonUnitInferenceIterator(Iterator<IClause> unitClauses,
			ResetIterator<IClause> nonUnitClauses) {
		this.unitClauses = unitClauses;
		this.nonUnitMatcher = new NonUnitMatcher(nonUnitClauses);
	}
	
	public IClause getMatchingUnit() {
		return currentMatchingUnit;
	}
	
	public IClause getMatchingNonUnit() {
		return currentMatchingNonUnit;
	}
	

	public boolean next() {
		if (!nextMatchingPair()) {
			currentMatchingNonUnit = null;
			currentMatchingUnit = null;
			return false;
		}
		return true;
	}
	
	private boolean nextMatchingNonUnit() {
		if (matchingNonUnitIterator == null) return false;
		if (!matchingNonUnitIterator.hasNext()) return false;
		currentMatchingNonUnit = matchingNonUnitIterator.next();
		return true;
	}
	
	private boolean nextMatchingUnit() {
		if (unitClauses.hasNext()) {
			currentMatchingUnit = unitClauses.next();
			// TODO delete the old iterator. or implement a reset mechanism
			matchingNonUnitIterator = nonUnitMatcher.iterator(currentMatchingUnit.getPredicateLiterals().get(0),true);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean nextMatchingPair() {
		if (currentMatchingUnit != null && nextMatchingNonUnit()) return true;
		else {
			return ( !nextMatchingUnit() ) ? false : nextMatchingPair();
		}
	}
	
	@Override
	public void removeClause(IClause clause) {
		if (currentMatchingNonUnit == clause) {
			currentMatchingNonUnit = null;
		}
		if (currentMatchingUnit == clause) {
			currentMatchingNonUnit = null;
			currentMatchingUnit = null;
			matchingNonUnitIterator = null;
		}
	}
	
	@Override
	public void newClause(IClause clause) {
		// do nothing
	}
	
}
