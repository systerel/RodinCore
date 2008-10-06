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
package org.rodinp.internal.core.index.tables.tests;

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.rodinp.internal.core.index.tests.FakeIndexer;

public class FakeExportIndexer extends FakeIndexer {

	protected final ExportTable exportTable;

	public FakeExportIndexer(RodinIndex rodinIndex, ExportTable exportTable) {
		super(rodinIndex);
		this.exportTable = exportTable;
	}

	@Override
	public void index(IIndexingToolkit index) {
		super.index(index);
		final IRodinFile file = index.getRodinFile();
		for (IDeclaration declaration : exportTable.get(file)) {
			index.export(declaration);
		}
	}

	Set<IDeclaration> getExports(IRodinFile file) {
		return new HashSet<IDeclaration>(exportTable.get(file));
	}
}
