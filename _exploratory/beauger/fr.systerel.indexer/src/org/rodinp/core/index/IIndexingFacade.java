package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;

public interface IIndexingFacade {

	void declare(IInternalElement element, String name);

	void addOccurrence(IInternalElement element, OccurrenceKind kind,
			IRodinLocation location);
	
	void export(IInternalElement element);

}