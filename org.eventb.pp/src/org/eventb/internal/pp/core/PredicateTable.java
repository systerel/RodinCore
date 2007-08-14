/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;

public class PredicateTable {

	private HashMap<Sort, PredicateLiteralDescriptor> map = new HashMap<Sort, PredicateLiteralDescriptor>();
	private HashMap<Integer, PredicateLiteralDescriptor> integerMap = new HashMap<Integer, PredicateLiteralDescriptor>();
	
	public PredicateTable() {
		// nothing for now
	}
	
	public boolean hasDescriptor(int index) {
		return integerMap.containsKey(index);
	}
	
	public PredicateLiteralDescriptor getDescriptor(int index) {
		return integerMap.get(index);
	}
	
	public void addDescriptor(int index, PredicateLiteralDescriptor descriptor) {
		integerMap.put(index, descriptor);
	}
	
	public void addSort(Sort sort, PredicateLiteralDescriptor descriptor) {
		map.put(sort, descriptor);
	}
	
	public PredicateLiteralDescriptor getDescriptor(Sort sort) {
		return map.get(sort);
	}
	
}
