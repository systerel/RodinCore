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
package org.rodinp.core.tests.indexer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rodinp.core.tests.util.IndexTestsUtil.TEST_FILE_TYPE;
import static org.rodinp.core.tests.util.IndexTestsUtil.TEST_FILE_TYPE_2;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertSameElements;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.tests.indexer.tables.FakeNameIndexer;
import org.rodinp.internal.core.indexer.IndexerRegistry;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexerRegistryTests extends IndexTests {

	private static IIndexer indexer;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static List<IIndexer> indexerList;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		indexer = new FakeNameIndexer(1, "name");
		indexerList = Arrays.asList(indexer);
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = createRodinFile(rodinProject, "indexersManager.test");
		file2 = createRodinFile(rodinProject, "indexersManager.test2");
		IndexerRegistry.getDefault().clear();
		// needed by first test to clear contributed indexers
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		IndexerRegistry.getDefault().clear();
		super.tearDown();
	}

	@Test
	public void testAddGetIndexer() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		indReg.addIndexer(indexer, TEST_FILE_TYPE);

		final List<IIndexer> actual = indReg.getIndexersFor(file1);

		assertSameElements(indexerList, actual, "indexer");
	}

	@Test
	public void testAddGetSeveralFileTypes() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		indReg.addIndexer(indexer, TEST_FILE_TYPE);
		indReg.addIndexer(indexer, TEST_FILE_TYPE_2);

		final List<IIndexer> actual1 = indReg.getIndexersFor(file1);
		final List<IIndexer> actual2 = indReg.getIndexersFor(file2);

		assertSameElements(indexerList, actual1, "indexer");
		assertSameElements(indexerList, actual2, "indexer");
	}

	@Test
	public void testAddGetVariousIndexers() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();
		final IIndexer indexer2 = new FakeNameIndexer(1, "name2");
		final List<IIndexer> indexer2List = Arrays.asList(indexer2);

		indReg.addIndexer(indexer, TEST_FILE_TYPE);
		indReg.addIndexer(indexer2, TEST_FILE_TYPE_2);

		final List<IIndexer> actual1 = indReg.getIndexersFor(file1);
		final List<IIndexer> actual2 = indReg.getIndexersFor(file2);

		assertSameElements(indexerList, actual1, "indexer");
		assertSameElements(indexer2List, actual2, "indexer");
	}

	@Test
	public void testAddGetSeveralIndexersSameType() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();
		final IIndexer indexer2 = new FakeNameIndexer(1, "name2");
		final List<IIndexer> indexer12List = Arrays.asList(indexer, indexer2);

		
		indReg.addIndexer(indexer, TEST_FILE_TYPE);
		indReg.addIndexer(indexer2, TEST_FILE_TYPE);

		final List<IIndexer> actual = indReg.getIndexersFor(file1);

		assertSameElements(indexer12List, actual, "indexer");
	}

	@Test
	public void testGetUnknownFileType() throws Exception {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		try {
			indReg.getIndexersFor(file1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	@Test
	public void testIsIndexableTrue() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		indReg.addIndexer(indexer, TEST_FILE_TYPE);
		final boolean indexable = indReg.isIndexable(file1);

		assertTrue("File type "
				+ TEST_FILE_TYPE
				+ " should be indexable", indexable);
	}

	@Test
	public void testIsIndexableFalse() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		final boolean indexable = indReg.isIndexable(file1);

		assertFalse("File type "
				+ TEST_FILE_TYPE
				+ " should NOT be indexable", indexable);
	}

	@Test
	public void testClear() {
		final IndexerRegistry indReg = IndexerRegistry.getDefault();

		indReg.addIndexer(indexer, TEST_FILE_TYPE);
		indReg.clear();

		final boolean indexable = indReg.isIndexable(file1);
		assertFalse("File type "
				+ TEST_FILE_TYPE
				+ " should NOT be indexable", indexable);
	}

}
