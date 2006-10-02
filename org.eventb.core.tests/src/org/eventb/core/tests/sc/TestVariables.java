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

/**
 * @author Stefan Hallerstede
 *
 */
public class TestVariables extends BasicTest {
	
	public void testVariables_0() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, makeSList("V1"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file);
		
	}

	public void testVariables_1() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℤ"));
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "V1");
		
		numberOfInvariants(file, 1);

	}
	
	public void testVariables_2() throws Exception {
		IMachineFile mac = createMachine("mac");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℤ"));

		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file);
		
		numberOfInvariants(file, 0);
	}
	
	public void testVariables_3() throws Exception {
		IContextFile con = createContext("con");

		addCarrierSets(con, makeSList("S1"));
		
		con.save(null, true);
		
		runSC(con);
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"));

		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		
		containsVariables(file, "V1");
		
		numberOfInvariants(file, 1);

	}
	
	public void testVariables_4() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
		
		con.save(null, true);
		
		runSC(con);

		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("C1"));
		addInvariants(mac, makeSList("I1"), makeSList("C1∈ℕ"));

		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
		
		containsVariables(file);
		
		numberOfInvariants(file, 1);

	}
	
	public void testVariables_5() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V1"));

		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
				
		containsVariables(file, "V1");
		
		numberOfInvariants(file, 1);

	}

}
