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
package org.eventb.core.indexer;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.indexer.IDeclaration;

public class IdentTable {
	private final Map<FreeIdentifier, IDeclaration> table;

	public IdentTable() {
		this.table = new HashMap<FreeIdentifier, IDeclaration>();
	}

	public void put(FreeIdentifier ident, IDeclaration declaration) {
		table.put(ident, declaration);
	}

	public IDeclaration get(FreeIdentifier ident) {
		return table.get(ident);
	}

	public boolean contains(FreeIdentifier ident) {
		return table.containsKey(ident);
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}
}