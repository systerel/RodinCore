/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.proofpurger.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import org.eventb.internal.ui.proofpurger.ProofPurgerContentProvider;
import org.junit.Test;

public class ProofPurgerContentProviderTests extends TestCase {

	private static final IPRRoot[] NO_ROOT = new IPRRoot[0];
	
	private static final IRodinDB db = RodinCore.getRodinDB();
	
	// variables for testing with several projects
	private static IRodinProject rp1 = db.getRodinProject("P1");
	private static IRodinProject rp2 = db.getRodinProject("P2");
	private static IRodinProject rp3 = db.getRodinProject("P3");

	private static final String PO1 = "PO1"; //$NON-NLS-1$
	private static final String PO2 = "PO2"; //$NON-NLS-1$
	private static final String PO3 = "PO3"; //$NON-NLS-1$
	private static final String PO4 = "PO4"; //$NON-NLS-1$
	
	/**
	 * Returns a handle to the IMachineRoot with the given barename.
	 * 
	 * @param rp
	 *            The enclosing IRodinProject.
	 * @param bareName
	 *            The IMachineRoot barename.
	 * @return A handle to the IMachineRoot.
	 */
	protected IMachineRoot getMachineRoot(IRodinProject rp, String bareName) {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		return (IMachineRoot) rp.getRodinFile(fileName).getRoot();
	}
	
	/**
	 * Returns a handle to the IPRRoot with the given barename.
	 * 
	 * @param rp
	 *            The enclosing IRodinProject.
	 * @param bareName
	 *            The IPRRoot barename.
	 * @return A handle to the IPRRoot.
	 */
	protected IPRRoot getPRRoot(IRodinProject rp, String bareName) {
		final String fileName = EventBPlugin.getPRFileName(bareName);
		return (IPRRoot) rp.getRodinFile(fileName).getRoot();
	}

	/**
	 * Asserts that the given arrays are equal.
	 * 
	 * @param message
	 *            Error message to display in case of inequality.
	 * @param expected
	 *            The expected array.
	 * @param actual
	 *            The actual array.
	 */
	public <T extends Object> void assertArrayEquals(String message, T[] expected,
			T[] actual) {
		final List<T> l1 = Arrays.asList(expected);
		final List<T> l2 = Arrays.asList(actual);
		assertEquals(message, l1, l2);
	}

	/**
	 * Asserts that the given sets are equal.
	 * 
	 * @param message
	 *            Error message to display in case of inequality.
	 * @param expected
	 *            The expected set.
	 * @param actual
	 *            The actual set.
	 */
	public <T extends Object> void assertSetEquals(String message,
			T[] expected, T[] actual) {
		final Set<T> s1 = new HashSet<T>(Arrays.asList(expected));
		final Set<T> s2 = new HashSet<T>(Arrays.asList(actual));
		assertTrue(message, s1.equals(s2));
	}

	/**
	 * Asserts that the given array is empty.
	 * 
	 * @param msg
	 *            A description of the array.
	 * @param array
	 *            The tested array.
	 */
	private void assertEmpty(final String msg, Object[] array) {
		assertEquals(msg + " should be empty", 0, array.length);
	}
	
	/**
	 * Asserts that the given parent has the given expected children.
	 * 
	 * @param msg
	 *            A description of the parent type.
	 * @param cp
	 *            The tested ProofPurgerContentProvider.
	 * @param parent
	 *            The tested parent.
	 * @param expected
	 *            The expected corresponding children.
	 */
	private void assertChildren(String msg, ProofPurgerContentProvider cp,
			IRodinElement parent, IRodinElement... expected) {
		if (expected.length != 0) {
			assertTrue("hasChildren(" + msg + ") == true", cp.hasChildren(parent));
			assertArrayEquals("getChildren(" + msg + ")", expected, cp.getChildren(parent));
		} else {
			assertFalse("hasChildren(" + msg + ") != true", cp.hasChildren(parent));
			assertEmpty("getChildren(" + msg + ")", cp.getChildren(parent));
		}
	}

	/**
	 * Asserts that the given element has the given expected parent.
	 * 
	 * @param msg
	 *            A description of the element type.
	 * @param cp
	 *            The tested ProofPurgerContentProvider.
	 * @param element
	 *            The tested element.
	 * @param expected
	 *            The expected corresponding parent.
	 */
	private void assertParent(String msg, ProofPurgerContentProvider cp,
			IRodinElement element, IRodinElement expected) {
		assertEquals("getParent(" + msg + ")", expected, cp.getParent(element));
	}

	/**
	 * Creates and initializes a ProofPurgerContentProvider from the given
	 * proofs with no files to delete.
	 * 
	 * @param cpProofs
	 *            The proofs from which to initialize the CP.
	 * @return The newly created ProofPurgerContentProvider
	 */
	private static ProofPurgerContentProvider initCP(IPRProof... cpProofs) {
		return initCP(cpProofs, NO_ROOT);
	}

	private static ProofPurgerContentProvider initCP(IPRProof[] cpProofs,
			IPRRoot[] cpFiles) {
		return new ProofPurgerContentProvider(cpProofs, cpFiles);
	}

	
	
	/**
	 * Tests hasChildren and GetChildren with null input argument.
	 */
	@Test
	public void testHasGetChildrenNull() throws Exception {
		final ProofPurgerContentProvider cp = initCP();
		assertChildren("null", cp, null);
	}

	/**
	 * Tests hasChildren and GetChildren with unexpected input argument type.
	 */
	@Test
	public void testHasGetChildrenUnexpectedType() throws Exception {
		final IMachineRoot m = getMachineRoot(rp1, "m1");
		final ProofPurgerContentProvider cp = initCP();
		assertChildren("MachineFile", cp, m);
	}

	/**
	 * Tests hasChildren and GetChildren with each valid input argument type.
	 */
	@Test
	public void testHasGetChildrenEachType() throws Exception {
		final IPRRoot prFile1 = getPRRoot(rp1, "m1");
		final IPRProof pr1 = prFile1.getProof(PO1);
		final ProofPurgerContentProvider cp = initCP(pr1);

		assertChildren("IRodinDB", cp, db, rp1);
		assertChildren("IRodinProject", cp, rp1, prFile1);
		assertChildren("IPRRoot", cp, prFile1, pr1);
	}

	/**
	 * Tests hasChildren and GetChildren with an empty input PRFile.
	 */
	@Test
	public void testHasGetChildrenEmptyPRFile() throws Exception {
		final IPRRoot prFile1 = getPRRoot(rp1, "m1");
		final ProofPurgerContentProvider cp = initCP();
		assertChildren("IPRRoot", cp, prFile1);
	}

	/**
	 * Tests getParent with a null input argument.
	 */
	@Test
	public void testGetParentNull() throws Exception {
		final ProofPurgerContentProvider cp = initCP();
		assertNull("getChildren(null) != null", cp.getParent(null));
	}

	/**
	 * Tests getParent with unexpected input argument types.
	 */
	@Test
	public void testGetParentUnexpectedType() throws Exception {
		final IMachineRoot m = getMachineRoot(rp1, "m1");
		final ProofPurgerContentProvider cp = initCP();
		assertParent("IMachineRoot", cp, m, null);
	}

	/**
	 * Tests getParent with each valid input argument type.
	 */
	@Test
	public void testGetParentEachType() throws Exception {
		final IPRRoot prFile1 = getPRRoot(rp1, "m1");
		final IPRProof pr1 = prFile1.getProof(PO1);
		final ProofPurgerContentProvider cp = initCP(pr1);
		assertParent("IPRProof", cp, pr1, prFile1);
		assertParent("IPRRoot", cp, prFile1, rp1);
		assertParent("IRodinProject", cp, rp1, db);
	}

	/**
	 * Tests getParent with input arguments which have an invisible parent (from
	 * ContentProvider).
	 */
	@Test
	public void testGetParentNoVisibleParent() throws Exception {
		final IPRRoot prFile1 = getPRRoot(rp1, "m1");
		final IPRProof pr1 = prFile1.getProof(PO1);
		final ProofPurgerContentProvider cp = initCP(pr1);
		assertParent("IRodinDB", cp, db, null);
	}

	/**
	 * Traverse the entire tree and verify every element.
	 */
	@Test
	public void testTraverse() throws Exception {
		final IPRRoot prFile1 = getPRRoot(rp1, "m1");
		final IPRProof pr1_1 = prFile1.getProof(PO1);

		final IPRRoot prFile2 = getPRRoot(rp2, "m2");
		final IPRProof pr2_1 = prFile2.getProof(PO1);
		final IPRProof pr2_2 = prFile2.getProof(PO2);

		final IPRRoot prFile3 = getPRRoot(rp2, "m3");

		final IPRRoot prFile4 = getPRRoot(rp3, "m4");
		final IPRProof pr4_2 = prFile4.getProof(PO2);

		final IPRRoot prFile5 = getPRRoot(rp3, "m5");
		final IPRProof pr5_3 = prFile5.getProof(PO3);
		final IPRProof pr5_4 = prFile5.getProof(PO4);
		
		final ProofPurgerContentProvider cp = initCP(pr1_1, pr2_1, pr2_2,
				pr4_2, pr5_3, pr5_4);

		assertChildren("IRodinDB", cp, db, rp1, rp2, rp3);

		assertChildren("IRodinProject", cp, rp1, prFile1);
		assertChildren("IRodinProject", cp, rp2, prFile2);
		assertChildren("IRodinProject", cp, rp3, prFile4, prFile5);

		assertChildren("IPRRoot", cp, prFile1, pr1_1);
		assertChildren("IPRRoot", cp, prFile2, pr2_1, pr2_2);
		assertChildren("IPRRoot", cp, prFile3);
		assertChildren("IPRRoot", cp, prFile4, pr4_2);
		assertChildren("IPRRoot", cp, prFile5, pr5_3, pr5_4);
	}
}
