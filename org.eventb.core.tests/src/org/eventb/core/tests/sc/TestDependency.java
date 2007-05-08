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

/**
 * @author Stefan Hallerstede
 *
 */
public class TestDependency extends BasicSCTest {

	/**
	 * markers in dependent files should be deleted
	 */
	public void testDep_01_checkMarkersDeleted() throws Exception {
		IContextFile con = createContext("con");

		con.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"));

		mac.save(null, true);
		
		runBuilder();
		
		containsMarkers(mac, true);
		
		addCarrierSets(con, makeSList("S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(mac, false);

	}
	
	public void testDep_02_nonexistentAbstractContext() throws Exception {
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");

		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[0]);
		
		IContextFile abs = createContext("abs");
		
		abs.save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getExtendsClauses()[0]);
	}
	
	public void testDep_03_nonexistentAbstractMachine() throws Exception {
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");

		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getRefinesClauses()[0]);
		
		IMachineFile abs = createMachine("abs");
		
		abs.save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getRefinesClauses()[0]);		
	}
	
	public void testDep_03_nonexistentSeenContext() throws Exception {
		IMachineFile con = createMachine("con");
		addMachineSees(con, "abs");

		con.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[0]);
		
		IContextFile abs = createContext("abs");
		
		abs.save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getSeesClauses()[0]);
	}

}
