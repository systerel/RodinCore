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
	
	public void testEvents_00() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
	}
	
	public void testEvents_01() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt1 = addEvent(mac, "evt1");
		addEventRefines(evt1, "evt");
		IEvent evt2 = addEvent(mac, "evt2");
		addEventRefines(evt2, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt1", "evt2");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "evt");
		
	}

	public void testEvents_02() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0], "L1");
		
	}
	
	public void testEvents_03() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1"), makeSList("G1"), makeSList("L1⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		containsVariables(events[0]);
		
	}
	
	public void testEvents_04() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1", "L3"), 
				makeSList("G1", "G3"), makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
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
	
	public void testEvents_05() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L2"), 
				makeSList("G1"), makeSList("L2∈ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
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
	
	public void testEvents_06() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addVariables(abs, "V1");
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"));
		addEvent(abs, "evt", makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A1"), makeSList("V1:∈ℕ"));

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V2");
		addInvariants(mac, makeSList("I2"), makeSList("V2⊆ℕ"));
		IEvent evt = addEvent(mac, "evt", makeSList(), 
				makeSList(), makeSList(), makeSList("A2"), makeSList("V2:∣V2'⊆ℕ"));
		addEventRefines(evt, "evt");
		addEventWitnesses(evt, makeSList("V1'"), makeSList("V1'∈V2'"));
		mac.save(null, true);
		
		runSC(mac);
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("V1", factory.makeIntegerType());
		typeEnvironment.addName("V1'", factory.makeIntegerType());
		typeEnvironment.addName("V2", factory.makePowerSetType(factory.makeIntegerType()));
		typeEnvironment.addName("V2'", factory.makePowerSetType(factory.makeIntegerType()));
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0]);
		containsWitnesses(events[0], typeEnvironment, makeSList("V1'"), makeSList("V1'∈V2'"));
		
	}
	
	public void testEvents_07() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList("L1", "L3"), 
				makeSList("G1", "G3"), makeSList("L1∈ℕ", "L3∈ℕ"), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt", makeSList("L1", "L2"), 
				makeSList("G1", "G2"), makeSList("L1=1", "L2⊆ℕ"), makeSList(), makeSList());
		addEventRefines(evt, "evt");
		mac.save(null, true);
		
		runSC(mac);
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("L1", factory.makeIntegerType());
		typeEnvironment.addName("L2", factory.makePowerSetType(factory.makeIntegerType()));
		typeEnvironment.addName("L3", factory.makeIntegerType());
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
		
		containsVariables(events[0], "L1", "L2");
		containsWitnesses(events[0], typeEnvironment, makeSList("L3"), makeSList("⊤"));
		
	}
	
	public void testEvents_08() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt", makeSList(), makeSList(), makeSList(), makeSList(), makeSList());

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		IEvent gvt = addEvent(mac, "gvt");
		addEventRefines(gvt, "evt");
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "evt");
		refinesEvents(events[2], "evt");
	}

	public void testEvents_09() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "fvt");
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt", "fvt");
	}
	
	public void testEvents_10() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		refinesEvents(events[0], "evt");
	}

	public void testEvents_11() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		refinesEvents(events[0], "evt");
		
	}

	public void testEvents_12() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "fvt");
		refinesEvents(events[0], "evt", "fvt");
		
	}	
	
	public void testEvents_13() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		
		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, "evt");
		addEventRefines(evt, "gvt");
		IEvent fvt = addEvent(mac, "fvt");
		addEventRefines(fvt, "evt");
		addEventRefines(fvt, "fvt");
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
	}	
	
	public void testEvents_14() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, "fvt");
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");
		
		abs.save(null, true);
		
		runSC(abs);

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
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "hvt", "ivt");
		refinesEvents(events[0], "evt");
		refinesEvents(events[1], "fvt", "gvt");
		refinesEvents(events[2], "hvt");
		refinesEvents(events[3]);
	}	
	
	public void testEvents_15() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		abs.save(null, true);
		
		runSC(abs);

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
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "ivt");
	}	
	
	public void testEvents_16() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addEvent(mac, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);
		
	}
	
	public void testEvents_17() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addEvent(mac, IEvent.INITIALISATION);
		addEventRefines(init, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file);
		
	}
	
	public void testEvents_18() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInheritedEvent(mac, IEvent.INITIALISATION);
		
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		refinesEvents(events[0], IEvent.INITIALISATION);
		
	}
	
	public void testEvents_19() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, IEvent.INITIALISATION);

		abs.save(null, true);
		
		runSC(abs);

		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent evt = addEvent(mac, "evt");
		addEventRefines(evt, IEvent.INITIALISATION);
		addEvent(mac, "fvt");
	
		mac.save(null, true);
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
	}
	
	public void testEvents_20() throws Exception {
		IMachineFile abs = createMachine("abs");
		
		addEvent(abs, "evt");
		addEvent(abs, IEvent.INITIALISATION);
		addEvent(abs, "gvt");
		addEvent(abs, "hvt");

		abs.save(null, true);
		
		runSC(abs);

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
		
		runSC(mac);
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		getSCEvents(file, "fvt");
		
	}

}
