/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

public class IndexingToolkit implements IIndexingToolkit {

	private final IRodinFile file;
	private final RodinIndex rodinIndex;
	private final FileTable fileTable;
	private final NameTable nameTable;
	private final ExportTable exportTable;
	private final Set<IDeclaration> previousExports;
	private final Map<IInternalElement, IDeclaration> imports;
	private final Map<IInternalElement, IDeclaration> declarations;
	private Descriptor currentDescriptor;
	private boolean isClean; // TODO call to clean from client

	/**
	 * The given imports are assumed to be up-to-date.
	 * <p>
	 * The given ExportTable is assumed to be unchanged since latest indexing of
	 * the given file (empty if it never was indexed). It will be updated
	 * through calls to {@link IndexingToolkit#export(IInternalElement)}.
	 * <p>
	 * The given RodinIndex, FileTable and NameTable are supposed to be just
	 * coherent with each other. They will be cleaned here.
	 * 
	 * @param file
	 * @param rodinIndex
	 * @param fileTable
	 * @param nameTable
	 * @param exportTable
	 * @param imports
	 */
	public IndexingToolkit(IRodinFile file, RodinIndex rodinIndex,
			FileTable fileTable, NameTable nameTable, ExportTable exportTable,
			Map<IInternalElement, IDeclaration> imports) {

		this.file = file;
		this.rodinIndex = rodinIndex;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exportTable = exportTable;
		this.previousExports = exportTable.get(file);
		this.imports = imports;
		this.declarations = new HashMap<IInternalElement, IDeclaration>();
		this.currentDescriptor = null;
		this.isClean = false;
	}

	public void declare(IInternalElement element, String name) {
		if (!isClean) {
			clean();
		}

		if (!isLocal(element)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (declarations.containsKey(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		currentDescriptor = rodinIndex.getDescriptor(element);
		// there may be import occurrences
		if (currentDescriptor == null) {
			currentDescriptor = rodinIndex.makeDescriptor(element, name);
		} else { // possible renaming
			final String previousName = currentDescriptor.getName();
			if (!previousName.equals(name)) {
				rodinIndex.removeDescriptor(element);
				currentDescriptor = rodinIndex.makeDescriptor(element, name);
				nameTable.remove(previousName, element);
				// there are import occurrences of the element;
				// in those files, it is referred to with previousName
				// => rodinIndex tables are coherent but those files may not be
				// indexed again, and the element name is incorrect there
			}
		}

		fileTable.add(element, file);
		nameTable.put(name, element);
		declarations.put(element, new Declaration(element, name));
	}

	public void addOccurrence(IInternalElement element, IOccurrenceKind kind,
			IRodinLocation location) {

		if (!isClean) {
			clean();
		}

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
		if (!isClean) {
			clean();
		}

		final IDeclaration declaration;
		if (isLocal(element)) {
			declaration = declarations.get(element);
		} else if (isImported(element)) {
			declaration = imports.get(element);
		} else {
			throw new IllegalArgumentException(
					"Cannot export an element that is neither local nor imported.");
		}

		fetchCurrentDescriptor(element);
		exportTable.add(file, declaration);
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
		return imports.containsKey(element);
	}

	private boolean isLocalOrImported(IInternalElement element) {
		return isLocal(element) || isImported(element);
	}

	public boolean mustReindexDependents() {
		// FIXME costly (?)
		return !exportTable.get(file).equals(previousExports);
	}

	public void clean() {

		for (IInternalElement element : fileTable.get(file)) {
			final Descriptor descriptor = rodinIndex.getDescriptor(element);

			if (descriptor == null) {
				IRodinDBStatus status = new RodinDBStatus(
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						element,
						getClass()
								+ ": element in FileTable with no Descriptor in RodinIndex.");
				RodinCore.getRodinCore().getLog().log(status);
			} else {
				descriptor.removeOccurrences(file);

				if (descriptor.getOccurrences().length == 0) {
					final String name = descriptor.getName();
					nameTable.remove(name, element);
					rodinIndex.removeDescriptor(element);
				}
			}
		}
		exportTable.remove(file);
		fileTable.remove(file);
		isClean = true;
	}

	public IDeclaration[] getImports() {
		if (!isClean) {
			clean();
		}
		return imports.values().toArray(new IDeclaration[imports.size()]);
	}

}
