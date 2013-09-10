/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - used list of string in Tool Trace
 *     Systerel - added test ensuring cleanup always happen
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author lvoisin
 *
 */
public class CBuilderTest extends AbstractBuilderTest {
	
	private IRodinProject project;
	
	public CBuilderTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;
		project = createRodinProject("P");
		ToolTrace.flush();
	}
	
	protected void tearDown() throws Exception {
		project.getProject().delete(true, true, null);
		super.tearDown();
	}

	private void runBuilder(String... expectedTrace) throws CoreException {
		super.runBuilder(project, expectedTrace);
	}
	
	/**
	 * Ensures that extractors and tools are run when a file is created.
	 */
	public void testOneBuild() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc"
		);
		
		IRodinFile scCtx = getRodinFile("P/x.csc");
		assertContents("Invalid contents of checked context",
				"x.csc\n" +
				"  data: one",
				scCtx);
	}
		
	/**
	 * Ensures that generated files are cleaned up when their source is deleted.
	 */
	public void testOneDelete() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder();
		ToolTrace.flush();

		ctx.delete(true, null);
		runBuilder(
				"CSC clean /P/x.csc"
		);
	}
	
	/**
	 * Ensures dependency is followed if source of dependency is created before target 
	 */
	public void testOneTwoCreate() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
//		ToolTrace.flush();
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder();
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
	}

	/**
	 * Ensures dependency is followed if target of dependency is created before source 
	 */
	public void testTwoOneCreate() throws Exception {
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		runBuilder();

		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		
		runBuilder(
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc",
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC run /P/y.csc"
		);
	}
	
	/**
	 * Ensures dependency is followed transitively
	 */
	public void testOneTwoThreeCreateChange() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder();
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		
		IRodinFile ctz = createRodinFile("P/z.ctx");
		createDependency(ctz, "y");
		createData(ctz, "three");
		ctz.save(null, true);
	
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC extract /P/y.ctx",
				"CSC extract /P/z.ctx",
				"CSC run /P/y.csc",
				"CSC run /P/z.csc"
		);
	}
	
	/**
	 * Ensures cycles are ignored
	 */
	public void testOneTwoThreeCreateCycle() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createDependency(ctx, "y");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder();
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		
		IRodinFile ctz = createRodinFile("P/z.ctx");
		createData(ctz, "three");
		ctz.save(null, true);
	
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC extract /P/y.ctx",
				"CSC extract /P/z.ctx",
				"CSC run /P/z.csc"
		);
	}
	/**
	 * Test that the test case for database problems can work correctly
	 */
	public void testRodinDBProblem() throws Exception {
		
		try {
			CSCTool.FAULTY_AFTER_TARGET_CREATION = true;

			IRodinFile ctx = createRodinFile("P/x.ctx");
			createData(ctx, "one");
			ctx.save(null, true);

			IRodinFile cty = createRodinFile("P/y.ctx");
			createDependency(cty, "x");
			createData(cty, "two");
			cty.save(null, true);		
			runBuilder(
					"CSC extract /P/y.ctx",
					"CSC extract /P/x.ctx",
					"CSC run /P/x.csc",
					"CSC run /P/y.csc"
			);
		} finally {
			CSCTool.FAULTY_AFTER_TARGET_CREATION = false;
		}
	}

	
	
	/**
	 * Proper treatment of database errors while tools are run
	 */
	public void testRodinDBProblemInTool() throws Exception {
		final IRodinFile ctx;
		try {
			CSCTool.FAULTY_AFTER_TARGET_CREATION = true;
		
			ctx = createRodinFile("P/x.ctx");
			createData(ctx, "one");
			ctx.save(null, true);

			IRodinFile cty = createRodinFile("P/y.ctx");
			createDependency(cty, "x");
			createData(cty, "two");
			cty.save(null, true);		
			runBuilder();
			ToolTrace.flush();
			
			hasMarkers("P/x.ctx");
			
		} finally {
			CSCTool.FAULTY_AFTER_TARGET_CREATION = false;
		}

		createData(ctx, "three");
		ctx.save(null, true);
		runBuilder();
		
		hasNotMarkers("P/x.ctx");
		
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC run /P/y.csc"
		);
		
		
	}

	/**
	 * Testing Bug #2417502: Tool problem reported for inexistent file
	 */
	public void testRodinDBProblemInToolBeforeTargetCreation() throws Exception {
		try {
			CSCTool.FAULTY_BEFORE_TARGET_CREATION = true;

			final IRodinFile ctx = createRodinFile("P/x.ctx");
			createData(ctx, "one");
			ctx.save(null, true);

			runBuilder();

		} finally {
			CSCTool.FAULTY_BEFORE_TARGET_CREATION = false;
		}

		hasMarkers("P/x.ctx");
	}
	
	private void hasMarkers(String name) throws CoreException {
		IRodinFile csc = RodinCore.valueOf(getFile(name));
		IMarker[] markers = 
			csc.getResource().findMarkers(
					RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, 
					false, 
					IResource.DEPTH_ZERO);
		assertNotSame("has markers", markers.length, 0);
	}
		
	private void hasNotMarkers(String name) throws CoreException {
		IRodinFile csc = RodinCore.valueOf(getFile(name));
		IMarker[] markers = 
			csc.getResource().findMarkers(
					RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, 
					false, 
					IResource.DEPTH_ZERO);
		assertSame("has markers", markers.length, 0);
	}
		
	/**
	 * Ensures that deleting a derived files have it rebuilt in the next
	 * cycle.
	 * 
	 * See Bug #1605247.
	 */
	public void testDeleteDerivedRebuild() throws Exception {
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc"
		);
		ToolTrace.flush();

		IRodinFile csc = getRodinFile("P/x.csc");
		csc.delete(true, null);
		runBuilder(
				"CSC run /P/x.csc"
		);
	}
	
	/**
	 * Ensures dependency is followed if source of dependency is deleted, and recreated
	 */
	public void testOneTwoDelete() throws Exception {
		
		IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		
		IRodinFile cty = createRodinFile("P/y.ctx");
		createDependency(cty, "x");
		createData(cty, "two");
		cty.save(null, true);		
		
		runBuilder();
		ToolTrace.flush();
		
		ctx.delete(true, null);
		
		runBuilder(
				"CSC clean /P/x.csc",
				"CSC run /P/y.csc"
				);
		
		hasMarkers("P/y.ctx");
		
		ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);
		ToolTrace.flush();
		
		runBuilder(
				"CSC extract /P/x.ctx",
				"CSC run /P/x.csc",
				"CSC run /P/y.csc"
		);
		
		hasNotMarkers("P/y.ctx");
	
	}

	/**
	 * Ensures that clean removes a generated file, even if it has been modified
	 * since the last build.
	 */
	public void testClean() throws Exception {
		final IRodinFile ctx = createRodinFile("P/x.ctx");
		createData(ctx, "one");
		ctx.save(null, true);

		runBuilder("CSC extract /P/x.ctx", "CSC run /P/x.csc");
		ToolTrace.flush();

		final IRodinFile csc = getRodinFile("P/x.csc");
		createData(csc, "new");
		csc.save(null, true);
		runBuilder();

		runBuilderClean(project);
		ToolTrace.assertTrace("CSC clean /P/x.csc");
	}

}
