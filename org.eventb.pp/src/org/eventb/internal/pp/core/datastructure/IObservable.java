package org.eventb.internal.pp.core.datastructure;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.search.ResetIterator;

public interface IObservable {

	public void addChangeListener(IChangeListener listener);
	
	// TODO maybe remove this from interface
	public ResetIterator<IClause> iterator();
	
}
