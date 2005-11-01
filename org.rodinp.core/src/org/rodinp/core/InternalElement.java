/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.InternalElementInfo;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;

/**
 * Common implementationfor all internal elements.
 * <p>
 * Internal elements are elements of the database that are stored within files.
 * </p>
 * 
 * TODO describe internal element
 * TODO extract an interface for it
 * 
 * @author Laurent Voisin
 */
public abstract class InternalElement extends RodinElement implements IParent {
	
	/* Name of this internal element */
	private String name;

	/*
	 * A count to uniquely identify this element in the case
	 * that a duplicate named element exists. For example, if
	 * there are two fields in a compilation unit with the
	 * same name, the occurrence count is used to distinguish
	 * them.  The occurrence count starts at 1 (thus the first 
	 * occurrence is occurrence 1, not occurrence 0).
	 */
	public int occurrenceCount = 1;

	public InternalElement(String name, IRodinElement parent) {
		super(parent);
		this.name = name;
		// Name must not be empty
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException();
	}

	@Override
	public InternalElementInfo createElementInfo() {
		return new InternalElementInfo();
	}
	
	/**
	 * Creates and returns a new internal element in this element with the given
	 * type and name. As a side effect, the file containing this element is opened
	 * if it was not already.
	 * 
	 * <p>
	 * A new internal element is always created by this method, whether there
	 * already exists an element with the same name or not.
	 * </p>
	 * 
	 * @param type
	 *            type of the internal element to create
	 * @param childName
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
	 *                <li> The given type is unknown (INVALID_INTERNAL_ELEMENT_TYPE)
	 *                </ul>
	 * @return an internal element in this file with the specified type and name
	 */
	public InternalElement createInternalElement(String type, String childName,
			InternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		InternalElement result = getInternalElement(type, childName);
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

	public InternalElement getInternalElement(String type, String childName) {
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(type, childName, this);
	}

	@Override
	public boolean equals(Object o) {
		if (! (o instanceof InternalElement))
			return false;
		return this.occurrenceCount == ((InternalElement) o).occurrenceCount
				&& super.equals(o);
	}

	@Override
	protected IRodinElement getHandleFromMemento(String token,
			MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_COUNT:
			return getHandleUpdatingCountFromMemento(memento);
		case REM_INTERNAL:
			if (!memento.hasMoreTokens())
				return this;
			String childName = memento.nextToken();
			RodinElement child = getChild(childName);
			return child.getHandleFromMemento(memento);
		}
		return this;
	}

	/*
	 * Update the occurence count of the receiver and creates a Java element handle from the given memento.
	 */
	public IRodinElement getHandleUpdatingCountFromMemento(MementoTokenizer memento) {
		if (!memento.hasMoreTokens()) return this;
		this.occurrenceCount = Integer.parseInt(memento.nextToken());
		if (!memento.hasMoreTokens()) return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento);
	}

	@Override
	protected void getHandleMemento(StringBuffer buff) {
		super.getHandleMemento(buff);
		if (this.occurrenceCount > 1) {
			buff.append(REM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return REM_INTERNAL;
	}

	public IResource getCorrespondingResource() {
		return null;
	}

	public IPath getPath() {
		return getOpenableParent().getPath();
	}

	public IResource getResource() {
		try {
			return getUnderlyingResource();
		} catch (RodinDBException e) {
			return null;
		}
	}

	public IResource getUnderlyingResource() throws RodinDBException {
		return getOpenableParent().getCorrespondingResource();
	}

	/**
	 * Returns the closest openable ancestor of this element (that is its
	 * enclosing file element). Should never return <code>null</code>.
	 * 
	 * @return the enclosing file of this element
	 */
	@Override
	public RodinFile getOpenableParent() {
		RodinElement ancestor = parent;
		while (ancestor != null) {
			if (ancestor instanceof Openable) {
				return (RodinFile) ancestor;
			}
			ancestor = ancestor.getParent();
		}
		assert false;
		return null;
	}

	@Override
	protected void generateInfos(RodinElementInfo info,
			HashMap<IRodinElement, RodinElementInfo> newElements,
			IProgressMonitor monitor) throws RodinDBException {
		
		Openable openableParent = getOpenableParent();
		if (openableParent == null) return;

		RodinElementInfo openableParentInfo = RodinDBManager.getRodinDBManager().getInfo(openableParent);
		if (openableParentInfo == null) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, monitor);
		}
	}
	
	/**
	 * @see IParent 
	 */
	@Override
	public boolean hasChildren() throws RodinDBException {
		return getChildren().length > 0;
	}

	
	/**
	 * Returns the child with the given name or <code>null</code> is there is no such child.
	 * @param childName
	 *   the name of the child
	 * @return the child with the given name
	 */
	protected RodinElement getChild(String childName) {
		// TODO implement getChild
		return null;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public InternalElementInfo getElementInfo(IProgressMonitor monitor) throws RodinDBException {
		RodinFile file = getOpenableParent();
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) file.getElementInfo(monitor);
		InternalElementInfo info = fileInfo.getElementInfo(this);
		if (info != null)
			return info;
		throw newNotPresentException();
	}

	/**
	 * Returns the contents of this internal element. The file containing this
	 * internal element is opened if it was not already.
	 * 
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 * @return the contents of this element.
	 */
	public String getContents() throws RodinDBException {
		return getContents(null);
	}

	/**
	 * Returns the contents of this internal element. The file containing this
	 * internal element is opened if it was not already.
	 * 
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 * @return the contents of this element.
	 */
	public String getContents(IProgressMonitor monitor) throws RodinDBException {
		return getElementInfo(monitor).getContents();
	}

	/**
	 * Sets the contents of this internal element to the provided string. The
	 * file containing this internal element is opened if it was not already.
	 * 
	 * @param contents
	 *            the new contents to set
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 */
	public void setContents(String contents) throws RodinDBException {
		setContents(contents, null);
	}

	/**
	 * Sets the contents of this internal element to the provided string. The
	 * file containing this internal element is opened if it was not already.
	 * 
	 * @param contents
	 *            the new contents to set
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be opened. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing its underlying resource
	 *                </ul>
	 */
	public void setContents(String contents, IProgressMonitor monitor) throws RodinDBException {
		getElementInfo(null).setContents(contents);
	}

}
