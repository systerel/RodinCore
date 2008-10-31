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
package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.TEST_KIND;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.IOccurrenceKind;
import org.rodinp.core.index.IInternalLocation;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Occurrence;

public class OccurrenceTests extends IndexTests {

	public OccurrenceTests(String name) {
		super(name, true);
	}

	private final IOccurrenceKind defaultKind = TEST_KIND;
	private IInternalLocation location;
	private IOccurrence occ;


	private static void assertLocation(IInternalLocation expected,
			IInternalLocation actual) {
		assertEquals("Field IInternalLocation in Occurrence is not correct", expected, actual);
	}

	private static void assertKind(IOccurrenceKind expected, IOccurrenceKind actual) {
		assertEquals("Field IOccurrenceKind in Occurrence is not correct", expected, actual);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final IRodinProject rodinProject = createRodinProject("P");
		IRodinFile file = createRodinFile(rodinProject, "occ.test");
		NamedElement elem = createNamedElement(file, "elem");

		location = RodinIndexer.getInternalLocation(elem);
		occ = new Occurrence(defaultKind, location);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertKind(defaultKind, occ.getKind());
		assertLocation(location, occ.getLocation());
	}

}
