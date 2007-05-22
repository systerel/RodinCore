package org.eventb.internal.pp.core.search;



public interface IRandomAccessIterable<T extends Object> extends Iterable<T> {

	T remove(T clause);
	
	T get(T clause);
	
	void appends(T clause);
	
	// TODO test
	boolean contains(T clause);
	
	
	void clear();
}
