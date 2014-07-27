/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.VariableHasDisappearedError;
import static org.eventb.core.sc.ParseProblem.FreeIdentifierHasBoundOccurencesWarning;
import static org.eventb.core.tests.MarkerMatcher.marker;

import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * @author Laurent Voisin
 */
public class TestEventGuardsAndTheorems extends
		GenericPredicateTest<IEvent, ISCEvent> {

	@Override
	protected IGenericSCTest<IEvent, ISCEvent> newGeneric() {
		return new GenericEventSCTest(this);
	}

	/**
	 * Ensures that a regular guard cannot refer to a disappearing variable.
	 */
	@Test
	public void test_13() throws Exception {
		createAbstractMachine();
		final IEvent evt = createConcreteEvent(false);
		final IGuard grd = evt.getGuards()[0];
		runBuilderCheck(marker(grd, PREDICATE_ATTRIBUTE, 0, 2,
				VariableHasDisappearedError, "V1"));
	}

	/**
	 * Ensures that a theorem guard can refer to a disappearing variable.
	 */
	@Test
	public void test_14() throws Exception {
		createAbstractMachine();
		createConcreteEvent(true);
		runBuilderCheck();
	}

	/**
	 * Ensures that a legibility warning is raised for a guard quantifying on a
	 * variable with the same name as a parameter (bug #720).
	 */
	@Test
	public void test_15() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		final IEvent evt = addEvent(mac, "evt",
				makeSList("p"), //
				makeSList("G1", "G2"), makeSList("p∈ℕ", "∀p·p∈ℕ"),
				makeBList(false, false), makeSList(), makeSList());
		saveRodinFileOf(mac);

		runBuilderCheck(marker(evt.getGuards()[1], PREDICATE_ATTRIBUTE,
				FreeIdentifierHasBoundOccurencesWarning, "p"));
	}

	private IMachineRoot createAbstractMachine() throws RodinDBException {
		final IMachineRoot abs = createMachine("abs");
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(abs, "V1");
		saveRodinFileOf(abs);
		return abs;
	}

	private IEvent createConcreteEvent(final boolean theorem)
			throws RodinDBException {
		final IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, makeSList("V2"));
		addInvariants(mac, makeSList("I2"), makeSList("V1=V2"), false);
		final IEvent ini = addInitialisation(mac, "V2");
		addEventWitness(ini, "V1'", "⊤");
		final IEvent evt = addEvent(mac, "evt", makeSList(), //
				makeSList("grd"), makeSList("V1∈ℕ"), makeBList(theorem), //
				makeSList(), makeSList());
		saveRodinFileOf(mac);
		return evt;
	}

}
