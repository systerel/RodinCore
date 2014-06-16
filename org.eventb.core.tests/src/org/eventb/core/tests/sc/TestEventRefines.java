/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - added tests about abstract event not refined
 *     Systerel - added tests about abstract parameter collisions
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXTENDED_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.IEvent.INITIALISATION;
import static org.eventb.core.sc.GraphProblem.AbstractEventNotRefinedWarning;
import static org.eventb.core.sc.GraphProblem.ActionLabelConflictError;
import static org.eventb.core.sc.GraphProblem.AssignedIdentifierNotVariableError;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameImportConflictError;
import static org.eventb.core.sc.GraphProblem.ConstantNameImportConflictError;
import static org.eventb.core.sc.GraphProblem.EventExtendedMergeError;
import static org.eventb.core.sc.GraphProblem.EventExtendedUnrefinedError;
import static org.eventb.core.sc.GraphProblem.EventMergeActionError;
import static org.eventb.core.sc.GraphProblem.EventMergeLabelError;
import static org.eventb.core.sc.GraphProblem.EventMergeVariableTypeError;
import static org.eventb.core.sc.GraphProblem.EventRefinementError;
import static org.eventb.core.sc.GraphProblem.GuardLabelConflictError;
import static org.eventb.core.sc.GraphProblem.InconsistentEventLabelWarning;
import static org.eventb.core.sc.GraphProblem.InitialisationRefinedError;
import static org.eventb.core.sc.GraphProblem.InitialisationRefinesEventWarning;
import static org.eventb.core.sc.GraphProblem.ParameterChangedTypeError;
import static org.eventb.core.sc.GraphProblem.ParameterNameConflictError;
import static org.eventb.core.sc.GraphProblem.ParameterNameImportConflictWarning;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.UntypedParameterError;
import static org.eventb.core.sc.GraphProblem.VariableHasDisappearedError;
import static org.eventb.core.sc.GraphProblem.VariableNameConflictError;
import static org.eventb.core.sc.GraphProblem.WitnessFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.WitnessLabelMissingWarning;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEventRefines extends BasicSCTestWithFwdConfig {

	/*
	 * simple refines clause
	 */
	@Test
	public void testEvents_00_refines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
	}

	/*
	 * split refines clause
	 */
	@Test
	public void testEvents_01_split2() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt1 = addEvent(mac, "evt1");
		addEventRefines(evt1, "evt");
		IEvent evt2 = addEvent(mac, "evt2");
		addEventRefines(evt2, "evt");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt1",
				"evt2");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
	}

	/*
	 * identically named parameters of abstract and concrete events do
	 * correspond
	 */
	@Test
	public void testEvents_02_parameterTypesCorrespond() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		containsParameters(events[1], "L1");
	}

	/*
	 * error if identically named parameters of abstract and concrete events do
	 * NOT correspond
	 */
	@Test
	public void testEvents_03_parameterTypeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"),
				makeSList("L1⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(evt.getGuards()[0], PREDICATE_ATTRIBUTE,
						ParameterChangedTypeError, "L1", "ℙ(ℤ)", "ℤ"),
				marker(evt.getParameters()[0], IDENTIFIER_ATTRIBUTE,
						UntypedParameterError, "L1"),
				marker(evt, WitnessLabelMissingWarning, "L1"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
		containsParameters(events[1]);
	}

	/*
	 * create default witness if parameter disappears
	 */
	@Test
	public void testEvents_04_parameterDefaultWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt", makeSList("L1", "L3"), makeSList("G1", "G3"),
				makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), makeSList("G1"),
				makeSList("L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("L1"), makeSList("L1∈L2"));
		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, WitnessLabelMissingWarning, "L3"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"L1=ℤ; L2=ℙ(ℤ)", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1], "L2");
		containsWitnesses(events[1], typeEnvironment, makeSList("L1", "L3"),
				makeSList("L1∈L2", "⊤"));
	}

	/*
	 * global witness referring to parameter of concrete event
	 */
	@Test
	public void testEvents_05_globalWitnessUseConcreteParameter()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		addEventWitnesses(init, makeSList("V1'"), makeSList("V1'=1"));
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), makeSList("G1"),
				makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=L2"));
		saveRodinFileOf(mac);

		runBuilderCheck();

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V1'=ℤ; L2=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1], "L2");
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"),
				makeSList("V1'=L2"));
	}

	/*
	 * global witness referring to post value of global variable of concrete
	 * machine
	 */
	@Test
	public void testEvents_06_globalWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

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

		runBuilderCheck();

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V1'=ℤ; V2=ℙ(ℤ); V2'=ℙ(ℤ)", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1]);
		containsWitnesses(events[1], typeEnvironment, makeSList("V1'"),
				makeSList("V1'∈V2'"));
	}

	/*
	 * creation of parameter default witness
	 */
	@Test
	public void testEvents_07_parameterTypesAndWitnesses() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt", makeSList("L1", "L3"), makeSList("G1", "G3"),
				makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList("L1", "L2"), makeSList(
				"G1", "G2"), makeSList("L1=1", "L2⊆ℕ"), makeSList(),
				makeSList());
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, WitnessLabelMissingWarning, "L3"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"L1=ℤ; L2=ℙ(ℤ); L3=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsParameters(events[1], "L1", "L2");
		containsWitnesses(events[1], typeEnvironment, makeSList("L3"),
				makeSList("⊤"));
	}

	/*
	 * split event into three events
	 */
	@Test
	public void testEvents_08_split3() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"gvt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
		refinesEvents(events[3], "evt");
	}

	/*
	 * merge two events into one
	 */
	@Test
	public void testEvents_09_merge2() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt", "fvt");
	}

	/*
	 * correctly extended event with refines clause
	 */
	@Test
	public void testEvents_10_extended() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent event = addExtendedEvent(mac, "evt");
		addEventRefines(event, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");
	}

	/*
	 * faulty extended event without refines clause
	 */
	@Test
	public void testEvents_11_extendedWithoutRefine() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addExtendedEvent(mac, "evt");
		IEvent fvt = addExtendedEvent(mac, "fvt");
		addEventRefines(fvt, "evt");

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(evt, InconsistentEventLabelWarning, "evt"),
				marker(evt, EXTENDED_ATTRIBUTE, EventExtendedUnrefinedError,
						"evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * conflict: an extended event must not merge events
	 */
	@Test
	public void testEvents_12_extendedMergeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addExtendedEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, EXTENDED_ATTRIBUTE,
				EventExtendedMergeError, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * events may be merged more than once
	 */
	@Test
	public void testEvents_13_mergeMergeConflict() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "evt", "fvt");
	}

	/*
	 * inherit / refines / merge used together correctly
	 */
	@Test
	public void testEvents_14_extendedRefinesMergeOK() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"hvt", "ivt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "fvt", "gvt");
		refinesEvents(events[3], "hvt");
		refinesEvents(events[4]);
	}

	/*
	 * events may be split and merged at the same time
	 */
	@Test
	public void testEvents_15_splitAndMerge() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "hvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "hvt");
		addEventRefines(fvt, "fvt");
		IEvent hvt = addExtendedEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		addEvent(mac, "ivt");
		IEvent jvt = addEvent(mac, "jvt");
		addEventRefines(jvt, "hvt");

		saveRodinFileOf(mac);

		final IRefinesMachine refines = mac.getRefinesClauses()[0];
		runBuilderCheck(
				marker(refines, TARGET_ATTRIBUTE,
						AbstractEventNotRefinedWarning, "evt"),
				marker(refines, TARGET_ATTRIBUTE,
						AbstractEventNotRefinedWarning, "gvt"),
				marker(evt, InconsistentEventLabelWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "evt", "fvt", "hvt", "ivt", "jvt");
	}

	/*
	 * initialization implicitly refined
	 */
	@Test
	public void testEvents_16_initialisationDefaultRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addEvent(mac, INITIALISATION);

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		refinesEvents(events[0], INITIALISATION);
	}

	/*
	 * initialization cannot be explicitly refined
	 */
	@Test
	public void testEvents_17_initialisationRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addEvent(mac, INITIALISATION);
		addEventRefines(init, INITIALISATION);

		saveRodinFileOf(mac);

		runBuilderCheck(marker(init.getRefinesClauses()[0], TARGET_ATTRIBUTE,
				InitialisationRefinesEventWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION);
	}

	/*
	 * initialization can be extended
	 */
	@Test
	public void testEvents_18_initialisationExtended() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addExtendedEvent(mac, INITIALISATION);

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		refinesEvents(events[0], INITIALISATION);
	}

	/*
	 * no other event can refine the initialization
	 */
	@Test
	public void testEvents_19_initialisationNotRefined() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, INITIALISATION);
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(evt.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						InitialisationRefinedError),
				marker(evt, EventRefinementError));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * several refinement problems occurring together
	 */
	@Test
	public void testEvents_20_multipleRefineProblems() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, "evt");
		addEvent(abs, INITIALISATION);
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, INITIALISATION);
		addEventRefines(ini, INITIALISATION);
		IEvent hvt = addEvent(mac, "hvt");
		addEventRefines(hvt, "hvt");
		addEventRefines(hvt, INITIALISATION);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent gvt = addExtendedEvent(mac, "gvt");
		addEventRefines(gvt, "gvt");
		addEventRefines(gvt, "evt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(ini.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						InitialisationRefinesEventWarning),
				marker(hvt, EventRefinementError),
				marker(hvt.getRefinesClauses()[1], TARGET_ATTRIBUTE,
						InitialisationRefinedError),
				marker(gvt, EXTENDED_ATTRIBUTE, EventExtendedMergeError, "gvt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "evt", "fvt");
	}

	/*
	 * identically named parameters of merged abstract events must have
	 * compatible types
	 */
	@Test
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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "evt", "fvt");
	}

	/*
	 * error: identically named parameter of merged abstract events do not have
	 * compatible types
	 */
	@Test
	public void testEvents_22_mergeParameterAbstractTypesConflict()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);

		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		addEvent(abs, "gvt", makeSList("x", "y"), makeSList("G1", "G2"),
				makeSList("x⊆ℕ", "y∈BOOL"), makeSList(), makeSList());
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, EventMergeVariableTypeError, "x"),
				marker(evt, WitnessLabelMissingWarning, "x"),
				marker(evt, WitnessLabelMissingWarning, "y"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * actions of abstract events must be identical (labels AND assignments)
	 * module reordering
	 */
	@Test
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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "evt", "fvt");
	}

	/*
	 * error: actions of abstract events differ
	 */
	@Test
	public void testEvents_24_mergeAbstractActionsDiffer() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addInitialisation(abs, "p", "q");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈{x}"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔y",
						"q:∈ℕ"));
		addEvent(abs, "hvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("q:∈ℕ",
						"p≔TRUE"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p", "q");
		addInitialisation(mac, "p", "q");
		IEvent evt = addEvent(mac, "evt", makeSList("x", "y"),
				makeSList("G1", "G2"), makeSList("x∈ℕ", "y∈BOOL"),
				makeSList("A1", "A2"), makeSList("p≔y", "q:∈{x}"));
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEventRefines(evt, "hvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, EventMergeActionError));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * parameter witness ok
	 */
	@Test
	public void testEvents_25_parameterWitnessRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), true);
		addInitialisation(abs, "p");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		IEvent fvt = addEvent(mac, "fvt", makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("p:∈{p}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x"), makeSList("x=p"));

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "fvt");
		containsWitnesses(events[1], typeEnvironment, makeSList("x"),
				makeSList("x=p"));
	}

	/*
	 * parameter witnesses in split ok
	 */
	@Test
	public void testEvents_26_parameterWitnessSplit() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), true);
		addInitialisation(abs, "p");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt");
		containsWitnesses(events[2], typeEnvironment, makeSList("x"),
				makeSList("x=p"));
	}

	/*
	 * post-values of abstract disappearing variables must not be referenced in
	 * witnesses
	 */
	@Test
	public void testEvents_27_globalWitnessAbstractVariables() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1", "V2");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"), true, true);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A1", "A2"), makeSList("V1≔0", "V2≔0"));
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(
				"A1", "A2"), makeSList("V1:∈ℕ", "V2:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V3");
		addInvariants(mac, makeSList("I1"), makeSList("V3∈ℕ"), false);
		addInitialisation(mac, "V3");
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=V2'+V3"));
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(evt.getWitnesses()[0], PREDICATE_ATTRIBUTE, 4, 7,
						WitnessFreeIdentifierError, "V2'"),
				marker(evt, WitnessLabelMissingWarning, "V1'"),
				marker(evt, WitnessLabelMissingWarning, "V2'"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V2=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsWitnesses(events[1], typeEnvironment, makeSList("V1'", "V2'"),
				makeSList("⊤", "⊤"));
	}

	/*
	 * Extended events should not have witnesses for parameters
	 */
	@Test
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
		runBuilderCheck();

		ISCMachineRoot file = ref.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());
	}

	/*
	 * Extended events should not refer to variables that exist no longer
	 */
	@Test
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
		final IEvent init = addInitialisation(ref, "B");
		// TODO why need a local witness
		addEventWitness(init, "A'", "A' = B'");
		IEvent evt = addExtendedEvent(ref, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(ref);
		runBuilderCheck(marker(evt, UndeclaredFreeIdentifierError, "A"));

		ISCMachineRoot file = ref.getSCMachineRoot();

		getSCEvents(file, INITIALISATION);
	}

	/*
	 * Extended events should not refer to variables that exist no longer
	 */
	@Test
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
		runBuilderCheck();

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"A=ℤ; B=ℤ; x=ℤ; y=ℤ", factory);

		ISCMachineRoot file = ref.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		containsParameters(events[1], "x", "y");
		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"x∈ℕ", "y∈ℕ"));
		containsActions(events[1], environment, makeSList("S", "T"), makeSList(
				"A≔A+1", "B≔B+1"));
	}

	/*
	 * labels of merged event actions must correspond (otherwise the ensueing
	 * POG would depend on the order of the events in the file, or create
	 * unreadable PO names)
	 */
	@Test
	public void testEvents_31_mergeAbstractActionLabelsDiffer()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addInitialisation(abs, "p", "q");
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A3"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔TRUE",
						"q:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p", "q");
		addInitialisation(mac, "p", "q");
		IEvent evt = addEvent(mac, "evt", makeSList("x", "y"),
				makeSList("G1", "G2"), makeSList("x∈ℕ", "y∈BOOL"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, EventMergeLabelError));

		ISCMachineRoot file = mac.getSCMachineRoot();

		getSCEvents(file, INITIALISATION, "fvt");
	}

	/*
	 * create default witnesses for abstract variables and parameters in merge
	 */
	@Test
	public void testEvents_32_mergeAbstractActionCreateDefaultWitnesses()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p", "q");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("p∈BOOL", "q∈ℕ"), true, true);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q≔0"));
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList("A1", "A2"), makeSList("p≔TRUE", "q:∈ℕ"));
		addEvent(abs, "gvt", makeSList("y"), makeSList("G1"),
				makeSList("y∈BOOL"), makeSList("A1", "A2"), makeSList("p≔TRUE",
						"q:∈ℕ"));

		saveRodinFileOf(abs);

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL; q=ℤ; x=ℤ; y=BOOL", factory);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		addEvent(mac, "fvt");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, WitnessLabelMissingWarning, "q'"),
				marker(evt, WitnessLabelMissingWarning, "x"),
				marker(evt, WitnessLabelMissingWarning, "y"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt");
		containsWitnesses(events[1], environment, makeSList("q'", "x", "y"),
				makeSList("⊤", "⊤", "⊤"));
	}

	/**
	 * events in refined machine can assign to variables that were declared in
	 * the abstract machine and repeated in the concrete machine.
	 */
	@Test
	public void testEvents_33_newEventPreservedVariableAssigned()
			throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I"), makeSList("p∈BOOL"), true);
		addInitialisation(abs, "p");

		saveRodinFileOf(abs);

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p=BOOL", factory);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "p");
		addInitialisation(mac, "p");
		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A"), makeSList("p≔TRUE"));

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		containsActions(events[1], environment, makeSList("A"),
				makeSList("p≔TRUE"));
	}

	/*
	 * Witnesses must not reference post values of abstract disappearing global
	 * variables
	 */
	@Test
	public void testEvents_34_parameterWitnessWithPostValues() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "p");
		addInvariants(abs, makeSList("I1"), makeSList("p∈ℕ"), false);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("p≔0"));
		addEvent(abs, "evt", makeSList("x", "y"), makeSList("G1", "G2"),
				makeSList("x∈ℕ", "y∈ℕ"), makeSList("A1"), makeSList("p:∈{x}"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "q");
		addInvariants(mac, makeSList("I1"), makeSList("q≠p"), false);
		addInitialisation(mac, "q");
		IEvent fvt = addEvent(mac, "fvt", makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("q:∈{q}"));
		addEventRefines(fvt, "evt");
		addEventWitnesses(fvt, makeSList("x", "y", "p'"), makeSList("x=p'",
				"y=q'", "q'≠p'"));

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(fvt.getWitnesses()[0], PREDICATE_ATTRIBUTE, 2, 4,
						WitnessFreeIdentifierError, "p'"),
				marker(fvt, WitnessLabelMissingWarning, "x"));

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"p'=ℤ; q'=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "fvt");
		containsWitnesses(events[1], environment, makeSList("x", "y", "p'"),
				makeSList("⊤", "y=q'", "q'≠p'"));
	}

	/*
	 * A concrete machine must not declare a variable that has the same name as
	 * a parameter in the abstract machine.
	 */
	@Test
	public void testEvents_35_parameterRefByVariableConflict() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		saveRodinFileOf(abs);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		final IEvent ini = addInitialisation(mac, "x");
		final IEvent fvt = addEvent(mac, "evt");
		addEventRefines(fvt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictError, "x"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"),
				marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 0, 1,
						UndeclaredFreeIdentifierError, "x"),
				marker(ini.getActions()[0], ASSIGNMENT_ATTRIBUTE,
						AssignedIdentifierNotVariableError, "x"),
				marker(fvt, WitnessLabelMissingWarning, "x"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		containsWitnesses(events[1], emptyEnv, makeSList("x"), makeSList("⊤"));
	}

	/*
	 * A concrete machine must not declare a variable that has the same name as
	 * a parameter in the abstract machine. This is the same test as
	 * testEvents_35_parameterRefByVariableConflict, but with an extended event.
	 */
	@Test
	public void testEvents_35bis_parameterRefByVariableConflict() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),
				makeSList(), makeSList());
		saveRodinFileOf(abs);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		final IEvent ini = addInitialisation(mac, "x");
		final IEvent evt = addExtendedEvent(mac, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictError, "x"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"),
				marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 0, 1,
						UndeclaredFreeIdentifierError, "x"),
				marker(ini.getActions()[0], ASSIGNMENT_ATTRIBUTE,
						AssignedIdentifierNotVariableError, "x"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		containsWitnesses(events[0], emptyEnv, makeSList(), makeSList());
	}

	/*
	 * variables that are not in the concrete machine cannot be assigned to or
	 * from
	 */
	@Test
	public void testEvents_36_disappearedVariableAssigned() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("V1:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"), false);
		final IEvent ini = addInitialisation(mac, "V2");
		addEventWitness(ini, "V1'", "V1' = V2'");
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList("A2", "A3"), makeSList("V1:∈ℕ", "V2≔V1"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("⊤"));
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(evt.getActions()[0], ASSIGNMENT_ATTRIBUTE,
						VariableHasDisappearedError, "V1"),
				marker(evt.getActions()[1], ASSIGNMENT_ATTRIBUTE,
						VariableHasDisappearedError, "V1"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V2=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsActions(events[1], typeEnvironment, makeSList(), makeSList());
	}

	/*
	 * variables that are not in the concrete machine cannot be in guards
	 */
	@Test
	public void testEvents_37_disappearedVariableInGuard() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, "V1");
		addEvent(abs, "evt", makeSList(), makeSList("G1"), makeSList("V1>0"),
				makeSList(), makeSList());

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I1"), makeSList("V2∈ℕ"), false);
		final IEvent ini = addInitialisation(mac, "V2");
		addEventWitness(ini, "V1'", "V1' = V2'");
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList("G2", "G3"),
				makeSList("V1>0", "V2>0"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt.getGuards()[0], PREDICATE_ATTRIBUTE, 0, 2,
				VariableHasDisappearedError, "V1"));

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ; V2=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		refinesEvents(events[1], "evt");

		containsGuards(events[1], typeEnvironment, makeSList("G3"),
				makeSList("V2>0"));
	}

	/*
	 * Check that there are no witnesses for an extended event.
	 */
	@Test
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

		addExtendedEvent(con, INITIALISATION);
		IEvent evt1 = addExtendedEvent(con, "evt");
		addEventRefines(evt1, "evt");

		saveRodinFileOf(con);

		runBuilderCheck();

		ISCMachineRoot file = con.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsWitnesses(events[1], emptyEnv, makeSList(), makeSList());

	}

	/*
	 * An extended event must (re-)declare a parameter that was already declared
	 * in an abstract event.
	 */
	@Test
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

		runBuilderCheck(
				marker(evt, EXTENDED_ATTRIBUTE,
						ParameterNameImportConflictWarning, "a", "abs"),
				marker(evt.getParameters()[0], IDENTIFIER_ATTRIBUTE,
						ParameterNameConflictError, "a"));

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsParameters(events[1], "a", "b");
	}

	/*
	 * An extended event must reuse a guard label
	 */
	@Test
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

		runBuilderCheck(
				// TODO should be warning for extended attribute
				marker(evt, EXTENDED_ATTRIBUTE, GuardLabelConflictError, "G"),
				marker(evt.getGuards()[0], LABEL_ATTRIBUTE,
						GuardLabelConflictError, "G"),
				marker(evt.getActions()[0], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "G"));

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);

		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"1 ∈ ℕ", "5=1"));

		containsActions(events[1], environment, makeSList("A"),
				makeSList("y≔y−1"));
	}

	/*
	 * An extended event must reuse an action label
	 */
	@Test
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

		runBuilderCheck(
				// TODO should be warning for extended attribute
				marker(evt, EXTENDED_ATTRIBUTE, ActionLabelConflictError, "A"),
				marker(evt.getGuards()[0], LABEL_ATTRIBUTE,
						GuardLabelConflictError, "A"),
				marker(evt.getActions()[0], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "A"));

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);

		containsGuards(events[1], environment, makeSList("H"), makeSList("5=1"));

		containsActions(events[1], environment, makeSList("A", "B"), makeSList(
				"x:∈ℕ", "y≔y−1"));
	}

	/*
	 * An extended event copies the abstract parameters and adds the concrete
	 * ones
	 */
	@Test
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

		runBuilderCheck();

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsParameters(events[1], "a", "b");

	}

	/*
	 * An extended event copies the abstract guards and adds the concrete ones
	 */
	@Test
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

		runBuilderCheck();

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"a=ℤ; b=ℤ", factory);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsGuards(events[1], environment, makeSList("G", "H"), makeSList(
				"a ∈ ℕ", "b=1"));

	}

	/*
	 * An extended event copies the abstract actions and adds the concrete ones
	 */
	@Test
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

		runBuilderCheck();

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ", factory);

		ISCMachineRoot file = ref.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsActions(events[1], environment, makeSList("A", "B"), makeSList(
				"x≔x+1", "y≔y+1"));

	}

	/*
	 * An extended event copies elements transitively from its abstractions
	 */
	@Test
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

		runBuilderCheck();

		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ; z=ℤ; a=ℤ; b=ℤ; c=ℤ", factory);

		ISCMachineRoot file = con.getSCMachineRoot();
		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");

		containsParameters(events[1], "a", "b", "c");
		containsGuards(events[1], environment, makeSList("G", "H", "D"),
				makeSList("a<x", "b<y", "c<z"));
		containsActions(events[1], environment, makeSList("A", "B", "C"),
				makeSList("x≔x+1", "y≔y+1", "z≔z+1"));

	}

	/*
	 * extended initialization implicitly refined
	 */
	@Test
	public void testEvents_46_initialisationExtendedRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addEvent(abs, INITIALISATION);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, INITIALISATION);
		setExtended(ini);

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		refinesEvents(events[0], INITIALISATION);
	}

	/*
	 * extended initialization action label conflict and checked actions copied
	 */
	@Test
	public void testEvents_47_initialisationExtendedActions() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x", "y");
		addInvariants(abs, makeSList("I", "J"), makeSList("x∈ℕ", "y∈ℕ"), false, false);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A", "B"), makeSList("x:∈ℕ", "y:∈ℕ"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "x", "y", "z");
		addInvariant(mac, "K", "z∈ℕ", false);
		addMachineRefines(mac, "abs");
		IEvent ini = addEvent(mac, INITIALISATION, makeSList(), makeSList(),
				makeSList(), makeSList("B", "C"), makeSList("y:∈{0,1}", "z:∈ℕ"));
		setExtended(ini);

		saveRodinFileOf(mac);

		runBuilderCheck(
				// TODO should be warning for extended attribute
				marker(ini, EXTENDED_ATTRIBUTE, ActionLabelConflictError, "B"),
				marker(ini.getActions()[0], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "B"));
		
		ITypeEnvironmentBuilder environment = mTypeEnvironment(
				"x=ℤ; y=ℤ; z=ℤ", factory);

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		refinesEvents(events[0], INITIALISATION);
		containsActions(events[0], environment, 
				makeSList("A", "B", "C"), makeSList("x:∈ℕ", "y:∈ℕ", "z:∈ℕ"));
	}

	/*
	 * There exists an abstract event with the same name but no refines clause
	 * pointing to it.
	 */
	@Test
	public void testEvents_48_sameNameNoRefines() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addInitialisation(abs);
		addEvent(abs, "evt");

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				// TODO should rather be missing refines event clause
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						AbstractEventNotRefinedWarning, "evt"),
				marker(evt, InconsistentEventLabelWarning, "evt"));
	}
	
	/*
	 * There exist an abstract event which is not refined and has not a guard
	 * with the literal predicate "false". Thus it appears unexplicitely
	 * disabled.
	 */
	@Test
	public void testEvents_49_AbstractEventNotExplicitelyDisabled()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt");
		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		saveRodinFileOf(mac);

		runBuilderCheck(marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
				AbstractEventNotRefinedWarning, "evt"));
	}

	/*
	 * There exist an abstract event which is not refined and has a guard with
	 * the literal predicate "false". Thus it appears explicitely disabled.
	 */
	@Test
	public void testEvents_50_AbstractEventExplicitelyDisabled()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt", makeSList(), makeSList("falseGuard"),
				makeSList("⊥"), makeSList(), makeSList());
		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		saveRodinFileOf(mac);

		runBuilderCheck();
	}

	/*
	 * Ensure that two events with different parameters can be merged, as far as
	 * their parameters do not occur in the actions.
	 */
	@Test
	public void testEvents_51_mergeDifferentParameters() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, "v");
		addInvariant(abs, "I1", "v∈ℕ", false);
		addInitialisation(abs, makeSList("A1"), makeSList("v≔0"));
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList("A1"), makeSList("v≔v+1"));
		addEvent(abs, "gvt",//
				makeSList("y"), makeSList("G1"), makeSList("y∈BOOL"),//
				makeSList("A1"), makeSList("v≔v+1"));
		saveRodinFileOf(abs);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "v");
		addInitialisation(mac, makeSList("A1"), makeSList("v≔0"));
		final IEvent fvt = addEvent(mac, "fvt",//
				makeSList(), makeSList(), makeSList(),//
				makeSList("A1"), makeSList("v≔v+1"));
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "gvt");
		addEventWitnesses(fvt, "x", "x = 1", "y", "y = TRUE");
		saveRodinFileOf(mac);

		runBuilderCheck();
	}

	/*
	 * Ensures that a collision between an abstract event parameter and a
	 * carrier set is properly reported (bug #712).
	 */
	@Test
	public void testEvents_52_abstractParameterCollidesWithSet() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IContextRoot ctx = createContext("ctx");
		addCarrierSets(ctx, "x");
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventWitness(fvt, "x", "x = 0");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"));
	}

	/*
	 * Ensures that a collision between an abstract event parameter and a
	 * constant is properly reported (bug #712).
	 */
	@Test
	public void testEvents_53_abstractParameterCollidesWithConstant() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IContextRoot ctx = createContext("ctx");
		addConstants(ctx, "x");
		addAxiom(ctx, "A1", "x ∈ BOOL", true);
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventWitness(fvt, "x", "x = 0");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						ConstantNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"));
	}

	/*
	 * Ensures that a collision between an abstract event parameter which comes
	 * from an extended event and a constant is properly reported (bug #712).
	 */
	@Test
	public void testEvents_54_abstractExtendedParameterCollidesWithConstant() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addInitialisation(ref);
		final IEvent ref_evt = addExtendedEvent(ref, "evt");
		addEventRefines(ref_evt, "evt");
		saveRodinFileOf(ref);

		final IContextRoot ctx = createContext("ctx");
		addConstants(ctx, "x");
		addAxiom(ctx, "A1", "x∈ℕ", true);
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "ref");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventWitness(fvt, "x", "x = 0");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						ConstantNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"));
	}
	
	/*
	 * Ensures that a collision between an extended parameter and a carrier set
	 * is properly reported (bug #712).
	 */
	@Test
	public void testEvents_55_extendedParameterCollidesWithSet() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IContextRoot ctx = createContext("ctx");
		addCarrierSets(ctx, "x");
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent evt = addExtendedEvent(mac, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"));
	}

	/*
	 * Ensures that a collision between an extended parameter and a constant
	 * is properly reported (bug #712).
	 */
	@Test
	public void testEvents_56_extendedParameterCollidesWithConstant() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IContextRoot ctx = createContext("ctx");
		addConstants(ctx, "x");
		addAxiom(ctx, "A1", "x ∈ BOOL", true);
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent evt = addExtendedEvent(mac, "evt");
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						ConstantNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"));
	}

	/*
	 * Ensures that a collision between several abstract parameters and a
	 * carrier set is properly reported (bug #712). Variant where the abstract
	 * parameters bear different types.
	 */
	@Test
	public void testEvents_57_abstractParametersCollideWithSet() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		addEvent(abs, "fvt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈BOOL"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);
		
		final IContextRoot ctx = createContext("ctx");
		addCarrierSets(ctx, "x");
		saveRodinFileOf(ctx);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addInitialisation(mac);
		final IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventWitness(evt, "x", "x = 0");
		final IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "fvt");
		addEventWitness(fvt, "x", "x = TRUE");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "x", "ctx"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "fvt"));
	}

	/*
	 * A concrete machine must not declare a variable that has the same name as
	 * parameters in the abstract machine. Variant with several abstract
	 * parameters with the same name, but different types.
	 */
	@Test
	public void testEvents_58_parametersRefByVariableConflict() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addEvent(abs, "evt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈ℕ"),//
				makeSList(), makeSList());
		addEvent(abs, "fvt",//
				makeSList("x"), makeSList("G1"), makeSList("x∈BOOL"),//
				makeSList(), makeSList());
		saveRodinFileOf(abs);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		final IEvent ini = addInitialisation(mac, "x");
		final IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		final IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "fvt");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictError, "x"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "evt"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						ParameterNameImportConflictWarning, "x", "fvt"),
				marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 0, 1,
						UndeclaredFreeIdentifierError, "x"),
				marker(ini.getActions()[0], ASSIGNMENT_ATTRIBUTE,
						AssignedIdentifierNotVariableError, "x"),
				marker(evt, WitnessLabelMissingWarning, "x"),
				marker(fvt, WitnessLabelMissingWarning, "x"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt");
		containsWitnesses(events[1], emptyEnv, makeSList("x"), makeSList("⊤"));
		containsWitnesses(events[2], emptyEnv, makeSList("x"), makeSList("⊤"));
	}

}
