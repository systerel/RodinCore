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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;

public class NameTable {

	private static final IInternalElement[] NO_ELEMENTS = new IInternalElement[0];

	private Map<String, Set<IInternalElement>> table;
	
	public NameTable() {
		table = new HashMap<String, Set<IInternalElement>>();
	}
	
	public void put(String name, IInternalElement element) {
		Set<IInternalElement> elements = table.get(name);
		if (elements == null) {
			elements = new HashSet<IInternalElement>();
			table.put(name, elements);
		}
		elements.add(element);
	}
	
	public void remove(String name, IInternalElement element) {
		Set<IInternalElement> elements = table.get(name);
		if (elements != null) {
			elements.remove(element);
			if (elements.size() == 0) {
				table.remove(name);
			}
		}
	}
	
	public IInternalElement[] getElements(String name) {
		final Set<IInternalElement> elements = table.get(name);
		if (elements == null || elements.size() == 0) {
			return NO_ELEMENTS;
		}
		return elements.toArray(new IInternalElement[elements.size()]);
	}
	
	public void clear() {
		table.clear();
	}
	
	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("NameTable\n");
		for (String name : table.keySet()) {
			sb.append(name + ": ");
			for (IInternalElement elem : table.get(name)) {
				sb.append(elem.getElementName()+"; ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
