/*******************************************************************************
 * Copyright (c) 2005-2007 ETH Zurich.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.AttributeType;
import org.rodinp.internal.core.Buffer;
import org.rodinp.internal.core.ChangeElementAttributeOperation;
import org.rodinp.internal.core.CopyResourceElementsOperation;
import org.rodinp.internal.core.CreateRodinFileOperation;
import org.rodinp.internal.core.DeleteResourceElementsOperation;
import org.rodinp.internal.core.ElementComparer;
import org.rodinp.internal.core.FileElementType;
import org.rodinp.internal.core.IInternalParentX;
import org.rodinp.internal.core.InternalElementType;
import org.rodinp.internal.core.MoveResourceElementsOperation;
import org.rodinp.internal.core.OpenableElementInfo;
import org.rodinp.internal.core.RemoveElementAttributeOperation;
import org.rodinp.internal.core.RenameResourceElementsOperation;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.RodinFileElementInfo;
import org.rodinp.internal.core.RodinProject;
import org.rodinp.internal.core.SaveRodinFileOperation;
import org.rodinp.internal.core.RodinDBManager.OpenableMap;
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
public abstract class RodinFile extends Openable implements IRodinFile,
		IInternalParentX {
	
	/**
	 * The platform file resource this <code>IRodinFile</code> is based on
	 */
	protected IFile file;
	
	/**
	 * <code>true</code> iff this handle corresponds to the snapshot version of
	 * a Rodin file, <code>false</code> for the mutable version.
	 */
	private boolean snapshot;

	protected RodinFile(IFile file, IRodinElement parent) {
		super((RodinElement) parent);
		this.file = file;
	}
	
	public final IRodinElement[] findElements(IRodinElement element) {
		// TODO implement findElements().
		return NO_ELEMENTS;
	}

	@Override
	protected final boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, OpenableMap newElements,
			IResource underlyingResource) throws RodinDBException {

		if (! file.exists()) {
			throw newNotPresentException();
		}
		RodinFileElementInfo fileInfo = (RodinFileElementInfo) info;
		//return false;
		return fileInfo.parseFile(pm, this);
	}

	@Override
	public void close() throws RodinDBException {
		super.close();
		// Also close the associated snapshot if this is a mutable file.
		if (! snapshot) {
			getSnapshot().close();
		}
	}

	@Override
	public void closing(OpenableElementInfo info) {
		final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
		rodinDBManager.removeBuffer(this.getSnapshot(), true);
		rodinDBManager.removeBuffer(this.getMutableCopy(), false);
		super.closing(info);
	}

	public final void copy(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	public void create(boolean force, IProgressMonitor monitor) throws RodinDBException {
		CreateRodinFileOperation op = new CreateRodinFileOperation(this, force);
		op.runOperation(monitor);
	}

	@Override
	protected final RodinFileElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	private final IRodinFile createNewHandle() {
		final FileElementType<?> type = (FileElementType<?>) getElementType();
		return type.createInstance(file, (RodinProject) getParent());
	}

	@Deprecated
	public final <T extends IInternalElement> T createInternalElement(
			IInternalElementType<T> type, String name,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		T result = getInternalElement(type, name);
		result.create(nextSibling, monitor);
		return result;
	}
	
	public final void delete(boolean force, IProgressMonitor monitor) throws RodinDBException {
		new DeleteResourceElementsOperation(this, force).runOperation(monitor);
	}

	@Override
	public final boolean equals(Object o) {
		if (super.equals(o)) {
			RodinFile other = (RodinFile) o;
			return snapshot == other.snapshot;
		}
		return false;
	}

	@Override
	public boolean exists() {
		if (snapshot) {
			return getMutableCopy().exists();
		}
		return super.exists();
	}

	public String getBareName() {
		String name = getElementName();
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1) {
			return name;
		} else {
			return name.substring(0, lastDot);
		}
	}

	@Override
	public final String getElementName() {
		return file.getName();
	}

	@Override
	public abstract IFileElementType<? extends IRodinFile> getElementType();

	@Override
	public final IRodinElement getHandleFromMemento(String token, MementoTokenizer memento) {
		switch (token.charAt(0)) {
		case REM_INTERNAL:
			return RodinElement.getInternalHandleFromMemento(memento, this);
		}
		return null;
	}

	@Override
	protected final char getHandleMementoDelimiter() {
		return REM_EXTERNAL;
	}

	public final <T extends IInternalElement> T getInternalElement(
			IInternalElementType<T> type, String name) {

		return ((InternalElementType<T>) type).createInstance(name, this);
	}

	@Deprecated
	public final <T extends IInternalElement> T getInternalElement(
			IInternalElementType<T> type, String name, int occurrenceCount) {

		if (occurrenceCount != 1) {
			throw new IllegalArgumentException("Occurrence count must be 1");
		}
		return getInternalElement(type, name);
	}

	public final IRodinFile getMutableCopy() {
		if (! isSnapshot()) {
			return this;
		}
		return createNewHandle();
	}
	
	public final IPath getPath() {
		return file.getFullPath();
	}

	public final IFile getResource() {
		return file;
	}
	
	public final IRodinFile getSnapshot() {
		if (isSnapshot()) {
			return this;
		}
		final RodinFile result = (RodinFile) createNewHandle();
		result.snapshot = true;
		return result;
	}

	public final IRodinFile getSimilarElement(IRodinFile newFile) {
		return newFile;
	}

	public final IFile getUnderlyingResource() {
		return file;
	}
	
	public boolean hasSameAttributes(IInternalParent other)
			throws RodinDBException {
		return ElementComparer
				.hasSameAttributes(this, (IInternalParentX) other);
	}

	public boolean hasSameChildren(IInternalParent other)
			throws RodinDBException {
		return ElementComparer.hasSameChildren(this, (IInternalParentX) other);
	}

	public boolean hasSameContents(IInternalParent other)
			throws RodinDBException {
		return ElementComparer.hasSameContents(this, (IInternalParentX) other);
	}

	@Override
	public boolean hasUnsavedChanges() {
		if (isReadOnly()) {
			return false;
		}
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		Buffer buffer = manager.getBuffer(this);
		return buffer != null && buffer.hasUnsavedChanges();
	}

	@Override
	public final boolean isConsistent() {
		return ! hasUnsavedChanges();
	}

	@Override
	public boolean isReadOnly() {
		return isSnapshot() || super.isReadOnly();
	}

	public final boolean isSnapshot() {
		return snapshot;
	}
	
	@Override
	public void makeConsistent(IProgressMonitor monitor) throws RodinDBException {
		revert();
		super.makeConsistent(monitor);
	}

	public final void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new MoveResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	public final void rename(String name, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameResourceElementsOperation(this, name, replace).runOperation(monitor);
	}

	public final void revert() throws RodinDBException {
		RodinDBManager.getRodinDBManager().removeBuffer(this, true);
		close();
	}

	@Override
	public final void save(IProgressMonitor monitor, boolean force) throws RodinDBException {
		super.save(monitor, force);
		
		// Then save the file contents.
		if (! hasUnsavedChanges())
			return;

		new SaveRodinFileOperation(this, force).runOperation(monitor);
	}

	@Override
	public final void save(IProgressMonitor monitor, boolean force,
			boolean keepHistory) throws RodinDBException {
		super.save(monitor, force);
		
		// Then save the file contents.
		if (! hasUnsavedChanges())
			return;

		new SaveRodinFileOperation(this, force, keepHistory).runOperation(monitor);
	}

	@Override
	protected void toStringName(StringBuilder buffer) {
		super.toStringName(buffer);
		if (snapshot) {
			buffer.append('!');
		}
	}

	private RodinFileElementInfo getFileInfo(IProgressMonitor monitor)
	throws RodinDBException {
		
		return (RodinFileElementInfo) getElementInfo(monitor);
	}
	
	public IAttributeType[] getAttributeTypes() throws RodinDBException {
		return getFileInfo(null).getAttributeTypes(this);
	}

	public String getAttributeRawValue(String attrName)
			throws RodinDBException {
		return getFileInfo(null).getAttributeRawValue(this, attrName);
	}

	public boolean getAttributeValue(IAttributeType.Boolean attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getBoolValue(rawValue);
	}

	public IRodinElement getAttributeValue(IAttributeType.Handle attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getHandleValue(rawValue);
	}

	public int getAttributeValue(IAttributeType.Integer attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getIntValue(rawValue);
	}

	public long getAttributeValue(IAttributeType.Long attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getLongValue(rawValue);
	}

	public String getAttributeValue(IAttributeType.String attrType)
			throws RodinDBException {
		final String rawValue = getAttributeRawValue(attrType.getId());
		return ((AttributeType) attrType).getStringValue(rawValue);
	}

	public boolean hasAttribute(IAttributeType type) throws RodinDBException {
		return getFileInfo(null).hasAttribute(this, type);
	}

	public void removeAttribute(IAttributeType type, IProgressMonitor monitor) throws RodinDBException {
		new RemoveElementAttributeOperation(this, type).runOperation(monitor);
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

}
