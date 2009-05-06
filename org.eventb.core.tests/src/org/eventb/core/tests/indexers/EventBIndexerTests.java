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

import org.eventb.core.tests.BuilderTest;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class EventBIndexerTests extends BuilderTest {

	protected static final String EVT1 = "evt1";
	protected static final String IMPORTER = "importer";
	protected static final String EXPORTER = "exporter";
	protected static final String VAR1 = "var1";
	protected static final String VAR2 = "var2";
	protected static final String PRM1 = "prm1";
	protected static final String CST1 = "cst1";

	protected static final String EMPTY_MACHINE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\"/>";

	public static final String EMPTY_CONTEXT =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\"/>";

	protected static final String CST_1DECL =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String CST_1DECL_1REF_AXM =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"axm1\""
					+ "		org.eventb.core.predicate=\"cst1 = 2\""
					+ " 	org.eventb.core.theorem=\"false\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String CST_1DECL_1REF_THM =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"thm1\""
					+ "		org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\""
					+ " 	org.eventb.core.theorem=\"true\"/>"
					+ "</org.eventb.core.contextFile>";

	protected static final String VAR_1DECL =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
					+ "<org.eventb.core.variable"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"var1\"/>"
					+ "</org.eventb.core.machineFile>";

	protected static final String VAR_1DECL_1REF_INV =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
					+ "<org.eventb.core.variable"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"var1\"/>"
					+ "<org.eventb.core.invariant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"inv1\""
					+ "		org.eventb.core.predicate=\"var1 = 1\""
					+ " 	org.eventb.core.theorem=\"false\"/>"
					+ "</org.eventb.core.machineFile>";
	
	protected static final String EVT_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<org.eventb.core.machineFile"
	+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
	+ "		version=\"5\">"
	+ "<org.eventb.core.event"
	+ "		name=\"internal_element1\""
	+ "		org.eventb.core.convergence=\"0\""
	+ "		org.eventb.core.extended=\"false\""
	+ "		org.eventb.core.label=\"evt1\"/>"
	+ "</org.eventb.core.machineFile>";
	
	protected static final String EVT_1REF_REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"5\">"
			+ "<org.eventb.core.refinesMachine"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"exporter\"/>"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\" org.eventb.core.extended=\"true\" org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.refinesEvent"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.target=\"evt1\"/>"
			+ "</org.eventb.core.event>"
			+ "</org.eventb.core.machineFile>";

	protected static final String PRM_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"5\">"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"false\""
			+ "		org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.parameter"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.identifier=\"prm1\"/>"
			+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

	protected static final String PRM_1DECL_1REF_GRD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"5\">"
			+ "<org.eventb.core.refinesMachine"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"exporter\"/>"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"true\""
			+ "		org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.refinesEvent"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.target=\"evt1\"/>"
			+ "		<org.eventb.core.parameter"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.identifier=\"prm1\"/>"
			+ "		<org.eventb.core.guard"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.label=\"grd1\""
			+ "				org.eventb.core.predicate=\"prm1 = 1\""
			+ "				org.eventb.core.theorem=\"false\"/>"
			+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";
	
	public EventBIndexerTests(String name) {
		super(name);
	}

}
