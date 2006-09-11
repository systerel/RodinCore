/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author Stefan Hallerstede
 *
 */
public class MBuilderTest extends AbstractBuilderTest {

	private IRodinProject project;
	
	public MBuilderTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		CPOTool.runCPO = false; // do not use dumb POG
		project = createRodinProject("P");
		ToolTrace.flush();
	}
	
	protected void tearDown() throws Exception {
		project.getProject().delete(true, true, null);
	}

	private void runBuilder(String expectedTrace) throws CoreException {
		super.runBuilder(project, expectedTrace);
	}
	
	/**
	 * Ensures that extractors and tools are run when a file is created.
	 */
	public void testOneBuild() throws Exception {
		IRodinFile mch = createRodinFile("P/x.mch");
		createData(mch, "one");
		mch.save(null, true);
		runBuilder(
				"CSC extract /P/x.ctx\n" +
				"CSC run /P/x.csc"
		);
		
		IRodinFile scMch = getRodinFile("P/x.msc");
		assertContents("Invalid contents of checked context",
				"x.msc\n" +
				"  data: one",
				scMch);
	}
		

}
