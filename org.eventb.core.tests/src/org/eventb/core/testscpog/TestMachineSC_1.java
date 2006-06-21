/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.RodinDBException;

public class TestMachineSC_1 extends BuilderTest {

	/*
	 * Test method for 'org.eventb.internal.core.protosc.ContextSC.run()'
	 * This test only checks whether the database works correctly
	 * with the context static checker
	 */
	public void testRunWithoutSees() throws Exception {
		IMachineFile machine = createMachineOne(false);
		ISCMachineFile scMachine = runSC(machine);
		assertTrue("Checked machine not produced", scMachine.exists());
	}
	
	/*
	 * Test method for 'org.eventb.internal.core.protosc.ContextSC.run()'
	 * This test only checks whether the database works correctly
	 * with the context static checker
	 */
	public void testRunWithSees() throws Exception {
		IContextFile context = createContextTwo();
		IMachineFile machine = createMachineOne(true);

		ISCContextFile scContext = runSC(context);
		assertTrue("Checked context not produced", scContext.exists());
		ISCMachineFile scMachine = runSC(machine);
		assertTrue("Checked machine not produced", scMachine.exists());
	}
	
	private IMachineFile createMachineOne(boolean sees) throws RodinDBException {
		IMachineFile rodinFile = createMachine("one");
		if(sees)
			addSees(rodinFile, "two");
		addVariables(rodinFile, makeList("V1", "V2"));
		addInvariants(rodinFile, makeList("I1", "I2"), makeList("V1∈ℕ", "V2>V1"));
		addTheorems(rodinFile, makeList("T2"), makeList("1 = V2+V1"), null);
		addEvent(rodinFile, "E1", 
				makeList("L1"), 
				makeList("G1", "G2"), 
				makeList("V1=V2", "L1>V1"), 
				makeList("V1≔1", "V2≔V1+L1"));
		rodinFile.save(null, true);
		return rodinFile;
	}

	private IContextFile createContextTwo() throws RodinDBException {
		IContextFile rodinFile = createContext("two");
		addCarrierSets(rodinFile, makeList("S1", "S2"));
		addConstants(rodinFile, makeList("C1", "C2", "C3", "F1"));
		addAxioms(rodinFile, makeList("A1", "A2", "A3", "A4"), makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		addTheorems(rodinFile, makeList("T1"), makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return rodinFile;
	}

}
