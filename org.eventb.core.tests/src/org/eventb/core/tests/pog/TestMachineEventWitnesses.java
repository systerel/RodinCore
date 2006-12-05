/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineEventWitnesses extends BasicPOTest {
	
	/**
	 * deterministic witnesses for invariants and simulation
	 */
	public void test_00() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "ax", "ay");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("ax>0", "ay≥6"));
		addEvent(abs, IEvent.INITIALISATION, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("ax,ay :∣ ax'>ay' ∧ ax'=5 ∧ ay'=7"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "cx", "ay");
		addInvariants(mac, makeSList("I3"), makeSList("cx=ax+1"));
		IEvent event = addEvent(mac, IEvent.INITIALISATION, 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2"), makeSList("cx≔8", "ay ≔ 7"));
		addEventWitnesses(event, makeSList("ax'"), makeSList("ax'=cx'+4"));
		
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = makeTypeEnvironment();
		
		IPOFile po = mac.getPOFile();
		
		IPOSequent sequent;
		
		sequent = getSequent(po, IEvent.INITIALISATION + "/I3/INV");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "cx'");
		sequentHasHypotheses(sequent, environment);
		sequentHasGoal(sequent, environment, "8=(8+4)+1");
		
		sequent = getSequent(po, IEvent.INITIALISATION + "/A1/SIM");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "cx'");
		sequentHasHypotheses(sequent, environment);
		sequentHasGoal(sequent, environment, "8+4>7 ∧ 8+4=5 ∧ 7=7");
		
	}

	/**
	 * well-definedness and feasibility of nondeterministic witnesses
	 */
	public void test_01() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "ax", "ay");
		addInvariants(abs, makeSList("I1", "I2"), makeSList("ax>0", "ay≥6"));
		addEvent(abs, "evt", 
				makeSList("pp"), 
				makeSList("G1"), makeSList("pp⊆ℕ∖{0}"), 
				makeSList("A1", "A2"), makeSList("ax:∈pp", "ay≔7"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "cx", "ay");
		addInvariants(mac, makeSList("I3"), makeSList("cx=ay+1"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("qq"), 
				makeSList("G1"), makeSList("qq∈ℕ"), 
				makeSList("A1"), makeSList("cx≔qq"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, makeSList("pp", "ax'"), makeSList("cx÷qq∈pp", "ax'=qq÷ay'"));
		
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = makeTypeEnvironment();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "ax", "ay", "cx");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt/pp/WFIS");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "cx'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "ax>0", "ay≥6", "qq∈ℕ");
		sequentHasGoal(sequent, environment, "∃pp·cx÷qq∈pp");
		
		sequent = getSequent(po, "evt/pp/WWD");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "cx'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "ax>0", "ay≥6", "qq∈ℕ");
		sequentHasGoal(sequent, environment, "qq≠0");
		
		sequent = getSequent(po, "evt/ax'/WWD");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "cx'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "ax>0", "ay≥6", "qq∈ℕ");
		sequentHasGoal(sequent, environment, "ay≠0");
		
	}

	private ITypeEnvironment makeTypeEnvironment() {
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("ax", intType);
		environment.addName("ay", intType);
		environment.addName("az", intType);
		environment.addName("cx", intType);
		environment.addName("cy", intType);
		environment.addName("cz", intType);
		environment.addName("ax'", intType);
		environment.addName("ay'", intType);
		environment.addName("az'", intType);
		environment.addName("cx'", intType);
		environment.addName("cy'", intType);
		environment.addName("cz'", intType);
		environment.addName("pp", powIntType);
		environment.addName("qq", intType);
		return environment;
	}
	
	/**
	 * categorisation of witnesses: deterministic or nondeterministic
	 */
	public void test_02() throws Exception {
		IMachineFile abs = createMachine("abs");

		addVariables(abs, "ax", "ay", "az");
		addInvariants(abs, makeSList("I1", "I2", "I3"), makeSList("ax>0", "ay>0", "az>0"));
		addEvent(abs, "evt", 
				makeSList("pp"), 
				makeSList("G1"), makeSList("pp⊆ℕ"), 
				makeSList("A1", "A2"), makeSList("ax:∈pp", "ay,az :∣ ay'=az'"));
		
		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");

		addMachineRefines(mac, "abs");
		addVariables(mac, "cx", "cy", "cz");
		addInvariants(mac, makeSList("I1", "I2", "I3"), makeSList("cx>ax", "cy+ax>ay", "cz+ay+ax>az"));
		IEvent event = addEvent(mac, "evt", 
				makeSList("qq"), 
				makeSList("G1"), makeSList("qq∈ℕ"), 
				makeSList("A1", "A2"), makeSList("cx≔qq", "cy,cz :∣ {cy',cz'} ⊆ {qq}"));
		addEventRefines(event, "evt");
		addEventWitnesses(event, 
				makeSList("pp", "ax'", "ay'", "az'"), 
				makeSList("qq∈pp", "ax'=cx'", "ay'=cx'+ay'", "cz'=az'"));
		
		mac.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = makeTypeEnvironment();
		
		IPOFile po = mac.getPOFile();
		
		containsIdentifiers(po, "ax", "ay", "az", "cx", "cy", "cz");
		
		IPOSequent sequent;
		
		sequent = getSequent(po, "evt/A1/SIM");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "az'", "cx'", "cy'", "cz'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "qq∈pp");
		sequentHasGoal(sequent, environment, "qq∈pp");
		
		sequent = getSequent(po, "evt/A2/SIM");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "az'", "cx'", "cy'", "cz'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "qq∈pp", "ay'=cx'+ay'", "cz'=az'");
		sequentHasGoal(sequent, environment, "ay'=az'");

		sequent = getSequent(po, "evt/I1/INV");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "az'", "cx'", "cy'", "cz'", "pp", "qq");
		sequentHasHypotheses(sequent, environment);
		sequentHasGoal(sequent, environment, "qq>qq");

		sequent = getSequent(po, "evt/I2/INV");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "az'", "cx'", "cy'", "cz'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "{cy',cz'} ⊆ {qq}", "ay'=cx'+ay'");
		sequentHasGoal(sequent, environment, "cy'+qq>ay'");

		sequent = getSequent(po, "evt/I3/INV");
		sequentHasIdentifiers(sequent, "ax'", "ay'", "az'", "cx'", "cy'", "cz'", "pp", "qq");
		sequentHasHypotheses(sequent, environment, "{cy',cz'} ⊆ {qq}", "ay'=cx'+ay'", "cz'=az'");
		sequentHasGoal(sequent, environment, "cz'+ay'+qq>az'");
	}

}
