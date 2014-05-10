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
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.AbstractMachineWithoutConfigurationError;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameImportConflictError;
import static org.eventb.core.sc.GraphProblem.ConfigurationMissingError;
import static org.eventb.core.sc.GraphProblem.VariableNameImportConflictWarning;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineRefines extends BasicSCTestWithFwdConfig {

	/**
	 * Abstract machine identifiers have precedence over seen context
	 * identifiers: In case of conflict, the seen context is completely ignored.
	 */
	@Test
	public void testMachineRefines_0() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, makeSList("A1"), makeSList("V1 ≔ 0"));
		saveRodinFileOf(abs);

		final IContextRoot con = createContext("ctx");
		addCarrierSets(con, "V1");
		saveRodinFileOf(con);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ctx");
		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"), false);
		addInitialisation(mac, "V2");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						VariableNameImportConflictWarning, "V1", "abs"),
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "V1", "ctx"));

		final ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file);
		getInternalContexts(file, 0);
		containsVariables(file, "V1", "V2");
		containsInvariants(file, mTypeEnvironment("V1=ℤ; V2=ℤ"),
				makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"), false, false);
	}

	/**
	 * Abstract machine identifiers have precedence over seen contexts
	 * identifiers: The whole SEES clause is ignored, rather than just the
	 * context containing the conflicting identifiers.
	 */
	@Test
	public void testMachineRefines_0bis() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, makeSList("A1"), makeSList("V1 ≔ 0"));
		saveRodinFileOf(abs);

		final IContextRoot acon = createContext("acon");
		addCarrierSets(acon, "V1");
		saveRodinFileOf(acon);

		final IContextRoot ccon = createContext("ccon");
		addContextExtends(ccon, "acon");
		saveRodinFileOf(ccon);

		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addMachineSees(mac, "ccon");
		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"), false);
		addInitialisation(mac, "V2");
		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						VariableNameImportConflictWarning, "V1", "abs"),
				marker(mac.getSeesClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "V1", "acon"));

		final ISCMachineRoot file = mac.getSCMachineRoot();
		seesContexts(file);
		getInternalContexts(file, 0);
		containsVariables(file, "V1", "V2");
		containsInvariants(file, mTypeEnvironment("V1=ℤ; V2=ℤ"),
				makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2∈ℕ"), false, false);
	}

	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; carrier sets are propagated
	 */
	@Test
	public void testMachineRefines_1() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);
		
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		
		addMachineSees(abs, "ctx");

		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
	
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");

		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 */
	@Test
	public void testMachineRefines_2() throws Exception {
		IContextRoot con =  createContext("ctx");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), true);
	
		saveRodinFileOf(con);
		
		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "ctx");
		
		addInitialisation(abs);

		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);

		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
	}
	
	/*
	 * refined machine should see at least all contexts seen by
	 * their abstract machine; constants are propagated
	 * when also variables are declared
	 */
	@Test
	public void testMachineRefines_3() throws Exception {
		IContextRoot con =  createContext("ctx");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), true);
	
		saveRodinFileOf(con);
		
		IMachineRoot abs = createMachine("abs");
		
		addMachineSees(abs, "ctx");
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineSees(mac, "ctx");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");
	}
	
	/*
	 * variables are propagated
	 */
	@Test
	public void testMachineRefines_4() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), true);
		addInitialisation(abs, "V1");

		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2=V1+1"), true);
		IEvent ini = addInitialisation(mac, "V2");
		addEventWitness(ini, "V1'", "⊤");
		
		saveRodinFileOf(mac);
		
		runBuilderCheck();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V1=ℤ; V2=ℤ",
				factory);
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V1", "V2");
		containsInvariants(file, typeEnvironment, makeSList("I1", "I2"), makeSList("V1∈ℕ", "V2=V1+1"), true, true);
	}
	
	/*
	 * abstract machine not saved!
	 */
	@Test
	public void testMachineRefines_5() throws Exception {
		IMachineRoot abs = createMachine("abs");
		
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);

		IMachineRoot mac = createMachine("mac");
		
		addMachineRefines(mac, "abs");

		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V2∈ℕ"), false);
		addInitialisation(mac, "V2");

		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(abs, ConfigurationMissingError, "abs"),
				marker(abs.getSCMachineRoot(), ConfigurationMissingError, "abs"),
				marker(mac.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						AbstractMachineWithoutConfigurationError, "abs"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
				
		containsVariables(file, "V2");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("V2=ℤ",
				factory);
		
		containsInvariants(file, typeEnvironment, 
				makeSList("I2"), makeSList("V2∈ℕ"), false);
	}

}
