/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Tests for the adapter factories provided by the Rodin Database.
 * 
 * @author Laurent Voisin
 */
public class AdapterFactoryTests extends AbstractRodinDBTests {

	public AdapterFactoryTests(String name) {
		super(name);
	}

	private static void assertAdapterPositive(Object adaptable,
			Class adapterType, Object expected) {

		IAdapterManager adapterManager = Platform.getAdapterManager();
		Object actual = adapterManager.getAdapter(adaptable, adapterType);
		assertTrue("Wrong type for adapter", adapterType.isInstance(actual));
		assertEquals("Wrong adapter", expected, actual);
	}
	
	private static void assertAdapterNegative(Object adaptable,
			Class adapterType) {

		IAdapterManager adapterManager = Platform.getAdapterManager();
		Object actual = adapterManager.getAdapter(adaptable, adapterType);
		assertNull("Adaptatation should have failed", actual);
	}
	
	/**
	 * Checks all possible adaptations from Eclipse resources to Rodin elements. 
	 */
	public void testResourceAdapterFactory() throws Exception {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		
		IWorkspaceRoot root = ws.getRoot();
		IRodinDB rodinDB = RodinCore.create(root);
		assertAdapterPositive(root, IRodinElement.class, rodinDB);
		
		IProject project = root.getProject("P");
		IRodinProject rodinProject = rodinDB.getRodinProject("P");
		assertAdapterPositive(project, IRodinElement.class, rodinProject);
		
		IFolder folder = project.getFolder("foo");
		assertAdapterNegative(folder, IRodinElement.class);
		
		IFile file = project.getFile("x.test");
		IRodinFile rodinFile = rodinProject.getRodinFile("x.test");
		assertAdapterPositive(file, IRodinElement.class, rodinFile);
		
		// Not a valid Rodin file element name 
		file = project.getFile("foo");
		assertAdapterNegative(file, IRodinElement.class);
		
		// Not a valid Rodin file element (lays inside a folder) 
		file = folder.getFile("x.test");
		assertAdapterNegative(file, IRodinElement.class);
	}
	
	/**
	 * Checks all possible adaptations from Rodin elements to Eclipse resources. 
	 */
	public void testRodinElementAdapterFactory() throws Exception {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		
		IRodinDB rodinDB = getRodinDB();
		IWorkspaceRoot root = ws.getRoot();
		assertAdapterPositive(rodinDB, IResource.class, root);
		
		IRodinProject rodinProject = getRodinProject("P");
		IProject project = rodinProject.getProject();
		assertAdapterPositive(rodinProject, IResource.class, project);
		
		IRodinFile rodinFile = getRodinFile(rodinProject, "x.test");
		IFile file = (IFile) rodinFile.getResource();
		assertAdapterPositive(rodinFile, IResource.class, file);
		
		// Internal elements do not have a corresponding resource 
		IInternalElement foo = getNamedElement(rodinFile, "foo");
		assertAdapterNegative(foo, IResource.class);
		
		IInternalElement bar = getNamedElement(foo, "bar");
		assertAdapterNegative(bar, IResource.class);
	}
	
	/**
	 * Checks adaptation from Rodin projects to Eclipse projects. 
	 */
	public void testRodinProjectAdapterFactory() throws Exception {
		IRodinProject rodinProject = getRodinProject("P");
		IProject project = rodinProject.getProject();
		assertAdapterPositive(rodinProject, IProject.class, project);
	}
	
}
