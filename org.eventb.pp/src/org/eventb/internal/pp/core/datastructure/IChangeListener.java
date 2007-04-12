package org.eventb.internal.pp.core.datastructure;

import org.eventb.internal.pp.core.elements.IClause;

public interface IChangeListener {

	public void removeClause(IClause clause);
	
	public void newClause(IClause clause);
	
}
