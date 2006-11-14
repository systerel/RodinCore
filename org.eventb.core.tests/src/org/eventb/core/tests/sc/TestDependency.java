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
public class TestDependency extends BasicTest {

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

}
