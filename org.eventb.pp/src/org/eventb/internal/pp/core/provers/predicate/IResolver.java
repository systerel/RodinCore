package org.eventb.internal.pp.core.provers.predicate;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.inferrers.InferrenceResult;

public interface IResolver {

	public InferrenceResult next(boolean force);	

	public boolean isInitialized();
	
	public void initialize(Clause matcher);
	
}
