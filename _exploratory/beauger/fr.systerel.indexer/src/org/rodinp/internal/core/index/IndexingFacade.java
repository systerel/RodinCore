package org.rodinp.internal.core.index;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IIndexingFacade;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
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
	// private final DependenceTable dependTable;
	private final IRodinFile[] localDeps;
	private final Set<IInternalElement> imports;
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

		// TODO make this method public, don't call it here
		clean(file, rodinIndex, fileTable, nameTable);

		this.file = file;
		this.rodinIndex = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exportTable = exportTable;
		this.previousExports = exportTable.get(file);
		// this.dependTable = dependTable;
		this.localDeps = dependTable.get(file);
		this.imports = computeImports();
		// TODO mv line below in clean()
		exportTable.remove(file); // reset exports for this file
		this.currentDescriptor = null;
	}

	public void declare(IInternalElement element, String name) {

		if (!isLocal(element)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (fileTable.contains(file, element)) {
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
			}
		}

		fileTable.add(element, file);
		nameTable.put(name, element);
	}

	public void addOccurrence(IInternalElement element, IOccurrenceKind kind,
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

	/**
	 * The ExportTable records names associated to the exported elements.
	 * <p>
	 * If the element is local, it is associated with its declaration name.
	 * <p>
	 * If the element was imported, the associated name will be the one it was
	 * declared with nevertheless, which is possibly different from the one it
	 * is known by in the current file.
	 * <p>
	 * Treating local incoherences of public names in imported elements is
	 * beyond the scope of the indexing system.
	 */
	public void export(IInternalElement element) {
		if (!isLocalOrImported(element)) {
			throw new IllegalArgumentException(
					"Cannot export an element that is neither local nor imported.");
		}
		fetchCurrentDescriptor(element);

		exportTable.add(file, element, currentDescriptor.getName());
	}

	private Set<IInternalElement> computeImports() {
		final Set<IInternalElement> result = new HashSet<IInternalElement>();
		for (IRodinFile f : localDeps) {
			result.addAll(exportTable.get(f).keySet());
		}
		return result;
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
		final IRodinFile locElemFile = location.getRodinFile();
		return file.equals(locElemFile) && isLocalOrImported(element);
	}

	private boolean isLocal(IInternalElement element) {
		return file.equals(element.getRodinFile());
	}

	private boolean isImported(IInternalElement element) {
		return imports.contains(element);
	}

	private boolean isLocalOrImported(IInternalElement element) {
		return isLocal(element) || isImported(element);
	}

	public boolean mustReindexDependents() {
		// FIXME costly (?)
		return !exportTable.get(file).equals(previousExports);
	}

	private void clean(IRodinFile f, final RodinIndex index,
			final FileTable fTable, final NameTable nTable) {

		for (IInternalElement element : fTable.get(f)) {
			final Descriptor descriptor = index.getDescriptor(element);

			if (descriptor == null) {
				// TODO log problem instead
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

	public IInternalElement[] getImports() {
		return imports.toArray(new IInternalElement[imports.size()]);
	}

}
