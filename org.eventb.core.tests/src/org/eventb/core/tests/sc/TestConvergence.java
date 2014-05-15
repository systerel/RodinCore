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
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.CONVERGENT;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.eventb.core.IEvent.INITIALISATION;
import static org.eventb.core.sc.GraphProblem.ConvergentEventNoVariantWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceAnticipatedWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceUnchangedWarning;
import static org.eventb.core.sc.GraphProblem.InitialisationNotOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.NoConvergentEventButVariantWarning;
import static org.eventb.core.sc.GraphProblem.OrdinaryFaultyConvergenceWarning;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.tests.MarkerMatcher;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class TestConvergence extends BasicSCTestWithFwdConfig {

	/**
	 * All kinds of convergence can be refined by the same kind.
	 */
	@Test
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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, IEvent.INITIALISATION, "evt",
				"fvt", "gvt");
		isOrdinary(events[1]);
		isAnticipated(events[2]);
		isConvergent(events[3]);
	}

	/**
	 * If there is no variant convergent events are set to ordinary. Unless: see
	 * testCvg_09
	 */
	@Test
	public void testCvg_01_NoVariantConvergentSetToOrdinary() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);

		IEvent evt = addEvent(mac, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(mac, "gvt");
		setConvergent(gvt);
		saveRodinFileOf(mac);

		runBuilderCheck(marker(gvt, CONVERGENCE_ATTRIBUTE,
				ConvergentEventNoVariantWarning, "gvt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"gvt");
		isOrdinary(events[1]);
		isAnticipated(events[2]);
		isOrdinary(events[3]);
	}

	/**
	 * anticipated events and convergent events can be refined by ordinary
	 * events.
	 */
	@Test
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

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"gvt");
		isOrdinary(events[1]);
		isOrdinary(events[2]);
		isOrdinary(events[3]);
	}

	/**
	 * ordinary events cannot be refined by anticipated events: the concrete is
	 * set to ordinary.
	 */
	@Test
	public void testCvg_03_AllRefinedByAnticipated() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		addMachineRefines(mac, "abs");
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

		runBuilderCheck(marker(mevt, CONVERGENCE_ATTRIBUTE,
				OrdinaryFaultyConvergenceWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"gvt");
		isOrdinary(events[1]);
		isAnticipated(events[2]);
		isAnticipated(events[3]);
	}

	/**
	 * ordinary events cannot be refined by convergent events. In that case the
	 * concrete event is set to ordinary.
	 */
	@Test
	public void testCvg_04_AllRefinedByConvergent() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
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

		runBuilderCheck(marker(mevt, CONVERGENCE_ATTRIBUTE,
				OrdinaryFaultyConvergenceWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt",
				"gvt");
		isOrdinary(events[1]);
		isConvergent(events[2]);
		isConvergent(events[3]);
	}

	/**
	 * If in a merge the abstract event have different convergences, a warning
	 * is issued and the refining event set to ordinary.
	 */
	@Test
	public void testCvg_05_mergeFaultySetToOrdinary() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setOrdinary(evt);
		IEvent fvt = addEvent(abs, "fvt");
		setAnticipated(fvt);
		IEvent gvt = addEvent(abs, "gvt");
		setConvergent(gvt);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		addMachineRefines(mac, "abs");
		addVariant(mac, "1");
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		addEventRefines(mevt, "fvt");
		addEventRefines(mevt, "gvt");
		setConvergent(mevt);

		saveRodinFileOf(mac);

		runBuilderCheck(
				marker(mevt.getRefinesClauses()[0], TARGET_ATTRIBUTE,
						FaultyAbstractConvergenceUnchangedWarning, "evt"),
				marker(mevt.getRefinesClauses()[1], TARGET_ATTRIBUTE,
						FaultyAbstractConvergenceOrdinaryWarning, "fvt"),
				marker(mevt.getRefinesClauses()[2], TARGET_ATTRIBUTE,
						FaultyAbstractConvergenceOrdinaryWarning, "gvt"),
				marker(mevt, CONVERGENCE_ATTRIBUTE,
						OrdinaryFaultyConvergenceWarning, "evt"),
				marker(mac.getVariants()[0], EXPRESSION_ATTRIBUTE,
						NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		isOrdinary(events[1]);
	}

	/**
	 * The initialisation should be marked ordinary.
	 */
	@Test
	public void testCvg_06_InitialisationIsOrdinary() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setOrdinary(init);

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		isOrdinary(events[0]);
	}

	/**
	 * The initialisation must not be marked anticipated.
	 */
	@Test
	public void testCvg_07_InitialisationIsNotAnticipated() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setAnticipated(init);

		saveRodinFileOf(mac);

		runBuilderCheck(marker(init, CONVERGENCE_ATTRIBUTE,
				InitialisationNotOrdinaryWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		isOrdinary(events[0]);
	}

	/**
	 * The initialisation must not be marked convergent.
	 */
	@Test
	public void testCvg_08_InitialisationIsNotConvergent() throws Exception {

		IMachineRoot mac = createMachine("mac");
		IEvent init = addInitialisation(mac);
		setConvergent(init);

		saveRodinFileOf(mac);

		runBuilderCheck(marker(init, CONVERGENCE_ATTRIBUTE,
				InitialisationNotOrdinaryWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION);
		isOrdinary(events[0]);
	}

	/**
	 * If a convergent event is refined by a convergent there is no need for a
	 * variant in the refined machine what concerns the convergent event.
	 */
	@Test
	public void testCvg_09_refinedByConvergentNoVariantNeeded()
			throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariant(abs, "1");
		addInitialisation(abs);
		IEvent evt = addEvent(abs, "evt");
		setConvergent(evt);

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addInitialisation(mac);
		IEvent mevt = addEvent(mac, "evt");
		addEventRefines(mevt, "evt");
		setConvergent(mevt);

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
		isConvergent(events[1]);
	}

	/**
	 * If a convergent event is refined by a convergent there is no need for a
	 * variant in the refined machine what concerns the convergent event.
	 */
	@Test
	public void testCvg_10_convergentEventNoVariant() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInitialisation(abs);
		addVariant(abs, "1");
		IEvent evt = addEvent(abs, "evt");
		setConvergent(evt);

		saveRodinFileOf(abs);

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

		runBuilderCheck(marker(mfvt, CONVERGENCE_ATTRIBUTE,
				ConvergentEventNoVariantWarning, "fvt"));

		ISCMachineRoot file = mac.getSCMachineRoot();

		ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt", "fvt");
		isConvergent(events[1]);
		isOrdinary(events[2]);
	}

	private class MergeTestCase {
		final Convergence absCvg1;
		final Convergence absCvg2;
		final Convergence conCvg;
		final GraphProblem[] expectedProblems;

		public MergeTestCase(Convergence absCvg1, Convergence absCvg2,
				Convergence conCvg, GraphProblem... expectedProblems) {
			this.absCvg1 = absCvg1;
			this.absCvg2 = absCvg2;
			this.conCvg = conCvg;
			this.expectedProblems = expectedProblems;
		}

		public void run() throws CoreException {
			IMachineRoot abs = createMachine("abs");
			addInitialisation(abs);
			if (absCvg1 == CONVERGENT || absCvg2 == CONVERGENT)
				addVariant(abs, "1");
			IEvent evt = addEvent(abs, "evt");
			evt.setConvergence(absCvg1, null);
			IEvent fvt = addEvent(abs, "fvt");
			fvt.setConvergence(absCvg2, null);

			saveRodinFileOf(abs);

			IMachineRoot mac = createMachine("mac");
			addMachineRefines(mac, "abs");
			addInitialisation(mac);
			IEvent mevt = addEvent(mac, "evt");
			addEventRefines(mevt, "evt", "fvt");
			mevt.setConvergence(conCvg, null);

			saveRodinFileOf(mac);

			IRefinesEvent[] refinesClauses = mevt.getRefinesClauses();
			runBuilderCheck(makeExpectedMarkers(refinesClauses));

			ISCMachineRoot file = mac.getSCMachineRoot();

			ISCEvent[] events = getSCEvents(file, INITIALISATION, "evt");
			assertEquals(conCvg, events[1].getConvergence());
		}

		private MarkerMatcher[] makeExpectedMarkers(
				IRefinesEvent[] refinesClauses) throws RodinDBException {
			final int length = expectedProblems.length;
			final MarkerMatcher[] result = new MarkerMatcher[length];
			for (int i = 0; i < length; i++) {
				result[i] = marker(refinesClauses[i], TARGET_ATTRIBUTE,
						expectedProblems[i],
						refinesClauses[i].getAbstractEventLabel());
			}
			return result;
		}

	}

	public MergeTestCase test(Convergence absCvg1, Convergence absCvg2,
			Convergence conCvg, GraphProblem... expectedProblems) {
		return new MergeTestCase(absCvg1, absCvg2, conCvg, expectedProblems);
	}

	/**
	 * If a convergent event is refined by a convergent event there is no need
	 * for a variant in the refined machine that concerns the convergent event.
	 */
	@Test
	public void testCvg_11_convergentMerge() throws Exception {

		final MergeTestCase[] tests = {
				test(ORDINARY, ORDINARY, ORDINARY),
				test(ORDINARY, ANTICIPATED, ORDINARY,
						FaultyAbstractConvergenceUnchangedWarning,
						FaultyAbstractConvergenceOrdinaryWarning),
				test(ORDINARY, CONVERGENT, ORDINARY,
						FaultyAbstractConvergenceUnchangedWarning,
						FaultyAbstractConvergenceOrdinaryWarning),
				test(ANTICIPATED, ORDINARY, ORDINARY,
						FaultyAbstractConvergenceOrdinaryWarning,
						FaultyAbstractConvergenceUnchangedWarning),
				test(ANTICIPATED, ANTICIPATED, ANTICIPATED),
				test(ANTICIPATED, CONVERGENT, ANTICIPATED,
						FaultyAbstractConvergenceUnchangedWarning,
						FaultyAbstractConvergenceAnticipatedWarning),
				test(CONVERGENT, ORDINARY, ORDINARY,
						FaultyAbstractConvergenceOrdinaryWarning,
						FaultyAbstractConvergenceUnchangedWarning),
				test(CONVERGENT, ANTICIPATED, ANTICIPATED,
						FaultyAbstractConvergenceAnticipatedWarning,
						FaultyAbstractConvergenceUnchangedWarning),
				test(CONVERGENT, CONVERGENT, CONVERGENT), };

		for (final MergeTestCase test : tests) {
			test.run();
		}
	}

}
