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
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends BasicSCTest {
	
	public void testMachineRefines_0() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "V1");
	
		con.save(null, true);
		
		runBuilder();

		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"));

		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		containsVariables(file, "V1", "V2");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V2", factory.makeIntegerType());
	
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"));
		
		getInternalContexts(file, 0);

	}
	
	public void testMachineRefines_1() throws Exception {
		IContextFile con =  createContext("con");
		addCarrierSets(con, "S1");
	
		con.save(null, true);
		
		runBuilder();

		IMachineFile abs = createMachine("abs");
		
		addMachineSees(abs, "con");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");

	}
	
	public void testMachineRefines_2() throws Exception {
		IContextFile con =  createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.save(null, true);
		
		runBuilder();

		IMachineFile abs = createMachine("abs");
		
		addMachineSees(abs, "con");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");

		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}
	
	public void testMachineRefines_3() throws Exception {
		IContextFile con =  createContext("con");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.save(null, true);
		
		runBuilder();

		IMachineFile abs = createMachine("abs");
		
		addMachineSees(abs, "con");
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));

		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}
	
	public void testMachineRefines_4() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2=V1+1"));

		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V2", factory.makeIntegerType());
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		containsVariables(file, "V1", "V2");
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2=V1+1"));
	}

}
