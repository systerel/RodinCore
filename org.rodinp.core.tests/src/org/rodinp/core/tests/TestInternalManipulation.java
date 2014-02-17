/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed test on pseudo-attribute "contents"
 *     Systerel - added tests for method getNextSibling()
 *     Systerel - separation of file and root element
 *     Systerel - added creation of new internal element child
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.rodinp.core.IRodinDBStatusConstants.ELEMENT_DOES_NOT_EXIST;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_CHILD_TYPE;
import static org.rodinp.core.IRodinDBStatusConstants.INVALID_SIBLING;
import static org.rodinp.core.IRodinDBStatusConstants.READ_ONLY;
import static org.rodinp.core.tests.version.db.VersionAttributes.StringAttr;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinDBStatusConstants;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.basis.RodinTestRoot;
import org.rodinp.core.tests.basis.RodinTestRoot2;
import org.rodinp.core.tests.basis.UbiquitousElement;

public class TestInternalManipulation extends ModifyingResourceTests {

	private static final org.rodinp.core.IAttributeType.String UBIQUITOUS_ATTR_TYPE = RodinCore
			.getStringAttrType(PLUGIN_ID + ".ubiqAttr");

	public TestInternalManipulation() {
		super(PLUGIN_ID + ".TestInternalManipulation");
	}

	private IRodinProject rodinProject;
	private IRodinFile rodinFile;
	private RodinTestRoot root;
	private RodinTestRoot2 root2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		rodinFile = createRodinFile("P/x.test");
		root = (RodinTestRoot) rodinFile.getRoot();
		root2 = (RodinTestRoot2) createRodinFile("P/y.test2").getRoot();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		root2.getResource().delete(true, null);
		rodinFile.getResource().delete(true, null);
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}

	private void checkChildren(IParent parent,
			IInternalElement... expChildren) throws RodinDBException {

		final IRodinElement[] children = parent.getChildren();
		assertEquals(expChildren.length, children.length);
		for (int i = 0; i < children.length; ++i) {
			assertEquals(expChildren[i], children[i]);
		}
		assertEquals(expChildren.length != 0, parent.hasChildren());
	}
	
	private void checkEmpty(IInternalElement... children)
			throws RodinDBException {
		for (IInternalElement child : children) {
			assertFalse(child.hasChildren());
		}
	}
	
	private void checkEmptyChildren(IParent parent,
			IInternalElement... expChildren) throws RodinDBException {
		checkChildren(parent, expChildren);
		checkEmpty(expChildren);
	}

	// Test creation of handle that would violate parent-child relationships
	public void testGetInternalElementError() {
		final NamedElement e1 = getNamedElement(root, "e1");
		try {
			e1.getInternalElement(NamedElement2.ELEMENT_TYPE, "e11");
			fail("shoud have failed");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	// Test creation of some top-level internal elements
	public void testCreateInternalElement() throws Exception {
		// File exists and is empty
		assertExists("File should exist", rodinFile);
		checkEmptyChildren(rodinFile, root);

		IInternalElement e1 = createNEPositive(root, "e1", null);
		checkEmptyChildren(root, e1);

		IInternalElement e3 = createNEPositive(root, "e3", null);
		checkEmptyChildren(root, e1, e3);

		IInternalElement e2 = createNEPositive(root, "e2", e3);
		checkEmptyChildren(root, e1, e2, e3);

		IInternalElement e0 = createNEPositive(root, "e0", e1);
		checkEmptyChildren(root, e0, e1, e2, e3);

		rodinFile.save(null, true);
		rodinFile.close();
		
		//showFile(rodinFile.getResource());
		
		assertExists("Internal element should exist", e0);
		assertExists("Internal element should exist", e1);
		assertExists("Internal element should exist", e2);
		assertExists("Internal element should exist", e3);
		checkEmptyChildren(root, e0, e1, e2, e3);
		
		// Create sub-elements
		IInternalElement e12 = createNEPositive(e1, "e12", null);
		assertExists("Internal element should exist", e12);
		checkChildren(root, e0, e1, e2, e3);
		checkEmpty(e0, e2, e3);
		checkEmptyChildren(e1, e12);
		
		IInternalElement e11 = createNEPositive(e1, "e11", e12);
		assertExists("Internal element should exist", e11);
		checkChildren(root, e0, e1, e2, e3);
		checkEmpty(e0, e2, e3);
		checkEmptyChildren(e1, e11, e12);

		assertEquals("Unexpected file contents",
				"x.test [in P]\n"
				+ "  x[org.rodinp.core.tests.test]\n"
				+ "    e0[org.rodinp.core.tests.namedElement]\n"
				+ "    e1[org.rodinp.core.tests.namedElement]\n"
				+ "      e11[org.rodinp.core.tests.namedElement]\n"
				+ "      e12[org.rodinp.core.tests.namedElement]\n"
				+ "    e2[org.rodinp.core.tests.namedElement]\n"
				+ "    e3[org.rodinp.core.tests.namedElement]",
				((RodinElement) rodinFile).toDebugString());
		
		// rodinFile.save(null, true);
		// showFile(rodinFile.getResource());
		
		// Cleanup
		rodinFile.getResource().delete(true, null);
		assertNotExists("File should not exist", rodinFile);
		createRodinFile("P/x.test");
		assertNotExists("Internal element should not exist", e0);
		assertNotExists("Internal element should not exist", e1);
		assertNotExists("Internal element should not exist", e11);
		assertNotExists("Internal element should not exist", e12);
		assertNotExists("Internal element should not exist", e2);
		assertNotExists("Internal element should not exist", e3);
		checkEmptyChildren(rodinFile, root);
	}

	/**
	 * Ensures that attempting to create a new internal element with
	 * the same type and name as an existing one fails.
	 */
	public void testCreateDuplicate() throws Exception {
		final NamedElement e1 = createNEPositive(root, "foo", null);
		checkEmptyChildren(root, e1);

		// Attempt to create a duplicate element
		createNENegative(root, "foo", null,
				IRodinDBStatusConstants.NAME_COLLISION);
		
		// File has not changed
		checkEmptyChildren(root, e1);
	}

	// Test creation of a Rodin file and an internal element as an atomic action
	public void testCreateInternalElementAtomic() throws Exception {
		
		// Start with a fresh file
		final String fileName = "titi.test";
		final IRodinFile rf = (IRodinFile) rodinProject
				.getRodinFile(fileName);
		final RodinTestRoot r = (RodinTestRoot) rf.getRoot();
		assertNotExists("Target file should not exist", rf);
		
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					rf.create(false, null);
					final IInternalElement e1 = createNEPositive(r, "e1", null);
					assertExists("New file should exist", rf);
					checkEmptyChildren(r, e1);
				}
			}, null);

			assertExists("New file should exist", rf);

			final IInternalElement e1 = r.getInternalElement(
					NamedElement.ELEMENT_TYPE, "e1");
			checkEmptyChildren(r, e1);
		} finally {
			// Ensure the new Rodin file is destroyed.
			rf.delete(true, null);
		}
	}

	// Test modification of the in-memory copy of a Rodin file and then
	// reverting the changes
	public void testCreateInternalElementRevert() throws Exception {
		
		// Start with a fresh file
		final String fileName = "titi.test";
		final IRodinFile rf = rodinProject.getRodinFile(fileName);
		assertNotExists("Target file should not exist", rf);
		final RodinTestRoot r = (RodinTestRoot) rf.getRoot();
		
		try {
			rf.create(false, null);
			IInternalElement e1 = createNEPositive(r, "e1", null);
			assertExists("Internal element should exist", e1);
			checkEmptyChildren(r, e1);

			// Revert the changes
			rf.makeConsistent(null);
			assertNotExists("Internal element should not exist", e1);
			checkEmptyChildren(r);
		} finally {
			// Ensure the new Rodin file is destroyed.
			rf.delete(true, null);
		}
	}

	// Test deletion of an internal element bypassing the Rodin database
	public void testDeleteInternalElementBypass() throws Exception {

		// First create an internal element
		final IInternalElement e1 = createNEPositive(root, "e1", null);
		rodinFile.save(null, false);
		checkEmptyChildren(root, e1);

		startDeltas();
		
		// Then, inside a workable, empty the Rodin file bypassing the database
		// The database is not updated!
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IFile file = rodinFile.getResource();
				file.setContents(new ByteArrayInputStream(emptyBytes),
						false, true, null);
				
				// Inside the runnable, the Rodin database doesn't get the
				// resource change delta, so the element is still considered to
				// exist.
				assertExists("Internal element should exist", e1);
				checkEmptyChildren(root, e1);
			}
		}, null);

		// Once the workable is finished, the database gets the resource delta
		assertDeltas("Should report an unknown change to the file",
				"P[*]: {CHILDREN}\n" + 
				"	x.test[*]: {CONTENT}"
		);

		checkEmptyChildren(root);
		assertNotExists("Internal element should not exist", e1);
	}

	
	@SuppressWarnings("unused")
	private void showFile(IFile file) throws Exception {
		InputStream contents = file.getContents();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(contents, "UTF-8")
		);
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();
	}

	/*
	 * Ensures that creating again an existing file empties it.
	 */
	public void testRecreateFile() throws Exception {
		checkEmptyChildren(root);
		IInternalElement ne = createNEPositive(root, "foo", null);
		rodinFile.save(null, false);
		checkEmptyChildren(root, ne);
		
		// Recreate the file
		rodinFile.create(true, null);
		checkEmptyChildren(root);
		createNEPositive(root, ne.getElementName(), null);
		checkEmptyChildren(root, ne);
	}
	
	/*
	 * Ensures that creating again an existing file empties it, even when done
	 * inside a runnable.
	 */
	public void testRecreateFileInRunnable() throws Exception {
		checkEmptyChildren(root);
		final IInternalElement ne = createNEPositive(root, "foo", null);
		rodinFile.save(null, false);
		checkEmptyChildren(root, ne);
		
		// Recreate the file
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				rodinFile.create(true, null);
				checkEmptyChildren(root);
				createNEPositive(root, ne.getElementName(), null);
				checkEmptyChildren(root, ne);
			}
		};
		getWorkspace().run(runnable, null);
		checkEmptyChildren(root, ne);
	}
	
	public void testGetChildrenOfType() throws Exception {
		IRodinElement[] children;
		
		checkEmptyChildren(root);
		children = root.getChildrenOfType(NamedElement.ELEMENT_TYPE);
		assertTrue(children instanceof NamedElement[]);
		assertEquals("Array should be empty", 0, children.length);
		
		final NamedElement ne = createNEPositive(root, "foo", null);
		children = root.getChildrenOfType(NamedElement.ELEMENT_TYPE);
		assertTrue(children instanceof NamedElement[]);
		assertEquals("Array should contain one element", 1, children.length);
		assertEquals("Wrong element", ne, children[0]);
	}

	/**
	 * Ensures that a similar element for a root element is constructed
	 * correctly.
	 */
	public void testSimilarRoot() {
		final IRodinFile rf1 = getRodinFile("P/X.test");
		final RodinTestRoot r1 = (RodinTestRoot) rf1.getRoot();
		final IRodinFile rf2 = getRodinFile("P/Y.test");
		final RodinTestRoot r2 = (RodinTestRoot) rf2.getRoot();
		assertEquals(r1, r1.getSimilarElement(rf1));
		assertEquals(r2, r1.getSimilarElement(rf2));
	}

	/**
	 * Ensures that a similar element for an internal element is constructed
	 * correctly.
	 */
	public void testSimilarInt() {
		final IRodinFile rf1 = getRodinFile("P/X.test");
		final RodinTestRoot r1 = (RodinTestRoot) rf1.getRoot();
		final NamedElement ie1 = getNamedElement(r1, "foo");

		final IRodinFile rf2 = getRodinFile("P/Y.test");
		final RodinTestRoot r2 = (RodinTestRoot) rf2.getRoot();
		final NamedElement ie2 = getNamedElement(r2, ie1.getElementName());
		assertEquals(ie1, ie1.getSimilarElement(rf1));
		assertEquals(ie2, ie1.getSimilarElement(rf2));
	}

	/**
	 * Ensures that a similar element for an internal element can fail if
	 * violating parent-child relationships.
	 */
	public void testSimilarIntError() {
		final IRodinFile rf1 = getRodinFile("P/X.test");
		final RodinTestRoot r1 = (RodinTestRoot) rf1.getRoot();
		final NamedElement ie1 = getNamedElement(r1, "foo");
		final IRodinFile rf2 = getRodinFile("P/Y.test2");

		try {
			ie1.getSimilarElement(rf2);
			fail("shoud have failed");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that trying to access to the next sibling of an inexistent
	 * element throws the appropriate exception.
	 */
	public void testNextSiblingInexistent() throws Exception {
		final NamedElement ne = getNamedElement(root, "foo");
		try {
			ne.getNextSibling();
			fail("should have raised an exception");
		} catch (RodinDBException e) {
			IRodinDBStatus st = e.getRodinDBStatus();
			assertTrue(st.isDoesNotExist());
			assertEquals(Arrays.asList(ne), Arrays.asList(st.getElements()));
		}
	}

	/**
	 * Ensures that the next sibling of an internal element is computed
	 * appropriately, when there is no other sibling.
	 */
	public void testNextSibling_1() throws Exception {
		final NamedElement ne = createNEPositive(root, "ne", null);
		assertEquals(null, ne.getNextSibling());
	}

	/**
	 * Ensures that the next sibling of an internal element is computed
	 * appropriately, when there is one other sibling.
	 */
	public void testNextSibling_2() throws Exception {
		final NamedElement ne1 = createNEPositive(root, "ne1", null);
		final NamedElement ne2 = createNEPositive(root, "ne2", null);
		assertEquals(ne2, ne1.getNextSibling());
		assertEquals(null, ne2.getNextSibling());
	}

	/**
	 * Ensures that the next sibling of an internal element is computed
	 * appropriately, when there are two other siblings.
	 */
	public void testNextSibling_3() throws Exception {
		final NamedElement ne1 = createNEPositive(root, "ne1", null);
		final NamedElement ne2 = createNEPositive(root, "ne2", null);
		final NamedElement ne3 = createNEPositive(root, "ne3", null);
		assertEquals(ne2, ne1.getNextSibling());
		assertEquals(ne3, ne2.getNextSibling());
		assertEquals(null, ne3.getNextSibling());
	}

	/**
	 * Ensures that creating a new child of a root element works as advertised
	 * in nominal cases.
	 */
	public void testCreateNewChildRoot() throws Exception {
		checkEmpty(root);
		final NamedElement e1 = createNewNEPositive(root, null);
		checkEmptyChildren(root, e1);
		final NamedElement e3 = createNewNEPositive(root, null);
		checkEmptyChildren(root, e1, e3);
		final NamedElement e0 = createNewNEPositive(root, e1);
		checkEmptyChildren(root, e0, e1, e3);
		final NamedElement e2 = createNewNEPositive(root, e3);
		checkEmptyChildren(root, e0, e1, e2, e3);
	}

	/**
	 * Ensures that creating a new child of a non root element works as advertised
	 * in nominal cases.
	 */
	public void testCreateNewChildNonRoot() throws Exception {
		checkEmpty(root);
		final NamedElement e1 = createNewNEPositive(root, null);
		final NamedElement e11 = createNewNEPositive(e1, null);
		checkEmptyChildren(e1, e11);
	}

	/**
	 * Ensures that creating a new child of a root element works as advertised
	 * in nominal cases.
	 */
	public void testCreateNewChildDelta() throws Exception {
		startDeltas();
		final NamedElement e1 = createNewNEPositive(root, null);
		final String n1 = e1.getElementName();
		assertDeltas(
				"Unexpected delta after creation",
				"P[*]: {CHILDREN}\n" + 
				"	x.test[*]: {CHILDREN}\n" + 
				"		x[org.rodinp.core.tests.test][*]: {CHILDREN}\n" + 
				"			" + n1 + "[org.rodinp.core.tests.namedElement][+]: {}"
		);
			
		clearDeltas();
		final NamedElement e11 = createNewNEPositive(e1, null);
		final String n11 = e11.getElementName();
		assertDeltas(
				"Unexpected delta after creation",
				"P[*]: {CHILDREN}\n" + 
				"	x.test[*]: {CHILDREN}\n" + 
				"		x[org.rodinp.core.tests.test][*]: {CHILDREN}\n" + 
				"			" + n1 + "[org.rodinp.core.tests.namedElement][*]: {CHILDREN}\n" +
				"				" + n11 + "[org.rodinp.core.tests.namedElement][+]: {}"
		);
	}

	public void testCreateNewChildErrors() throws Exception {
		final IInternalElementType<NamedElement> type = NamedElement.ELEMENT_TYPE;

		final IRodinFile rf = getRodinFile(rodinProject, "inexistent.test");
		final IInternalElement ir = rf.getRoot();
		assertCreateNewChildError(ir, type, null, ELEMENT_DOES_NOT_EXIST, ir);

		final InternalElement ro = root.getSnapshot();
		assertCreateNewChildError(ro, type, null, READ_ONLY, ro);

		assertCreateNewChildError(root, type, root, INVALID_SIBLING, root);

		final NamedElement sibling = root
				.getInternalElement(type, "inexistent");
		assertCreateNewChildError(root, type, sibling, ELEMENT_DOES_NOT_EXIST,
				sibling);
	}

	/**
	 * Ensures that an element which type is invalid for a child of a root
	 * element cannot be created.
	 */
	public void testCreateNewChildRootInvalidType() throws RodinDBException {
		assertCreateNewChildError(root2, NamedElement.ELEMENT_TYPE, null,
				INVALID_CHILD_TYPE, root2);
	}

	/**
	 * Ensures that an element which type is invalid for a child of a non-root
	 * parent element cannot be created.
	 */
	public void testCreateNewChildNonRootInvalidType() throws RodinDBException {
		final NamedElement named = createNEPositive(root, "elem1", null);
		assertCreateNewChildError(named, NamedElement2.ELEMENT_TYPE, null,
				INVALID_CHILD_TYPE, named);
	}

	private void assertCreateNewChildError(IInternalElement parent,
			IInternalElementType<?> type, IInternalElement nextSibling,
			int errorCode, IInternalElement... elements) {
		try {
			parent.createChild(type, nextSibling, null);
			fail("Should have raised an error");
		} catch (RodinDBException e) {
			final IRodinDBStatus status = e.getRodinDBStatus();
			assertEquals(IStatus.ERROR, status.getSeverity());
			assertEquals(errorCode, status.getCode());
			assertEquals(Arrays.asList(elements), //
					Arrays.asList(status.getElements()));
		}
	}

	/**
	 * Ensures that no exception is thrown when creating a ubiquitous child.
	 */
	@Test
	public void testAddUbiquitousChild() throws Exception {
		final NamedElement ne = createNEPositive(root, "ne", null);
		final UbiquitousElement ubiqElem = ne.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		assertTrue(ubiqElem.getElementType().isUbiquitous());
	}
	
	/**
	 * Ensures that no exception is thrown when setting a ubiquitous attribute.
	 */
	@Test
	public void testAddUbiquitousAttribute() throws Exception {
		final NamedElement ne = createNEPositive(root, "ne", null);
		// check no exception is thrown:
		ne.setAttributeValue(UBIQUITOUS_ATTR_TYPE, "ubiq", null);
		assertTrue(UBIQUITOUS_ATTR_TYPE.isUbiquitous());
	}

	/**
	 * Ensures that element type API methods return expected values when
	 * handling ubiquitous items.
	 */
	@Test
	public void testAPIGettersForUbiquitous() throws Exception {
		final NamedElement ne = createNEPositive(root, "ne", null);
		final IInternalElementType<?> neElemType = ne.getElementType();
		assertTrue(neElemType.canParent(UbiquitousElement.ELEMENT_TYPE));
		assertEquals(0, UbiquitousElement.ELEMENT_TYPE.getParentTypes().length);
		assertFalse(Arrays.asList(
				UbiquitousElement.ELEMENT_TYPE.getChildTypes()).contains(
				UbiquitousElement.ELEMENT_TYPE));
		assertFalse(Arrays.asList(
				UbiquitousElement.ELEMENT_TYPE.getAttributeTypes()).contains(
				UBIQUITOUS_ATTR_TYPE));
		assertTrue(neElemType.canCarry(UBIQUITOUS_ATTR_TYPE));
		assertEquals(0, UBIQUITOUS_ATTR_TYPE.getElementTypes().length);
		assertTrue(UBIQUITOUS_ATTR_TYPE.isAttributeOf(neElemType));
	}

	/**
	 * Ensures that no exception is thrown when creating child of an ubiquitous
	 * element (i.e. also checks that an ubiquitous element can parent a named
	 * element).
	 */
	@Test
	public void testAddChildToUbiquitous() throws Exception {
		final UbiquitousElement ubiqElem = root.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		createNEPositive(ubiqElem, "ne", null);
	}

	/**
	 * Ensures that no exception is thrown when creating child of an ubiquitous
	 * element (i.e. also checks that an ubiquitous element can parent a named
	 * element).
	 */
	@Test
	public void testAddAttributeToUbiquitous() throws Exception {
		final UbiquitousElement ubiqElem = root.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		ubiqElem.setAttributeValue(StringAttr, "V", null);
		assertTrue(ubiqElem.hasAttribute(StringAttr));
		assertEquals("V", ubiqElem.getAttributeValue(StringAttr));
	}

	@Test
	public void testAddUbiquitousChildToUbiquitous() throws Exception {
		final UbiquitousElement ubiqElem = root.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		final UbiquitousElement ubiqChild = ubiqElem.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		assertExists("Could not create ubiquitous child in ubiquitous parent",
				ubiqChild);
		assertEquals(ubiqElem, ubiqChild.getParent());
	}
	

	@Test
	public void testAddUbiquitousAttributeToUbiquitous() throws Exception {
		final UbiquitousElement ubiqElem = root.createChild(
				UbiquitousElement.ELEMENT_TYPE, null, null);
		ubiqElem.setAttributeValue(UBIQUITOUS_ATTR_TYPE, "U", null);
		assertTrue(ubiqElem.hasAttribute(UBIQUITOUS_ATTR_TYPE));
		assertEquals("U", ubiqElem.getAttributeValue(UBIQUITOUS_ATTR_TYPE));
	}
	
}
