package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public abstract class IndexedDescriptor extends LiteralDescriptor {

	protected int index;
	
	public int getIndex() {
		return index;
	}

	public IndexedDescriptor(IContext context, int index) {
		super(context);
		this.index = index;
	}

	public IndexedDescriptor(IContext context, List<IIntermediateResult> termList, int index) {
		super(context, termList);
		this.index = index;
	}
	
//	@Override
//	public int hashCode() {
//		return index;
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof IndexedDescriptor) {
//			IndexedDescriptor temp = (IndexedDescriptor) obj;
//			return temp.index == index;
//		}
//		return false;
//	}
	
}
