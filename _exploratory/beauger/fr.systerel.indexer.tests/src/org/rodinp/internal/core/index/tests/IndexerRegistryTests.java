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

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IIndexer;
import org.rodinp.internal.core.index.IndexerRegistry;
import org.rodinp.internal.core.index.tables.tests.FakeNameIndexer;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexerRegistryTests extends IndexTests {

	private static FakeNameIndexer indexer;
	private static IRodinFile file1;
	private static IRodinFile file2;

	/**
	 * @param name
	 */
	public IndexerRegistryTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		indexer = new FakeNameIndexer(1, "name");
		final IRodinProject rodinProject = createRodinProject("P");
		file1 = createRodinFile(rodinProject, "indexersManager.test");
		file2 = createRodinFile(rodinProject, "indexersManager.test2");

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testAddGetIndexer() {
		final IndexerRegistry indMan = new IndexerRegistry();

		indMan.addIndexer(indexer, file1.getRoot().getElementType());
		final IIndexer actual = indMan.getIndexerFor(file1);

		assertEquals("Bad indexer", indexer, actual);
	}

	public void testAddGetSeveralFileTypes() {
		final IndexerRegistry indMan = new IndexerRegistry();

		indMan.addIndexer(indexer, file1.getRoot().getElementType());
		indMan.addIndexer(indexer, file2.getRoot().getElementType());

		final IIndexer actual1 = indMan.getIndexerFor(file1);
		final IIndexer actual2 = indMan.getIndexerFor(file2);

		assertEquals("Bad indexer", indexer, actual1);
		assertEquals("Bad indexer", indexer, actual2);
	}

	public void testAddGetVariousIndexers() {
		final IndexerRegistry indMan = new IndexerRegistry();
		final FakeNameIndexer indexer2 = new FakeNameIndexer(1, "name2");

		indMan.addIndexer(indexer, file1.getRoot().getElementType());
		indMan.addIndexer(indexer2, file2.getRoot().getElementType());

		final IIndexer actual1 = indMan.getIndexerFor(file1);
		final IIndexer actual2 = indMan.getIndexerFor(file2);

		assertEquals("Bad indexer", indexer, actual1);
		assertEquals("Bad indexer", indexer2, actual2);
	}

	public void testGetUnknownFileType() throws Exception {
		final IndexerRegistry indMan = new IndexerRegistry();

		try {
			indMan.getIndexerFor(file1);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Test method for
	 * {@link org.rodinp.internal.core.index.IndexerRegistry#isIndexable(IRodinFile)}.
	 */
	public void testIsIndexableTrue() {
		final IndexerRegistry indMan = new IndexerRegistry();

		indMan.addIndexer(indexer, file1.getRoot().getElementType());
		final boolean indexable = indMan.isIndexable(file1);

		assertTrue("File type " + file1.getElementType()
				+ " should be indexable", indexable);
	}

	public void testIsIndexableFalse() {
		final IndexerRegistry indMan = new IndexerRegistry();

		final boolean indexable = indMan.isIndexable(file1);

		assertFalse("File type " + file1.getElementType()
				+ " should NOT be indexable", indexable);
	}

	public void testClear() {
		final IndexerRegistry indMan = new IndexerRegistry();

		indMan.addIndexer(indexer, file1.getRoot().getElementType());
		indMan.clear();

		final boolean indexable = indMan.isIndexable(file1);
		assertFalse("File type " + file1.getElementType()
				+ " should NOT be indexable", indexable);
	}

}
