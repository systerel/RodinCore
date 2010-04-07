/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
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

import java.io.PrintStream;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Tests exercising build on whole projects.
 * 
 * @author Laurent Voisin
 */
public class ProjectBuildTests extends EventBTest {

	private static final String FWD_CONFIG = "org.eventb.core.fwd";

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
		final String inexistentConfig = "org.event.core.tests.inexistent";
		
		final IMachineRoot abs = createMachine("M1");
		final String oldConfig = abs.getConfiguration();
		final String newConfig = oldConfig + ";" + inexistentConfig;
		abs.setConfiguration(newConfig, null);
		addInitialisation(abs);
		saveRodinFileOf(abs);

		runBuilder();
		assertNoMarker(abs.getRodinProject());
		assertGenerated(abs);
		
		assertEquals(newConfig, abs.getSCMachineRoot().getConfiguration());
	}

}
