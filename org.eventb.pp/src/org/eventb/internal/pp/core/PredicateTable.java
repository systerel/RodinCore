/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.Sort;

public class PredicateTable {

	private HashMap<Sort, Integer> map = new HashMap<Sort, Integer>();
	
	public PredicateTable() {
		// nothing for now
	}
	
	public void addCompletePredicate(Sort sort, Integer index) {
		map.put(sort, index);
	}

	public boolean hasPredicateForSort(Sort sort) {
		return map.containsKey(sort);
	}
	
	public int getIndexForSort(Sort sort) {
		return map.get(sort);
	}
	
}
