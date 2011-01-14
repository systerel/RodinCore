/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Relation<T> {
	protected final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

	public Relation() {
		// avoid synthetic accessor emulation
	}
	
	public Map<T, Set<T>> getRelationMap() {
		return Collections.unmodifiableMap(maplets);
	}
	
	public void add(T a, T b) {
		Set<T> set = maplets.get(a);
		if (set == null) {
			set = new HashSet<T>();
			maplets.put(a, set);
		}
		set.add(b);
	}

	public boolean contains(T a, T b) {
		Set<T> set = maplets.get(a);
		if (set == null) {
			return false;
		}
		return set.contains(b);
	}

	@Override
	public String toString() {
		return maplets.toString();
	}
}