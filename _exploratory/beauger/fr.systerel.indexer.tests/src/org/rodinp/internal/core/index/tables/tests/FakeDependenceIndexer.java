package org.rodinp.internal.core.index.tables.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IndexingFacade;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;

public class FakeDependenceIndexer implements IIndexer {

	private final DependenceTable dependencies;
	private final ExportTable exports;
	private final List<IRodinFile> indexingOrder;
	
	
	public FakeDependenceIndexer(DependenceTable dependencies, ExportTable exports) {
		this.dependencies = dependencies;
		this.exports = exports;
		this.indexingOrder = new ArrayList<IRodinFile>();
	}

	public boolean canIndex(IRodinFile file) {
		return true;
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return dependencies.get(file);
	}

	public Map<IInternalElement, String> getExports(IRodinFile file) {
		return exports.get(file);
	}

	public void index(IRodinFile file, IndexingFacade index) {
		indexingOrder.add(file);
	}

	public IRodinFile[] getIndexingOrder() {
		return indexingOrder.toArray(new IRodinFile[indexingOrder.size()]);
	}
	
	public void clearOrder() {
		indexingOrder.clear();
	}
}
