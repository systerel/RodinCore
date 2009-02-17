/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.Openable.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.internal.core.RodinDBManager.OpenableMap;

/**
 * Abstract class for implementations of Rodin elements which are IOpenable.
 * 
 * @see IRodinElement
 * @see IOpenable
 */
public abstract class Openable extends RodinElement implements IOpenable {

	protected Openable(RodinElement parent) {
		super(parent);
	}

	/**
	 * Builds this element's structure and properties in the given
	 * info object, based on this element's current contents (reuse buffer
	 * contents if this element has an open buffer, or resource contents
	 * if this element does not have an open buffer). Children
	 * are placed in the given newElements table (note, this element
	 * has already been placed in the newElements table). Returns true
	 * if successful, or false if an error is encountered while determining
	 * the structure of this element.
	 */
	protected abstract boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm,
			OpenableMap newElements,
			IResource underlyingResource) throws RodinDBException;

	/*
	 * Returns whether this element can be removed from the Rodin database cache
	 * to make space.
	 */
	public boolean canBeRemovedFromCache() {
		return !hasUnsavedChanges();
	}

	/*
	 * @see IOpenable
	 */
	public void close() throws RodinDBException {
		RodinDBManager.getRodinDBManager().removeInfoAndChildren(this);
	}

	/**
	 * This element is being closed. Do any necessary cleanup.
	 */
	public void closing(OpenableElementInfo info) {
		// TODO what cleanup to do?
	}

	/*
	 * Returns a new element info for this element.
	 */
	@Override
	protected abstract OpenableElementInfo createElementInfo();

	@Override
	public boolean exists() {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		if (manager.getInfo(this) != null)
			return true;
		if (parent == null) {
			// The Rodin DB always exists
			return true;
		}
		if (!parent.exists()) {
			return false;
		}
		try {
			RodinElementInfo parentInfo = parent.getElementInfo(null);
			return parentInfo.containsChild(this);
		} catch (RodinDBException e) {
			// Parent can't be open
			return false;
		}
	}

	protected void generateInfos(OpenableElementInfo info, 
			OpenableMap newElements,
			IProgressMonitor monitor) throws RodinDBException {

		if (RodinDBManager.VERBOSE) {
			String element = getElementType().toString();
			System.out.println(Thread.currentThread()
					+ " OPENING " + element + " " + this.toStringWithAncestors()); //$NON-NLS-1$//$NON-NLS-2$
		}

		// open the parent if necessary
		openParent(info, newElements, monitor);
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();

		// puts the info before building the structure so that questions to the
		// handle behave as if the element existed
		// (case of Rodin files becoming working copies)
		newElements.put(this, info);

		// build the structure of the openable (this will open the buffer if
		// needed)
		try {
			boolean isStructureKnown = buildStructure(info,
					monitor, newElements, getResource());
			info.setIsStructureKnown(isStructureKnown);
		} catch (RodinDBException e) {
			newElements.remove(this);
			throw e;
		}

		if (RodinDBManager.VERBOSE) {
			System.out.println(RodinDBManager.getRodinDBManager().cache
					.toStringFillingRation("-> ")); //$NON-NLS-1$
		}
	}

	/**
	 * Return my underlying resource. Elements that may not have a corresponding
	 * resource must override this method.
	 * 
	 * @see IRodinElement
	 */
	public IResource getCorrespondingResource() {
		return getUnderlyingResource();
	}

	/**
	 * Returns the info for this handle. If this element is not already open, it
	 * and all of its parents are opened. Does not return null.
	 * 
	 * @exception RodinDBException
	 *                if the element is not present or not accessible
	 */
	@Override
	public OpenableElementInfo getElementInfo(IProgressMonitor monitor)
			throws RodinDBException {

		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		OpenableElementInfo info = manager.getInfo(this);
		if (info != null)
			return info;
		return openWhenClosed(createElementInfo(), monitor);
	}

	@Override
	public Openable getOpenable() {
		return this;
	}

	@Override
	public Openable getParent() {
		return (Openable) super.getParent();
	}
	
	/*
	 * @see IParent
	 */
	@Override
	public boolean hasChildren() throws RodinDBException {
		// if I am not open, return true to avoid opening (case of a Rodin
		// project, a compilation unit or a class file).
		// also see https://bugs.eclipse.org/bugs/show_bug.cgi?id=52474
		final RodinDBManager manager = RodinDBManager.getRodinDBManager();
		OpenableElementInfo elementInfo = manager.getInfo(this);
		if (elementInfo != null) {
			return elementInfo.getChildren().length > 0;
		} else {
			return true;
		}
	}

	/**
	 * @see IOpenable
	 */
	public boolean hasUnsavedChanges() {
		if (isReadOnly()) {
			return false;
		}
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		OpenableElementInfo info = manager.getInfo(this);
		return info != null && info.hasUnsavedChanges();
	}

	/**
	 * Subclasses must override as required.
	 * 
	 * @see IOpenable
	 */
	public boolean isConsistent() {
		return true;
	}

	/**
	 * 
	 * @see IOpenable
	 */
	public boolean isOpen() {
		return RodinDBManager.getRodinDBManager().getInfo(this) != null;
	}

	@Override
	public boolean isReadOnly() {
		IResource resource = getCorrespondingResource();
		if (resource == null) {
			return false;
		}
		ResourceAttributes attributes = resource.getResourceAttributes();
		return attributes != null && attributes.isReadOnly();
	}

	/**
	 * Returns true if this represents a source element. Openable source
	 * elements have an associated buffer created when they are opened.
	 */
	protected boolean isSourceElement() {
		return false;
	}

	/**
	 * Returns whether the structure of this element is known. For example, for a
	 * Rodin file that could not be parsed, <code>false</code> is returned.
	 * If the structure of an element is unknown, navigations will return reasonable
	 * defaults. For example, <code>getChildren</code> will return an empty collection.
	 * <p>
	 * Note: This does not imply anything about consistency with the
	 * underlying resource contents.
	 * </p>
	 *
	 * @return <code>true</code> if the structure of this element is known
	 */
	protected boolean isStructureKnown() {
		try {
			return ((OpenableElementInfo) getElementInfo()).isStructureKnown();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @see IOpenable
	 */
	public void makeConsistent(IProgressMonitor monitor)
			throws RodinDBException {
		if (isConsistent())
			return;

		// create a new info and make it the current info
		// (this will remove the info and its children just before storing the
		// new infos)
		openWhenClosed(createElementInfo(), monitor);
	}

	/**
	 * @see IOpenable
	 */
	public void open(IProgressMonitor pm) throws RodinDBException {
		getElementInfo(pm);
	}

	/**
	 * Open the parent element if necessary.
	 */
	protected void openParent(Object childInfo, OpenableMap newElements,
			IProgressMonitor pm) throws RodinDBException {

		if (parent == null)
			return;
		final Openable openableParent = parent.getOpenable();
		if (!openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(),
					newElements, pm);
		}
	}

	/*
	 * Opens an <code>Openable</code> that is known to be closed (no check for
	 * <code>isOpen()</code>). Returns the created element info.
	 */
	protected OpenableElementInfo openWhenClosed(OpenableElementInfo info,
			IProgressMonitor monitor) throws RodinDBException {

		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		boolean hadTemporaryCache = manager.hasTemporaryCache();
		try {
			OpenableMap newElements = manager.getTemporaryCache();
			generateInfos(info, newElements, monitor);
			if (info == null) {
				info = newElements.get(this);
			}
			if (info == null) {
				throw newNotPresentException();
			}
			if (!hadTemporaryCache) {
				manager.putInfos(this, newElements);
			}
		} finally {
			if (!hadTemporaryCache) {
				manager.resetTemporaryCache();
			}
		}
		return info;
	}

	/**
	 * Answers true if the parent exists (null parent is answering true)
	 * 
	 */
	protected boolean parentExists() {

		IRodinElement parentElement = getParent();
		if (parentElement == null)
			return true;
		return parentElement.exists();
	}

	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	protected boolean resourceExists() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null)
			return false;
		return RodinDB.getTarget(workspace.getRoot(), this.getPath()
				.makeRelative(), true) != null;
	}

	public void save(IProgressMonitor progress, boolean force)
			throws RodinDBException {
		if (isReadOnly()) {
			throw new RodinDBException(new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY, this));
		}
	}

	public void save(IProgressMonitor progress, boolean force,
			boolean keepHistory) throws RodinDBException {
		if (isReadOnly()) {
			throw new RodinDBException(new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY, this));
		}
	}

	@Override
	public RodinElementInfo toStringInfo(int tab, StringBuilder buffer) {
		final RodinDBManager manager = RodinDBManager.getRodinDBManager();
		RodinElementInfo info = manager.peekAtInfo(this);
		this.toStringInfo(tab, buffer, info);
		return info;
	}
}
