package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;

public interface IInferrer {

	public void inferFromEquivalenceClause(PPEqClause clause);
	
	public void inferFromDisjunctiveClause(PPDisjClause clause);
	
	// TODO see what this exactly does, is it useful ?
//	public boolean canInfer(IClause clause);
	
}
