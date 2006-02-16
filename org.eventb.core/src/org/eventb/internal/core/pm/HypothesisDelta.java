package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.pm.IHypothesisDelta;
import org.eventb.core.prover.sequent.Hypothesis;

public class HypothesisDelta implements IHypothesisDelta {
	private Collection<Hypothesis> addedToSelected;
	private Collection<Hypothesis> removedFromSelected;
	private Collection<Hypothesis> addedToCached;
	private Collection<Hypothesis> removedFromCached;
	private Collection<Hypothesis> addedToSearched;
	private Collection<Hypothesis> removedFromSearched;
	
	public HypothesisDelta(
			Collection<Hypothesis> addedToSelected,
			Collection<Hypothesis> removedFromSelected) {
		this.addedToSelected = addedToSelected;
		this.removedFromSelected = removedFromSelected;
		return;
	}
	
	public Collection<Hypothesis> getHypotheses(int place, int type) {
		switch (place){
		case SELECTED:
			if (type == ADDED) return addedToSelected;
			if (type == REMOVED) return removedFromSelected;
			break;
		case CACHED:
			if (type == ADDED) return addedToCached;
			if (type == REMOVED) return removedFromCached;
			break;
		case SEARCHED:
			if (type == ADDED) return addedToSearched;
			if (type == REMOVED) return removedFromSearched;
			break;
		
		}
		return new ArrayList<Hypothesis>();
	}

}
