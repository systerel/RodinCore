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
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.sc.GraphProblem;

/**
 * @author Stefan Hallerstede
 * 
 */
public class TestConvergence extends BasicSCTestWithFwdConfig {

	/**
	 * All kinds of convergence can be refined by the same kind.
	 */
	public void testCvg_00_AllThreeKindsOK() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		addVariant(mac, "1");
		IEvent evt = addEvent(mac, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(mac, "gvt");
		setConvergent(gvt);
		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt", "gvt");
		isOrdinary(events[1]);
		isAnticipated(events[2]);
		isConvergent(events[3]);

		containsMarkers(mac, false);
	}

	/**
	 * If there is no variant convergent events are set to ordinary. Unless: see
	 * testCvg_09
	 */
	public void testCvg_01_NoVariantConvergentSetToOrdinary() throws Exception {
		IMachineRoot mac = createMachine("mac");

		IEvent evt = addEvent(mac, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(mac, "gvt");
		setConvergent(gvt);
		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isAnticipated(events[1]);
		isOrdinary(events[2]);

		hasMarker(gvt);
	}

	/**
	 * anticipated events and convergent events can be refined by ordinary
	 * events.
	 */
	public void testCvg_02_AllRefinedByOrdinary() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		addInitialisation(abs);
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setOrdinary(mevt);
		IEvent mfvt = addEvent(mac, "fvt");
		addEventRefines(mfvt, "fvt");
		setOrdinary(mfvt);
		IEvent mgvt = addEvent(mac, "gvt");
		addEventRefines(mgvt, "gvt");
		setOrdinary(mgvt);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt", "gvt");
		isOrdinary(events[1]);
		isOrdinary(events[2]);
		isOrdinary(events[3]);

		containsMarkers(mac, false);
	}

	/**
	 * ordinary events cannot be refined by anticipated events: the concrete is
	 * set to ordinary.
	 */
	public void testCvg_03_AllRefinedByAnticipated() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
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

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isAnticipated(events[1]);
		isAnticipated(events[2]);

		hasMarker(mevt);
	}

	/**
	 * ordinary events cannot be refined by convergent events. In that case the
	 * concrete event is set to ordinary.
	 */
	public void testCvg_04_AllRefinedByConvergent() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
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

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt", "fvt", "gvt");
		isOrdinary(events[0]);
		isConvergent(events[1]);
		isConvergent(events[2]);

		hasMarker(mevt);
	}

	/**
	 * If in a merge the abstract event have different convergences, a warning
	 * is issued and the refining event set to ordinary.
	 */
	public void testCvg_05_mergeFaultySetToOrdinary() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		addEventRefines(mevt, "fvt");
		addEventRefines(mevt, "gvt");
		setConvergent(mevt);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, "evt");
		isOrdinary(events[0]);

		hasMarker(mevt);
	}

	/**
	 * The initialisation should be marked ordinary.
	 */
	public void testCvg_06_InitialisationIsOrdinary() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setOrdinary(init);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);

		containsMarkers(mac, false);
	}

	/**
	 * The initialisation must not be marked anticipated.
	 */
	public void testCvg_07_InitialisationIsNotAnticipated() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setAnticipated(init);
		addVariant(mac, "1");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);

		hasMarker(init);
	}

	/**
	 * The initialisation must not be marked convergent.
	 */
	public void testCvg_08_InitialisationIsNotConvergent() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setConvergent(init);
		addVariant(mac, "1");

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION);
		isOrdinary(events[0]);

		hasMarker(init);
	}

	/**
	 * If a convergent event is refined by a convergent there is no need for a
	 * variant in the refined machine what concerns the convergent event.
	 */
	public void testCvg_09_refinedByConvergentNoVariantNeeded()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		addInitialisation(abs);
		IEvent evt = addEvent(abs, "evt");
		setConvergent(evt);

		saveRodinFileOf(abs);

		runBuilder();

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setConvergent(mevt);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
		isConvergent(events[1]);

		containsMarkers(mac, false);
	}

	/**
	 * If a convergent event is refined by a convergent there is no need for a
	 * variant in the refined machine what concerns the convergent event.
	 */
	public void testCvg_10_convergentEventNoVariant() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setConvergent(evt);

		saveRodinFileOf(abs);

		runBuilder();

		containsMarkers(abs, false);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setConvergent(mevt);
		IEvent mfvt = addEvent(mac, "fvt");
		setConvergent(mfvt);

		saveRodinFileOf(mac);

		runBuilder();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt");
		isConvergent(events[1]);
		isOrdinary(events[2]);
		hasMarker(mfvt, EventBAttributes.CONVERGENCE_ATTRIBUTE);
	}

	private static final IConvergenceElement.Convergence[] cvg(
			IConvergenceElement.Convergence... cvgs) {
		return cvgs;
	}

	/**
	 * If a convergent event is refined by a convergent there is no need for a
	 * variant in the refined machine what concerns the convergent event.
	 */
	public void testCvg_11_convergentMerge() throws Exception {

		IConvergenceElement.Convergence[][] cvgMatrix = new IConvergenceElement.Convergence[][] {
				cvg(Convergence.ORDINARY, Convergence.ORDINARY,
						Convergence.ORDINARY),
				cvg(Convergence.ORDINARY, Convergence.ANTICIPATED,
						Convergence.ORDINARY),
				cvg(Convergence.ORDINARY, Convergence.CONVERGENT,
						Convergence.ORDINARY),
				cvg(Convergence.ANTICIPATED, Convergence.ORDINARY,
						Convergence.ORDINARY),
				cvg(Convergence.ANTICIPATED, Convergence.ANTICIPATED,
						Convergence.ANTICIPATED),
				cvg(Convergence.ANTICIPATED, Convergence.CONVERGENT,
						Convergence.ANTICIPATED),
				cvg(Convergence.CONVERGENT, Convergence.ORDINARY,
						Convergence.ORDINARY),
				cvg(Convergence.CONVERGENT, Convergence.ANTICIPATED,
						Convergence.ANTICIPATED),
				cvg(Convergence.CONVERGENT, Convergence.CONVERGENT,
						Convergence.CONVERGENT) };

		for (IConvergenceElement.Convergence[] cvgs : cvgMatrix) {
			IMachineRoot abs = createMachine("abs");
			addInitialisation(abs);
			if (cvgs[0] == Convergence.CONVERGENT
					|| cvgs[1] == Convergence.CONVERGENT)
				addVariant(abs, "1");
			IEvent evt = addEvent(abs, "evt");
			evt.setConvergence(cvgs[0], null);
			IEvent fvt = addEvent(abs, "fvt");
			fvt.setConvergence(cvgs[1], null);

			saveRodinFileOf(abs);

			runBuilder();

			containsMarkers(abs, false);

			IMachineRoot mac = createMachine("mac");
			addMachineRefines(mac, "abs");
			addInitialisation(mac);
			IEvent mevt = addEvent(mac, "evt");
			addEventRefines(mevt, "evt", "fvt");
			mevt.setConvergence(cvgs[2], null);

			saveRodinFileOf(mac);

			runBuilder();

			ISCMachineRoot file = mac.getSCMachineRoot();

			ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt");
			if (cvgs[2] == Convergence.CONVERGENT) {
				isConvergent(events[1]);
			} else if (cvgs[2] == Convergence.ANTICIPATED) {
				isAnticipated(events[1]);
			} else if (cvgs[2] == Convergence.ORDINARY) {
				isOrdinary(events[1]);
			}
			if (cvgs[0] == cvgs[2] && cvgs[1] == cvgs[2]) {
				containsMarkers(mac, false);			
			} else {
				IRefinesEvent[] refinesClauses = mevt.getRefinesClauses();
				hasMarker(refinesClauses[0], EventBAttributes.TARGET_ATTRIBUTE);
				hasMarker(refinesClauses[1], EventBAttributes.TARGET_ATTRIBUTE);
				hasNotMarker(mevt,
						GraphProblem.ConvergentFaultyConvergenceWarning);
				hasNotMarker(mevt,
						GraphProblem.AnticipatedFaultyConvergenceWarning);
				hasNotMarker(mevt,
						GraphProblem.OrdinaryFaultyConvergenceWarning);
			}
		}
	}
}
