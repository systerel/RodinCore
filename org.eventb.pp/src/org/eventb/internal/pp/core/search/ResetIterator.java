package org.eventb.internal.pp.core.search;

import java.util.Iterator;

public interface ResetIterator<T> extends Iterator<T> {

	public void reset();
	
	public void delete();
	
}
