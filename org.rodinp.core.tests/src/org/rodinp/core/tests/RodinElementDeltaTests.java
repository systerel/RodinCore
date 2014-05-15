/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.ModifyingResourceTests
 *     Systerel - fixed expected delta for attribute change
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.eclipse.core.resources.IContainer.INCLUDE_HIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.ElementChangedEvent.POST_CHANGE;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Test;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * These test ensure that modifications in Rodin projects are correctly reported as
 * IRodinElementDeltas.
 */
public class RodinElementDeltaTests extends ModifyingResourceTests {
	
	@After
	public void tearDown() throws Exception {
		stopDeltas();
		clearWorkspace();
		super.tearDown();
	}

	private void clearWorkspace() throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject p : root.getProjects(INCLUDE_HIDDEN)) {
			p.open(null);
		}
		root.delete(true, null);
	}

	/**
	 * Add file in project.
	 */
	@Test
	public void testAddFile() throws CoreException {
		createRodinProject("P");
		startDeltas();
		createRodinFile("P/X.test");
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN}\n" +
				"	X.test[+]: {}"
		);
	}
	
	/**
	 * Add file after opening its project
	 */
	@Test
	public void testAddFileAfterProjectOpen() throws CoreException {
		createRodinProject("P1");
		IRodinProject p2 = createRodinProject("P2");
		createRodinFile("P2/X.test");
		IProject project = p2.getProject();
		project.close(null);
		
		// open project
		project.open(null);
		
		startDeltas();
		createRodinFile("P2/Y.test");
		assertDeltas(
				"Unexpected delta", 
				"P2[*]: {CHILDREN}\n" +
				"	Y.test[+]: {}"
		);
	}

	/**
	 * Ensure that a resource delta is fired when a file is added to a non-Rodin project.
	 */
	@Test
	public void testAddFileToNonRodinProject() throws CoreException {
		createProject("P");
		startDeltas();
		createFile("/P/toto.txt", "");
		assertDeltas(
				"Unexpected delta", 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Add the Rodin nature to an existing project.
	 */
	@Test
	public void testAddRodinNature() throws CoreException {
		createProject("P");
		startDeltas();
		addRodinNature("P");
		assertDeltas(
				"Unexpected delta", 
				"P[+]: {}\n" + 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Add a Rodin project.
	 */
	@Test
	public void testAddRodinProject() throws CoreException {
		startDeltas();
		createRodinProject("P");
		assertDeltas(
				"Unexpected delta", 
				"P[+]: {}"
		);
	}
	
	/**
	 * Add a non-Rodin project.
	 */
	@Test
	public void testAddNonRodinProject() throws CoreException {
		startDeltas();
		createProject("P");
		assertDeltas(
				"Should get a non-Rodin resource delta", 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Add a (non-Rodin) folder.
	 */
	@Test
	public void testAddFolder() throws CoreException {
		createRodinProject("P");
		startDeltas();
		createFolder("P/x");
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/x)[+]"
		);
	}
	
	/**
	 * Add a (non-Rodin) folder inside another folder.
	 */
	@Test
	public void testAddFolder2() throws CoreException {
		createRodinProject("P");
		createFolder("P/x");

		startDeltas();
		createFolder("P/x/y");
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/x)[*]"
		);
	}
	
	/**
	 * Add two Rodin projects in an IWorkspaceRunnable.
	 */
	@Test
	public void testAddTwoRodinProjects() throws CoreException {
		startDeltas();
		ResourcesPlugin.getWorkspace().run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						createRodinProject("P1");
						createRodinProject("P2");
					}
				},
				null);
		assertEquals(
				"Unexpected delta", 
				"P1[+]: {}\n" +
				"P2[+]: {}", 
				getSortedByProjectDeltas());
	}
	
	/**
	 * Batch operation test.
	 */
	@Test
	public void testBatchOperation() throws CoreException {
		createRodinProject("P");
		createRodinFile("P/A.test");
		startDeltas();
		RodinCore.run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						IRodinFile rf = getRodinFile("P/A.test");
						RodinTestRoot root = (RodinTestRoot) rf.getRoot();
						NamedElement foo = createNEPositive(root, "foo", null);
						NamedElement bar = createNEPositive(root, "bar", foo);
						createNEPositive(bar, "baz", null);
					}
				},
				null);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN}\n" + 
				"	A.test[*]: {CHILDREN}\n" + 
				"		A[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
				"			foo[org.rodinp.core.tests.namedElement][+]: {}\n" + 
				"			bar[org.rodinp.core.tests.namedElement][+]: {}"
		);
	}
	
	/**
	 * Close a Rodin project.
	 */
	@Test
	public void testCloseRodinProject() throws CoreException {
		createRodinProject("P");
		IProject project = getProject("P");
		startDeltas();
		project.close(null);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CLOSED}\n" + 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Close a non-Rodin project.
	 */
	@Test
	public void testCloseNonRodinProject() throws CoreException {
		createProject("P");
		IProject project = getProject("P");
		startDeltas();
		project.close(null);
		assertDeltas(
				"Unexpected delta", 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Test that deltas are generated when a file is added
	 * and removed from a project via core API.
	 */
	@Test
	public void testRodinFileRemoveAndAdd() throws CoreException {
		createRodinProject("P");
		IFile file = createRodinFile("/P/X.test").getResource();
		
		// delete file
		startDeltas();
		deleteResource(file);
		assertDeltas(
				"Unexpected delta after deleting /P/p/X.test",
				"P[*]: {CHILDREN}\n" + 
				"	X.test[-]: {}"
		);
		
		// add file
		clearDeltas();
		createRodinFile("/P/X.test");
		assertDeltas(
				"Unexpected delta after adding /P/p/X.test",
				"P[*]: {CHILDREN}\n" + 
				"	X.test[+]: {}"
		);
	}

	/**
	 * Ensures that merging a Rodin delta with another one that contains a resource delta
	 * results in a Rodin delta with the resource delta.
	 * (regression test for 11210 ResourceDeltas are lost when merging deltas)
	 */
	@Test
	public void testMergeResourceDeltas() throws CoreException {
		createRodinProject("P");
		startDeltas();
		ResourcesPlugin.getWorkspace().run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						// an operation that creates a Rodin delta without firing it
						createRodinFile("P/X.test");
						
						// an operation that generates a non Rodin resource delta
						createFile("P/Y.txt", "");
					}
				},
				null
		);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN | CONTENT}\n" +
				"	X.test[+]: {}\n" +
				"	ResourceDelta(/P/Y.txt)[+]"
		);
	}

	@Test
	public void testModifyContents() throws CoreException {
		createRodinProject("P");
		IRodinFile rodinFile = createRodinFile("P/A.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		
		startDeltas();
		assertContentsChanged(ne, "bar");
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN}\n" +
				"	A.test[*]: {CHILDREN}\n" +
				"		A[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
				"			foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
		);
	}

	@Test
	public void testModifyContentsAndSave() throws CoreException {
		createRodinProject("P");
		IRodinFile rodinFile = createRodinFile("P/A.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		
		startDeltas();
		assertContentsChanged(ne, "bar");
		rodinFile.save(null, false);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN}\n" + 
				"	A.test[*]: {CHILDREN}\n" + 
				"		A[org.rodinp.core.tests.test][*]: {CHILDREN}\n" + 
				"			foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}\n" + 
				"\n" + 
				"P[*]: {CHILDREN}\n" + 
				"	A.test[*]: {CONTENT}"
		);
		// TODO should produce only the first delta, not the second.
	}

	@Test
	public void testModifyContentsNoSave() throws CoreException {
		createRodinProject("P");
		IRodinFile rodinFile = createRodinFile("P/A.test");
		RodinTestRoot root = (RodinTestRoot) rodinFile.getRoot();
		NamedElement ne = createNEPositive(root, "foo", null);
		
		startDeltas();
		assertContentsChanged(ne, "bar");
		rodinFile.close();
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN}\n" +
				"	A.test[*]: {CHILDREN}\n" +
				"		A[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
				"			foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
		);
	}

	/**
	 * bug 18953
	 */
	@Test
	public void testModifyProjectDescriptionAndRemoveFolder() throws CoreException {
		IRodinProject project = createRodinProject("P");
		final IProject projectFolder = project.getProject();
		final IFolder folder = createFolder("/P/folder");
		
		startDeltas();
		getWorkspace().run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						IProjectDescription desc = projectFolder.getDescription();
						desc.setComment("A comment");
						projectFolder.setDescription(desc, null);
						deleteResource(folder);
					}
				},
				null);
		
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/.project)[*]\n" + 
				"	ResourceDelta(/P/folder)[-]"
		);
	}
	
	/**
	 * Move a non-Rodin file that is under a folder.
	 */
	@Test
	public void testMoveResInFolder() throws CoreException {
		createRodinProject("P");
		IProject project = getProject("P");
		createFolder("P/x");
		IFile file = createFile("P/x/X.test", emptyBytes);
		
		assertElementDescendants("Unexpected project descendants",
				"P",
				getRodinProject("P")
		);
		startDeltas();
		file.move(project.getFullPath().append("/X.test"), true, null);
		assertElementDescendants("Unexpected project descendants",
				"P\n" + 
				"  X.test\n" +
				"    X[org.rodinp.core.tests.test]",
				getRodinProject("P")
		);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CHILDREN | CONTENT}\n" +
				"	X.test[+]: {}\n" +
				"	ResourceDelta(/P/x)[*]"
		);
	}
	
	/**
	 * Test that deltas are generated when a non-Rodin file is
	 * removed and added
	 */
	@Test
	public void testNonRodinResourceRemoveAndAdd() throws CoreException {
		createRodinProject("P");
		IFile file = createFile("/P/read.txt", "");
		
		startDeltas();
		deleteResource(file);
		assertDeltas(
				"Unexpected delta after deleting /P/src/read.txt",
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/read.txt)[-]"
		);
		
		clearDeltas();
		createFile("/P/read.txt", "");
		assertDeltas(
				"Unexpected delta after creating /P/src/read.txt",
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/read.txt)[+]"
		);
	}
	
	
	/**
	 * Open a Rodin project.
	 */
	@Test
	public void testOpenRodinProject() throws CoreException {
		createRodinProject("P");
		IProject project = getProject("P");
		project.close(null);
		startDeltas();
		project.open(null);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {OPENED}\n" + 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Open a non-Rodin project.
	 */
	@Test
	public void testOpenNonRodinProject() throws CoreException {
		createProject("P");
		IProject project = getProject("P");
		project.close(null);
		startDeltas();
		project.open(null);
		assertDeltas(
				"Unexpected delta", 
				"ResourceDelta(/P)"
		);
	}

	/**
	 * Remove then add a Rodin project (in a workspace runnable).
	 */
	@Test
	public void testRemoveAddRodinProject() throws CoreException {
		createRodinProject("P");
		startDeltas();
		getWorkspace().run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						deleteProject("P");
						createRodinProject("P");
					}
				},
				null);
		assertDeltas(
				"Unexpected delta", 
				"P[*]: {CONTENT}\n" + 
				"	ResourceDelta(/P/.project)[*]"
		);
	}

	/**
	 * Remove the Rodin nature of an existing Rodin project.
	 */
	@Test
	public void testRemoveRodinNature() throws CoreException {
		createRodinProject("P");
		startDeltas();
		removeRodinNature("P");
		assertDeltas(
				"Unexpected delta", 
				"P[-]: {}\n" + 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Remove a Rodin project.
	 */
	@Test
	public void testRemoveRodinProject() throws CoreException {
		createRodinProject("P");
		startDeltas();
		deleteProject("P");
		assertDeltas(
				"Unexpected delta", 
				"P[-]: {}"
		);
	}
	
	/**
	 * Remove a non-Rodin project.
	 */
	@Test
	public void testRemoveNonRodinProject() throws CoreException {
		createProject("P");
		startDeltas();
		deleteProject("P");
		assertDeltas(
				"Should get a non-Rodin resource delta", 
				"ResourceDelta(/P)"
		);
	}
	
	/**
	 * Rename a Rodin project.
	 */
	@Test
	public void testRenameRodinProject() throws CoreException {
		createRodinProject("P");
		startDeltas();
		renameProject("P", "P1");
		assertDeltas(
				"Unexpected delta", 
				"P[-]: {MOVED_TO(P1)}\n" +
				"P1[+]: {MOVED_FROM(P)}"
		);
	}

	/**
	 * Rename a non-Rodin project.
	 */
	@Test
	public void testRenameNonRodinProject() throws CoreException {
		createProject("P");
		startDeltas();
		renameProject("P", "P1");
		assertDeltas(
				"Unexpected delta", 
				"ResourceDelta(/P)\n" + 
				"ResourceDelta(/P1)"
		);
	}
	
	/**
	 * Create a Rodin file in a batch operation.
	 */
	@Test
	public void testCreateInBatchOperation() throws CoreException {
		createRodinProject("P");
		startDeltas();
		RodinCore.run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				createRodinFile("P/X.test");
			}
		}, null);
		assertDeltas(
				"Unexpected delta",
				"P[*]: {CHILDREN}\n" +
				"	X.test[+]: {}");
	}

	@Test
	public void testEventType() throws Exception {
		final EventTypeListener listener = new EventTypeListener();
		try {
			RodinCore.addElementChangedListener(listener);
			createProject("P");
		} finally {
			RodinCore.removeElementChangedListener(listener);
		}
		assertTrue(listener.done);
	}

	private static final class EventTypeListener implements
			IElementChangedListener {
		public boolean done;

		public void elementChanged(ElementChangedEvent event) {
			assertEquals(POST_CHANGE, event.getType());
			done = true;
		}
	}

}
