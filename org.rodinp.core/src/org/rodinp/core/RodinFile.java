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
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.DeleteResourceElementsOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.OpenableElementInfo;
import org.rodinp.internal.core.RodinDBStatus;
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
public abstract class RodinFile extends Openable implements IParent {
	
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
		//return false;
		return fileInfo.parseFile(pm, this);
	}

	@Override
	protected RodinElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	/**
	 * Creates and returns a new internal element in this file with the given
	 * type and name. As a side effect, this file is opened if it was not already.
	 * 
	 * <p>
	 * A new internal element is always created by this method, whether there
	 * already exists an element with the same name or not.
	 * </p>
	 * 
	 * @param type
	 *            type of the internal element to create
	 * @param name
	 *            name of the internal element to create. Should be
	 *            <code>null</code> if the new element is unnamed.
	 * @param nextSibling
	 *            succesor node of the internal element to create. Must be a
	 *            child of this element or <code>null</code> (in that latter
	 *            case, the new element will be the last child of this element).
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be created. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                creating an underlying resource
	 *                <li> The given type is unknown
	 *                </ul>
	 * @return an internal element in this file with the specified type and name
	 */
	public InternalElement createInternalElement(String type, String name,
			InternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		InternalElement result = getInternalElement(type, name);
		if (result == null) {
			IRodinDBStatus status =
				new RodinDBStatus(IRodinDBStatusConstants.INVALID_INTERNAL_ELEMENT_TYPE, type);
			throw new RodinDBException(status);
		}
		CreateInternalElementOperation op =
			new CreateInternalElementOperation(result, nextSibling);
		op.runOperation(monitor);
		return result;
	}
	
	/**
	 * Deletes this element, forcing if specified and necessary.
	 *
	 * @param force a flag controlling whether underlying resources that are not
	 *    in sync with the local file system will be tolerated (same as the force flag
	 *	  in IResource operations).
	 * @param monitor a progress monitor
	 * @exception RodinDBException if this element could not be deleted. Reasons include:
	 * <ul>
	 * <li> This Rodin element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while updating an underlying resource (CORE_EXCEPTION)</li>
	 * <li> This element is read-only (READ_ONLY)</li>
	 * </ul>
	 */
	public void delete(boolean force, IProgressMonitor monitor) throws RodinDBException {
		new DeleteResourceElementsOperation(this, force).runOperation(monitor);
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

	/**
	 * Returns a handle to a top-level internal element with the given type and
	 * name. This is a handle-only method. The internal element may or may not
	 * be present.
	 * 
	 * @param type
	 *            type of the internal element
	 * @param name
	 *            name of the internal element
	 * @return the internal element with the given type and name or
	 *         <code>null</code> if the given element type is unknown.
	 */
	public InternalElement getInternalElement(String type, String name) {
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(type, name, this);
	}

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

	@Override
	public void save(IProgressMonitor progress, boolean force) throws RodinDBException {
		super.save(progress, force);
		
		// Then save the file contents.
		if (! hasUnsavedChanges())
			return;

		RodinFileElementInfo info = (RodinFileElementInfo) getElementInfo();
		info.saveToFile(this, force, progress);
	}
}
