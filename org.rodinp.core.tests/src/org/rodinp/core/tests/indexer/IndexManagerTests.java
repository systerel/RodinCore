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

import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.util.IndexTestsUtil.TEST_FILE_TYPE;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertDescriptor;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertNotIndexed;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertSameElements;
import static org.rodinp.core.tests.util.IndexTestsUtil.createNamedElement;
import static org.rodinp.core.tests.util.IndexTestsUtil.createRodinFile;
import static org.rodinp.core.tests.util.IndexTestsUtil.makeArray;
import static org.rodinp.core.tests.util.IndexTestsUtil.makeDescAndDefaultOcc;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.tables.IRodinIndex;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

public class IndexManagerTests extends IndexTests {

	private static IIndexer indexer;
	private static RodinIndex rodinIndex;
	private IRodinProject project;
	private IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static NamedElement elt3;
	private static final String name1 = "elt1Name";
	private static final String name2 = "elt2Name";
	private static final String name3 = "elt3Name";
	private static IDeclaration declElt1;
	private static IDeclaration declElt2;
	private static IDeclaration declElt3;
	private static final IndexManager manager = IndexManager.getDefault();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file = createRodinFile(project, "indMan.test");
		elt1 = createNamedElement(file, "elt1");
		elt2 = createNamedElement(file, "elt2");
		elt3 = createNamedElement(file, "elt3");
		rodinIndex = new RodinIndex();
		declElt1 = new Declaration(elt1, name1);
		declElt2 = new Declaration(elt2, name2);
		declElt3 = new Declaration(elt3, name3);
		makeDescAndDefaultOcc(rodinIndex, declElt1, file.getRoot());
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());
		// no desc3
		manager.clear();
		indexer = new FakeIndexer(rodinIndex);
		manager.addIndexer(indexer, TEST_FILE_TYPE);
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	@Test
	public void testScheduleIndexing() throws Exception {

		manager.scheduleIndexing(file);

		assertDescriptor(manager, declElt1, 1);
		assertDescriptor(manager, declElt2, 1);
	}

	@Test
	public void testSeveralIndexing() throws Exception {

		rodinIndex.removeDescriptor(elt2);

		// first indexing with elt1, without elt2
		manager.scheduleIndexing(file);

		assertDescriptor(manager, declElt1, 1);
		assertNotIndexed(manager, elt2);

		// removing elt1, adding elt2
		rodinIndex.removeDescriptor(elt1);
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());

		// second indexing with element2, without element
		manager.scheduleIndexing(file);

		assertNotIndexed(manager, elt1);
		assertDescriptor(manager, declElt2, 1);
	}

	@Test
	public void testSeveralIndexers() throws Exception {

		final IRodinIndex rodinIndex2 = new RodinIndex();
		makeDescAndDefaultOcc(rodinIndex, declElt3, file.getRoot());

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		manager.addIndexer(indexer, TEST_FILE_TYPE);
		
		final IIndexer indexer2 = new FakeIndexer(rodinIndex2);
		manager.addIndexer(indexer2, TEST_FILE_TYPE);

		manager.scheduleIndexing(file);

		assertDescriptor(manager, declElt1, 1);
		assertDescriptor(manager, declElt2, 1);
		assertDescriptor(manager, declElt3, 1);
	}

	@Test
	public void testSeveralIndexersFail() throws Exception {

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		manager.addIndexer(indexer, TEST_FILE_TYPE);
		
		final IIndexer indexer2 = new FakeFailIndexer();
		manager.addIndexer(indexer2, TEST_FILE_TYPE);

		manager.scheduleIndexing(file);

		assertDescriptor(manager, declElt1, 1);
		assertDescriptor(manager, declElt2, 1);
		assertNotIndexed(manager, elt3);
	}
	
	@Test
	public void testGetDeclarationsSecondIndexer() throws Exception {

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		manager.addIndexer(indexer, TEST_FILE_TYPE);

		final IDeclaration[] expected = makeArray(declElt1, declElt2);
		
		final FakeGetDeclIndexer indexer2 = new FakeGetDeclIndexer();
		manager.addIndexer(indexer2, TEST_FILE_TYPE);

		manager.scheduleIndexing(file);

		final IDeclaration[] declarations = indexer2.getDeclarations();
		
		assertSameElements(expected, declarations,
				"declarations obtained by a second indexer");
	}

	@Test
	public void testIndexFileDoesNotExist() throws Exception {
		final IRodinFile inexistentFile =
				project.getRodinFile("inexistentFile.test");
		manager.scheduleIndexing(inexistentFile);
	}

	@Test
	public void testIndexNoIndexer() throws Exception {
		manager.clearIndexers();
		manager.scheduleIndexing(file);
	}

	@Test
	public void testIndexSeveralProjects() throws Exception {
		final String eltF2Name = "eltF2Name";

		final IRodinProject project2 = createRodinProject("P2");
		try {
			final IRodinFile file2 = createRodinFile(project2, "file2P2.test");
			final NamedElement eltF2 = createNamedElement(file2, eltF2Name);

			final Declaration declEltF2 = new Declaration(eltF2, eltF2Name);
			makeDescAndDefaultOcc(rodinIndex, declEltF2, file2.getRoot());

			manager.scheduleIndexing(file, file2);

			assertDescriptor(manager, declElt1, 1);
			assertDescriptor(manager, declEltF2, 1);
		} finally {
			deleteProject("P2");
		}
	}

	@Test
	public void testIndexerException() throws Exception {
		final IIndexer exceptIndexer = new FakeExceptionIndexer();

		manager.clearIndexers();

		manager.addIndexer(exceptIndexer, TEST_FILE_TYPE);

		// should not throw an exception
		manager.scheduleIndexing(file);

		final Set<IDeclaration> declarations = manager.getDeclarations(file);
		assertTrue("no element expected", declarations.isEmpty());
	}

	@Test
	public void testIndexerFailed() throws Exception {
		final IIndexer failIndexer = new FakeFailIndexer();

		manager.clearIndexers();

		manager.addIndexer(failIndexer, TEST_FILE_TYPE);

		// should not throw an exception
		manager.scheduleIndexing(file);

		final Set<IDeclaration> declarations = manager.getDeclarations(file);
		assertTrue("no element expected", declarations.isEmpty());
	}
}
