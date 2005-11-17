/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eventb.internal.core.sc.ContextSC;
import org.osgi.framework.BundleContext;

/**
 * @author halstefa
 *
 */
public class SCPlugin extends Plugin {
	
	/**
	 * The plug-in identifier of the Event-B core static checker (value
	 * <code>"org.eventb.core.sc"</code>).
	 * This plug-in contributes static checkers for machines and contexts. 
	 */
	public static final String PLUGIN_ID = EventBPlugin.PLUGIN_ID + ".sc"; //$NON-NLS-1$

	public static final String CONTEXT_PRODUCER_ID = PLUGIN_ID + ".context"; //$NON-NLS-1$
	
	public static final String MACHINE_PRODUCER_ID = PLUGIN_ID + ".machine"; //$NON-NLS-1$

	//The shared instance.
	private static SCPlugin plugin;
	
	/**
	 * Creates the Event-B core static checker plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public SCPlugin() {
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
	public static SCPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SCPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * This method is for testing purposes and can change without notice.
	 * @param context
	 * @param scContext
	 * @throws CoreException
	 */
	public static void runSC(IContext context, ISCContext scContext) throws CoreException {
		ContextSC staticChecker = new ContextSC(context, scContext, null, null);
		staticChecker.run();

	}

}
