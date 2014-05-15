/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Tests on Rodin openable elements.
 * 
 * @author Laurent Voisin
 */
public class OpenableTests extends ModifyingResourceTests {
	
	private final IRodinDB rodinDB = getRodinDB();
	private IRodinProject rodinProject;
	private IRodinProject rodinProject2;
	private IRodinFile rodinFile2;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		rodinProject2 = createRodinProject("P2");
		rodinFile2 = createRodinFile("P/immutable.test");
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		deleteProject("P2");
		super.tearDown();
	}

	private void assertUnsavedChanges(IRodinFile rodinFile, boolean expected)
			throws Exception {

		assertFalse("Other project should not have unsaved changes",
				rodinProject2.hasUnsavedChanges());
		assertEquals("Database unsaved changes report is wrong",
					expected, rodinDB.hasUnsavedChanges());
		assertEquals("Project unsaved changes report is wrong",
				expected, rodinProject.hasUnsavedChanges());
		assertEquals("File unsaved changes report is wrong",
				expected, rodinFile.hasUnsavedChanges());
		assertFalse("Other file should not have unsaved changes",
				rodinFile2.hasUnsavedChanges());
	}
	
	/*
	 * Ensures that a call to isConsistent() doesn't open a Rodin project. 
	 */
	@Test
	public void testIsConsistentProjectNoOpen() throws Exception {
		rodinProject.close();
		assertTrue("closed project should be consistent", 
				rodinProject.isConsistent());
		assertFalse("project should not be open", rodinProject.isOpen());
	}
	
	/*
	 * Ensures that a call to isConsistent() doesn't open a Rodin file. 
	 */
	@Test
	public void testIsConsistentFileNoOpen() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		rodinFile.close();
		assertTrue("closed file should be consistent", 
				rodinFile.isConsistent());
		assertFalse("file should not be open", rodinFile.isOpen());
	}
	
	/*
	 * Ensures that isConsistent() returns false for a modified file. 
	 */
	@Test
	public void testIsConsistentModifiedFile() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		assertFalse("modified file should not be consistent", 
				rodinFile.isConsistent());

		// Should report the same after closing the file.
		rodinFile.close();
		assertFalse("modified file should not be consistent", 
				rodinFile.isConsistent());
	}
	
	/*
	 * Ensures that isConsistent() returns true after saving a modified file. 
	 */
	@Test
	public void testIsConsistentModifiedFileSave() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		rodinFile.save(null, false);
		assertTrue("saved file should be consistent", 
				rodinFile.isConsistent());
	}
	
	/*
	 * Ensures that isConsistent() returns true after reverting a modified file. 
	 */
	@Test
	public void testIsConsistentModifiedFileRevert() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		rodinFile.makeConsistent(null);
		assertTrue("reverted file should be consistent", 
				rodinFile.isConsistent());
	}
	
	/*
	 * Ensures that a call to hasUnsavedChanges() doesn't open a Rodin file. 
	 */
	@Test
	public void testHasUnsavedChangesFileNoOpen() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		rodinFile.close();
		assertUnsavedChanges(rodinFile, false);
		assertFalse("file should not be open", rodinFile.isOpen());
	}
	
	/*
	 * Ensures that hasUnsavedChanges() returns true for a modified file. 
	 */
	@Test
	public void testHasUnsavedChangesModifiedFile() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		assertUnsavedChanges(rodinFile, true);

		// Should report the same after closing the file.
		rodinFile.close();
		assertUnsavedChanges(rodinFile, true);
	}
	
	/*
	 * Ensures that hasUnsavedChanges() returns false after saving a modified file. 
	 */
	@Test
	public void testHasUnsavedChangesModifiedFileSave() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		rodinFile.save(null, false);
		assertUnsavedChanges(rodinFile, false);
	}
	
	/*
	 * Ensures that hasUnsavedChanges() returns false after reverting a modified
	 * file.
	 */
	@Test
	public void testHasUnsavedChangesModifiedFileRevert() throws Exception {
		IRodinFile rodinFile = createRodinFile("P/x.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		createNEPositive(root, "foo", null);
		rodinFile.makeConsistent(null);
		assertUnsavedChanges(rodinFile, false);
	}
	
	/*
	 * Ensures that the Rodin Database always exists.
	 */
	@Test
	public void testExistsDB() throws Exception {
		assertExists("The Rodin database should exist", rodinDB);
	}
	
	/*
	 * Ensures that an opened Rodin project exists.
	 */
	@Test
	public void testExistsProjectOpen() throws Exception {
		assertExists("The Rodin project should exist", rodinProject);

		// Try after unloading the project from the database 
		rodinProject.close();
		assertExists("The Rodin project should exist", rodinProject);
		assertFalse("The existence test should not have opened the project",
				rodinProject.isOpen());
	}
	
	/*
	 * Ensures that a closed Rodin project doesn't exist.
	 */
	@Test
	public void testExistsProjectClosed() throws Exception {
		rodinProject.getProject().close(null);
		assertFalse("The project should not be open", rodinProject.isOpen());
		assertNotExists("The Rodin project should not exist", rodinProject);
		assertFalse("The existence test should not have opened the project",
				rodinProject.isOpen());
	}
	
	/*
	 * Ensures that an inexistent Rodin project doesn't exist.
	 */
	@Test
	public void testExistsProjectInexistent() throws Exception {
		IRodinProject other = getRodinProject("Inexistent");
		assertFalse("An existent project should not be open",
				other.isOpen());
		assertNotExists("The Rodin project should not exist", other);
		assertFalse("The existence test should not have opened the project",
				other.isOpen());
	}
	
}
