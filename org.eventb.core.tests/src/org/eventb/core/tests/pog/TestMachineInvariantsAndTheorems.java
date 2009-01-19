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
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineInvariantsAndTheorems extends GenericPredicateTest<IMachineRoot> {
	
	/*
	 * proper creation of hypothesis from seen
	 */
	public void testMachine_01_seen() throws Exception {
		IContextRoot ctx = createContext("ctx");
		
		addTheorems(ctx, makeSList("N0"), makeSList("2>9"));
		
		saveRodinFileOf(ctx);
		
		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"));
		addTheorems(mac, makeSList("T1"), makeSList("1<0"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}
	
	/*
	 * proper creation of hypothesis from seen and refined
	 */
	public void testMachine_02_seenAndRefined() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addTheorems(abs, makeSList("T0"), makeSList("5>9"));	
		
		saveRodinFileOf(abs);

		IContextRoot ctx = createContext("ctx");
		
		addTheorems(ctx, makeSList("N0"), makeSList("2>9"));
		
		saveRodinFileOf(ctx);
		

		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"));
		addTheorems(mac, makeSList("T1"), makeSList("1<0"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "5>9", "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	@Override
	protected IGenericPOTest<IMachineRoot> newGeneric() {
		return new GenericMachinePOTest(this);
	}

}
