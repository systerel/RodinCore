package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade {

	final IRodinIndex index;
	final FileTable fileTable;
	final NameTable nameTable;

	public IndexingFacade(IRodinIndex rodinIndex, FileTable fileTable,
			NameTable nameTable) {
		this.index = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
	}

	public void addOccurrence(IInternalElement element, String name,
			Occurrence occurrence) {
		// FIXME problem : the name needs not be given each time
		// we could perhaps allow a null name provided that the
		// name has already been given (existing descriptor)
		// alternatively, we could provide a method that omits the name argument
		IDescriptor descriptor = index.getDescriptor(element);

		if (descriptor == null) {
			descriptor = index.makeDescriptor(element, name);
		} else {
			if (!descriptor.getName().equals(name)) {
				throw new IllegalArgumentException(
						"given name differs from the one previously"
								+ " given for the same element");
			}
		}
		descriptor.addOccurrence(occurrence);
		fileTable.addElement(element, element.getRodinFile());
		nameTable.put(name, element);
	}

}
