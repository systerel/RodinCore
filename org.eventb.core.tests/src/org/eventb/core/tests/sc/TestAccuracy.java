/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAccuracy extends BasicSCTest {

	/**
	 * erroneous axioms should make an sc context inaccurate
	 */
	public void testAcc_01() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("A"), makeSList("x∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
		isNotAccurate(con.getSCContextFile());
	}

	/**
	 * erroneous theorems should make an sc context inaccurate
	 */
	public void testAcc_02() throws Exception {
		IContextFile con = createContext("con");

		addTheorems(con, makeSList("T"), makeSList("x∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
		isNotAccurate(con.getSCContextFile());
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 */
	public void testAcc_03() throws Exception {
		IContextFile abs = createContext("abs");
		
		addTheorems(abs, makeSList("T"), makeSList("x∈ℕ"));
		
		abs.save(null, true);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		
		addTheorems(con, makeSList("X"), makeSList("1<0"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCContextFile());
	}

	/**
	 * erroneous invariants should make an sc machine inaccurate
	 */
	public void testAcc_04() throws Exception {
		IMachineFile con = createMachine("con");

		addInvariants(con, makeSList("I"), makeSList("x∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
		isNotAccurate(con.getSCMachineFile());
	}

	/**
	 * erroneous theorems should make an sc machine inaccurate
	 */
	public void testAcc_05() throws Exception {
		IMachineFile con = createMachine("con");

		addTheorems(con, makeSList("T"), makeSList("x∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();
		
		isNotAccurate(con.getSCMachineFile());
	}

	/**
	 * inaccurate seen contexts should make an sc machine inaccurate
	 */
	public void testAcc_06() throws Exception {
		IContextFile abs = createContext("abs");
		
		addTheorems(abs, makeSList("T"), makeSList("x∈ℕ"));
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineSees(con, "abs");
		
		addTheorems(con, makeSList("X"), makeSList("1<0"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCMachineFile());
	}

	/**
	 * inaccurate abstract machines should make an sc machine inaccurate
	 */
	public void testAcc_07() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addTheorems(abs, makeSList("T"), makeSList("x∈ℕ"));
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");
		
		addTheorems(con, makeSList("X"), makeSList("1<0"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCMachineFile());
	}

	/**
	 * erroneous guards should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_08() throws Exception {
		IMachineFile con = createMachine("con");

		addInitialisation(con, makeSList());
		addEvent(con, "evt", makeSList(), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		con.save(null, true);
		
		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
	}

	/**
	 * erroneous theorems should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_09() throws Exception {
		// TODO implement test: erroneous theorems should make an sc event inaccurate
	}

	/**
	 * erroneous needed witnesses should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_10() throws Exception {
		IMachineFile abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		IEvent evt = addEvent(con, "evt", makeSList("y"), 
				makeSList("G"), makeSList("y∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("y"));
		
		con.save(null, true);

		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
	}

	/**
	 * missing needed witnesses should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_11() throws Exception {
		IMachineFile abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		IEvent evt = addEvent(con, "evt", makeSList("y"), 
				makeSList("G"), makeSList("y∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		con.save(null, true);

		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
	}

	/**
	 * erroneous actions should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_12() throws Exception {
		IMachineFile con = createMachine("con");

		addInitialisation(con, makeSList());
		addEvent(con, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x≔0"));
		
		con.save(null, true);
		
		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
	}

	/**
	 * missing action in initialisation should make the initialisation inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_13() throws Exception {
		IMachineFile con = createMachine("con");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"));
		addInitialisation(con);
		
		con.save(null, true);
		
		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[0]);
	}

	/**
	 * inherited events inherit accuracy
	 */
	public void testAcc_14() throws Exception {
		IMachineFile abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", makeSList(), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		addInheritedEvent(con, "evt");
		addInheritedEvent(con, "fvt");
	
		con.save(null, true);

		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isAccurate(con.getSCMachineFile().getSCEvents()[1]);
		isNotAccurate(con.getSCMachineFile().getSCEvents()[2]);
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 * (it is suffiecient that some abstract context is inaccurate for the
	 * sc context to be inaccurate)
	 */
	public void testAcc_15() throws Exception {
		IContextFile abs = createContext("abs");
		
		addTheorems(abs, makeSList("T"), makeSList("1<0"));
		
		abs.save(null, true);
		
		IContextFile bbs = createContext("bbs");
		
		addTheorems(bbs, makeSList("T"), makeSList("x∈ℕ"));
		
		bbs.save(null, true);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs");
		addContextExtends(con, "bbs");
		
		addTheorems(con, makeSList("X"), makeSList("1<0"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCContextFile());
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 * (it is suffiecient that some abstract context is inaccurate for the
	 * sc context to be inaccurate, also if automatically added by the static checker)
	 */
	public void testAcc_16() throws Exception {
		IContextFile abs = createContext("abs");
		
		addTheorems(abs, makeSList("T"), makeSList("x∈ℕ"));
		
		abs.save(null, true);
		
		IContextFile bbs = createContext("bbs");
		addContextExtends(bbs, "abs");
	
		addTheorems(bbs, makeSList("T"), makeSList("1<0"));
		
		bbs.save(null, true);
		
		IContextFile con = createContext("con");
		addContextExtends(con, "bbs");
		
		addTheorems(con, makeSList("X"), makeSList("1<0"));
		
		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCContextFile());
	}

	/**
	 * faulty variant should make an anticipated or convergent sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_17() throws Exception {
		IMachineFile con = createMachine("con");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"));
		addInitialisation(con);
		IEvent evt = addEvent(con, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setConvergent(evt);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setAnticipated(fvt);
		addVariant(con, "x");
	
		con.save(null, true);
		
		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
		isAccurate(con.getSCMachineFile().getSCEvents()[2]);
	}

	/**
	 * missing variant should make an anticipated or convergent sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_18() throws Exception {
		IMachineFile con = createMachine("con");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"));
		addInitialisation(con);
		IEvent evt = addEvent(con, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setConvergent(evt);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setAnticipated(fvt);
	
		con.save(null, true);
		
		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
		isAccurate(con.getSCMachineFile().getSCEvents()[2]);
	}

	/**
	 * faulty inherited convergence should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_19() throws Exception {
		IMachineFile abs = createMachine("abs");

		addInitialisation(abs);
		IEvent evt = addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setOrdinary(evt);
		addVariant(abs, "1");
		
		abs.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "abs");

		addInitialisation(con);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(fvt, "evt");
		setConvergent(fvt);
	
		con.save(null, true);

		runBuilder();
		
		isAccurate(con.getSCMachineFile());
		isNotAccurate(con.getSCMachineFile().getSCEvents()[1]);
	}

}
