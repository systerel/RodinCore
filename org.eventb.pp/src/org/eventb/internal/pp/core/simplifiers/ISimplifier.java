package org.eventb.internal.pp.core.simplifiers;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;

public interface ISimplifier {

	public Clause simplifyEquivalenceClause(EquivalenceClause clause);
	
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause);
	
	public boolean canSimplify(Clause clause);
	
}
