package org.eventb.internal.pp.core.elements.terms;

import java.util.Map;

import org.eventb.internal.pp.core.elements.Sort;

/**
 * INVARIANT:
 * 	- no equal variables in different clauses
 *
 * @author François Terrier
 *
 */
public abstract class AbstractVariable extends Term {

	protected AbstractVariable(Sort sort) {
		super(sort);
	}

	@Override
	public Term substitute(Map<AbstractVariable, ? extends Term> map) {
		return map.containsKey(this) ? map.get(this) : this;
	}

	@Override
	public boolean contains(AbstractVariable variables) {
		return variables.equals(this);
	}
	
	@Override
	public int hashCodeWithDifferentVariables() {
		return 1;
	}
}
