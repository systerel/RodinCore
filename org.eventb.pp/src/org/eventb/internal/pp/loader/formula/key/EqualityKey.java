package org.eventb.internal.pp.loader.formula.key;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

public class EqualityKey extends SymbolKey<EqualityDescriptor> {

	private Sort sort;
	
	public EqualityKey(Sort sort) {
		this.sort = sort;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof EqualityKey) {
			EqualityKey temp = (EqualityKey) obj;
			return sort.equals(temp.sort);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return "E".hashCode() * 31 + sort.hashCode();
	}
	
	@Override
	public String toString() {
		return sort.toString();
	}

	@Override
	public EqualityDescriptor newDescriptor(IContext context) {
		return new EqualityDescriptor(context, sort);
	}

}
