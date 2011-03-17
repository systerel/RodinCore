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
package org.rodinp.core.emf.lightcore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for the RODIN-EMF core plug-in
 */
public class LightCorePlugin extends Plugin implements BundleActivator {

	public static final String PLUGIN_ID = "org.rodinp.emf.core";

	private static final String MAIN_TRACE = PLUGIN_ID + "/debug";

	public static boolean DEBUG;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (isDebugging())
			configureDebugOptions();
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		DEBUG = parseOption(MAIN_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

}
