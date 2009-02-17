/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.tables;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;

public class FileTable {

	private Map<IRodinFile, Set<IDeclaration>> table;

	public FileTable() {
		table = new HashMap<IRodinFile, Set<IDeclaration>>();
	}

	public Set<IDeclaration> get(IRodinFile file) {
		final Set<IDeclaration> elements = table.get(file);
		if (elements == null || elements.size() == 0) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(elements);
	}

	public void remove(IRodinFile file) {
		table.remove(file);
	}

	public void clear() {
		table.clear();
	}

	public void add(IRodinFile file, IDeclaration declaration) {
		Set<IDeclaration> elements = table.get(file);
		if (elements == null) {
			elements = new HashSet<IDeclaration>();
			table.put(file, elements);
		}
		elements.add(declaration);
	}

	// DEBUG
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("File Table\n");
		for (IRodinFile file : table.keySet()) {
			sb.append(file.getBareName() + ": ");
			for (IDeclaration decl : table.get(file)) {
				sb.append(decl.getName() + "; ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean contains(IRodinFile file, IDeclaration declaration) {
		final Set<IDeclaration> set = table.get(file);
		if (set == null) {
			return false;
		}
		return set.contains(declaration);
	}
}
