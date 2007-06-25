/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * Tests of the delta-checking facility of the Static Checker.
 * 
 * @author Laurent Voisin
 */
public class DeltaCheckingTests extends BasicSCTest {

	public class DeltaListener implements IElementChangedListener {

		final ArrayList<IRodinElementDelta> deltas;

		public DeltaListener() {
			deltas = new ArrayList<IRodinElementDelta>();
		}

		public void elementChanged(ElementChangedEvent event) {
			deltas.add(event.getDelta());
		}

		public void assertNotChanged(IRodinFile... rfs) {
			for (IRodinFile rf : rfs) {
				for (IRodinElementDelta delta : deltas) {
					assertNotChanged(delta, rf);
				}
			}
		}

		public void assertNotChanged(IRodinElementDelta delta, IRodinFile rf) {
			final IRodinElement elem = delta.getElement();
			if (elem.equals(rf)) {
				fail("File " + rf + " should not have changed.");
			}
			if (elem.isAncestorOf(rf)) {
				for (IRodinElementDelta child : delta.getAffectedChildren()) {
					assertNotChanged(child, rf);
				}
			}

		}
	}

	protected void runBuilderNotChanged(IRodinFile... rfs) throws CoreException {
		final DeltaListener listener = new DeltaListener();
		RodinCore.addElementChangedListener(listener);
		super.runBuilder();
		RodinCore.removeElementChangedListener(listener);
		listener.assertNotChanged(rfs);
	}

	/**
	 * Ensures that the statically-checked file of a context is modifed only
	 * when needed.
	 */
	public void testDeltaContext() throws Exception {
		final IContextFile con = createContext("con");
		final ISCContextFile sc = con.getSCContextFile();
		final IPOFile po = con.getPOFile();
		
		addCarrierSets(con, makeSList("S1"));
		con.save(null, true);

		runBuilder();

		con.getCarrierSets()[0].setComment("foo", null);
		con.save(null, true);

		runBuilderNotChanged(sc, po);
	}

	/**
	 * Ensures that the statically-checked file of a context is modifed only
	 * when needed, when another context (for instance an ancestor) has changed.
	 */
	public void testDeltaContextIndirect() throws Exception {
		final IContextFile abs = createContext("abs");
		final ISCContextFile scAbs = abs.getSCContextFile();
		final IPOFile poAbs = abs.getPOFile();
		addCarrierSets(abs, makeSList("S1"));
		abs.save(null, true);

		final IContextFile con = createContext("con");
		final ISCContextFile scCon = con.getSCContextFile();
		final IPOFile poCon = con.getPOFile();
		addContextExtends(con, "abs");
		addCarrierSets(con, makeSList("S11"));
		con.save(null, true);

		runBuilder();

		abs.getCarrierSets()[0].setComment("foo", null);
		abs.save(null, true);

		runBuilderNotChanged(scAbs, poAbs, scCon, poCon);
	}

	/**
	 * Ensures that the statically-checked file of a machine is modifed only
	 * when needed.
	 */
	public void testDeltaMachine() throws Exception {
		final IMachineFile mac = createMachine("con");
		final ISCMachineFile sc = mac.getSCMachineFile();
		final IPOFile po = mac.getPOFile();

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈BOOL"));
		mac.save(null, true);

		runBuilder();

		mac.getVariables()[0].setComment("foo", null);
		mac.save(null, true);

		runBuilderNotChanged(sc, po);
	}

	/**
	 * Ensures that the statically-checked file of a machine is modifed only
	 * when needed, when another machine (for instance an ancestor) has changed.
	 */
	public void testDeltaMachineIndirect() throws Exception {
		final IMachineFile abs = createMachine("abs");
		final ISCMachineFile scAbs = abs.getSCMachineFile();
		final IPOFile poAbs = abs.getPOFile();
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈BOOL"));
		abs.save(null, true);

		final IMachineFile con = createMachine("con");
		final ISCMachineFile scCon = con.getSCMachineFile();
		final IPOFile poCon = con.getPOFile();
		addMachineRefines(con, "abs");
		addVariables(con, makeSList("V1"));
		con.save(null, true);

		runBuilder();

		abs.getVariables()[0].setComment("foo", null);
		abs.save(null, true);

		runBuilderNotChanged(scAbs, poAbs, scCon, poCon);
	}

}
