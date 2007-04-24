/**
 * 
 */
package org.eventb.core.tests.pm;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportManagerDeltas extends TestPMDelta {

	public void testUserSupportManagerDeltas() throws RodinDBException,
			CoreException {
		IPOFile poFile1 = createPOFile("x");
		IPSFile psFile1 = poFile1.getPSFile();

		IPOFile poFile2 = createPOFile("y");
		IPSFile psFile2 = poFile2.getPSFile();

		AutoProver.enable();
		runBuilder();

		startDeltas();
		IUserSupport userSupport1 = manager.newUserSupport();
		assertDeltas("Creating first user support", "[+] null []");

		clearDeltas();
		userSupport1.setInput(psFile1, new NullProgressMonitor());

		assertDeltas("Set input for the first user support",
				"[*] x.bps [CURRENT|STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n"
						+ "New current obligation (priority 2)\n" 
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
		userSupport2.setInput(psFile2, new NullProgressMonitor());

		assertDeltas("Set input for the second user support",
				"[*] y.bps [CURRENT|STATE|INFORMATION]\n"
						+ "Select a new proof node (priority 1)\n"
						+ "Proof Tree is reloaded (priority 2)\n"
						+ "New current obligation (priority 2)\n" 
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
