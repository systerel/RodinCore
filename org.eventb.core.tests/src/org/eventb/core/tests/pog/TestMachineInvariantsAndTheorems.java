/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineInvariantsAndTheorems extends GenericPredicateTest<IMachineFile> {
	
	/*
	 * proper creation of hypothesis from seen
	 */
	public void testMachine_01_seen() throws Exception {
		IContextFile ctx = createContext("ctx");
		
		addTheorems(ctx, makeSList("N0"), makeSList("2>9"));
		
		ctx.save(null, true);
		
		IMachineFile mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"));
		addTheorems(mac, makeSList("T1"), makeSList("1<0"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(mac);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}
	
	/*
	 * proper creation of hypothesis from seen and refined
	 */
	public void testMachine_02_seenAndRefined() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addTheorems(abs, makeSList("T0"), makeSList("5>9"));	
		
		abs.save(null, true);

		IContextFile ctx = createContext("ctx");
		
		addTheorems(ctx, makeSList("N0"), makeSList("2>9"));
		
		ctx.save(null, true);
		
		IMachineFile mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"));
		addTheorems(mac, makeSList("T1"), makeSList("1<0"));
		
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(mac);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "5>9", "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

}
