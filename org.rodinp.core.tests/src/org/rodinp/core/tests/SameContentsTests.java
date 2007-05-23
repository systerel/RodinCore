/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;

/**
 * Unit tests for IInternalParent.hasSameContents().
 * 
 * @author Laurent Voisin
 */
public class SameContentsTests extends ModifyingResourceTests {

	public static void assertSameContents(IInternalParent left,
			IInternalParent right, boolean expected) throws RodinDBException {

		assertEquals(expected, left.hasSameContents(right));
		assertEquals(expected, right.hasSameContents(left));
	}
	
	public SameContentsTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		createRodinProject("P");
		createRodinProject("P2");
	}
	
	public void tearDown() throws Exception {
		deleteProject("P");
		deleteProject("P2");
		super.tearDown();
	}

	/**
	 * Ensures that element name and types are taken into account when comparing
	 * files.
	 */
	public void testFileNameType() throws Exception {
		IRodinFile rf1, rf2;
		
		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P2/x.test");
		assertSameContents(rf1, rf2, true);

		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P/x.test2");
		assertSameContents(rf1, rf2, false);
		
		rf1 = getRodinFile("P/x.test");
		rf2 = getRodinFile("P/y.test");
		assertSameContents(rf1, rf2, false);
	}
	
	/**
	 * Ensures that element existence is taken into account when comparing
	 * files.
	 */
	public void testFileExistence() throws Exception {
		final IRodinFile rf1 = getRodinFile("P/x.test");
		final IRodinFile rf2 = getRodinFile("P2/x.test");
		assertSameContents(rf1, rf2, true);
		
		rf1.create(false, null);
		assertSameContents(rf1, rf2, false);

		rf2.create(false, null);
		assertSameContents(rf1, rf2, true);
	}
	
	/**
	 * Ensures that file attributes are taken into account when comparing
	 * files.
	 */
	public void testFileAttributes() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");

		rf1.setAttributeValue(fBool, true, null);
		assertSameContents(rf1, rf2, false);
		
		rf2.setAttributeValue(fBool, true, null);
		assertSameContents(rf1, rf2, true);
		
		rf2.setAttributeValue(fBool, false, null);
		assertSameContents(rf1, rf2, false);
	}
	
	/**
	 * Ensures that file children are taken into account when comparing
	 * files.
	 */
	public void testFileChildren() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P2/x.test");

		createNEPositive(rf1, "foo", null);
		assertSameContents(rf1, rf2, false);
		
		final NamedElement foo2 = createNEPositive(rf2, "foo", null);
		assertSameContents(rf1, rf2, true);
		
		createNEPositive(rf1, "bar", null);
		final NamedElement bar2 = createNEPositive(rf2, "bar", foo2);
		assertSameContents(rf1, rf2, false);
		
		foo2.move(rf2, bar2, null, false, null);
		assertSameContents(rf1, rf2, true);
	}
	
	/**
	 * Ensures that element name and types are taken into account when comparing
	 * internal elements.
	 */
	public void testIntNameType() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		IInternalElement ne1, ne2;
		
		ne1 = rf1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = rf2.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		assertSameContents(ne1, ne2, true);

		ne1 = rf1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = rf2.getInternalElement(NamedElement2.ELEMENT_TYPE, "foo");
		assertSameContents(ne1, ne2, false);
		
		ne1 = rf1.getInternalElement(NamedElement.ELEMENT_TYPE, "foo");
		ne2 = rf2.getInternalElement(NamedElement.ELEMENT_TYPE, "bar");
		assertSameContents(ne1, ne2, false);
	}
	
	/**
	 * Ensures that element existence is taken into account when comparing
	 * internal elements.
	 */
	public void testIntExistence() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		final NamedElement ne1 = getNamedElement(rf1, "foo");
		final NamedElement ne2 = getNamedElement(rf2, "foo");

		assertSameContents(ne1, ne2, true);
		
		ne1.create(null, null);
		assertSameContents(ne1, ne2, false);

		ne2.create(null, null);
		assertSameContents(ne1, ne2, true);
	}
	
	/**
	 * Ensures that file attributes are taken into account when comparing
	 * internal elements.
	 */
	public void testIntAttributes() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		final NamedElement ne1 = createNEPositive(rf1, "foo", null);
		final NamedElement ne2 = createNEPositive(rf2, "foo", null);

		ne1.setAttributeValue(fBool, true, null);
		assertSameContents(ne1, ne2, false);
		
		ne2.setAttributeValue(fBool, true, null);
		assertSameContents(ne1, ne2, true);
		
		ne2.setAttributeValue(fBool, false, null);
		assertSameContents(ne1, ne2, false);
	}
	
	/**
	 * Ensures that file children are taken into account when comparing
	 * internal elements.
	 */
	public void testIntChildren() throws Exception {
		final IRodinFile rf1 = createRodinFile("P/x.test");
		final IRodinFile rf2 = createRodinFile("P/y.test");
		final NamedElement ne1 = createNEPositive(rf1, "root", null);
		final NamedElement ne2 = createNEPositive(rf2, "root", null);

		createNEPositive(ne1, "foo", null);
		assertSameContents(ne1, ne2, false);
		
		final NamedElement foo2 = createNEPositive(ne2, "foo", null);
		assertSameContents(ne1, ne2, true);
		
		createNEPositive(ne1, "bar", null);
		final NamedElement bar2 = createNEPositive(ne2, "bar", foo2);
		assertSameContents(ne1, ne2, false);
		
		foo2.move(ne2, bar2, null, false, null);
		assertSameContents(ne1, ne2, true);
	}
	
}
