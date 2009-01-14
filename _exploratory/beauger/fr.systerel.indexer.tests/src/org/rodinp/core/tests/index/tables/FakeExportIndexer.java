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
package org.rodinp.core.tests.index.tables;

import java.util.HashSet;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingBridge;
import org.rodinp.core.tests.index.FakeIndexer;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class FakeExportIndexer extends FakeIndexer {

	protected final ExportTable exportTable;

	public FakeExportIndexer(RodinIndex rodinIndex, ExportTable exportTable) {
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
