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
package fr.systerel.editor.tests.commandTests;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Utility methods to set up and perform tests.
 */
public class TestUtils {

	private static final String PLUGIN_ID = "fr.systerel.editor.tests";

	private static final IWorkspaceRoot WS_ROOT = getWorkspace().getRoot();

	private static final String TEST_FOLDER_NAME = "test-files";

	private static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

	public static void addRodinNature(String projectName) throws CoreException {
		final IProject project = WS_ROOT.getProject(projectName);
		final IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(description, null);
	}

	/*
	 * Create simple project.
	 */
	public static IProject createProject(final String projectName)
			throws CoreException {
		final IProject project = WS_ROOT.getProject(projectName);
		final IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(null);
				project.open(null);
			}
		};
		getWorkspace().run(create, null);
		return project;
	}

	public static IRodinProject createRodinProject(final String projectName)
			throws CoreException {
		final IWorkspaceRunnable create = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// create project
				createProject(projectName);
				// set Rodin nature
				addRodinNature(projectName);
			}
		};
		getWorkspace().run(create, null);
		return RodinCore.getRodinDB().getRodinProject(projectName);
	}

	public static void copyTestFileInProject(String fileName, IContainer destination)
			throws Exception {
		final String entryPathStr = getFilePathString(fileName);
		final URL fileURL = BUNDLE.getEntry(entryPathStr.toString());
		final File file = new File(FileLocator.resolve(fileURL).toURI());
		final IFile newFile = destination.getFile(new Path(file.getName()));
		final FileInputStream source = new FileInputStream(file);
		newFile.create(source, true, null);
	}

	public static String getFilePathString(final String fileName) {
		return TEST_FOLDER_NAME + "/" + fileName;
	}

}