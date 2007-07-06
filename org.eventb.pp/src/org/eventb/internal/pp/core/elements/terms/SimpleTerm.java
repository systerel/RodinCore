package org.eventb.internal.pp.core.elements.terms;

import java.util.Map;

import org.eventb.internal.pp.core.elements.Sort;

public abstract class SimpleTerm extends Term {

	protected SimpleTerm(Sort sort) {
		super(sort);
	}
	
	@Override
	public int hashCodeWithDifferentVariables() {
		return 1;
	}
	
	@Override
	protected <S extends Term> Term substitute(Map<SimpleTerm, S> map) {
		return map.containsKey(this)?map.get(this):this;
	}
	
	@Override
	public boolean contains(SimpleTerm variable) {
		return variable.equals(this);
	}
	
}
