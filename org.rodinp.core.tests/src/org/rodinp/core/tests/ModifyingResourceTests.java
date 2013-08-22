/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.ModifyingResourceTests
 *     Systerel - moved attribute type declarations to super
 *     Systerel - separation of file and root element
 *     Systerel - added creation of new internal element child
 *     Systerel - imported sub directory
 *******************************************************************************/
package org.rodinp.core.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.util.Util;

/*
 * Tests that modify resources in the workspace.
 */
public abstract class ModifyingResourceTests extends AbstractRodinDBTests {
	
	static final String emptyContents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<org.rodinp.core.tests.test/>\n";
	static byte[] emptyBytes = emptyContents.getBytes();

	public ModifyingResourceTests(String name) {
		super(name);
	}
	
	protected static void assertElementDescendants(String message,  String expected, IRodinElement element) throws CoreException {
		String actual = expandAll(element);
		if (!expected.equals(actual)){
			System.out.println(Util.displayString(actual, 4));
		}
		assertEquals(
				message,
				expected,
				actual);
	}
	
	protected static IRodinFile createRodinFile(String path) throws CoreException {
		IFile file = getFile(path);
		IRodinFile rodinFile = RodinCore.valueOf(file);
		rodinFile.create(true, null);
		return rodinFile;
	}

	protected static IFile createFile(String path, InputStream content) throws CoreException {
		IFile file = getFile(path);
		file.create(content, true, null);
		return file;
	}
	
	protected static IFile createFile(String path, byte[] content) throws CoreException {
		return createFile(path, new ByteArrayInputStream(content));
	}
	
	protected static IFile createFile(String path, String content) throws CoreException {
		return createFile(path, content.getBytes());
	}

	protected static IFile createFile(String path, String content, String charsetName) throws CoreException, UnsupportedEncodingException {
		return createFile(path, content.getBytes(charsetName));
	}
	
	protected static IFolder createFolder(String path) throws CoreException {
		return createFolder(new Path(path));
	}
	
	protected static NamedElement createNEPositive(IInternalElement parent, String name,
			IInternalElement nextSibling) throws RodinDBException {
		
		NamedElement element = getNamedElement(parent, name);
		assertExists("Parent should exist", parent);
		assertNotExists("Element to create should not exist", element);
		element.create(nextSibling, null);
		assertExists("Created element should exist", element);
		return element;
	}

	protected static NamedElement2 createNE2Positive(IInternalElement parent, String name,
			IInternalElement nextSibling) throws RodinDBException {

		NamedElement2 element = getNamedElement2(parent, name);
		assertExists("Parent should exist", parent);
		assertNotExists("Element to create should not exist", element);
		element.create(nextSibling, null);
		assertExists("Created element should exist", element);
		return element;
	}

	protected static NamedElement createNewNEPositive(IInternalElement parent,
			IInternalElement nextSibling) throws RodinDBException {
		assertExists("Parent should exist", parent);
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;
		final NamedElement element = parent
				.createChild(type, nextSibling, null);
		assertExists("Created element should exist", element);
		return element;
	}

	protected static void createNENegative(IInternalElement parent, String name,
			IInternalElement nextSibling, int failureCode) throws RodinDBException {
		
		NamedElement element = getNamedElement(parent, name);
		if (parent.isReadOnly()) {
			assertEquals("Wrong failure code", 
					IRodinDBStatusConstants.READ_ONLY, failureCode);
			try {
				element.create(nextSibling, null);
				fail("Should have raised an exception");
			} catch (RodinDBException e) {
				assertReadOnlyErrorFor(e, parent);
			}
			return;
		}
		if (element.exists()) {
			assertEquals("Wrong failure code", 
					IRodinDBStatusConstants.NAME_COLLISION, failureCode);
			try {
				element.create(nextSibling, null);
				fail("Should have raised an exception");
			} catch (RodinDBException e) {
				assertNameCollisionErrorFor(e, element);
			}
			assertExists("Conflicting element should still exist", element);
			return;
		}
		fail("Unexpected failure of createNENegative");
	}

	protected static void deleteFile(String filePath) throws CoreException {
		deleteResource(getFile(filePath));
	}
	
	protected static void deleteFolder(String folderPath) throws CoreException {
		deleteFolder(new Path(folderPath));
	}
	
	protected static IFile editFile(String path, String content) throws CoreException {
		IFile file = getFile(path);
		InputStream input = new ByteArrayInputStream(content.getBytes());
		file.setContents(input, IResource.FORCE, null);
		return file;
	}
	
	/* 
	 * Expands (i.e. open) the given element and returns a toString() representation
	 * of the tree.
	 */
	protected static String expandAll(IRodinElement element) throws CoreException {
		StringBuilder buffer = new StringBuilder();
		expandAll(element, 0, buffer);
		return buffer.toString();
	}
	
	private static void expandAll(IRodinElement element, int tab, StringBuilder buffer) throws CoreException {
		IRodinElement[] children = null;
		// force opening of element by getting its children
		if (element instanceof IParent) {
			IParent parent = (IParent)element;
			children = parent.getChildren();
		}
		((RodinElement)element).toStringInfo(tab, buffer);
		if (children != null) {
			for (int i = 0, length = children.length; i < length; i++) {
				buffer.append("\n");
				expandAll(children[i], tab+1, buffer);
			}
		}
	}
	
	protected static void renameProject(String project, String newName) throws CoreException {
		getProject(project).move(new Path(newName), true, null);
	}

	protected static IFolder getFolder(String path) {
		return getFolder(new Path(path));
	}
	
	protected String getSortedByProjectDeltas() {
		StringBuffer buffer = new StringBuffer();
		for (int i=0, length = this.deltaListener.deltas.length; i<length; i++) {
			IRodinElementDelta[] projects = this.deltaListener.deltas[i].getAffectedChildren();
			int projectsLength = projects.length;
			
			// sort by project
			IRodinElementDelta[] sorted = new IRodinElementDelta[projectsLength];
			System.arraycopy(projects, 0, sorted, 0, projectsLength);
			Arrays.sort(
					sorted, 
					new  Comparator<IRodinElementDelta>() {
						public int compare(IRodinElementDelta a, IRodinElementDelta b) {
							return a.toString().compareTo(b.toString());
						}
					});
			
			for (int j=0; j<projectsLength; j++) {
				buffer.append(sorted[j]);
				if (j != projectsLength-1) {
					buffer.append("\n");
				}
			}
			if (i != length-1) {
				buffer.append("\n\n");
			}
		}
		return buffer.toString();
	}
	
	protected static void moveFile(String sourcePath, String destPath) throws CoreException {
		getFile(sourcePath).move(getFile(destPath).getFullPath(), false, null);
	}
	
	protected static void moveFolder(String sourcePath, String destPath) throws CoreException {
		getFolder(sourcePath).move(getFolder(destPath).getFullPath(), false, null);
	}
	
	protected static void swapFiles(String firstPath, String secondPath) throws CoreException {
		final IFile first = getFile(firstPath);
		final IFile second = getFile(secondPath);
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(	) {
			public void run(IProgressMonitor monitor) throws CoreException {
				IPath tempPath = first.getParent().getFullPath().append("swappingFile.temp");
				first.move(tempPath, false, monitor);
				second.move(first.getFullPath(), false, monitor);
				getWorkspaceRoot().getFile(tempPath).move(second.getFullPath(), false, monitor);
			}
		};
		getWorkspace().run(runnable, null);
	}

	public static void setReadOnly(IResource resource, boolean readOnly) throws CoreException {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes != null) {
			resourceAttributes.setReadOnly(readOnly);
			resource.setResourceAttributes(resourceAttributes);
		}		
	}
	
	public static boolean isReadOnly(IResource resource) throws CoreException {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes != null) {
			return resourceAttributes.isReadOnly();
		}
		return false;
	}

	protected static void importProject(String projectName) throws Exception {
		Bundle plugin = Platform.getBundle(PLUGIN_ID);
		URL projectsURL = plugin.getEntry("projects");
		projectsURL = FileLocator.toFileURL(projectsURL);
		File projectsDir = new File(projectsURL.toURI());
		for (File project: projectsDir.listFiles()) {
			if (project.isDirectory() && project.getName().equals(projectName)) 
				importProject(project);
		}
	}

	private static void importProject(File projectDir) throws Exception {
		final String projectName = projectDir.getName();
		IProject project = getWorkspaceRoot().getProject(projectName);
		IProjectDescription desc = getWorkspace().newProjectDescription(projectName); 
		desc.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.create(desc, null);
		project.open(null);
		IProjectNature nature = project.getNature(RodinCore.NATURE_ID);
		nature.configure();
		importFiles(project, projectDir, true);
	}
	
	private static void importFiles(IProject project, File root, boolean isRoot)
			throws Exception {
		for (File file : root.listFiles()) {
			if (file.isFile()) {
				InputStream is = new FileInputStream(file);
				final String name = (isRoot) ? file.getName() : root.getName()
						+ "/" + file.getName();
				IFile target = project.getFile(name);
				target.create(is, false, null);
			} else if (file.isDirectory() && !file.getName().equals(".svn")) {
				IFolder folder = project.getFolder(file.getName());
				folder.create(true, false, null);
				importFiles(project, file, false);
			}
		}
	}
}
