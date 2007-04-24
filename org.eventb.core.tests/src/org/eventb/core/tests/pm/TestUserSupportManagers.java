/**
 * 
 */
package org.eventb.core.tests.pm;

import java.util.Collection;

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
public class TestUserSupportManagers extends TestPM {

	public void testUserSupportManager() throws RodinDBException, CoreException {
		IPOFile poFile1 = createPOFile("x");
		IPSFile psFile1 = poFile1.getPSFile();

		IPOFile poFile2 = createPOFile("y");
		IPSFile psFile2 = poFile2.getPSFile();

		AutoProver.enable();
		runBuilder();

		// Initial number of opened user supports
		final int nbUS = manager.getUserSupports().size();
		
		IUserSupport userSupport1 = manager.newUserSupport();

		assertNotNull("First user support is not null ", userSupport1);
		assertNull("There is no input yet for the first user support ",
				userSupport1.getInput());

		Collection<IUserSupport> userSupports = manager.getUserSupports();
		assertEquals("There is at least one user support ", nbUS + 1, userSupports.size());
		assertTrue("The first user support is stored ", userSupports
				.contains(userSupport1));

		userSupport1.setInput(psFile1, new NullProgressMonitor());

		assertEquals(
				"The input for first user support has been set correctly ",
				psFile1, userSupport1.getInput());
		assertInformation("Select a new PO ", "Select a new proof node\n"
				+ "Proof Tree is reloaded\n"
				+ "New current obligation",
				userSupport1.getInformation());

		IUserSupport userSupport2 = manager.newUserSupport();

		assertNotNull("Second user support is not null ", userSupport2);
		assertNull("There is no input yet for the second user support ",
				userSupport2.getInput());

		userSupports = manager.getUserSupports();
		assertEquals("There are at least two user support ",
				nbUS + 2, userSupports.size());
		assertTrue("The first user support is stored ", userSupports
				.contains(userSupport1));
		assertTrue("The second user support is stored ", userSupports
				.contains(userSupport2));

		userSupport2.setInput(psFile2, new NullProgressMonitor());

		assertEquals(
				"The input for second user support has been set correctly ",
				psFile2, userSupport2.getInput());
		assertInformation("Select a new PO ", "Select a new proof node\n"
				+ "Proof Tree is reloaded\n" + "New current obligation",
				userSupport2.getInformation());

		userSupport1.dispose();
		userSupports = manager.getUserSupports();
		assertEquals("There is only one user support left ", 
				nbUS + 1, userSupports.size());
		assertTrue("The second user support still exists ", userSupports
				.contains(userSupport2));

		userSupport2.dispose();
		userSupports = manager.getUserSupports();
		assertEquals("There are no user supports left ",
				nbUS, userSupports.size());

	}

}
