/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added tests for asRodinElement()
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
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

	private static void assertAdapterPositive(Object adaptable,
			Class<?> adapterType, Object expected) {

		final IAdapterManager adapterManager = Platform.getAdapterManager();
		final Object actual = adapterManager.getAdapter(adaptable, adapterType);
		assertTrue("Wrong type for adapter", adapterType.isInstance(actual));
		assertEquals("Wrong adapter", expected, actual);

		// In addition check the asRodinElement() method
		final IRodinElement elem = RodinCore.asRodinElement(adaptable);
		if (expected instanceof IRodinElement) {
			assertEquals("Wrong Rodin element", expected, elem);
		} else if (adaptable instanceof IRodinElement){
			assertSame("Wrong Rodin element", adaptable, elem);
		} else {
			assertNull("Wrong Rodin element", elem);
		}
	}
	
	private static void assertAdapterNegative(Object adaptable,
			Class<?> adapterType) {

		IAdapterManager adapterManager = Platform.getAdapterManager();
		Object actual = adapterManager.getAdapter(adaptable, adapterType);
		assertNull("Adaptation should have failed", actual);

		// In addition check the asRodinElement() method
		final IRodinElement elem = RodinCore.asRodinElement(adaptable);
		if (adaptable instanceof IRodinElement){
			assertSame("Wrong Rodin element", adaptable, elem);
		} else {
			assertNull("Wrong Rodin element", elem);
		}
	}
	
	/**
	 * Checks all possible adaptations from Eclipse resources to Rodin elements. 
	 */
	@Test
	public void testResourceAdapterFactory() throws Exception {
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		
		IWorkspaceRoot root = ws.getRoot();
		IRodinDB rodinDB = getRodinDB();
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
		
		// Not a valid Rodin file element (lies inside a folder) 
		file = folder.getFile("x.test");
		assertAdapterNegative(file, IRodinElement.class);
	}
	
	/**
	 * Checks all possible adaptations from Rodin elements to Eclipse resources. 
	 */
	@Test
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
		IInternalElement foo = getNamedElement(rodinFile.getRoot(), "foo");
		assertAdapterNegative(foo, IResource.class);
		
		IInternalElement bar = getNamedElement(foo, "bar");
		assertAdapterNegative(bar, IResource.class);
	}
	
	/**
	 * Checks adaptation from Rodin projects to Eclipse projects. 
	 */
	@Test
	public void testRodinProjectAdapterFactory() throws Exception {
		IRodinProject rodinProject = getRodinProject("P");
		IProject project = rodinProject.getProject();
		assertAdapterPositive(rodinProject, IProject.class, project);
	}

	/**
	 * Checks cases where no adaptation is possible. 
	 */
	@Test
	public void testNoAdaptation() throws Exception {
		final Integer i = 0;
		assertAdapterNegative(i, IRodinFile.class);
		
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final IResource foo = ws.getRoot().getProject("P").getFolder("foo");
		assertAdapterPositive(foo, IResource.class, foo);
	}

}
