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
package org.rodinp.core.tests.index.persistence;

import static org.rodinp.core.tests.index.IndexTestsUtil.*;
import static org.rodinp.core.tests.index.persistence.Resources.*;
import static org.rodinp.core.tests.index.persistence.XMLAssert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.index.IndexTests;
import org.rodinp.core.tests.index.persistence.Resources.IPersistResource;
import org.rodinp.internal.core.index.IIndexDelta;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;
import org.rodinp.internal.core.index.Registry;
import org.rodinp.internal.core.index.persistence.IPersistor;
import org.rodinp.internal.core.index.persistence.PersistentIndexManager;
import org.rodinp.internal.core.index.persistence.xml.XMLPersistor;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistorTests extends IndexTests {

	private static IRodinProject project;

	private static void assertIMData(IPersistResource resource,
			PersistentIndexManager actual) {
		final PersistentIndexManager expected = resource.getIMData();
		final List<IRodinFile> files = resource.getRodinFiles();
		final List<String> names = resource.getNames();

		final PerProjectPIM expPPPIM = expected.getPPPIM();
		final PerProjectPIM actPPPIM = actual.getPPPIM();

		final Set<IRodinProject> expProjects = expPPPIM.projects();
		final Set<IRodinProject> actProjects = actPPPIM.projects();
		assertEquals("bad PerProjectPIM projects", expProjects, actProjects);
		for (IRodinProject prj : expProjects) {
			final ProjectIndexManager expPIM = expPPPIM.get(prj);
			final ProjectIndexManager actPIM = actPPPIM.get(prj);

			assertEquals("bad project", expPIM.getProject(), actPIM
					.getProject());
			assertIndex(expPIM.getIndex(), actPIM.getIndex());
			assertExportTable(expPIM.getExportTable(), actPIM.getExportTable(),
					files);
			assertOrder(expPIM.getOrder(), actPIM.getOrder(), files);

			assertFileTable(expPIM.getFileTable(), actPIM.getFileTable(), files);
			assertNameTable(expPIM.getNameTable(), actPIM.getNameTable(), names);
		}

		final List<IIndexDelta> expDeltas =
				new ArrayList<IIndexDelta>(expected.getDeltas());
		final List<IIndexDelta> actDeltas =
				new ArrayList<IIndexDelta>(actual.getDeltas());
		assertSameElements(expDeltas, actDeltas, "saved deltas");
	}

	public XMLPersistorTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	@Override
	protected void tearDown() throws Exception {
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

	private static void restoreTest(File file, IPersistResource expected) {

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

	public void testRestoreNoPIM() throws Exception {

		IPersistResource pr = makeNoPIM(project);
		final File prFile = makeNoPIMFile();

		restoreTest(prFile, pr);

	}

	public void testSaveNoPIM() throws Exception {

		IPersistResource pr = Resources.makeNoPIM(project);

		final File prFile = makeNoPIMFile();

		saveTest(pr, prFile, getName());

	}

	public void testSaveBasic() throws Exception {

		IPersistResource pr = Resources.makeBasic(project);

		final File prFile = makeBasicFile();

		saveTest(pr, prFile, getName());
	}

	public void testRestoreBasic() throws Exception {

		IPersistResource pr = makeBasic(project);
		final File prFile = makeBasicFile();

		restoreTest(prFile, pr);
	}

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

	public void testSaveSortedFiles() throws Exception {
		IPersistResource pr = Resources.makeSortedFiles(project);

		final File prFile = makeSortedFilesFile();

		saveTest(pr, prFile, getName());
	}

	public void testRestoreSortedFiles() throws Exception {
		IPersistResource pr = makeSortedFiles(project);
		final File prFile = makeSortedFilesFile();

		restoreTest(prFile, pr);
	}

	public void testSaveIterating() throws Exception {
		IPersistResource pr = Resources.makeIterating(project);

		final File prFile = makeIteratingFile();

		saveTest(pr, prFile, getName());
	}

	public void testRestoreIterating() throws Exception {
		IPersistResource pr = makeIterating(project);
		final File prFile = makeIteratingFile();

		restoreTest(prFile, pr);
	}

	public void testRestoreIncorrectFileNoExportNode() throws Exception {
		final File prFile = makeNoExportNodeFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	public void testRestoreIncorrectFileTwoRodinIndexes() throws Exception {
		final File prFile = makeTwoRodinIndexesFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	public void testRestoreIncorrectFileMissingttribute() throws Exception {
		final File prFile = makeMissingAttributeFile();
		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	public void testRestoreIncorrectElementHandle() throws Exception {
		final File prFile = makeBadElementHandleFile();

		// should make a brand new PPPIM without throwing any exception
		restoreTest(prFile, EMPTY_RESOURCE);
	}

	public void testSaveDelta() throws Exception {
		IPersistResource pr = Resources.makeDelta(project);

		final File prFile = makeDeltaFile();

		saveTest(pr, prFile, getName());
	}
	
	public void testRestoreDelta() throws Exception {
		IPersistResource pr = makeDelta(project);
		final File prFile = makeDeltaFile();

		restoreTest(prFile, pr);
	}

}
