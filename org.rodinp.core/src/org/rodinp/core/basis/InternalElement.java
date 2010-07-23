/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added clear() method
 *     Systerel - removed deprecated methods and occurrence count
 *     Systerel - added method getNextSibling()
 *     Systerel - separation of file and root element
 *     Systerel - added inquiry methods
 *     Systerel - added creation of new internal element child
 *     Systerel - generic attribute manipulation
 *     Systerel - now using Token objects
 *******************************************************************************/
package org.rodinp.core.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.AttributeValue;
import org.rodinp.internal.core.ChangeElementAttributeOperation;
import org.rodinp.internal.core.ClearElementsOperation;
import org.rodinp.internal.core.CopyElementsOperation;
import org.rodinp.internal.core.CreateFreshInternalElementOperation;
import org.rodinp.internal.core.CreateInternalElementOperation;
import org.rodinp.internal.core.CreateProblemMarkerOperation;
import org.rodinp.internal.core.DeleteElementsOperation;
import org.rodinp.internal.core.ElementComparer;
import org.rodinp.internal.core.InternalElementInfo;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.MoveElementsOperation;
import org.rodinp.internal.core.Openable;
import org.rodinp.internal.core.RemoveElementAttributeOperation;
import org.rodinp.internal.core.RenameElementsOperation;
import org.rodinp.internal.core.RodinFile;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.Util;
import org.rodinp.internal.core.util.MementoTokenizer.Token;

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
 * @since 1.0
 */
public abstract class InternalElement extends RodinElement implements
		IInternalElement {
	
	/* Name of this internal element */
	private String name;
	
	public InternalElement(String name, IRodinElement parent) {
		super(parent);
		this.name = name;
		// Name must not be null
		if (name == null)
			throw new IllegalArgumentException();
	}

	@Override
	public void clear(boolean force, IProgressMonitor monitor)
			throws RodinDBException {
		new ClearElementsOperation(this, force).runOperation(monitor);
	}

	@Override
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
	public void create(IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		new CreateInternalElementOperation(this, nextSibling)
				.runOperation(monitor);
	}

	@Override
	public final <T extends IInternalElement> T createChild(
			IInternalElementType<T> type, IInternalElement nextSibling,
			IProgressMonitor monitor) throws RodinDBException {
		final CreateFreshInternalElementOperation<T> op = //
		new CreateFreshInternalElementOperation<T>(this, type, nextSibling);
		op.runOperation(monitor);
		return op.getResultElement();
	}

	@Override
	public InternalElementInfo createElementInfo() {
		return new InternalElementInfo();
	}
	
	@Override
	public void createProblemMarker(IAttributeType.String attributeType,
			int charStart, int charEnd, IRodinProblem problem, Object... args)
			throws RodinDBException {
		
		new CreateProblemMarkerOperation(this, problem, args, attributeType,
				charStart, charEnd).runOperation(null);
	}

	@Override
	public void createProblemMarker(IAttributeType attributeType, IRodinProblem problem,
			Object... args) throws RodinDBException {

		new CreateProblemMarkerOperation(this, problem, args, attributeType, -1,
				-1).runOperation(null);
	}

	@Override
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

	@Override
	public IAttributeType[] getAttributeTypes()
			throws RodinDBException {
		return getFileInfo(null).getAttributeTypes(this);
	}
	
	@Override
	public final IAttributeValue[] getAttributeValues() throws RodinDBException {
		return getFileInfo(null).getAttributeValues(this);
	}

	@Override
	public final IAttributeValue getAttributeValue(IAttributeType attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType<?>) attrType);
	}

	@Override
	public boolean getAttributeValue(IAttributeType.Boolean attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType.Boolean) attrType);
	}

	@Override
	public IRodinElement getAttributeValue(IAttributeType.Handle attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType.Handle) attrType);
	}

	@Override
	public int getAttributeValue(IAttributeType.Integer attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType.Integer) attrType);
	}

	@Override
	public long getAttributeValue(IAttributeType.Long attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType.Long) attrType);
	}

	@Override
	public String getAttributeValue(IAttributeType.String attrType)
			throws RodinDBException {
		return getFileInfo(null).getAttributeValue(this,
				(AttributeType.String) attrType);
	}

	@Override
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
	public abstract IInternalElementType<? extends IInternalElement> getElementType();

	protected RodinFileElementInfo getFileInfo(IProgressMonitor monitor) throws RodinDBException {
		RodinFile file = getRodinFile();
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) file.getElementInfo(monitor);
		if (fileInfo != null)
			return fileInfo;
		throw newNotPresentException();
	}

	@Override
	protected IRodinElement getHandleFromMemento(Token token,
			MementoTokenizer memento) {
		if (token == Token.INTERNAL) {
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
	
	@Override
	public <T extends IInternalElement> T getInternalElement(
			IInternalElementType<T> childType,
			String childName) {

		return ((InternalElementType<T>) childType).createInstance(childName, this);
	}

	@Override
	public final InternalElement getMutableCopy() {
		final RodinFile file = getRodinFile();
		if (! file.isSnapshot()) {
			return this;
		}
		
		// Recreate this handle in the mutable version of its file.
		final IRodinFile newFile = file.getMutableCopy();
		return (InternalElement) getSimilarElement(newFile);
	}

	@Override
	public final IInternalElement getNextSibling() throws RodinDBException {
		final RodinElement[] siblings = getParent().getChildren();
		final int len = siblings.length;
		for (int i = 0; i < len; ++i) {
			if (siblings[i].equals(this)) {
				if (i + 1 < len) {
					return (IInternalElement) siblings[i + 1];
				}
				return null;
			}
		}
		throw newNotPresentException();
	}

	@Override
	public RodinFile getOpenable() {
		return getRodinFile();
	}

	@Override
	public IPath getPath() {
		return getRodinFile().getPath();
	}

	@Override
	public IFile getResource() {
		return getUnderlyingResource();
	}

	@Override
	public RodinFile getRodinFile() {
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
	public final InternalElement getRoot() {
		InternalElement elem = this;
		while (!elem.isRoot()) {
			elem = (InternalElement) elem.parent;
		}
		return elem;
	}
	
	@Override
	public final InternalElement getSnapshot() {
		final RodinFile file = getRodinFile();
		if (file.isSnapshot()) {
			return this;
		}
		
		// Recreate this handle in the snapshot version of its file.
		final IRodinFile newFile = file.getSnapshot();
		return (InternalElement) getSimilarElement(newFile);
	}

	@Override
	public final IInternalElement getSimilarElement(IRodinFile newFile) {
		// Special case for root element
		if (parent instanceof IRodinFile) {
			return newFile.getRoot();
		}
		
		final IInternalElement myParent = (IInternalElement) getParent();
		final IInternalElement newParent = myParent.getSimilarElement(newFile);
		final IInternalElementType<?> newType = getElementType();
		final String newName = getElementName();
		return newParent.getInternalElement(newType, newName);
	}

	@Override
	public IFile getUnderlyingResource() {
		return getRodinFile().getResource();
	}

	@Override
	public boolean hasAttribute(IAttributeType type) throws RodinDBException {
		return getFileInfo(null).hasAttribute(this, type);
	}

	@Override
	public boolean hasChildren() throws RodinDBException {
		return getChildren().length > 0;
	}

	@Override
	public boolean hasSameAttributes(IInternalElement other)
			throws RodinDBException {
		return ElementComparer.hasSameAttributes(this, (InternalElement) other);
	}

	@Override
	public boolean hasSameChildren(IInternalElement other)
			throws RodinDBException {
		return ElementComparer.hasSameChildren(this, (InternalElement) other);
	}

	@Override
	public boolean hasSameContents(IInternalElement other)
			throws RodinDBException {
		return ElementComparer.hasSameContents(this, (InternalElement) other);
	}

	@Override
	public boolean isReadOnly() {
		return getRodinFile().isReadOnly();
	}

	@Override
	public final boolean isSnapshot() {
		return getRodinFile().isSnapshot();
	}

	@Override
	public void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new MoveElementsOperation(this, container, replace),
				sibling, rename, monitor);
		
	}
	
	@Override
	public void removeAttribute(IAttributeType attrType, IProgressMonitor monitor)
			throws RodinDBException {
		new RemoveElementAttributeOperation(this, attrType).runOperation(monitor);
	}
	
	@Override
	public void rename(String newName, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameElementsOperation(this, newName, replace).runOperation(monitor);
	}

	@Override
	public void setAttributeValue(IAttributeType.Boolean attrType,
			boolean newValue, IProgressMonitor monitor) throws RodinDBException {
		final AttributeType.Boolean iType = (AttributeType.Boolean) attrType;
		final AttributeValue<?,?> attrValue = iType.makeValue(newValue);
		new ChangeElementAttributeOperation(this, attrValue)
				.runOperation(monitor);
	}

	@Override
	public final void setAttributeValue(IAttributeType.Handle attrType,
			IRodinElement newValue, IProgressMonitor monitor)
			throws RodinDBException {
		if (newValue == null) {
			throw new NullPointerException();
		}
		final AttributeType.Handle iType = (AttributeType.Handle) attrType;
		final AttributeValue<?,?> attrValue = iType.makeValue(newValue);
		new ChangeElementAttributeOperation(this, attrValue)
				.runOperation(monitor);
	}

	@Override
	public final void setAttributeValue(IAttributeType.Integer attrType,
			int newValue, IProgressMonitor monitor) throws RodinDBException {
		final AttributeType.Integer iType = (AttributeType.Integer) attrType;
		final AttributeValue<?,?> attrValue = iType.makeValue(newValue);
		new ChangeElementAttributeOperation(this, attrValue)
				.runOperation(monitor);
	}

	@Override
	public final void setAttributeValue(IAttributeType.Long attrType, long newValue,
			IProgressMonitor monitor) throws RodinDBException {
		final AttributeType.Long iType = (AttributeType.Long) attrType;
		final AttributeValue<?,?> attrValue = iType.makeValue(newValue);
		new ChangeElementAttributeOperation(this, attrValue)
				.runOperation(monitor);
	}

	@Override
	public final void setAttributeValue(IAttributeType.String attrType,
			String newValue, IProgressMonitor monitor) throws RodinDBException {
		if (newValue == null) {
			throw new NullPointerException();
		}
		final AttributeType.String iType = (AttributeType.String) attrType;
		final AttributeValue<?,?> attrValue = iType.makeValue(newValue);
		new ChangeElementAttributeOperation(this, attrValue)
				.runOperation(monitor);
	}

	@Override
	public void setAttributeValue(IAttributeValue newValue,
			IProgressMonitor monitor) throws RodinDBException {
		if (newValue == null) {
			throw new NullPointerException();
		}
		new ChangeElementAttributeOperation(this,
				(AttributeValue<?, ?>) newValue).runOperation(monitor);
	}

	@Override
	public InternalElementInfo toStringInfo(int tab, StringBuilder buffer) {
		InternalElementInfo info = null;
		try {
			RodinFile rf = getRodinFile();
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
