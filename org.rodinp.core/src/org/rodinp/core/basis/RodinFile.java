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
package org.rodinp.core.basis;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.CopyResourceElementsOperation;
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.DeleteResourceElementsOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.OpenableElementInfo;
import org.rodinp.internal.core.RenameResourceElementsOperation;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;

/**
 * Represents an entire Rodin file. File elements need to be opened before they
 * can be navigated or manipulated.
 * <p>
 * This abstract class is intended to be implemented by clients that contribute
 * to the <code>org.rodinp.core.fileElementTypes</code> extension point.
 * </p>
 * <p>
 * This abstract class should not be used in any other way than subclassing it
 * in database extensions. In particular, database clients should not use it,
 * but rather use its associated interface <code>IRodinFile</code>.
 * </p>
 * 
 * @see IRodinFile
 */
public abstract class RodinFile extends Openable implements IRodinFile {
	
	/**
	 * The platform project this <code>IRodinProject</code> is based on
	 */
	protected IFile file;

	protected RodinFile(IFile file, IRodinElement parent) {
		super((RodinElement) parent);
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinFile#findElements(org.rodinp.core.IRodinElement)
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

	/* (non-Javadoc)
	 * @see IElementManipulation
	 */
	public void copy(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	@Override
	protected RodinElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IInternalParent#createInternalElement(java.lang.String, java.lang.String, org.rodinp.core.IInternalElement, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public InternalElement createInternalElement(String type, String name,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		InternalElement result = getInternalElement(type, name);
		if (result == null) {
			IRodinDBStatus status =
				new RodinDBStatus(IRodinDBStatusConstants.INVALID_INTERNAL_ELEMENT_TYPE, type);
			throw new RodinDBException(status);
		}
		new CreateInternalElementOperation(result, nextSibling).runOperation(monitor);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see IElementManipulation
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

	/* (non-Javadoc)
	 * @see IInternalParent
	 */
	public InternalElement getInternalElement(String type, String name) {
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(type, name, this);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinFile#getPath()
	 */
	public IPath getPath() {
		return file.getFullPath();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinFile#getResource()
	 */
	public IFile getResource() {
		return file;
	}
	
	@Override
	public String getElementName() {
		return file.getName();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinFile#hasUnsavedChanges()
	 */
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

	/* (non-Javadoc)
	 * @see IElementManipulation
	 */
	public void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	/* (non-Javadoc)
	 * @see IElementManipulation
	 */
	public void rename(String name, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameResourceElementsOperation(this, name, replace).runOperation(monitor);
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
