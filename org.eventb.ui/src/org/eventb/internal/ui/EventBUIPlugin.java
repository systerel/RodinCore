/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 * <p>
 * The main plugin class for Event-B UI to be used in the desktop.
 */
public class EventBUIPlugin
	extends AbstractUIPlugin
{
	// TODO Should be merged with other main plugin classes?

	/**
	 * The plug-in identifier of the Event-B UI (value
	 * <code>"org.eventb.internal.ui"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.ui";

	/**
	 * Default values for creating RODIN Elements 
	 */
	public static final String PRD_DEFAULT = "⊤";
	public static final String INV_DEFAULT = PRD_DEFAULT;
	public static final String AXM_DEFAULT = PRD_DEFAULT;
	public static final String THM_DEFAULT = PRD_DEFAULT;
	public static final String GRD_DEFAULT = PRD_DEFAULT;
	public static final String SUB_DEFAULT = "skip";
	
	// The shared instance.
	private static EventBUIPlugin plugin;

	// The instance to connect with the data base
	private static IRodinDB database;
	
	/**
	 * The constructor, also store the database instance of
	 * the current Workspace.
	 */
	public EventBUIPlugin() {
		plugin = this;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		database = RodinCore.create(root);
	}
	
	/**
	 * Getting the RODIN database for the current Workspace.
	 * @return the RODIN database
	 */
	public static IRodinDB getRodinDatabase() {return database;}
	

	/**
	 * This method is called upon plug-in activation.
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	
	/**
	 * This method is called when the plug-in is stopped.
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	
	/**
	 * @returns the shared instance of the plugin.
	 */
	public static EventBUIPlugin getDefault() {
		return plugin;
	}
	
	
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		// Initialise from EventBImage
		EventBImage.initializeImageRegistry(reg);
		super.initializeImageRegistry(reg);
	}

	/**
	 * Get the active workbench page.
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}
	

	/*
	 * Getting the current active page from the active workbench window.
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}	
	
	/**
	 * Getting the workbench shell
	 * <p>
	 * @return the shell associated with the active workbench window
	 *         or null if there is no active workbench window
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}
	

	/**
	 * Return the active workbench window
	 * <p>
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
	
}
