/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eventb.core.tests.BuilderTest.PLUGIN_ID;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ResourceUtils {

	private static void setContents(IFile file, String contents)
			throws Exception {
		final InputStream input = new ByteArrayInputStream(contents
				.getBytes("utf-8"));
		file.setContents(input, IResource.NONE, null);
	}

	private static IRodinFile createRodinFile(IRodinProject project,
			String fileName) throws RodinDBException {
		IRodinFile file = project.getRodinFile(fileName);
		assert file != null;
		file.create(true, null);
		return file;
	}

	private static void initFile(IRodinFile rodinFile, String contents)
			throws Exception {
		final IFile resource = rodinFile.getResource();
		setContents(resource, contents);
	}

	public static IContextRoot createContext(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String contextName = EventBPlugin.getContextFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, contextName);
		initFile(rFile, contents);
		return (IContextRoot) rFile.getRoot();
	}

	public static IMachineRoot createMachine(IRodinProject project,
			String bareName, String contents) throws Exception {
		final String machineName = EventBPlugin.getMachineFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, machineName);
		initFile(rFile, contents);
		return (IMachineRoot) rFile.getRoot();
	}
	
	public static IPRRoot createPRFile(IRodinProject project, String bareName, String contents) throws Exception {
		final String prFileName = EventBPlugin.getPRFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, prFileName);
		initFile(rFile, contents);
		return (IPRRoot) rFile.getRoot();

	}
	
	public static IPSRoot createPSFile(IRodinProject project, String bareName, String contents) throws Exception {
		final String psFileName = EventBPlugin.getPSFileName(bareName);
		final IRodinFile rFile = createRodinFile(project, psFileName);
		initFile(rFile, contents);
		return (IPSRoot) rFile.getRoot();

	}
	
	public static final String MCH_BARE_NAME = "machine";
	public static final List<IDeclaration> EMPTY_DECL = Collections.emptyList();
	public static final String INTERNAL_ELEMENT1 = "internal_element1";
	public static final String INTERNAL_ELEMENT2 = "internal_element2";
	public static final String CTX_BARE_NAME = "context";

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
		final URL entry = getProjectsURL();
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory() && project.getName().equals(srcName))
				importFiles(dest, project, true);
		}
	}

	
	private static URL getProjectsURL() {
		return Platform.getBundle(PLUGIN_ID).getEntry("projects");
	}

	private static void importFiles(IProject project, File root, boolean isRoot)
			throws IOException, CoreException {
		for (final File file : root.listFiles()) {
			final String filename = file.getName();
			if (file.isFile()) {
				final InputStream is = new FileInputStream(file);
				final String name = (isRoot) ? filename : root.getName() + "/"
						+ filename;
				final IFile target = project.getFile(name);
				target.create(is, false, null);
			} else if (file.isDirectory() && !filename.equals(".svn")) {
				final IFolder folder = project.getFolder(filename);
				folder.create(true, false, null);
				importFiles(project, file, false);
			}
		}
	}

}
