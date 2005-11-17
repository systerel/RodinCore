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
package org.rodinp.core.basis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.OpenableElementInfo;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.RodinDBStatus;
import org.rodinp.internal.core.RodinElementInfo;

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
			Map<IRodinElement, RodinElementInfo> newElements,
			IResource underlyingResource) throws RodinDBException;

	/*
	 * Returns whether this element can be removed from the Rodin database cache
	 * to make space.
	 */
	public boolean canBeRemovedFromCache() {
		return !hasUnsavedChanges();
	}

	/**
	 * This element is being closed. Do any necessary cleanup.
	 */
	public void closing(RodinElementInfo info) {
		// TODO what cleanup to do?
	}

	/*
	 * Returns a new element info for this element.
	 */
	@Override
	protected abstract RodinElementInfo createElementInfo();

	/**
	 * @see IRodinElement
	 */
	@Override
	public boolean exists() {
		RodinDBManager manager = RodinDBManager.getRodinDBManager();
		if (manager.getInfo(this) != null)
			return true;
		if (!parentExists())
			return false;
		return super.exists();
	}

	@Override
	protected void generateInfos(RodinElementInfo info, 
			HashMap<IRodinElement, RodinElementInfo> newElements,
			IProgressMonitor monitor) throws RodinDBException {

		if (RodinDBManager.VERBOSE) {
			String element = getElementType();
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
			OpenableElementInfo openableElementInfo = (OpenableElementInfo) info;
			boolean isStructureKnown = buildStructure(openableElementInfo,
					monitor, newElements, getResource());
			openableElementInfo.setIsStructureKnown(isStructureKnown);
		} catch (RodinDBException e) {
			newElements.remove(this);
			throw e;
		}

		// remove out of sync buffer for this element
		RodinDBManager.getRodinDBManager().getElementsOutOfSynch().remove(this);

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
	public IResource getCorrespondingResource() throws RodinDBException {
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
	public RodinElementInfo getElementInfo(IProgressMonitor monitor)
			throws RodinDBException {

		RodinElementInfo info = null;
		try {
			info = super.getElementInfo(monitor);
		} catch (RodinDBException e) {
			// taken care below
		}
		if (info != null)
			return info;
		return openWhenClosed(createElementInfo(), monitor);
	}

	/*
	 * @see IRodinElement
	 */
	@Override
	public Openable getOpenable() {
		return this;
	}

	/**
	 * @see IRodinElement
	 */
	public IResource getUnderlyingResource() throws RodinDBException {
		IResource parentResource = this.parent.getUnderlyingResource();
		if (parentResource == null) {
			return null;
		}
		int type = parentResource.getType();
		if (type == IResource.FOLDER || type == IResource.PROJECT) {
			IContainer folder = (IContainer) parentResource;
			IResource resource = folder.findMember(getElementName());
			if (resource == null) {
				throw newNotPresentException();
			} else {
				return resource;
			}
		} else {
			return parentResource;
		}
	}

	/**
	 * @see IOpenable
	 */
	public boolean hasUnsavedChanges() {

		if (isReadOnly() || !isOpen()) {
			return false;
		}
		// TODO define how we record that an openable has changed
		// For projects and folders, should go down recursively on children
		// looking for a file element with unsaved changes ?
		
		return false;
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
	protected void openParent(Object childInfo, 
			HashMap<IRodinElement, RodinElementInfo> newElements,
			IProgressMonitor pm) throws RodinDBException {

		Openable openableParent = getOpenableParent();
		if (openableParent != null && !openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(),
					newElements, pm);
		}
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

	/**
	 * @see IOpenable
	 */
	public void save(IProgressMonitor progress, boolean force)
			throws RodinDBException {
		if (isReadOnly()) {
			throw new RodinDBException(new RodinDBStatus(
					IRodinDBStatusConstants.READ_ONLY, this));
		}
	}

}
