/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.basis;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.ChangeElementContentsOperation;
import org.rodinp.internal.core.CopyElementsOperation;
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.DeleteElementsOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.InternalElementInfo;
import org.rodinp.internal.core.MoveElementsOperation;
import org.rodinp.internal.core.RenameElementsOperation;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinElementInfo;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

/**
 * This abstract class is intended to be implemented by clients that contribute
 * to the <code>org.rodinp.core.internalElementTypes</code> extension point.
 * <p>
 * This abstract class should not be used in any other way than subclassing it
 * in database extensions. In particular, database clients should not use it,
 * but rather use its associated interface <code>IInternalElement</code>.
 * </p>
 * 
 * @see IInternalElement
 */
public abstract class InternalElement extends RodinElement implements IInternalElement {
	
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
		// Name must not be null
		if (name == null)
			throw new IllegalArgumentException();
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
		runOperation(new CopyElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	@Override
	public InternalElementInfo createElementInfo() {
		return new InternalElementInfo();
	}
	
	/* (non-Javadoc)
	 * @see IInternalParent
	 */
	public InternalElement createInternalElement(String type, String childName,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		InternalElement result = getInternalElement(type, childName);
		if (result == null) {
			IRodinDBStatus status =
				new RodinDBStatus(IRodinDBStatusConstants.INVALID_INTERNAL_ELEMENT_TYPE, type);
			throw new RodinDBException(status);
		}
		new CreateInternalElementOperation(result, nextSibling).runOperation(monitor);
		return result;
	}

	/* (non-Javadoc)
	 * @see IInternalParent
	 */
	public InternalElement getInternalElement(String childType, String childName) {
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(childType, childName, this);
	}

	/* (non-Javadoc)
	 * @see IInternalParent
	 */
	public InternalElement getInternalElement(String type, String childName, int childOccurrenceCount) {
		InternalElement result = getInternalElement(type, childName);
		result.occurrenceCount = childOccurrenceCount;
		return result;
	}

	/* (non-Javadoc)
	 * @see IElementManipulation
	 */
	public void delete(boolean force, IProgressMonitor monitor) throws RodinDBException {
		new DeleteElementsOperation(this, force).runOperation(monitor);
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
			return RodinElement.getInternalHandleFromMemento(memento, this);
		}
		return this;
	}

	/*
	 * Update the occurence count of the receiver and creates a Rodin element handle from the given memento.
	 */
	public IRodinElement getHandleUpdatingCountFromMemento(MementoTokenizer memento) {
		if (! memento.hasMoreTokens()) return this;
		this.occurrenceCount = Integer.parseInt(memento.nextToken());
		if (! memento.hasMoreTokens()) return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento);
	}

	@Override
	protected void getHandleMemento(StringBuilder buff) {
		getParent().getHandleMemento(buff);
		buff.append(REM_INTERNAL);
		escapeMementoName(buff, getElementType());
		buff.append(REM_TYPE_SEP);
		escapeMementoName(buff, getElementName());
		if (this.occurrenceCount > 1) {
			buff.append(REM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return REM_INTERNAL;
	}

	/* (non-Javadoc)
	 * @see IRodinElement
	 */
	public IResource getCorrespondingResource() {
		return null;
	}

	/* (non-Javadoc)
	 * @see IRodinElement
	 */
	public IPath getPath() {
		return getOpenableParent().getPath();
	}

	/* (non-Javadoc)
	 * @see IRodinElement
	 */
	public IFile getResource() {
		try {
			return getUnderlyingResource();
		} catch (RodinDBException e) {
			return null;
		}
	}

	@Override
	public RodinFile getRodinFile() {
		return getOpenableParent();
	}

	public IFile getUnderlyingResource() throws RodinDBException {
		return getOpenableParent().getResource();
	}

	/*
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

	public int getOccurrenceCount() {
		return occurrenceCount;
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
	
	/*
	 * @see IParent 
	 */
	@Override
	public boolean hasChildren() throws RodinDBException {
		return getChildren().length > 0;
	}

	@Override
	public final String getElementName() {
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

	/* (non-Javadoc)
	 * @see org.rodinp.core.IInternalElement#getContents()
	 */
	public String getContents() throws RodinDBException {
		return getContents(null);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IInternalElement#getContents(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getContents(IProgressMonitor monitor) throws RodinDBException {
		return getElementInfo(monitor).getContents();
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
		runOperation(new MoveElementsOperation(this, container, replace),
				sibling, rename, monitor);
		
	}

	/* (non-Javadoc)
	 * @see IElementManipulation
	 */
	public void rename(String newName, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameElementsOperation(this, newName, replace).runOperation(monitor);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IInternalElement#setContents(java.lang.String)
	 */
	public void setContents(String contents) throws RodinDBException {
		setContents(contents, null);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IInternalElement#setContents(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setContents(String contents, IProgressMonitor monitor) throws RodinDBException {
		new ChangeElementContentsOperation(this, contents).runOperation(monitor);
	}

	@Override
	public Object toStringInfo(int tab, StringBuilder buffer) {
		RodinElementInfo info = null;
		try {
			RodinFile rf = getOpenableParent();
			RodinFileElementInfo rfInfo = (RodinFileElementInfo) rf.getElementInfo();
			info = rfInfo.getElementInfo(this);
		} catch (RodinDBException e) {
			Util.log(e, "can't read element info in toStringInfo");
		}
		this.toStringInfo(tab, buffer, info);
		return info;
	}

	@Override
	protected void toStringName(StringBuilder buffer) {
		buffer.append(getElementName());
		buffer.append("[");
		buffer.append(getElementType());
		buffer.append("]");
		buffer.append(getOccurrenceCount());
	}

}
