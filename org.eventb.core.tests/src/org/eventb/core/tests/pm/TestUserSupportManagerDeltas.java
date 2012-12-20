/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPORoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportManagerDeltas extends TestPMDelta {

	@Test
	public void testUserSupportManagerDeltas() throws RodinDBException,
			CoreException {
		IPORoot poRoot1 = createPOFileWithContents("x");
		IRodinFile psFile1 = poRoot1.getPSRoot().getRodinFile();

		IPORoot poRoot2 = createPOFileWithContents("y");
		IRodinFile psFile2 = poRoot2.getPSRoot().getRodinFile();

		enableTestAutoProver();
		runBuilder();

		startDeltas();
		IUserSupport userSupport1 = manager.newUserSupport();
		assertDeltas("Creating first user support", "[+] null []");

		clearDeltas();
		userSupport1.setInput(psFile1);
		assertDeltas("No deltas should have been produced", "");

		clearDeltas();
		userSupport1.loadProofStates();
		assertDeltas("Set input for the first user support",
				"[*] x.bps [STATE]\n"
						+ "  [+] PO1[org.eventb.core.psStatus] []\n"
						+ "  [+] PO2[org.eventb.core.psStatus] []\n"
						+ "  [+] PO3[org.eventb.core.psStatus] []\n"
						+ "  [+] PO4[org.eventb.core.psStatus] []\n"
						+ "  [+] PO5[org.eventb.core.psStatus] []\n"
						+ "  [+] PO6[org.eventb.core.psStatus] []\n"
						+ "  [+] PO7[org.eventb.core.psStatus] []");

		clearDeltas();
		IUserSupport userSupport2 = manager.newUserSupport();

		assertDeltas("Creating the second user support", "[+] null []");

		clearDeltas();
		userSupport2.setInput(psFile2);
		assertDeltas("No deltas should have been produced", "");

		clearDeltas();
		userSupport2.loadProofStates();
		assertDeltas("Set input for the second user support",
				"[*] y.bps [STATE]\n"
						+ "  [+] PO1[org.eventb.core.psStatus] []\n"
						+ "  [+] PO2[org.eventb.core.psStatus] []\n"
						+ "  [+] PO3[org.eventb.core.psStatus] []\n"
						+ "  [+] PO4[org.eventb.core.psStatus] []\n"
						+ "  [+] PO5[org.eventb.core.psStatus] []\n"
						+ "  [+] PO6[org.eventb.core.psStatus] []\n"
						+ "  [+] PO7[org.eventb.core.psStatus] []");

		clearDeltas();
		userSupport1.dispose();

		assertDeltas("Removing the second user support", "[-] x.bps []");

		clearDeltas();
		userSupport2.dispose();
		assertDeltas("Removing the second user support", "[-] y.bps []");

		stopDeltas();
	}

}
