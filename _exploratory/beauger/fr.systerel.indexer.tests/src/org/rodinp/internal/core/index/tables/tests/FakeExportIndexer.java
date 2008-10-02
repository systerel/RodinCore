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
	public void index(IRodinFile file, IIndexingToolkit index) {
		super.index(file, index);
		for (IDeclaration declaration : exportTable.get(file)) {
			index.export(declaration.getElement());
		}
	}

	Set<IDeclaration> getExports(IRodinFile file) {
		return new HashSet<IDeclaration>(exportTable.get(file));
	}
}
