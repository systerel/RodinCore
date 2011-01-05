/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Nicolas Beauger
 *
 */
public class RodinDBUtils {

	private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

	public static IWorkspaceRoot getWorkspaceRoot() {
		return RodinDBUtils.WORKSPACE.getRoot();
	}

	public static IWorkspace getWorkspace() {
		return WORKSPACE;
	}

	public static IProject getProject(String project) {
		return getWorkspaceRoot().getProject(project);
	}

	/**
	 * Returns the Rodin Project with the given name in this test
	 * suite's database. This is a convenience method.
	 */
	public static IRodinProject getRodinProject(String name) {
		IProject project = getProject(name);
		return RodinCore.valueOf(project);
	}

	public static IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = getWorkspaceRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}

	public static void deleteAllProjects() throws RodinDBException, CoreException {
		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp: rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}
	}
	
}
