package org.eventb.internal.pp.loader.formula.terms;

import org.eventb.internal.pp.core.elements.Sort;

public class TrueConstantSignature extends ConstantSignature {

	public TrueConstantSignature(Sort sort) {
		super("TRUE", sort);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof TrueConstantSignature) {
			TrueConstantSignature temp = (TrueConstantSignature) obj;
			return super.equals(temp);
		}
		return false;
	}

}
