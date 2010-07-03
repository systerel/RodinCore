/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.improved.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class ProjectImporter {

	private static final String PLUGIN_ID = "org.eventb.core.seqprover.improved.tests";

	/**
	 * Imports files of a template project into an already existing project.
	 * 
	 * @param dest
	 *            destination project. Must already exist and be configured
	 * @param srcName
	 *            name of the source project which lies in the
	 *            <code>projects</code> folder of this plug-in
	 * @throws Exception
	 *             in case of error
	 */
	public static void importProjectFiles(IProject dest, String srcName)
			throws Exception {
		final URL entry = Platform.getBundle(PLUGIN_ID).getEntry("projects");
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory() && project.getName().equals(srcName))
				importFiles(dest, project, true);
		}
	}

	private static void importFiles(IProject project, File root, boolean isRoot)
			throws Exception {
		for (final File file : root.listFiles()) {
			if (file.isFile()) {
				final InputStream is = new FileInputStream(file);
				final String name = (isRoot) ? file.getName() : root.getName()
						+ "/" + file.getName();
				final IFile target = project.getFile(name);
				target.create(is, false, null);
			} else if (file.isDirectory() && !file.getName().equals(".svn")) {
				final IFolder folder = project.getFolder(file.getName());
				folder.create(true, false, null);
				importFiles(project, file, false);
			}
		}
	}

}
