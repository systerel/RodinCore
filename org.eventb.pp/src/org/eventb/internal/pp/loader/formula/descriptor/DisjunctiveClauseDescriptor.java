package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class DisjunctiveClauseDescriptor extends IndexedDescriptor {

	public DisjunctiveClauseDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
		super(context, termList, index);
	}

	public DisjunctiveClauseDescriptor(IContext context, int index) {
		super(context, index);
	}

	@Override
	public String toString() {
		return "Ld"+index;
	}

//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof DisjunctiveClauseDescriptor) {
//			DisjunctiveClauseDescriptor temp = (DisjunctiveClauseDescriptor) obj;
//			return super.equals(temp);
//		}
//		return false;
//	}
}
