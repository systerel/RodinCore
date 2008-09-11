package org.rodinp.internal.core.index;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade implements IIndexingFacade {

	private final IRodinFile file;
	private final RodinIndex index;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private Descriptor currentDescriptor;

	public IndexingFacade(IRodinFile file, RodinIndex rodinIndex,
			FileTable fileTable, NameTable nameTable, ExportTable exportTable) {
		this.file = file;
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
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		currentDescriptor = index.makeDescriptor(element, name);
		// fileTable.addElement(element, element.getRodinFile());
		fileTable.addElement(element, file);
		// FIXME check with specifications and enforce constraints
		nameTable.put(name, element);
	}

	public void addOccurrence(IInternalElement element, OccurrenceKind kind,
			IRodinLocation location) {

		if (!verifyOccurrence(element, kind, location)) {
			throw new IllegalArgumentException(
					"Incorrect occurrence for element: " + element);
		}

		fetchCurrentDescriptor(element);
		final Occurrence occurrence = new Occurrence(kind, location);
		currentDescriptor.addOccurrence(occurrence);
	}

	private void fetchCurrentDescriptor(IInternalElement element) {
		if (currentDescriptor != null
				&& currentDescriptor.getElement() == element) {
			return;
		}
		currentDescriptor = index.getDescriptor(element);
		// FIXME could be not null from previous indexing (must not (?)
		// be declared during this pass). The indexer can not know that.
		// Verify that getting descriptors of alien elements (declared outside)
		// is allowed.
		if (currentDescriptor == null) {
			throw new IllegalArgumentException("Element not declared: "
					+ element);
		}
	}

	private boolean verifyOccurrence(IInternalElement element,
			OccurrenceKind kind, IRodinLocation location) {
		// TODO: change constraint
		// accept an alien IRodinFile only when it is referenced in the
		// dependence table and there is a concomitant reference in export table.
		final IRodinElement locElem = location.getElement();
		if (locElem instanceof IRodinFile) {
			return locElem.equals(file);
		}
		if (locElem instanceof IInternalElement) {
			return ((IInternalElement) locElem).getRodinFile().equals(file);
		}
		return false;
	}

}
