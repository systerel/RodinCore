/*******************************************************************************
 * Copyright (c) 2009, 2012 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestPOFilter extends EventBPOTest {
	
	private static final String ORG_EVENTB_CORE_TESTS_FILTER = "org.eventb.core.tests.filter";

	/**
	 * stop machine theorem POs from being generated
	 */
	@Test
	public void test_01_filterTheorems() throws Exception {
		IPORoot po = createMachineWithTheorem(false);
		
		getSequent(po, "T1/THM");
		getSequent(po, "T2/THM");
		getSequent(po, "T2/WD");
		
		po = createMachineWithTheorem(true);
		
		noSequent(po, "T1/THM");
		noSequent(po, "T2/THM");
		getSequent(po, "T2/WD");
	}

	private IPORoot createMachineWithTheorem(boolean filter) throws CoreException {
		IMachineRoot mac = createMachine("mac");
		if (filter)
			mac.setConfiguration(ORG_EVENTB_CORE_TESTS_FILTER, null);

		addInvariants(mac, makeSList("T1", "T2"), makeSList("∀x·x>1", "∃x·1÷x>0"), true, true);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		return mac.getPORoot();
	}

	/**
	 * stop action feasibility POs from being generated
	 */
	@Test
	public void test_02_filterActionFIS() throws Exception {
		IPORoot po = createMachineWithEventAction(false);
		
		getSequent(po, "evt/A1/FIS");
		getSequent(po, "evt/A1/WD");
		getSequent(po, "evt/I1/INV");
		
		po = createMachineWithEventAction(true);
		
		noSequent(po, "evt/A1/FIS");
		getSequent(po, "evt/A1/WD");
		getSequent(po, "evt/I1/INV");
	}

	private IPORoot createMachineWithEventAction(boolean filter) throws CoreException {
		IMachineRoot mac = createMachine("mac");
		if (filter)
			mac.setConfiguration(ORG_EVENTB_CORE_TESTS_FILTER, null);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1 :∣ V1'÷V1>0"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		return mac.getPORoot();
	}

}
