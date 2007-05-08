package org.eventb.internal.pp.core.provers.equality;

import java.util.List;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;

public interface IQueryResult {

	public boolean getValue();
	
	public List<IClause> getSolvedValueOrigin();
	
	public List<IClause> getSolvedClauses();

	public IEquality getEquality();
}
