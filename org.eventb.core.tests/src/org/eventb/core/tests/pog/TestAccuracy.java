/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAccuracy extends EventBPOTest {
	
	/**
	 * If a machine is inaccurate, then all invariant and theorem POs are inaccurate.
	 */
	public void testAcc_00() throws Exception {
		IMachineFile mac = createMachine("mac");
		addInvariants(mac, makeSList("H", "I", "J"), makeSList("0÷9>0", "p>0", "∀x·x÷9>0"));
		addTheorems(mac, makeSList("T"), makeSList("0>0"));
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		sequentIsNotAccurate(po, "H/WD");
		sequentIsNotAccurate(po, "J/WD");
		sequentIsNotAccurate(po, "T/THM");
	}
	
	/**
	 * If an event is inaccurate, then all POs generated from that event are inaccurate.
	 */
	public void testAcc_01() throws Exception {
		IMachineFile mac = createMachine("mac");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H", "I", "J"), makeSList("m>0", "0>0", "∀x·x÷9>0"));
		addInitialisation(mac, "m");
		addEvent(mac, "evt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		sequentIsAccurate(po, "J/WD");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If an abstract event is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_02() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If an abstract event in a merge is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_03() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("0<0"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", makeSList(), 
				makeSList("G"), makeSList("p<m"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt", "fvt");
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = mac.getPOFile();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsNotAccurate(po, "evt/A/FIS");
	}	
	
	/**
	 * If a context is inaccurate, then all axiom and theorem POs are inaccurate.
	 */
	public void testAcc_04() throws Exception {
		IContextFile ctx = createContext("ctx");
		addAxioms(ctx, makeSList("H", "I", "J"), makeSList("0÷9>0", "p>0", "∀x·x÷9>0"));
		addTheorems(ctx, makeSList("T"), makeSList("0>0"));
		ctx.save(null, true);
		
		runBuilder();
		
		IPOFile po = ctx.getPOFile();
		
		sequentIsNotAccurate(po, "H/WD");
		sequentIsNotAccurate(po, "J/WD");
		sequentIsNotAccurate(po, "T/THM");
	}
	
	/**
	 * If an abstract event in a merge is inaccurate, 
	 * then all POs generated from the concrete event are inaccurate.
	 */
	public void testAcc_05() throws Exception {
		IMachineFile aab = createMachine("aab");
		addEvent(aab, "evt", makeSList(), 
				makeSList("G"), makeSList("0<m"), 
				makeSList(), makeSList());
		aab.save(null, true);
		
		IMachineFile abs = createMachine("abs");
		addMachineRefines(abs, "aab");
		IEvent eet = addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("9÷9>0"), 
				makeSList(), makeSList());
		addEventRefines(eet, "evt");
		abs.save(null, true);
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "m");
		addInvariants(mac, makeSList("H"), makeSList("m>0"));
		addTheorems(mac, makeSList("T"), makeSList("9>9"));
		addInitialisation(mac, "m");
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("m:∣ m'>m"));
		addEventRefines(evt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		IPOFile po = abs.getPOFile();
		
		sequentIsNotAccurate(po, "evt/G/WD");
		
		po = mac.getPOFile();
		
		sequentIsAccurate(po, "T/THM");
		
		sequentIsAccurate(po, "evt/A/FIS");
	}	

}
