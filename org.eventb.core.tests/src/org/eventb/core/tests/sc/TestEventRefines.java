/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventRefines extends BasicSCTestWithFwdConfig {
	
	/*
	 * simple refines clause
	 */
	public void testEvents_00_refines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		
		containsMarkers(mac, false);
	}
	
	/*
	 * split refines clause
	 */
	public void testEvents_01_split2() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt1 = addEvent(mac, "evt1");
		addEventRefines(evt1, "evt");
		IEvent evt2 = addEvent(mac, "evt2");
		addEventRefines(evt2, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt1", "evt2");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
		
		containsMarkers(mac, false);
	}

	/*
	 * identically named local variables of abstract and cocrete events do correspond
	 */
	public void testEvents_02_localTypesCorrespond() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		containsVariables(events[1], "L1");
		
		containsMarkers(mac, false);
	}
	
	/*
	 * error if identically named local variables of abstract and concrete events do NOT correspond
	 */
	public void testEvents_03_localTypeConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0]);
		
		hasMarker(evt.getGuards()[0], null, GraphProblem.LocalVariableChangedTypeError, "L1", "ℙ(ℤ)", "ℤ");
		hasMarker(evt.getVariables()[0], null, GraphProblem.UntypedVariableError, "L1");
		
	}
	
	/*
	 * create default witness if local variable disappears
	 */
	public void testEvents_04_localDefaultWitness() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1", "L3"), 
				makeSList("G1", "G3"), makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("L1"), makeSList("L1∈L2"));
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", powIntType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L1","L3"), makeSList("L1∈L2", "⊤"));
		
		hasMarker(evt, null, GraphProblem.WitnessLabelMissingWarning, "L3");
		
	}
	
	/*
	 * global witness refering to local variable of abstract event
	 */
	public void testEvents_05_globalWitness() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		addEventWitnesses(init, makeSList("V1'"), makeSList("V1'=1"));
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=L2"));
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("L2", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		
		containsVariables(events[1], "L2");
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"), makeSList("V1'=L2"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * global witness refering to post value of global variable of concrete machine
	 */
	public void testEvents_06_globalWitness() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2⊆ℕ"));
		IEvent init = addInitialisation(mac, "V2");
		addEventWitnesses(init, makeSList("V1'"), makeSList("V1'=1"));
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), makeSList("A2"), makeSList("V2:∣V2'⊆ℕ"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'∈V2'"));
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2", powIntType);
		typeEnvironment.addName("V2'", powIntType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		
		containsVariables(events[1]);
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"), makeSList("V1'∈V2'"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * creation of local default witness
	 */
	public void testEvents_07_localTypesAndWitnesses() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1", "L3"), 
				makeSList("G1", "G3"), makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1", "L2"), 
				makeSList("G1", "G2"), makeSList("L1=1", "L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", powIntType);
		typeEnvironment.addName("L3", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L1", "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L3"), makeSList("⊤"));
		
		hasMarker(evt, null, GraphProblem.WitnessLabelMissingWarning, "L3");
		
	}
	
	/*
	 * split event into three events
	 */
	public void testEvents_08_split3() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		IEvent gvt = addEvent(mac, "gvt");
		addEventRefines(gvt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt", "gvt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
		refinesEvents(events[3], "evt");
		
		containsMarkers(mac, false);
	}

	/*
	 * merge two events into one
	 */
	public void testEvents_09_merge2() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt", "fvt");
		
		containsMarkers(mac, false);
	}
	
	/*
	 * inherit event
	 */
	public void testEvents_10_inherited() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		addInheritedEvent(mac, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		
		containsMarkers(mac, false);
	}

	/*
	 * conflict: inherited AND refined event
	 */
	public void testEvents_11_InheritedRefinesConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
		hasMarker(evt);
		hasMarker(fvt);
		
	}

	/*
	 * conflict: inherited AND split event
	 */
	public void testEvents_12_inheritedSplitConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
		hasMarker(evt);
		hasMarker(fvt);
		
	}	
	
	/*
	 * conflict: event merge more than once
	 */
	public void testEvents_13_mergeMergeConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		
		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
		hasMarker(evt);
		hasMarker(fvt);
		
	}	
	
	/*
	 * interit / refines / merge used together correctly
	 */
	public void testEvents_14_inheritedRefinesMergeOK() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");
		
		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "gvt");
		addEventRefines(fvt, "fvt");
		addInheritedEvent(mac, "hvt");
		addEvent(mac, "ivt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt", "hvt", "ivt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "fvt", "gvt");
		refinesEvents(events[3], "hvt");
		refinesEvents(events[4]);
		
		containsMarkers(mac, false);
	}	
	
	/*
	 * inherit / refines / merge used together in-correctly
	 */
	public void testEvents_15_inheritedRefinesMergeConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");
		
		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "hvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "hvt");
		addEventRefines(fvt, "fvt");
		IEvent hvt = addInheritedEvent(mac, "hvt");
		addEvent(mac, "ivt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "ivt");
		
		hasMarker(evt);
		hasMarker(fvt);
		hasMarker(hvt);
	}	
	
	/*
	 * initialisation implicitly refined
	 */
	public void testEvents_16_initialisationDefaultRefines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addEvent(mac, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);
		
		containsMarkers(mac, false);
		
	}
	
	/*
	 * initialisation cannot be explicitly refined
	 */
	public void testEvents_17_initialisationRefines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addEvent(mac, IEvent.INITIALISATION);
		addEventRefines(init, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
		hasMarker(init);
		
	}
	
	/*
	 * initialisation can be inherited
	 */
	public void testEvents_18_initialisationInherited() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);
		
		containsMarkers(mac, false);
	}
	
	/*
	 * no other event can refine the initialisation
	 */
	public void testEvents_19_initialisationNotRefined() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, IEvent.INITIALISATION);
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
		hasMarker(evt);
		
	}
	
	/*
	 * several refinement problems occuring together
	 */
	public void testEvents_20_multipleRefineProblems() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, IEvent.INITIALISATION);
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent hvt = addEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		addEventRefines(hvt, IEvent.INITIALISATION);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent gvt = addEvent(mac, "gvt");
		addEventRefines(gvt, "gvt");
		addEventRefines(gvt, "evt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
		hasMarker(hvt);
		hasMarker(evt);
		hasMarker(gvt);
		
	}
	
	/*
	 * identically named local variables of merged abstract events must have compatible types
	 */
	public void testEvents_21_mergeLocalAbstractTypesCorrespond() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addInitialisation(abs);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "gvt", 
				makeSList("x", "y"), makeSList("G1", "G2"), 
				makeSList("x∈ℕ","y∈BOOL"), 
				makeSList(), makeSList());
		addEvent(abs, "hvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", 
				makeSList("x", "y"), makeSList("G1", "G2"), 
				makeSList("x∈ℕ","y∈BOOL"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");
		
		containsMarkers(mac, false);
	}

	/*
	 * error:
	 * identically named local variables of merged abstract events do not have compatible types
	 */
	public void testEvents_22_mergeLocalAbstractTypesConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		addEvent(abs, "gvt", 
				makeSList("x", "y"), 
				makeSList("G1", "G2"), 
				makeSList("x⊆ℕ","y∈BOOL"), 
				makeSList(), makeSList());
		addEvent(abs, "hvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
		hasMarker(evt, null, GraphProblem.EventMergeVariableTypeError, "x");
		
	}

	/*
	 * actions of abstract events must be identical (labels AND assignments)
	 * module reordering
	 */
	public void testEvents_23_mergeAbstractActionsIdentical() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"));
		addInitialisation(abs, "p", "q");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE","q:∈ℕ"));
		addEvent(abs, "gvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE","q:∈ℕ"));
		addEvent(abs, "hvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A2", "A1"), makeSList("q:∈ℕ", "p≔TRUE"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p", "q");
		addInitialisation(mac, "p", "q");
		IEvent evt = addEvent(mac, "evt", 
				makeSList("x", "y"), 
				makeSList("G1", "G2"), makeSList("x∈ℕ", "y∈BOOL"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");
		
		containsMarkers(mac, false);
	}

	/*
	 * error:
	 * actions of abstract events differ
	 */
	public void testEvents_24_mergeAbstractActionsDiffer() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE","q:∈{x}"));
		addEvent(abs, "gvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A1", "A2"), makeSList("p≔y","q:∈ℕ"));
		addEvent(abs, "hvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A1", "A2"), makeSList("q:∈ℕ", "p≔TRUE"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
		hasMarker(evt);
		
	}
	
	/*
	 * local witness ok
	 */
	public void testEvents_25_localWitnessRefines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"));
		addInitialisation(abs, "p");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		IEvent fvt = addEvent(mac, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("p:∈{p}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("x=p"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "fvt");
		containsWitnesses(events[1], typeEnvironment, makeSList("x"), makeSList("x=p"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * local witnesses in split ok
	 */
	public void testEvents_26_localWitnessSplit() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"));
		addInitialisation(abs, "p");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		IEvent evt = addEvent(mac, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A2"), makeSList("p:∈{p}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("x=p"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");
		containsWitnesses(events[2], typeEnvironment, makeSList("x"), makeSList("x=p"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * post-values of abstract disappearing variables must be be referenced in witnesses 
	 */
	public void testEvents_27_globalWitnessAbstractVariables() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2"), makeSList("V1:∈ℕ", "V2:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V3");
		addInvariants(mac, makeSList("I1"), makeSList("V3∈ℕ"));
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=V2'+V3"));
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsWitnesses(events[0], typeEnvironment, makeSList("V1'", "V2'"), makeSList("⊤", "⊤"));
		
		hasMarker(evt);
		hasMarker(evt.getWitnesses()[0]);
		
	}
	
	/*
	 * Inherited events should not have witnesses for local variables
	 */
	public void testEvents_28_inheritedNoWitnesses() throws Exception {
		IMachineFile abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addInitialisation(ref);
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		
		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());
		
		containsMarkers(ref, false);
	}
	
	/*
	 * Inherited events should not refer to variables that exist no longer
	 */
	public void testEvents_29_inheritedAndDisappearingVariables() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℤ"));
		addInitialisation(abs, "A");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList("S"), makeSList("A≔A+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "B");
		addInvariants(ref, makeSList("J"), makeSList("A=B"));
		addInitialisation(ref, "B");
		IEvent evt = addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		getSCEvents(file, IEvent.INITIALISATION);
		
		hasMarker(evt);
	}
	
	/*
	 * Inherited events should not refer to variables that exist no longer
	 */
	public void testEvents_30_inheritedCopyEvent() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I", "J"), makeSList("A∈ℤ", "B∈ℤ"));
		addInitialisation(abs, "A", "B");
		addEvent(abs, "evt", 
				makeSList("x", "y"), 
				makeSList("G", "H"), makeSList("x∈ℕ", "y∈ℕ"), 
				makeSList("S", "T"), makeSList("A≔A+1", "B≔B+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInitialisation(ref, "A", "B");
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		containsVariables(events[1], "x", "y");
		containsGuards(events[1], environment, makeSList("G", "H"), makeSList("x∈ℕ", "y∈ℕ"));
		containsActions(events[1], environment, makeSList("S", "T"), makeSList("A≔A+1", "B≔B+1"));
		
		containsMarkers(ref, false);
	}
	
	/*
	 * labels of merged event actions must correspond
	 * (otherwise the ensueing POG would depend on the order of the events in the file,
	 * or create unreadable PO names)
	 */
	public void testEvents_31_mergeAbstractActionLabelsDiffer() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1", "A3"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
		hasMarker(evt);
		
	}
	
	/*
	 * create default witnesses for abstract global and local variables in merge
	 */
	public void testEvents_32_mergeAbstractActionCreateDefaultWitnesses() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", 
				makeSList("y"), 
				makeSList("G1"), makeSList("y∈BOOL"), 
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("q", intType);
		environment.addName("x", intType);
		environment.addName("y", boolType);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt");
		containsWitnesses(events[0], environment, 
				makeSList("q'", "x", "y"), 
				makeSList("⊤", "⊤", "⊤"));
		
		hasMarker(evt);
	}
	
	/**
	 * events in refined machine can assign to variables that were declared in
	 * the abstract machine and repeated in the concrete machine.
	 */
	public void testEvents_33_newEventPreservedVariableAssigned() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));
		addInitialisation(abs, "p");

		abs.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		containsActions(events[1], environment, makeSList("A"), makeSList("p≔TRUE"));
		
		containsMarkers(mac, false);
	}
	
	/*
	 * Witnesses must not reference post values of abstract disappearing global variables
	 */
	public void testEvents_34_localWitnessWithPostValues() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x", "y"), 
				makeSList("G1", "G2"), makeSList("x∈ℕ", "y∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "q");
		addInvariants(mac, makeSList("I1"), makeSList("q≠p"));
		IEvent fvt = addEvent(mac, "fvt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("q:∈{q}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x", "y", "p'"), makeSList("x=p'", "y=q'", "q'≠p'"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p'", intType);
		environment.addName("q'", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		containsWitnesses(events[0], environment, 
				makeSList("x", "y", "p'"), makeSList("⊤", "y=q'", "q'≠p'"));
		
		hasMarker(fvt.getWitnesses()[0]);
	}
	
	/*
	 * 
	 */
	public void testEvents_35_localVarRefByGlobalVarConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"));
		IEvent fvt = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
		addEventRefines(fvt, "evt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		containsWitnesses(events[0], emptyEnv, makeSList("x"), makeSList("⊤"));
		
		hasMarker(mac.getVariables()[0]);
		hasMarker(fvt);
	}
	
	/*
	 * variables that are not in the concrete machine cannot be assigned to or from
	 */
	public void testEvents_36_disappearedVariableAssigned() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"));
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), makeSList("A2", "A3"), makeSList("V1:∈ℕ", "V2≔V1"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("⊤"));
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsActions(events[0], typeEnvironment, makeSList(), makeSList());
		
		hasMarker(evt.getActions()[0]);
		hasMarker(evt.getActions()[1]);
		
	}
	
	/*
	 * variables that are not in the concrete machine cannot be in guards
	 */
	public void testEvents_37_disappearedVariableInGuard() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList("G1"), makeSList("V1>0"), 
				makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"));
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList("G2", "G3"), makeSList("V1>0", "V2>0"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsGuards(events[0], typeEnvironment, makeSList("G3"), makeSList("V2>0"));
		
		hasMarker(evt.getGuards()[0]);
		
	}

	/*
	 * Check that there are no witnesses for an inherited event.
	 */
	public void testEvents_38_removeWitnessesFromInherited() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"));
		addInitialisation(abs, "x");
		addEvent(abs, "evt", 
				makeSList("a"), 
				makeSList("G"), makeSList("a ∈ ℕ"), 
				makeSList("A"), makeSList("x ≔ a"));

		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"));
	
		IEvent ini = addInitialisation(ref, "y");
		addEventWitnesses(ini, makeSList("x'"), makeSList("y'=x'"));
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList("H"), makeSList("y=1"), 
				makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a÷1=y'"));

		ref.save(null, true);
		
		IMachineFile con = createMachine("con");
		addMachineRefines(con, "ref");
		addVariables(con, "y");
	
		addInheritedEvent(con, IEvent.INITIALISATION);
		addInheritedEvent(con, "evt");

		con.save(null, true);
		
		runBuilder();
		
		containsMarkers(abs, false);
		containsMarkers(ref, false);
		containsMarkers(con, false);
		
		ISCMachineFile file = con.getSCMachineFile();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());
		
	}

}
