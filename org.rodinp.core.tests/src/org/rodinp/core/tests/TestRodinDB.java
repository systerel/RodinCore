package org.rodinp.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class TestRodinDB extends TestCase {

	/*
	 * Test an empty database.
	 */
	public final void testRodinDBEmpty() throws CoreException, RodinDBException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IRodinDB db = RodinCore.create(workspaceRoot);

		// Test handle-only methods
		assertNotNull(db);
		assertNull(db.getParent());
		assertEquals("", db.getElementName());
		assertEquals(IRodinElement.RODIN_DATABASE, db.getElementType());
		assertEquals(workspace, db.getWorkspace());
		assertEquals(workspaceRoot, db.getCorrespondingResource());
		assertFalse(db.isReadOnly());
		// last handle-only method
		
		// Opening methods.
		assertTrue(db.exists());
		assertTrue(db.isOpen());
		assertTrue(db.contains(workspaceRoot));
		assertEquals("No project initially", 0, db.getNonRodinResources().length);
		assertEquals("No project initially", 0, db.getRodinProjects().length);
		assertFalse("No project initially", db.hasChildren());
		assertEquals("No project initially", 0, db.getChildren().length);
	}

	/*
	 * Test project creation and deletion.
	 */
	public final void testRodinProject() throws CoreException, RodinDBException {
		IRodinDB db = RodinCore.create(ResourcesPlugin.getWorkspace().getRoot());

		// Creating a project handle
		IRodinProject rodinProject = db.getRodinProject("foo"); 
		assertNotNull(rodinProject);
		assertEquals("foo", rodinProject.getElementName());
		assertEquals(db, rodinProject.getParent());
		assertFalse(rodinProject.exists());

		// Actually create the project
		IProject project = rodinProject.getProject();
		project.create(null);
		assertEquals("One non Rodin project", 1, db.getNonRodinResources().length);
		assertEquals("One non Rodin project", project, db.getNonRodinResources()[0]);
		assertEquals("No children", 0, db.getChildren().length);
		assertFalse(rodinProject.exists());
		
		// Set the Rodin nature
		project.open(null);
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(description, null);
		assertEquals("No non Rodin project", 0, db.getNonRodinResources().length);
		IRodinElement[] childrens = db.getChildren();
		assertEquals(1, childrens.length);
		assertEquals(rodinProject, childrens[0]);
		assertTrue(rodinProject.exists());

		// Test a memento of the project
		String memento = rodinProject.getHandleIdentifier();
		assertEquals("/foo", memento);
		IRodinElement element = RodinCore.create(memento);
		assertEquals(rodinProject, element);
		
		// When closed, the Rodin nature is not visible anymore.
		project.close(null);
		assertEquals("One non Rodin project", 1, db.getNonRodinResources().length);
		assertEquals("One non Rodin project", project, db.getNonRodinResources()[0]);
		assertEquals("No children", 0, db.getChildren().length);
		assertFalse(rodinProject.exists());
		
		// Remove the project
		project.delete(true, true, null);
		assertEquals("No project left", 0, db.getNonRodinResources().length);
		assertEquals("No project left", 0, db.getRodinProjects().length);
		assertFalse(rodinProject.exists());
	}

}
