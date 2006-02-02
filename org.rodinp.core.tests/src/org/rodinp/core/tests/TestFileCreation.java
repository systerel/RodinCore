package org.rodinp.core.tests;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestFileCreation extends AbstractRodinDBTests {

	public TestFileCreation() {
		super("org.rodinp.core.tests.TestFileCreation");
	}

	private IRodinProject rodinProject;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("foo");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}
	
	// Test creation of a Rodin file through the IResource API
	public void testCreateRodinFile1() throws Exception {
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertFalse(rodinFile.exists());
		
		// Actually create the file
		IFile file = (IFile) rodinFile.getResource();
		
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<org.rodinp.core.tests.test/>\n";
		file.create(new ByteArrayInputStream(contents.getBytes("UTF-8")), true, null);
		assertTrue(rodinFile.exists());
		
		// Test a memento of the file
		String memento = rodinFile.getHandleIdentifier();
		assertEquals("/foo/toto.test", memento);
		IRodinElement element = RodinCore.create(memento);
		assertEquals(rodinFile, element);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of an empty Rodin file through the IResource API
	public void testCreateEmptyRodinFile() throws Exception {
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.getRodinFile("toto.test");
		assertFalse(rodinFile.exists());
		
		// Actually create the file
		IFile file = rodinFile.getResource();
		file.create(new ByteArrayInputStream(new byte[0]), true, null);
		// As the file is not an XML file, there is no Rodin file for it.
		assertFalse(rodinFile.exists());
		// TODO fix error message on the console for this test.
		
		// Then delete the file
		file.delete(true, null);
		assertFalse(rodinFile.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a Rodin file through the Rodin API
	public void testCreateRodinFile2() throws CoreException, RodinDBException{
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertTrue(rodinFile.exists());
		
		// Test a memento of the file
		String memento = rodinFile.getHandleIdentifier();
		assertEquals("/foo/toto.test", memento);
		IRodinElement element = RodinCore.create(memento);
		assertEquals(rodinFile, element);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a Rodin file through the Rodin API, when the file exists already
	public void testCreateExistingRodinFile() throws CoreException, RodinDBException{
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		
		// Create one Rodin file handle
		IRodinFile rodinFile = rodinProject.createRodinFile("toto.test", true, null);
		assertTrue(rodinFile.exists());
		
		// Create the same Rodin file again
		IRodinFile rodinFile2 = rodinProject.createRodinFile("toto.test", true, null);
		assertTrue(rodinFile2.exists());
		assertEquals(rodinFile, rodinFile2);
		
		// Then delete it
		rodinFile.getResource().delete(true, null);
		assertFalse(rodinFile.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

	// Test creation of a non-Rodin file
	public void testCreateNonRodinFile() throws CoreException, RodinDBException{
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one Non Rodin file handle
		IFile file = rodinProject.getProject().getFile("tagada");
		assertFalse(file.exists());
		
		// Actually create the file
		file.create(new ByteArrayInputStream(new byte[] {}), true, null);
		assertTrue(file.exists());
		assertEquals("Project with one non-Rodin file", 2, rodinProject.getNonRodinResources().length);
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getRodinFiles().length);
		
		// Then delete it
		file.delete(true, null);
		assertFalse(file.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Remove project
		rodinProject.getProject().delete(true, true, null);
	}

	// Test creation of a folder
	public void testCreateFolder() throws CoreException, RodinDBException{
		assertTrue(rodinProject.exists());
		
		// Check project is empty
		assertEquals("Empty project", 0, rodinProject.getChildren().length);
		assertFalse(rodinProject.hasChildren());
		// 1 because of the ".project" file
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
		
		// Create one folder handle
		IFolder folder = rodinProject.getProject().getFolder("bar");
		assertFalse(folder.exists());
		
		// Actually create the file
		folder.create(true, true, null);
		assertTrue(folder.exists());
		assertEquals("Project with one non-Rodin file", 2, rodinProject.getNonRodinResources().length);
		assertEquals("Project with one non-Rodin file", 0, rodinProject.getRodinFiles().length);
		
		// Then delete it
		folder.delete(true, null);
		assertFalse(folder.exists());
		assertEquals("Empty project", 1, rodinProject.getNonRodinResources().length);
		assertEquals("Empty project", 0, rodinProject.getRodinFiles().length);
	}

}
