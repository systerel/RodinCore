/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static org.eventb.core.tests.ResourceUtils.*;
import static org.eventb.core.tests.indexers.ListAssert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.MachineIndexer;
import org.rodinp.core.IRodinFile;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineDependenciesTests extends EventBIndexerTests {

	private static final List<IRodinFile> NO_FILES = Collections.emptyList();
	private static final String C1_NAME = "c1";
	private static final String M1_NAME = "m1";
	private static final String M2_NAME = "m2";

	private static void assertDependencies(List<IRodinFile> expected,
			IRodinFile[] actual) {
		assertSameAsArray(expected, actual, "dependencies");
	}

	private static final String MCH_1REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "	<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
			+ "	<org.eventb.core.refinesMachine"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"m1\"/>"
			+ "	</org.eventb.core.machineFile>";

	private static final String MCH_1SEES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
			+ "<org.eventb.core.seesContext"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"c1\"/>"
			+ "</org.eventb.core.machineFile>";

	/**
	 * @param name
	 */
	public MachineDependenciesTests(String name) {
		super(name);
	}

	public void testNoDependencies() throws Exception {

		final IMachineRoot file = ResourceUtils.createMachine(rodinProject, M1_NAME, EMPTY_MACHINE);

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(file);

		assertDependencies(NO_FILES, actual);
	}

	public void testOneRefines() throws Exception {

		final IMachineRoot parent = ResourceUtils.createMachine(rodinProject, M1_NAME, EMPTY_MACHINE);

		final IMachineRoot child = ResourceUtils.createMachine(rodinProject, M2_NAME, MCH_1REFINES);

		final List<IRodinFile> expected = Arrays.asList(parent.getRodinFile());

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		assertDependencies(expected, actual);
	}

	public void testOneSees() throws Exception {

		final IContextRoot parent = ResourceUtils.createContext(rodinProject, C1_NAME, EMPTY_CONTEXT);

		final IMachineRoot child = ResourceUtils.createMachine(rodinProject, M2_NAME, MCH_1SEES);

		final List<IRodinFile> expected = Arrays.asList(parent.getRodinFile());

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		assertDependencies(expected, actual);
	}

	/**
	 * @throws Exception
	 */
	public void testFileDoesNotExist() throws Exception {

		final IMachineRoot child = ResourceUtils.createMachine(rodinProject, M2_NAME, MCH_1REFINES);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.getDependencies(child);
	}

	public void testBadFileType() throws Exception {

		final IContextRoot context = ResourceUtils.createContext(rodinProject, CTX_BARE_NAME,
				CST_1DECL_1REF_THM);

		final MachineIndexer indexer = new MachineIndexer();

		try {
			indexer.getDependencies(context);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// missing = at internal_element1 in variable node
		final String MALFORMED_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
				+ "<org.eventb.core.seesContext"
				+ "		name\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				MALFORMED_MACHINE);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.getDependencies(machine);
	}

	/**
	 * @throws Exception
	 */
	public void testMissingAttribute() throws Exception {
		final String MCH_1REFINES_NO_TARGET_ATT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				MCH_1REFINES_NO_TARGET_ATT);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.getDependencies(machine);
	}

}
