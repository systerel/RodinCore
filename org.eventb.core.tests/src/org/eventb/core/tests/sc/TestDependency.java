/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;

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

		con.getRodinFile().save(null, true);
		
		runBuilder();
		
		IMachineRoot mac = (IMachineRoot) createMachine("mac");
		IRodinFile macFile = mac.getRodinFile();
		
		addMachineSees(mac, "con");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"));

		macFile.save(null, true);
		
		runBuilder();
		
		containsMarkers(macFile, true);
		
		addCarrierSets(con, makeSList("S1"));
		
		con.getRodinFile().save(null, true);
		
		runBuilder();
		
		containsMarkers(macFile, false);

	}
	
	public void testDep_02_nonexistentAbstractContext() throws Exception {
		IContextRoot con = createContext("con");
		
		addContextExtends(con, "abs");

		con.getRodinFile().save(null, true);
		
		runBuilder();
		
		hasMarker(con.getExtendsClauses()[0]);
		
		IContextRoot abs = createContext("abs");
		
		abs.getRodinFile().save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getExtendsClauses()[0]);
	}
	
	public void testDep_03_nonexistentAbstractMachine() throws Exception {
		IMachineRoot con = createMachine("con");
		IRodinFile conFile = con.getRodinFile();
		addMachineRefines(con, "abs");

		conFile.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getRefinesClauses()[0]);
		
		IMachineRoot abs = createMachine("abs");
		
		abs.getRodinFile().save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getRefinesClauses()[0]);		
	}
	
	public void testDep_03_nonexistentSeenContext() throws Exception {
		IMachineRoot con = createMachine("con");
		IRodinFile conFile = con.getRodinFile();
		addMachineSees(con, "abs");

		conFile.save(null, true);
		
		runBuilder();
		
		hasMarker(con.getSeesClauses()[0]);
		
		IContextRoot abs = createContext("abs");
		
		abs.getRodinFile().save(null, true);
		
		runBuilder();
		
		hasNotMarker(con.getSeesClauses()[0]);
	}

}
