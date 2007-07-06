package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EquivalenceClauseDescriptor extends IndexedDescriptor {

	public EquivalenceClauseDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
		super(context, termList, index);
	}

	public EquivalenceClauseDescriptor(IContext context, int index) {
		super(context, index);
	}

	@Override
	public String toString() {
		return "Le"+index;
	}

//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof EquivalenceClauseDescriptor) {
//			EquivalenceClauseDescriptor temp = (EquivalenceClauseDescriptor) obj;
//			return super.equals(temp);
//		}
//		return false;
//	}
}
