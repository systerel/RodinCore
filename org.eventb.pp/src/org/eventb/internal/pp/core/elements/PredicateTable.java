/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.elements;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public class PredicateTable implements Iterable<PredicateLiteralDescriptor> {

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
	
	public PredicateLiteralDescriptor newDescriptor(int index, 
			int arity, int realArity, boolean isLabel, boolean isMembership,
			List<Sort> sortList) {
		assert !integerMap.containsKey(index);
		return new PredicateLiteralDescriptor(index, arity, realArity, isLabel, isMembership, sortList);
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

	@Override
	public Iterator<PredicateLiteralDescriptor> iterator() {
		final Collection<PredicateLiteralDescriptor> vs = integerMap.values();
		return Collections.unmodifiableCollection(vs).iterator();
	}
	
}
