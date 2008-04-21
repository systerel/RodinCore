/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;

/**
 * Unit tests for the Proof Manager.
 * 
 * @author Laurent Voisin
 */
public class ProofManagerTests extends AbstractProofTests {

	/**
	 * Ensures that one can get an instance of the Proof Manager.
	 */
	public void testPMExists() throws Exception {
		assertNotNull(pm);
	}

	/**
	 * Ensures that the Proof Manager is unique.
	 */
	public void testPMUnique() throws Exception {
		final IProofManager otherPM = EventBPlugin.getProofManager();
		assertEquals(pm, otherPM);
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * context.
	 */
	public void testContextProofComponent() throws Exception {
		final IContextFile ctx = (IContextFile) rodinProject
				.getRodinFile("c.buc");
		final IProofComponent pc = pm.getProofComponent(ctx);
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(ctx.getSCContextFile()));
		assertEquals(pc, pm.getProofComponent(ctx.getPOFile()));
		assertEquals(pc, pm.getProofComponent(ctx.getPRFile()));
		assertEquals(pc, pm.getProofComponent(ctx.getPSFile()));
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * machine.
	 */
	public void testMachineProofComponent() throws Exception {
		final IMachineFile mch = (IMachineFile) rodinProject
				.getRodinFile("m.bum");
		final IProofComponent pc = pm.getProofComponent(mch);
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(mch.getSCMachineFile()));
		assertEquals(pc, pm.getProofComponent(mch.getPOFile()));
		assertEquals(pc, pm.getProofComponent(mch.getPRFile()));
		assertEquals(pc, pm.getProofComponent(mch.getPSFile()));
	}

}
