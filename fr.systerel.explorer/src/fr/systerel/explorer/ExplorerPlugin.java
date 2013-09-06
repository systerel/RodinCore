/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;

/**
 * The activator class controls the plug-in life cycle
 * @since 1.0
 */
public class ExplorerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.systerel.explorer";

	public static final String NAVIGATOR_ID = PLUGIN_ID + ".navigator.view";
	
	// Trace Options
	private static final String DEBUG_NAVIGATOR = PLUGIN_ID +"/debug/navigator"; //$NON-NLS-1$

	// The shared instance
	private static ExplorerPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (isDebugging())
			configureDebugOptions();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ExplorerPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		final String option = Platform.getDebugOption(DEBUG_NAVIGATOR);
		ExplorerUtils.DEBUG = "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}
	
}
