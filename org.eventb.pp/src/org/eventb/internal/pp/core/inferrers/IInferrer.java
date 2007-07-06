package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;

public interface IInferrer {

	public void inferFromEquivalenceClause(EquivalenceClause clause);
	
	public void inferFromDisjunctiveClause(DisjunctiveClause clause);
	
	// TODO see what this exactly does, is it useful ?
//	public boolean canInfer(Clause clause);
	
}
