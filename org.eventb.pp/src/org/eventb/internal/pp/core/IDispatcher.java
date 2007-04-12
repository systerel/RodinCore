package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.elements.IClause;

public interface IDispatcher {
	
	public Level getLevel();

	public void newClause(IClause clause);
	
	public boolean hasStopped();
	
}
