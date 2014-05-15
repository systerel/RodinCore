/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestRodinDB extends AbstractRodinDBTests {

	/*
	 * Test an empty database.
	 */
	@Test
	public final void testRodinDBEmpty() throws CoreException, RodinDBException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IRodinDB db = getRodinDB();

		// Test handle-only methods
		assertNotNull(db);
		assertNull(db.getParent());
		assertEquals("", db.getElementName());
		assertEquals(IRodinDB.ELEMENT_TYPE, db.getElementType());
		assertEquals(workspace, db.getWorkspace());
		assertEquals(workspaceRoot, db.getWorkspaceRoot());
		assertEquals(workspaceRoot, db.getCorrespondingResource());
		assertTrue(db.exists());
		assertFalse(db.isReadOnly());
		// last handle-only method
		
		// Opening methods.
		assertExists("Database should exist", db);
		assertTrue(db.contains(workspaceRoot));
		assertEquals("No project initially", 0, db.getNonRodinResources().length);
		assertEquals("No project initially", 0, db.getRodinProjects().length);
		assertFalse("No project initially", db.hasChildren());
		assertEquals("No project initially", 0, db.getChildren().length);
	}

	/*
	 * Test project creation and deletion.
	 */
	@Test
	public final void testRodinProject() throws CoreException, RodinDBException {
		IRodinDB db = getRodinDB();

		// Creating a project handle
		IRodinProject rodinProject = db.getRodinProject("foo"); 
		assertNotNull(rodinProject);
		assertEquals("foo", rodinProject.getElementName());
		assertEquals(db, rodinProject.getParent());
		assertNotExists("Project should not exist", rodinProject);

		// Actually create the project
		IProject project = rodinProject.getProject();
		project.create(null);
		assertEquals("One non Rodin project", 1, db.getNonRodinResources().length);
		assertEquals("One non Rodin project", project, db.getNonRodinResources()[0]);
		assertEquals("No children", 0, db.getChildren().length);
		assertNotExists("Project should not exist", rodinProject);
		
		// Set the Rodin nature
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(description, null);
		assertEquals("No non Rodin project", 0, db.getNonRodinResources().length);
		IRodinElement[] childrens = db.getChildren();
		assertEquals(1, childrens.length);
		assertEquals(rodinProject, childrens[0]);
		assertExists("Project should exist", rodinProject);

		// Test a memento of the project
		String memento = rodinProject.getHandleIdentifier();
		assertEquals("/foo", memento);
		IRodinElement element = RodinCore.valueOf(memento);
		assertEquals(rodinProject, element);
		
		// When closed, the Rodin nature is not visible anymore.
		project.close(null);
		assertEquals("One non Rodin project", 1, db.getNonRodinResources().length);
		assertEquals("One non Rodin project", project, db.getNonRodinResources()[0]);
		assertEquals("No children", 0, db.getChildren().length);
		assertNotExists("Project should not exist", rodinProject);
		
		// Remove the project
		project.delete(true, true, null);
		assertEquals("No project left", 0, db.getNonRodinResources().length);
		assertEquals("No project left", 0, db.getRodinProjects().length);
		assertNotExists("Project should not exist", rodinProject);
	}

}
