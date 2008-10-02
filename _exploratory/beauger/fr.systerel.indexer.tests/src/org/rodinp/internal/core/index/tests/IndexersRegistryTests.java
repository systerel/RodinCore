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
import org.rodinp.internal.core.index.IndexersRegistry;
import org.rodinp.internal.core.index.tables.tests.FakeNameIndexer;

/**
 * @author Nicolas Beauger
 * 
 */
public class IndexersRegistryTests extends IndexTests {

	private static FakeNameIndexer indexer;
	private static IRodinFile file1;
	private static IRodinFile file2;

	/**
	 * @param name
	 */
	public IndexersRegistryTests(String name) {
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
		final IndexersRegistry indMan = new IndexersRegistry();

		indMan.addIndexer(indexer, file1.getElementType());
		final IIndexer actual = indMan.getIndexerFor(file1.getElementType());

		assertEquals("Bad indexer", indexer, actual);
	}

	public void testAddGetSeveralFileTypes() {
		final IndexersRegistry indMan = new IndexersRegistry();

		indMan.addIndexer(indexer, file1.getElementType());
		indMan.addIndexer(indexer, file2.getElementType());

		final IIndexer actual1 = indMan.getIndexerFor(file1.getElementType());
		final IIndexer actual2 = indMan.getIndexerFor(file2.getElementType());

		assertEquals("Bad indexer", indexer, actual1);
		assertEquals("Bad indexer", indexer, actual2);
	}

	public void testAddGetVariousIndexers() {
		final IndexersRegistry indMan = new IndexersRegistry();
		final FakeNameIndexer indexer2 = new FakeNameIndexer(1, "name2");

		indMan.addIndexer(indexer, file1.getElementType());
		indMan.addIndexer(indexer2, file2.getElementType());

		final IIndexer actual1 = indMan.getIndexerFor(file1.getElementType());
		final IIndexer actual2 = indMan.getIndexerFor(file2.getElementType());

		assertEquals("Bad indexer", indexer, actual1);
		assertEquals("Bad indexer", indexer2, actual2);
	}

	public void testGetUnknownFileType() throws Exception {
		final IndexersRegistry indMan = new IndexersRegistry();

		try {
			indMan.getIndexerFor(file1.getElementType());
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Test method for
	 * {@link org.rodinp.internal.core.index.IndexersRegistry#isIndexable(org.rodinp.core.IFileElementType)}.
	 */
	public void testIsIndexableTrue() {
		final IndexersRegistry indMan = new IndexersRegistry();

		indMan.addIndexer(indexer, file1.getElementType());
		final boolean indexable = indMan.isIndexable(file1.getElementType());

		assertTrue("File type " + file1.getElementType()
				+ " should be indexable", indexable);
	}

	public void testIsIndexableFalse() {
		final IndexersRegistry indMan = new IndexersRegistry();

		final boolean indexable = indMan.isIndexable(file1.getElementType());

		assertFalse("File type " + file1.getElementType()
				+ " should NOT be indexable", indexable);
	}

	public void testClear() {
		final IndexersRegistry indMan = new IndexersRegistry();

		indMan.addIndexer(indexer, file1.getElementType());
		indMan.clear();

		final boolean indexable = indMan.isIndexable(file1.getElementType());
		assertFalse("File type " + file1.getElementType()
				+ " should NOT be indexable", indexable);
	}

}
