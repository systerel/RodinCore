package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.elements.Clause;

public interface IDispatcher {
	
	public Level getLevel();

	public void nextLevel();

	public boolean contains(Clause clause);
	
//	public void contradiction(IOrigin origin);
	
//	public void removeClause(Clause clause);
	
}
