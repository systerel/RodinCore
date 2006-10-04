/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestEvents extends BasicTest {
	
	public void testEvents_0() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt");
		
	}

	public void testEvents_1() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt1", "evt2");
		
	}
	
	public void testEvents_2() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file);
		
	}
	
	public void testEvents_3() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt1", makeSList(), makeSList("G1"), makeSList("1∈ℕ"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
		
	}

	public void testEvents_4() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt1", makeSList(), makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], emptyEnv, makeSList("G1", "G2"), makeSList("1∈ℕ", "2∈ℕ"));
		
	}

	public void testEvents_5() throws Exception {
		IMachineFile mac = createMachine("mac");

		addEvent(mac, "evt1", makeSList(), makeSList("G1", "G1"), makeSList("1∈ℕ", "2∈ℕ"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], emptyEnv, makeSList("G1"), makeSList("1∈ℕ"));
		
	}
	
	public void testEvents_6() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0], "L1");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		
	}

	public void testEvents_7() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1⊆ℕ"));
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈V1"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0], "L1");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L1∈V1"));
		
	}
	
	public void testEvents_8() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"));
		addEvent(mac, "evt1", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0]);
		containsGuards(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
	}
	
	public void testEvents_9() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1⊆ℕ"));
		addEvent(mac, "evt1", makeSList("L2"), makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEvent(mac, "evt2", makeSList("L2"), makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1", "evt2");
		
		containsVariables(scEvents[0], "L2");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L2∈ℕ"));
		
		containsVariables(scEvents[1], "L2");
		containsGuards(scEvents[1], typeEnvironment, makeSList("G1"), makeSList("L2⊆ℕ"));	
		
	}
	
	public void testEvents_10() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList("A1"), makeSList("L1≔1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
		
	}
	
	public void testEvents_11() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), 
				makeSList("A1","A1"), makeSList("L1≔1", "L1≔2"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("L1≔1"));
		
	}
	
	public void testEvents_12() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());

		addVariables(mac, "L1");
		addInvariants(mac, makeSList("I1"), makeSList("L1∈ℕ"));
		addEvent(mac, "evt1", makeSList(), makeSList("A1"), makeSList("1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "L1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsGuards(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("1∈ℕ"));
		containsActions(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
	}
	
	public void testEvents_13() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("V1≔L1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0], "L1");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("V1≔L1"));
		
	}
	
	public void testEvents_14() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(mac, "evt1", makeSList("L1"), 
				makeSList("G1"), makeSList("L1∈ℕ"), 
				makeSList("A1"), makeSList("L1≔V1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0], "L1");
		containsGuards(scEvents[0], typeEnvironment, makeSList("G1"), makeSList("L1∈ℕ"));
		containsActions(scEvents[0], typeEnvironment, makeSList(), makeSList());
		
	}
	
	public void testEvents_15() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V1'", factory.makeIntegerType());

		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(mac, "evt1", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsEvents(file, "evt1");
		
		containsVariables(file, "V1");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt1");
		
		containsVariables(scEvents[0]);
		containsGuards(scEvents[0], typeEnvironment, makeSList(), makeSList());
		containsActions(scEvents[0], typeEnvironment, makeSList("A1"), makeSList("V1:∣V1'∈ℕ"));
		
	}
	
	public void testEvents_16() throws Exception {
		IMachineFile mac = createMachine("mac");

		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("u", factory.makeIntegerType());
		typeEnvironment.addName("x", factory.makeIntegerType());

		addVariables(mac, "u", "v", "w", "x", "y", "z");
		addInvariants(mac, makeSList("I1"), makeSList("u∈ℕ ∧ v∈ℕ ∧ w∈ℕ ∧ x∈ℕ ∧ y∈ℕ ∧ z∈ℕ"));
		addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1", "A2", "A3", "A4", "A5"), 
				makeSList("u≔1", "v,w:∣⊤", "v≔1", "x≔u", "y,z,w≔v,w,1"));
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		containsVariables(file, "u", "v", "w", "x", "y", "z");
		
		ISCEvent[] scEvents = getSCEvents(file, "evt");
		
		containsActions(scEvents[0], typeEnvironment, makeSList("A1", "A4"), makeSList("u≔1", "x≔u"));
		
	}
	
}
