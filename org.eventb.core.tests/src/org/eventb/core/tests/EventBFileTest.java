/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added test for as***File()
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Tests of the file manipulation routines provided by the event-B plugin.
 * 
 * @author Laurent Voisin
 * @see IEventBProject
 */
public class EventBFileTest {
	
	private static final String BARE_NAME = "foo";

	private static final IRodinProject rodinProject = RodinCore
			.valueOf(getWorkspace().getRoot().getProject("P"));

	private static final IEventBProject evbProject = (IEventBProject) rodinProject
			.getAdapter(IEventBProject.class);

	private void assertFileName(String name, IRodinFile file) {
		assertEquals("Invalid file name", name, file.getElementName());
	}

	private void assertRootFileName(String extension, IEventBRoot root) {
		final String fileName = root.getElementName() + "." + extension;
		assertFileName(fileName, root.getRodinFile());
	}

	/**
	 * Check all file conversions from the given file.
	 * 
	 * @param file
	 *            an event-B file
	 */
	private void checkFileConversions(IRodinFile file) {
		final IEventBRoot root = (IEventBRoot)file.getRoot();
		final String bareName = file.getBareName();
		assertEquals(bareName, root.getComponentName());
		assertRootFileName("buc", root.getContextRoot());
		assertRootFileName("bum", root.getMachineRoot());
		assertRootFileName("bcc", root.getSCContextRoot());
		assertRootFileName("bcm", root.getSCMachineRoot());
		assertRootFileName("bpo", root.getPORoot());
		assertRootFileName("bpr", root.getPRRoot());
		assertRootFileName("bps", root.getPSRoot());
	}

	/**
	 * Ensures that one can adapt a Rodin project to an event-B project,
	 * and vice-versa.
	 */
	@Test
	public void testProjectAdapters() throws Exception {
		assertEquals(rodinProject, evbProject.getRodinProject());
		assertEquals(rodinProject, evbProject.getAdapter(IRodinProject.class));
	}

	/**
	 * Ensures that an unchecked context can be created from an event-B project.
	 */
	@Test
	public void testContextFile() throws Exception {
		IRodinFile file = evbProject.getContextFile(BARE_NAME);
		assertFileName(BARE_NAME + ".buc", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getContextRoot(BARE_NAME));
	}

	/**
	 * Ensures that an unchecked machine can be created from an event-B project.
	 */
	@Test
	public void testMachineFile() throws Exception {
		IRodinFile file = evbProject.getMachineFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bum", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getMachineRoot(BARE_NAME));
	}

	/**
	 * Ensures that a checked context can be created from an event-B project.
	 */
	@Test
	public void testSCContextFile() throws Exception {
		IRodinFile file = evbProject.getSCContextFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bcc", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getSCContextRoot(BARE_NAME));
	}

	/**
	 * Ensures that a checked machine can be created from an event-B project.
	 */
	@Test
	public void testSCMachineFile() throws Exception {
		IRodinFile file = evbProject.getSCMachineFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bcm", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getSCMachineRoot(BARE_NAME));
	}

	/**
	 * Ensures that a PO file can be created from an event-B project.
	 */
	@Test
	public void testPOFile() throws Exception {
		IRodinFile file = evbProject.getPOFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bpo", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getPORoot(BARE_NAME));
	}

	/**
	 * Ensures that a proof file can be created from an event-B project.
	 */
	@Test
	public void testPRFile() throws Exception {
		IRodinFile file = evbProject.getPRFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bpr", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getPRRoot(BARE_NAME));
	}

	/**
	 * Ensures that a proof status file can be created from an event-B project.
	 */
	@Test
	public void testPSFile() throws Exception {
		IRodinFile file = evbProject.getPSFile(BARE_NAME);
		assertFileName(BARE_NAME + ".bps", file);
		checkFileConversions(file);
		assertEquals(file.getRoot(), evbProject.getPSRoot(BARE_NAME));
	}

	private void assertSimilar(IRodinFile input, IRodinFile expected, IRodinFile actual) {
		if (expected.getRootElementType() == input.getRootElementType()) {
			assertSame(expected, actual);
		} else {
			assertEquals(expected, actual);
		}
	}
	
	/**
	 * Ensures that adaptation to event-B files works appropriately on all
	 * event-B files.
	 */
	@Test
	public void testFileAdaptation() throws Exception {
		final IRodinFile buc = evbProject.getContextFile(BARE_NAME);
		final IRodinFile bum = evbProject.getMachineFile(BARE_NAME);
		final IRodinFile bcc = evbProject.getSCContextFile(BARE_NAME);
		final IRodinFile bcm = evbProject.getSCMachineFile(BARE_NAME);
		final IRodinFile bpo = evbProject.getPOFile(BARE_NAME);
		final IRodinFile bpr = evbProject.getPRFile(BARE_NAME);
		final IRodinFile bps = evbProject.getPSFile(BARE_NAME);
		final IRodinFile[] files = new IRodinFile[] { buc, bum, bcc, bcm, bpo,
				bpr, bps };

		for (IRodinFile file : files) {
			final IFile res = file.getResource();
			assertEquals(file, EventBPlugin.asEventBFile(res));
			assertSimilar(file, file, EventBPlugin.asEventBFile(file));
			assertSimilar(file, buc, EventBPlugin.asContextFile(file));
			assertSimilar(file, bum, EventBPlugin.asMachineFile(file));
			assertEquals(bcc, EventBPlugin.asSCContextFile(file));
			assertEquals(bcm, EventBPlugin.asSCMachineFile(file));
			assertEquals(bpo, EventBPlugin.asPOFile(file));
			assertSimilar(file, bpr, EventBPlugin.asPRFile(file));
			assertSimilar(file, bps, EventBPlugin.asPSFile(file));
		}
	}

	/**
	 * Ensures that adaptation to event-B roots works appropriately on all
	 * event-B roots.
	 */
	@Test
	public void testRootAdaptation() throws Exception {
		final IContextRoot buc = evbProject.getContextRoot(BARE_NAME);
		final IMachineRoot bum = evbProject.getMachineRoot(BARE_NAME);
		final ISCContextRoot bcc = evbProject.getSCContextRoot(BARE_NAME);
		final ISCMachineRoot bcm = evbProject.getSCMachineRoot(BARE_NAME);
		final IPORoot bpo = evbProject.getPORoot(BARE_NAME);
		final IPRRoot bpr = evbProject.getPRRoot(BARE_NAME);
		final IPSRoot bps = evbProject.getPSRoot(BARE_NAME);
		final IEventBRoot[] roots = new IEventBRoot[] { buc, bum, bcc, bcm, bpo,
				bpr, bps };

		for (IEventBRoot root : roots) {
			assertEquals(buc, root.getContextRoot());
			assertEquals(bum, root.getMachineRoot());
			assertEquals(bcc, root.getSCContextRoot());
			assertEquals(bcm, root.getSCMachineRoot());
			assertEquals(bpo, root.getPORoot());
			assertEquals(bpr, root.getPRRoot());
			assertEquals(bps, root.getPSRoot());
		}
	}
}
