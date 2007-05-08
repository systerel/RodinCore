package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.tracing.IOrigin;

public interface IDispatcher {
	
	public Level getLevel();

//	public void newClause(IClause clause);
	
	
	
	public void contradiction(IOrigin origin);
	
	
	public void removeClause(IClause clause);
	
	public boolean hasStopped();
	
}
