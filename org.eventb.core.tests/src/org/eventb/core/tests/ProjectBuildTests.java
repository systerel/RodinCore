/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.core.resources.IncrementalProjectBuilder.INCREMENTAL_BUILD;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.rodinp.core.RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Tests exercising build on whole projects.
 * 
 * @author Laurent Voisin
 */
public class ProjectBuildTests extends EventBTest {

	private static final String FWD_CONFIG = "org.eventb.core.fwd";
	private static final String INEXISTENT_CONFIG = "org.event.core.tests.inexistent";

	private static void assertExists(IRodinElement element) {
		if (!element.exists()) {
			fail("Element " + element.getHandleIdentifier() + " should exist");
		}
	}

	private static void assertGenerated(IEventBRoot... roots) {
		for (IEventBRoot root : roots) {
			assertGenerated(root);
		}
	}

	private static void assertGenerated(IEventBRoot root) {
		if (root instanceof IContextRoot)
			assertExists(root.getSCContextRoot());
		if (root instanceof IMachineRoot)
			assertExists(root.getSCMachineRoot());
		assertExists(root.getPORoot());
		assertExists(root.getPSRoot());
	}

	private static void assertNoMarker(IRodinProject rp) throws CoreException {
		final IResource res = rp.getResource();
		final IMarker[] markers = res.findMarkers(null, true, DEPTH_INFINITE);
		if (markers.length != 0) {
			System.out.println("Unexpected markers found:");
			for (IMarker marker: markers) {
				printMarker(marker, System.out);
			}
			fail("Project shouldn't contain any marker");
		}
	}
	
	private static void assertMarkers(IRodinProject rp,
			GraphProblem... problems) throws CoreException {
		final IResource res = rp.getResource();
		final IMarker[] markers = res.findMarkers(null, true, DEPTH_INFINITE);
		if (problems.length != markers.length) {
			final StringBuilder sb = new StringBuilder("Expected error codes:");
			for (IMarker marker : markers) {
				final Object errorCode = marker
						.getAttribute(RodinMarkerUtil.ERROR_CODE);
				sb.append("\n\t" + errorCode);
			}
			fail(sb.toString());
		}
		final List<String> expCodes = new ArrayList<String>();
		for (GraphProblem problem : problems) {
			expCodes.add(problem.getErrorCode());
		}
		for (IMarker marker : markers) {
			final Object errorCode = marker
					.getAttribute(RodinMarkerUtil.ERROR_CODE);
			assertTrue("unexpected error code: " + errorCode,
					expCodes.contains(errorCode));
		}
	}

	private static void printMarker(IMarker marker, PrintStream stream)
			throws CoreException {
		stream.println(marker.getResource());
		stream.println(marker.getType());
		stream.println("Attributes:");
		final Map<?, ?> attrs = marker.getAttributes();
		for (Map.Entry<?, ?> e : attrs.entrySet()) {
			stream.println(e.getKey() + ": " + e.getValue());
		}
		stream.println();
	}

	public ProjectBuildTests() {
		super();
	}

	public ProjectBuildTests(String name) {
		super(name);
	}

	@Override
	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		IMachineRoot root = super.createMachine(bareName);
		root.setConfiguration(FWD_CONFIG, null);
		return root;
	}

	// Creates a file for the given root and ensures that it contains garbage.
	private void makeGarbageFile(IEventBRoot root) throws CoreException {
		final IRodinFile rodinFile = root.getRodinFile();
		rodinFile.create(true, null);
		root.setAttributeValue(ASSIGNMENT_ATTRIBUTE, "garbage", null);
	}

	/**
	 * Verifies that, the project being read-only, the build fails with a tool
	 * problem but that the given temporary file is not present afterwards.
	 */
	private void assertBuildFailsNoTempFile(IEventBRoot root, String tmpFileName)
			throws CoreException {
		final IProject project = root.getRodinProject().getProject();
		setReadOnly(project, true);

		// Run builder
		project.build(INCREMENTAL_BUILD, null);

		// Verify that some tools encountered a problem
		final IMarker[] markers = project.findMarkers(BUILDPATH_PROBLEM_MARKER,
				false, DEPTH_INFINITE);
		assertTrue(markers.length > 0);

		final IFile scTmpFile = project.getFile(tmpFileName);
		assertFalse("Temporary file should not exist", scTmpFile.exists());
	}

	/**
	 * Regression test for builder bug: Two components depend on a third one,
	 * but PO are generated only for one of them.
	 */
	public void testTwoConcrete() throws Exception {
		final IMachineRoot abs = createMachine("M1");
		addInitialisation(abs);
		saveRodinFileOf(abs);

		final IMachineRoot con1 = createMachine("A1");
		addMachineRefines(con1, abs.getElementName());
		addInitialisation(con1);
		saveRodinFileOf(con1);

		final IMachineRoot con2 = createMachine("A2");
		addMachineRefines(con2, abs.getElementName());
		addInitialisation(con2);
		saveRodinFileOf(con2);

		runBuilder();
		assertNoMarker(abs.getRodinProject());
		assertGenerated(abs, con1, con2);
	}

	/**
	 * Test ensuring that several configurations can be given within a file, and
	 * that the SC and POG work appropriately as soon as one configuration is
	 * present.
	 */
	public void testMultipleConfigurations() throws Exception {
		final IMachineRoot abs = createMachine("M1");
		final String oldConfig = abs.getConfiguration();
		final String newConfig = oldConfig + ";" + INEXISTENT_CONFIG;
		abs.setConfiguration(newConfig, null);
		addInitialisation(abs);
		saveRodinFileOf(abs);

		runBuilder();
		assertMarkers(abs.getRodinProject(), GraphProblem.UnknownConfigurationWarning);
		assertGenerated(abs);
		
		assertEquals(newConfig, abs.getSCMachineRoot().getConfiguration());
	}

	/**
	 * Ensures that the static checker temporary file gets deleted when the
	 * static checker ends in error on a context. The error is caused by the
	 * output file being read-only.
	 */
	public void testContextSCTmpFile() throws Exception {
		final IContextRoot con = createContext("C");
		addConstants(con, "c");
		saveRodinFileOf(con);
		makeGarbageFile(con.getSCContextRoot());
		assertBuildFailsNoTempFile(con, "C.bcc_tmp");
	}

	/**
	 * Ensures that the static checker temporary file gets deleted when the
	 * static checker ends in error on a machine. The error is caused by the
	 * output file being read-only.
	 */
	public void testMachineSCTmpFile() throws Exception {
		final IMachineRoot mch = createMachine("M");
		saveRodinFileOf(mch);
		makeGarbageFile(mch.getSCMachineRoot());
		assertBuildFailsNoTempFile(mch, "M.bcm_tmp");
	}

	/**
	 * Ensures that the proof obligation generator temporary file gets deleted
	 * when the static checker ends in error. The error is caused by the output
	 * file being read-only.
	 */
	public void testMachinePOTmpFile() throws Exception {
		final IMachineRoot mch = createMachine("M");
		saveRodinFileOf(mch);
		makeGarbageFile(mch.getPORoot());
		assertBuildFailsNoTempFile(mch, "M.bpo_tmp");
	}

}
