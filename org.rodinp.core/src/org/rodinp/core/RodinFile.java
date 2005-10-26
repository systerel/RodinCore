/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.ICompilationUnit.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added J2SE 1.5 support
 *******************************************************************************/
package org.rodinp.core;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.OpenableElementInfo;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;


/**
 * Represents an entire Rodin file. File elements need to be opened before they
 * can be navigated or manipulated.
 * <p>
 * This abstract class is intended to be implemented by clients that contribute
 * to the <code>org.rodinp.core.fileElementTypes</code> extension point.
 * </p>
 */
public abstract class RodinFile extends Openable {
	
	/**
	 * The platform project this <code>IRodinProject</code> is based on
	 */
	protected IFile file;

	protected RodinFile(IFile file, IRodinElement parent) {
		super((RodinElement) parent);
		this.file = file;
	}
	
	/**
	 * Finds the elements in this file that correspond to the given element. An
	 * element A corresponds to an element B if:
	 * <ul>
	 * <li>A has the same element name as B.
	 * <li>The parent of A corresponds to the parent of B recursively up to
	 * their respective files.
	 * <li>A exists.
	 * </ul>
	 * Returns <code>null</code> if no such Rodin elements can be found or if
	 * the given element is not included in a file.
	 * 
	 * @param element
	 *            the given element
	 * @return the found elements in this file that correspond to the given
	 *         element
	 */
	public IRodinElement[] findElements(IRodinElement element) {
		// TODO implement findElements().
		return NO_ELEMENTS;
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm,
			Map<IRodinElement, RodinElementInfo> newElements,
			IResource underlyingResource) throws RodinDBException {
		if (! file.exists()) {
			throw newNotPresentException();
		}
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) info;
		return fileInfo.parseFile(pm, this);
	}

	@Override
	protected RodinElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	@Override
	public IRodinElement getHandleFromMemento(String token, MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_INTERNAL:
			// TODO Return the Rodin internal element.
		}
		return null;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return REM_EXTERNAL;
	}

	public abstract String getElementType();

	public IPath getPath() {
		return file.getFullPath();
	}

	public IFile getResource() {
		return file;
	}
	
	@Override
	public String getElementName() {
		return file.getName();
	}

	@Override
	public boolean hasUnsavedChanges() {
		if (isOpen()) {
			try {
				RodinFileElementInfo info = (RodinFileElementInfo) getElementInfo();
				return info.hasUnsavedChanges();
			} catch (RodinDBException e) {
				return false;
			}
		}
		// File not open
		return false;
	}

	@Override
	public boolean isConsistent() {
		return hasUnsavedChanges();
	}
}
