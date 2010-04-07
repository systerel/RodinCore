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

import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
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
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsMarkers(mac, false);
	}

	/*
	 * split refines clause
	 */
	public void testEvents_01_split2() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt1 = addEvent(mac, "evt1");
		addEventRefines(evt1, "evt");
		IEvent evt2 = addEvent(mac, "evt2");
		addEventRefines(evt2, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt1",
				"evt2");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");

		containsMarkers(mac, false);
	}

	/*
	 * identically named parameters of abstract and concrete events do
	 * correspond
	 */
	public void testEvents_02_parameterTypesCorrespond() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		containsParameters(events[1], "L1");

		containsMarkers(mac, false);
	}

	/*
	 * error if identically named parameters of abstract and concrete events do
	 * NOT correspond
	 */
	public void testEvents_03_parameterTypeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsParameters(events[0]);

		hasMarker(evt.getGuards()[0], null,
				GraphProblem.ParameterChangedTypeError, "L1", "ℙ(ℤ)", "ℤ");
		hasMarker(evt.getParameters()[0], null,
				GraphProblem.UntypedParameterError, "L1");

	}

	/*
	 * create default witness if parameter disappears
	 */
	public void testEvents_04_parameterDefaultWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt", makeSList("L1", "L3"), makeSList("G1", "G3"),
				makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), makeSList("G1"),
				makeSList("L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("L1"), makeSList("L1∈L2"));
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", powIntType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");

		containsParameters(events[0], "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L1", "L3"),
				makeSList("L1∈L2", "⊤"));

		hasMarker(evt, null, GraphProblem.WitnessLabelMissingWarning, "L3");

	}

	/*
	 * global witness referring to parameter of concrete event
	 */
	public void testEvents_05_globalWitnessUseConcreteParameter()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		addEventWitnesses(init, makeSList("V1'"), makeSList("V1'=1"));
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), makeSList("G1"),
				makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=L2"));
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("L2", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1], "L2");
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"),
				makeSList("V1'=L2"));

		containsMarkers(mac, false);
	}

	/*
	 * global witness referring to post value of global variable of concrete
	 * machine
	 */
	public void testEvents_06_globalWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2⊆ℕ"), true);
		IEvent init = addInitialisation(mac, "V2");
		addEventWitnesses(init, makeSList("V1'"), makeSList("V1'=1"));
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList("A2"), makeSList("V2:∣V2'⊆ℕ"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'∈V2'"));
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", intType);
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2", powIntType);
		typeEnvironment.addName("V2'", powIntType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1]);
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"),
				makeSList("V1'∈V2'"));

		containsMarkers(mac, false);
	}

	/*
	 * creation of parameter default witness
	 */
	public void testEvents_07_parameterTypesAndWitnesses() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt", makeSList("L1", "L3"), makeSList("G1", "G3"),
				makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1", "L2"), makeSList(
				"G1", "G2"), makeSList("L1=1", "L2⊆ℕ"), makeSList(),
				makeSList());
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", intType);
		typeEnvironment.addName("L2", powIntType);
		typeEnvironment.addName("L3", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");

		containsParameters(events[0], "L1", "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L3"),
				makeSList("⊤"));

		hasMarker(evt, null, GraphProblem.WitnessLabelMissingWarning, "L3");

	}

	/*
	 * split event into three events
	 */
	public void testEvents_08_split3() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		IEvent gvt = addEvent(mac, "gvt");
		addEventRefines(gvt, "evt");
		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt", "gvt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
		refinesEvents(events[3], "evt");

		containsMarkers(mac, false);
	}

	/*
	 * merge two events into one
	 */
	public void testEvents_09_merge2() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt", "fvt");

		containsMarkers(mac, false);
	}

	/*
	 * correctly extended event with refines clause
	 */
	public void testEvents_10_extended() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent event = addExtendedEvent(mac, "evt");
		addEventRefines(event, "evt");
		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsMarkers(mac, false);
	}

	/*
	 * faulty extended event without refines clause
	 */
	public void testEvents_11_extendedWithoutRefine() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addExtendedEvent(mac, "evt");
		IEvent fvt = addExtendedEvent(mac, "fvt");
		addEventRefines(fvt, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt);
		hasNotMarker(fvt);

	}

	/*
	 * conflict: an extended event must not merge events
	 */
	public void testEvents_12_extendedMergeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addExtendedEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt);
		hasNotMarker(fvt);

	}

	/*
	 * events may be merged more than once
	 */
	public void testEvents_13_mergeMergeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "evt", "fvt");

		hasNotMarker(evt);
		hasNotMarker(fvt);

	}

	/*
	 * inherit / refines / merge used together correctly
	 */
	public void testEvents_14_extendedRefinesMergeOK() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "gvt");
		addEventRefines(fvt, "fvt");
		IEvent hvt = addExtendedEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		addEvent(mac, "ivt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt", "hvt", "ivt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "fvt", "gvt");
		refinesEvents(events[3], "hvt");
		refinesEvents(events[4]);

		containsMarkers(mac, false);
	}

	/*
	 * events may be split and merged at the same time
	 */
	public void testEvents_15_splitAndMerge() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "hvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "hvt");
		addEventRefines(fvt, "fvt");
		IEvent hvt = addExtendedEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		IEvent ivt = addEvent(mac, "ivt");
		IEvent jvt = addEvent(mac, "jvt");
		addEventRefines(jvt, "hvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "evt", "fvt", "hvt", "ivt", "jvt");

		hasMarker(evt, null, GraphProblem.InconsistentEventLabelWarning, "evt");
		hasNotMarker(fvt);
		hasNotMarker(hvt);
		hasNotMarker(ivt);
		hasNotMarker(jvt);
	}

	/*
	 * initialization implicitly refined
	 */
	public void testEvents_16_initialisationDefaultRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, IEvent.INITIALISATION);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addEvent(mac, IEvent.INITIALISATION);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);

		containsMarkers(mac, false);

	}

	/*
	 * initialization cannot be explicitly refined
	 */
	public void testEvents_17_initialisationRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, IEvent.INITIALISATION);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addEvent(mac, IEvent.INITIALISATION);
		addEventRefines(init, IEvent.INITIALISATION);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, IEvent.INITIALISATION);

		hasMarker(init.getRefinesClauses()[0]);

	}

	/*
	 * initialization can be extended
	 */
	public void testEvents_18_initialisationExtended() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, IEvent.INITIALISATION);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addExtendedEvent(mac, IEvent.INITIALISATION);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);

		containsMarkers(mac, false);
	}

	/*
	 * no other event can refine the initialization
	 */
	public void testEvents_19_initialisationNotRefined() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, IEvent.INITIALISATION);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, IEvent.INITIALISATION);
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt);

	}

	/*
	 * several refinement problems occurring together
	 */
	public void testEvents_20_multipleRefineProblems() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");
		addEvent(abs, IEvent.INITIALISATION);
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, IEvent.INITIALISATION);
		addEventRefines(ini, IEvent.INITIALISATION);
		IEvent hvt = addEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		addEventRefines(hvt, IEvent.INITIALISATION);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent gvt = addExtendedEvent(mac, "gvt");
		addEventRefines(gvt, "gvt");
		addEventRefines(gvt, "evt");
		IEvent fvt = addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");

		hasMarker(ini.getRefinesClauses()[0], EventBAttributes.TARGET_ATTRIBUTE);
		hasMarker(hvt);
		hasNotMarker(evt);
		hasMarker(gvt);
		hasNotMarker(fvt);

	}

	/*
	 * identically named parameters of merged abstract events must have
	 * compatible types
	 */
	public void testEvents_21_mergeParameterAbstractTypesCorrespond()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		addEvent(abs, "gvt", makeSList("x", "y"), makeSList("G1", "G2"),
				makeSList("x∈ℕ", "y∈BOOL"), makeSList(), makeSList());
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("x", "y"), makeSList("G1",
				"G2"), makeSList("x∈ℕ", "y∈BOOL"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");

		containsMarkers(mac, false);
	}

	/*
	 * error: identically named parameter of merged abstract events do not have
	 * compatible types
	 */
	public void testEvents_22_mergeParameterAbstractTypesConflict()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		addEvent(abs, "gvt", makeSList("x", "y"), makeSList("G1", "G2"),
				makeSList("x⊆ℕ", "y∈BOOL"), makeSList(), makeSList());
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt, null, GraphProblem.EventMergeVariableTypeError, "x");

	}

	/*
	 * actions of abstract events must be identical (labels AND assignments)
	 * module reordering
	 */
	public void testEvents_23_mergeAbstractActionsIdentical() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addInitialisation(abs, "p", "q");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔TRUE",
						"q:∈ℕ"));
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A2", "A1"), makeSList("q:∈ℕ",
						"p≔TRUE"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p", "q");
		addInitialisation(mac, "p", "q");
		IEvent evt = addEvent(mac, "evt", makeSList("x", "y"), makeSList("G1",
				"G2"), makeSList("x∈ℕ", "y∈BOOL"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt");

		containsMarkers(mac, false);
	}

	/*
	 * error: actions of abstract events differ
	 */
	public void testEvents_24_mergeAbstractActionsDiffer() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈{x}"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔y",
						"q:∈ℕ"));
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("q:∈ℕ",
						"p≔TRUE"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt);

	}

	/*
	 * parameter witness ok
	 */
	public void testEvents_25_parameterWitnessRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), true);
		addInitialisation(abs, "p");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		IEvent fvt = addEvent(mac, "fvt", makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("p:∈{p}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("x=p"));

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "fvt");
		containsWitnesses(events[1], typeEnvironment, makeSList("x"),
				makeSList("x=p"));

		containsMarkers(mac, false);
	}

	/*
	 * parameter witnesses in split ok
	 */
	public void testEvents_26_parameterWitnessSplit() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), true);
		addInitialisation(abs, "p");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		IEvent evt = addEvent(mac, "evt", makeSList("x"), makeSList("G1"),
				makeSList("x∈ℕ"), makeSList("A1"), makeSList("p:∈{x}"));
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt", makeSList(), makeSList(),
				makeSList(), makeSList("A2"), makeSList("p:∈{p}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("x=p"));

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		typeEnvironment.addName("y", intType);

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt");
		containsWitnesses(events[2], typeEnvironment, makeSList("x"),
				makeSList("x=p"));

		containsMarkers(mac, false);
	}

	/*
	 * post-values of abstract disappearing variables must be be referenced in
	 * witnesses
	 */
	public void testEvents_27_globalWitnessAbstractVariables() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"), true, true);
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(
				"A1", "A2"), makeSList("V1:∈ℕ", "V2:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V3");
		addInvariants(mac, makeSList("I1"), makeSList("V3∈ℕ"), false);
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=V2'+V3"));
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");

		containsWitnesses(events[0], typeEnvironment, makeSList("V1'", "V2'"),
				makeSList("⊤", "⊤"));

		hasMarker(evt);
		hasMarker(evt.getWitnesses()[0]);

	}

	/*
	 * Extended events should not have witnesses for parameters
	 */
	public void testEvents_28_extendedNoParameterWitnesses() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("x"), makeSList("G"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addInitialisation(ref);
		IEvent evt = addExtendedEvent(ref, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(ref);
		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);

		ISCMachineRoot file = ref.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());

		containsMarkers(ref, false);
	}

	/*
	 * Extended events should not refer to variables that exist no longer
	 */
	public void testEvents_29_extendedAndDisappearingVariables()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A");
		addInvariants(abs, makeSList("I"), makeSList("A∈ℤ"), false);
		addInitialisation(abs, "A");
		addEvent(abs, "evt", makeSList("x"), makeSList("G"), makeSList("x∈ℕ"),
				makeSList("S"), makeSList("A≔A+1"));
		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "B");
		addInvariants(ref, makeSList("J"), makeSList("A=B"), false);
		addInitialisation(ref, "B");
		IEvent evt = addExtendedEvent(ref, "evt");
		saveRodinFileOf(ref);
		runBuilder();

		ISCMachineRoot file = ref.getSCMachineRoot();

		getSCEvents(file, IEvent.INITIALISATION);

		hasMarker(evt);
	}

	/*
	 * Extended events should not refer to variables that exist no longer
	 */
	public void testEvents_30_extendedCopyEvent() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "A", "B");
		addInvariants(abs, makeSList("I", "J"), makeSList("A∈ℤ", "B∈ℤ"), true, true);
		addInitialisation(abs, "A", "B");
		addEvent(abs, "evt", makeSList("x", "y"), makeSList("G", "H"),
				makeSList("x∈ℕ", "y∈ℕ"), makeSList("S", "T"), makeSList(
						"A≔A+1", "B≔B+1"));
		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "A", "B");
		addInitialisation(ref, "A", "B");
		IEvent evt = addExtendedEvent(ref, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(ref);
		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("A", intType);
		environment.addName("B", intType);
		environment.addName("x", intType);
		environment.addName("y", intType);

		ISCMachineRoot file = ref.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		containsParameters(events[1], "x", "y");
		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"x∈ℕ", "y∈ℕ"));
		containsActions(events[1], environment, makeSList("S", "T"), makeSList(
				"A≔A+1", "B≔B+1"));

		containsMarkers(ref, false);
	}

	/*
	 * labels of merged event actions must correspond (otherwise the ensueing
	 * POG would depend on the order of the events in the file, or create
	 * unreadable PO names)
	 */
	public void testEvents_31_mergeAbstractActionLabelsDiffer()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A3"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔TRUE",
						"q:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, "fvt");

		hasMarker(evt);

	}

	/*
	 * create default witnesses for abstract variables and parameters in merge
	 */
	public void testEvents_32_mergeAbstractActionCreateDefaultWitnesses()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔TRUE",
						"q:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);
		environment.addName("q", intType);
		environment.addName("x", intType);
		environment.addName("y", boolType);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt", "fvt");
		containsWitnesses(events[0], environment, makeSList("q'", "x", "y"),
				makeSList("⊤", "⊤", "⊤"));

		hasMarker(evt);
	}

	/**
	 * events in refined machine can assign to variables that were declared in
	 * the abstract machine and repeated in the concrete machine.
	 */
	public void testEvents_33_newEventPreservedVariableAssigned()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), true);
		addInitialisation(abs, "p");

		saveRodinFileOf(abs);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p", boolType);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A"), makeSList("p≔TRUE"));

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		containsActions(events[1], environment, makeSList("A"),
				makeSList("p≔TRUE"));

		containsMarkers(mac, false);
	}

	/*
	 * Witnesses must not reference post values of abstract disappearing global
	 * variables
	 */
	public void testEvents_34_parameterWitnessWithPostValues() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), false);
		addEvent(abs, "evt", makeSList("x", "y"), makeSList("G1", "G2"),
				makeSList("x∈ℕ", "y∈ℕ"), makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "q");
		addInvariants(mac, makeSList("I1"), makeSList("q≠p"), false);
		IEvent fvt = addEvent(mac, "fvt", makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("q:∈{q}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x", "y", "p'"), makeSList("x=p'",
				"y=q'", "q'≠p'"));

		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("p'", intType);
		environment.addName("q'", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "fvt");
		containsWitnesses(events[0], environment, makeSList("x", "y", "p'"),
				makeSList("⊤", "y=q'", "q'≠p'"));

		hasMarker(fvt.getWitnesses()[0]);
	}

	/*
	 * A concrete machine must not declare a variable that has the same name as
	 * a parameter in the abstract machine.
	 */
	public void testEvents_35_parameterRefByVariableConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		IEvent fvt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList(), makeSList());
		addEventRefines(fvt, "evt");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		containsWitnesses(events[0], emptyEnv, makeSList("x"), makeSList("⊤"));

		hasMarker(mac.getVariables()[0]);
		hasMarker(fvt);
	}

	/*
	 * variables that are not in the concrete machine cannot be assigned to or
	 * from
	 */
	public void testEvents_36_disappearedVariableAssigned() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"), false);
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList("A2", "A3"), makeSList("V1:∈ℕ", "V2≔V1"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("⊤"));
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

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
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addEvent(abs, "evt", makeSList(), makeSList("G1"), makeSList("V1>0"),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"), false);
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList("G2", "G3"),
				makeSList("V1>0", "V2>0"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilder();

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1'", intType);
		typeEnvironment.addName("V2'", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");

		containsGuards(events[0], typeEnvironment, makeSList("G3"),
				makeSList("V2>0"));

		hasMarker(evt.getGuards()[0]);

	}

	/*
	 * Check that there are no witnesses for an extended event.
	 */
	public void testEvents_38_removeWitnessesFromExtended() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		addInitialisation(abs, "x");
		addEvent(abs, "evt", makeSList("a"), makeSList("G"),
				makeSList("a ∈ ℕ"), makeSList("A"), makeSList("x ≔ a"));

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "y");
		addInvariants(ref, makeSList("J"), makeSList("x+y=2"), false);

		IEvent ini = addInitialisation(ref, "y");
		addEventWitnesses(ini, makeSList("x'"), makeSList("y'=x'"));
		IEvent evt = addEvent(ref, "evt", makeSList(), makeSList("H"),
				makeSList("y=1"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("a"), makeSList("a÷1=y'"));

		saveRodinFileOf(ref);

		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "ref");
		addVariables(con, "y");

		addExtendedEvent(con, IEvent.INITIALISATION);
		IEvent evt1 = addExtendedEvent(con, "evt");
		addEventRefines(evt1, "evt");

		saveRodinFileOf(con);

		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(ref, false);
		containsMarkers(con, false);

		ISCMachineRoot file = con.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());

	}

	/*
	 * An extended event must (re-)declare a parameter that was already declared
	 * in an abstract event.
	 */
	public void testEvents_39_extendedParameterCollision() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("a"), makeSList("G"),
				makeSList("a ∈ ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addInitialisation(ref);
		IEvent evt = addEvent(ref, "evt", makeSList("a", "b"), makeSList("H"),
				makeSList("b=1"), makeSList(), makeSList());
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(ref, true);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsParameters(events[1], "a", "b");

		hasMarker(evt, EventBAttributes.EXTENDED_ATTRIBUTE);
		hasMarker(evt.getParameters()[0]);
	}

	/*
	 * An extended event must reuse a guard label
	 */
	public void testEvents_40_extendedGuardLabelCollision() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), makeSList("G"), makeSList("1 ∈ ℕ"),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addVariables(ref, "x", "y");
		addInitialisation(ref, "x", "y");
		addInvariant(ref, "I", "x∈ℕ", true);
		addInvariant(ref, "J", "y∈ℕ", true);
		IEvent evt = addEvent(ref, "evt", makeSList(), makeSList("G", "H"),
				makeSList("1<0", "5=1"), makeSList("G", "A"), makeSList(
						"x≔x+1", "y≔y−1"));
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(ref, true);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);

		hasMarker(evt, EventBAttributes.EXTENDED_ATTRIBUTE);
		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"1 ∈ ℕ", "5=1"));
		hasMarker(evt.getGuards()[0], EventBAttributes.LABEL_ATTRIBUTE);

		containsActions(events[1], environment, makeSList("A"),
				makeSList("y≔y−1"));
		hasMarker(evt.getActions()[0], EventBAttributes.LABEL_ATTRIBUTE);

	}

	/*
	 * An extended event must reuse an action label
	 */
	public void testEvents_41_extendedActionLabelCollision() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariant(abs, "I", "x∈ℕ", true);
		addInitialisation(abs, "x");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A"), makeSList("x:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addVariables(ref, "x", "y");
		addInitialisation(ref, "x", "y");
		addInvariant(ref, "J", "y∈ℕ", true);
		IEvent evt = addEvent(ref, "evt", makeSList(), makeSList("A", "H"),
				makeSList("1<0", "5=1"), makeSList("A", "B"), makeSList(
						"x≔x+1", "y≔y−1"));
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(ref, true);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);

		hasMarker(evt, EventBAttributes.EXTENDED_ATTRIBUTE);
		containsGuards(events[1], environment, makeSList("H"), makeSList("5=1"));
		hasMarker(evt.getGuards()[0], EventBAttributes.LABEL_ATTRIBUTE);

		containsActions(events[1], environment, makeSList("A", "B"), makeSList(
				"x:∈ℕ", "y≔y−1"));
		hasMarker(evt.getActions()[0], EventBAttributes.LABEL_ATTRIBUTE);

	}

	/*
	 * An extended event copies the abstract parameters and adds the concrete
	 * ones
	 */
	public void testEvents_42_extendedAddAndCopyParameters() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("a"), makeSList("G"),
				makeSList("a ∈ ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addInitialisation(ref);
		IEvent evt = addEvent(ref, "evt", makeSList("b"), makeSList("H"),
				makeSList("b=1"), makeSList(), makeSList());
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(ref, false);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsParameters(events[1], "a", "b");

	}

	/*
	 * An extended event copies the abstract guards and adds the concrete ones
	 */
	public void testEvents_43_extendedAddAndCopyGuards() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("a"), makeSList("G"),
				makeSList("a ∈ ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addInitialisation(ref);
		IEvent evt = addEvent(ref, "evt", makeSList("b"), makeSList("H"),
				makeSList("b=1"), makeSList(), makeSList());
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("a", intType);
		environment.addName("b", intType);

		containsMarkers(abs, false);
		containsMarkers(ref, false);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"a ∈ ℕ", "b=1"));

	}

	/*
	 * An extended event copies the abstract actions and adds the concrete ones
	 */
	public void testEvents_44_extendedAddAndCopyActions() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariant(abs, "I", "x∈ℕ", true);
		addInitialisation(abs, "x");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A"), makeSList("x≔x+1"));

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addVariables(ref, "x", "y");
		addInvariant(ref, "J", "y∈ℕ", true);
		addInitialisation(ref, "x", "y");
		IEvent evt = addEvent(ref, "evt", makeSList(), makeSList(),
				makeSList(), makeSList("B"), makeSList("y≔y+1"));
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);

		containsMarkers(abs, false);
		containsMarkers(ref, false);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsActions(events[1], environment, makeSList("A", "B"), makeSList(
				"x≔x+1", "y≔y+1"));

	}

	/*
	 * An extended event copies elements transitively from its abstractions
	 */
	public void testEvents_45_extendedCopyTransitive() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariant(abs, "I", "x∈ℕ", true);
		addInitialisation(abs, "x");
		addEvent(abs, "evt", makeSList("a"), makeSList("G"), makeSList("a<x"),
				makeSList("A"), makeSList("x≔x+1"));

		saveRodinFileOf(abs);

		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");

		addVariables(ref, "x", "y");
		addInvariant(ref, "J", "y∈ℕ", true);
		addInitialisation(ref, "x", "y");
		IEvent evt = addEvent(ref, "evt", makeSList("b"), makeSList("H"),
				makeSList("b<y"), makeSList("B"), makeSList("y≔y+1"));
		setExtended(evt);
		addEventRefines(evt, "evt");

		saveRodinFileOf(ref);

		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "ref");

		addVariables(con, "x", "y", "z");
		addInvariant(con, "K", "z∈ℕ", true);
		addInitialisation(con, "x", "y", "z");
		IEvent evt1 = addEvent(con, "evt", makeSList("c"), makeSList("D"),
				makeSList("c<z"), makeSList("C"), makeSList("z≔z+1"));
		setExtended(evt1);
		addEventRefines(evt1, "evt");

		saveRodinFileOf(con);

		runBuilder();

		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);
		environment.addName("z", intType);
		environment.addName("a", intType);
		environment.addName("b", intType);
		environment.addName("c", intType);

		containsMarkers(abs, false);
		containsMarkers(ref, false);
		containsMarkers(con, false);

		ISCMachineRoot file = con.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");

		containsParameters(events[1], "a", "b", "c");
		containsGuards(events[1], environment, makeSList("G", "H", "D"),
				makeSList("a<x", "b<y", "c<z"));
		containsActions(events[1], environment, makeSList("A", "B", "C"),
				makeSList("x≔x+1", "y≔y+1", "z≔z+1"));

	}

	/*
	 * extended initialization implicitly refined
	 */
	public void testEvents_46_initialisationExtendedRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, IEvent.INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, IEvent.INITIALISATION);
		setExtended(ini);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);

		containsMarkers(abs, false);
		containsMarkers(mac, false);

	}

	/*
	 * extended initialization action label conflict and checked actions copied
	 */
	public void testEvents_47_initialisationExtendedActions() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℕ", "y∈ℕ"), false, false);
		addEvent(abs, IEvent.INITIALISATION, makeSList(), makeSList(),
				makeSList(), makeSList("A", "B"), makeSList("x:∈ℕ", "y:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "x", "y", "z");
		addInvariant(mac, "K", "z∈ℕ", false);
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, IEvent.INITIALISATION, makeSList(), makeSList(),
				makeSList(), makeSList("B", "C"), makeSList("y:∈{0,1}", "z:∈ℕ"));
		setExtended(ini);

		saveRodinFileOf(mac);

		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		environment.addName("y", intType);
		environment.addName("z", intType);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);
		containsActions(events[0], environment, 
				makeSList("A", "B", "C"), makeSList("x:∈ℕ", "y:∈ℕ", "z:∈ℕ"));

		containsMarkers(abs, false);

		hasMarker(ini, EventBAttributes.EXTENDED_ATTRIBUTE);
	}

	/*
	 * There exists an abstract event with the same name but no refines clause
	 * pointing to it.
	 */
	public void testEvents_48_sameNameNoRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		saveRodinFileOf(mac);

		runBuilder();

		hasMarker(evt, null, GraphProblem.InconsistentEventLabelWarning,
				"evt");
	}

}
