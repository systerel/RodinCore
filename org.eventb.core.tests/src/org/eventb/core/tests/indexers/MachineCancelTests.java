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
import static org.eventb.core.tests.indexers.CancelBridgeStub.*;

import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.MachineIndexer;
import org.rodinp.core.indexer.IDeclaration;

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

	public void testCancelImmediately() throws Exception {
		final IMachineRoot context = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				VAR_1DECL);

		final CancelBridgeStub tk = new CancelBridgeStub(NO_LIMIT, NO_LIMIT,
				NO_LIMIT, true, context);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumExp(0);
	}

	public void testCancelAfterImports() throws Exception {
		final IContextRoot exporter = ResourceUtils.createContext(rodinProject, EXPORTER,
				CST_1DECL);
		final IConstant cstExp = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp = OccUtils.newDecl(cstExp, CST1);

		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject, IMPORTER,
				VAR_1DECL_1REF_INV);

		final CancelBridgeStub tk = new CancelBridgeStub(NO_LIMIT, NO_LIMIT,
				1, false, importer, declCstExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumOcc(0);
	}

	public void testCancelAfterDecl() throws Exception {
		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				VAR_1DECL_1REF_INV);

		final CancelBridgeStub tk = new CancelBridgeStub(1, NO_LIMIT,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only the DECLARATION occurrence
		tk.assertNumOcc(1);
	}

	public void testCancelInPredicates() throws Exception {
		final String VAR_1DECL_2REF_2INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
				+ "<org.eventb.core.variable"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"inv1\""
				+ "		org.eventb.core.predicate=\"var1 = 0\""
				+ " 	org.eventb.core.theorem=\"false\"/>"	
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element2\""
				+ "		org.eventb.core.label=\"inv2\""
				+ "		org.eventb.core.predicate=\"var1 = 1\""
				+ " 	org.eventb.core.theorem=\"false\"/>"	
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				VAR_1DECL_2REF_2INV);

		final CancelBridgeStub tk = new CancelBridgeStub(NO_LIMIT, 2,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

	public void testCancelInExpressions() throws Exception {
		final String VAR_1DECL_2REF_2VRT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
				+ "<org.eventb.core.variable"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.variant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.expression=\"1 + var1 ∗ 2\"/>"
				+ "<org.eventb.core.variant"
				+ "		name=\"internal_element2\""
				+ "		org.eventb.core.expression=\"5 − var1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
				VAR_1DECL_2REF_2VRT);

		final CancelBridgeStub tk = new CancelBridgeStub(NO_LIMIT, 2,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

	public void testCancelEventsInActions() throws Exception {
		final String VAR_4OCC = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
				+ "<org.eventb.core.refinesMachine name=\"internal_element1\" org.eventb.core.target=\"exporter\"/>"
				+ "		<org.eventb.core.variable"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.identifier=\"var1\"/>"
				+ "		<org.eventb.core.event"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.convergence=\"0\""
				+ "				org.eventb.core.extended=\"true\""
				+ "				org.eventb.core.label=\"evt1\">"
				+ "				<org.eventb.core.action"
				+ "						name=\"internal_element1\""
				+ "						org.eventb.core.assignment=\"var1 ≔ 1\""
				+ "						org.eventb.core.label=\"act1\"/>"
				+ "				<org.eventb.core.action"
				+ "						name=\"internal_element2\""
				+ "						org.eventb.core.assignment=\"var1 ≔ var1 + 1\""
				+ "						org.eventb.core.label=\"act2\"/>"
				+ "		</org.eventb.core.event>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine = ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_4OCC);

		final CancelBridgeStub tk = new CancelBridgeStub(NO_LIMIT, 2,
				NO_LIMIT, false, machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 ASSIGNMENT occurrences
		tk.assertNumOcc(2);
	}

}
