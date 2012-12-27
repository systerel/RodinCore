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
package org.rodinp.core.tests.indexer.tables;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.internal.core.indexer.tables.IExportTable;
import org.rodinp.internal.core.indexer.tables.IRodinIndex;

public class FakeDependenceIndexer extends FakeExportIndexer {

	protected final DependenceTable dependencies;
	protected final List<IRodinFile> indexingOrder;

	public FakeDependenceIndexer(IRodinIndex rodinIndex,
			DependenceTable dependencies, IExportTable exportTable) {
		super(rodinIndex, exportTable);
		this.dependencies = dependencies;
		this.indexingOrder = new ArrayList<IRodinFile>();
	}

	@Override
	public IRodinFile[] getDependencies(IInternalElement root) {
		return dependencies.get(root.getRodinFile());
	}

	@Override
	public boolean index(IIndexingBridge bridge) {
		final boolean success = super.index(bridge);
		if (!success) {
			return false;
		}
		final IRodinFile file = bridge.getRootToIndex().getRodinFile();
		indexingOrder.add(file);
		return true;
	}

	public IRodinFile[] getIndexingOrder() {
		return indexingOrder.toArray(new IRodinFile[indexingOrder.size()]);
	}

	public void clearOrder() {
		indexingOrder.clear();
	}

}
