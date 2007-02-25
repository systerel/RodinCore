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

/**
 * @author Stefan Hallerstede
 *
 */
public class TestConvergence extends BasicSCTest {
	
	public void testCvg_00_AllThreeKindsOK() throws Exception {
		IMachineFile mac = createMachine("mac");

		addInitialisation(mac);
		addVariant(mac, "1");
		IEvent evt = addEvent(mac, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(mac, "gvt");
		setConvergent(gvt);
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt", "fvt", "gvt");
		isOrdinary(events[1]);
		isAnticipated(events[2]);
		isConvergent(events[3]);
		
		containsMarkers(mac, false);
	}
	
	public void testCvg_01_FaultySetToDefaultOrdinary() throws Exception {
		IMachineFile mac = createMachine("mac");

		IEvent evt = addEvent(mac, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(mac, "gvt");
		setConvergent(gvt);
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isOrdinary(events[1]);
		isOrdinary(events[2]);
	}
	
	public void testCvg_02_AllRefinedByOrdinary() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setOrdinary(mevt);
		IEvent mfvt = addEvent(mac, "fvt");
		addEventRefines(mfvt, "fvt");
		setOrdinary(mfvt);
		IEvent mgvt = addEvent(mac, "gvt");
		addEventRefines(mgvt, "gvt");
		setOrdinary(mgvt);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isAnticipated(events[1]);
		isConvergent(events[2]);
		
	}
	
	public void testCvg_03_AllRefinedByAnticipated() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setAnticipated(mevt);
		IEvent mfvt = addEvent(mac, "fvt");
		addEventRefines(mfvt, "fvt");
		setAnticipated(mfvt);
		IEvent mgvt = addEvent(mac, "gvt");
		addEventRefines(mgvt, "gvt");
		setAnticipated(mgvt);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isAnticipated(events[1]);
		isConvergent(events[2]);
		
	}
	
	public void testCvg_04_AllRefinedByConvergent() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setConvergent(mevt);
		IEvent mfvt = addEvent(mac, "fvt");
		addEventRefines(mfvt, "fvt");
		setConvergent(mfvt);
		IEvent mgvt = addEvent(mac, "gvt");
		addEventRefines(mgvt, "gvt");
		setConvergent(mgvt);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isConvergent(events[1]);
		isConvergent(events[2]);
		
	}
	
	public void testCvg_05_mergeFaultySetToOrdinary() throws Exception {
		IMachineFile abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		addEventRefines(mevt, "fvt");
		addEventRefines(mevt, "gvt");
		setConvergent(mevt);
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, "evt");
		isOrdinary(events[0]);
	}
	
	public void testCvg_06_InitialisationIsOrdinary() throws Exception {
		
		IMachineFile abs = createMachine("abs");
		addInitialisation(abs);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		setOrdinary(init);
		addVariant(mac, "1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);
		
		containsMarkers(mac, false);
	}
	
	public void testCvg_07_InitialisationIsNotAnticipated() throws Exception {
		
		IMachineFile abs = createMachine("abs");
		addInitialisation(abs);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		setAnticipated(init);
		addVariant(mac, "1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);
	}
	
	public void testCvg_08_InitialisationIsNotConvergent() throws Exception {
		
		IMachineFile abs = createMachine("abs");
		addInitialisation(abs);

		abs.save(null, true);
		
		runBuilder();
		
		IMachineFile mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		IEvent init = addInitialisation(mac);
		setConvergent(init);
		addVariant(mac, "1");
		
		mac.save(null, true);
		
		runBuilder();
		
		ISCMachineFile file = mac.getSCMachineFile();
		
		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);
	}

}
