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

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexingBridge;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IInternalLocation;

/**
 * Bridge implementation.
 * <p>
 * Enforces the following constraints:
 * <ul>
 * <li>an element cannot have an occurrence if it has not been declared before</li>
 * <li>an element cannot be declared more than once</li>
 * <li>declared elements must be local to the file</li>
 * <li>occurring elements must be either local or imported</li>
 * <li>declared elements should have one or more occurrences when indexing
 * completes</li>
 * </ul>
 * </p>
 * <p>
 * Calling {@link #complete()} at the end of indexing is mandatory: it checks
 * the result and sets the success value.
 * </p>
 * 
 */
public class IndexingBridge implements IIndexingBridge {

	private final IRodinFile file;
	private final Map<IInternalElement, IDeclaration> imports;

	private final IndexingResult result;
	private final IProgressMonitor monitor;

	public IndexingBridge(IRodinFile file,
			Map<IInternalElement, IDeclaration> imports,
			IProgressMonitor monitor) {

		this.file = file;
		this.imports = imports;
		this.result = new IndexingResult(file);
		this.monitor = monitor;
	}

	@Override
	public IDeclaration[] getDeclarations() {
		// must not return declColl nor
		// Collections.unmodifiableCollection(declColl)
		// as ConcurrentModificationException would raise if a client declares
		// new elements while iterating on the unmodifiable view of declarations
		final Collection<IDeclaration> declColl = result.getDeclarations();
		return declColl.toArray(new IDeclaration[declColl.size()]);
	}

	@Override
	public IDeclaration declare(IInternalElement element, String name) {

		if (element == null) {
			throw new NullPointerException("null element");
		}
		if (name == null) {
			throw new NullPointerException("null name");
		}
		
		if (name.length() == 0) {
			throw new IllegalArgumentException(
					"Declared name must not be empty");
		}
		
		if (!isLocal(element)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (result.isDeclared(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		final Declaration declaration = new Declaration(element, name);
		result.putDeclaration(declaration);

		return declaration;
	}

	@Override
	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IInternalLocation location) {
		final IInternalElement element = declaration.getElement();

		if (!verifyOccurrence(element, location)) {
			throw new IllegalArgumentException(
					"Incorrect occurrence for element: " + element);
		}

		final IOccurrence occurrence =
				new Occurrence(kind, location, declaration);
		result.addOccurrence(element, occurrence);
	}

	@Override
	public void export(IDeclaration declaration) {

		final IInternalElement element = declaration.getElement();
		if (!isLocalOrImported(element)) {
			throw new IllegalArgumentException(
					"Cannot export an element that is neither local nor imported.");
		}

		// modifications storage
		result.addExport(declaration);
	}

	public IRodinFile getRodinFile() {
		return file;
	}

	@Override
	public IInternalElement getRootToIndex() {
		return file.getRoot();
	}

	@Override
	public IDeclaration[] getImports() {
		return imports.values().toArray(new IDeclaration[imports.size()]);
	}

	private boolean verifyOccurrence(IInternalElement element,
			IInternalLocation location) {
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

	@Override
	public boolean isCancelled() {
		if (monitor == null) {
			return false;
		}
		return monitor.isCanceled();
	}

	// to call before getResult;
	public void complete() {
		result.removeNonOccurringElements();
		result.setSuccess(!isCancelled());
	}

	public IIndexingResult getResult() {
		return result.clone();
	}

}
