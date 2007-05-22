package org.eventb.internal.pp.core.elements;


public class PredicateDescriptor implements ILiteralDescriptor {

	final private int index;
	
	public PredicateDescriptor(int index) {
		this.index = index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateDescriptor) {
			PredicateDescriptor temp = (PredicateDescriptor) obj;
			return index == temp.index;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return PredicateDescriptor.class.hashCode()*31 + index;
	}
	
}
