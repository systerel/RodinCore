/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class TestAutomatic extends BuilderTest {;
	
	protected void runBuilder() throws CoreException {
		rodinProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	private IContext createContextOne() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.buc", true, null);
		addCarrierSets(rodinFile, makeList("S1", "S2"));
		addConstants(rodinFile, makeList("C1", "C2", "C3", "F1"));
		addAxioms(rodinFile, makeList("A1", "A2", "A3", "A4"), makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		addTheorems(rodinFile, makeList("T1"), makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return (IContext) rodinFile;
	}

	public final void testContext1() throws Exception {
		createContextOne();
		runBuilder();
	}
	
	private IMachine createMachineOne(boolean sees) throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("one.bum", true, null);
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
		return (IMachine) rodinFile;
	}

	private IContext createContextTwo() throws RodinDBException {
		IRodinFile rodinFile = rodinProject.createRodinFile("two.buc", true, null);
		addCarrierSets(rodinFile, makeList("S1", "S2"));
		addConstants(rodinFile, makeList("C1", "C2", "C3", "F1"));
		addAxioms(rodinFile, makeList("A1", "A2", "A3", "A4"), makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		addTheorems(rodinFile, makeList("T1"), makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return (IContext) rodinFile;
	}
	public final void testMachine1() throws Exception {
		createMachineOne(true);
		createContextTwo();
		runBuilder();
	}
	
}
