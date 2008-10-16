/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.tests.ListAssert.*;
import static org.eventb.core.indexer.tests.ResourceUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.indexer.MachineIndexer;
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

	private static final String EMPTY_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\"/>";

	private static final String MCH_1REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "	<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "	<org.eventb.core.refinesMachine"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"m1\"/>"
			+ "	</org.eventb.core.machineFile>";

	private static final String MCH_1SEES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.seesContext"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"c1\"/>"
			+ "</org.eventb.core.machineFile>";

	public static final String EMPTY_CONTEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\"/>";

	/**
	 * @param name
	 */
	public MachineDependenciesTests(String name) {
		super(name);
	}

	public void testNoDependencies() throws Exception {

		final IMachineFile file = createMachine(project, M1_NAME, EMPTY_MACHINE);

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(file);

		assertDependencies(NO_FILES, actual);
	}

	public void testOneRefines() throws Exception {

		final IRodinFile parent = createMachine(project, M1_NAME, EMPTY_MACHINE);

		final IMachineFile child = createMachine(project, M2_NAME, MCH_1REFINES);

		final List<IRodinFile> expected = Arrays.asList(parent);

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		assertDependencies(expected, actual);
	}

	public void testOneSees() throws Exception {

		final IRodinFile parent = createContext(project, C1_NAME, EMPTY_CONTEXT);

		final IMachineFile child = createMachine(project, M2_NAME, MCH_1SEES);

		final List<IRodinFile> expected = Arrays.asList(parent);

		final MachineIndexer indexer = new MachineIndexer();

		final IRodinFile[] actual = indexer.getDependencies(child);

		assertDependencies(expected, actual);
	}

	/**
	 * @throws Exception
	 */
	public void testFileDoesNotExist() throws Exception {

		final IMachineFile child = createMachine(project, M2_NAME, MCH_1REFINES);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.getDependencies(child);
	}

	public void testBadFileType() throws Exception {
		final String CST_1DECL_1REF_THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_THM);

		final MachineIndexer indexer = new MachineIndexer();

		try {
			indexer.getDependencies(context);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// missing closing " after internal_element1 in variable node
		final String MALFORMED_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1 org.eventb.core.identifier=\"var1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
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
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				MCH_1REFINES_NO_TARGET_ATT);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.getDependencies(machine);
	}

}
