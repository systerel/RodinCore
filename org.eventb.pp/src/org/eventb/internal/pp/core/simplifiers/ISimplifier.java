package org.eventb.internal.pp.core.simplifiers;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;

public interface ISimplifier {

	public IClause simplifyEquivalenceClause(PPEqClause clause);
	
	public IClause simplifyDisjunctiveClause(PPDisjClause clause);
	
	public boolean canSimplify(IClause clause);
	
}
