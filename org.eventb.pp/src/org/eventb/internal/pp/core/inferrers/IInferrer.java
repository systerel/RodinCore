package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;

public interface IInferrer {

	public void inferFromEquivalenceClause(PPEqClause clause);
	
	public void inferFromDisjunctiveClause(PPDisjClause clause);
	
	public boolean canInfer(IClause clause);
	
}
