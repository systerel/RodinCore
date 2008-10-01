/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.tables;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

public class ExportTable {

	Map<IRodinFile, Map<IInternalElement, String>> table;

	public ExportTable() {
		table = new HashMap<IRodinFile, Map<IInternalElement, String>>();
	}

	// TODO consider providing a type that hides the map
	public Map<IInternalElement, String> get(IRodinFile file) {
		final Map<IInternalElement, String> map = table.get(file);
		if (map == null) {
			return new HashMap<IInternalElement, String>();
		}
		return new HashMap<IInternalElement, String>(map);
	}

	// Overwrites any previous mapping from the given file to the element,
	// and from the given element to the name.
	public void add(IRodinFile file, IInternalElement element, String name) {
		Map<IInternalElement, String> map = table.get(file);
		if (map == null) {
			map = new HashMap<IInternalElement, String>();
			table.put(file, map);
		}
		map.put(element, name);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

	public boolean contains(IRodinFile f, IInternalElement element) {
		final Map<IInternalElement, String> map = table.get(f);
		if (map == null) {
			return false;
		}
		return map.keySet().contains(element);
	}

}
