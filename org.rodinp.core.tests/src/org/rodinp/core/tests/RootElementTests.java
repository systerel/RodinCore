/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.core.IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_DESTINATION;
import static org.rodinp.core.IRodinDBStatusConstants.NAME_COLLISION;
import static org.rodinp.core.IRodinDBStatusConstants.ROOT_ELEMENT;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Unit tests for root elements.
 * 
 * @author Laurent Voisin
 */
public class RootElementTests extends CopyMoveTests {

	// Source file
	IRodinFile rfSource;

	// Source root element
	IInternalElement rSource;

	// Source internal element
	IInternalElement neSource;

	// Destination file
	IRodinFile rfDest;

	// Destination root element
	IInternalElement rDest;

	public RootElementTests(String name) {
		super(name);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		rfSource = createRodinFile("P/X.test");
		rSource = rfSource.getRoot();
		neSource = createNEPositive(rSource, "foo", null);

		rfDest = createRodinFile("P/Y.test");
		rDest = rfDest.getRoot();
	}

	@Override
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	/**
	 * Ensure that isRoot() works properly on handles only.
	 */
	public void testIsRoot() throws Exception {
		final IRodinDB db = getRodinDB();
		final IRodinProject prj = getRodinProject("INEXISTENT");
		final IRodinFile rf = getRodinFile("P/inexistent.test");
		final IInternalElement root = rf.getRoot();
		final NamedElement ne = getNamedElement(root, "inexistent");

		assertFalse(db.isRoot());
		assertFalse(prj.isRoot());
		assertFalse(rf.isRoot());
		assertTrue(root.isRoot());
		assertFalse(ne.isRoot());
	}

	/**
	 * Ensures that copying to a root element is invalid, whatever the
	 * parameters.
	 */
	public void testCopyToRoot() throws CoreException {
		final String name = rDest.getElementName();

		copyNegative(rSource, rfDest, null, null, false, INVALID_DESTINATION);
		copyNegative(rSource, rfDest, null, name, false, INVALID_DESTINATION);
		copyNegative(rSource, rfDest, null, null, true, INVALID_DESTINATION);
		copyNegative(rSource, rfDest, null, name, true, INVALID_DESTINATION);
		copyNegative(neSource, rfDest, null, null, false, INVALID_DESTINATION);

		assertCleared("Should be empty", rDest);
	}

	/**
	 * Ensures that copying from a root element works as expected.
	 */
	public void testCopyFromRoot() throws CoreException {
		copyPositive(rSource, rDest, null, null, false);
	}

	/**
	 * Ensures that a root element cannot be deleted.
	 */
	public void testDeleteRoot() throws Exception {
		deleteNegative(rSource, false, ROOT_ELEMENT, rSource);
		deleteNegative(rSource, true, ROOT_ELEMENT, rSource);
	}

	/**
	 * Ensures that a root element cannot be moved from.
	 */
	public void testMoveFromRoot() throws CoreException {
		moveNegative(rSource, rDest, null, null, false, ROOT_ELEMENT);
		moveNegative(rSource, rDest, null, null, true, ROOT_ELEMENT);
		moveNegative(rSource, rfSource, null, null, false, ROOT_ELEMENT);
		moveNegative(rSource, rfSource, null, null, true, ROOT_ELEMENT);
	}

	/**
	 * Ensures that one cannot move to a root element.
	 */
	public void testMoveToRoot() throws CoreException {
		final String name = rDest.getElementName();

		moveNegative(neSource, rfDest, null, null, false, INVALID_DESTINATION);
		moveNegative(neSource, rfDest, null, name, false, INVALID_DESTINATION);
		moveNegative(neSource, rfDest, null, null, true, INVALID_DESTINATION);
		moveNegative(neSource, rfDest, null, name, true, INVALID_DESTINATION);
	}

	/**
	 * Ensures that a root element cannot be renamed.
	 */
	public void testRenameRoot() throws CoreException {
		renameNegative(rSource, "Y", false, ROOT_ELEMENT);
		renameNegative(rSource, "Y", true, ROOT_ELEMENT);
	}

	/**
	 * Ensures that an existing root element cannot be created again.
	 */
	public void testCreateRootExists() throws Exception {
		createNegative(rSource, null, NAME_COLLISION, rSource);
		createNegative(rSource, rSource, NAME_COLLISION, rSource);
	}

	/**
	 * Ensures a root element cannot be created when its file doesn't exist.
	 */
	public void testCreateRootNoFile() throws Exception {
		final IRodinFile rf = getRodinFile("P/Z.test");
		final IInternalElement root = rf.getRoot();

		createNegative(root, null, ELEMENT_DOES_NOT_EXIST, rf);
		assertFalse(rf.exists());
		assertFalse(root.exists());
	}

	private void createNegative(final IInternalElement element,
			final IInternalElement nextSibling, int errorCode,
			IRodinElement errorElement) throws CoreException {
		assertNoop(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				element.create(nextSibling, null);
			}
		}, errorCode, errorElement);
	}

	private void deleteNegative(final IInternalElement element,
			final boolean force, int errorCode, IInternalElement errorElement)
			throws CoreException {
		assertNoop(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				element.delete(force, null);
			}
		}, errorCode, errorElement);
	}

	private void assertNoop(IWorkspaceRunnable runnable, int errorCode,
			IRodinElement element) throws CoreException {
		try {
			startDeltas();
			assertErrorFor(runnable, errorCode, element);
			assertNoDeltas("Unexpected delta");
			assertElementDescendants(
					"Source file has changed", //
					"X.test\n" //
							+ "  X[org.rodinp.core.tests.test]\n"
							+ "    foo[org.rodinp.core.tests.namedElement]",
					rfSource);
			assertCleared("Destination file should be empty", rDest);
		} finally {
			stopDeltas();
		}
	}

}
