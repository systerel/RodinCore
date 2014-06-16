/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
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
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈S1"), true);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilderIssuesSomeMarkers();
		
		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
	}
	
	@Test
	public void testDep_02_nonexistentAbstractContext() throws Exception {
		IContextRoot con = createContext("ctx");
		
		addContextExtends(con, "abs");

		saveRodinFileOf(con);
		
		runBuilderIssuesSomeMarkers();
		
		IContextRoot abs = createContext("abs");
		
		saveRodinFileOf(abs);
		
		runBuilderCheck();
	}
	
	@Test
	public void testDep_03_nonexistentAbstractMachine() throws Exception {
		IMachineRoot con = createMachine("ctx");
		addMachineRefines(con, "abs");
		addInitialisation(con);

		saveRodinFileOf(con);
		
		runBuilderIssuesSomeMarkers();
		
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		
		saveRodinFileOf(abs);
		
		runBuilderCheck();
	}
	
	@Test
	public void testDep_04_nonexistentSeenContext() throws Exception {
		IMachineRoot con = createMachine("ctx");
		addMachineSees(con, "abs");
		addInitialisation(con);

		saveRodinFileOf(con);
		
		runBuilderIssuesSomeMarkers();
		
		IContextRoot abs = createContext("abs");
		
		saveRodinFileOf(abs);
		
		runBuilderCheck();
	}

}
