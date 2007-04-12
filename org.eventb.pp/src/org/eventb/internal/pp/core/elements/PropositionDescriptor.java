package org.eventb.internal.pp.core.elements;


public class PropositionDescriptor implements ILiteralDescriptor {

	private int index;
	
	public PropositionDescriptor(int index) {
		this.index = index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PropositionDescriptor) {
			PropositionDescriptor temp = (PropositionDescriptor) obj;
			return index == temp.index;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return PropositionDescriptor.class.hashCode()*31 + index;
	}

	
}
