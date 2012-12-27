/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariables extends GenericIdentTest<IMachineRoot, ISCMachineRoot> {
	
	
	
	/**
	 * check type propagation of carrier sets in seeing machine
	 */
	@Test
	public void testVariables_01() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"), false);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("S1=ℙ(S1); V1=S1",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈S1"), false);

		containsMarkers(mac, false);
	}
	
	/**
	 * name conflict of variable and seen constant: variable removed!
	 */
	@Test
	public void testVariables_02() throws Exception {
		IContextRoot con = createContext("ctx");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈BOOL"), false);
		
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("C1"));
		addInvariants(mac, makeSList("I1", "I2"), makeSList("C1∈ℕ", "C1=TRUE"), false, false);

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("C1=BOOL",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
		
		containsVariables(file);
		
		containsInvariants(file, environment, makeSList("I2"), makeSList("C1=TRUE"), false);

		hasMarker(mac.getVariables()[0]);
		hasMarker(mac.getInvariants()[0]);
	}
	
	/**
	 * variables and invariants are preserved in refinements
	 */
	@Test
	public void testVariables_03() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V1"));
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment("V1=ℤ",
				factory);

		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1");
		
		containsInvariants(file, environment, makeSList("I1"), makeSList("V1∈ℕ"), false, false);

		containsMarkers(mac, false);
	}

	/**
	 * A variable that has disappeared and reappears in a later refinement.
	 */
	@Test
	public void testVariables_04() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		addVariables(m0, "v1");
		addInvariants(m0, makeSList("I1"), makeSList("v1 ∈ ℕ"), true);
		addInitialisation(m0, "v1");
		saveRodinFileOf(m0);
		
		final IMachineRoot m1 = createMachine("m1");
		addMachineRefines(m1, "m0");
		addVariables(m1, "v2");
		addInvariants(m1, makeSList("I1"), makeSList("v2 ∈ ℕ"), true);
		addInitialisation(m1, "v2");
		saveRodinFileOf(m1);

		final IMachineRoot m2 = createMachine("m2");
		addMachineRefines(m2, "m1");
		addVariables(m2, "v1");
		addInvariants(m2, makeSList("I1"), makeSList("v1 ∈ ℕ"), true);
		addInitialisation(m2, "v1");
		saveRodinFileOf(m2);
		
		runBuilder();

		final ISCMachineRoot m0c = m0.getSCMachineRoot();
		containsVariables(m0c, "v1");
		containsMarkers(m0c, false);

		final ISCMachineRoot m1c = m1.getSCMachineRoot();
		containsVariables(m1c, "v1", "v2");
		forbiddenVariables(m1c, "v1");
		containsMarkers(m1c, false);
		
		final ISCMachineRoot m2c = m2.getSCMachineRoot();
		containsVariables(m2c, "v1", "v2");
		forbiddenVariables(m2c, "v1", "v2");
		containsMarkers(m2, true);
		
		ISCEvent[] events = getSCEvents(m2c, IEvent.INITIALISATION);
		containsActions(events[0], emptyEnv, makeSList(), makeSList());
		
		// TODO should also check that sc reports only that "v1" has disappeared
		// and can't be resurrected.
		hasMarker(m2.getVariables()[0]);
	}
	
	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}

}
