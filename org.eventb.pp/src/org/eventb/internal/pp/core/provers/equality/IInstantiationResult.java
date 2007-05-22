package org.eventb.internal.pp.core.provers.equality;

import java.util.Set;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.terms.Constant;

public interface IInstantiationResult {

	public Set<IClause> getSolvedClauses();
	
	public IEquality getEquality();
	
	public Constant getInstantiationValue();
	
	public Set<IClause> getSolvedValueOrigin();
}
