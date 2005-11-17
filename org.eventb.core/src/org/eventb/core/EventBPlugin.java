/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The Event-B core plugin class.
 */
public class EventBPlugin extends Plugin {

	/**
	 * The plug-in identifier of the Event-B core support (value
	 * <code>"org.eventb.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.core"; //$NON-NLS-1$

	//The shared instance.
	private static EventBPlugin plugin;
	
	/**
	 * Creates the Event-B core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public EventBPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getPlugin() {
		return plugin;
	}

}
