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
import org.rodinp.internal.core.SaveRodinFileOperation;
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
	public void close() throws RodinDBException {
		super.close();
		// Also close the associated snapshot if this is a mutable file.
		if (! snapshot) {
			getSnapshot().close();
		}
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

	@Override
	protected final RodinElementInfo createElementInfo() {
		return new RodinFileElementInfo();
	}

	private final RodinFile createNewHandle() {
		final ElementTypeManager etm =
			ElementTypeManager.getElementTypeManager();
		return 
			etm.createRodinFileHandle(getRodinProject(), getElementName());
	}

	public final InternalElement createInternalElement(String type, String name,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException {
		
		InternalElement result = getInternalElement(type, name);
		if (result == null) {
			IRodinDBStatus status = new RodinDBStatus(
					IRodinDBStatusConstants.INVALID_INTERNAL_ELEMENT_TYPE,
					type
			);
			throw new RodinDBException(status);
		}
		new CreateInternalElementOperation(result, nextSibling).runOperation(monitor);
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
	public final String getElementName() {
		return file.getName();
	}

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

	public final InternalElement getInternalElement(String type, String name) {
		ElementTypeManager manager = ElementTypeManager.getElementTypeManager();
		return manager.createInternalElementHandle(type, name, this);
	}

	@Deprecated
	public final InternalElement getInternalElement(String type, String name, int occurrenceCount) {
		if (occurrenceCount != 1) {
			throw new IllegalArgumentException("Occurrence count must be 1");
		}
		return getInternalElement(type, name);
	}

	public final RodinFile getMutableCopy() {
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
	
	public final RodinFile getSnapshot() {
		if (isSnapshot()) {
			return this;
		}
		final RodinFile result = createNewHandle();
		result.snapshot = true;
		return result;
	}
	
	public final IFile getUnderlyingResource() {
		return file;
	}
	
	@Override
	public final boolean hasUnsavedChanges() {
		try {
			RodinFileElementInfo info = (RodinFileElementInfo) getElementInfo();
			return info != null && info.hasUnsavedChanges();
		} catch (RodinDBException e) {
			return false;
		}
	}

	@Override
	public final boolean isConsistent() {
		return hasUnsavedChanges();
	}

	@Override
	public boolean isReadOnly() {
		return isSnapshot() || super.isReadOnly();
	}

	public final boolean isSnapshot() {
		return snapshot;
	}
	
	public final void move(IRodinElement container, IRodinElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws RodinDBException {

		if (container == null) {
			throw new IllegalArgumentException(Messages.operation_nullContainer); 
		}
		runOperation(new CopyResourceElementsOperation(this, container, replace),
				sibling, rename, monitor);
	}

	public final void rename(String name, boolean replace, IProgressMonitor monitor) throws RodinDBException {
		new RenameResourceElementsOperation(this, name, replace).runOperation(monitor);
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
	protected void toStringName(StringBuilder buffer) {
		super.toStringName(buffer);
		if (snapshot) {
			buffer.append('!');
		}
	}

}
