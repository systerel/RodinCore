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
package org.rodinp.core.tests.indexer.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.rodinp.core.tests.indexer.persistence.Resources.EMPTY_RESOURCE;
import static org.rodinp.core.tests.indexer.persistence.Resources.getNewFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.make2PIMs;
import static org.rodinp.core.tests.indexer.persistence.Resources.make2PIMsFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeBadElementHandleFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeBasic;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeBasicFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeDelta;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeDeltaFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeIterating;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeIteratingFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeMissingAttributeFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeNoExportNodeFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeNoPIM;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeNoPIMFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeSortedFiles;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeSortedFilesFile;
import static org.rodinp.core.tests.indexer.persistence.Resources.makeTwoRodinIndexesFile;
import static org.rodinp.core.tests.indexer.persistence.XMLAssert.assertFile;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertIndex;
import static org.rodinp.core.tests.util.IndexTestsUtil.assertSameElements;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.basis.RodinTestRoot;
import org.rodinp.core.tests.indexer.IndexTests;
import org.rodinp.core.tests.indexer.persistence.Resources.IPersistResource;
import org.rodinp.internal.core.indexer.IIndexDelta;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.IndexerRegistry;
import org.rodinp.internal.core.indexer.PerProjectPIM;
import org.rodinp.internal.core.indexer.ProjectIndexManager;
import org.rodinp.internal.core.indexer.Registry;
import org.rodinp.internal.core.indexer.persistence.IPersistor;
import org.rodinp.internal.core.indexer.persistence.PersistentIndexManager;
import org.rodinp.internal.core.indexer.persistence.xml.XMLPersistor;
import org.rodinp.internal.core.indexer.sort.TotalOrder;
import org.rodinp.internal.core.indexer.tables.FileTable;
import org.rodinp.internal.core.indexer.tables.IExportTable;
import org.rodinp.internal.core.indexer.tables.NameTable;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistorTests extends IndexTests {

	private static IRodinProject project;

	public static void assertOrder(TotalOrder<IRodinFile> expected,
			ProjectIndexManager pim, List<IRodinFile> files) {
		// assert initially marked files
		assertOrder(expected, pim, false);

		// assert that files not marked are present and well sorted
		for (IRodinFile file : files) {
			expected.setToIter(file);
		}
		assertOrder(expected, pim, true);
	}

	private static void assertOrder(TotalOrder<IRodinFile> expected,
			ProjectIndexManager pim, boolean indexAll) {
		// nodes must already be marked
		final FakeOrderRecordIndexer indexer = new FakeOrderRecordIndexer();
		IndexerRegistry.getDefault().clear();
		IndexerRegistry.getDefault().addIndexer(indexer,
				RodinTestRoot.ELEMENT_TYPE);
		if (indexAll) {
			pim.indexAll(null);
		} else {
			pim.doIndexing(null);
		}
		
		final Iterator<IRodinFile> actual = indexer.getIndexedFiles().iterator();
		while (expected.hasNext()) {
			final IRodinFile expFile = expected.next();
			assertTrue("should have next: " + expFile, actual.hasNext());
			final IRodinFile actFile = actual.next();
			assertEquals("Bad file", expFile, actFile);
		}
	}

	public static void assertExportTable(IExportTable expected,
			ProjectIndexManager actual, List<IRodinFile> files)
			throws InterruptedException {
		for (IRodinFile file : files) {
			assertSameElements(expected.get(file), actual.getExports(file),
					"exports");
		}
	}

	public static void assertFileTable(FileTable expected,
			ProjectIndexManager actual, List<IRodinFile> files)
			throws InterruptedException {
		for (IRodinFile file : files) {
			assertSameElements(expected.get(file),
					actual.getDeclarations(file), "elements in file table");
		}
	}

	public static void assertNameTable(NameTable expected, ProjectIndexManager actual,
			List<String> names) throws InterruptedException {
		for (String name : names) {
			assertSameElements(expected.getDeclarations(name), actual
					.getDeclarations(name), "elements in name table");
		}
	}


	private static void assertIMData(IPersistResource resource,
			PersistentIndexManager actual) throws InterruptedException {
		final Map<IRodinProject, PublicPIM> expectedPIMs = resource.getPublicPIMs();
		final List<IRodinFile> files = resource.getRodinFiles();
		final List<String> names = resource.getNames();

		final PerProjectPIM actPPPIM = actual.getPPPIM();

		final Set<IRodinProject> expProjects = expectedPIMs.keySet();
		final Set<IRodinProject> actProjects = actPPPIM.projects();
		assertEquals("bad PerProjectPIM projects", expProjects, actProjects);
		for (IRodinProject prj : expProjects) {
			final PublicPIM expPIM = expectedPIMs.get(prj);
			final ProjectIndexManager actPIM = actPPPIM.get(prj);

			assertEquals("bad project", expPIM.project, actPIM
					.getProject());
			assertIndex(expPIM.index, actPIM);
			assertExportTable(expPIM.exportTable, actPIM, files);

			assertFileTable(expPIM.fileTable, actPIM, files);
			assertNameTable(expPIM.nameTable, actPIM, names);
			// test order at the end (changes tables)
			assertOrder(expPIM.order, actPIM, files);
		}

		final List<IIndexDelta> expDeltas =
				new ArrayList<IIndexDelta>(resource.getDeltas());
		final List<IIndexDelta> actDeltas =
				new ArrayList<IIndexDelta>(actual.getDeltas());
		assertSameElements(expDeltas, actDeltas, "saved deltas");
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	@After
	public void tearDown() throws Exception {
		deleteProject("P");
		IndexManager.getDefault().clear();
		super.tearDown();
	}

	private static void saveTest(IPersistResource pr, File expected,
			String newFileName) throws Exception {

		final File file = getNewFile(newFileName);

		final IPersistor ps = new XMLPersistor();

		final boolean success = ps.save(pr.getIMData(), file);

		assertTrue("error while saving", success);
		assertFile(expected, file);
		
		expected.delete();
		file.delete();
	}

	private static void restoreTest(File file, IPersistResource expected)
			throws InterruptedException {

		final IPersistor ps = new XMLPersistor();

		final PerProjectPIM pppim = new PerProjectPIM();
		final List<IIndexDelta> deltas = new ArrayList<IIndexDelta>();
		final PersistentIndexManager persistIM =
				new PersistentIndexManager(
				pppim, deltas, new Registry<String, String>());
		ps.restore(file, persistIM);

		assertIMData(expected, persistIM);
		
		file.delete();
	}

	@Test
	public void testRestoreNoPIM() throws Exception {

		IPersistResource pr = makeNoPIM(project);
		final File prFile = makeNoPIMFile();

		restoreTest(prFile, pr);

	}

	@Test
	public void testSaveNoPIM() throws Exception {

		IPersistResource pr = Resources.makeNoPIM(project);

		final File prFile = makeNoPIMFile();

		saveTest(pr, prFile, getName());

	}

	@Test
	public void testSaveBasic() throws Exception {

		IPersistResource pr = Resources.makeBasic(project);

		final File prFile = makeBasicFile();

		saveTest(pr, prFile, getName());
	}

	@Test
	public void testRestoreBasic() throws Exception {

		IPersistResource pr = makeBasic(project);
		final File prFile = makeBasicFile();

		restoreTest(prFile, pr);
	}

	@Test
	public void testSave2PIMs() throws Exception {
		// FIXME P1 & P2 might appear reversed between actual and expected order
		final IRodinProject p1 = createRodinProject("P1");
		final IRodinProject p2 = createRodinProject("P2");
		IPersistResource pr = Resources.make2PIMs(p1, p2);

		final File prFile = make2PIMsFile();

		try {
			saveTest(pr, prFile, getName());
		} finally {
			deleteProject("P1");
			deleteProject("P2");
		}
	}

	@Test
	public void testRestore2PIMs() throws Exception {
		final IRodinProject p1 = createRodinProject("P1");
		final IRodinProject p2 = createRodinProject("P2");
		IPersistResource pr = make2PIMs(p1, p2);
		final File prFile = make2PIMsFile();

		try {
			restoreTest(prFile, pr);
		} finally {
			deleteProject("P1");
			deleteProject("P2");
		}
	}

	@Test
	public void testSaveSortedFiles() throws Exception {
		IPersistResource pr = Resources.makeSortedFiles(project);

		final File prFile = makeSortedFilesFile();

		saveTest(pr, prFile, getName());
	}

	@Test
	public void testRestoreSortedFiles() throws Exception {
		IPersistResource pr = makeSortedFiles(project);
		final File prFile = makeSortedFilesFile();

		restoreTest(prFile, pr);
	}

	@Test
	public void testSaveIterating() throws Exception {
		IPersistResource pr = Resources.makeIterating(project);

		final File prFile = makeIteratingFile();

		saveTest(pr, prFile, getName());
	}

	@Test
	public void testRestoreIterating() throws Exception {
		IPersistResource pr = makeIterating(project);
		final File prFile = makeIteratingFile();

		restoreTest(prFile, pr);
	}

	@Test
	public void testRestoreIncorrectFileNoExportNode() throws Exception {
		final File prFile = makeNoExportNodeFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	@Test
	public void testRestoreIncorrectFileTwoRodinIndexes() throws Exception {
		final File prFile = makeTwoRodinIndexesFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	@Test
	public void testRestoreIncorrectFileMissingttribute() throws Exception {
		final File prFile = makeMissingAttributeFile();
		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	@Test
	public void testRestoreIncorrectElementHandle() throws Exception {
		final File prFile = makeBadElementHandleFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	@Test
	public void testSaveDelta() throws Exception {
		IPersistResource pr = Resources.makeDelta(project);

		final File prFile = makeDeltaFile();

		saveTest(pr, prFile, getName());
	}
	
	@Test
	public void testRestoreDelta() throws Exception {
		IPersistResource pr = makeDelta(project);
		final File prFile = makeDeltaFile();

		restoreTest(prFile, pr);
	}

}
