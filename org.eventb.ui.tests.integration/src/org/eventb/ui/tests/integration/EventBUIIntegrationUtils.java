/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests.integration;

import static org.eventb.core.IConfigurationElement.DEFAULT_CONFIGURATION;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Utility methods dedicated to EventB projects and elements.
 * 
 * @author Thomas Muller
 */
public class EventBUIIntegrationUtils {

	/**
	 * Utility method to get a handle to a context root with the given name. The
	 * context is located in the given project ({@link #project}).
	 * 
	 * @param rodinProject
	 *            the project to create a context in
	 * @param bareName
	 *            the bare name (without any file extension) of the context
	 * @return a handle to a context root with the given name
	 */
	public static IContextRoot createContext(IRodinProject rodinProject,
			String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		final IRodinFile file = rodinProject.getRodinFile(fileName);
		file.create(true, null);
		final IContextRoot result = (IContextRoot) file.getRoot();
		result.setConfiguration(DEFAULT_CONFIGURATION, null);
		return result;
	}

	/**
	 * Utility method to get a handle to a machine root with the given name. The
	 * machine is located in the given project ({@link #project}).
	 * 
	 * @param rodinProject
	 *            the project to create a context in
	 * @param bareName
	 *            the bare name (without any file extension) of the machine
	 * @return a handle to a machine root with the given name
	 */
	public static IMachineRoot createMachine(IRodinProject rodinProject,
			String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		final IRodinFile file = rodinProject.getRodinFile(fileName);
		file.create(true, null);
		final IMachineRoot result = (IMachineRoot) file.getRoot();
		result.setConfiguration(DEFAULT_CONFIGURATION, null);
		return result;
	}

	/**
	 * Utility method to create a rodin project with the given name.
	 * 
	 * @param name
	 *            the name of the project to create
	 */
	public static IRodinProject createRodinProject(String name)
			throws CoreException {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(name);
		project.create(null);
		project.open(null);
		final IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		final IRodinProject rodinPrj = RodinCore.valueOf(project);
		assertNotNull(rodinPrj);
		rodinPrj.save(null, true);
		return rodinPrj;
	}

	/**
	 * Utility method to open the given root element with the editor
	 * corresponding to the given ID.
	 * 
	 * @param display
	 *            the current display to use
	 * @param page
	 *            the current workbench page
	 * @param editorID
	 *            the id of the editor to open
	 * @param root
	 *            the given root to open with the editor
	 */
	public static void openEditor(Display display, final IWorkbenchPage page,
			final String editorID, final IEventBRoot root) {

		display.syncExec(new Runnable() {

			@Override
			public void run() {
				try {
					page.openEditor(new FileEditorInput(root.getResource()),
							editorID);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
