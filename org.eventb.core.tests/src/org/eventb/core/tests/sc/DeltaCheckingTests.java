/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;

/**
 * Tests of the delta-checking facility of the Static Checker.
 * 
 * @author Laurent Voisin
 */
public class DeltaCheckingTests extends BasicSCTestWithFwdConfig {

	/**
	 * Ensures that the statically-checked file of a context is modified only
	 * when needed.
	 */
	public void testDeltaContext() throws Exception {
		final IContextRoot root = createContext("con");
		final ISCContextRoot sc = root.getSCContextRoot();
		final IPORoot po = root.getPORoot();
		
		addCarrierSets(root, makeSList("S1"));
		saveRodinFileOf(root);

		runBuilder();

		root.getCarrierSets()[0].setComment("foo", null);
		saveRodinFileOf(root);

		runBuilderNotChanged(sc, po);
	}

	/**
	 * Ensures that the statically-checked file of a context is modified only
	 * when needed, when another context (for instance an ancestor) has changed.
	 */
	public void testDeltaContextIndirect() throws Exception {
		final IContextRoot rootAbs = createContext("abs");
		final ISCContextRoot scAbs = rootAbs
				.getSCContextRoot();
		final IPORoot poAbs = rootAbs.getPORoot();
		addCarrierSets(rootAbs, makeSList("S1"));
		saveRodinFileOf(rootAbs);

		final IContextRoot rootCon = createContext("con");
		final ISCContextRoot scCon = rootCon.getSCContextRoot();
		final IPORoot poCon = rootCon.getPORoot();
		addContextExtends(rootCon, "abs");
		addCarrierSets(rootCon, makeSList("S11"));
		saveRodinFileOf(rootCon);

		runBuilder();

		rootAbs.getCarrierSets()[0].setComment("foo", null);
		saveRodinFileOf(rootAbs);

		runBuilderNotChanged(scAbs, poAbs, scCon, poCon);
	}

	/**
	 * Ensures that the statically-checked file of a machine is modified only
	 * when needed.
	 */
	public void testDeltaMachine() throws Exception {
		final IMachineRoot mac = createMachine("con");
		final ISCMachineRoot sc = mac.getSCMachineRoot();
		final IPORoot po = mac.getPORoot();

		addVariables(mac, makeSList("V1"));
		addInvariants(mac, makeSList("I1"), makeSList("V1∈BOOL"), true);
		saveRodinFileOf(mac);

		runBuilder();

		mac.getVariables()[0].setComment("foo", null);
		saveRodinFileOf(mac);

		runBuilderNotChanged(sc, po);
	}

	/**
	 * Ensures that the statically-checked file of a machine is modified only
	 * when needed, when another machine (for instance an ancestor) has changed.
	 */
	public void testDeltaMachineIndirect() throws Exception {
		final IMachineRoot abs = createMachine("abs");
		final ISCMachineRoot scAbs = abs.getSCMachineRoot();
		final IPORoot poAbs = abs.getPORoot();
		addVariables(abs, makeSList("V1"));
		addInvariants(abs, makeSList("I1"), makeSList("V1∈BOOL"), true);
		saveRodinFileOf(abs);

		final IMachineRoot con = createMachine("con");
		final ISCMachineRoot scCon = con.getSCMachineRoot();
		final IPORoot poCon = con.getPORoot();
		addMachineRefines(con, "abs");
		addVariables(con, makeSList("V1"));
		saveRodinFileOf(con);

		runBuilder();

		abs.getVariables()[0].setComment("foo", null);
		saveRodinFileOf(abs);

		runBuilderNotChanged(scAbs, poAbs, scCon, poCon);
	}

}
