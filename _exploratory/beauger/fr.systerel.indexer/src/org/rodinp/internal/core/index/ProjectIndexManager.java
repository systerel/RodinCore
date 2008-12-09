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
import java.util.concurrent.CancellationException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.index.sort.TotalOrder;
import org.rodinp.internal.core.index.tables.ExportTable;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.NameTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

/**
 * The ProjectIndexManager (PIM) stores and maintains index tables for a single
 * project.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProjectIndexManager {

	private final IRodinProject project;

	private final RodinIndex index;

	private final FileTable fileTable;

	private final NameTable nameTable;

	private final ExportTable exportTable;

	private final TotalOrder<IRodinFile> order;

	private final ReentrantReadWriteLock tableRWL;

	public ProjectIndexManager(IRodinProject project) {
		this.project = project;
		this.index = new RodinIndex();
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = new ExportTable();
		this.order = new TotalOrder<IRodinFile>();
		this.tableRWL = new ReentrantReadWriteLock();
	}

	public void lockRead() throws InterruptedException {
		tableRWL.readLock().lockInterruptibly();
	}

	public void unlockRead() {
		tableRWL.readLock().unlock();
	}

	public void lockWrite() throws InterruptedException {
		tableRWL.writeLock().lockInterruptibly();
	}

	public void unlockWrite() {
		tableRWL.writeLock().unlock();
	}

	public void doIndexing(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask("indexing project " + project,
					IProgressMonitor.UNKNOWN);
		}
		while (order.hasNext()) {
			final IRodinFile file = order.next();

			final Map<IInternalElement, IDeclaration> fileImports =
					computeImports(file);
			final IndexingToolkit indexingToolkit =
					new IndexingToolkit(file, fileImports, monitor);

			final FileIndexingManager fim = FileIndexingManager.getDefault();
			final IIndexingResult result = fim.doIndexing(indexingToolkit);

			checkCancel(monitor);
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
		final Set<IDeclaration> currentExports =
				exportTable.get(result.getFile());
		final Set<IDeclaration> resultExports = result.getExports();
		return !currentExports.equals(resultExports);
	}

	private void updateTables(IIndexingResult result) {
		tableRWL.writeLock().lock();

		clean(result.getFile());

		updateDeclarations(result);

		updateOccurrences(result);

		updateExports(result);

		tableRWL.writeLock().unlock();
	}

	private void updateExports(IIndexingResult result) {
		final IRodinFile file = result.getFile();
		for (IDeclaration export : result.getExports()) {
			exportTable.add(file, export);
		}
	}

	private void updateOccurrences(IIndexingResult result) {
		final IRodinFile file = result.getFile();

		final Map<IInternalElement, Set<IOccurrence>> occurrencesMap =
				result.getOccurrences();

		for (IInternalElement element : occurrencesMap.keySet()) {
			final Set<IOccurrence> occurrences = occurrencesMap.get(element);
			final Descriptor descriptor = index.getDescriptor(element);
			// not null assumed from toolkit checks
			for (IOccurrence occurrence : occurrences) {
				descriptor.addOccurrence(occurrence);
			}
			fileTable.add(file, element);
		}
	}

	private void updateDeclarations(IIndexingResult result) {
		for (IDeclaration declaration : result.getDeclarations().values()) {
			final IInternalElement element = declaration.getElement();
			final String name = declaration.getName();

			Descriptor descriptor = index.getDescriptor(element);
			// there may be import occurrences
			if (descriptor == null) {
				descriptor = index.makeDescriptor(declaration);
			} else { // possible renaming
				final IDeclaration previousDecl = descriptor.getDeclaration();
				final String previousName = previousDecl.getName();
				if (!previousName.equals(name)) {
					index.removeDescriptor(element);
					descriptor = index.makeDescriptor(declaration);
					nameTable.remove(previousDecl);
					// there are import occurrences of the element;
					// in those files, it is referred to with previousName
					// => rodinIndex tables are coherent but those files may not
					// be
					// indexed again, and the element name is incorrect there
				}
			}

			fileTable.add(result.getFile(), element);
			nameTable.add(declaration);
		}
	}

	private void clean(IRodinFile file) {

		for (IInternalElement element : fileTable.get(file)) {
			final Descriptor descriptor = index.getDescriptor(element);

			if (descriptor == null) {
				IRodinDBStatus status =
						new RodinDBStatus(
								IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
								element,
								getClass()
										+ ": element in FileTable with no Descriptor in RodinIndex.");
				RodinCore.getRodinCore().getLog().log(status);
			} else {
				descriptor.removeOccurrences(file);

				if (descriptor.getOccurrences().length == 0) {
					final IDeclaration declaration =
							descriptor.getDeclaration();
					nameTable.remove(declaration);
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
	public void fileChanged(IRodinFile file) {
		if (!file.getRodinProject().equals(project)) {
			throw new IllegalArgumentException(file
					+ " should be indexed in project "
					+ project);
		}

		if (!IndexerRegistry.getDefault().isIndexable(file)) {
			return;
		}
		final FileIndexingManager fim = FileIndexingManager.getDefault();

		IRodinFile[] dependFiles;
		try {
			dependFiles = fim.getDependencies(file);
			order.setPredecessors(file, dependFiles);
			order.setToIter(file);
		} catch (IndexingException e) {
			// forget this file
		}

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

	public TotalOrder<IRodinFile> getOrder() {
		return order;
	}

	private Map<IInternalElement, IDeclaration> computeImports(IRodinFile file) {
		final Map<IInternalElement, IDeclaration> result =
				new HashMap<IInternalElement, IDeclaration>();
		final List<IRodinFile> fileDeps = order.getPredecessors(file);

		for (IRodinFile f : fileDeps) {
			final Set<IDeclaration> exports = exportTable.get(f);
			for (IDeclaration declaration : exports) {
				result.put(declaration.getElement(), declaration);
			}
		}
		return result;
	}

	/**
	 * @return the managed project
	 */
	public IRodinProject getProject() {
		return project;
	}

	/**
	 * Assumes that the RodinIndex is up to date.
	 */
	public void restoreNonPersistentData() {
		nameTable.clear();
		fileTable.clear();
		for (Descriptor desc : index.getDescriptors()) {
			final IDeclaration declaration = desc.getDeclaration();
			nameTable.add(declaration);

			final IInternalElement element = declaration.getElement();
			for (IOccurrence occurrence : desc.getOccurrences()) {
				final IRodinFile file = occurrence.getRodinFile();
				fileTable.add(file, element);
			}
		}

	}

	public boolean indexAll(IProgressMonitor monitor) {
		try {
			final IRodinFile[] files = project.getRodinFiles();
			for (IRodinFile file : files) {
				fileChanged(file);
				checkCancel(monitor);
			}
			doIndexing(monitor);
			return true;
		} catch (RodinDBException e) {
			return false;
		}

	}

	private void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new CancellationException();
		}
	}

}
