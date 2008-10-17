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

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexingToolkit;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IRodinLocation;

public class IndexingToolkit implements IIndexingToolkit {

	private final IRodinFile file;
	private final Map<IInternalElement, IDeclaration> imports;

	private final Map<IInternalElement, IDeclaration> declarations;
	private final IndexingResult result;
	private final IProgressMonitor monitor;

	/**
	 * The given imports are assumed to be up-to-date.
	 * <p>
	 * The given ExportTable is assumed to be unchanged since latest indexing of
	 * the given file (empty if it never was indexed). It will be updated
	 * through calls to {@link IndexingToolkit#export(IDeclaration)}.
	 * </p>
	 * <p>
	 * The given RodinIndex, FileTable and NameTable are supposed to be just
	 * coherent with each other. They will be cleaned here.
	 * </p>
	 * 
	 * @param file
	 * @param imports
	 * @param monitor
	 */
	public IndexingToolkit(IRodinFile file,
			Map<IInternalElement, IDeclaration> imports,
			IProgressMonitor monitor) {

		this.file = file;
		this.imports = imports;

		this.declarations = new HashMap<IInternalElement, IDeclaration>();
		this.result = new IndexingResult(file);
		this.result.setDeclarations(declarations);
		this.monitor = monitor;
	}

	public IDeclaration declare(IInternalElement element, String name) {

		if (!isLocal(element)) {
			throw new IllegalArgumentException(
					"Element must be in indexed file: "
							+ element.getRodinFile());
		}

		if (declarations.containsKey(element)) {
			throw new IllegalArgumentException(
					"Element has already been declared: " + element);
		}

		final Declaration declaration = new Declaration(element, name);
		declarations.put(element, declaration);
		
		return declaration;
	}

	public void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IRodinLocation location) {
		final IInternalElement element = declaration.getElement();
		
		if (!verifyOccurrence(element, location)) {
			throw new IllegalArgumentException(
					"Incorrect occurrence for element: " + element);
		}

		final IOccurrence occurrence = new Occurrence(kind, location);
		result.addOccurrence(element, occurrence);
	}

	public void export(IDeclaration declaration) {

		final IInternalElement element = declaration.getElement();

		// TODO simplify by not testing any condition and performing two lookups
		// in a row
		if (isLocal(element)) {
			declaration = declarations.get(element);
		} else if (isImported(element)) {
			declaration = imports.get(element);
		} else {
			throw new IllegalArgumentException(
					"Cannot export an element that is neither local nor imported.");
		}

		// modifications storage
		result.addExport(declaration);
	}

	public IRodinFile getRodinFile() {
		return file;
	}
	
	public IInternalElement getRootToIndex() {
		return file.getRoot();
	}

	public IDeclaration[] getImports() {
		return imports.values().toArray(new IDeclaration[imports.size()]);
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

	public boolean isCancelled() {
		if (monitor == null) {
			return false;
		}
		return monitor.isCanceled();
	}

	// to call before getResult;
	public void complete() {
		result.setSuccess(!isCancelled());
	}

	public IIndexingResult getResult() {
		return result;
	}

}
