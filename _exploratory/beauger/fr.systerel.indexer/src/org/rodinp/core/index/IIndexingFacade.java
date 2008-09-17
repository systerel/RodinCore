package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IIndexingFacade {

	void declare(IInternalElement element, String name);

	void addOccurrence(IInternalElement element, IOccurrenceKind kind,
			IRodinLocation location);
	
	IInternalElement[] getImports();
	
	void export(IInternalElement element);

}