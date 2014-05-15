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
package org.rodinp.core.tests.indexer.tables;

import static org.rodinp.core.tests.util.IndexTestsUtil.TEST_FILE_TYPE;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertSameElements;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.IndexManager;

public class NameTableUsageTests extends IndexTests {

	private static final boolean DEBUG = false;

	private static IRodinFile file;
	private static final String name1 = "NTUT_name1";
	private static final String name2 = "NTUT_name2";
	private static FakeNameIndexer indexer = new FakeNameIndexer(2, name1,
			name2);
	private static final IndexManager manager = IndexManager.getDefault();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "nameInd.test");
		manager.addIndexer(indexer, TEST_FILE_TYPE);
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	private void assertNameTable(IRodinFile rodinFile, String name,
			Set<IDeclaration> expectedElements, String message) throws InterruptedException {

		Set<IDeclaration> actualElements = manager.getDeclarations(rodinFile
				.getRodinProject(), name);

		if (DEBUG) {
			System.out.println(getName() + ": " + message);
		}
		assertSameElements(expectedElements, actualElements, "declarations");
	}

	@Test
	public void testNameTableFilling() throws Exception {
		manager.scheduleIndexing(file);
		Set<IDeclaration> expectedName1 = indexer.getIndexedElements(name1);
		Set<IDeclaration> expectedName2 = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1, "");
		assertNameTable(file, name2, expectedName2, null);
	}

	@Test
	public void testNameTableUpdating() throws Exception {

		// first indexing with 2 elements for both name1 and name2
		manager.scheduleIndexing(file);
		Set<IDeclaration> expectedName1 = indexer.getIndexedElements(name1);
		Set<IDeclaration> expectedName2 = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1, "Before");
		assertNameTable(file, name2, expectedName2, null);

		// changing the indexer
		manager.clearIndexers();
		indexer = new FakeNameIndexer(1, name1);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		// second indexing with 1 element for name1 only
		manager.scheduleIndexing(file);
		Set<IDeclaration> expectedName1Bis = indexer.getIndexedElements(name1);
		Set<IDeclaration> expectedName2Bis = indexer.getIndexedElements(name2);

		assertNameTable(file, name1, expectedName1Bis, "After");
		assertNameTable(file, name2, expectedName2Bis, null);
	}

}
