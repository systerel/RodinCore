/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.tests.builder.basis.Data;

/**
 * @author A. Gilles
 *
 */
public class GraphBuilderTest extends AbstractBuilderTest {
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ToolTrace.flush();
	}

	@After
	public void tearDown() throws Exception {
		for (IProject project: getWorkspaceRoot().getProjects()) {
			project.delete(true, true, null);
		}
		super.tearDown();
	}

	/**
	 * check that the graph doesn't contain sub directory
	 */
	@Test
	public void testGraphFullBuild() throws Exception {
		importProject("Q2");
		
		IRodinProject qProject = getRodinProject("Q2");
		
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		
		POTool.RUN_PO = false;
		POTool.SHOW_CLEAN = false;
		POTool.SHOW_EXTRACT = false;
		POTool.SHOW_RUN = false;

		runBuilder(qProject, "MSC extract /Q2/a.mch", "CSC extract /Q2/x.ctx",
				"CSC run /Q2/x.csc", "MSC run /Q2/a.msc");
	}
	
	
	/**
	 * check that the graph doesn't contain sub directory
	 */
	@Test
	public void testGraphIncrementalBuild() throws Exception {		
		importProject("Q2");

		IRodinProject qProject = getRodinProject("Q2");
		
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		
		POTool.RUN_PO = false;
		POTool.SHOW_CLEAN = false;
		POTool.SHOW_EXTRACT = false;
		POTool.SHOW_RUN = false;

		runBuilder(qProject, (String[]) null);
		ToolTrace.flush();
			
		final IInternalElement root = qProject.getRodinFile("a.mch").getRoot();
		root.createChild(Data.ELEMENT_TYPE, null, null);
		root.getRodinFile().save(null, true);
		qProject.getProject().getFile("Q3/b.mch").touch(null);
		runBuilder(qProject, "MSC extract /Q2/a.mch","MSC run /Q2/a.msc");
	}

}
