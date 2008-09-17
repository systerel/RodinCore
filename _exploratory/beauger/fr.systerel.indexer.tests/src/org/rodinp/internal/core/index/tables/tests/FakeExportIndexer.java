package org.rodinp.internal.core.index.tables.tests;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.internal.core.index.RodinIndex;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tests.FakeIndexer;

public class FakeExportIndexer extends FakeIndexer {

	protected final ExportTable exportTable;

	public FakeExportIndexer(RodinIndex rodinIndex, ExportTable exportTable) {
		super(rodinIndex);
		this.exportTable = exportTable;
	}

	@Override
	public void index(IRodinFile file, IIndexingFacade index) {
		super.index(file, index);
		for (IInternalElement elt : exportTable.get(file).keySet()) {
			index.export(elt);
		}
	}

	Map<IInternalElement, String> getExports(IRodinFile file) {
		return new HashMap<IInternalElement, String>(exportTable.get(file));
	}
}
