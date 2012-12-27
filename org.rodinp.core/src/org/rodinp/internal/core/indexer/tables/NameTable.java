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
package org.rodinp.internal.core.indexer.tables;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.indexer.IDeclaration;

public class NameTable {

	private Map<String, Set<IDeclaration>> table;

	public NameTable() {
		table = new HashMap<String, Set<IDeclaration>>();
	}

	private void add(String name, IDeclaration declaration) {
		Set<IDeclaration> declarations = table.get(name);
		if (declarations == null) {
			declarations = new HashSet<IDeclaration>();
			table.put(name, declarations);
		}
		declarations.add(declaration);
	}

	public void add(IDeclaration declaration) {
		add(declaration.getName(), declaration);
	}

	private void remove(String name, IDeclaration declaration) {
		Set<IDeclaration> declarations = table.get(name);
		if (declarations != null) {
			declarations.remove(declaration);
			if (declarations.size() == 0) {
				table.remove(name);
			}
		}
	}

	public void remove(IDeclaration declaration) {
		remove(declaration.getName(), declaration);
	}

	public Set<IDeclaration> getDeclarations(String name) {
		final Set<IDeclaration> declarations = table.get(name);
		if (declarations == null || declarations.size() == 0) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(declarations);
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
			for (IDeclaration decl : table.get(name)) {
				sb.append(decl.getElement().getElementName() + "; ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
