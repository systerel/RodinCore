/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.indexer.persistence.PersistentPIM;
import org.rodinp.internal.core.indexer.persistence.PersistentTotalOrder;
import org.rodinp.internal.core.indexer.sort.TotalOrder;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.FileTable;
import org.rodinp.internal.core.indexer.tables.NameTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

/**
 * The ProjectIndexManager (PIM) stores and maintains index tables for a single
 * project.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProjectIndexManager {

	private static final IOccurrence[] NO_OCCURRENCES = new IOccurrence[0];

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
		this.tableRWL = new ReentrantReadWriteLock(true);
	}
	
	// for persistence purposes
	public ProjectIndexManager(IRodinProject project, RodinIndex index,
			ExportTable exportTable, TotalOrder<IRodinFile> order) {
		this.project = project;
		this.index = index;
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = exportTable;
		this.order = order;
		this.tableRWL = new ReentrantReadWriteLock(true);
		restoreNonPersistentData();
	}
	
	// for testing purposes
	public ProjectIndexManager(IRodinProject project, RodinIndex index,
			FileTable fileTable, NameTable nameTable, ExportTable exportTable,
			TotalOrder<IRodinFile> order) {
		this.project = project;
		this.index = index;
		this.fileTable = fileTable;
		this.nameTable = nameTable;
		this.exportTable = exportTable;
		this.order = order;
		this.tableRWL = new ReentrantReadWriteLock(true);
	}
	
	private void lockRead() throws InterruptedException {
		tableRWL.readLock().lockInterruptibly();
	}

	private void unlockRead() {
		tableRWL.readLock().unlock();
	}

	private void lockWrite() {
		tableRWL.writeLock().lock();
	}

	private void unlockWrite() {
		tableRWL.writeLock().unlock();
	}

	public synchronized void doIndexing(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask("indexing project " + project,
					IProgressMonitor.UNKNOWN);
		}
		while (order.hasNext()) {
			final IRodinFile file = order.next();
			
			doIndexing(file, monitor);
		}
		order.end();
	}

	private void doIndexing(final IRodinFile file, IProgressMonitor monitor) {
		final Map<IInternalElement, IDeclaration> fileImports =
				computeImports(file);

		final FileIndexingManager fim = FileIndexingManager.getDefault();
		final IIndexingResult result = fim.doIndexing(file, fileImports, monitor);

		checkCancel(monitor);
		if (result.isSuccess()) {
			if (mustReindexDependents(result)) {
				order.setToIterSuccessors();
			}

			updateTables(result);
		} else {
			order.setToIterSuccessors();
			order.remove();
			lockWrite();
			clean(file);
			unlockWrite();
		}
	}

	private boolean mustReindexDependents(IIndexingResult result) {
		// FIXME costly (?)
		final Set<IDeclaration> currentExports =
				exportTable.get(result.getFile());
		final Set<IDeclaration> resultExports = result.getExports();
		return !currentExports.equals(resultExports);
	}

	private void updateTables(IIndexingResult result) {
		lockWrite();

		clean(result.getFile());

		updateDeclarations(result);

		updateOccurrences(result);

		updateExports(result);

		unlockWrite();
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
			// not null expected from bridge checks
			assert descriptor != null;

			for (IOccurrence occurrence : occurrences) {
				descriptor.addOccurrence(occurrence);
			}
			fileTable.add(file, descriptor.getDeclaration());
		}
	}

	private void updateDeclarations(IIndexingResult result) {
		for (IDeclaration declaration : result.getDeclarations()) {
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
					// be indexed again, and the element name is incorrect there
				}
			}

			fileTable.add(result.getFile(), declaration);
			nameTable.add(declaration);
		}
	}

	private void clean(IRodinFile file) {

		for (IDeclaration declaration : fileTable.get(file)) {
			final IInternalElement element = declaration.getElement();
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

				if (descriptor.isEmpty()) {
					nameTable.remove(declaration);
					// even imported elements with no remaining occurrences
					// are removed from index (no more descriptor)
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
	 *            the changed file
	 */
	public synchronized void fileChanged(IRodinFile file) {
		if (!file.getRodinProject().equals(project)) {
			throw new IllegalArgumentException(file
					+ " should be indexed in project "
					+ project);
		}

		if (!IndexerRegistry.getDefault().isIndexable(file)) {
			return;
		}
		final FileIndexingManager fim = FileIndexingManager.getDefault();

		try {
			final Set<IRodinFile> dependFiles = fim.getDependencies(file);
			order.setPredecessors(file, dependFiles);
			order.setToIter(file);
		} catch (IndexingException e) {
			// forget this file
		}

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

	//  Assumes that the RodinIndex is up to date.
	private void restoreNonPersistentData() {
		nameTable.clear();
		fileTable.clear();
		for (Descriptor desc : index.getDescriptors()) {
			final IDeclaration declaration = desc.getDeclaration();
			nameTable.add(declaration);

			for (IOccurrence occurrence : desc.getOccurrences()) {
				final IRodinFile file = occurrence.getRodinFile();
				fileTable.add(file, declaration);
			}
		}

	}

	public synchronized boolean indexAll(IProgressMonitor monitor) {
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
	
	public IDeclaration getDeclaration(IInternalElement element)
			throws InterruptedException {
		try {
			lockRead();
			final Descriptor descriptor = index.getDescriptor(element);
			if (descriptor == null) {
				return null;
			} else {
				return descriptor.getDeclaration();
			}
		} finally {
			unlockRead();
		}
	}

	public IDeclaration[] getDeclarations(IRodinFile file)
			throws InterruptedException {
		try {
			lockRead();
			final Set<IDeclaration> decls = fileTable.get(file);
			return decls.toArray(new IDeclaration[decls.size()]);
		} finally {
			unlockRead();
		}
	}

	public IDeclaration[] getVisibleDeclarations(IRodinFile file)
			throws InterruptedException {
		try {
			lockRead();
			final Set<IDeclaration> decls = new HashSet<IDeclaration>(fileTable
					.get(file));
			final Collection<IDeclaration> imports = computeImports(file).values();
			decls.addAll(imports);
			return decls.toArray(new IDeclaration[decls.size()]);
		} finally {
			unlockRead();
		}
	}

	public IDeclaration[] getDeclarations(String name)
			throws InterruptedException {
		try {
			lockRead();
			final Set<IDeclaration> decls = nameTable.getDeclarations(name);
			return decls.toArray(new IDeclaration[decls.size()]);
		} finally {
			unlockRead();
		}
	}

	private IOccurrence[] getOccurrences(IInternalElement element)
			throws InterruptedException {
		try {
			lockRead();
			final Descriptor descriptor = index.getDescriptor(element);
			if (descriptor == null) {
				return NO_OCCURRENCES;
			}
			final Set<IOccurrence> occs = descriptor.getOccurrences();
			return occs.toArray(new IOccurrence[occs.size()]);
		} finally {
			unlockRead();
		}
	}

	public IOccurrence[] getOccurrences(IDeclaration declaration) throws InterruptedException {
		return getOccurrences(declaration.getElement());
	}

	public IRodinFile[] exportFiles() throws InterruptedException {
		try {
			lockRead();
			final Set<IRodinFile> files = exportTable.files();
			return files.toArray(new IRodinFile[files.size()]);
		} finally {
			unlockRead();
		}
	}

	public IDeclaration[] getExports(IRodinFile file)
			throws InterruptedException {
		try {
			lockRead();
			final Set<IDeclaration> exports = exportTable.get(file);
			return exports.toArray(new IDeclaration[exports.size()]);
		} finally {
			unlockRead();
		}
	}
	
	public PersistentPIM getPersistentData() {
		try {
			tableRWL.readLock().lock();
			final Collection<Descriptor> descColl = index.getDescriptors();
			final Descriptor[] descriptors = descColl
					.toArray(new Descriptor[descColl.size()]);
			final PersistentTotalOrder<IRodinFile> persistOrder = order
					.getPersistentData();
			final ExportTable exportClone = exportTable.clone();
			return new PersistentPIM(project, descriptors, exportClone,
					persistOrder);
		} finally {
			unlockRead();
		}
	}
}
