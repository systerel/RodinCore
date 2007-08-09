/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.search;



public interface IRandomAccessIterable<T extends Object> extends Iterable<T> {

	T remove(T clause);
	
	T get(T clause);
	
	void appends(T clause);
	
	// TODO test
	boolean contains(T clause);
	
	
	void clear();
}
