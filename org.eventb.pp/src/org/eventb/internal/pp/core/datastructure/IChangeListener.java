package org.eventb.internal.pp.core.datastructure;

import org.eventb.internal.pp.core.elements.Clause;

public interface IChangeListener {

	// TODO delete this class
	
	public void removeClause(Clause clause);
	
	public void newClause(Clause clause);
	
}
