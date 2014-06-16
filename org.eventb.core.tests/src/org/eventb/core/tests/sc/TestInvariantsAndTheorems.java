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

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.sc.ParseProblem.TypeUnknownError;
import static org.eventb.core.sc.ParseProblem.TypesDoNotMatchError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestInvariantsAndTheorems extends GenericPredicateTest<IMachineRoot, ISCMachineRoot> {
		
	/**
	 * propagation of carrier set types, partial typing
	 */
	@Test
	public void testInvariantsAndTheorems_05_carrierSetType() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);

		IMachineRoot mac = createMachine("mac");
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("S1=ℙ(S1)",
				factory);

		addMachineSees(mac, "ctx");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1", "I2"), makeSList("V1∈ℕ∪S1", "V1∈S1"), false, false);
		addInitialisation(mac, "V1");
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 3,
				7, TypesDoNotMatchError, "ℤ", "S1"));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, makeSList("I2"), makeSList("V1∈S1"), false);
	}
	
	/**
	 * more on partial typing (more complex set up)
	 */
	@Test
	public void testInvariantsAndTheorems_06_partialTyping() throws Exception {
		IContextRoot con =  createContext("ctx");
		addCarrierSets(con, "S1");
	
		saveRodinFileOf(con);

		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment("S1=ℙ(S1); V1=S1",
				factory);
		
		IMachineRoot mac = createMachine("mac");
		addMachineSees(mac, "ctx");
		addVariables(mac, "V1");
		addInvariants(mac, 
				makeSList("I1", "I2", "I3", "I4"), 
				makeSList("V1=V1", "V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"),
				false, false, false, false);
		addInitialisation(mac, "V1");
	
		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(mac.getInvariants()[0], PREDICATE_ATTRIBUTE, 0,
				2, TypeUnknownError));
		
		ISCMachineRoot file = mac.getSCMachineRoot();
		
		containsInvariants(file, typeEnvironment, 
				makeSList("I2", "I3", "I4"), 
				makeSList("V1∈S1", "V1∈{V1}", "S1 ⊆ {V1}"),
				false, false, false);
	}

	@Override
	protected IGenericSCTest<IMachineRoot, ISCMachineRoot> newGeneric() {
		return new GenericMachineSCTest(this);
	}
	
}
