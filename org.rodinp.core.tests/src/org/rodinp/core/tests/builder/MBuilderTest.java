/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - used list of string in Tool Trace
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author Stefan Hallerstede
 *
 */
public class MBuilderTest extends AbstractBuilderTest {

	private IRodinProject project;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		ToolTrace.flush();
	}

	@After
	public void tearDown() throws Exception {
		for (IProject project: getWorkspaceRoot().getProjects()) {
			project.delete(true, true, null);
		}
		super.tearDown();
	}

	private void runBuilder(String... expectedTrace) throws CoreException {
		super.runBuilder(project, expectedTrace);
	}
	
	private void cleanBuilder() throws CoreException {
		super.runBuilderClean(project);
	}
	
	/**
	 * Ensures that extractors and tools are run when a file is created.
	 */
	@Test
	public void testOneBuild() throws Exception {
		IRodinFile mch = createRodinFile("P/x.mch");
		createData(mch, "one");
		mch.save(null, true);
		
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		
		POTool.RUN_PO = true;
		POTool.SHOW_CLEAN = true;
		POTool.SHOW_EXTRACT = true;
		POTool.SHOW_RUN = true;
		
		runBuilder(
				"MSC extract /P/x.mch",
				"MSC run /P/x.msc",
				"MPO extract /P/x.msc",
				"MPO run /P/x.po"
		);
		
		IRodinFile scMch = getRodinFile("P/x.msc");
		assertContents("Invalid contents of checked machine",
				"x.msc\n" +
				"  data: one",
				scMch);
	}
	
	/**
	 * Ensures that extractors and tools are run when a file is created.
	 */
	@Test
	public void testGraphBuild() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "cone");
		ctx.save(null, true);
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "ctwo");
		cty.save(null, true);		
		
		IRodinFile ctz = createRodinFile("P/z.ctx");
		createData(ctz, "cthree");
		ctz.save(null, true);
	
		IRodinFile mca = createRodinFile("P/a.mch");
		createDependency(mca, "x");
		createData(mca, "mone");
		mca.save(null, true);
		
		IRodinFile mcb = createRodinFile("P/b.mch");
		createDependency(mcb, "y");
		createReference(mcb, "a");
		createData(mcb, "mtwo");
		mcb.save(null, true);
		
		IRodinFile mcc = createRodinFile("P/c.mch");
		createDependency(mcc, "y");
		createReference(mcc, "b");
		createData(mcc, "mthree");
		mcc.save(null, true);
		
		IRodinFile mcd = createRodinFile("P/d.mch");
		createDependency(mcd, "y");
		createDependency(mcd, "z");
		createReference(mcd, "c");
		createData(mcd, "mfour");
		mcd.save(null, true);
		
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = false;
		SCTool.SHOW_EXTRACT = false;
		SCTool.SHOW_RUN = true;
		
		POTool.RUN_PO = true;
		POTool.SHOW_CLEAN = false;
		POTool.SHOW_EXTRACT = false;
		POTool.SHOW_RUN = true;	
		
		runBuilder(
				"CSC run /P/x.csc",
				"CPO run /P/x.po",
				"MSC run /P/a.msc",
				"MPO run /P/a.po",
				"CSC run /P/y.csc",
				"CPO run /P/y.po",
				"MSC run /P/b.msc",
				"MPO run /P/b.po",
				"MSC run /P/c.msc",
				"MPO run /P/c.po",
				"CSC run /P/z.csc",
				"CPO run /P/z.po",
				"MSC run /P/d.msc",
				"MPO run /P/d.po"		);
		
		IRodinFile scMch = getRodinFile("P/d.msc");
		assertContents("Invalid contents of checked machine",
				"d.msc\n" + 
				"  data: mfour\n" + 
				"  data: mthree\n" + 
				"  data: mtwo\n" + 
				"  data: mone\n" + 
				"  data: cone\n" + 
				"  data: ctwo\n" + 
				"  data: cone\n" + 
				"  data: ctwo\n" + 
				"  data: cone\n" + 
				"  data: ctwo\n" + 
				"  data: cone\n" + 
				"  data: cthree",
				scMch);
	}	
	
	/**
	 * same as <code>testGraphBuild()</code> but with subsequent cleaning
	 */
	@Test
	public void testGraphBuildClean() throws Exception {
		testGraphBuild();
		cleanBuilder();
		ToolTrace.flush();
		runBuilder(
				"CSC run /P/x.csc",
				"CPO run /P/x.po",
				"MSC run /P/a.msc",
				"MPO run /P/a.po",
				"CSC run /P/y.csc",
				"CPO run /P/y.po",
				"MSC run /P/b.msc",
				"MPO run /P/b.po",
				"MSC run /P/c.msc",
				"MPO run /P/c.po",
				"CSC run /P/z.csc",
				"CPO run /P/z.po",
				"MSC run /P/d.msc",
				"MPO run /P/d.po"
		);
	}
	
	/**
	 * check that when importing a project it is subsequently built, i.e.
	 * the graph is properly created on import
	 */
	@Test
	public void testProjectImport() throws Exception {
		importProject("Q");
		
		IRodinProject qProject = getRodinProject("Q");
		
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		
		POTool.RUN_PO = false;
		POTool.SHOW_CLEAN = false;
		POTool.SHOW_EXTRACT = false;
		POTool.SHOW_RUN = false;	
		
		runBuilder(qProject,
				"MSC extract /Q/a.mch", 
				"CSC extract /Q/x.ctx",
				"CSC run /Q/x.csc",
				"MSC run /Q/a.msc"
			);
	}

}
