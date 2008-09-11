package org.rodinp.internal.core.index.tables.tests;

import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;

public class FakeExportIndexer implements IIndexer {

	final Map<IInternalElement, String> elements;

	public FakeExportIndexer(Map<IInternalElement, String> elements) {
		this.elements = elements;
	}

	public boolean canIndex(IRodinFile file) {
		return true;
	}

	public void index(IRodinFile file, IIndexingFacade index) {
		// do nothing
	}

	public IRodinFile[] getDependencies(IRodinFile file) {
		return new IRodinFile[0];
	}

	public Map<IInternalElement, String> getExports(IRodinFile file) {
		return elements;
	}

}
