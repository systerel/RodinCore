package org.eventb.internal.pp.loader.formula.descriptor;

import org.eventb.internal.pp.loader.predicate.IContext;

public class PredicateDescriptor extends IndexedDescriptor {

//	public PredicateDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
//		super(context, termList, index);
//	}

	public PredicateDescriptor(IContext context, int index) {
		super(context, index);
	}

	@Override
	public String toString() {
		return "P"+index;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateDescriptor) {
			PredicateDescriptor temp = (PredicateDescriptor) obj;
			return super.equals(temp);
		}
		return false;
	}
	
}
