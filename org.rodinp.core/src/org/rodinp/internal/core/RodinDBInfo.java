/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.JavaModelInfo
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Implementation of IRodinDB. A Rodin database is specific to a workspace.
 * 
 * @see org.rodinp.core.IRodinDB
 */
public class RodinDBInfo extends OpenableElementInfo {

	/**
	 * An array with all the non-Rodin projects contained by this database
	 */
	IResource[] nonRodinResources;

	/**
	 * Compute the non-Rodin projects contained in this Rodin database.
	 */
	private IResource[] computeNonRodinResources() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		int length = projects.length;
		IResource[] resources = null;
		int index = 0;
		for (IProject project: projects) {
			if (!RodinProject.hasRodinNature(project)) {
				if (resources == null) {
					resources = new IResource[length];
				}
				resources[index++] = project;
			}
		}
		if (index == 0)
			return NO_NON_RODIN_RESOURCES;
		if (index < length) {
			System.arraycopy(resources, 0, resources = new IResource[index], 0,
					index);
		}
		return resources;
	}

	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	IResource[] getNonRodinResources() {

		if (this.nonRodinResources == null) {
			this.nonRodinResources = computeNonRodinResources();
		}
		return this.nonRodinResources;
	}
}
