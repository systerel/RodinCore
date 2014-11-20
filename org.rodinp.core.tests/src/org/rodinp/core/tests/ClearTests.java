/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.tests.model.DeleteTests
 *     Systerel - copied from DeleteTests to ClearTests
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.RodinTestRoot;

/*
 * Tests for clear(...) methods on elements
 */
public class ClearTests extends ModifyingResourceTests {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		// ensure that indexing is not going to interfere with deletion
//		waitUntilIndexesReady();
	}
	
	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Should be able to clear the root element of a Rodin file.
	 */
	@Test
	public void testClearRoot() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			RodinTestRoot root = (RodinTestRoot) file.getRoot();
			createNEPositive(root, "foo", null);
			root.setAttributeValue(fBool, true, null);
			
			startDeltas();
			root.clear(false, null);
			assertCleared("Should be able to clear a Rodin file", root);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN | ATTRIBUTE}\n" + 
					"			foo[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * After clearing the root of a Rodin file in an IWorkspaceRunnable, it
	 * should be cleared.
	 */
	@Test
	public void testClearRootInRunnable() throws CoreException {
		try {
			final IRodinFile file = createRodinFile("P/X.test");
			final RodinTestRoot root = (RodinTestRoot) file.getRoot();
			createNEPositive(root, "foo", null);
			
			// force the file to be opened
			file.open(null);
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							root.clear(true, null);
							assertCleared("Should be able to clear a Rodin file", root);
						}
					},
					null
			);
			assertCleared("Should be able to clear a Rodin file", root);
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
	 * Ensures that an internal element can be cleared. Verifies that the
	 * correct change deltas are generated.
	 */
	@Test
	public void testClearInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			RodinTestRoot root = (RodinTestRoot) file.getRoot();
			IInternalElement ne = createNEPositive(root, "foo", null);
			ne.setAttributeValue(fBool, true, null);
			
			startDeltas();
			assertClearing(ne);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that when an internal element is cleared, all its descendants are
	 * deleted. Verifies that the correct change deltas are generated.
	 */
	@Test
	public void testClearInternalWithChildren() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			RodinTestRoot root = (RodinTestRoot) file.getRoot();
			IInternalElement ne1 = createNEPositive(root, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			IInternalElement ne3 = createNEPositive(ne2, "baz", null);
			
			startDeltas();
			assertClearing(ne1);
			assertCleared("Should be able to clear an internal element",  ne1);
			assertNotExists("A cleared element child should not exist",   ne2);
			assertNotExists("A cleared element descendant should not exist", ne3);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		X[org.rodinp.core.tests.test][*]: {CHILDREN}\n" +
					"			foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"				bar[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that clearing can be canceled.
	 */
	@Test
	public void testClearInternalCancelled() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			RodinTestRoot root = (RodinTestRoot) file.getRoot();
			IInternalElement ne = createNEPositive(root, "foo", null);
			
			boolean isCanceled = false;
			try {
				TestProgressMonitor monitor = TestProgressMonitor.getInstance();
				monitor.setCancelledCounter(0);
				ne.clear(false, monitor);
			} catch (OperationCanceledException e) {
				isCanceled = true;
			}
			assertTrue("Operation should have thrown an operation canceled exception", isCanceled);
		} finally {
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that an internal element can be cleared inside a scheduling rule
	 * that includes the resource only.
	 */
	@Test
	public void testClearElementSchedulingRule() throws CoreException {
		try {
			IRodinFile rf = createRodinFile("P/X.test");
			RodinTestRoot root = (RodinTestRoot) rf.getRoot();
			final IInternalElement ne = createNEPositive(root, "foo", null);
			ne.setAttributeValue(fBool, true, null);
			
			startDeltas();
			getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							assertClearing(ne);
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
					"			foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * Ensure that the correct exception is thrown for invalid input to the <code>ClearOperation</code>
	 */
	@Test
	public void testClearWithInvalidInput() throws CoreException {
		try {
			final IRodinFile rf = getRodinFile("P/X.test");
			final IInternalElement root = rf.getRoot();
			try {
				root.clear(false, null);
				fail("Should have raised a Rodin exception");
			} catch (RodinDBException e) {
				assertEquals("Should be an element does not exist",
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						e.getStatus().getCode());
			}

			createRodinFile("P/X.test");
			final IInternalElement ne = getNamedElement(root, "foo");
			try {
				ne.clear(false, null);
				fail("Should have raised a Rodin exception");
			} catch (RodinDBException e) {
				assertEquals("Should be an element does not exist",
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						e.getStatus().getCode());
			}
			
			final IInternalElement root2 = root.getSnapshot();
			try {
				root2.clear(false, null);
				fail("Should have raised a Rodin exception");
			} catch (RodinDBException e) {
				assertEquals("Should be an element is read-only",
						IRodinDBStatusConstants.READ_ONLY,
						e.getStatus().getCode());
			}
		} finally {
			deleteFile("P/X.test");
		}
	}

}
