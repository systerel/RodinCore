/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariables extends GenericIdentTest<IMachineFile, ISCMachineFile> {
	
	
	
	/**
	 * check type propagation of carrier sets in seeing machine
	 */
	public void testVariables_01() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"));

		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addGivenSet("S1");
		environment.addName("V1", factory.makeGivenType("S1"));
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈S1"));

		containsMarkers(mac, false);
	}
	
	/**
	 * name conflict of variable and seen constant: variable removed!
	 */
	public void testVariables_02() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈BOOL"));
		
		con.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("C1"));
		addInvariants(mac, makeSList("I1", "I2"), makeSList("C1∈ℕ", "C1=TRUE"));

		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("C1", factory.makeBooleanType());
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
		
		containsVariables(file);
		
		containsInvariants(file, environment, makeSList("I2"), makeSList("C1=TRUE"));

	}
	
	/**
	 * variables and invariants are preserved in refinements
	 */
	public void testVariables_03() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V1"));

		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("V1", factory.makeIntegerType());

		ISCMachineFile file = mac.getSCMachineFile();
				
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈ℕ"));

		containsMarkers(mac, false);
	}

	/**
	 * A variable that has disappeared and reappears in a later refinement.
	 */
	public void testVariables_04() throws Exception {
		final IMachineFile m0 = createMachine("m0");
		addVariables(m0, "v1");
		addInvariants(m0, makeSList("I1"), makeSList("v1 ∈ ℕ"));
		addInitialisation(m0, "v1");
		m0.save(null, true);
		
		final IMachineFile m1 = createMachine("m1");
		addMachineRefines(m1, "m0");
		addVariables(m1, "v2");
		addInvariants(m1, makeSList("I1"), makeSList("v2 ∈ ℕ"));
		addInitialisation(m1, "v2");
		m1.save(null, true);

		final IMachineFile m2 = createMachine("m2");
		addMachineRefines(m2, "m1");
		addVariables(m2, "v1");
		addInvariants(m2, makeSList("I1"), makeSList("v1 ∈ ℕ"));
		addInitialisation(m2, "v1");
		m2.save(null, true);
		
		runBuilder();

		final ISCMachineFile m0c = m0.getSCMachineFile();
		containsVariables(m0c, "v1");
		containsMarkers(m0c, false);

		final ISCMachineFile m1c = m1.getSCMachineFile();
		containsVariables(m1c, "v1", "v2");
		forbiddenVariables(m1c, "v1");
		containsMarkers(m1c, false);
		
		final ISCMachineFile m2c = m2.getSCMachineFile();
		containsVariables(m2c, "v1", "v2");
		forbiddenVariables(m2c, "v1", "v2");
		containsMarkers(m2, true);
		
		ISCEvent[] events = getSCEvents(m2c, IEvent.INITIALISATION);
		containsActions(events[0], emptyEnv, makeSList(), makeSList());
		
		// TODO should also check that sc reports only that "v1" has disappeared
		// and can't be resurrected.
	}
	
	@Override
	protected IGenericSCTest<IMachineFile, ISCMachineFile> newGeneric() {
		return new GenericMachineSCTest(this);
	}

}
