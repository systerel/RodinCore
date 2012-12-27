/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Nicolas Beauger
 *
 */
public class Registry <T, U> {

	private final Map<T, List<U>> registry;

	public Registry() {
		registry = new HashMap<T, List<U>>();
	}
	
	public void add(T t, U u) {
		List<U> list = registry.get(t);
		if (list == null) {
			list = new ArrayList<U>();
			registry.put(t, list);
		}
		list.add(u);
	}
	
	public List<U> get(T t) {
		return registry.get(t);
	}
	
	public Set<Entry<T,List<U>>> entrySet() {
		return registry.entrySet();
	}
	
	public void clear() {
		registry.clear();
	}
	
	@Override
	public int hashCode() {
		return 31 + registry.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Registry<?, ?> other = (Registry<?, ?>) obj;
		if (!registry.equals(other.registry))
			return false;
		return true;
	}
	
	
}
