/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.tests.model.DeleteTests.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;

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
	public void testDeleteRodinFile1() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			
			startDeltas();
			file.delete(false, null);
			assertTrue("Should be able to delete a Rodin file", ! file.exists());
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
	public void testDeleteRodinFile2() throws CoreException {
		try {
			final IRodinFile file = createRodinFile("P/X.test");
			
			// force the file to be opened
			file.open(null);
			
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							file.delete(true, null);
							assertTrue("Should be able to delete a Rodin file", ! file.exists());
						}
					},
					null
			);
			assertTrue("Should be able to delete a Rodin file", ! file.exists());
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
	 * Ensures that a top internal element can be deleted.
	 * Verify that the correct change deltas are generated.
	 */
	public void testDeleteTopInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne = createNEPositive(file, "foo", null);
			
			startDeltas();
			assertDeletion(ne);
			assertTrue("Should be able to delete an internal element", ! ne.exists());
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {}"
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
	public void testDeleteTopInternalWithChildren() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			IInternalElement ne3 = createNEPositive(ne2, "baz", null);
			
			startDeltas();
			assertDeletion(ne1);
			assertTrue("Should be able to delete an internal element",  ! ne1.exists());
			assertTrue("A deleted element children should not exist",   ! ne2.exists());
			assertTrue("A deleted element descendant should not exist", ! ne3.exists());
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that an internal element can be deleted. 
	 * Verify that the correct change deltas are generated.
	 */
	public void testDeleteInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			
			startDeltas();
			assertDeletion(ne2);
			assertTrue("Parent of deleted element should still exist",    ne1.exists());
			assertTrue("Should be able to delete an internal element",  ! ne2.exists());
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" +
					"			bar[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that a internal element is deleted, all its descendants are also deleted. 
	 * Verify that the correct change deltas are generated.
	 */
	public void testDeleteInternalWithChildren() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			IInternalElement ne3 = createNEPositive(ne2, "baz", null);
			IInternalElement ne4 = createNEPositive(ne3, "ba2", null);
			
			startDeltas();
			assertDeletion(ne2);
			assertTrue("Parent of deleted element should still exist",    ne1.exists());
			assertTrue("Should be able to delete an internal element",  ! ne2.exists());
			assertTrue("A deleted element descendant should not exist", ! ne3.exists());
			assertTrue("A deleted element descendant should not exist", ! ne4.exists());
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" +
					"			bar[org.rodinp.core.tests.namedElement][-]: {}"
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
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne = createNEPositive(file, "foo", null);
			
			boolean isCanceled = false;
			try {
				TestProgressMonitor monitor = TestProgressMonitor.getInstance();
				monitor.setCancelledCounter(0);
				getRodinDB().delete(new IRodinElement[] {ne}, false, monitor);
			} catch (OperationCanceledException e) {
				isCanceled = true;
			}
			assertTrue("Operation should have thrown an operation canceled exception", isCanceled);
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
			final IInternalElement ne = createNEPositive(rf, "foo", null);
			
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
					"		foo[org.rodinp.core.tests.namedElement][-]: {}"
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
			NamedElement x1  = createNEPositive(rfX, "1", null);
			createNEPositive(x1,  "11", null);
			NamedElement x2  = createNEPositive(rfX, "2", null);
			NamedElement x21 = createNEPositive(x2,  "21", null);
			
			IRodinFile rfY = createRodinFile("P/Y.test");
			NamedElement y1  = createNEPositive(rfY, "1", null);
			NamedElement y11 = createNEPositive(y1,  "11", null);
			NamedElement y2  = createNEPositive(rfY, "2", null);
			createNEPositive(y2,  "21", null);

			IRodinElement[] toBeDeleted = new IRodinElement[] { x1, x21, y11, y2 };
			
			startDeltas();
			assertDeletion(toBeDeleted);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		1[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"		2[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"			21[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"	Y.test[*]: {CHILDREN}\n" + 
					"		1[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"			11[org.rodinp.core.tests.namedElement][-]: {}\n" + 
					"		2[org.rodinp.core.tests.namedElement][-]: {}" 
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
			createRodinFile("P/X.test");
			getRodinDB().delete(null, false, null);
		} catch (RodinDBException e) {
			assertTrue("Should be a no elements to process: null supplied", e.getStatus().getCode() == IRodinDBStatusConstants.NO_ELEMENTS_TO_PROCESS);
			try {
				getRodinDB().delete(new IRodinElement[] {null}, false, null);
			} catch (RodinDBException e2) {
				assertTrue("Should be an no elements to process: null in the array supplied", e2.getStatus().getCode() == IRodinDBStatusConstants.NO_ELEMENTS_TO_PROCESS);
			}
			try {
				getRodinDB().delete(new IRodinElement[0], false, null);
			} catch (RodinDBException e3) {
				assertTrue("Should be an no elements to process: null in the array supplied", e3.getStatus().getCode() == IRodinDBStatusConstants.NO_ELEMENTS_TO_PROCESS);
			}
		} finally {
			deleteFile("P/X.test");
		}
	}
}
