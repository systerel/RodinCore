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

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventRefines extends BasicSCTest {
	
	public void testEvents_00_refines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
	}
	
	public void testEvents_01_split2() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt1 = addEvent(mac, "evt1");
		addEventRefines(evt1, "evt");
		IEvent evt2 = addEvent(mac, "evt2");
		addEventRefines(evt2, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt1", "evt2");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "evt");
		
	}

	public void testEvents_02_localTypesCorrespond() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0], "L1");
		
	}
	
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
		
	}
	
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
		
	}
	
	public void testEvents_05_globalDefaultWitness() throws Exception {
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
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("V1'"), makeSList("V1'=L2"));
		
	}
	
	public void testEvents_06_globalDefaultWitness() throws Exception {
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
		addInvariants(mac, makeSList("I2"), makeSList("V2⊆ℕ"));
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
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0]);
		containsWitnesses(events[0], typeEnvironment, makeSList("V1'"), makeSList("V1'∈V2'"));
		
	}
	
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
		
	}
	
	public void testEvents_08_split3() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		IEvent gvt = addEvent(mac, "gvt");
		addEventRefines(gvt, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
	}

	public void testEvents_09_merge2() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt", "fvt");
	}
	
	public void testEvents_10_inherited() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
	}

	public void testEvents_11_InheritedRefinesConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		refinesEvents(events[0], "evt");
		
	}

	public void testEvents_12_inheritedSplitConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		refinesEvents(events[0], "evt", "fvt");
		
	}	
	
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
		
	}	
	
	public void testEvents_14_inheritedRefinesMergeOK() throws Exception {
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
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "gvt");
		addEventRefines(fvt, "fvt");
		addInheritedEvent(mac, "hvt");
		addEvent(mac, "ivt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "hvt", "ivt");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "fvt", "gvt");
		refinesEvents(events[2], "hvt");
		refinesEvents(events[3]);
	}	
	
	public void testEvents_15_inheritedRefinesMergeConflict() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
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
		
		getSCEvents(file, "ivt");
	}	
	
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
		
	}
	
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
		
	}
	
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
		
	}
	
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
		
	}
	
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
		
	}
	
	public void testEvents_21_mergeLocalAbstractTypesCorrespond() throws Exception {
		IMachineFile abs = createMachine("abs");
		
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
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "evt", "fvt");
		
	}

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
		
	}

	public void testEvents_23_mergeAbstractActionsIdentical() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"));
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
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "evt", "fvt");
		
	}

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
		
	}
	
	public void testEvents_25_localWitnessRefines() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
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
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		containsWitnesses(events[0], typeEnvironment, makeSList("x"), makeSList("x=p"));
	}
	
	public void testEvents_26_localWitnessSplit() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));

		abs.save(null, true);
		
		runBuilder();

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		IEvent evt = addEvent(mac, "evt", 
				makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList("A1"), makeSList("p:∈{x}"));
		addEventRefines(evt, "evt");
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
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt");
		containsWitnesses(events[1], typeEnvironment, makeSList("x"), makeSList("x=p"));
	}
	
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
		
	}
	
	/*
	 * Inherited events should not have witnesses for local variables
	 */
	public void testEvents_28_inheritedNoWitnesses() throws Exception {
		IMachineFile abs = createMachine("abs");
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		
		containsWitnesses(events[0], emptyEnv, makeSList(), makeSList());
	}
	
	/*
	 * Inherited events should not refer to variables that exist no longer
	 */
	public void testEvents_29_inheritedAndDisappearingVariables() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℤ"));
		addEvent(abs, "evt", 
				makeSList("x"), 
				makeSList("G"), makeSList("x∈ℕ"), 
				makeSList("S"), makeSList("A≔A+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(abs, "B");
		addInvariants(abs, makeSList("J"), makeSList("A=B"));
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		getSCEvents(file);
	}
	
	/*
	 * Inherited events should not refer to variables that exist no longer
	 */
	public void testEvents_30_inheritedCopyEvent() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I", "J"), makeSList("A∈ℤ", "B∈ℤ"));
		addEvent(abs, "evt", 
				makeSList("x", "y"), 
				makeSList("G", "H"), makeSList("x∈ℕ", "y∈ℕ"), 
				makeSList("S", "T"), makeSList("A≔A+1", "B≔B+1"));
		abs.save(null, true);
		
		IMachineFile ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInheritedEvent(ref, "evt");
		ref.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);
		
		ISCMachineFile file = ref.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		containsVariables(events[0], "x", "y");
		containsGuards(events[0], environment, makeSList("G", "H"), makeSList("x∈ℕ", "y∈ℕ"));
		containsActions(events[0], environment, makeSList("S", "T"), makeSList("A≔A+1", "B≔B+1"));
	}
	
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
		
	}
	
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
	}
	
	public void testEvents_33_newEventPreservedVariableAssigned() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"));

		abs.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("p≔TRUE"));
	
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		containsActions(events[0], environment, makeSList("A"), makeSList("p≔TRUE"));
	}
	
	/*
	 * Witnesses must not reference post values of abstract disappearing global variables
	 */
	public void testEvents_25_localWitnessWithPostValues() throws Exception {
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
	}

}
