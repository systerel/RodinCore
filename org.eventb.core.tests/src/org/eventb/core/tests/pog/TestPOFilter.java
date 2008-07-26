/*******************************************************************************
 * Copyright (c) 2008 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestPOFilter extends EventBPOTest {
	
	private static final String ORG_EVENTB_CORE_TESTS_FILTER = "org.eventb.core.tests.filter";

	/**
	 * stop machine theorem POs from being generated
	 */
	public void test_01_filterTheorems() throws Exception {
		IPOFile po = createMachineWithTheorem(false);
		
		getSequent(po, "T1/THM");
		getSequent(po, "T2/THM");
		getSequent(po, "T2/WD");
		
		po = createMachineWithTheorem(true);
		
		noSequent(po, "T1/THM");
		noSequent(po, "T2/THM");
		getSequent(po, "T2/WD");
	}

	private IPOFile createMachineWithTheorem(boolean filter) throws CoreException {
		IMachineFile mac = createMachine("mac");
		if (filter)
			mac.setConfiguration(ORG_EVENTB_CORE_TESTS_FILTER, null);

		addTheorems(mac, makeSList("T1", "T2"), makeSList("∀x·x>1", "∃x·1÷x>0"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(mac);
		return po;
	}

	/**
	 * stop action feasibility POs from being generated
	 */
	public void test_02_filterActionFIS() throws Exception {
		IPOFile po = createMachineWithEventAction(false);
		
		getSequent(po, "evt/A1/FIS");
		getSequent(po, "evt/A1/WD");
		getSequent(po, "evt/I1/INV");
		
		po = createMachineWithEventAction(true);
				
		noSequent(po, "evt/A1/FIS");
		getSequent(po, "evt/A1/WD");
		getSequent(po, "evt/I1/INV");
	}

	private IPOFile createMachineWithEventAction(boolean filter) throws CoreException {
		IMachineFile mac = createMachine("mac");
		if (filter)
			mac.setConfiguration(ORG_EVENTB_CORE_TESTS_FILTER, null);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"));
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1 :∣ V1'÷V1>0"));
		
		mac.save(null, true);
		
		runBuilder();
		
		return getPOFile(mac);
	}

}
