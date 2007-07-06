package org.eventb.internal.pp.core.provers.equality;

import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;

public interface IInstantiationResult {

	public Set<Clause> getSolvedClauses();
	
	public EqualityLiteral getEquality();
	
	public Constant getInstantiationValue();
	
	public Set<Clause> getSolvedValueOrigin();
}
