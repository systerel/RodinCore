package org.rodinp.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade {

	private final IRodinFile file;
	private final IRodinIndex index;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private IDescriptor currentDescriptor;

	public IndexingFacade(IRodinFile file, IRodinIndex rodinIndex,
			FileTable fileTable, NameTable nameTable, ExportTable exportTable) {
		this.file = file;
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
		// fileTable.addElement(element, element.getRodinFile());
		fileTable.addElement(element, file);
		// FIXME check with specifications and enforce constraints
		nameTable.put(name, element);
	}

	/** FIXME could as well be defined as the behavior of {@link IRodinIndex#getDescriptor(Object)} */
	private IDescriptor getNotNullDescriptor(IInternalElement element,
			String name) {
		IDescriptor descriptor = index.getDescriptor(element);
		if (descriptor == null) {
			descriptor = index.makeDescriptor(element, name);
		}
		return descriptor;
	}

}
