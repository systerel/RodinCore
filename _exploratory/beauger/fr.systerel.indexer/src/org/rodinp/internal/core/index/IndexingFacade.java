package org.rodinp.internal.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade implements IIndexingFacade {

	private final IRodinFile file;
	private final IIndexer indexer;
	private final RodinIndex index;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private Descriptor currentDescriptor;

	public IndexingFacade(IRodinFile file, IIndexer indexer,
			RodinIndex rodinIndex, FileTable fileTable, NameTable nameTable,
			ExportTable exportTable) {
		this.file = file;
		this.indexer = indexer;
		this.index = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.currentDescriptor = null;
	}

	public void addDeclaration(IInternalElement element, String name) {
		if (index.isDeclared(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		if (!element.getRodinFile().equals(file)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: " + element);
		}

		currentDescriptor = (Descriptor) index.makeDescriptor(element, name);
		// fileTable.addElement(element, element.getRodinFile());
		fileTable.addElement(element, file);
		// FIXME check with specifications and enforce constraints
		nameTable.put(name, element);
	}

	public void addOccurrence(IInternalElement element, OccurrenceKind kind,
			IRodinLocation location) {

		// TODO check constraints on arguments

		fetchCurrentDescriptor(element);
		final Occurrence occurrence = new Occurrence(kind, location, indexer);
		currentDescriptor.addOccurrence(occurrence);
	}

	private void fetchCurrentDescriptor(IInternalElement element) {
		if (currentDescriptor != null
				&& currentDescriptor.getElement() == element) {
			return;
		}
		currentDescriptor = (Descriptor) index.getDescriptor(element);
		if (currentDescriptor == null) {
			throw new IllegalArgumentException("Element not declared: "
					+ element);
		}
	}

}
