package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade {

	private final IRodinIndex index;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private IDescriptor currentDescriptor;

	public IndexingFacade(IRodinIndex rodinIndex, FileTable fileTable,
			NameTable nameTable) {
		this.index = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.currentDescriptor = null;
	}

	public void addOccurrence(IInternalElement element, String name,
			Occurrence occurrence) {

		if (currentDescriptor == null
				|| (currentDescriptor.getElement() != element)) {
			currentDescriptor = getNotNullDescriptor(element, name);
		}

		currentDescriptor.addOccurrence(occurrence);
		fileTable.addElement(element, element.getRodinFile());
		nameTable.put(name, element);
	}

	// FIXME could as well be defined as the behavior of getDescriptor
	private IDescriptor getNotNullDescriptor(IInternalElement element,
			String name) {
		IDescriptor descriptor = index.getDescriptor(element);
		if (descriptor == null) {
			descriptor = index.makeDescriptor(element, name);
		}
		return descriptor;
	}

}
