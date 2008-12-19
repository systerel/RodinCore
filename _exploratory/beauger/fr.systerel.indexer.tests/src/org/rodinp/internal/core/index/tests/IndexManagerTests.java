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

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.Descriptor;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.RodinIndex;

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

	public IndexManagerTests(String name) {
		super(name, true);
		RodinIndexer.disableIndexing();
	}

	@Override
	protected void setUp() throws Exception {
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
		indexer = new FakeIndexer(rodinIndex);
		RodinIndexer.register(indexer, file.getRootElementType());
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	public void testScheduleIndexing() throws Exception {

		manager.scheduleIndexing(file);

		final RodinIndex index = manager.getIndex(project);
		final Descriptor desc1 = index.getDescriptor(elt1);
		final Descriptor desc2 = index.getDescriptor(elt2);

		assertDescriptor(desc1, declElt1, 1);
		assertDescriptor(desc2, declElt2, 1);
	}

	public void testSeveralIndexing() throws Exception {

		rodinIndex.removeDescriptor(elt2);

		// first indexing with elt1, without elt2
		manager.scheduleIndexing(file);

		final RodinIndex index1 = manager.getIndex(project);
		final Descriptor descElement = index1.getDescriptor(elt1);

		assertDescriptor(descElement, declElt1, 1);
		assertNoSuchDescriptor(index1, elt2);

		// removing elt1, adding elt2
		rodinIndex.removeDescriptor(elt1);
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());

		// second indexing with element2, without element
		manager.scheduleIndexing(file);

		final RodinIndex index2 = manager.getIndex(project);
		final Descriptor descElement2 = index2.getDescriptor(elt2);

		assertNoSuchDescriptor(index2, elt1);
		assertDescriptor(descElement2, declElt2, 1);
	}

	public void testSeveralIndexers() throws Exception {

		final RodinIndex rodinIndex2 = new RodinIndex();
		makeDescAndDefaultOcc(rodinIndex, declElt3, file.getRoot());

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		RodinIndexer.register(indexer, file.getRootElementType());
		
		final IIndexer indexer2 = new FakeIndexer(rodinIndex2);
		RodinIndexer.register(indexer2, file.getRootElementType());

		manager.scheduleIndexing(file);

		final RodinIndex index = manager.getIndex(project);
		final Descriptor desc1 = index.getDescriptor(elt1);
		final Descriptor desc2 = index.getDescriptor(elt2);
		final Descriptor desc3 = index.getDescriptor(elt3);

		assertDescriptor(desc1, declElt1, 1);
		assertDescriptor(desc2, declElt2, 1);
		assertDescriptor(desc3, declElt3, 1);
	}

	public void testSeveralIndexersFail() throws Exception {

		final IInternalElement root = file.getRoot();

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		RodinIndexer.register(indexer, root.getElementType());
		
		final IIndexer indexer2 = new FakeFailIndexer();
		RodinIndexer.register(indexer2, root.getElementType());

		manager.scheduleIndexing(file);

		final RodinIndex index = manager.getIndex(project);
		final Descriptor desc1 = index.getDescriptor(elt1);
		final Descriptor desc2 = index.getDescriptor(elt2);

		assertDescriptor(desc1, declElt1, 1);
		assertDescriptor(desc2, declElt2, 1);
		assertNoSuchDescriptor(index, elt3);
	}
	
	public void testGetDeclarationsSecondIndexer() throws Exception {

		manager.clearIndexers();
		indexer = new FakeIndexer(rodinIndex);
		RodinIndexer.register(indexer, file.getRootElementType());

		final IDeclaration[] expected = makeArray(declElt1, declElt2);
		
		final FakeGetDeclIndexer indexer2 = new FakeGetDeclIndexer();
		RodinIndexer.register(indexer2, file.getRootElementType());

		manager.scheduleIndexing(file);

		final IDeclaration[] declarations = indexer2.getDeclarations();
		
		assertSameElements(expected, declarations,
				"declarations obtained by a second indexer");
	}

	public void testIndexFileDoesNotExist() throws Exception {
		final IRodinFile inexistentFile =
				project.getRodinFile("inexistentFile.test");
		manager.scheduleIndexing(inexistentFile);
	}

	public void testIndexNoIndexer() throws Exception {
		manager.clearIndexers();
		manager.scheduleIndexing(file);
	}

	public void testIndexSeveralProjects() throws Exception {
		final String eltF2Name = "eltF2Name";

		final IRodinProject project2 = createRodinProject("P2");
		try {
			final IRodinFile file2 = createRodinFile(project2, "file2P2.test");
			final NamedElement eltF2 = createNamedElement(file2, eltF2Name);

			final Declaration declEltF2 = new Declaration(eltF2, eltF2Name);
			makeDescAndDefaultOcc(rodinIndex, declEltF2, file2.getRoot());

			manager.scheduleIndexing(file, file2);

			final RodinIndex index1 = manager.getIndex(project);
			final Descriptor desc1 = index1.getDescriptor(elt1);
			final RodinIndex index2 = manager.getIndex(project2);
			final Descriptor desc2 = index2.getDescriptor(eltF2);

			assertDescriptor(desc1, declElt1, 1);
			assertDescriptor(desc2, declEltF2, 1);
		} finally {
			deleteProject("P2");
		}
	}

	public void testIndexerException() throws Exception {
		final IIndexer exceptIndexer = new FakeExceptionIndexer();

		manager.clearIndexers();

		RodinIndexer.register(exceptIndexer, file.getRoot().getElementType());

		// should not throw an exception
		manager.scheduleIndexing(file);

		final FileTable fileTable = manager.getFileTable(project);
		final IInternalElement[] elements = fileTable.get(file);
		assertEquals("no element expected", 0, elements.length);
	}

	public void testIndexerFailed() throws Exception {
		final IIndexer failIndexer = new FakeFailIndexer();

		manager.clearIndexers();

		RodinIndexer.register(failIndexer, file.getRoot().getElementType());

		// should not throw an exception
		manager.scheduleIndexing(file);

		final FileTable fileTable = manager.getFileTable(project);
		final IInternalElement[] elements = fileTable.get(file);
		assertEquals("no element expected", 0, elements.length);
	}
}
