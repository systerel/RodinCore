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
package org.rodinp.core.tests.indexer.tables;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.indexer.FakeIndexer;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.tables.IFileTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

public class FileTableUsageTests extends IndexTests {

	public FileTableUsageTests(String name) {
		super(name);
	}

	private static final boolean DEBUG = false;

	private static IIndexer indexer;
	private static IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static IDeclaration declElt1;
	private static IDeclaration declElt2;
	private static IDeclaration[] fileDecls;
	private static final IndexManager manager = IndexManager.getDefault();

	private static final String elt1Name = "elt1Name";
	private static final String elt2Name = "elt2Name";

	private static RodinIndex rodinIndex;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final IRodinProject rodinProject = createRodinProject("P");
		file = createRodinFile(rodinProject, "fileTable.test");
		elt1 = createNamedElement(file, "elt1");
		elt2 = createNamedElement(file, "elt2");
		declElt1 = new Declaration(elt1, elt1Name);
		declElt2 = new Declaration(elt2, elt2Name);
		fileDecls = new IDeclaration[] { declElt1, declElt2 };
		rodinIndex = new RodinIndex();
		makeDescAndDefaultOcc(rodinIndex, declElt1, file.getRoot());
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());

		indexer = new FakeIndexer(rodinIndex);
		manager.addIndexer(indexer, TEST_FILE_TYPE);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	private void assertFileTable(IRodinFile rodinFile,
			IDeclaration[] expectedElements, String message) throws InterruptedException {

		final IFileTable fileTable = manager.getFileTable(rodinFile
				.getRodinProject());
		IDeclaration[] actualElements = fileTable.get(rodinFile);

		if (DEBUG) {
			System.out.println(getName() + message);
			System.out.println(fileTable.toString());
		}
		assertSameElements(expectedElements, actualElements, "elements in file table");
	}

	public void testFileTableFilling() throws Exception {
		manager.scheduleIndexing(file);
		assertFileTable(file, fileDecls, "");
	}

	public void testDeleteElement() throws Exception {

		// first indexing with elt1 and elt2
		manager.scheduleIndexing(file);
		assertFileTable(file, fileDecls, "\nBefore");

		// removing an element
		rodinIndex.removeDescriptor(elt1);
		final IDeclaration[] fileElementsAfter = new IDeclaration[] { declElt2 };

		// second indexing with elt2 only
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElementsAfter, "\nAfter");

	}

}
