package org.rodinp.internal.core.index.tables.tests;

import java.util.ArrayList;
import java.util.List;

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
	public IRodinFile[] getDependencies(IRodinFile file) {
		return dependencies.get(file);
	}

	@Override
	public void index(IRodinFile file, IIndexingToolkit index) {
		super.index(file, index);
		indexingOrder.add(file);
	}

	public IRodinFile[] getIndexingOrder() {
		return indexingOrder.toArray(new IRodinFile[indexingOrder.size()]);
	}

	public void clearOrder() {
		indexingOrder.clear();
	}

}
