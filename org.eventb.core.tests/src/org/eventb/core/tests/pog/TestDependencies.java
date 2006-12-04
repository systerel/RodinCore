/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestDependencies extends BasicPOTest {

	public void testDependencies_00_MachineRefinement() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈{1}"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I11"), makeSList("V1>V1"));
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		mac.save(null, true);
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I11/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈{1}");
		sequentHasGoal(sequent, typeEnvironment, "V1+1>V1+1");
		
		// check if abstract invariant propagates
		addInvariants(abs, makeSList("I2"), makeSList("V1∈{2}"));
		abs.save(null, true);
		runBuilder();
	
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈{1}", "V1∈{2}");	
		
	}

}
