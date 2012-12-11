/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestInvariantsAndTheorems extends GenericPredicateTest<IMachineRoot, ISCMachineRoot> {
		
	/**
	 * propagation of carrier set types, partial typing
	 */
	public void testInvariantsAndTheorems_05_carrierSetType() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);
		
		runBuilder();

		IMachineRoot mac = createMachine("mac");
		
		ITypeEnvironmentBuilder typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addMachineSees(mac, "ctx");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ∪S1", "V1∈S1"), false, false);
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, makeSList("I2"), makeSList("V1∈S1"), false);
		
		hasMarker(mac.getInvariants()[0]);
		
	}
	
	/**
	 * more on partial typing (more complex set up)
	 */
	public void testInvariantsAndTheorems_06_partialTyping() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);
		
		runBuilder();

		ITypeEnvironmentBuilder typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		IMachineRoot mac = createMachine("mac");
		addMachineSees(mac, "ctx");
		addVariables(mac, "V1");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4"), 
				makeSList("V1=V1", "V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"),
				false, false, false, false);
	
		saveRodinFileOf(mac);
		
		runBuilder();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, 
				makeSList("I2", "I3", "I4"), 
				makeSList("V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"),
				false, false, false);
		
		hasMarker(mac.getInvariants()[0]);
	}

	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}
	
}
