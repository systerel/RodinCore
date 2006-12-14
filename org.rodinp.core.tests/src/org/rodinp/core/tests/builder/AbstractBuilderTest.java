/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.ModifyingResourceTests;
import org.rodinp.core.tests.util.Util;

public abstract class AbstractBuilderTest extends ModifyingResourceTests {
	
	public static final String PLUGIN_ID = "org.rodinp.core.tests";
	
	public AbstractBuilderTest(String name) {
		super(name);
	}
	
	protected void runBuilder(IRodinProject project, String expectedTrace) throws CoreException {
		project.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		if (expectedTrace != null)
			assertStringEquals("Unexpected tool trace", expectedTrace, ToolTrace.getTrace());
	}
	
	protected void runBuilderClean(IRodinProject project) throws CoreException {
		project.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
	}
	
	@SuppressWarnings("deprecation")
	private String expandFile(IRodinFile file) throws RodinDBException {
		StringBuilder builder = new StringBuilder(file.getElementName());
		IRodinElement[] children = file.getChildren();
		for (IRodinElement element : children) {
			IInternalElement child = (IInternalElement) element;
			if (child.getElementType() == IDependency.ELEMENT_TYPE) {
				builder.append("\n  dep: ");
				builder.append(child.getElementName());
			} else {
				builder.append("\n  data: ");
				builder.append(child.getContents());
			}
		}
		return builder.toString();
	}
	
	private void assertStringEquals(String message, String expected, String actual) {
		if (!expected.equals(actual)){
			System.out.println(Util.displayString(actual, 4));
		}
		assertEquals(message, expected, actual);
	}
	
	protected void assertContents(String message,  String expected, IRodinFile file) throws CoreException {
		assertStringEquals(message, expected, expandFile(file));
	}
	
	int index = 0;
	
	@SuppressWarnings("deprecation")
	protected IData createData(IRodinFile parent, String contents) throws RodinDBException {
		IData data = (IData) parent.createInternalElement(
				IData.ELEMENT_TYPE,
				"foo" + index++,
				null,
				null);
		data.setContents(contents);
		return data;
	}

	protected IDependency createDependency(IRodinFile parent, String target) throws RodinDBException {
		IDependency dep = (IDependency) parent.getInternalElement(
				IDependency.ELEMENT_TYPE, target);
		dep.create(null, null);
		return dep;
	}
	
	protected IReference createReference(IRodinFile parent, String target) throws RodinDBException {
		IReference ref = (IReference) parent.getInternalElement(
				IReference.ELEMENT_TYPE, target);
		ref.create(null, null);
		return ref;
	}
	
	public static String getComponentName(String fileName) {
		final int length = fileName.length() - 4;
		assert 0 < length;
		return fileName.substring(0, length);
	}

	protected IFile getFile(String path) {
		return getWorkspaceRoot().getFile(new Path(path));
	}

	@Override
	protected IRodinFile createRodinFile(String path) throws CoreException {
		IFile file = getFile(path);
		IRodinFile rodinFile = RodinCore.valueOf(file);
		rodinFile.create(true, null);
		return rodinFile;
	}
	
	protected void importProject(String projectName) throws Exception {
		Bundle plugin = Platform.getBundle(PLUGIN_ID);
		URL projectsURL = plugin.getEntry("projects");
		projectsURL = FileLocator.toFileURL(projectsURL);
		File projectsDir = new File(projectsURL.toURI());
		for (File project: projectsDir.listFiles()) {
			if (project.isDirectory()) 
				importProject(project);
		}
	}
	
	private void importProject(File projectDir) throws Exception {
		final String projectName = projectDir.getName();
		IProject project = getWorkspaceRoot().getProject(projectName);
		IProjectDescription desc = getWorkspace().newProjectDescription(projectName); 
		desc.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.create(desc, null);
		project.open(null);
		IProjectNature nature = project.getNature(RodinCore.NATURE_ID);
		nature.configure();
		for (File file: projectDir.listFiles()) {
			if (file.isFile()) {
				InputStream is = new FileInputStream(file);
				IFile target = project.getFile(file.getName());
				target.create(is, false, null);
			}
		}
	}

}
