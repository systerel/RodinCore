package org.rodinp.internal.core.index.tests;

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
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;

public class DescriptorTests extends IndexTests {

	public DescriptorTests(String name) {
		super(name, true);
	}

	// private void assertElement(IRodinElement expected, IRodinElement actual)
	// {
	// assertEquals("unexpected element", expected, actual);
	// }

	private IRodinProject rodinProject;
	private IRodinFile file;
	private Descriptor testDesc;
	private NamedElement testElt;

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		testElt = createNamedElement(file, "internalName");
		testDesc = new Descriptor(testElt, testEltName);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testElt = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		assertElement(testDesc, testElt);
		assertName(testDesc, testEltName);
		assertNotNull("occurrences should not be null", testDesc
				.getOccurrences());
	}

	public void testAddOccurrence() throws Exception {
		Occurrence occ = createDefaultOccurrence(testElt);

		testDesc.addOccurrence(occ);

		assertContains(testDesc, occ);
	}

	public void testRemoveOccurrences() throws Exception {
		Occurrence localOcc = createDefaultOccurrence(testElt);
		IRodinFile importer = createRodinFile(rodinProject, "importerFile.test");
		Occurrence importOcc = createDefaultOccurrence(importer);

		testDesc.addOccurrence(localOcc);
		testDesc.addOccurrence(importOcc);

		testDesc.removeOccurrences(testElt.getRodinFile());

		assertContainsNot(testDesc, localOcc);
		assertContains(testDesc, importOcc);
	}

}
