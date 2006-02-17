/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;


import org.eventb.core.IContext;
import org.eventb.core.ISCContext;
import org.rodinp.core.RodinDBException;

public class TestContextSC_1 extends BuilderTest {
	
	/*
	 * Test method for 'org.eventb.internal.core.protosc.ContextSC.run()'
	 * This test only checks whether the database works correctly
	 * with the context static checker
	 */
	public void testRun() throws Exception {
		IContext context = createContextOne();
		ISCContext scContext = runSC(context);
		assertTrue("Checked context not produced", scContext.exists());
	}
	
	private IContext createContextOne() throws RodinDBException {
		IContext rodinFile = createContext("one");
		addCarrierSets(rodinFile, makeList("S1", "S2"));
		addConstants(rodinFile, makeList("C1", "C2", "C3", "F1"));
		addAxioms(rodinFile, makeList("A1", "A2", "A3", "A4"), makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		addTheorems(rodinFile, makeList("T1"), makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return rodinFile;
	}

}
