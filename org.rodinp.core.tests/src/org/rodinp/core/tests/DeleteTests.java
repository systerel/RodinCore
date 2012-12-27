/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.DeleteTests
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.core.IRodinDBStatusConstants.NO_ELEMENTS_TO_PROCESS;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.RodinTestRoot;

/*
 * Tests for delete(...) methods on elements
 */
public class DeleteTests extends ModifyingResourceTests {
	
	public DeleteTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		// ensure that indexing is not going to interfer with deletion
//		waitUntilIndexesReady();
	}
	
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Should be able to delete a Rodin file.
	 */
	public void testDeleteRodinFile() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			
			startDeltas();
			file.delete(false, null);
			assertNotExists("Should be able to delete a Rodin file", file);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * After deleting a Rodin file in an IWorkspaceRunnable, it should not exist.
	 */
	public void testDeleteRodinFileInRunnable() throws CoreException {
		try {
			final IRodinFile file = createRodinFile("P/X.test");
			
			// force the file to be opened
			file.open(null);
			
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							file.delete(true, null);
							assertNotExists("Should be able to delete a Rodin file", file);
						}
					},
					null
			);
			assertNotExists("Should be able to delete a Rodin file", file);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * Ensures that a top internal element is deleted, all its descendants are also deleted. 
	 * Verify that the correct change deltas are generated.
	 */
	public void testDeleteInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			RodinTestRoot root= (RodinTestRoot) file.getRoot();
			IInternalElement ne1 = createNEPositive(root, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			IInternalElement ne3 = createNEPositive(ne2, "baz", null);
			
			startDeltas();
			assertDeletion(ne1);
			assertNotExists("Should be able to delete an internal element",  ne1);
			assertNotExists("A deleted element child should not exist",   ne2);
			assertNotExists("A deleted element descendant should not exist", ne3);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			foo[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that deletion can be canceled.
	 */
	public void testDeleteInternalCancelled() throws CoreException {
		try {
			final IRodinFile file = createRodinFile("P/X.test");
			final RodinTestRoot root= (RodinTestRoot) file.getRoot();
			final IInternalElement ne = createNEPositive(root, "foo", null);
			
			try {
				TestProgressMonitor monitor = TestProgressMonitor.getInstance();
				monitor.setCancelledCounter(0);
				getRodinDB().delete(new IRodinElement[] {ne}, false, monitor);
				fail("Should have been canceled");
			} catch (OperationCanceledException e) {
				// Pass
			}
		} finally {
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that an internal element can be deleted inside a scheduling rule
	 * that includes the resource only.
	 */
	public void testDeleteElementSchedulingRule() throws CoreException {
		try {
			IRodinFile rf = createRodinFile("P/X.test");
			RodinTestRoot root= (RodinTestRoot) rf.getRoot();
			final IInternalElement ne = createNEPositive(root, "foo", null);
			
			startDeltas();
			getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							assertDeletion(ne);
						}
					}, 
					rf.getUnderlyingResource(),
					IWorkspace.AVOID_UPDATE,
					null);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			foo[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that multiple member Rodin elements contained within different
	 * compilation units can be deleted.
	 * Verifies that the correct changed deltas are generated.
	 */
	public void testDeleteMultipleMembersFromVariousRFs() throws CoreException {
		try {
			IRodinFile rfX = createRodinFile("P/X.test");
			RodinTestRoot rootX = (RodinTestRoot) rfX.getRoot();
			NamedElement x1  = createNEPositive(rootX, "1", null);
			createNEPositive(x1,  "11", null);
			NamedElement x2  = createNEPositive(rootX, "2", null);
			NamedElement x21 = createNEPositive(x2,  "21", null);
			
			IRodinFile rfY = createRodinFile("P/Y.test");
			RodinTestRoot rootY = (RodinTestRoot) rfY.getRoot();
			NamedElement y1  = createNEPositive(rootY, "1", null);
			NamedElement y11 = createNEPositive(y1,  "11", null);
			NamedElement y2  = createNEPositive(rootY, "2", null);
			createNEPositive(y2,  "21", null);

			IRodinElement[] toBeDeleted = new IRodinElement[] { x1, x21, y11, y2 };
			
			startDeltas();
			assertDeletion(toBeDeleted);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			1[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"			2[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"				21[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		Y[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			1[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"				11[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"			2[org.rodinp.core.tests.namedElement][-]: {}" 
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
			deleteFile("P/Y.test");
		}
	}
	
	/**
	 * Ensure that the correct exception is thrown for invalid input to the <code>DeleteOperation</code>
	 */
	public void testDeleteWithInvalidInput() throws CoreException {
		try {
			getRodinDB().delete(null, false, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertError(e, NO_ELEMENTS_TO_PROCESS);
		}
		try {
			getRodinDB().delete(new IRodinElement[] {null}, false, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertError(e, NO_ELEMENTS_TO_PROCESS);
		}
		try {
			getRodinDB().delete(new IRodinElement[0], false, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			assertError(e, NO_ELEMENTS_TO_PROCESS);
		}
	}
}
