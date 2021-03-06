/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored enableAutoProver
 *     Systerel - added post-tactic manipulation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.junit.Assert.assertEquals;
import static org.eventb.core.EventBPlugin.getUserSupportManager;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Abstract class for proof manager tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BasicTest extends BuilderTest {
	
	@Override
	protected void runBuilder() throws CoreException {
		super.runBuilder();
		checkPSFiles();
	}

	private void checkPSFiles() throws RodinDBException {
		final IRodinFile[] files = rodinProject.getRodinFiles();
		for (final IRodinFile file: files) {
			final IInternalElement root = file.getRoot();
			if (root instanceof IPSRoot) {
				checkPSFile((IPSRoot) root);
			}
		}
	}

	private void checkPSFile(IPSRoot root) throws RodinDBException {
		for (final IPSStatus psStatus: root.getStatuses()) {
			final IPOSequent poSequent = psStatus.getPOSequent();
			assertEquals("PS file not in sync with PO file",
					poSequent.getPOStamp(), psStatus.getPOStamp());
		}
	}
	
	protected static void enableTestAutoProver() {
		enableAutoProver();
	}

	protected static IUserSupport newUserSupport(IPSRoot psRoot) {
		final IUserSupportManager usm = getUserSupportManager();
		final IUserSupport us = usm.newUserSupport();
		us.setInput(psRoot);
		return us;
	}

}
