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

package org.eventb.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
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
	 * <code>"org.eventb.ui"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.ui";

	/**
	 * Image IDs for RODIN Elements
	 */
	public static final String IMG_PROJECT = "Project";
	public static final String IMG_MACHINE = "IMachine";
	public static final String IMG_CONTEXT = "IContext";
	public static final String IMG_VARIABLE = "Variable";
	public static final String IMG_INVARIANT = "Invariant";
	public static final String IMG_THEOREM = "Theorem";
	public static final String IMG_EVENT = "Event";
	public static final String IMG_LOCAL_VARIABLE = "Local variable";
	public static final String IMG_GUARD = "Guard";
	public static final String IMG_ACTION = "Action";
	public static final String IMG_CARRIER_SET = "CarrierSet";
	public static final String IMG_CONSTANT = "Constant";
	public static final String IMG_AXIOM = "Axiom";
	public static final String IMG_VARIABLES = "Variables";
	public static final String IMG_INVARIANTS = "Invariants";
	public static final String IMG_THEOREMS = "Theorems";
	public static final String IMG_INITIALISATION = "Initialisation";
	public static final String IMG_EVENTS = "Events";
	public static final String IMG_LOCAL_VARIABLES = "Local variables";
	public static final String IMG_GUARDS = "Guards";
	public static final String IMG_ACTIONS = "Actions";
	public static final String IMG_CARRIER_SETS = "CarrierSets";
	public static final String IMG_CONSTANTS = "Constants";
	public static final String IMG_AXIOMS = "Axioms";

	/**
	 * Image IDs for buttons, menu, etc.
	 */
	public static final String IMG_DELETE = "Delete";
	public static final String IMG_NEW_PROJECT = "New Project";
	public static final String IMG_NEW_CONSTRUCT = "New Construct";
	
	/**
	 * Default values for creating RODIN Elements 
	 */
	public static final String INV_DEFAULT = "⊤";
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

	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path relative path of the image
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eventb.ui", path);
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
	 * Initialialise the image registry.
	 * Additional image should be added here.
	 */
	protected void initializeImageRegistry(ImageRegistry registry) {
		registerImage(registry, IMG_PROJECT, "project.gif");
		registerImage(registry, IMG_MACHINE, "machine.gif");
		registerImage(registry, IMG_CONTEXT, "context.gif");
		registerImage(registry, IMG_VARIABLE, "variable.gif");
		registerImage(registry, IMG_INVARIANT, "invariant.gif");
		registerImage(registry, IMG_THEOREM, "theorem.gif");
		registerImage(registry, IMG_EVENT, "event.gif");
		registerImage(registry, IMG_LOCAL_VARIABLE, "localvariable.gif");
		registerImage(registry, IMG_GUARD, "guard.gif");
		registerImage(registry, IMG_ACTION, "action.gif");
		registerImage(registry, IMG_CARRIER_SET, "carrierset.gif");
		registerImage(registry, IMG_CONSTANT, "constant.gif");
		registerImage(registry, IMG_AXIOM, "axiom.gif");
		registerImage(registry, IMG_VARIABLES, "variables.gif");
		registerImage(registry, IMG_INVARIANTS, "invariants.gif");
		registerImage(registry, IMG_THEOREMS, "theorems.gif");
		registerImage(registry, IMG_INITIALISATION, "initialisation.gif");
		registerImage(registry, IMG_EVENTS, "events.gif");
		registerImage(registry, IMG_LOCAL_VARIABLES, "localvariables.gif");
		registerImage(registry, IMG_GUARDS, "guards.gif");
		registerImage(registry, IMG_ACTIONS, "actions.gif");
		registerImage(registry, IMG_CARRIER_SETS, "carriersets.gif");
		registerImage(registry, IMG_CONSTANTS, "constants.gif");
		registerImage(registry, IMG_AXIOMS, "axioms.gif");
		registerImage(registry, IMG_DELETE, "delete.gif");
		registerImage(registry, IMG_NEW_PROJECT, "new-project.gif");
		registerImage(registry, IMG_NEW_CONSTRUCT, "new-construct.gif");
	}

	
	/*
	 * Register the image with the image registry. This method is used 
	 * in the initialisation of the image registry. 
	 * @param registry the registry
	 * @param key the key to retrieve the image
	 * @param fileName the name of the image file in folder "icons"
	 */
	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
			ImageDescriptor desc = EventBUIPlugin.getImageDescriptor("icons/" + fileName);
			registry.put(key, desc);
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
