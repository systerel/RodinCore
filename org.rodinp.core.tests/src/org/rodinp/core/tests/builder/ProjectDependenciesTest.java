/*******************************************************************************
 * Copyright (c) 2021 CentraleSupélec and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CentraleSupélec - initial tests
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import static org.eclipse.core.resources.IncrementalProjectBuilder.INCREMENTAL_BUILD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.rodinp.core.RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * Tests for builds with dependencies between two projects.
 *
 * @author Guillaume Verdier
 */
public class ProjectDependenciesTest extends AbstractBuilderTest {

	private IRodinProject testProject;

	private IRodinFile testFile;

	private IRodinProject depProject;

	private IRodinFile depFile;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		SCTool.RUN_SC = true;
		SCTool.SHOW_CLEAN = true;
		SCTool.SHOW_EXTRACT = true;
		SCTool.SHOW_RUN = true;

		testProject = createRodinProject("P");
		depProject = createRodinProject("P2");

		depFile = createRodinFile("P2/x.ctx");
		createData(depFile, "one");
		depFile.save(null, true);

		testFile = createRodinFile("P/y.ctx");
		createDependency(testFile, "P2/x");
		createData(testFile, "two");
		testFile.save(null, true);
	}

	@After
	public void tearDown() throws Exception {
		testProject.getProject().delete(true, true, null);
		depProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	@Override
	protected void runBuilder(IRodinProject project, String... expectedTrace)
			throws CoreException {
		ToolTrace.flush();
		project.getProject().build(INCREMENTAL_BUILD, null);
		ToolTrace.assertTrace(expectedTrace);
	}

	protected void runWorkspaceBuilder(String... expectedTrace)
			throws CoreException {
		ToolTrace.flush();
		testProject.getProject().getWorkspace().build(INCREMENTAL_BUILD, null);
		ToolTrace.assertTrace(expectedTrace);
	}

	private void hasMarkers(IRodinFile file) throws CoreException {
		IFile res = file.getResource();
		IMarker[] markers = res.findMarkers(BUILDPATH_PROBLEM_MARKER, false,
				IResource.DEPTH_ZERO);
		assertNotEquals("should have markers", markers.length, 0);
	}

	private void hasNotMarkers(IRodinFile file) throws CoreException {
		IFile res = file.getResource();
		IMarker[] markers = res.findMarkers(BUILDPATH_PROBLEM_MARKER, false,
				IResource.DEPTH_ZERO);
		assertEquals("should not have markers", markers.length, 0);
	}

	/**
	 * Checks that P/y contains its data ("two") and the data imported from its
	 * dependency ("one").
	 */
	private void assertFileWithDependencyContents() throws CoreException {
		IRodinFile scCtx = getRodinFile("P/y.csc");
		assertContents("Invalid contents of checked context",
				"y.csc\n" +
				"  data: two\n" +
				"  data: one",
				scCtx);
	}

	/**
	 * Ensures dependency works if target in another project is built before dependency
	 */
	@Test
	public void testDependenciesProjectsOneTwo() throws Exception {
		runBuilder(depProject,
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		runBuilder(testProject,
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		assertFileWithDependencyContents();

		// Then, the incremental builder has nothing to do anymore.
		runWorkspaceBuilder();
	}

	/**
	 * Ensures dependency works if source is built before target in another
	 * project (source has to be rebuilt)
	 */
	@Test
	public void testDependenciesProjectsTwoOne() throws Exception {
		runBuilder(testProject,
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		hasMarkers(testFile);
		runBuilder(depProject,
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);
		
		// The incremental build detects that P/y.csc needs rebuilding
		runWorkspaceBuilder("CSC run /P/y.csc");
		hasNotMarkers(testFile);
		assertFileWithDependencyContents();

		// Then, the incremental builder has nothing to do anymore.
		runWorkspaceBuilder();
	}

	/**
	 * Ensures building a project without its dependencies in another project
	 * fails, and then succeeds when the dependencies get created.
	 */
	@Test
	public void testDependenciesProjectsSingleBuildFails() throws Exception {
		runBuilder(testProject,
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		hasMarkers(testFile);
		
		// The incremental builder performs the other project
		runWorkspaceBuilder(
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);

		// A second round of the incremental builder makes everything OK
		runWorkspaceBuilder("CSC run /P/y.csc");
		hasNotMarkers(testFile);

		// Then, the incremental builder has nothing to do anymore.
		runWorkspaceBuilder();
	}

	/**
	 * Ensures that a project is rebuilt if a dependency in another project is
	 * modified.
	 */
	@Test
	public void testDependenciesProjectsUpdates() throws Exception {
		// First, build everything
		runBuilder(depProject,
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);
		runBuilder(testProject,
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		hasNotMarkers(testFile);
		
		// The state is stable, nothing more to do.
		runWorkspaceBuilder();

		// Then modify P2/x.ctx
		IData data = depFile.getRoot().getChildrenOfType(IData.ELEMENT_TYPE)[0];
		data.setAttributeValue(fString, "three", null);
		depFile.save(null, true);

		// Rebuild everything: only P2 is rebuilt in the first round
		runWorkspaceBuilder(
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);
		
		// Then project P is rebuilt in a second round
		runWorkspaceBuilder("CSC run /P/y.csc");
		hasNotMarkers(testFile);
		// Finally, ensure that P/y.csc contains the new data
		assertContents("Invalid contents of checked context",
				"y.csc\n" +
				"  data: two\n" +
				"  data: three",
				getRodinFile("P/y.csc"));

		// Then, the incremental builder has nothing to do anymore.
		runWorkspaceBuilder();
	}

	/**
	 * Ensures that when the dependency between two projects is removed, the
	 * builder removes it too.
	 */
	@Test
	public void testDependenciesRemoval() throws Exception {
		// First, build everything
		runBuilder(depProject,
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);
		runBuilder(testProject,
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		hasNotMarkers(testFile);

		// The state is stable, nothing more to do.
		runWorkspaceBuilder();

		// Then remove the dependency
		testFile.getRoot().getChildrenOfType(IDependency.ELEMENT_TYPE)[0]
				.delete(true, null);
		testFile.save(null, true);

		// Rebuild everything: only P needs to be rebuilt
		runWorkspaceBuilder(
				"CSC extract /P/y.ctx",
				"CSC run /P/y.csc"
		);
		hasNotMarkers(testFile);

		// Now, modify P2
		IData data = depFile.getRoot().getChildrenOfType(IData.ELEMENT_TYPE)[0];
		data.setAttributeValue(fString, "three", null);
		depFile.save(null, true);

		// Rebuild everything: P2 is rebuilt
		runWorkspaceBuilder(
				"CSC extract /P2/x.ctx",
				"CSC run /P2/x.csc"
		);
		hasNotMarkers(depFile);

		// Then, the incremental builder has nothing else to do as P does not
		// depend on P2 anymore
		runWorkspaceBuilder();
	}

}
