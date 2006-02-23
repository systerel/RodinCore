/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IPOFile;
import org.eventb.core.ISCMachine;
import org.rodinp.core.RodinDBException;

public class TestMachinePOG_1 extends BuilderTest {
	
	/*
	 * Test method for 'org.eventb.internal.core.protopog.ContextPOG.run()'
	 */
	public final void testRun() throws Exception {
		ISCMachine machine = createMachineOne();
		IPOFile poFile = runPOG(machine);
		assertTrue("Proof obligations not produced", poFile.exists());
	}

	private ISCMachine createMachineOne() throws RodinDBException {
		ISCMachine rodinFile = createSCMachine("one");
		addOldAxioms(rodinFile, "AXIOMS");
		addOldTheorems(rodinFile, "THEOREMS");
		addSCVariables(rodinFile,
				makeList("V1", "V2"),
				makeList("ℤ", "ℤ"));
		addInvariants(rodinFile, 
				makeList("I1", "I2", "I3"), 
				makeList("V1∈ℤ", "V2∈ℤ", "V1+V2=0"));
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList("V1>0 ⇒ (∃ x · x ∈ 0‥V2)"), null);
		addEvent(rodinFile,
				"E1",
				makeList("T1", "T2"),
				makeList("G1", "G2"),
				makeList("T1∈ℕ", "T2∈ℕ"),
				makeList("V1≔1")
		);
		rodinFile.save(null, true);
		return rodinFile;
	}

}
