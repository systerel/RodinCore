package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.tracing.IOrigin;

public interface IDispatcher {
	
	// remove this
	public Level getLevel();

	public void contradiction(IOrigin origin);
	
//	public void removeClause(IClause clause);
	
}
