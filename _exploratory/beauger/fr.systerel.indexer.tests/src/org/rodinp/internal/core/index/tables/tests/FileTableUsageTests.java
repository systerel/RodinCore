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
package org.rodinp.internal.core.index.tables.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IIndexer;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.tables.FileTable;
import org.rodinp.internal.core.index.tables.RodinIndex;
import org.rodinp.internal.core.index.tests.FakeIndexer;
import org.rodinp.internal.core.index.tests.IndexTests;

public class FileTableUsageTests extends IndexTests {

	public FileTableUsageTests(String name) {
		super(name, true);
	}

	private static final boolean DEBUG = false;

	private static IIndexer indexer;
	private static IRodinFile file;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static IInternalElement[] fileElements;
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
		fileElements = new NamedElement[] { elt1, elt2 };
		rodinIndex = new RodinIndex();
		final IDeclaration declElt1 = new Declaration(elt1, elt1Name);
		final IDeclaration declElt2 = new Declaration(elt2, elt2Name);
		makeDescAndDefaultOcc(rodinIndex, declElt1, file.getRoot());
		makeDescAndDefaultOcc(rodinIndex, declElt2, file.getRoot());

		indexer = new FakeIndexer(rodinIndex);
		RodinIndexer.register(indexer, file.getRoot().getElementType());
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		manager.clear();
		super.tearDown();
	}

	private void assertFileTable(IRodinFile rodinFile,
			IInternalElement[] expectedElements, String message) throws InterruptedException {

		final FileTable fileTable = manager.getFileTable(rodinFile
				.getRodinProject());
		IInternalElement[] actualElements = fileTable.get(rodinFile);

		if (DEBUG) {
			System.out.println(getName() + message);
			System.out.println(fileTable.toString());
		}
		assertSameElements(expectedElements, actualElements);
	}

	public void testFileTableFilling() throws Exception {
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElements, "");
	}

	public void testDeleteElement() throws Exception {

		// first indexing with elt1 and elt2
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElements, "\nBefore");

		// removing an element
		rodinIndex.removeDescriptor(elt1);
		IInternalElement[] fileElementsAfter = new NamedElement[] { elt2 };

		// second indexing with elt2 only
		manager.scheduleIndexing(file);
		assertFileTable(file, fileElementsAfter, "\nAfter");

	}

}
