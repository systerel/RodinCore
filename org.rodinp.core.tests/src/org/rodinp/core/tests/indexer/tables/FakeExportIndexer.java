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

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.tests.indexer.FakeIndexer;
import org.rodinp.internal.core.indexer.tables.IExportTable;
import org.rodinp.internal.core.indexer.tables.IRodinIndex;

public class FakeExportIndexer extends FakeIndexer {

	protected final IExportTable exportTable;

	public FakeExportIndexer(IRodinIndex rodinIndex, IExportTable exportTable) {
		super(rodinIndex);
		this.exportTable = exportTable;
	}

	@Override
	public boolean index(IIndexingBridge bridge) {
		final boolean success = super.index(bridge);
		if (!success) {
			return false;
		}
		final IRodinFile file = bridge.getRootToIndex().getRodinFile();
		for (IDeclaration declaration : exportTable.get(file)) {
			bridge.export(declaration);
		}
		return true;
	}

	Set<IDeclaration> getExports(IRodinFile file) {
		return new HashSet<IDeclaration>(exportTable.get(file));
	}
}
