/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;

/**
 * Tests of the delta-checking facility of the Static Checker.
 * 
 * @author Laurent Voisin
 */
public class DeltaCheckingTests extends BasicSCTest {

	/**
	 * Ensures that the statically-checked file of a context is modified only
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
	 * Ensures that the statically-checked file of a context is modified only
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
	 * Ensures that the statically-checked file of a machine is modified only
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
	 * Ensures that the statically-checked file of a machine is modified only
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
