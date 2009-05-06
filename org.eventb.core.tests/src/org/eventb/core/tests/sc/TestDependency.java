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

/**
 * @author Stefan Hallerstede
 *
 */
public class TestDependency extends BasicSCTestWithFwdConfig {

	/**
	 * markers in dependent files should be deleted
	 */
	public void testDep_01_checkMarkersDeleted() throws Exception {
		IContextRoot con = createContext("con");

		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = (IMachineRoot) createMachine("mac");
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"), true);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilder();
		
		containsMarkers(mac, true);
		
		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(mac, false);

	}
	
	public void testDep_02_nonexistentAbstractContext() throws Exception {
		IContextRoot con = createContext("con");
		
		addContextExtends(con, "abs");

		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[0]);
		
		IContextRoot abs = createContext("abs");
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasNotMarker(con.getExtendsClauses()[0]);
	}
	
	public void testDep_03_nonexistentAbstractMachine() throws Exception {
		IMachineRoot con = createMachine("con");
		addMachineRefines(con, "abs");

		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getRefinesClauses()[0]);
		
		IMachineRoot abs = createMachine("abs");
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasNotMarker(con.getRefinesClauses()[0]);		
	}
	
	public void testDep_03_nonexistentSeenContext() throws Exception {
		IMachineRoot con = createMachine("con");
		addMachineSees(con, "abs");

		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[0]);
		
		IContextRoot abs = createContext("abs");
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasNotMarker(con.getSeesClauses()[0]);
	}

}
