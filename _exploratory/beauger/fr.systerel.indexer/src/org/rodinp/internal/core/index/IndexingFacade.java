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
	private final RodinIndex rodinIndex;
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
	 * The given ExportTable is assumed to be unchanged since latest indexing of
	 * the given file (empty if it never was indexed). It will be updated
	 * through calls to {@link IndexingFacade#export(IInternalElement)}.
	 * <p>
	 * The given RodinIndex, FileTable and NameTable are supposed to be just
	 * coherent with each other. They will be cleaned here.
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

		clean(file, rodinIndex, fileTable, nameTable);

		this.file = file;
		this.rodinIndex = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exportTable = exportTable;
		this.localDeps = Arrays.asList(dependTable.get(file));
		this.previousExports = exportTable.get(file);
		exportTable.remove(file); // reset exports for this file
		this.reindexDependents = false;
		this.currentDescriptor = null;
	}

	public void declare(IInternalElement element, String name) {

		if (!isLocal(element)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (Arrays.asList(fileTable.get(file)).contains(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		currentDescriptor = rodinIndex.getDescriptor(element); // there may be
		// alien occurrences
		if (currentDescriptor == null) {
			currentDescriptor = rodinIndex.makeDescriptor(element, name);
		} else { // possible renaming
			final String previousName = currentDescriptor.getName();
			if (!previousName.equals(name)) {
				rodinIndex.rename(element, name);
				nameTable.remove(previousName, element);
				// there are alien occurrences of the element;
				// in those files, it is referred to with previousName
				// => rodinIndex tables are coherent but those files may not be
				// indexed again, and the element name is incorrect there
				// NB: actually, the ExportTable can be incoherent, as the
				// element may be re-exported by dependent files with a bad
				// name.
				// TODO as far as I can see, the name is no more needed in the
				// ExportTable, so removing it would be the simplest solution.
				// Else just propagate name changes through export tables.
			}
		}

		fileTable.add(element, file);
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
		fileTable.add(element, file);
	}

	public void export(IInternalElement element) {
		if (!isLocalOrImported(element)) {
			throw new IllegalArgumentException(
					"Cannot export an element that is neither local nor imported.");
		}
		fetchCurrentDescriptor(element);

		final String name = currentDescriptor.getName();

		exportTable.add(file, element, name);

		reindexDependents = !exportTable.get(file).keySet().equals(
				previousExports.keySet()); // FIXME costly (?)
	}

	private void fetchCurrentDescriptor(IInternalElement element) {
		if (currentDescriptor != null
				&& currentDescriptor.getElement() == element) {
			return;
		}
		currentDescriptor = rodinIndex.getDescriptor(element);

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
		return locElemFile.equals(file) && isLocalOrImported(element);
	}

	private boolean isLocal(IInternalElement element) {
		return element.getRodinFile().equals(file);
	}

	private boolean isImported(IInternalElement element) {
		for (IRodinFile f : localDeps) {
			if (exportTable.get(f).keySet().contains(element)) {
				return true;
			}
		}
		return false;
	}

	private boolean isLocalOrImported(IInternalElement element) {
		return isLocal(element) || isImported(element);
	}

	public boolean mustReindexDependents() {
		return reindexDependents;
	}

	private void clean(IRodinFile f, final RodinIndex index,
			final FileTable fTable, final NameTable nTable) {

		for (IInternalElement element : fTable.get(f)) {
			final Descriptor descriptor = index.getDescriptor(element);

			if (descriptor == null) {
				throw new IllegalStateException(
						"Elements in FileTable with no Descriptor in RodinIndex.");
			}
			descriptor.removeOccurrences(f);

			if (descriptor.getOccurrences().length == 0) {
				final String name = descriptor.getName();
				nTable.remove(name, element);
				index.removeDescriptor(element);
			}
		}
		fTable.remove(f);
	}

}
