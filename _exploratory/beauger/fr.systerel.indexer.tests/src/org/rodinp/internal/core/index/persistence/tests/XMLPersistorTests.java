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
package org.rodinp.internal.core.index.persistence.tests;

import static org.rodinp.internal.core.index.persistence.tests.Resources.*;
import static org.rodinp.internal.core.index.persistence.tests.XMLUtils.*;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.internal.core.index.IndexManager;
import org.rodinp.internal.core.index.PerProjectPIM;
import org.rodinp.internal.core.index.ProjectIndexManager;
import org.rodinp.internal.core.index.persistence.IPersistor;
import org.rodinp.internal.core.index.persistence.tests.Resources.IPersistResource;
import org.rodinp.internal.core.index.persistence.xml.XMLPersistor;
import org.rodinp.internal.core.index.tests.IndexTests;

/**
 * @author Nicolas Beauger
 * 
 */
public class XMLPersistorTests extends IndexTests {

	private static IRodinProject project;


	private static void assertPPPIM(IPersistResource resource,
			PerProjectPIM actual) {
		final PerProjectPIM expected = resource.getPPPIM();
		final List<IRodinFile> files = resource.getRodinFiles();
		final List<String> names = resource.getNames();
		
		final Set<IRodinProject> expProjects = expected.projects();
		final Set<IRodinProject> actProjects = actual.projects();
		assertEquals("bad PerProjectPIM projects", expProjects, actProjects);
		for (IRodinProject prj : expProjects) {
			final ProjectIndexManager expPIM = expected.get(prj);
			final ProjectIndexManager actPIM = actual.get(prj);

			assertEquals("bad project", expPIM.getProject(), actPIM
					.getProject());
			assertIndex(expPIM.getIndex(), actPIM.getIndex());
			assertExportTable(expPIM.getExportTable(), actPIM.getExportTable(),
					files);
			assertOrder(expPIM.getOrder(), actPIM.getOrder(), files);

			assertFileTable(expPIM.getFileTable(), actPIM.getFileTable(), files);
			assertNameTable(expPIM.getNameTable(), actPIM.getNameTable(), names);
		}
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

//	public void testRestoreEmpty() throws Exception {
//		fail("Not yet implemented");
//	}

	public void testSavePR1() throws Exception {
		
		IPersistResource pr1 = Resources.makePR1(project);
		
		final File expFile = makePR1File();
	
		final File file = getNewFile(getName());
		final IPersistor ps = new XMLPersistor();
	
		ps.save(pr1.getPPPIM(), file);
		
		assertFile(expFile, file);
	}
	
	public void testRestorePR1() throws Exception {

		IPersistResource pr1 = makePR1(project);
		final File pr1File = makePR1File();

		final IPersistor ps = new XMLPersistor();

		final PerProjectPIM pppim = new PerProjectPIM();
		ps.restore(pr1File, pppim);

		assertPPPIM(pr1, pppim);
	}


//	public void testSave2Projects() throws Exception {
//		fail("Not yet implemented");
//	}
//	
//	public void testRestore2Projects() throws Exception {
//		fail("Not yet implemented");
//	}

}
