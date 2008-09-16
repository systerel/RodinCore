package org.rodinp.internal.core.index.tables.tests;

import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;

public class FakeExportIndexer implements IIndexer {

	private final Map<IInternalElement, String> exports;

	public FakeExportIndexer(Map<IInternalElement, String> elements) {
		this.exports = elements;
	}

	public void index(IRodinFile file, IIndexingFacade index) {
		for (IInternalElement elt : exports.keySet()) {
			if (elt.getRodinFile().equals(file)) {
				index.declare(elt, exports.get(elt));
			}
			index.export(elt);
		}
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return new IRodinFile[0];
	}

	Map<IInternalElement, String> getExports() {
		return new HashMap<IInternalElement, String>(exports);
	}
}
