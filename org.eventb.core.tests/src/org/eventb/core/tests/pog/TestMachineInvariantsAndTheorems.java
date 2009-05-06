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
		
		addAxioms(ctx, makeSList("N0"), makeSList("2>9"), true);
		
		saveRodinFileOf(ctx);
		
		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"), false);
		addInvariants(mac, makeSList("T1"), makeSList("1<0"), true);
		
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
		
		addInvariants(abs, makeSList("T0"), makeSList("5>9"), true);	
		
		saveRodinFileOf(abs);

		IContextRoot ctx = createContext("ctx");
		
		addAxioms(ctx, makeSList("N0"), makeSList("2>9"), true);
		
		saveRodinFileOf(ctx);
		

		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addInvariants(mac, makeSList("N1"), makeSList("7<1"), false);
		addInvariants(mac, makeSList("T1"), makeSList("1<0"), true);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "5>9", "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	
	/*
	 * proof obligations for mixed list of invariants and theorems
	 */
	public void testMachine_03_mixedTheoremsAndInvariants() throws Exception {

		IMachineRoot mac = createMachine("mac");

		addMachineSees(mac, "ctx");
		addInvariant(mac, "N1", "7<max({1})", false);
		addInvariant(mac, "T1", "1<max({1})", true);
		addInvariant(mac, "N2", "8<max({1})", false);
		addInvariant(mac, "T2", "9<max({1})", true);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		noSequent(po, "N1/THM");
		getSequent(po, "N1/WD");
		getSequent(po, "T1/THM");
		getSequent(po, "T1/WD");
		noSequent(po, "N2/THM");
		getSequent(po, "N2/WD");
		getSequent(po, "T2/THM");
		getSequent(po, "T2/WD");
		
	}
	
	@Override
	protected IGenericPOTest<IMachineRoot> newGeneric() {
		return new GenericMachinePOTest(this);
	}

}
