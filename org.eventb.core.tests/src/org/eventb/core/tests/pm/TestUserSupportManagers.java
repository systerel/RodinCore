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
 *     Systerel - test about internal state leakage
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPORoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for class {@link IUserSupportManager}
 * 
 * @author htson
 */
public class TestUserSupportManagers extends TestPM {

	private IRodinFile psFile1;
	private IRodinFile psFile2;

	@Before
	public void createPSFiles() throws Exception {
		final IPORoot poRoot1 = createPOFile("x");
		psFile1 = poRoot1.getPSRoot().getRodinFile();

		final IPORoot poRoot2 = createPOFile("y");
		psFile2 = poRoot2.getPSRoot().getRodinFile();

		runBuilder();
	}

	@Test
	public void testUserSupportManager() throws RodinDBException, CoreException {
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

		userSupport1.setInput(psFile1);

		assertEquals(
				"The input for first user support has been set correctly ",
				psFile1, userSupport1.getInput());

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

		userSupport2.setInput(psFile2);

		assertEquals(
				"The input for second user support has been set correctly ",
				psFile2, userSupport2.getInput());

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

	/**
	 * Ensures that the collection returned by
	 * {@link IUserSupportManager#getUserSupports()} can be modified by clients
	 * with no harm.
	 */
	@Test
	public void getUserSupportsIsRobust() throws Exception {
		manager.newUserSupport();
		final Collection<IUserSupport> initial = manager.getUserSupports();
		final IUserSupport[] backup = toArray(initial);

		assertFalse(initial.isEmpty());
		initial.clear();

		final Collection<IUserSupport> actual = manager.getUserSupports();
		assertArrayEquals(backup, toArray(actual));
	}

	/**
	 * Ensures that the collection returned by
	 * {@link IUserSupportManager#getUserSupports()} is stable.
	 */
	@Test
	public void getUserSupportsDoesNotLeak() throws Exception {
		final Collection<IUserSupport> initial = manager.getUserSupports();
		final IUserSupport[] backup = toArray(initial);

		// The collection returned the first time does not change
		manager.newUserSupport();
		assertArrayEquals(backup, toArray(initial));

		// The collections returned before and after creation differ
		final Collection<IUserSupport> actual = manager.getUserSupports();
		assertFalse(initial.equals(actual));
	}

	protected IUserSupport[] toArray(Collection<IUserSupport> coll) {
		return coll.toArray(new IUserSupport[coll.size()]);
	}

}
