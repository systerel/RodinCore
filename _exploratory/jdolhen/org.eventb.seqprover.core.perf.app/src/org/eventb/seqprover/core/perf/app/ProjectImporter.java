/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import static org.eclipse.core.resources.IResource.DEPTH_ONE;
import static org.rodinp.core.RodinCore.getRodinDB;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Imports projects into workspace. Only proof obligation files are imported, as
 * we are only interested in proofs. Moreover, this allows to work with any
 * project, whatever the configuration of the static checker and proof
 * obligation generator.
 * 
 * @author Laurent Voisin
 */
public class ProjectImporter {

	/**
	 * Extension of proof obligation files.
	 */
	private static final String PO_FILE_EXT = ".bpo";

	/**
	 * Natures to set on a Rodin project.
	 */
	private static final String[] RODIN_NATURE_IDS = { RodinCore.NATURE_ID };

	/**
	 * Store for the folder that contains the projects to import.
	 */
	private final IFileStore sourceStore;

	public ProjectImporter(IFileStore sourceStore) {
		this.sourceStore = sourceStore;
	}

	/**
	 * Imports all projects to the workspace
	 */
	public void importProjects() throws Exception {
		final String[] projectNames = listProjects();
		for (final String projectName : projectNames) {
			importProject(projectName);
		}
	}

	// Returns the list of available project names
	private String[] listProjects() throws CoreException {
		final List<String> projectNames = new ArrayList<String>();
		final IFileInfo[] childInfos = sourceStore.childInfos(EFS.NONE, null);
		for (final IFileInfo childInfo : childInfos) {
			if (childInfo.isDirectory()) {
				projectNames.add(childInfo.getName());
			}
		}
		return projectNames.toArray(new String[projectNames.size()]);
	}

	private void importProject(String projectName) throws CoreException {
		final IFileStore srcPrj = sourceStore.getChild(projectName);
		final IRodinProject rodinPrj = createRodinProject(projectName);
		final IFileStore dstPrj = getRodinProjectStore(rodinPrj);
		importPOFiles(srcPrj, dstPrj);
		rodinPrj.getProject().refreshLocal(DEPTH_ONE, null);
	}

	private IRodinProject createRodinProject(String projectName)
			throws CoreException {
		final IRodinProject result = getRodinDB().getRodinProject(projectName);
		final IProject project = result.getProject();
		project.create(null);
		project.open(null);
		setRodinNature(project);
		return result;
	}

	private void setRodinNature(IProject project) throws CoreException {
		final IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(RODIN_NATURE_IDS);
		project.setDescription(pDescription, null);
	}

	private IFileStore getRodinProjectStore(IRodinProject rodinPrj)
			throws CoreException {
		final URI uri = rodinPrj.getProject().getLocationURI();
		return EFS.getStore(uri);
	}

	private void importPOFiles(IFileStore srcPrj, IFileStore dstPrj)
			throws CoreException {
		for (final IFileInfo fileInfo : srcPrj.childInfos(EFS.NONE, null)) {
			final String filename = fileInfo.getName();
			if (filename.endsWith(PO_FILE_EXT)) {
				final IFileStore srcFile = srcPrj.getChild(filename);
				final IFileStore dstFile = dstPrj.getChild(filename);
				srcFile.copy(dstFile, EFS.NONE, null);
			}
		}
	}

}
