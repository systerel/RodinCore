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
package org.eventb.core.tests.pog;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineInit extends EventBPOTest {
	
	private static String init = IEvent.INITIALISATION;

	@Test
	public void testInit_00() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(mac, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔1"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ", factory);
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, init + "/I1/INV");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "1∈0‥4");
		
	}
	
	@Test
	public void testInit_01() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈0‥4"), false);
		addEvent(abs, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔1"));
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"V1=ℤ", factory);
		
		saveRodinFileOf(abs);
		
		runBuilder();
		
		IMachineRoot mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addEvent(mac, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1≔2"));
		
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPORoot po = mac.getPORoot();
		
		containsIdentifiers(po, "V1");
		
		IPOSequent sequent = getSequent(po, init + "/A1/SIM");
		
		sequentHasIdentifiers(sequent, "V1'");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "2=1");
		
	}
	
	/*
	 * Ensures that both abstract and concrete actions of the initialisation are
	 * applied to the glueing invariant.
	 */
	@Test
	public void testInit_02() throws Exception {
		
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I1"), makeSList("x ∈ ℤ"), false);
		addEvent(abs, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x≔0"));
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ", factory);
		saveRodinFileOf(abs);
		runBuilder();
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");
		addVariables(con, "y");
		addInvariants(con, makeSList("I1"), makeSList("y = x + 1"), false);
		addEvent(con, init,
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("y ≔ 1"));
		saveRodinFileOf(con);
		runBuilder();
		
		IPORoot po = con.getPORoot();
		IPOSequent sequent = getSequent(po, init + "/I1/INV");
		sequentHasNoHypotheses(sequent);
		sequentHasGoal(sequent, typeEnvironment, "1=0+1");
	}
	
	@Test
	public void testInit_Bug2813537() throws Exception {
		
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x", "y", "z");
		addInvariants(abs, makeSList("I1"), makeSList("x∈ℤ ∧ y∈ℤ ∧ z∈ℤ"), false);
		addEvent(abs, init, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x,y,z ≔ 0,0,0"));
		saveRodinFileOf(abs);
		runBuilder();
		
		IMachineRoot con = createMachine("cnc");
		addMachineRefines(con, "abs");
		addVariables(con, "x");
		addEvent(con, init,
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("x ≔ 0"));
		saveRodinFileOf(con);

		// ArrayIndexOutOfBoundsException is thrown when the bug is present
		// and results in a build problem
		runBuilder();
	}
	
	

}
