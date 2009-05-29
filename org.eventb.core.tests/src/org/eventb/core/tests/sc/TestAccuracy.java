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
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAccuracy extends BasicSCTestWithFwdConfig {

	/**
	 * erroneous axioms should make an sc context inaccurate
	 */
	public void testAcc_01() throws Exception {
		IContextRoot root = createContext("ctx");

		addAxioms(root, makeSList("A"), makeSList("x∈ℕ"), false);
		
		saveRodinFileOf(root);
		
		runBuilder();
		
		isNotAccurate(root.getSCContextRoot());
	}

	/**
	 * erroneous theorems should make an sc context inaccurate
	 */
	public void testAcc_02() throws Exception {
		IContextRoot root = createContext("ctx");

		addAxioms(root, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(root);
		
		runBuilder();
		
		isNotAccurate(root.getSCContextRoot());
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 */
	public void testAcc_03() throws Exception {
		IContextRoot abs = createContext("abs");

		addAxioms(abs, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		addAxioms(con, makeSList("X"), makeSList("1<0"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(abs.getSCContextRoot());
	}

	/**
	 * erroneous invariants should make an sc machine inaccurate
	 */
	public void testAcc_04() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addInvariants(con, makeSList("I"), makeSList("x∈ℕ"), false);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		isNotAccurate(con.getSCMachineRoot());
	}

	/**
	 * erroneous theorems should make an sc machine inaccurate
	 */
	public void testAcc_05() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addInvariants(con, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		isNotAccurate(con.getSCMachineRoot());
	}

	/**
	 * inaccurate seen contexts should make an sc machine inaccurate
	 */
	public void testAcc_06() throws Exception {
		IContextRoot abs = createContext("abs");
		
		addAxioms(abs, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineSees(con, "abs");
		addInitialisation(con);
		
		addInvariants(con, makeSList("X"), makeSList("1<0"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCMachineRoot());
	}

	/**
	 * inaccurate abstract machines should make an sc machine inaccurate
	 */
	public void testAcc_07() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		
		addInvariants(abs, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");
		addInitialisation(con);
		
		addInvariants(con, makeSList("X"), makeSList("1<0"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCMachineRoot());
	}

	/**
	 * erroneous guards should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_08() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addInitialisation(con, makeSList());
		addEvent(con, "evt", makeSList(), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * erroneous event theorems should make an sc event inaccurate,
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
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		IEvent evt = addEvent(con, "evt", makeSList("y"), 
				makeSList("G"), makeSList("y∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("x"), makeSList("y"));
		
		saveRodinFileOf(con);

		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * missing needed witnesses should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_11() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		IEvent evt = addEvent(con, "evt", makeSList("y"), 
				makeSList("G"), makeSList("y∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		saveRodinFileOf(con);

		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * erroneous actions should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_12() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addInitialisation(con, makeSList());
		addEvent(con, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x≔0"));
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * missing action in initialisation should make the initialisation inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_13() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"), true);
		addInitialisation(con);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[0]);
	}

	/**
	 * inherited events inherit accuracy
	 */
	public void testAcc_14() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs, makeSList());
		addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "fvt", makeSList(), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");

		addInitialisation(con, makeSList());
		IEvent evt = addExtendedEvent(con, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addExtendedEvent(con, "fvt");
		addEventRefines(fvt, "fvt");
	
		saveRodinFileOf(con);

		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isAccurate(con.getSCMachineRoot().getSCEvents()[1]);
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[2]);
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 * (it is suffiecient that some abstract context is inaccurate for the
	 * sc context to be inaccurate)
	 */
	public void testAcc_15() throws Exception {
		IContextRoot abs = createContext("abs");
		
		addAxioms(abs, makeSList("T"), makeSList("1<0"), true);
		
		saveRodinFileOf(abs);
		
		IContextRoot bbs = createContext("bbs");
		
		addAxioms(bbs, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(bbs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		addContextExtends(con, "bbs");
		
		addAxioms(con, makeSList("X"), makeSList("1<0"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(con.getSCContextRoot());
	}

	/**
	 * inaccurate abstract contexts should make an sc context inaccurate
	 * (it is suffiecient that some abstract context is inaccurate for the
	 * sc context to be inaccurate, also if automatically added by the static checker)
	 */
	public void testAcc_16() throws Exception {
		IContextRoot abs = createContext("abs");
		
		addAxioms(abs, makeSList("T"), makeSList("x∈ℕ"), true);
		
		saveRodinFileOf(abs);
		
		IContextRoot bbs = createContext("bbs");
		addContextExtends(bbs, "abs");
	
		addAxioms(bbs, makeSList("T"), makeSList("1<0"), true);
		
		saveRodinFileOf(bbs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "bbs");
		
		addAxioms(con, makeSList("X"), makeSList("1<0"), true);
		
		saveRodinFileOf(con);
		
		runBuilder();
		
		containsMarkers(con, false);
		isNotAccurate(abs.getSCContextRoot());
	}

	/**
	 * faulty variant should make an anticipated or convergent sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_17() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"), true);
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
	
		saveRodinFileOf(con);
		
		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
		isAccurate(con.getSCMachineRoot().getSCEvents()[2]);
	}

	/**
	 * missing variant should make an anticipated or convergent sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_18() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addVariables(con, "v");
		addInvariants(con, makeSList("I"), makeSList("v∈ℕ"), true);
		addInitialisation(con);
		IEvent evt = addEvent(con, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setConvergent(evt);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setAnticipated(fvt);
	
		saveRodinFileOf(con);
		
		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
		isAccurate(con.getSCMachineRoot().getSCEvents()[2]);
	}

	/**
	 * faulty refined convergence should make an sc event inaccurate,
	 * but not the sc machine
	 */
	public void testAcc_19() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		IEvent evt = addEvent(abs, "evt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		setOrdinary(evt);
		addVariant(abs, "1");
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");

		addInitialisation(con);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(fvt, "evt");
		setConvergent(fvt);
	
		saveRodinFileOf(con);

		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isNotAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * for non-inherited events:
	 * faulty abstract sc event but well-formed concrete sc event 
	 * should make the concrete sc event and the sc machine accurate
	 */
	public void testAcc_20() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		
		saveRodinFileOf(abs);
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");

		addInitialisation(con);
		IEvent fvt = addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEventRefines(fvt, "evt");
	
		saveRodinFileOf(con);

		runBuilder();
		
		isAccurate(con.getSCMachineRoot());
		isAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

	/**
	 * an inaccurate sc machine does not automatically make contained sc events inaccurate
	 */
	public void testAcc_21() throws Exception {
		IMachineRoot con = createMachine("cnc");

		addInvariants(con, makeSList("I"), makeSList("p>0"), true);
		addInitialisation(con);
		addEvent(con, "fvt", makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
	
		saveRodinFileOf(con);

		runBuilder();
		
		isNotAccurate(con.getSCMachineRoot());
		isAccurate(con.getSCMachineRoot().getSCEvents()[1]);
	}

}
