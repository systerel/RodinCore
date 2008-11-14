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
import org.rodinp.core.IRodinFile;

public class FileTable {

	private Map<IRodinFile, Set<IInternalElement>> table;

	private static final IInternalElement[] NO_ELEMENTS =
			new IInternalElement[0];

	public FileTable() {
		table = new HashMap<IRodinFile, Set<IInternalElement>>();
	}

	public IInternalElement[] get(IRodinFile file) {
		final Set<IInternalElement> elements = table.get(file);
		if (elements == null || elements.size() == 0) {
			return NO_ELEMENTS;
		}
		return elements.toArray(new IInternalElement[elements.size()]);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

	public void add(IRodinFile file, IInternalElement element) {
		Set<IInternalElement> elements = table.get(file);
		if (elements == null) {
			elements = new HashSet<IInternalElement>();
			table.put(file, elements);
		}
		elements.add(element);
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("File Table\n");
		for (IRodinFile file : table.keySet()) {
			sb.append(file.getBareName() + ": ");
			for (IInternalElement elem : table.get(file)) {
				sb.append(elem.getElementName() + "; ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean contains(IRodinFile file, IInternalElement element) {
		final Set<IInternalElement> set = table.get(file);
		if (set == null) {
			return false;
		}
		return set.contains(element);
	}
}
