/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.ChangeElementAttributeOperation;
import org.rodinp.internal.core.ChangeElementContentsOperation;
import org.rodinp.internal.core.CopyElementsOperation;
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.CreateProblemMarkerOperation;
import org.rodinp.internal.core.DeleteElementsOperation;
import org.rodinp.internal.core.InternalElementInfo;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.MoveElementsOperation;
import org.rodinp.internal.core.RemoveElementAttributeOperation;
import org.rodinp.internal.core.RenameElementsOperation;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;

/**
 * This abstract class is intended to be implemented by clients that contribute
 * to the <code>org.rodinp.core.internalElementTypes</code> extension point.
 * <p>
 * This abstract class should not be used in any other way than subclassing it
 * in database extensions. In particular, database clients must not use it,
 * but rather use its associated interface <code>IInternalElement</code>.
 * </p>
 * 
 * @see IInternalElement
 */
public abstract class InternalElement extends RodinElement implements IInternalElement {
	
	/* Name of this internal element */
	private String name;
	
	public InternalElement(String name, IRodinElement parent) {
		super(parent);
		this.name = name;
		// Name must not be null
		if (name == null)
			throw new IllegalArgumentException();
	}

	public void copy(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	public void create(IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		new CreateInternalElementOperation(this, nextSibling)
				.runOperation(monitor);
	}

	@Override
	public InternalElementInfo createElementInfo() {
		return new InternalElementInfo();
	}
	
	@Deprecated
	public InternalElement createInternalElement(IInternalElementType type,
			String childName, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		
		InternalElement result = getInternalElement(type, childName);
		result.create(nextSibling, monitor);
		return result;
	}

	public void createProblemMarker(IAttributeType.String attributeType,
			int charStart, int charEnd, IRodinProblem problem, Object... args)
			throws RodinDBException {
		
		new CreateProblemMarkerOperation(this, problem, args, attributeType,
				charStart, charEnd).runOperation(null);
	}

	public void createProblemMarker(IAttributeType attributeType, IRodinProblem problem,
			Object... args) throws RodinDBException {

		new CreateProblemMarkerOperation(this, problem, args, attributeType, -1,
				-1).runOperation(null);
	}

	public void delete(boolean force, IProgressMonitor monitor) throws RodinDBException {
		new DeleteElementsOperation(this, force).runOperation(monitor);
	}

	@Override
	public boolean exists() {
		RodinFile rodinFile = getRodinFile();
		if (!rodinFile.exists()) {
			return false;
		}
		try {
			RodinFileElementInfo fileInfo = 
				(RodinFileElementInfo) rodinFile.getElementInfo();
			return fileInfo.containsDescendant(this);
		} catch (RodinDBException e) {
			// file doesn't exist or is not parseable
			return false;
		}
	}

	public IAttributeType[] getAttributeTypes()
			throws RodinDBException {
		return getFileInfo(null).getAttributeTypes(this);
	}
	
	private String getAttributeRawValue(String attrName)
			throws RodinDBException {

		return getFileInfo(null).getAttributeRawValue(this, attrName);
	}

	public boolean getAttributeValue(IAttributeType.Boolean attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getBoolValue(rawValue);
	}

	public IRodinElement getAttributeValue(IAttributeType.Handle attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getHandleValue(rawValue);
	}

	public int getAttributeValue(IAttributeType.Integer attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getIntValue(rawValue);
	}

	public long getAttributeValue(IAttributeType.Long attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getLongValue(rawValue);
	}

	public String getAttributeValue(IAttributeType.String attrType) throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getStringValue(rawValue);
	}

	@Deprecated
	public String getContents() throws RodinDBException {
		return getContents(null);
	}

	@Deprecated
	public String getContents(IProgressMonitor monitor) throws RodinDBException {
		return getFileInfo(monitor).getDescendantContents(this);
	}

	public IResource getCorrespondingResource() {
		return null;
	}

	@Override
	public InternalElementInfo getElementInfo(IProgressMonitor monitor) throws RodinDBException {
		RodinFileElementInfo fileInfo = getFileInfo(monitor);
		InternalElementInfo info = fileInfo.getElementInfo(this);
		if (info != null)
			return info;
		throw newNotPresentException();
	}

	@Override
	public final String getElementName() {
		return name;
	}

	@Override
	public abstract IInternalElementType getElementType();

	protected RodinFileElementInfo getFileInfo(IProgressMonitor monitor) throws RodinDBException {
		RodinFile file = getOpenableParent();
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) file.getElementInfo(monitor);
		if (fileInfo != null)
			return fileInfo;
		throw newNotPresentException();
	}

	@Override
	protected IRodinElement getHandleFromMemento(String token,
			MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_COUNT:
			// just skip the unused count.
			if (! memento.hasMoreTokens()) return this;
			if (! memento.hasMoreTokens()) return this;
			return getHandleFromMemento(memento.nextToken(), memento);
		case REM_INTERNAL:
			return RodinElement.getInternalHandleFromMemento(memento, this);
		}
		return this;
	}

	@Override
	protected void getHandleMemento(StringBuilder buff) {
		getParent().getHandleMemento(buff);
		buff.append(REM_INTERNAL);
		escapeMementoName(buff, getElementType().getId());
		buff.append(REM_TYPE_SEP);
		escapeMementoName(buff, getElementName());
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return REM_INTERNAL;
	}
	
	public InternalElement getInternalElement(IInternalElementType childType,
			String childName) {

		return ((InternalElementType) childType).createInstance(childName, this);
	}

	@Deprecated
	public InternalElement getInternalElement(IInternalElementType childType,
			String childName, int childOccurrenceCount) {

		if (childOccurrenceCount != 1) {
			throw new IllegalArgumentException("Occurrence count must be 1.");
		}
		return getInternalElement(childType, childName);
	}
	
	public final InternalElement getMutableCopy() {
		final RodinFile file = getRodinFile();
		if (! file.isSnapshot()) {
			return this;
		}
		
		// Recreate this handle in the mutable version of its file.
		final RodinFile newFile = file.getMutableCopy();
		return (InternalElement) Util.getSimilarElement(this, newFile);
	}

	@Deprecated
	public int getOccurrenceCount() {
		return 1;
	}

	public Openable getOpenable() {
		return this.getOpenableParent();
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

	public IPath getPath() {
		return getOpenableParent().getPath();
	}

	public IFile getResource() {
		return getUnderlyingResource();
	}

	public RodinFile getRodinFile() {
		return getOpenableParent();
	}

	public final InternalElement getSnapshot() {
		final RodinFile file = getRodinFile();
		if (file.isSnapshot()) {
			return this;
		}
		
		// Recreate this handle in the snapshot version of its file.
		final RodinFile newFile = file.getSnapshot();
		return (InternalElement) Util.getSimilarElement(this, newFile);
	}

	public IFile getUnderlyingResource() {
		return getOpenableParent().getResource();
	}

	public boolean hasAttribute(IAttributeType type) throws RodinDBException {
		return getFileInfo(null).hasAttribute(this, type);
	}

	@Override
	public boolean hasChildren() throws RodinDBException {
		return getChildren().length > 0;
	}

	@Override
	public boolean isReadOnly() {
		return getRodinFile().isReadOnly();
	}

	public final boolean isSnapshot() {
		return getRodinFile().isSnapshot();
	}

	public void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new MoveElementsOperation(this, container, replace),
				sibling, rename, monitor);
		
	}
	
	public void removeAttribute(IAttributeType attrType, IProgressMonitor monitor)
			throws RodinDBException {
		new RemoveElementAttributeOperation(this, attrType).runOperation(monitor);
	}
	
	public void rename(String newName, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameElementsOperation(this, newName, replace).runOperation(monitor);
	}

	private void setAttributeRawValue(String attrName, String newRawValue,
			IProgressMonitor monitor) throws RodinDBException {
		
		new ChangeElementAttributeOperation(this, attrName, newRawValue)
				.runOperation(monitor);
	}

	public void setAttributeValue(IAttributeType.Boolean attrType, boolean newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final String newRawValue = ((AttributeType) attrType).toString(newValue);
		setAttributeRawValue(attrType.getId(), newRawValue, monitor);
	}

	public void setAttributeValue(IAttributeType.Handle attrType, IRodinElement newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final String newRawValue = ((AttributeType) attrType).toString(newValue);
		setAttributeRawValue(attrType.getId(), newRawValue, monitor);
	}

	public void setAttributeValue(IAttributeType.Integer attrType, int newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final String newRawValue = ((AttributeType) attrType).toString(newValue);
		setAttributeRawValue(attrType.getId(), newRawValue, monitor);
	}

	public void setAttributeValue(IAttributeType.Long attrType, long newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final String newRawValue = ((AttributeType) attrType).toString(newValue);
		setAttributeRawValue(attrType.getId(), newRawValue, monitor);
	}

	public void setAttributeValue(IAttributeType.String attrType, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final String newRawValue = ((AttributeType) attrType).toString(newValue);
		setAttributeRawValue(attrType.getId(), newRawValue, monitor);
	}

	@Deprecated
	public void setContents(String contents) throws RodinDBException {
		setContents(contents, null);
	}

	@Deprecated
	public void setContents(String contents, IProgressMonitor monitor) throws RodinDBException {
		new ChangeElementContentsOperation(this, contents).runOperation(monitor);
	}

	@Override
	public InternalElementInfo toStringInfo(int tab, StringBuilder buffer) {
		InternalElementInfo info = null;
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
	}

}
