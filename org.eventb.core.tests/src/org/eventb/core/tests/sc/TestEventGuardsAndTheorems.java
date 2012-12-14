/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
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
		final IMachineRoot abs = createAbstractMachine();
		final IEvent evt = createConcreteEvent(false);
		runBuilder();

		hasNotMarker(abs);
		final IGuard grd = evt.getGuards()[0];
		hasMarker(grd, PREDICATE_ATTRIBUTE, VariableHasDisappearedError, "V1");
	}

	/**
	 * Ensures that a theorem guard can refer to a disappearing variable.
	 */
	@Test
	public void test_14() throws Exception {
		final IMachineRoot abs = createAbstractMachine();
		final IEvent evt = createConcreteEvent(true);
		runBuilder();

		containsMarkers(abs, false);
		containsMarkers(evt.getRoot(), false);
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
