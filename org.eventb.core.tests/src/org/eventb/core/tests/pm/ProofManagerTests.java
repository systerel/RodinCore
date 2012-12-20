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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.junit.Test;

/**
 * Unit tests for the Proof Manager.
 * 
 * @author Laurent Voisin
 */
public class ProofManagerTests extends AbstractProofTests {

	/**
	 * Ensures that one can get an instance of the Proof Manager.
	 */
	@Test
	public void testPMExists() throws Exception {
		assertNotNull(pm);
	}

	/**
	 * Ensures that the Proof Manager is unique.
	 */
	@Test
	public void testPMUnique() throws Exception {
		final IProofManager otherPM = EventBPlugin.getProofManager();
		assertEquals(pm, otherPM);
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * context.
	 */
	@Test
	public void testContextProofComponent() throws Exception {
		final IContextRoot root = eventBProject.getContextRoot("c");
		final IProofComponent pc = pm.getProofComponent(root);
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(root.getSCContextRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPORoot()));
		assertEquals(pc, pm.getProofComponent(root.getPRRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPSRoot()));
	}

	/**
	 * Ensures that one can get a Proof Component from any file related to a
	 * machine.
	 */
	@Test
	public void testMachineProofComponent() throws Exception {
		final IMachineRoot root = eventBProject.getMachineRoot("m");
		final IProofComponent pc = pm.getProofComponent(root);
		assertNotNull(pc);
		assertEquals(pc, pm.getProofComponent(root.getSCMachineRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPORoot()));
		assertEquals(pc, pm.getProofComponent(root.getPRRoot()));
		assertEquals(pc, pm.getProofComponent(root.getPSRoot()));
	}

}
