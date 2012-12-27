/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

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
 * TODO instead of synchronizing everything, take a snapshot of the tables
 * before any change, then manage exclusive access to writers, meanwhile read of
 * the snapshot shall be non exclusive. Would prevents blocking while building.
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

	private volatile boolean isProjectVanishing = false;
	
	// FIXME protect from concurrent access
	private final Set<IRodinFile> unprocessedFiles = Collections
			.synchronizedSet(new LinkedHashSet<IRodinFile>());  
	
	public ProjectIndexManager(IRodinProject project) {
		this.project = project;
		this.index = new RodinIndex();
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = new ExportTable();
		this.order = new TotalOrder<IRodinFile>();
	}
	
	// for persistence purposes
	public ProjectIndexManager(IRodinProject project, RodinIndex index,
			ExportTable exportTable, TotalOrder<IRodinFile> order,
			List<IRodinFile> unprocessedFiles) {
		this.project = project;
		this.index = index;
		this.fileTable = new FileTable();
		this.nameTable = new NameTable();
		this.exportTable = exportTable;
		this.order = order;
		this.unprocessedFiles.addAll(unprocessedFiles);
		restoreNonPersistentData();
		fireUnprocessedFiles();
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
	}
	
	public void fireUnprocessedFiles() {
		for(IRodinFile file: unprocessedFiles) {
			IndexManager.getDefault().enqueueUnprocessedFile(file);
		}
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
		unprocessedFiles.add(file);
		if (isProjectVanishing) {
			printDebugVanish(file);
			return;
		}

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
			clean(file);
		}
		unprocessedFiles.remove(file);
	}

	private boolean mustReindexDependents(IIndexingResult result) {
		// FIXME costly (?)
		final Set<IDeclaration> currentExports =
				exportTable.get(result.getFile());
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
				IRodinDBStatus status = new RodinDBStatus(
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
	 * @param monitor
	 *            the monitor to manage cancellation with
	 */
	public synchronized void fileChanged(IRodinFile file, IProgressMonitor monitor) {
		if (!file.getRodinProject().equals(project)) {
			throw new IllegalArgumentException(file
					+ " should be indexed in project "
					+ project);
		}
		unprocessedFiles.add(file);
		if (isProjectVanishing) {
			printDebugVanish(file);
			return;
		}
		
		if (!IndexerRegistry.getDefault().isIndexable(file)) {
			return;
		}
		final FileIndexingManager fim = FileIndexingManager.getDefault();

		try {
			final Set<IRodinFile> dependFiles = fim.getDependencies(file, monitor);
			order.setPredecessors(file, dependFiles);
			order.setToIter(file);
		} catch (IndexingException e) {
			// forget this file
		}

	}

	private void printDebugVanish(IRodinFile file) {
		if (IndexManager.DEBUG) {
			System.out.println("PIM: RENOUNCES processing of " + file
					+ " because project " + project + " is VANISHING");
		}
	}

	// the file argument must be known (order.contains(file) is true)
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
			unprocessedFiles.addAll(Arrays.asList(files));
			for (IRodinFile file : files) {
				fileChanged(file, monitor);
				checkCancel(monitor);
			}
			doIndexing(monitor);
			return true;
		} catch (RodinDBException e) {
			return false;
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}

	}

	private void checkCancel(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new CancellationException();
		}
	}

	public void setProjectVanishing() {
		this.isProjectVanishing = true;
	}
	
	public synchronized IDeclaration getDeclaration(IInternalElement element) {
		final Descriptor descriptor = index.getDescriptor(element);
		if (descriptor == null) {
			return null;
		} else {
			return descriptor.getDeclaration();
		}
	}

	public synchronized Set<IDeclaration> getDeclarations(IRodinFile file) {
		final Set<IDeclaration> decls = fileTable.get(file);
		return new LinkedHashSet<IDeclaration>(decls);
	}

	public synchronized Set<IDeclaration> getVisibleDeclarations(IRodinFile file) {
		if (!order.contains(file)) {
			// unknown file
			return Collections.emptySet();
		}
		final Set<IDeclaration> decls = new LinkedHashSet<IDeclaration>(
				fileTable.get(file));
		final Collection<IDeclaration> imports = computeImports(file).values();
		decls.addAll(imports);
		return decls;
	}

	public synchronized Set<IDeclaration> getDeclarations(String name) {
		final Set<IDeclaration> decls = nameTable.getDeclarations(name);
		return new LinkedHashSet<IDeclaration>(decls);
	}

	private synchronized Set<IOccurrence> getOccurrences(
			IInternalElement element) {
		final Descriptor descriptor = index.getDescriptor(element);
		if (descriptor == null) {
			return Collections.emptySet();
		}
		final Set<IOccurrence> occs = descriptor.getOccurrences();
		return new LinkedHashSet<IOccurrence>(occs);
	}

	public synchronized Set<IOccurrence> getOccurrences(IDeclaration declaration) {
		return getOccurrences(declaration.getElement());
	}

	public synchronized Set<IRodinFile> exportFiles() {
		final Set<IRodinFile> files = exportTable.files();
		return new LinkedHashSet<IRodinFile>(files);
	}

	public synchronized Set<IDeclaration> getExports(IRodinFile file) {
		final Set<IDeclaration> exports = exportTable.get(file);
		return new LinkedHashSet<IDeclaration>(exports);
	}

	public PersistentPIM getPersistentData() {
		final Collection<Descriptor> descColl = index.getDescriptors();
		final Descriptor[] descriptors = descColl
				.toArray(new Descriptor[descColl.size()]);
		final PersistentTotalOrder<IRodinFile> persistOrder = order
				.getPersistentData();
		final ExportTable exportClone = exportTable.clone();
		final List<IRodinFile> unprocessed = new ArrayList<IRodinFile>();
		synchronized (unprocessedFiles) {
			unprocessed.addAll(unprocessedFiles);
		}
		return new PersistentPIM(project, descriptors, exportClone,
				persistOrder, unprocessed);
	}
}
