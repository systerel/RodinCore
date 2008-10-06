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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.rodinp.internal.core.index.tables.TotalOrder;

public class ProjectIndexManager {

	private final IRodinProject project;

	private final FileIndexingManager fim;

	private final IndexerRegistry indexersRegistry;

	private final RodinIndex index;

	private final FileTable fileTable;

	private final NameTable nameTable;

	private final ExportTable exportTable;

	private final TotalOrder<IRodinFile> order;

	public ProjectIndexManager(IRodinProject project, FileIndexingManager fim,
			IndexerRegistry indManager) {
		this.project = project;
		this.fim = fim;
		this.indexersRegistry = indManager;
		this.index = new RodinIndex();
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = new ExportTable();
		this.order = new TotalOrder<IRodinFile>();
	}

	public void doIndexing(IProgressMonitor monitor) {
		monitor.beginTask("indexing project " + project,
				IProgressMonitor.UNKNOWN);
		while (order.hasNext()) {
			final IRodinFile file = order.next();

			final Map<IInternalElement, IDeclaration> fileImports = computeImports(file);
			final IndexingToolkit indexingToolkit = new IndexingToolkit(file,
					fileImports, monitor);

			final IIndexingResult result = fim
					.doIndexing(indexingToolkit);

			if (indexingToolkit.isCancelled()) {
				break;
			}
			if (result.isSuccess()) {
				if (mustReindexDependents(result)) {
					order.setToIterSuccessors();
				}

				updateTables(result);
			} else {
				order.setToIterSuccessors();
				order.remove();
				clean(file);
			}
		}
		order.end();
	}

	public boolean mustReindexDependents(IIndexingResult result) {
		// FIXME costly (?)
		final Set<IDeclaration> currentExports = exportTable.get(result
				.getFile());
		final Set<IDeclaration> resultExports = result.getExports();
		return !currentExports.equals(resultExports);
	}

	private void updateTables(IIndexingResult result) {
		clean(result.getFile());

		updateDeclarations(result);

		updateOccurrences(result);

		updateExports(result);
	}

	private void updateExports(IIndexingResult result) {
		final IRodinFile file = result.getFile();
		for (IDeclaration export : result.getExports()) {
			exportTable.add(file, export);
		}
	}

	private void updateOccurrences(IIndexingResult result) {
		final IRodinFile file = result.getFile();

		final Map<IInternalElement, Set<IOccurrence>> occurrencesMap = result
				.getOccurrences();

		for (IInternalElement element : occurrencesMap.keySet()) {
			final Set<IOccurrence> occurrences = occurrencesMap.get(element);
			final Descriptor descriptor = index.getDescriptor(element);
			// not null assumed from toolkit checks
			for (IOccurrence occurrence : occurrences) {
				descriptor.addOccurrence(occurrence);
			}
			fileTable.add(element, file);
		}
	}

	private void updateDeclarations(IIndexingResult result) {
		for (IDeclaration declaration : result.getDeclarations().values()) {
			final IInternalElement element = declaration.getElement();
			final String name = declaration.getName();

			Descriptor descriptor = index.getDescriptor(element);
			// there may be import occurrences
			if (descriptor == null) {
				descriptor = index.makeDescriptor(element, name);
			} else { // possible renaming
				final String previousName = descriptor.getName();
				if (!previousName.equals(name)) {
					index.removeDescriptor(element);
					descriptor = index.makeDescriptor(element, name);
					nameTable.remove(previousName, element);
					// there are import occurrences of the element;
					// in those files, it is referred to with previousName
					// => rodinIndex tables are coherent but those files may not
					// be
					// indexed again, and the element name is incorrect there
				}
			}

			fileTable.add(element, result.getFile());
			nameTable.put(name, element);
		}
	}

	private void clean(IRodinFile file) {

		for (IInternalElement element : fileTable.get(file)) {
			final Descriptor descriptor = index.getDescriptor(element);

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
					index.removeDescriptor(element);
				}
			}
		}
		exportTable.remove(file);
		fileTable.remove(file);
	}

	/**
	 * Indicates that the given file has to be (re)-indexed. Also, update the
	 * dependents of the given file in the dependency graph.
	 * 
	 * @param file
	 */
	// TODO rename to fileChanged() ?
	public void setToIndex(IRodinFile file) {
		if (!file.getRodinProject().equals(project)) {
			throw new IllegalArgumentException(file
					+ " should be indexed in project " + project);
		}

		if (!indexersRegistry.isIndexable(file.getElementType())) {
			return;
		}
		final IRodinFile[] dependFiles = fim.getDependencies(file);
		if (dependFiles == null)
			return;

		order.setPredecessors(file, dependFiles);
		order.setToIter(file);
	}

	public FileTable getFileTable() {
		return fileTable;
	}

	public NameTable getNameTable() {
		return nameTable;
	}

	public ExportTable getExportTable() {
		return exportTable;
	}

	public RodinIndex getIndex() {
		return index;
	}

	private Map<IInternalElement, IDeclaration> computeImports(IRodinFile file) {
		final Map<IInternalElement, IDeclaration> result = new HashMap<IInternalElement, IDeclaration>();
		final List<IRodinFile> fileDeps = order.getPredecessors(file);

		for (IRodinFile f : fileDeps) {
			final Set<IDeclaration> exports = exportTable.get(f);
			for (IDeclaration declaration : exports) {
				result.put(declaration.getElement(), declaration);
			}
		}
		return result;
	}

}
