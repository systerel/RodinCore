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
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestDependency extends BasicSCTestWithFwdConfig {

	/**
	 * markers in dependent files should be deleted
	 */
	@Test
	public void testDep_01_checkMarkersDeleted() throws Exception {
		IContextRoot con = createContext("ctx");

		saveRodinFileOf(con);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

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
	
	@Test
	public void testDep_02_nonexistentAbstractContext() throws Exception {
		IContextRoot con = createContext("ctx");
		
		addContextExtends(con, "abs");

		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[0]);
		
		IContextRoot abs = createContext("abs");
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasNotMarker(con.getExtendsClauses()[0]);
	}
	
	@Test
	public void testDep_03_nonexistentAbstractMachine() throws Exception {
		IMachineRoot con = createMachine("ctx");
		addMachineRefines(con, "abs");

		saveRodinFileOf(con);
		
		runBuilder();
		
		hasMarker(con.getRefinesClauses()[0]);
		
		IMachineRoot abs = createMachine("abs");
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasNotMarker(con.getRefinesClauses()[0]);		
	}
	
	@Test
	public void testDep_03_nonexistentSeenContext() throws Exception {
		IMachineRoot con = createMachine("ctx");
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
