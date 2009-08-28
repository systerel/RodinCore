/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - handling of LaTeX symbols
 *     Systerel - delegated to org.rodinp.keyboard
 ******************************************************************************/

package org.eventb.eventBKeyboard;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.internal.eventBKeyboard.KeyboardUtils;
import org.osgi.framework.BundleContext;

/**
 * @author htson
 *         <p>
 *         The main plugin class for the Event-B Keyboard.
 */
public class EventBKeyboardPlugin extends AbstractUIPlugin {

	/**
	 * The identifier of the Event-B Keyboard View (value
	 * <code>"org.eventb.eventBKeyboard.views.EventBKeyboardView"</code>).
	 */
	public static final String EventBKeyboardView_ID = "org.eventb.eventBKeyboard.views.EventBKeyboardView";

	public static final String PLUGIN_ID = "org.eventb.eventBKeyboard";
	
	private static final String MATH_TRACE = PLUGIN_ID + "/debug/math";

	private static final String TEXT_TRACE = PLUGIN_ID + "/debug/text";
	
	// The shared instance.
	private static EventBKeyboardPlugin plugin;

	/**
	 * The constructor.
	 */
	public EventBKeyboardPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		if (isDebugging()) {
			String option = Platform.getDebugOption(TEXT_TRACE);
			if (option != null)
				KeyboardUtils.TEXT_DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$

			option = Platform.getDebugOption(MATH_TRACE);
			if (option != null)
				KeyboardUtils.MATH_DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBKeyboardPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.eventb.eventBKeyboard", path);
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

}
