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

import static org.eventb.core.indexer.tests.CancelToolkitStub.*;
import static org.eventb.core.indexer.tests.ResourceUtils.*;

import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.indexer.MachineIndexer;
import org.rodinp.core.index.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineCancelTests extends EventBIndexerTests {

	/**
	 * @param name
	 */
	public MachineCancelTests(String name) {
		super(name);
	}

	private static final String VAR_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.variable"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"var1\"/>"
			+ "</org.eventb.core.machineFile>";

	private static final String VAR_1DECL_1REF_INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.variable"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"var1\"/>"
			+ "<org.eventb.core.invariant"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.label=\"inv1\""
			+ "		org.eventb.core.predicate=\"var1 = 1\"/>"
			+ "</org.eventb.core.machineFile>";

	public void testCancelImmediately() throws Exception {
		final IMachineFile context = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, NO_LIMIT,
				NO_LIMIT, true, context);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumExp(0);
	}

	public void testCancelAfterImports() throws Exception {
		final String CST_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
			+ "</org.eventb.core.contextFile>";

		final IContextFile exporter = createContext(project, EXPORTER,
				CST_1DECL);
		final IConstant cstExp = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp = OccUtils.newDecl(cstExp, CST1);

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1DECL_1REF_INV);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, NO_LIMIT,
				1, false, importer, declCstExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumOcc(0);
	}

	public void testCancelAfterDecl() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_INV);

		final CancelToolkitStub tk = new CancelToolkitStub(1, NO_LIMIT,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only the DECLARATION occurrence
		tk.assertNumOcc(1);
	}

	public void testCancelInPredicates() throws Exception {
		final String VAR_1DECL_3REF_2INV_1THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"inv1\""
				+ "		org.eventb.core.predicate=\"var1 = 0\"/>"
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element2\""
				+ "		org.eventb.core.label=\"inv2\""
				+ "		org.eventb.core.predicate=\"var1 = 1\"/>"
				+ "<org.eventb.core.theorem"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"thm1\""
				+ "		org.eventb.core.predicate=\"var1 = 2\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_3REF_2INV_1THM);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, 2,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

	public void testCancelInExpressions() throws Exception {
		final String VAR_1DECL_2REF_2VRT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "		<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "		<org.eventb.core.variable name=\"internal_element1\" org.eventb.core.identifier=\"var1\"/>"
				+ "		<org.eventb.core.variant name=\"internal_element1\" org.eventb.core.expression=\"1 + var1 ∗ 2\"/>"
				+ "		<org.eventb.core.variant name=\"internal_element2\" org.eventb.core.expression=\"5 − var1\"/>"
				+ "		</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_2REF_2VRT);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, 2,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

	// TODO maybe refine to event elements
	public void testCancelInEvents() throws Exception {

	}

}
