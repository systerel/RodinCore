package org.rodinp.internal.core.index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.core.index.OccurrenceKind;
import org.rodinp.internal.core.index.tables.DependenceTable;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;

public class IndexingFacade implements IIndexingFacade {

	private final IRodinFile file;
	private final RodinIndex index;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private final ExportTable exports;
	private final DependenceTable dependencies;
	private Descriptor currentDescriptor;

	public IndexingFacade(IRodinFile file, RodinIndex rodinIndex,
			FileTable fileTable, NameTable nameTable, ExportTable exports,
			DependenceTable dependencies) {
		this.file = file;
		this.index = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exports = exports;
		this.dependencies = dependencies;
		this.currentDescriptor = null;
	}

	public void addDeclaration(IInternalElement element, String name) {

		if (!element.getRodinFile().equals(file)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (index.isDeclared(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		currentDescriptor = index.makeDescriptor(element, name);
		fileTable.addElement(element, file);
		nameTable.put(name, element);
	}

	public void addOccurrence(IInternalElement element, OccurrenceKind kind,
			IRodinLocation location) {

		if (!verifyOccurrence(element, location)) {
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

		if (currentDescriptor == null) {
			throw new IllegalArgumentException("Element not declared: "
					+ element);
		}
	}

	private boolean verifyOccurrence(IInternalElement element,
			IRodinLocation location) {
		final IRodinElement locElem = location.getElement();
		final IRodinFile locElemFile;
		if (locElem instanceof IRodinFile) {
			locElemFile = (IRodinFile) locElem;
		} else if (locElem instanceof IInternalElement) {
			locElemFile = ((IInternalElement) locElem).getRodinFile();
		} else {
			return false;
		}
		if (locElemFile.equals(file)) { // local occurrence (mandatory)
			return element.getRodinFile().equals(file) // local element
					|| isImported(element); // imported element
		}
		return false;
	}

	private boolean isImported(IInternalElement element) {
		final IRodinFile elemFile = element.getRodinFile();

		final List<IRodinFile> dependsOn = Arrays
				.asList(dependencies.get(file));
		final Map<IInternalElement, String> exported = exports.get(elemFile);

		return dependsOn.contains(elemFile) && exported.containsKey(element);
	}

}
