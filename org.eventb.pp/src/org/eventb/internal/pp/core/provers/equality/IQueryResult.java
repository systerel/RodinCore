package org.eventb.internal.pp.core.provers.equality;

import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;

public interface IQueryResult {

	public boolean getValue();
	
	public List<Clause> getSolvedValueOrigin();
	
	public Set<Clause> getSolvedClauses();

	public EqualityLiteral getEquality();
}
