/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestInvariantsAndTheorems extends GenericPredicateTest<IMachineFile, ISCMachineFile> {
		
	/**
	 * propagation of carrier set types, partial typing
	 */
	public void testInvariantsAndTheorems_05_carrierSetType() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ∪S1", "V1∈S1"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, typeEnvironment, makeSList("I2"), makeSList("V1∈S1"));
		
		hasMarker(mac.getInvariants()[0]);
		
	}
	
	/**
	 * more on partial typing (more complex set up)
	 */
	public void testInvariantsAndTheorems_06_partialTyping() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.save(null, true);
		
		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		IMachineFile mac = createMachine("mac");
		addMachineSees(mac, "con");
		addVariables(mac, "V1");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4"), 
				makeSList("V1=V1", "V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsInvariants(file, typeEnvironment, 
				makeSList("I2", "I3", "I4"), 
				makeSList("V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"));
		
		hasMarker(mac.getInvariants()[0]);
	}

	@Override
	protected IGenericSCTest<IMachineFile, ISCMachineFile> newGeneric() {
		return new GenericMachineSCTest(this);
	}
	
}
