/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.location;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.location.RodinLocationTests.TEST_ATTR_TYPE;
import static org.rodinp.core.tests.util.IndexTestsUtil.createNamedElement;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.location.IRodinLocation;
import org.rodinp.core.tests.AbstractRodinDBTests;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.location.AttributeLocation;
import org.rodinp.internal.core.location.AttributeSubstringLocation;
import org.rodinp.internal.core.location.InternalLocation;
import org.rodinp.internal.core.location.RodinLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class LocationInclusionTests extends AbstractRodinDBTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement elt1F1;
	private static NamedElement elt2F1;
	private static NamedElement elt1F2;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = createRodinFile(project, "inclusion1.test");
		file2 = createRodinFile(project, "inclusion2.test");
		elt1F1 = createNamedElement(file1, "internalName1");
		elt2F1 = createNamedElement(file1, "internalName2");
		elt1F2 = createNamedElement(file2, "internalName1");

	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	@Test
	public void testRLsameElement() throws Exception {
		final IRodinLocation loc1 = new RodinLocation(project);
		final IRodinLocation loc2 = new RodinLocation(project);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("an element should be included in itself", included);
	}

	@Test
	public void testRLFileInProject() throws Exception {
		final IRodinLocation loc1 = new RodinLocation(file1);
		final IRodinLocation loc2 = new RodinLocation(project);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("a file should be included in its project", included);
	}

	@Test
	public void testRLProjectInFile() throws Exception {
		final IRodinLocation loc1 = new RodinLocation(project);
		final IRodinLocation loc2 = new RodinLocation(file1);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse("a project should not be included in a file", included);
	}

	@Test
	public void testRLeltNotInFile() throws Exception {
		final IRodinLocation loc1 = new RodinLocation(file1);
		final IRodinLocation loc2 = new RodinLocation(elt1F2);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse("element is not in file", included);
	}

	@Test
	public void testILDiffElt() throws Exception {
		final IRodinLocation loc1 = new InternalLocation(elt1F1);
		final IRodinLocation loc2 = new InternalLocation(elt2F1);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse("elements are different", included);
	}

	@Test
	public void testAttLocInIntLoc() throws Exception {
		final IRodinLocation loc1 = new AttributeLocation(elt1F1, TEST_ATTR_TYPE);
		final IRodinLocation loc2 = new InternalLocation(elt1F1);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("an attribute should be included in its element", included);
	}

	@Test
	public void testSubsLocInAttLoc() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 1, 5);
		final IRodinLocation loc2 = new AttributeLocation(elt1F1, TEST_ATTR_TYPE);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue(
				"an attribute substring should be included in its attribute",
				included);
	}

	@Test
	public void testSubsLocInProject() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 0, 5);
		final IRodinLocation loc2 = new RodinLocation(project);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("an attribute substring should be included in its project",
				included);
	}

	@Test
	public void testSameSubsLoc() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 1, 5);
		final IRodinLocation loc2 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 1, 5);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("an attribute substring should be included in itself",
				included);
	}

	@Test
	public void testSubsLocInSubsLoc() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 3, 5);
		final IRodinLocation loc2 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 1, 15);

		final boolean included = loc1.isIncludedIn(loc2);

		assertTrue("an attribute substring should be included in a wider one",
				included);
	}

	@Test
	public void testSubsLocNotInSubsLoc() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 3, 15);
		final IRodinLocation loc2 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 8, 11);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse(
				"an attribute substring should not be included in an inner one",
				included);
	}

	@Test
	public void testSubsLocApart() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 3, 5);
		final IRodinLocation loc2 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 8, 11);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse(
				"an attribute substring should not be included in a separate one",
				included);
	}
	@Test
	public void testSubsLocOverlap() throws Exception {
		final IRodinLocation loc1 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 3, 9);
		final IRodinLocation loc2 =
				new AttributeSubstringLocation(elt1F1, TEST_ATTR_TYPE, 7, 11);

		final boolean included = loc1.isIncludedIn(loc2);

		assertFalse(
				"an attribute substring should not be included in an overlapping one",
				included);
	}
}
