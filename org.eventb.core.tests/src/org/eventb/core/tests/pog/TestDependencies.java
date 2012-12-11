/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironmentBuilder;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestDependencies extends EventBPOTest {

	public void testDependencies_00_MachineRefinement() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈{1}"), false);
		
		ITypeEnvironmentBuilder typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I11"), makeSList("V1>V1"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔V1+1"));
		
		saveRodinFileOf(mac);
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, "evt/I11/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈{1}");
		sequentHasGoal(sequent, typeEnvironment, "V1+1>V1+1");
		
		// check if abstract invariant propagates
		addInvariants(abs, makeSList("I2"), makeSList("V1∈{2}"), false);
		saveRodinFileOf(abs);
		runBuilder();
	
		sequentHasHypotheses(sequent, typeEnvironment, "V1∈{1}", "V1∈{2}");	
		
	}

}
