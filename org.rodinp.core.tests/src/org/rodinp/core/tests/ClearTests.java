/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.tests.model.DeleteTests
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - copied from DeleteTests to ClearTests
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
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/*
 * Tests for clear(...) methods on elements
 */
public class ClearTests extends ModifyingResourceTests {

	public ClearTests(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		// ensure that indexing is not going to interfere with deletion
//		waitUntilIndexesReady();
	}
	
	@Override
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Should be able to clear a Rodin file.
	 */
	public void testClearRodinFile1() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			createNEPositive(file, "foo", null);
			file.setAttributeValue(fBool, true, null);
			
			startDeltas();
			file.clear(false, null);
			assertCleared("Should be able to clear a Rodin file", file);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN | ATTRIBUTE}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * After clearing a Rodin file in an IWorkspaceRunnable, it should be
	 * cleared.
	 */
	public void testClearRodinFile2() throws CoreException {
		try {
			final IRodinFile file = createRodinFile("P/X.test");
			createNEPositive(file, "foo", null);
			
			// force the file to be opened
			file.open(null);
			
			startDeltas();
			ResourcesPlugin.getWorkspace().run(
					new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							file.clear(true, null);
							assertCleared("Should be able to clear a Rodin file", file);
						}
					},
					null
			);
			assertCleared("Should be able to clear a Rodin file", file);
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
	 * Ensures that a top internal element can be cleared.
	 * Verifies that the correct change deltas are generated.
	 */
	public void testClearTopInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne = createNEPositive(file, "foo", null);
			ne.setAttributeValue(fBool, true, null);
			
			startDeltas();
			assertClearing(ne);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that when a top internal element is cleared, all its descendants
	 * are deleted. Verifies that the correct change deltas are generated.
	 */
	public void testClearTopInternalWithChildren() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
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
					"		foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"			bar[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that an internal element can be cleared. 
	 * Verifies that the correct change deltas are generated.
	 */
	public void testClearInternal() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			ne2.setAttributeValue(fBool, true, null);
			
			startDeltas();
			assertClearing(ne2);
			assertExists("Parent of cleared element should still exist", ne1);
			assertCleared("Should be able to clear an internal element", ne2);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" +
					"			bar[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that when a internal element is cleared, all its descendants are deleted. 
	 * Verifies that the correct change deltas are generated.
	 */
	public void testClearInternalWithChildren() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne1 = createNEPositive(file, "foo", null);
			IInternalElement ne2 = createNEPositive(ne1, "bar", null);
			IInternalElement ne3 = createNEPositive(ne2, "baz", null);
			IInternalElement ne4 = createNEPositive(ne3, "ba2", null);
			
			startDeltas();
			assertClearing(ne2);
			assertExists("Parent of deleted element should still exist", ne1);
			assertCleared("Should be able to clear an internal element", ne2);
			assertNotExists("A cleared element child should not exist", ne3);
			assertNotExists("A cleared element descendant should not exist", ne4);
			assertDeltas(
					"Unexpected delta",
					"P[*]: {CHILDREN}\n" + 
					"	X.test[*]: {CHILDREN}\n" + 
					"		foo[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" +
					"			bar[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" + 
					"				baz[org.rodinp.core.tests.namedElement][-]: {}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}

	/**
	 * Ensures that clearing can be canceled.
	 */
	public void testClearInternalCancelled() throws CoreException {
		try {
			IRodinFile file = createRodinFile("P/X.test");
			IInternalElement ne = createNEPositive(file, "foo", null);
			
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
	public void testClearElementSchedulingRule() throws CoreException {
		try {
			IRodinFile rf = createRodinFile("P/X.test");
			final IInternalElement ne = createNEPositive(rf, "foo", null);
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
					"		foo[org.rodinp.core.tests.namedElement][*]: {ATTRIBUTE}"
			);
		} finally {
			stopDeltas();
			deleteFile("P/X.test");
		}
	}
	
	/**
	 * Ensure that the correct exception is thrown for invalid input to the <code>ClearOperation</code>
	 */
	public void testClearWithInvalidInput() throws CoreException {
		try {
			final IRodinFile rf = getRodinFile("P/X.test");
			try {
				rf.clear(false, null);
				fail("Should have raised a Rodin exception");
			} catch (RodinDBException e) {
				assertEquals("Should be an element does not exist",
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						e.getStatus().getCode());
			}

			createRodinFile("P/X.test");
			final IInternalElement ne = getNamedElement(rf, "foo");
			try {
				ne.clear(false, null);
				fail("Should have raised a Rodin exception");
			} catch (RodinDBException e) {
				assertEquals("Should be an element does not exist",
						IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST,
						e.getStatus().getCode());
			}
			
			final IRodinFile rf2 = rf.getSnapshot();
			try {
				rf2.clear(false, null);
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
