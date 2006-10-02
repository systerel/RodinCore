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
public class TestSeesContext extends BasicTest {
	
	public void testSeesContext_0() throws Exception {
		IContextFile con = createContext("con");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addCarrierSets(con, makeSList("S1"));
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		con.save(null, true);
		
		runSC(con);
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1=C1"));

		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
		containsConstants(contexts[0], "C1");
		
		containsAxioms(contexts[0], typeEnvironment, makeSList("A1", "A2"), makeSList("C1∈S1", "1∈ℕ"));
		
		containsVariables(file, "V1");
		
		typeEnvironment.addName("V1", factory.makeGivenType("S1"));
		
		containsInvariants(file, typeEnvironment, makeSList("I1"), makeSList("V1=C1"));

	}
	

}
