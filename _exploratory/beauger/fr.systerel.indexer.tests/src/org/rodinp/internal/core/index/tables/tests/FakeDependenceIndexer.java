package org.rodinp.internal.core.index.tables.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;

public class FakeDependenceIndexer implements IIndexer {

	private final DependenceTable dependencies;
	private final ExportTable exportTable;
	private final List<IRodinFile> indexingOrder;
	
	
	public FakeDependenceIndexer(DependenceTable dependencies, ExportTable exportTable) {
		this.dependencies = dependencies;
		this.exportTable = exportTable;
		this.indexingOrder = new ArrayList<IRodinFile>();
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return dependencies.get(file);
	}

	public void index(IRodinFile file, IIndexingFacade index) {
		indexingOrder.add(file);
		final Map<IInternalElement, String> exports = exportTable.get(file);
		for (IInternalElement elt : exports.keySet()) {
			if (elt.getRodinFile().equals(file)) {
				index.declare(elt, exports.get(elt));
			}
			index.export(elt);
		}
	}

	public IRodinFile[] getIndexingOrder() {
		return indexingOrder.toArray(new IRodinFile[indexingOrder.size()]);
	}
	
	public void clearOrder() {
		indexingOrder.clear();
	}

}
