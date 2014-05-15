/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.basis.RodinTestRoot;

/**
 * Unit tests for: IInternalParent.hasSameContents().
 * IInternalParent.hasSameAttributes(). IInternalParent.hasSameChildren().
 * 
 * @author Laurent Voisin
 */
public class SameContentsTests extends ModifyingResourceTests {

	public static void assertSameContents(IInternalElement left,
			IInternalElement right, boolean sameContents,
			boolean sameAttributes, boolean sameChildren)
			throws RodinDBException {

		assertEquals(sameContents, left.hasSameContents(right));
		assertEquals(sameContents, right.hasSameContents(left));

		assertEquals(sameAttributes, left.hasSameAttributes(right));
		assertEquals(sameAttributes, right.hasSameAttributes(left));

		assertEquals(sameChildren, left.hasSameChildren(right));
		assertEquals(sameChildren, right.hasSameChildren(left));
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		createRodinProject("P2");
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		deleteProject("P2");
		super.tearDown();
	}

	/**
	 * Ensures that element name and types are taken into account when comparing
	 * files.
	 */
	@Test
	public void testFileNameType() throws Exception {
		IRodinFile rf1, rf2;
		IInternalElement root1, root2;
		
		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P2/x.test");
		root1 = rf1.getRoot();
		root2 = rf2.getRoot();
		assertSameContents(root1, root2, true, true, true);

		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P/x.test2");
		root1 = rf1.getRoot();
		root2 = rf2.getRoot();
		assertSameContents(root1, root2, false, true, true);

		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P/y.test");
		root1 = rf1.getRoot();
		root2 = rf2.getRoot();
		assertSameContents(root1, root2, false, true, true);
	}

	/**
	 * Ensures that element existence is taken into account when comparing
	 * files.
	 */
	@Test
	public void testFileExistence() throws Exception {
		final IRodinFile rf1 = getRodinFile("P/x.test");
		final IRodinFile rf2 = getRodinFile("P2/x.test");
		final RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		final RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		assertSameContents(root1, root2, true, true, true);

		rf1.create(false, null);
		assertSameContents(root1, root2, false, false, false);

		rf2.create(false, null);
		assertSameContents(root1, root2, true, true, true);
	}

	/**
	 * Ensures that file attributes are taken into account when comparing files.
	 */
	@Test
	public void testFileAttributes() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();

		root1.setAttributeValue(fBool, true, null);
		assertSameContents(root1, root2, false, false, true);

		root2.setAttributeValue(fBool, true, null);
		assertSameContents(root1, root2, true, true, true);

		root2.setAttributeValue(fBool, false, null);
		assertSameContents(root1, root2, false, false, true);

		root2.setAttributeValue(fBool, true, null);
		createNEPositive(root2, "foo", null);
		assertSameContents(root1, root2, false, true, false);
	}

	/**
	 * Ensures that file children are taken into account when comparing files.
	 */
	@Test
	public void testFileChildren() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();

		createNEPositive(root1, "foo", null);
		assertSameContents(root1, root2, false, true, false);

		final NamedElement foo2 = createNEPositive(root2, "foo", null);
		assertSameContents(root1, root2, true, true, true);

		createNEPositive(root1, "bar", null);
		final NamedElement bar2 = createNEPositive(root2, "bar", foo2);
		assertSameContents(root1, root2, false, true, false);

		foo2.move(root2, bar2, null, false, null);
		assertSameContents(root1, root2, true, true, true);
	}

	/**
	 * Ensures that element name and types are taken into account when comparing
	 * internal elements.
	 */
	@Test
	public void testIntNameType() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		IInternalElement ne1, ne2;
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();

		ne1 = root1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = root2.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		assertSameContents(ne1, ne2, true, true, true);

		ne1 = root1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = root2.getInternalElement(NamedElement2.ELEMENT_TYPE, "foo");
		assertSameContents(ne1, ne2, false, true, true);

		ne1 = root1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = root2.getInternalElement(NamedElement.ELEMENT_TYPE, "bar");
		assertSameContents(ne1, ne2, false, true, true);
	}

	/**
	 * Ensures that element existence is taken into account when comparing
	 * internal elements.
	 */
	@Test
	public void testIntExistence() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		final NamedElement ne1 = getNamedElement(root1, "foo");
		final NamedElement ne2 = getNamedElement(root2, "foo");

		assertSameContents(ne1, ne2, true, true, true);

		ne1.create(null, null);
		assertSameContents(ne1, ne2, false, false, false);

		ne2.create(null, null);
		assertSameContents(ne1, ne2, true, true, true);
	}

	/**
	 * Ensures that file attributes are taken into account when comparing
	 * internal elements.
	 */
	@Test
	public void testIntAttributes() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		final NamedElement ne1 = createNEPositive(root1, "foo", null);
		final NamedElement ne2 = createNEPositive(root2, "foo", null);

		ne1.setAttributeValue(fBool, true, null);
		assertSameContents(ne1, ne2, false, false, true);

		ne2.setAttributeValue(fBool, true, null);
		assertSameContents(ne1, ne2, true, true, true);

		ne2.setAttributeValue(fBool, false, null);
		assertSameContents(ne1, ne2, false, false, true);

		ne2.setAttributeValue(fBool, true, null);
		createNEPositive(ne2, "foo", null);
		assertSameContents(ne1, ne2, false, true, false);
	}

	/**
	 * Ensures that file children are taken into account when comparing internal
	 * elements.
	 */
	@Test
	public void testIntChildren() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();
		final NamedElement ne1 = createNEPositive(root1, "root", null);
		final NamedElement ne2 = createNEPositive(root2, "root", null);

		createNEPositive(ne1, "foo", null);
		assertSameContents(ne1, ne2, false, true, false);

		final NamedElement foo2 = createNEPositive(ne2, "foo", null);
		assertSameContents(ne1, ne2, true, true, true);

		createNEPositive(ne1, "bar", null);
		final NamedElement bar2 = createNEPositive(ne2, "bar", foo2);
		assertSameContents(ne1, ne2, false, true, false);

		foo2.move(ne2, bar2, null, false, null);
		assertSameContents(ne1, ne2, true, true, true);
	}

	/**
	 * Ensures that children attributes are taken into account when comparing
	 * elements.
	 */
	@Test
	public void testChildrenAttributes() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();

		final NamedElement foo1 = createNEPositive(root1, "foo", null);
		final NamedElement foo2 = createNEPositive(root2, "foo", null);
		foo1.setAttributeValue(fBool, true, null);
		foo2.setAttributeValue(fBool, true, null);
		assertSameContents(root1, root2, true, true, true);

		foo2.setAttributeValue(fBool, false, null);
		assertSameContents(root1, root2, false, true, false);
	}

	/**
	 * Ensures that grand-children are taken into account when comparing
	 * elements.
	 */
	@Test
	public void testGrandChildren() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");
		RodinTestRoot root1 = (RodinTestRoot) rf1.getRoot();
		RodinTestRoot root2 = (RodinTestRoot) rf2.getRoot();

		final NamedElement foo1 = createNEPositive(root1, "foo", null);
		createNEPositive(root2, "foo", null);
		assertSameContents(root1, root2, true, true, true);

		createNEPositive(foo1, "bar", null);
		assertSameContents(root1, root2, false, true, false);
	}

}
