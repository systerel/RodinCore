package org.eventb.internal.pp.core.elements;

public final class PredicateDescriptor {
	
	final private int index;
	final private boolean positive;
	
	public PredicateDescriptor(int index, boolean positive) {
		this.index = index;
		this.positive = positive;
	}
	
	public boolean isPositive() {
		return positive;
	}
	
	public int getIndex() {
		return index;
	}
	
	public PredicateDescriptor getInverse() {
		return new PredicateDescriptor(index, !positive);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateDescriptor) {
			PredicateDescriptor temp = (PredicateDescriptor) obj;
			return index == temp.index && positive == temp.positive;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 7*index + (positive?1:0);
	}
	
	@Override
	public String toString() {
		return (positive?"":"¬")+"P"+index;
	}

}
