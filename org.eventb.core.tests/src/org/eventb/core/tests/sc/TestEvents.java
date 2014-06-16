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
 *     Systerel - ensure that all AST problems are reported
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - added a test for extended initialization repairing
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.IEvent.INITIALISATION;
import static org.eventb.core.sc.GraphProblem.ActionDisjointLHSError;
import static org.eventb.core.sc.GraphProblem.ActionLabelConflictError;
import static org.eventb.core.sc.GraphProblem.AssignmentToParameterError;
import static org.eventb.core.sc.GraphProblem.EmptyLabelError;
import static org.eventb.core.sc.GraphProblem.EventInitLabelMisspellingWarning;
import static org.eventb.core.sc.GraphProblem.EventLabelConflictError;
import static org.eventb.core.sc.GraphProblem.GuardLabelConflictError;
import static org.eventb.core.sc.GraphProblem.GuardLabelConflictWarning;
import static org.eventb.core.sc.GraphProblem.InitialisationActionRHSError;
import static org.eventb.core.sc.GraphProblem.InitialisationGuardError;
import static org.eventb.core.sc.GraphProblem.InitialisationIncompleteWarning;
import static org.eventb.core.sc.GraphProblem.InitialisationVariableError;
import static org.eventb.core.sc.GraphProblem.MachineWithoutInitialisationWarning;
import static org.eventb.core.sc.GraphProblem.ParameterNameConflictError;
import static org.eventb.core.sc.GraphProblem.PredicateUndefError;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.UntypedParameterError;
import static org.eventb.core.sc.GraphProblem.VariableHasDisappearedError;
import static org.eventb.core.sc.GraphProblem.VariableNameConflictWarning;
import static org.eventb.core.sc.GraphProblem.WitnessLabelMissingWarning;
import static org.eventb.core.sc.ParseProblem.LexerError;
import static org.eventb.core.sc.ParseProblem.TypesDoNotMatchError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.IWitness;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEvents extends BasicSCTestWithFwdConfig {
	
	/*
	 * create an event
	 */
	@Test
	public void testEvents_00_createEvent() throws Exception {
		IMachineRoot mac = createMachine("mac");
		
		addInitialisation(mac);

		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, INITIALISATION, "evt");
	}

	/*
	 * create two events
	 */
	@Test
	public void testEvents_01_createTwoEvents() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, INITIALISATION, "evt1", "evt2");
	}
	
	/*
	 * create two events with name conflict
	 */
	@Test
	public void testEvents_02_createTwoEventsWithNameConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);

		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		IEvent f = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				// TODO should be warning for "e"
				marker(e, LABEL_ATTRIBUTE, EventLabelConflictError, "evt1"),
				marker(f, LABEL_ATTRIBUTE, EventLabelConflictError, "evt1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, INITIALISATION);
	}
	
	/*
	 * create guard
	 */
	@Test
	public void testEvents_03_createGuard() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList("G1"), makeSList("1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
	}

	/*
	 * create two guards
	 */
	@Test
	public void testEvents_04_createTwoGuards() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList(), makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], emptyEnv, makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"));
	}

	/*
	 * create two guards with label conflict (last one is filtered)
	 */
	@Test
	public void testEvents_05_createTwoGuardsWithLabelConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);

		IEvent e = 
			addEvent(mac, "evt1", 
					makeSList(), 
					makeSList("G1", "G1"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getGuards()[0], LABEL_ATTRIBUTE,
						GuardLabelConflictWarning, "G1"),
				marker(e.getGuards()[1], LABEL_ATTRIBUTE,
						GuardLabelConflictError, "G1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
	}
	
	/*
	 * create local variable
	 */
	@Test
	public void testEvents_06_parameter() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addInitialisation(mac);
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
	}

	/*
	 * create global and local variable
	 */
	@Test
	public void testEvents_07_variableAndParameter() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1⊆ℕ"), true);
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈V1"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈V1"));
	}
	
	/*
	 * create global and local variable with name conflict (local variable is filtered)
	 */
	@Test
	public void testEvents_08_variableAndParameterWithNameConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"), true);
		addInitialisation(mac, "L1");
		IEvent e = 
			addEvent(mac, "evt1", 
					makeSList("L1"), 
					makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(mac.getVariables()[0], IDENTIFIER_ATTRIBUTE,
						VariableNameConflictWarning, "L1"),
				marker(e.getParameters()[0], IDENTIFIER_ATTRIBUTE,
						ParameterNameConflictError, "L1"),
				marker(e.getGuards()[0], PREDICATE_ATTRIBUTE, 0, 4,
						TypesDoNotMatchError, "ℤ", "ℙ(ℤ)"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1]);
		containsGuards(scEvents[1], typeEnvironment, makeSList(), makeSList());
}
	
	/*
	 * create parameters with same name but different types in different events
	 */
	@Test
	public void testEvents_09_parametersOfDifferentEvents() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"), true);
		addInitialisation(mac, "L1");
		addEvent(mac, "evt1", makeSList("L2"), makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList("L2"), makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1", "evt2");
		
		containsParameters(scEvents[1], "L2");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L2∈ℕ"));
		
		containsParameters(scEvents[2], "L2");
		containsGuards(scEvents[2], typeEnvironment, makeSList("G1"), makeSList("L2⊆ℕ"));	
	}
	
	/*
	 * actions can assign to global variables
	 */
	@Test
	public void testEvents_10_globalVariableInAction() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"), true);
		addInitialisation(mac, "L1");
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList("A1"), makeSList("L1≔1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
	}
	
	/*
	 * error: two actions cannot assign to same variable
	 */
	@Test
	public void testEvents_11_actionSameLHSConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"), true);
		addInitialisation(mac, "L1");
		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), 
				makeSList("A1","A1"), makeSList("L1≔1", "L1≔2"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				// TODO shoud be warning for first action
				marker(e.getActions()[0], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "A1"),
				marker(e.getActions()[1], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "A1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
	}
	
	/*
	 * action and gaurd labels in the same event must be different (actions filtered)
	 */
	@Test
	public void testEvents_12_actionGuardLabelConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("L1=ℤ;",
				factory);

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"), true);
		addInitialisation(mac, "L1");
		IEvent e = addEvent(mac, "evt1", makeSList(), makeSList("A1"), makeSList("1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getGuards()[0], LABEL_ATTRIBUTE,
						GuardLabelConflictWarning, "A1"),
				marker(e.getActions()[0], LABEL_ATTRIBUTE,
						ActionLabelConflictError, "A1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsGuards(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("1∈ℕ"));
		containsActions(scEvents[1], typeEnvironment, makeSList(), makeSList());
	}
	
	/*
	 * local variables may appear on the RHS of an assignment
	 */
	@Test
	public void testEvents_13_actionAssignFromLocalVariableOK() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ;",
				factory);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, INITIALISATION, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("V1≔L1"));
	}
	
	/*
	 * error: cannot assign to local variable
	 */
	@Test
	public void testEvents_14_assignmentToParameterProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ;",
				factory);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(mac, "V1");
		IEvent e = addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔V1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(e.getActions()[0], ASSIGNMENT_ATTRIBUTE,
				AssignmentToParameterError, "L1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1], "L1");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[1], typeEnvironment, makeSList(), makeSList());
	}
	
	/*
	 * action with nondetermistic assignment
	 */
	@Test
	public void testEvents_15_actionNondetAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ; V1'=ℤ",
				factory);

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(mac, "V1");
		addEvent(mac, "evt1", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsEvents(file, INITIALISATION, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsParameters(scEvents[1]);
		containsGuards(scEvents[1], typeEnvironment, makeSList(), makeSList());
		containsActions(scEvents[1], typeEnvironment, makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
	}
	
	/*
	 * multiple assignment to same variables problem
	 */
	@Test
	public void testEvents_16_actionMultipleLHSConflict() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("u=ℤ; x=ℤ",
				factory);

		addVariables(mac, "u", "v", "w", "x", "y", "z");
		addInvariants(mac, makeSList("I1"), makeSList("u∈ℕ ∧ v∈ℕ ∧ w∈ℕ ∧ x∈ℕ ∧ y∈ℕ ∧ z∈ℕ"), true);
		addInitialisation(mac, "u", "v", "w", "x", "y", "z");
		IEvent e = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2", "A3", "A4", "A5"), 
				makeSList("u≔1", "v,w:∣⊤", "v≔1", "x≔u", "y,z,w≔v,w,1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getActions()[1], ASSIGNMENT_ATTRIBUTE,
						ActionDisjointLHSError),
				marker(e.getActions()[2], ASSIGNMENT_ATTRIBUTE,
						ActionDisjointLHSError),
				marker(e.getActions()[4], ASSIGNMENT_ATTRIBUTE,
						ActionDisjointLHSError));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "u", "v", "w", "x", "y", "z");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt");
		
		containsActions(scEvents[1], typeEnvironment, makeSList("A1", "A4"), makeSList("u≔1", "x≔u"));
	}
	
	/*
	 * initialisation must not have local variables
	 */
	@Test
	public void testEvents_17_initialisationParameterProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		IEvent e = addEvent(mac, INITIALISATION, makeSList("x"), 
				makeSList("G1"), makeSList("x∈ℕ"), 
				makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getParameters()[0], InitialisationVariableError),
				// TODO do not issue warning for untyped
				marker(e.getParameters()[0], IDENTIFIER_ATTRIBUTE,
						UntypedParameterError, "x"),
				// TODO should not be on predicate attribute
				marker(e.getGuards()[0], PREDICATE_ATTRIBUTE,
						InitialisationGuardError));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsParameters(scEvents[0]);
		containsGuards(scEvents[0], emptyEnv, makeSList(), makeSList());
	}
	
	/*
	 * initialisation must not have guards
	 */
	@Test
	public void testEvents_18_initialisationGuardProblem() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		IEvent e = addEvent(mac, INITIALISATION, makeSList(), 
				makeSList("G1", "G2"), makeSList("x∈ℕ", "⊤"), 
				makeSList("A1"), makeSList("x ≔ 0"));
	
		saveRodinFileOf(mac);
		
		// TODO should not be on predicate attribute
		runBuilderCheck(
				marker(e.getGuards()[0], PREDICATE_ATTRIBUTE,
						InitialisationGuardError),
				marker(e.getGuards()[1], PREDICATE_ATTRIBUTE,
						InitialisationGuardError));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsGuards(scEvents[0], emptyEnv, makeSList(), makeSList());
	}
	
	/*
	 * nondeterministic assignments can only refer to post-state of variables
	 * occurring on their left hand sides
	 */
	@Test
	public void testEvents_19_actionFaultyNondetAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		addInitialisation(mac, "x");
		IEvent e = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ y'=x"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(e.getActions()[0], ASSIGNMENT_ATTRIBUTE, 5, 7,
				UndeclaredFreeIdentifierError, "y'"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt");
		
		containsActions(scEvents[1], emptyEnv, makeSList(), makeSList());
	}
	
	/*
	 * nondetermistic initialisation ok
	 */
	@Test
	public void testEvents_20_initialisationNondetAssignmentOK() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ",
				factory);

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		addEvent(mac, INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ x'=1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("x :∣ x'=1"));
	}
	
	/*
	 * faulty initialisation replaced by default initialisation
	 */
	@Test
	public void testEvents_21_initialisationNondetAssignmentReplacedByDefault() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ",
				factory);

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		IEvent e = addEvent(mac, INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x :∣ x=1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getActions()[0], ASSIGNMENT_ATTRIBUTE, 5, 6,
						InitialisationActionRHSError, "x"),
				marker(e, InitialisationIncompleteWarning, "x"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("GEN"), makeSList("x :∣ ⊤"));
	}
	
	/*
	 * creation of default initialisation
	 */
	@Test
	public void testEvents_22_initialisationDefaultAssignment() throws Exception {
		IMachineRoot mac = createMachine("mac");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ",
				factory);

		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		IEvent e = addEvent(mac, INITIALISATION, makeSList(), 
				makeSList(), makeSList(), 
				makeSList(), makeSList());
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(e, InitialisationIncompleteWarning, "x"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsActions(scEvents[0], typeEnvironment, makeSList("GEN"), makeSList("x :∣ ⊤"));
	}
	
	/*
	 * creation of default witness for initialisation
	 */
	@Test
	public void testEvents_23_initialisationDefaultWitness() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x∈ℕ"), true);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("x:∣x'=1"));
	
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; x'=ℤ; y=ℤ",
				factory);

		addVariables(mac, "y");
		addInvariants(mac, makeSList("I1"), makeSList("y∈ℕ"), true);
		IEvent e = addEvent(mac, INITIALISATION, makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("x:∣x'=2"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(e.getActions()[0], ASSIGNMENT_ATTRIBUTE,
						VariableHasDisappearedError, "x"),
				marker(e, WitnessLabelMissingWarning, "x'"),
				marker(e, InitialisationIncompleteWarning, "y"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsWitnesses(scEvents[0], typeEnvironment, makeSList("x'"), makeSList("⊤"));
	}
	
	/*
	 * faulty witness in initialisation replaced by default witness
	 */
	@Test
	public void testEvents_24_initialisationFaultyWitnessReplacedByDefault() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x∈ℕ"), true);
		addEvent(abs, INITIALISATION, makeSList(), makeSList(), makeSList(),
				makeSList("A1"), makeSList("x:∣x'∈{1}"));
	
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; x'=ℤ; y=ℤ",
				factory);

		addVariables(mac, "y");
		addInvariants(mac, makeSList("I1"), makeSList("y∈ℕ"), true);
		IEvent evt = addEvent(mac, INITIALISATION, makeSList(), makeSList(),
				makeSList(), makeSList("A1"), makeSList("y≔2"));
		addEventWitnesses(evt, makeSList("x'"), makeSList("x'=y−1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(
				// TODO wrong error message?
				marker(evt.getWitnesses()[0], PREDICATE_ATTRIBUTE, 3, 4,
						InitialisationActionRHSError, "y"),
				marker(evt, WitnessLabelMissingWarning, "x'"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION);
		
		containsWitnesses(scEvents[0], typeEnvironment, makeSList("x'"), makeSList("⊤"));
	}
	
	/**
	 * an empty machine must have an initialisation
	 */
	@Test
	public void testEvents_25_emptyMachineWithoutInit() throws Exception {
		IMachineRoot mac = createMachine("mac");

		mac.getRodinFile().save(null, true);
		
		runBuilderCheck(marker(mac.getRodinFile(),
				MachineWithoutInitialisationWarning));
	}

	/**
	 * A lexical error in an action is reported
	 */
	@Test
	public void testEvents_26_bug2689872() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"), true);
		addInitialisation(mac, "L1");
		IEvent evt = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList("A1"), makeSList("L1 :∣ 0 /= 1"));
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(evt.getActions()[0], ASSIGNMENT_ATTRIBUTE, 8, 9,
				LexerError, "/"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");
		
		containsActions(scEvents[1], emptyEnv, makeSList(), makeSList());
	}

	/**
	 * Guards can also be theorems
	 */
	@Test
	public void testEvents_27_theoremGuard() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);

		addEvent(mac, "evt1", makeSList(), makeSList("G1", "G2"),
				makeSList("⊤", "⊤"), makeBList(false, true), makeSList(),
				makeSList());

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] scEvents = getSCEvents(file, INITIALISATION, "evt1");

		containsGuards(scEvents[1], emptyEnv, makeSList("G1", "G2"), makeSList(
				"⊤", "⊤"), false, true);
	}
	
	/**
	 * Create an event with an empty label.
	 */
	@Test
	public void testEvents_28_createEventWithEmptyLabel() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt, LABEL_ATTRIBUTE, EmptyLabelError));
	}

	/**
	 * Create a guard with an empty label.
	 */
	@Test
	public void testEvents_29_createGuardWithEmptyLabel() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(""),
				makeSList("1∈ℕ"), makeSList(), makeSList());

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt.getGuards()[0], LABEL_ATTRIBUTE,
				EmptyLabelError));
	}

	/**
	 * Create an action an empty label.
	 */
	@Test
	public void testEvents_30_createActionWithEmptyLabel() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList(""), makeSList("L1≔1"));

		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt.getActions()[0], LABEL_ATTRIBUTE,
				EmptyLabelError));
	}
	
	/**
	 * Create a witness with an empty label.
	 */
	@Test
	public void testEvents_31_createWitnessWithEmptyLabel() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IEvent evt = addEvent(mac, "evt");
		IWitness witness = evt.createChild(IWitness.ELEMENT_TYPE, null, null);
		witness.setLabel("", null);

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(witness, LABEL_ATTRIBUTE, EmptyLabelError),
				marker(witness, PREDICATE_ATTRIBUTE, PredicateUndefError));
	}

	/**
	 * creation of default initialization extending default abstraction;
	 * checking that we have markers for each initialization
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEvents_32_initialisationDefaultAssignments()
			throws Exception {
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("x=ℤ; y=ℤ",
				factory);

		final IMachineRoot mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈ℕ"), true);
		final IEvent e = addEvent(mac, INITIALISATION, makeSList(),
				makeSList(), makeSList(), makeSList(), makeSList());
		saveRodinFileOf(mac);

		final IMachineRoot mac2 = createMachine("mac2");
		addVariables(mac2, "x");
		addVariables(mac2, "y");
		addInvariants(mac2, makeSList("I2"), makeSList("y∈ℕ"), true);
		final IEvent e2 = addEvent(mac2, INITIALISATION, makeSList(),
				makeSList(), makeSList(), makeSList(), makeSList());
		e2.setExtended(true, null);
		addMachineRefines(mac2, mac.getElementName());
		saveRodinFileOf(mac2);

		runBuilderCheck(marker(e, InitialisationIncompleteWarning, "x"),
				marker(e2, InitialisationIncompleteWarning, "y"));

		final ISCMachineRoot file2 = mac2.getSCMachineRoot();
		final ISCEvent[] scEvents2 = getSCEvents(file2, INITIALISATION);
		final String[] actionsLabels = { "GEN", "GEN1" };
		final String[] actionsPredicates = { "x :∣ ⊤", "y :∣ ⊤" };
		containsActions(scEvents2[0], typeEnvironment, actionsLabels,
				actionsPredicates);
	}
	
	@Test
	public void testEvents_32_initialisationMisspelled() throws Exception {
		final IMachineRoot mac = createMachine("mac");

		final String initialization = "INITIALIZATION";
		final IEvent init = addEvent(mac, initialization, makeSList(), makeSList(), makeSList(),
				makeSList(), makeSList());

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(init, LABEL_ATTRIBUTE, EventInitLabelMisspellingWarning,
						initialization),
				marker(mac.getRodinFile(), MachineWithoutInitialisationWarning));
	}
	
}
