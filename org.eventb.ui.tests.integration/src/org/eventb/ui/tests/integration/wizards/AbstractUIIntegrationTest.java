/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests.integration.wizards;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.tests.integration.EventBUIIntegrationUtils;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IRodinProject;

/**
 * Abstract class for UIIntegrationTests
 * 
 * @author Thomas Muller
 */
public abstract class AbstractUIIntegrationTest {

	protected static final String eventBContextEditorID = "org.eventb.ui.editors.context";
	protected static final String eventBMachineEditorID = "org.eventb.ui.editors.machine";

	protected static final String ctxname = "ctx0";
	protected static final String mchname = "mch0";

	protected IRodinProject project;
	protected IContextRoot ctx;
	protected IMachineRoot mch;

	protected final SWTWorkbenchBot bot = new SWTWorkbenchBot();
	protected IWorkbench workbench;
	protected IWorkbenchWindow ww;

	@Before
	public void setUp() throws CoreException {
		project = EventBUIIntegrationUtils.createRodinProject("TEST");
		ctx = EventBUIIntegrationUtils.createContext(project, ctxname);
		ctx.getRodinFile().save(null, true);
		mch = EventBUIIntegrationUtils.createMachine(project, mchname);
		mch.getRodinFile().save(null, true);

		try {
			// We close the welcome view
			final SWTBotView welcome = bot
					.viewById("org.eclipse.ui.internal.introview");
			if (welcome != null && welcome.isActive())
				welcome.close();
		} catch (WidgetNotFoundException e) {
			// The welcome window has previously been closed. There is no
			// problem here.
		}

		workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow[] wws = workbench.getWorkbenchWindows();
		assertTrue(wws.length == 1); // There is only one inactive workbench
		ww = wws[0];
	}

	@After
	public void tearDown() throws CoreException {
		project.getProject().delete(true, true, null);
	}

}
