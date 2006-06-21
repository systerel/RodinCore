/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.rodinp.core.RodinDBException;

public class TestContextPOG_1 extends BuilderTest {

	/*
	 * Test method for 'org.eventb.internal.core.protopog.ContextPOG.run()'
	 */
	public final void testRun() throws Exception {
		ISCContextFile context = createContextOne();
		IPOFile poFile = runPOG(context);
		assertTrue("Proof obligations not produced", poFile.exists());
	}

	private ISCContextFile createContextOne() throws RodinDBException {
		ISCContextFile rodinFile = createSCContext("one");
		addInternalContext(rodinFile, "CONTEXT");
//		TestUtil.addIdentifiers(rodinFile,
//				TestUtil.makeList("S1", "S2", "C1", "C2", "C3", "F1"),
//				TestUtil.makeList("ℙ(S1)", "ℙ(S2)", "S1", "ℙ(S1∗S2)", "S2", "ℕ"));
		addSCCarrierSets(rodinFile, makeList("S1", "S2"), makeList("ℙ(S1)", "ℙ(S2)"));
		addSCConstants(rodinFile, makeList("C1", "F1", "C2", "C3"), makeList("S1", "ℙ(S1×S2)", "S2", "ℤ"));
		addAxioms(rodinFile, 
				makeList("A1", "A2", "A3", "A4"), 
				makeList("C1∈S1", "F1∈S1↔S2", "C2∈F1[{C1}]", "C3=1"), null);
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList("C3>0 ⇒ (∃ x · x ∈ ran(F1))"), null);
		rodinFile.save(null, true);
		return rodinFile;
	}
}
