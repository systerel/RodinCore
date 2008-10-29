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

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContains;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertContainsNot;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.assertDescDeclaration;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createDefaultOccurrence;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.Occurrence;

public class DescriptorTests extends IndexTests {

	public DescriptorTests(String name) {
		super(name, true);
	}

	private IRodinProject rodinProject;
	private IRodinFile file;
	private Descriptor testDesc;
	private NamedElement testElt;
	private IDeclaration declTestElt;

	private static final String testEltName = "testElt";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "desc.test");
		testElt = createNamedElement(file, "internalName");
		declTestElt = new Declaration(testElt, testEltName);
		testDesc = new Descriptor(declTestElt);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		testElt = null;
		super.tearDown();
	}

	public void testConstructor() throws Exception {
		final Descriptor desc = new Descriptor(declTestElt);
		assertDescDeclaration(desc, declTestElt);
		assertNotNull("occurrences should not be null", testDesc
				.getOccurrences());
	}

	public void testAddHasOccurrence() throws Exception {
		final Occurrence occ = createDefaultOccurrence(testElt);

		testDesc.addOccurrence(occ);

		assertTrue("occurrence expected: " + occ, testDesc.hasOccurrence(occ));
	}

	public void testGetOccurrences() throws Exception {
		final Occurrence occ1 = createDefaultOccurrence(testElt);
		final Occurrence occ2 = createDefaultOccurrence(file);

		testDesc.addOccurrence(occ1);
		testDesc.addOccurrence(occ2);
		
		IndexTestsUtil.assertContainsAll(testDesc, occ1, occ2);
	}

	public void testRemoveOccurrences() throws Exception {
		final Occurrence localOcc = createDefaultOccurrence(testElt);
		final IRodinFile importer = createRodinFile(rodinProject, "importerFile.test");
		final Occurrence importOcc = createDefaultOccurrence(importer);

		testDesc.addOccurrence(localOcc);
		testDesc.addOccurrence(importOcc);

		testDesc.removeOccurrences(testElt.getRodinFile());

		assertContainsNot(testDesc, localOcc);
		assertContains(testDesc, importOcc);
	}

}
