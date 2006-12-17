/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IEventBFile;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Tests of the file manipulation routines provided by the event-B plugin.
 * 
 * @author Laurent Voisin
 * @see IEventBFile
 * @see IEventBProject
 */
public class EventBFileTest extends TestCase {
	
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	IRodinProject rodinProject = RodinCore.valueOf(root.getProject("P"));

	IEventBProject evbProject = (IEventBProject) rodinProject
			.getAdapter(IEventBProject.class);

	private void assertFileName(String name, IRodinFile file) {
		assertEquals("Invalid file name", name, file.getElementName());
	}

	/**
	 * Check all file conversions from the given file.
	 * 
	 * @param file
	 *            an event-B file
	 */
	private void checkFileConversions(IEventBFile file) {
		final String bareName = file.getBareName();
		assertEquals(bareName, file.getComponentName());
		assertFileName(bareName + ".buc", file.getContextFile());
		assertFileName(bareName + ".bum", file.getMachineFile());
		assertFileName(bareName + ".bcc", file.getSCContextFile());
		assertFileName(bareName + ".bcm", file.getSCMachineFile());
		assertFileName(bareName + ".bpo", file.getPOFile());
		assertFileName(bareName + ".bpr", file.getPRFile());
		assertFileName(bareName + ".bps", file.getPSFile());
	}

	/**
	 * Ensures that one can adapt a Rodin project to an event-B project,
	 * and vice-versa.
	 */
	public void testProjectAdapters() throws Exception {
		assertEquals(rodinProject, evbProject.getRodinProject());
		assertEquals(rodinProject, evbProject.getAdapter(IRodinProject.class));
	}

	/**
	 * Ensures that an unchecked context can be created from an event-B project.
	 */
	public void testContextFile() throws Exception {
		IContextFile file = evbProject.getContextFile("foo");
		assertFileName("foo.buc", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that an unchecked machine can be created from an event-B project.
	 */
	public void testMachineFile() throws Exception {
		IMachineFile file = evbProject.getMachineFile("foo");
		assertFileName("foo.bum", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that a checked context can be created from an event-B project.
	 */
	public void testSCContextFile() throws Exception {
		ISCContextFile file = evbProject.getSCContextFile("foo");
		assertFileName("foo.bcc", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that a checked machine can be created from an event-B project.
	 */
	public void testSCMachineFile() throws Exception {
		ISCMachineFile file = evbProject.getSCMachineFile("foo");
		assertFileName("foo.bcm", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that a PO file can be created from an event-B project.
	 */
	public void testPOFile() throws Exception {
		IPOFile file = evbProject.getPOFile("foo");
		assertFileName("foo.bpo", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that a proof file can be created from an event-B project.
	 */
	public void testPRFile() throws Exception {
		IPRFile file = evbProject.getPRFile("foo");
		assertFileName("foo.bpr", file);
		checkFileConversions(file);
	}

	/**
	 * Ensures that a proof status file can be created from an event-B project.
	 */
	public void testPSFile() throws Exception {
		IPSFile file = evbProject.getPSFile("foo");
		assertFileName("foo.bps", file);
		checkFileConversions(file);
	}

}
