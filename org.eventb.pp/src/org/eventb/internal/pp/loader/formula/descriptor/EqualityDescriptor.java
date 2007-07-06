package org.eventb.internal.pp.loader.formula.descriptor;

import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.predicate.IContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EqualityDescriptor extends LiteralDescriptor {

	private Sort sort;
	
	public Sort getSort() {
		return sort;
	}

	public EqualityDescriptor(IContext context, Sort sort) {
		super(context);
		this.sort = sort;
	}

	public EqualityDescriptor(IContext context, List<IIntermediateResult> termList, Sort sort) {
		super(context, termList);
		this.sort = sort;
	}
	
	@Override
	public String toString() {
		return "E" + sort.toString();
	}

//	@Override
//	public int hashCode() {
//		return sort.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof EqualityDescriptor) {
//			EqualityDescriptor temp = (EqualityDescriptor) obj;
//			return super.equals(temp);
//		}
//		return false;
//	}
}
