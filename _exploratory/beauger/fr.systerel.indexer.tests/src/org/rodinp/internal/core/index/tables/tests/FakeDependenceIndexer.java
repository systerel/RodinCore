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

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class FakeDependenceIndexer extends FakeExportIndexer {

	protected final DependenceTable dependencies;
	protected final List<IRodinFile> indexingOrder;

	public FakeDependenceIndexer(RodinIndex rodinIndex,
			DependenceTable dependencies, ExportTable exportTable) {
		super(rodinIndex, exportTable);
		this.dependencies = dependencies;
		this.indexingOrder = new ArrayList<IRodinFile>();
	}

	@Override
	public IRodinFile[] getDependencies(IInternalElement root) {
		return dependencies.get(root.getRodinFile());
	}

	@Override
	public void index(IIndexingToolkit index) {
		super.index(index);
		final IRodinFile file = index.getIndexedRoot().getRodinFile();
		indexingOrder.add(file);
	}

	public IRodinFile[] getIndexingOrder() {
		return indexingOrder.toArray(new IRodinFile[indexingOrder.size()]);
	}

	public void clearOrder() {
		indexingOrder.clear();
	}

}
