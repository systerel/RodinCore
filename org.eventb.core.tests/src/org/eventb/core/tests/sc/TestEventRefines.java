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
public class TestEventRefines extends BasicTest {
	
	public void testEvents_0() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEventRefines(evt, "evt", "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
	}
	
	public void testEvents_1() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt1 = addEvent(mac, "evt1", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEventRefines(evt1, "evt", "evt");
		IEvent evt2 = addEvent(mac, "evt2", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
		addEventRefines(evt2, "evt", "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt1", "evt2");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "evt");
		
	}

	public void testEvents_2() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt", "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0], "L1");
		
	}
	
	public void testEvents_3() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt", "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0]);
		
	}
	
	public void testEvents_4() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1", "L3"), 
				makeSList("G1", "G3"), makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt", "evt");
		addEventWitnesses(evt, makeSList("L1"), makeSList("L1∈L2"));
		mac.save(null, true);
		
		runSC(mac);
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());
		typeEnvironment.addName("L2", factory.makePowerSetType(factory.makeIntegerType()));
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L1","L3"), makeSList("L1∈L2", "⊤"));
		
	}
	
	public void testEvents_5() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs", "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt", "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'=L2"));
		mac.save(null, true);
		
		runSC(mac);
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V1'", factory.makeIntegerType());
		typeEnvironment.addName("L2", factory.makeIntegerType());
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("V1'"), makeSList("V1'=L2"));
		
	}
	
}
