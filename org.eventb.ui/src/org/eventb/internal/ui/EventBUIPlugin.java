/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.prover.ProverUI;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         The main plugin class for Event-B UI.
 */
public class EventBUIPlugin extends AbstractUIPlugin {

	/**
	 * The identifier of the Event-B UI plug-in (value
	 * <code>"org.eventb.ui"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.ui";

	private static final String GLOBAL_TRACE = PLUGIN_ID + "/debug";

	private static final String EVENTBEDITOR_TRACE = PLUGIN_ID + "/debug/eventbeditor";

	private static final String PROJECTEXPLORER_TRACE = PLUGIN_ID + "/debug/projectexplorer";

	private static final String OBLIGATIONEXPLORER_TRACE = PLUGIN_ID + "/debug/obligationexplorer";
	
	private static final String PROVERUI_TRACE = PLUGIN_ID + "/debug/proverui";

	/**
	 * Default values for creating RODIN Elements
	 */
	public static final String PRD_DEFAULT = FormulaFactory.getDefault()
			.makeLiteralPredicate(Formula.BTRUE, null).toString();

	public static final String INV_DEFAULT = PRD_DEFAULT;

	public static final String AXM_DEFAULT = PRD_DEFAULT;

	public static final String THM_DEFAULT = PRD_DEFAULT;

	public static final String GRD_DEFAULT = PRD_DEFAULT;

	public static final String SUB_DEFAULT = "";

	// The shared instance.
	private static EventBUIPlugin plugin;

	// The instance to connect with the data base
	private static IRodinDB database;

	/**
	 * The constructor, also store the database instance of the current
	 * Workspace.
	 */
	public EventBUIPlugin() {
		plugin = this;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		database = RodinCore.create(root);
	}

	/**
	 * Getting the RODIN database for the current Workspace.
	 * 
	 * @return the RODIN database
	 */
	public static IRodinDB getRodinDatabase() {
		return database;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			String option = Platform.getDebugOption(GLOBAL_TRACE);
			if (option != null)
				UIUtils.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
	
			option = Platform.getDebugOption(EVENTBEDITOR_TRACE);
			if (option != null)
				EventBEditor.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
			
			option = Platform.getDebugOption(OBLIGATIONEXPLORER_TRACE);
			if (option != null)
				ObligationExplorer.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
	
			option = Platform.getDebugOption(PROJECTEXPLORER_TRACE);
			if (option != null)
				ProjectExplorer.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
	
			option = Platform.getDebugOption(PROVERUI_TRACE);
			if (option != null)
				ProverUI.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Getting the shared instance of the plugin.
	 * <p>
	 * 
	 * @returns the shared instance of the plugin.
	 */
	public static EventBUIPlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		// Initialise from EventBImage
		EventBImage.initializeImageRegistry(reg);
		super.initializeImageRegistry(reg);
	}

	/**
	 * Get the active workbench page.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * Getting the workbench shell
	 * <p>
	 * 
	 * @return the shell associated with the active workbench window or null if
	 *         there is no active workbench window
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
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

}
