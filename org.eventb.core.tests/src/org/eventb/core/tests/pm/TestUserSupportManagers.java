/**
 * 
 */
package org.eventb.core.tests.pm;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.core.pm.IUSManagerListener;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportManagers extends BasicTest {

	IUserSupportManager manager;

	IUserSupport actualUserSupport;

	int actualStatus;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = EventBPlugin.getPlugin().getUserSupportManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateAndDisposeUserSupports() throws Exception {
		IMachineFile machine1 = createMachine("m0");
		addVariables(machine1, "v0");
		addInvariants(machine1, makeSList("inv0"),
				makeSList("v0 ∈ ℕ"));
		addEvent(machine1, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"),
				makeSList("v0 ≔ 0"));
		machine1.save(null, true);

		runBuilder();
		IPSFile psFile1 = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport1 = manager.newUserSupport();
		manager.setInput(userSupport1, psFile1, new NullProgressMonitor());

		Collection<IUserSupport> userSupports = manager.getUserSupports();

		assertEquals("There should be only one user Support ", 1, userSupports
				.size());

		IMachineFile machine2 = createMachine("m0");
		addVariables(machine2, "v0");
		addInvariants(machine2, makeSList("inv0"),
				makeSList("v0 ∈ ℕ"));
		addEvent(machine2, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"),
				makeSList("v0 ≔ 0"));
		machine2.save(null, true);

		runBuilder();
		IPSFile psFile2 = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport2 = manager.newUserSupport();
		manager.setInput(userSupport2, psFile2, new NullProgressMonitor());

		userSupports = manager.getUserSupports();

		assertEquals("There should be two user Supports ", 2, userSupports
				.size());

		manager.disposeUserSupport(userSupport2);

		userSupports = manager.getUserSupports();

		assertEquals("There now should be only one user Support ", 1,
				userSupports.size());

		manager.disposeUserSupport(userSupport1);

		userSupports = manager.getUserSupports();

		assertEquals("There should be only no user Supports ", 0, userSupports
				.size());
	}

	public void testUserSupportManagerListener() throws RodinDBException,
			CoreException {
		UserSupportManagerListener listener = new UserSupportManagerListener();
		manager.addUSManagerListener(listener);
		
		IMachineFile machine = createMachine("m0");
		addVariables(machine, "v0");
		addInvariants(machine, makeSList("inv0"),
				makeSList("v0 ∈ ℕ"));
		addEvent(machine, "INITIALISATION", makeSList(), makeSList(),
				makeSList(), makeSList("act1"),
				makeSList("v0 ≔ 0"));
		machine.save(null, true);

		runBuilder();
		IPSFile psFile = (IPSFile) rodinProject.getRodinFile(EventBPlugin
				.getPSFileName("m0"));

		IUserSupport userSupport = manager.newUserSupport();

		assertEquals("User Support is the same ", userSupport, actualUserSupport);
		assertEquals("User Support is added ", IUSManagerListener.ADDED, actualStatus);
		
		manager.setInput(userSupport, psFile, new NullProgressMonitor());
		
		assertEquals("User Support is the same ", userSupport, actualUserSupport);
		assertEquals("User Support is changed ", IUSManagerListener.CHANGED, actualStatus);
		
		manager.disposeUserSupport(userSupport);

		assertEquals("User Support is the same ", userSupport, actualUserSupport);
		assertEquals("User Support is removed ", IUSManagerListener.REMOVED, actualStatus);
	}

	private class UserSupportManagerListener implements IUSManagerListener {

		public void USManagerChanged(IUserSupport userSupport, int status) {
			actualUserSupport = userSupport;
			actualStatus = status;
		}

	}
}
