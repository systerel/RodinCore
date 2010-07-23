/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.ICompilationUnit
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - added clear() method
 *     Systerel - removed deprecated methods and occurrence count
 *     Systerel - separation of file and root element
 *     Systerel - now using Token objects
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.RodinDBManager.OpenableMap;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.Messages;
import org.rodinp.internal.core.util.MementoTokenizer.Token;

/**
 * Represents an entire Rodin file. File elements need to be opened before they
 * can be navigated or manipulated.
 * 
 * @see IRodinFile
 */
public final class RodinFile extends Openable implements IRodinFile {
	
	/**
	 * The platform file resource this <code>IRodinFile</code> is based on
	 */
	protected IFile file;
	
	/**
	 * <code>true</code> iff this handle corresponds to the snapshot version of
	 * a Rodin file, <code>false</code> for the mutable version.
	 */
	private boolean snapshot;

	private InternalElementType<?> rootType ;
	
	public RodinFile(IFile file, IRodinElement parent) {
		super((RodinElement) parent);
		this.file = file;
		
		final ElementTypeManager etm = ElementTypeManager.getInstance();
		final FileAssociation association = etm.getFileAssociation(file);
		rootType = association.getRootElementType();
	}
	
	@Override
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
	public void clear(boolean force, IProgressMonitor monitor)
			throws RodinDBException {
		new ClearElementsOperation(this, force).runOperation(monitor);
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

	@Override
	public final void copy(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	@Override
	public void create(boolean force, IProgressMonitor monitor) throws RodinDBException {
		CreateRodinFileOperation op = new CreateRodinFileOperation(this, force);
		op.runOperation(monitor);
	}

	@Override
	protected final RodinFileElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	private final IRodinFile createNewHandle() {
		return new RodinFile(file, getParent());
	}

	@Override
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

	@Override
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
	public IElementType<IRodinFile> getElementType(){
		return ELEMENT_TYPE;
	}

	@Override
	public final IRodinElement getHandleFromMemento(Token token, MementoTokenizer memento) {
		if (token == Token.INTERNAL) {
			return RodinElement.getInternalHandleFromMemento(memento, this);
		}
		return null;
	}

	@Override
	protected final char getHandleMementoDelimiter() {
		return REM_EXTERNAL;
	}

	@Override
	public final IRodinFile getMutableCopy() {
		if (! isSnapshot()) {
			return this;
		}
		return createNewHandle();
	}
	
	@Override
	public final IPath getPath() {
		return file.getFullPath();
	}

	@Override
	public final IFile getResource() {
		return file;
	}
	
	@Override
	public final IRodinFile getSnapshot() {
		if (isSnapshot()) {
			return this;
		}
		final IRodinFile result = createNewHandle();
		((RodinFile) result).snapshot = true;
		return result;
	}

	public final IRodinFile getSimilarElement(IRodinFile newFile) {
		return newFile;
	}

	@Override
	public final IFile getUnderlyingResource() {
		return file;
	}
	
	@Override
	public boolean hasUnsavedChanges() {
		if (isReadOnly()) {
			return false;
		}
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		Buffer buffer = manager.peekAtBuffer(this);
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

	@Override
	public final boolean isSnapshot() {
		return snapshot;
	}
	
	@Override
	public void makeConsistent(IProgressMonitor monitor) throws RodinDBException {
		revert();
		super.makeConsistent(monitor);
	}

	@Override
	public final void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new MoveResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	@Override
	public final void rename(String name, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameResourceElementsOperation(this, name, replace).runOperation(monitor);
	}

	@Override
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

	@Override
	public IInternalElement getRoot() {
		final String name = getBareName();
		return rootType.createInstance(name, this);
	}

	@Override
	public IInternalElementType<?> getRootElementType() {
		return rootType;
	}

}
