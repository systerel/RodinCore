package org.eventb.internal.pp.core.datastructure;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.search.ResetIterator;

public interface IObservable {

	// TODO delete this class
	
	public void addChangeListener(IChangeListener listener);
	
	// TODO remove this from interface
	public ResetIterator<Clause> iterator();
	
}
