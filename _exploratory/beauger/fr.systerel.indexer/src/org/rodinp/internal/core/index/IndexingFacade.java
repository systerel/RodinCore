package org.rodinp.internal.core.index;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private final ExportTable exportTable;
	private final Map<IInternalElement, String> previousExports;
	private final List<IRodinFile> localDeps;
	private boolean reindexDependents;
	private Descriptor currentDescriptor;

	/**
	 * The given DependenceTable is assumed to be up-to-date.
	 * <p>
	 * The given ExportTable is assumed to be unchanged since latest indexing
	 * (empty if it never was indexed). It will be updated through calls to
	 * {@link IndexingFacade#export(IInternalElement)}.
	 * <p>
	 * The given FileTable and NameTable must have been clean // TODO consider
	 * cleaning here
	 * 
	 * @param file
	 * @param rodinIndex
	 * @param fileTable
	 * @param nameTable
	 * @param exportTable
	 * @param dependTable
	 */
	public IndexingFacade(IRodinFile file, RodinIndex rodinIndex,
			FileTable fileTable, NameTable nameTable, ExportTable exportTable,
			DependenceTable dependTable) {
		this.file = file;
		this.index = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exportTable = exportTable;
		this.localDeps = Arrays.asList(dependTable.get(file));
		this.previousExports = exportTable.get(file);
		exportTable.remove(file); // reset exports for this file
		this.reindexDependents = false;
		this.currentDescriptor = null;
	}

	public void addDeclaration(IInternalElement element, String name) {

		assertIsLocal(element);

		if (index.isDeclared(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		currentDescriptor = index.makeDescriptor(element, name);
		// FIXME element may have been declared in a previous indexing and have
		// alien occurrences that should be preserved.
		// FIXME treat renaming also in this case.
		if (previousExports.containsKey(element)) {
			final String previousName = previousExports.get(element);
			if (!previousName.equals(name)) { // renaming
				nameTable.remove(previousName, element);
			} // else declared again with same name but maybe no more exported
		} // else was not yet exported, but may get 

		fileTable.addElement(element, file);
		nameTable.put(name, element);
	}

	private void assertIsLocal(IInternalElement element) {
		if (!element.getRodinFile().equals(file)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}
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

	public void export(IInternalElement element) {
		assertIsLocal(element);
		fetchCurrentDescriptor(element);

		final String name = currentDescriptor.getName();

		exportTable.add(file, element, name);

		reindexDependents = !exportTable.get(file).keySet().equals(
				previousExports.keySet()); // FIXME costly
	}

	private void fetchCurrentDescriptor(IInternalElement element) {
		if (currentDescriptor != null
				&& currentDescriptor.getElement() == element) {
			return;
		}
		currentDescriptor = index.getDescriptor(element);
		// TODO: will never be null after implementing method changes => review

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

		final Set<IInternalElement> exported = exportTable.get(elemFile)
				.keySet();

		return localDeps.contains(elemFile) && exported.contains(element);
	}

	public boolean mustReindexDependents() {
		return reindexDependents;
	}

}
