/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaProjectElementInfo.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.basis.RodinElement;

/** 
 * Info for IRodinProject.
 */
/* package */ class RodinProjectElementInfo extends OpenableElementInfo {

	/**
	 * An array with all the non-java resources contained by this project
	 */
	private IResource[] nonRodinResources;
	
	/**
	 * Create and initialize a new instance of the receiver
	 */
	public RodinProjectElementInfo() {
		this.nonRodinResources = null;
	}
	
	/**
	 * Compute the non-Rodin resources contained in this Rodin project.
	 */
	private IResource[] computeNonRodinResources(RodinProject project) {

		IResource[] resources = NO_NON_RODIN_RESOURCES;
		int resourcesCounter = 0;
		try {
			IResource[] members = project.getProject().members();
			resources = new IResource[members.length];
			for (IResource res : members) {
				switch (res.getType()) {
				case IResource.FILE:
					ElementTypeManager mgr = ElementTypeManager.getInstance();
					if (mgr.getFileAssociation((IFile) res) == null) {
						// Not a Rodin file
						resources[resourcesCounter++] = res;
					}
					break;
				case IResource.FOLDER:
					resources[resourcesCounter++] = res;
					break;
				}
			}
			if (resources.length != resourcesCounter) {
				System.arraycopy(resources, 0,
						(resources = new IResource[resourcesCounter]), 0,
						resourcesCounter);
			}
		} catch (CoreException e) {
			resources = NO_NON_RODIN_RESOURCES;
			resourcesCounter = 0;
		}
		return resources;
	}
	
	/**
	 * Compute the Rodin resources contained in this Rodin project.
	 */
	public void computeChildren(RodinProject project) {

		RodinElement[] newChildren = RodinElement.NO_ELEMENTS;
		int resourcesCounter = 0;
		try {
			IResource[] members = project.getProject().members();
			newChildren = new RodinElement[members.length];
			for (IResource res : members) {
				switch (res.getType()) {
				case IResource.FILE:
					final IRodinFile rf = project.getRodinFile(res.getName());
					if (rf != null) {
						newChildren[resourcesCounter++] = (RodinElement) rf;
					}
					break;
				}
			}
			if (newChildren.length != resourcesCounter) {
				System.arraycopy(newChildren, 0,
						(newChildren = new RodinElement[resourcesCounter]), 0,
						resourcesCounter);
			}
		} catch (CoreException e) {
			newChildren = RodinElement.NO_ELEMENTS;
		}
		setChildren(newChildren);
	}
	
	/**
	 * Returns an array of non-Rodin resources contained in the receiver.
	 */
	IResource[] getNonRodinResources(RodinProject project) {
		if (this.nonRodinResources == null) {
			this.nonRodinResources = computeNonRodinResources(project);
		}
		return this.nonRodinResources;
	}

	/**
	 * Set the nonRodinResources field to resources value
	 */
	void setNonRodinResources(IResource[] resources) {
		this.nonRodinResources = resources;
	}
	
}
